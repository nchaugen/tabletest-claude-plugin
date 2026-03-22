#!/usr/bin/env node

const { execSync, spawn } = require("child_process");
const fs = require("fs");
const path = require("path");

const EVALS_PATH = "skills-workspace/evals/evals.json";
const WORKSPACE_PATH = "skills-workspace";

function parseArgs(argv) {
  const args = {
    iteration: null,
    evals: null,       // null = all, or array of ids
    baseline: false,
    model: "sonnet",
    parallel: 4,
    gradeOnly: false,
  };

  for (let i = 2; i < argv.length; i++) {
    switch (argv[i]) {
      case "--iteration":
        args.iteration = parseInt(argv[++i], 10);
        break;
      case "--evals":
        args.evals = argv[++i].split(",").map(Number);
        break;
      case "--baseline":
        args.baseline = true;
        break;
      case "--model":
        args.model = argv[++i];
        break;
      case "--parallel":
        args.parallel = parseInt(argv[++i], 10);
        break;
      case "--grade-only":
        args.gradeOnly = true;
        break;
      default:
        console.error(`Unknown argument: ${argv[i]}`);
        process.exit(1);
    }
  }

  if (!args.iteration) {
    console.error("Usage: node scripts/run-evals.js --iteration N [options]");
    console.error("Options:");
    console.error("  --evals 1,2,3       Run specific evals only");
    console.error("  --baseline          Also run without skill");
    console.error("  --model MODEL       Model to use (default: sonnet)");
    console.error("  --parallel N        Max parallel evals (default: 4)");
    console.error("  --grade-only        Re-grade existing outputs");
    process.exit(1);
  }

  return args;
}

async function main() {
  const args = parseArgs(process.argv);
  const repoRoot = execSync("git rev-parse --show-toplevel", {
    encoding: "utf-8",
  }).trim();

  const evalsData = JSON.parse(
    fs.readFileSync(path.join(repoRoot, EVALS_PATH), "utf-8")
  );

  let evals = evalsData.evals;
  if (args.evals) {
    evals = evals.filter((e) => args.evals.includes(e.id));
  }

  console.log(
    `\nEval run: iteration ${args.iteration}, ${evals.length} evals, model ${args.model}`
  );

  const worktreePath = args.gradeOnly ? null : setupWorktree(repoRoot);
  const iterationDir = path.join(
    repoRoot,
    WORKSPACE_PATH,
    `iteration-${args.iteration}`
  );
  fs.mkdirSync(iterationDir, { recursive: true });

  try {
    if (!args.gradeOnly) {
      await generateResponses(evals, worktreePath, iterationDir, args);
    }
    await gradeResponses(evals, iterationDir, args);
    const benchmark = aggregateResults(evals, iterationDir, args);
    const previousBenchmark = loadPreviousBenchmark(repoRoot, args.iteration);
    generateReport(benchmark, previousBenchmark, iterationDir, args);
    console.log("\nDone. Results in:", iterationDir);
  } finally {
    if (worktreePath) {
      cleanupWorktree(worktreePath);
    }
  }
}

function setupWorktree(repoRoot) {
  const worktreePath = path.join(
    require("os").tmpdir(),
    `eval-run-${Date.now()}`
  );
  console.log("Creating clean worktree at:", worktreePath);

  execSync(`git worktree add --detach "${worktreePath}" HEAD`, {
    cwd: repoRoot,
    stdio: "inherit",
  });

  // Remove experiment documents to prevent contamination
  const experimentsDir = path.join(
    worktreePath,
    "docs",
    "superpowers",
    "experiments"
  );
  if (fs.existsSync(experimentsDir)) {
    fs.rmSync(experimentsDir, { recursive: true });
    console.log("Removed experiment documents for contamination isolation");
  }

  return worktreePath;
}

function cleanupWorktree(worktreePath) {
  console.log("Cleaning up worktree...");
  try {
    execSync(`git worktree remove --force "${worktreePath}"`, {
      stdio: "inherit",
    });
  } catch {
    console.warn("Warning: could not remove worktree at", worktreePath);
  }
}

