#!/usr/bin/env node

const { execSync, spawn } = require("child_process");
const fs = require("fs");
const path = require("path");

const EVALS_PATH = "skills-workspace/evals/evals.json";
const WORKSPACE_PATH = "skills-workspace";

// Logger that writes to both console and a log file (sync flush to survive crashes)
let logFile = null;

function log(...args) {
  const msg = args.map(a => typeof a === "string" ? a : JSON.stringify(a)).join(" ");
  console.log(msg);
  if (logFile) fs.appendFileSync(logFile, msg + "\n");
}

function logError(...args) {
  const msg = args.map(a => typeof a === "string" ? a : JSON.stringify(a)).join(" ");
  console.error(msg);
  if (logFile) fs.appendFileSync(logFile, "ERROR: " + msg + "\n");
}

const GRADING_SYSTEM_PROMPT = `You are an eval grader. You will receive a model response and a list of assertions.
For each assertion, determine whether it passes or fails based on the response content.

Return ONLY a JSON object (no markdown fencing, no explanation) with this exact structure:
{
  "assertions": [
    {
      "id": "<assertion id>",
      "text": "<assertion text>",
      "passed": true or false,
      "evidence": "<direct quote or specific reference from the response supporting your judgement>"
    }
  ]
}

Rules:
- Be strict. An assertion passes only if clearly demonstrated in the response.
- The evidence field must contain a direct quote from the response, not your interpretation.
- If the assertion is about absence (e.g. "does NOT invent..."), evidence should explain what you checked and why it passes/fails.
- Return valid JSON only. No markdown code fences. No text before or after the JSON.`;

function parseEvalIds(str) {
  const ids = [];
  for (const part of str.split(",")) {
    const range = part.match(/^(\d+)-(\d+)$/);
    if (range) {
      const start = parseInt(range[1], 10);
      const end = parseInt(range[2], 10);
      for (let i = start; i <= end; i++) ids.push(i);
    } else {
      ids.push(Number(part));
    }
  }
  return ids;
}

