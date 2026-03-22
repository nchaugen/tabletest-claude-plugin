#!/usr/bin/env node

const { execSync, spawn } = require("child_process");
const fs = require("fs");
const path = require("path");

const EVALS_PATH = "skills-workspace/evals/evals.json";
const WORKSPACE_PATH = "skills-workspace";

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

async function gradeOne(evalDef, config, iterationDir, model) {
  const evalDir = path.join(
    iterationDir,
    `eval-${evalDef.id}-${evalDef.slug}`,
    config
  );
  const responsePath = path.join(evalDir, "outputs", "response.md");

  if (!fs.existsSync(responsePath)) {
    console.log(`  Skipping eval ${evalDef.id} [${config}] — no response`);
    return;
  }

  const response = fs.readFileSync(responsePath, "utf-8");
  if (response.startsWith("ERROR:")) {
    console.log(`  Skipping eval ${evalDef.id} [${config}] — generation error`);
    return;
  }

  console.log(`  Grading eval ${evalDef.id} (${evalDef.slug}) [${config}]...`);

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
    const match = gradingText.match(/```(?:json)?\s*([\s\S]*?)```/);
    if (match) {
      grading = JSON.parse(match[1]);
    } else {
      throw new Error(
        `Failed to parse grading JSON for eval ${evalDef.id}: ${gradingText.slice(0, 200)}`
      );
    }
  }

  // Add summary fields
  grading.assertions_passed = grading.assertions.filter((a) => a.passed).length;
  grading.assertions_total = grading.assertions.length;
  grading.pass_rate =
    grading.assertions_total > 0
      ? grading.assertions_passed / grading.assertions_total
      : 0;

  fs.writeFileSync(
    path.join(evalDir, "grading.json"),
    JSON.stringify(grading, null, 2),
    "utf-8"
  );

  const status = grading.pass_rate === 1 ? "✓" : "✗";
  console.log(
    `  ${status} Eval ${evalDef.id} [${config}] — ${grading.assertions_passed}/${grading.assertions_total}`
  );
}

async function gradeResponses(evals, iterationDir, args) {
  const configs = ["with_skill"];
  if (args.baseline) configs.push("no_skill");

  console.log(`\nGrading responses...`);

  // Grade sequentially — grading is cheap, no need for parallelism
  for (const evalDef of evals) {
    for (const config of configs) {
      await gradeOne(evalDef, config, iterationDir, args.model);
    }
  }
}
function aggregateResults(evals, iterationDir, args) {
  const configs = ["with_skill"];
  if (args.baseline) configs.push("no_skill");

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

  console.log("\nBenchmark saved:", path.join(iterationDir, "benchmark.json"));
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

    const currResult = evalEntry.results.with_skill;
    // Fallback to old_skill for backward compat with iterations 1-3
    const prevResult = prevEval.results.with_skill || prevEval.results.old_skill;
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
  const configs = ["with_skill"];
  if (args.baseline) configs.push("no_skill");

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
  console.log("Report saved:", reportPath);
}

main().catch((err) => {
  console.error("Fatal error:", err.message);
  process.exit(1);
});