function runClaude({ prompt, systemPrompt, model, cwd, pluginDir, timeoutMs = 300000 }) {
  return new Promise((resolve, reject) => {
    const args = ["--bare", "--print", "--output-format", "json", "--model", model];

    if (pluginDir) {
      args.push("--plugin-dir", pluginDir);
    }
    if (systemPrompt) {
      args.push("--system-prompt", systemPrompt);
    }
    args.push("-p", prompt);

    let stdout = "";
    let stderr = "";

    const proc = spawn("claude", args, {
      cwd,
      env: { ...process.env },
    });

    const timer = setTimeout(() => {
      proc.kill("SIGTERM");
      reject(new Error(`Timed out after ${timeoutMs}ms`));
    }, timeoutMs);

    proc.stdout.on("data", (data) => { stdout += data; });
    proc.stderr.on("data", (data) => { stderr += data; });

    proc.on("close", (code) => {
      clearTimeout(timer);
      if (code !== 0) {
        reject(new Error(`claude exited with code ${code}: ${stderr}`));
        return;
      }
      try {
        resolve(JSON.parse(stdout));
      } catch {
        reject(new Error(`Failed to parse claude output: ${stdout.slice(0, 200)}`));
      }
    });

    proc.on("error", (err) => {
      clearTimeout(timer);
      reject(err);
    });
  });
}

async function generateResponses(evals, worktreePath, iterationDir, args) {
  const configs = ["with_skill"];
  if (args.baseline) configs.push("no_skill");

  console.log(
    `\nGenerating responses (${configs.join(", ")}, parallel=${args.parallel})...`
  );

  const jobs = [];
  for (const evalDef of evals) {
    for (const config of configs) {
      jobs.push({ evalDef, config });
    }
  }

  for (let i = 0; i < jobs.length; i += args.parallel) {
    const batch = jobs.slice(i, i + args.parallel);
    await Promise.all(
      batch.map(({ evalDef, config }) =>
        generateOne(evalDef, config, worktreePath, iterationDir, args.model)
      )
    );
  }
}

async function generateOne(evalDef, config, worktreePath, iterationDir, model) {
  const evalDir = path.join(
    iterationDir,
    `eval-${evalDef.id}-${evalDef.slug}`,
    config
  );
  fs.mkdirSync(path.join(evalDir, "outputs"), { recursive: true });

  console.log(`  Running eval ${evalDef.id} (${evalDef.slug}) [${config}]...`);

  try {
    const result = await runClaude({
      prompt: evalDef.prompt,
      model,
      cwd: worktreePath,
      pluginDir: config === "with_skill" ? worktreePath : undefined,
    });

    fs.writeFileSync(
      path.join(evalDir, "outputs", "response.md"),
      result.result || "",
      "utf-8"
    );

    const timing = {
      duration_ms: result.duration_ms || 0,
      total_tokens:
        (result.usage?.input_tokens || 0) +
        (result.usage?.output_tokens || 0),
      input_tokens: result.usage?.input_tokens || 0,
      output_tokens: result.usage?.output_tokens || 0,
      cost_usd: result.total_cost_usd || 0,
    };
    fs.writeFileSync(
      path.join(evalDir, "timing.json"),
      JSON.stringify(timing, null, 2),
      "utf-8"
    );

    console.log(
      `  ✓ Eval ${evalDef.id} [${config}] — ${timing.total_tokens} tokens, ${timing.duration_ms}ms`
    );
  } catch (err) {
    console.error(`  ✗ Eval ${evalDef.id} [${config}] — ${err.message}`);
    fs.writeFileSync(
      path.join(evalDir, "outputs", "response.md"),
      `ERROR: ${err.message}`,
      "utf-8"
    );
    fs.writeFileSync(
      path.join(evalDir, "timing.json"),
      JSON.stringify({ error: err.message }, null, 2),
      "utf-8"
    );
  }
}

// Placeholder stubs — implemented in later tasks
async function gradeResponses() { throw new Error("Not implemented"); }
function aggregateResults() { throw new Error("Not implemented"); }
function loadPreviousBenchmark() { return null; }
function generateReport() {}

main().catch((err) => {
  console.error("Fatal error:", err.message);
  process.exit(1);
});