function parseArgs(argv) {
  const args = {
    iteration: null,
    evals: null,       // null = all, or array of ids
    baseline: false,
    baselineOnly: false,
    model: "sonnet",
    gradingModel: "haiku",
    gradingSuffix: null,
    parallel: 4,
    gradeOnly: false,
  };

  for (let i = 2; i < argv.length; i++) {
    switch (argv[i]) {
      case "--iteration":
        args.iteration = parseInt(argv[++i], 10);
        break;
      case "--evals":
        args.evals = parseEvalIds(argv[++i]);
        break;
      case "--baseline":
        args.baseline = true;
        break;
      case "--baseline-only":
        args.baselineOnly = true;
        break;
      case "--model":
        args.model = argv[++i];
        break;
      case "--grading-model":
        args.gradingModel = argv[++i];
        break;
      case "--grading-suffix":
        args.gradingSuffix = argv[++i];
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
    console.error("  --evals 1,2,3       Run specific evals (supports ranges: 1-13)");
    console.error("  --baseline          Also run without skill");
    console.error("  --baseline-only     Run without skill only (no with_skill)");
    console.error("  --model MODEL       Model to use (default: sonnet)");
    console.error("  --grading-model M   Model for grading (default: haiku)");
  console.error("  --grading-suffix S  Write grading to grading-S.json instead of grading.json");
  console.error("  --parallel N        Max parallel evals (default: 4)");
    console.error("  --grade-only        Re-grade existing outputs");
    process.exit(1);
  }

  return args;
}

async function main() {
  const args = parseArgs(process.argv);

  if (!process.env.ANTHROPIC_API_KEY) {
    console.error("Error: ANTHROPIC_API_KEY environment variable is not set.");
    console.error("The eval script requires an API key to avoid consuming interactive plan usage.");
    console.error("Set it with: ANTHROPIC_API_KEY=sk-... node scripts/run-evals.js ...");
    process.exit(1);
  }

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

  const iterationDir = path.join(
    repoRoot,
    WORKSPACE_PATH,
    `iteration-${args.iteration}`
  );
  fs.mkdirSync(iterationDir, { recursive: true });

  // Set up log file (sync writes so output survives crashes)
  logFile = path.join(iterationDir, "run.log");
  fs.appendFileSync(logFile, `\n--- Run started at ${new Date().toISOString()} ---\n`);

  log(
    `\nEval run: iteration ${args.iteration}, ${evals.length} evals, model ${args.model}`
  );

  const worktreePath = (args.gradeOnly || args.baselineOnly) ? null : setupWorktree(repoRoot, "skill");
  const baselineWorktreePath = (!args.gradeOnly && (args.baseline || args.baselineOnly)) ? setupWorktree(repoRoot, "baseline") : null;

  try {
    if (!args.gradeOnly) {
      await generateResponses(evals, worktreePath, baselineWorktreePath, iterationDir, args);
    }
    await gradeResponses(evals, iterationDir, args);
    const benchmark = aggregateResults(evals, iterationDir, args);
    const previousBenchmark = loadPreviousBenchmark(repoRoot, args.iteration);
    generateReport(benchmark, previousBenchmark, iterationDir, args);
    log("\nDone. Results in:", iterationDir);
  } finally {
    if (worktreePath) {
      cleanupWorktree(worktreePath);
    }
    if (baselineWorktreePath) {
      cleanupWorktree(baselineWorktreePath);
    }
    logFile = null;
  }
}

function setupWorktree(repoRoot, mode = "skill") {
  const branch = `eval-${mode}-${Date.now()}`;
  const worktreePath = path.join(
    require("os").tmpdir(),
    branch
  );
  log(`Creating worktree (${mode}) at: ${worktreePath}`);

  execSync(
    `git worktree add -b "${branch}" "${worktreePath}" HEAD`,
    { cwd: repoRoot, stdio: "pipe" }
  );

  // Remove contaminating files — git tools are disallowed at runtime
  // so the agent cannot recover these via git history
  const removals = [];

  // docs/ contains ideal answers, eval strategy, and known weaknesses
  for (const dir of ["docs"]) {
    const p = path.join(worktreePath, dir);
    if (fs.existsSync(p)) { fs.rmSync(p, { recursive: true }); removals.push(dir + "/"); }
  }

  // Project files that leak eval strategy or expected answers
  for (const file of ["README.md", "CLAUDE.md", "CHANGELOG.md"]) {
    const p = path.join(worktreePath, file);
    if (fs.existsSync(p)) { fs.rmSync(p); removals.push(file); }
  }

  // Eval definitions (assertions are the answer key)
  const evalsFile = path.join(worktreePath, EVALS_PATH);
  if (fs.existsSync(evalsFile)) { fs.rmSync(evalsFile); removals.push("evals.json"); }

  // Prior iteration outputs (model answers to the same prompts)
  const workspaceDir = path.join(worktreePath, "skills-workspace");
  if (fs.existsSync(workspaceDir)) {
    for (const entry of fs.readdirSync(workspaceDir)) {
      if (entry.startsWith("iteration-")) {
        fs.rmSync(path.join(workspaceDir, entry), { recursive: true });
        removals.push(entry);
      }
    }
  }

  // Baseline: also remove skill files so the agent can't discover them
  if (mode === "baseline") {
    const skillsDir = path.join(worktreePath, "skills");
    if (fs.existsSync(skillsDir)) { fs.rmSync(skillsDir, { recursive: true }); removals.push("skills/"); }
    if (fs.existsSync(workspaceDir)) {
      for (const entry of fs.readdirSync(workspaceDir)) {
        if (entry.startsWith("skill-snapshot-")) {
          fs.rmSync(path.join(workspaceDir, entry), { recursive: true });
          removals.push(entry);
        }
      }
    }
  }

  log(`  Removed for isolation: ${removals.join(", ")}`);
  return worktreePath;
}

function cleanupWorktree(worktreePath) {
  log("Cleaning up worktree...");
  try {
    const repoRoot = execSync("git rev-parse --show-toplevel", { encoding: "utf-8" }).trim();
    const branch = path.basename(worktreePath);
    execSync(`git worktree remove "${worktreePath}" --force`, { cwd: repoRoot, stdio: "pipe" });
    // Clean up the temporary branch
    try { execSync(`git branch -D "${branch}"`, { cwd: repoRoot, stdio: "pipe" }); } catch {}
  } catch {
    // Fallback: just remove the directory
    try { fs.rmSync(worktreePath, { recursive: true, force: true }); } catch {}
    logError("Warning: could not cleanly remove worktree at", worktreePath);
  }
}

function runClaude({ prompt, systemPrompt, model, cwd, pluginDir, timeoutMs = 600000 }) {
  return new Promise((resolve, reject) => {
    let settled = false;
    const settle = (fn, value) => {
      if (settled) return;
      settled = true;
      fn(value);
    };

    const args = ["--print", "--output-format", "stream-json", "--verbose", "--model", model];

    args.push("--disallowedTools", "Bash(git:*)");
    args.push("--no-session-persistence", "--setting-sources", "");

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
      stdio: ['ignore', 'pipe', 'pipe'],
    });

    const timer = setTimeout(() => {
      proc.kill("SIGTERM");
      const err = new Error(`Timed out after ${timeoutMs}ms`);
      err.stdout = stdout;
      err.stderr = stderr;
      settle(reject, err);
    }, timeoutMs);

    proc.stdout.on("data", (data) => { stdout += data; });
    proc.stderr.on("data", (data) => { stderr += data; });

    proc.on("close", (code) => {
      clearTimeout(timer);
      if (code !== 0) {
        const err = new Error(`claude exited with code ${code}:\n  stderr: ${stderr.slice(0, 500)}`);
        err.stdout = stdout;
        err.stderr = stderr;
        settle(reject, err);
        return;
      }
      try {
        const lines = stdout.split("\n").filter(l => l.trim());
        const parsed = lines.map(l => JSON.parse(l));
        const resultLine = parsed.filter(o => o.type === "result").pop();
        if (!resultLine) throw new Error("No result line found in stream-json output");
        resultLine._conversationJsonl = stdout;
        settle(resolve, resultLine);
      } catch (e) {
        settle(reject, new Error(`Failed to parse claude output: ${e.message}\n${stdout.slice(0, 500)}`));
      }
    });

    proc.on("error", (err) => {
      clearTimeout(timer);
      settle(reject, err);
    });
  });
}

async function generateResponses(evals, worktreePath, baselineWorktreePath, iterationDir, args) {
  const configs = args.baselineOnly ? ["no_skill"] : ["with_skill"];
  if (args.baseline && !args.baselineOnly) configs.push("no_skill");

  log(
    `\nGenerating responses (${configs.join(", ")}, parallel=${args.parallel})...`
  );

  const jobs = [];
  for (const evalDef of evals) {
    for (const config of configs) {
      const cwd = config === "no_skill" ? baselineWorktreePath : worktreePath;
      jobs.push({ evalDef, config, cwd });
    }
  }

  const totalJobs = jobs.length;
  let completedJobs = 0;
  const startTime = Date.now();

  for (let i = 0; i < jobs.length; i += args.parallel) {
    const batch = jobs.slice(i, i + args.parallel);
    const batchNum = Math.floor(i / args.parallel) + 1;
    const totalBatches = Math.ceil(jobs.length / args.parallel);
    const elapsed = ((Date.now() - startTime) / 1000).toFixed(0);
    log(`  [batch ${batchNum}/${totalBatches}, ${completedJobs}/${totalJobs} done, ${elapsed}s elapsed]`);

    await Promise.all(
      batch.map(({ evalDef, config, cwd }) =>
        generateOne(evalDef, config, cwd, iterationDir, args.model)
      )
    );
    completedJobs += batch.length;
  }
}

async function generateOne(evalDef, config, worktreePath, iterationDir, model) {
  const evalDir = path.join(
    iterationDir,
    `eval-${evalDef.id}-${evalDef.slug}`,
    config
  );
  fs.mkdirSync(path.join(evalDir, "outputs"), { recursive: true });

  // Skip if already completed (enables resuming interrupted runs)
  const timingPath = path.join(evalDir, "timing.json");
  if (fs.existsSync(timingPath)) {
    try {
      const existing = JSON.parse(fs.readFileSync(timingPath, "utf-8"));
      if (existing.duration_ms != null && !existing.error) {
        log(`  ⏭ Eval ${evalDef.id} [${config}] — already completed, skipping`);
        return;
      }
    } catch { /* re-run if timing.json is corrupt */ }
  }

  log(`  Running eval ${evalDef.id} (${evalDef.slug}) [${config}]...`);

  try {
    const result = await runClaude({
      prompt: evalDef.prompt,
      model,
      cwd: worktreePath,
      pluginDir: config === "with_skill" ? worktreePath : undefined,
      timeoutMs: evalDef.timeout_ms,
    });

    fs.writeFileSync(
      path.join(evalDir, "outputs", "response.md"),
      result.result || "",
      "utf-8"
    );

    const usage = result.usage || {};
    const timing = {
      duration_ms: result.duration_ms || 0,
      input_tokens: usage.input_tokens || 0,
      output_tokens: usage.output_tokens || 0,
      cache_creation_input_tokens: usage.cache_creation_input_tokens || 0,
      cache_read_input_tokens: usage.cache_read_input_tokens || 0,
      total_tokens:
        (usage.input_tokens || 0) +
        (usage.output_tokens || 0) +
        (usage.cache_creation_input_tokens || 0) +
        (usage.cache_read_input_tokens || 0),
      cost_usd: result.total_cost_usd || 0,
    };
    fs.writeFileSync(
      path.join(evalDir, "timing.json"),
      JSON.stringify(timing, null, 2),
      "utf-8"
    );

    if (result._conversationJsonl) {
      fs.writeFileSync(
        path.join(evalDir, "conversation.jsonl"),
        result._conversationJsonl,
        "utf-8"
      );
    }

    log(
      `  ✓ Eval ${evalDef.id} [${config}] — ${timing.total_tokens} tokens, ${timing.duration_ms}ms`
    );
  } catch (err) {
    logError(`  ✗ Eval ${evalDef.id} [${config}] — ${err.message}`);
    // Write error details for debugging
    let errorLog = `${new Date().toISOString()}\n${err.message}\n${err.stack || ""}\n`;
    if (err.stderr) errorLog += `\n--- stderr ---\n${err.stderr}\n`;
    if (err.stdout) errorLog += `\n--- stdout (partial conversation trace) ---\n${err.stdout}\n`;
    fs.writeFileSync(path.join(evalDir, "error.log"), errorLog, "utf-8");

    // Save partial conversation trace even on failure
    if (err.stdout) {
      fs.writeFileSync(path.join(evalDir, "conversation.jsonl"), err.stdout, "utf-8");
    }
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

function buildGradingPrompt(evalDef, response) {
  const assertionsList = evalDef.assertions
    .map((a) => `- [${a.id}] ${a.text}`)
    .join("\n");

  return `## Assertions

${assertionsList}

## Expected Output Description

${evalDef.expected_output}

## Model Response

${response}`;
}

async function gradeOne(evalDef, config, iterationDir, model, gradingSuffix = null) {
  const evalDir = path.join(
    iterationDir,
    `eval-${evalDef.id}-${evalDef.slug}`,
    config
  );
  const responsePath = path.join(evalDir, "outputs", "response.md");

  if (!fs.existsSync(responsePath)) {
    log(`  Skipping eval ${evalDef.id} [${config}] — no response`);
    return;
  }

  const response = fs.readFileSync(responsePath, "utf-8");
  if (response.startsWith("ERROR:")) {
    log(`  Skipping eval ${evalDef.id} [${config}] — generation error`);
    return;
  }

  log(`  Grading eval ${evalDef.id} (${evalDef.slug}) [${config}]...`);

  const gradingPrompt = buildGradingPrompt(evalDef, response);

  const result = await runClaude({
    prompt: gradingPrompt,
    systemPrompt: GRADING_SYSTEM_PROMPT,
    model: model,
    cwd: process.cwd(),
  });

  // Parse grading JSON from the response
  const gradingText = result.result || "";
  let grading;
  try {
    grading = JSON.parse(gradingText);
  } catch {
    // Try extracting JSON from markdown code fences
    const fenceMatch = gradingText.match(/```(?:json)?\s*([\s\S]*?)```/);
    if (fenceMatch) {
      grading = JSON.parse(fenceMatch[1]);
    } else {
      // Try extracting the outermost JSON object (handles prose before/after)
      const start = gradingText.indexOf("{");
      const end = gradingText.lastIndexOf("}");
      if (start !== -1 && end > start) {
        grading = JSON.parse(gradingText.slice(start, end + 1));
      } else {
        throw new Error(
          `Failed to parse grading JSON for eval ${evalDef.id}: ${gradingText.slice(0, 200)}`
        );
      }
    }
  }

  // Add summary fields
  grading.assertions_passed = grading.assertions.filter((a) => a.passed).length;
  grading.assertions_total = grading.assertions.length;
  grading.pass_rate =
    grading.assertions_total > 0
      ? grading.assertions_passed / grading.assertions_total
      : 0;

  const gradingFile = gradingSuffix ? `grading-${gradingSuffix}.json` : "grading.json";
  fs.writeFileSync(
    path.join(evalDir, gradingFile),
    JSON.stringify(grading, null, 2),
    "utf-8"
  );

  const status = grading.pass_rate === 1 ? "✓" : "✗";
  log(
    `  ${status} Eval ${evalDef.id} [${config}] — ${grading.assertions_passed}/${grading.assertions_total}`
  );
}

async function gradeResponses(evals, iterationDir, args) {
  const configs = args.baselineOnly ? ["no_skill"] : ["with_skill"];
  if (args.baseline && !args.baselineOnly) configs.push("no_skill");

  log(`\nGrading responses (model=${args.gradingModel}, parallel=${args.parallel})...`);

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
        gradeOne(evalDef, config, iterationDir, args.gradingModel, args.gradingSuffix).catch((err) => {
          logError(`  ✗ Eval ${evalDef.id} [${config}] — GRADING FAILED: ${err.message}`);
          // Write grading error details for debugging
          const errDir = path.join(iterationDir, `eval-${evalDef.id}-${evalDef.slug}`, config);
          if (fs.existsSync(errDir)) {
            fs.writeFileSync(
              path.join(errDir, "error.log"),
              `${new Date().toISOString()}\nGRADING FAILED: ${err.message}\n${err.stack || ""}\n`,
              "utf-8"
            );
          }
        })
      )
    );
  }
}
function aggregateResults(evals, iterationDir, args) {
  const configs = args.baselineOnly ? ["no_skill"] : ["with_skill"];
  if (args.baseline && !args.baselineOnly) configs.push("no_skill");

  const benchmark = {
    skill_name: "tabletest + spec-by-example",
    iteration: args.iteration,
    model: args.model,
    timestamp: new Date().toISOString(),
    evals: [],
    summary: {},
  };

  for (const evalDef of evals) {
    const evalEntry = {
      id: `eval-${evalDef.id}-${evalDef.slug}`,
      name: evalDef.slug.replace(/-/g, " "),
      skill: evalDef.skill,
      results: {},
    };

    for (const config of configs) {
      const evalDir = path.join(
        iterationDir,
        `eval-${evalDef.id}-${evalDef.slug}`,
        config
      );

      const gradingPath = path.join(evalDir, "grading.json");
      const timingPath = path.join(evalDir, "timing.json");

      if (fs.existsSync(gradingPath)) {
        const grading = JSON.parse(fs.readFileSync(gradingPath, "utf-8"));
        const timing = fs.existsSync(timingPath)
          ? JSON.parse(fs.readFileSync(timingPath, "utf-8"))
          : {};

        evalEntry.results[config] = {
          assertions_passed: grading.assertions_passed,
          assertions_total: grading.assertions_total,
          pass_rate: grading.pass_rate,
          failed_assertions: grading.assertions
            .filter((a) => !a.passed)
            .map((a) => a.id),
          total_tokens: timing.total_tokens || 0,
          duration_ms: timing.duration_ms || 0,
          cost_usd: timing.cost_usd || 0,
        };
      }
    }

    benchmark.evals.push(evalEntry);
  }

  // Compute summaries per config
  for (const config of configs) {
    const results = benchmark.evals
      .map((e) => e.results[config])
      .filter(Boolean);

    benchmark.summary[config] = {
      assertions_passed: results.reduce(
        (sum, r) => sum + r.assertions_passed,
        0
      ),
      assertions_total: results.reduce(
        (sum, r) => sum + r.assertions_total,
        0
      ),
      pass_rate:
        results.reduce((sum, r) => sum + r.assertions_total, 0) > 0
          ? results.reduce((sum, r) => sum + r.assertions_passed, 0) /
            results.reduce((sum, r) => sum + r.assertions_total, 0)
          : 0,
      total_tokens: results.reduce((sum, r) => sum + r.total_tokens, 0),
      total_duration_ms: results.reduce((sum, r) => sum + r.duration_ms, 0),
      total_cost_usd: results.reduce((sum, r) => sum + r.cost_usd, 0),
    };
  }

  fs.writeFileSync(
    path.join(iterationDir, "benchmark.json"),
    JSON.stringify(benchmark, null, 2),
    "utf-8"
  );

  log("\nBenchmark saved:", path.join(iterationDir, "benchmark.json"));
  return benchmark;
}

function loadPreviousBenchmark(repoRoot, currentIteration) {
  const prevDir = path.join(
    repoRoot,
    WORKSPACE_PATH,
    `iteration-${currentIteration - 1}`
  );
  const prevPath = path.join(prevDir, "benchmark.json");

  if (!fs.existsSync(prevPath)) {
    return null;
  }

  return JSON.parse(fs.readFileSync(prevPath, "utf-8"));
}

function detectRegressions(benchmark, previousBenchmark) {
  if (!previousBenchmark) return { regressions: [], improvements: [] };

  const regressions = [];
  const improvements = [];

  const prevByEvalNum = {};
  for (const prevEval of previousBenchmark.evals) {
    const match = prevEval.id.match(/eval-(\d+)/);
    if (match) prevByEvalNum[match[1]] = prevEval;
  }

  for (const evalEntry of benchmark.evals) {
    const evalNum = String(
      evalEntry.id.match(/eval-(\d+)/)?.[1]
    );
    const prevEval = prevByEvalNum[evalNum];
    if (!prevEval) continue;

    const currResult = evalEntry.results.with_skill || evalEntry.results.no_skill;
    // Fallback chain for backward compat with baseline-only and iterations 1-3
    const prevResult = prevEval.results.with_skill || prevEval.results.no_skill || prevEval.results.old_skill;
    if (!currResult || !prevResult) continue;

    const prevFailed = new Set(prevResult.failed_assertions || []);
    const currFailed = new Set(currResult.failed_assertions || []);

    for (const assertionId of currFailed) {
      if (!prevFailed.has(assertionId)) {
        regressions.push({
          eval: evalEntry.id,
          assertion: assertionId,
        });
      }
    }

    for (const assertionId of prevFailed) {
      if (!currFailed.has(assertionId)) {
        improvements.push({
          eval: evalEntry.id,
          assertion: assertionId,
        });
      }
    }
  }

  return { regressions, improvements };
}

function generateReport(benchmark, previousBenchmark, iterationDir, args) {
  const { regressions, improvements } = detectRegressions(
    benchmark,
    previousBenchmark
  );
  const configs = args.baselineOnly ? ["no_skill"] : ["with_skill"];
  if (args.baseline && !args.baselineOnly) configs.push("no_skill");

  let md = `# Eval Review — Iteration ${args.iteration}\n\n`;
  md += `**Model:** ${args.model} · **Date:** ${new Date().toISOString().split("T")[0]} · **Evals:** ${benchmark.evals.length}\n\n`;

  // Summary
  md += `## Summary\n\n`;
  for (const config of configs) {
    const s = benchmark.summary[config];
    if (!s) continue;
    md += `**${config}:** ${s.assertions_passed}/${s.assertions_total} (${(s.pass_rate * 100).toFixed(1)}%)`;
    md += ` · ${s.total_tokens} tokens · ${(s.total_duration_ms / 1000).toFixed(1)}s`;
    if (s.total_cost_usd > 0) md += ` · $${s.total_cost_usd.toFixed(4)}`;
    md += `\n\n`;
  }

  // Regression summary
  if (previousBenchmark) {
    md += `## Delta vs Iteration ${args.iteration - 1}\n\n`;
    if (regressions.length === 0 && improvements.length === 0) {
      md += `No changes.\n\n`;
    } else {
      if (regressions.length > 0) {
        md += `**Regressions (${regressions.length}):**\n`;
        for (const r of regressions) {
          md += `- ❌ ${r.eval}: \`${r.assertion}\`\n`;
        }
        md += `\n`;
      }
      if (improvements.length > 0) {
        md += `**Improvements (${improvements.length}):**\n`;
        for (const imp of improvements) {
          md += `- ✅ ${imp.eval}: \`${imp.assertion}\`\n`;
        }
        md += `\n`;
      }
    }
  }

  // Resource comparison with previous iteration
  if (previousBenchmark) {
    md += `## Resource Comparison vs Iteration ${args.iteration - 1}\n\n`;
    md += `| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |\n`;
    md += `|------|-----------|------|--------|------|---------|------|\n`;

    const prevByEvalNum = {};
    for (const prevEval of previousBenchmark.evals) {
      const match = prevEval.id.match(/eval-(\d+)/);
      if (match) prevByEvalNum[match[1]] = prevEval;
    }

    for (const evalEntry of benchmark.evals) {
      const evalNum = String(evalEntry.id.match(/eval-(\d+)/)?.[1]);
      const curr = evalEntry.results.with_skill || evalEntry.results.no_skill;
      const prev = prevByEvalNum[evalNum]?.results?.with_skill || prevByEvalNum[evalNum]?.results?.no_skill || prevByEvalNum[evalNum]?.results?.old_skill;

      // Also check timing.json for timed-out evals
      const currConfig = evalEntry.results.with_skill ? "with_skill" : "no_skill";
      const timingPath = path.join(iterationDir, evalEntry.id, currConfig, "timing.json");
      let timedOut = false;
      if (fs.existsSync(timingPath)) {
        const t = JSON.parse(fs.readFileSync(timingPath, "utf-8"));
        if (t.error) timedOut = true;
      }

      const passRate = curr ? `${curr.assertions_passed}/${curr.assertions_total}` : (timedOut ? "T/O" : "—");
      const prevPassRate = prev ? `${prev.assertions_passed}/${prev.assertions_total}` : "—";
      const tokens = curr ? String(curr.total_tokens) : "—";
      const prevTokens = prev ? String(prev.total_tokens) : "—";
      const time = curr ? (curr.duration_ms / 1000).toFixed(1) : (timedOut ? "T/O" : "—");
      const prevTime = prev ? (prev.duration_ms / 1000).toFixed(1) : "—";

      md += `| ${evalEntry.id} | ${passRate} | ${prevPassRate} | ${tokens} | ${prevTokens} | ${time} | ${prevTime} |\n`;
    }
    md += `\n`;
  }

  // Per-eval breakdown
  md += `## Per-Eval Results\n\n`;
  for (const evalEntry of benchmark.evals) {
    for (const config of configs) {
      const result = evalEntry.results[config];
      if (!result) continue;

      const status = result.pass_rate === 1 ? "✅" : "⚠️";
      md += `### ${status} Eval ${evalEntry.id} [${config}]\n\n`;
      md += `**${result.assertions_passed}/${result.assertions_total}** · ${result.total_tokens} tokens · ${result.duration_ms}ms\n\n`;

      // Load grading for evidence
      const gradingPath = path.join(
        iterationDir,
        evalEntry.id,
        config,
        "grading.json"
      );
      if (fs.existsSync(gradingPath)) {
        const grading = JSON.parse(fs.readFileSync(gradingPath, "utf-8"));
        for (const a of grading.assertions) {
          const mark = a.passed ? "✅" : "❌";
          md += `- ${mark} **${a.id}**: ${a.text}\n`;
          if (!a.passed && a.evidence) {
            md += `  > ${a.evidence}\n`;
          }
        }
      }
      md += `\n`;
    }
  }

  const reportPath = path.join(iterationDir, "eval-review.md");
  fs.writeFileSync(reportPath, md, "utf-8");
  log("Report saved:", reportPath);
}

process.on("unhandledRejection", (reason) => {
  console.error("Unhandled rejection:", reason);
});

process.on("SIGINT", () => {
  console.error("\nInterrupted — partial results may be in the iteration directory.");
  process.exit(130);
});

main().catch((err) => {
  console.error("Fatal error:", err.message);
  if (err.stack) console.error(err.stack);
  process.exit(1);
});
