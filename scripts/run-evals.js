#!/usr/bin/env node

const { execSync, spawn } = require("child_process");
const fs = require("fs");
const path = require("path");

function evalsDir(skill) { return `evals/${skill}`; }
function iterationsDir(skill) { return `iterations/${skill}`; }
function variantSkillDir(skill, variant) { return `skill-variants/${skill}/${variant}`; }
function variantIterationsDir(skill, variant) { return `iterations/${skill}/${variant}`; }

function loadEvalsFromDir(evalsDir) {
  const entries = fs.readdirSync(evalsDir).filter(e => e.startsWith("eval-"));
  entries.sort((a, b) => {
    const idA = parseInt(a.split("-")[1], 10);
    const idB = parseInt(b.split("-")[1], 10);
    return idA - idB;
  });
  return entries.map(entry => {
    const dir = path.join(evalsDir, entry);
    const meta = JSON.parse(fs.readFileSync(path.join(dir, "eval.json"), "utf-8"));
    meta.prompt = fs.readFileSync(path.join(dir, "prompt.md"), "utf-8").replace(/\n$/, "");
    meta.expected_output = fs.readFileSync(path.join(dir, "expected_output.md"), "utf-8").replace(/\n$/, "");
    return meta;
  });
}

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
- Return valid JSON only. No markdown code fences. No text before or after the JSON.
- Keep evidence short (under 80 chars). Summarise or truncate long quotes. Replace triple-quotes or special characters with ellipsis.
- Ensure all strings in the JSON are properly escaped (especially double quotes within values).`;

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
    skill: null,
    variant: null,
    compareOfficial: false,
    evals: null,       // null = all, or array of ids
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
      case "--skill":
        args.skill = argv[++i];
        break;
      case "--variant":
        args.variant = argv[++i];
        break;
      case "--compare-official":
        args.compareOfficial = true;
        break;
      case "--evals":
        args.evals = parseEvalIds(argv[++i]);
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

  if (!args.iteration || !args.skill) {
    console.error("Usage: node scripts/run-evals.js --skill SKILL --iteration N [options]");
    console.error("Options:");
    console.error("  --skill SKILL       Skill to evaluate (required: tabletest, spec-by-example)");
    console.error("  --variant NAME      Run a skill variant instead of the official skill");
    console.error("  --compare-official   Compare variant results against latest official benchmark");
    console.error("  --evals 1,2,3       Run specific evals (supports ranges: 1-13)");
    console.error("  --model MODEL       Model to use (default: sonnet)");
    console.error("  --grading-model M   Model for grading (default: haiku)");
    console.error("  --grading-suffix S  Write grading to grading-S.json instead of grading.json");
    console.error("  --parallel N        Max parallel evals (default: 4)");
    console.error("  --grade-only        Re-grade existing outputs");
    process.exit(1);
  }

  if (args.variant && !args.skill) {
    console.error("Error: --variant requires --skill");
    process.exit(1);
  }

  if (args.compareOfficial && !args.variant) {
    console.error("Error: --compare-official requires --variant");
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

  let evals = loadEvalsFromDir(path.join(repoRoot, evalsDir(args.skill)));
  if (args.evals) {
    evals = evals.filter((e) => args.evals.includes(e.id));
  }

  // Determine iteration directory based on variant
  const iterBase = args.variant
    ? variantIterationsDir(args.skill, args.variant)
    : iterationsDir(args.skill);
  const iterationDir = path.join(repoRoot, iterBase, `iteration-${args.iteration}`);
  fs.mkdirSync(iterationDir, { recursive: true });

  // Set up log file (sync writes so output survives crashes)
  logFile = path.join(iterationDir, "run.log");
  fs.appendFileSync(logFile, `\n--- Run started at ${new Date().toISOString()} ---\n`);

  const label = args.variant ? `variant=${args.variant}` : "official";
  log(
    `\nEval run: ${args.skill} (${label}), iteration ${args.iteration}, ${evals.length} evals, model ${args.model}`
  );

  // Validate variant directory exists
  if (args.variant) {
    const variantDir = path.join(repoRoot, variantSkillDir(args.skill, args.variant));
    if (!fs.existsSync(path.join(variantDir, "SKILL.md"))) {
      console.error(`Error: Variant skill file not found: ${path.join(variantDir, "SKILL.md")}`);
      process.exit(1);
    }
  }

  const worktreeMode = args.variant || "skill";
  const worktreePath = args.gradeOnly ? null : setupWorktree(repoRoot, worktreeMode, args);

  try {
    if (!args.gradeOnly) {
      await generateResponses(evals, worktreePath, iterationDir, args);
    }
    await gradeResponses(evals, iterationDir, args);
    const benchmark = aggregateResults(evals, iterationDir, args);
    const previousBenchmark = loadPreviousBenchmark(repoRoot, args);
    const officialBenchmark = args.compareOfficial ? loadOfficialBenchmark(repoRoot, args.skill) : null;
    generateReport(benchmark, previousBenchmark, officialBenchmark, iterationDir, args);
    log("\nDone. Results in:", iterationDir);
  } finally {
    if (worktreePath) {
      cleanupWorktree(worktreePath);
    }
    logFile = null;
  }
}

function setupWorktree(repoRoot, mode = "skill", args = {}) {
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

  // Eval definitions: remove eval.json and expected_output.md (answer keys), keep prompt.md (input)
  const wtEvalsDir = path.join(worktreePath, "evals");
  if (fs.existsSync(wtEvalsDir)) {
    for (const skillDir of fs.readdirSync(wtEvalsDir)) {
      const skillEvalsDir = path.join(wtEvalsDir, skillDir);
      if (!fs.statSync(skillEvalsDir).isDirectory()) continue;
      for (const entry of fs.readdirSync(skillEvalsDir)) {
        if (!entry.startsWith("eval-")) continue;
        const dir = path.join(skillEvalsDir, entry);
        for (const file of ["eval.json", "expected_output.md"]) {
          const p = path.join(dir, file);
          if (fs.existsSync(p)) fs.rmSync(p);
        }
      }
    }
    removals.push("eval definitions (eval.json + expected_output.md)");
  }

  // Prior iteration outputs (model answers to the same prompts)
  const iterDir = path.join(worktreePath, "iterations");
  if (fs.existsSync(iterDir)) {
    fs.rmSync(iterDir, { recursive: true });
    removals.push("iterations/");
  }

  // Skill variants (so agent can't discover other variants)
  const variantsDir = path.join(worktreePath, "skill-variants");
  if (fs.existsSync(variantsDir)) {
    fs.rmSync(variantsDir, { recursive: true });
    removals.push("skill-variants/");
  }

  // Legacy skills-workspace (if still present)
  const legacyDir = path.join(worktreePath, "skills-workspace");
  if (fs.existsSync(legacyDir)) {
    fs.rmSync(legacyDir, { recursive: true });
    removals.push("skills-workspace/");
  }

  // Variant: swap the skill files with variant content
  if (args.variant) {
    applyVariant(worktreePath, args.skill, path.join(repoRoot, variantSkillDir(args.skill, args.variant)));
    removals.push(`applied variant: ${args.variant}`);
  }

  log(`  Removed for isolation: ${removals.join(", ")}`);
  return worktreePath;
}

function applyVariant(worktreePath, skillName, variantSourceDir) {
  const skillDir = path.join(worktreePath, "skills", skillName);

  // Remove existing skill content
  if (fs.existsSync(skillDir)) {
    for (const entry of fs.readdirSync(skillDir)) {
      fs.rmSync(path.join(skillDir, entry), { recursive: true });
    }
  } else {
    fs.mkdirSync(skillDir, { recursive: true });
  }

  // Copy variant files into skill directory
  for (const entry of fs.readdirSync(variantSourceDir)) {
    fs.cpSync(
      path.join(variantSourceDir, entry),
      path.join(skillDir, entry),
      { recursive: true }
    );
  }
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

async function generateResponses(evals, worktreePath, iterationDir, args) {
  log(
    `\nGenerating responses (parallel=${args.parallel})...`
  );

  const totalJobs = evals.length;
  let completedJobs = 0;
  const startTime = Date.now();

  for (let i = 0; i < evals.length; i += args.parallel) {
    const batch = evals.slice(i, i + args.parallel);
    const batchNum = Math.floor(i / args.parallel) + 1;
    const totalBatches = Math.ceil(evals.length / args.parallel);
    const elapsed = ((Date.now() - startTime) / 1000).toFixed(0);
    log(`  [batch ${batchNum}/${totalBatches}, ${completedJobs}/${totalJobs} done, ${elapsed}s elapsed]`);

    await Promise.all(
      batch.map((evalDef) =>
        generateOne(evalDef, worktreePath, iterationDir, args.model)
      )
    );
    completedJobs += batch.length;
  }
}

async function generateOne(evalDef, worktreePath, iterationDir, model) {
  const evalDir = path.join(
    iterationDir,
    `eval-${evalDef.id}-${evalDef.slug}`
  );
  fs.mkdirSync(path.join(evalDir, "outputs"), { recursive: true });

  // Skip if already completed (enables resuming interrupted runs)
  const timingPath = path.join(evalDir, "timing.json");
  if (fs.existsSync(timingPath)) {
    try {
      const existing = JSON.parse(fs.readFileSync(timingPath, "utf-8"));
      if (existing.duration_ms != null && !existing.error) {
        log(`  ⏭ Eval ${evalDef.id} — already completed, skipping`);
        return;
      }
    } catch { /* re-run if timing.json is corrupt */ }
  }

  log(`  Running eval ${evalDef.id} (${evalDef.slug})...`);

  try {
    const result = await runClaude({
      prompt: evalDef.prompt,
      model,
      cwd: worktreePath,
      pluginDir: worktreePath,
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
      `  ✓ Eval ${evalDef.id} — ${timing.total_tokens} tokens, ${timing.duration_ms}ms`
    );
  } catch (err) {
    logError(`  ✗ Eval ${evalDef.id} — ${err.message}`);
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

async function gradeOne(evalDef, iterationDir, model, gradingSuffix = null) {
  const evalDir = path.join(
    iterationDir,
    `eval-${evalDef.id}-${evalDef.slug}`
  );
  const responsePath = path.join(evalDir, "outputs", "response.md");

  if (!fs.existsSync(responsePath)) {
    log(`  Skipping eval ${evalDef.id} — no response`);
    return;
  }

  const response = fs.readFileSync(responsePath, "utf-8");
  if (response.startsWith("ERROR:")) {
    log(`  Skipping eval ${evalDef.id} — generation error`);
    return;
  }

  log(`  Grading eval ${evalDef.id} (${evalDef.slug})...`);

  const gradingPrompt = buildGradingPrompt(evalDef, response);

  const result = await runClaude({
    prompt: gradingPrompt,
    systemPrompt: GRADING_SYSTEM_PROMPT,
    model: model,
    cwd: process.cwd(),
  });

  // Parse grading JSON from the response — try multiple extraction strategies
  const gradingText = result.result || "";
  let grading;

  function extractJson(text) {
    const start = text.indexOf("{");
    const end = text.lastIndexOf("}");
    if (start === -1 || end <= start) return null;
    return text.slice(start, end + 1);
  }

  // Repair common JSON issues: unescaped quotes inside string values
  function repairJson(text) {
    // Fix unescaped quotes in "evidence" values — the most common failure mode.
    // Strategy: find "evidence": "..." patterns and escape inner quotes.
    return text.replace(/"evidence":\s*"((?:[^"\\]|\\.)*)(")((?:[^"\\]|\\.)*"[^,}\]]*)/g,
      (match) => {
        // Fall back to a simpler approach: find each evidence value and escape it
        return match;
      }
    ) || text;
  }

  // More robust repair: re-serialize by finding assertion blocks with regex
  function repairGradingJson(text) {
    const json = extractJson(text) || text;
    const assertions = [];
    // Match each assertion object, tolerant of broken evidence strings
    const pattern = /"id"\s*:\s*"([^"]*)"[\s\S]*?"text"\s*:\s*"((?:[^"\\]|\\.)*)"[\s\S]*?"passed"\s*:\s*(true|false)[\s\S]*?"evidence"\s*:\s*"([\s\S]*?)"\s*\n?\s*[}\]]/g;
    let m;
    while ((m = pattern.exec(json)) !== null) {
      assertions.push({
        id: m[1],
        text: m[2].replace(/\\"/g, '"'),
        passed: m[3] === "true",
        evidence: m[4].replace(/\\"/g, '"').replace(/"/g, "'").replace(/\n/g, " ").trim(),
      });
    }
    if (assertions.length === 0) return null;
    return { assertions };
  }

  const parseAttempts = [
    () => JSON.parse(gradingText),
    () => {
      const m = gradingText.match(/```(?:json)?\s*([\s\S]*?)```/);
      if (!m) throw new Error("no fence");
      return JSON.parse(m[1]);
    },
    () => {
      const json = extractJson(gradingText);
      if (!json) throw new Error("no braces");
      return JSON.parse(json);
    },
    () => {
      const repaired = repairGradingJson(gradingText);
      if (!repaired) throw new Error("repair failed");
      return repaired;
    },
  ];
  for (const attempt of parseAttempts) {
    try {
      grading = attempt();
      break;
    } catch {
      // try next strategy
    }
  }
  if (!grading) {
    throw new Error(
      `Failed to parse grading JSON for eval ${evalDef.id}: ${gradingText.slice(0, 200)}`
    );
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
    `  ${status} Eval ${evalDef.id} — ${grading.assertions_passed}/${grading.assertions_total}`
  );
}

async function gradeResponses(evals, iterationDir, args) {
  log(`\nGrading responses (model=${args.gradingModel}, parallel=${args.parallel})...`);

  for (let i = 0; i < evals.length; i += args.parallel) {
    const batch = evals.slice(i, i + args.parallel);
    await Promise.all(
      batch.map((evalDef) =>
        gradeOne(evalDef, iterationDir, args.gradingModel, args.gradingSuffix).catch((err) => {
          logError(`  ✗ Eval ${evalDef.id} — GRADING FAILED: ${err.message}`);
          const errDir = path.join(iterationDir, `eval-${evalDef.id}-${evalDef.slug}`);
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
// Unwrap results from old benchmark format (results.with_skill) or new flat format
function unwrapResults(evalEntry) {
  if (!evalEntry || !evalEntry.results) return null;
  if (evalEntry.results.assertions_passed !== undefined) return evalEntry.results;
  return evalEntry.results.with_skill || evalEntry.results.no_skill || evalEntry.results.old_skill || Object.values(evalEntry.results)[0] || null;
}

function unwrapSummary(benchmark) {
  if (!benchmark || !benchmark.summary) return null;
  if (benchmark.summary.assertions_passed !== undefined) return benchmark.summary;
  return benchmark.summary.with_skill || benchmark.summary.no_skill || Object.values(benchmark.summary)[0] || null;
}

function aggregateResults(evals, iterationDir, args) {
  const benchmark = {
    skill_name: args.variant ? `${args.skill} (${args.variant})` : args.skill,
    iteration: args.iteration,
    model: args.model,
    timestamp: new Date().toISOString(),
    evals: [],
    summary: {},
  };

  const regradedEntries = [];
  for (const evalDef of evals) {
    const evalDir = path.join(
      iterationDir,
      `eval-${evalDef.id}-${evalDef.slug}`
    );

    const evalEntry = {
      id: `eval-${evalDef.id}-${evalDef.slug}`,
      name: evalDef.slug.replace(/-/g, " "),
      skill: evalDef.skill,
      results: {},
    };

    const gradingPath = path.join(evalDir, "grading.json");
    const timingPath = path.join(evalDir, "timing.json");

    if (fs.existsSync(gradingPath)) {
      const grading = JSON.parse(fs.readFileSync(gradingPath, "utf-8"));
      const timing = fs.existsSync(timingPath)
        ? JSON.parse(fs.readFileSync(timingPath, "utf-8"))
        : {};

      evalEntry.results = {
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

    regradedEntries.push(evalEntry);
  }

  // When re-grading a subset, merge with existing benchmark instead of replacing
  const benchPath = path.join(iterationDir, "benchmark.json");
  if (args.gradeOnly && fs.existsSync(benchPath)) {
    const existing = JSON.parse(fs.readFileSync(benchPath, "utf-8"));
    for (const newEntry of regradedEntries) {
      const idx = existing.evals.findIndex((e) => e.id === newEntry.id);
      if (idx !== -1) existing.evals[idx] = newEntry;
      else existing.evals.push(newEntry);
    }
    benchmark.evals = existing.evals;
  } else {
    benchmark.evals = regradedEntries;
  }

  // Compute summary
  const results = benchmark.evals
    .map((e) => unwrapResults(e))
    .filter(Boolean);

  benchmark.summary = {
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

  fs.writeFileSync(
    path.join(iterationDir, "benchmark.json"),
    JSON.stringify(benchmark, null, 2),
    "utf-8"
  );

  log("\nBenchmark saved:", path.join(iterationDir, "benchmark.json"));
  return benchmark;
}

function loadPreviousBenchmark(repoRoot, args) {
  const iterBase = args.variant
    ? variantIterationsDir(args.skill, args.variant)
    : iterationsDir(args.skill);
  const prevDir = path.join(repoRoot, iterBase, `iteration-${args.iteration - 1}`);
  const prevPath = path.join(prevDir, "benchmark.json");

  if (!fs.existsSync(prevPath)) {
    return null;
  }

  return JSON.parse(fs.readFileSync(prevPath, "utf-8"));
}

function loadOfficialBenchmark(repoRoot, skill) {
  // Find the latest official iteration with a benchmark
  const officialDir = path.join(repoRoot, iterationsDir(skill));
  if (!fs.existsSync(officialDir)) return null;

  const iterations = fs.readdirSync(officialDir)
    .filter(e => e.startsWith("iteration-") && fs.statSync(path.join(officialDir, e)).isDirectory())
    .sort((a, b) => {
      const numA = parseInt(a.split("-")[1], 10);
      const numB = parseInt(b.split("-")[1], 10);
      return numB - numA; // descending — latest first
    });

  for (const iter of iterations) {
    const benchPath = path.join(officialDir, iter, "benchmark.json");
    if (fs.existsSync(benchPath)) {
      const benchmark = JSON.parse(fs.readFileSync(benchPath, "utf-8"));
      benchmark._iterationName = iter;
      return benchmark;
    }
  }
  return null;
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

    const currResult = unwrapResults(evalEntry);
    const prevResult = unwrapResults(prevEval);
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

function generateReport(benchmark, previousBenchmark, officialBenchmark, iterationDir, args) {
  const { regressions, improvements } = detectRegressions(
    benchmark,
    previousBenchmark
  );
  const label = args.variant ? `${args.skill} variant=${args.variant}` : args.skill;
  let md = `# Eval Review — ${label}, Iteration ${args.iteration}\n\n`;
  md += `**Model:** ${args.model} · **Date:** ${new Date().toISOString().split("T")[0]} · **Evals:** ${benchmark.evals.length}\n\n`;

  // Summary
  md += `## Summary\n\n`;
  const s = unwrapSummary(benchmark);
  if (s) {
    md += `${s.assertions_passed}/${s.assertions_total} (${(s.pass_rate * 100).toFixed(1)}%)`;
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
      const curr = unwrapResults(evalEntry);
      const prev = unwrapResults(prevByEvalNum[evalNum]);

      // Also check timing.json for timed-out evals
      const timingPath = path.join(iterationDir, evalEntry.id, "timing.json");
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
    const result = unwrapResults(evalEntry);
    if (!result) continue;

    const status = result.pass_rate === 1 ? "✅" : "⚠️";
    md += `### ${status} Eval ${evalEntry.id}\n\n`;
    md += `**${result.assertions_passed}/${result.assertions_total}** · ${result.total_tokens} tokens · ${result.duration_ms}ms\n\n`;

    // Load grading for evidence
    const gradingPath = path.join(
      iterationDir,
      evalEntry.id,
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

  // Cross-comparison with official benchmark (when --compare-official)
  if (officialBenchmark && args.variant) {
    const officialLabel = officialBenchmark._iterationName || "official";
    md += `## Variant vs Official (${officialLabel})\n\n`;

    // Summary comparison
    const variantSummary = unwrapSummary(benchmark);
    const officialSummary = unwrapSummary(officialBenchmark);
    if (variantSummary && officialSummary) {
      md += `| Source | Pass Rate | Tokens | Cost |\n`;
      md += `|--------|-----------|--------|------|\n`;
      md += `| ${args.variant} (iter ${args.iteration}) | ${variantSummary.assertions_passed}/${variantSummary.assertions_total} (${(variantSummary.pass_rate * 100).toFixed(1)}%) | ${variantSummary.total_tokens} | $${(variantSummary.total_cost_usd || 0).toFixed(4)} |\n`;
      md += `| official (${officialLabel}) | ${officialSummary.assertions_passed}/${officialSummary.assertions_total} (${(officialSummary.pass_rate * 100).toFixed(1)}%) | ${officialSummary.total_tokens} | $${(officialSummary.total_cost_usd || 0).toFixed(4)} |\n`;
      md += `\n`;
    }

    // Per-assertion diff
    const officialByEvalNum = {};
    for (const e of officialBenchmark.evals) {
      const match = e.id.match(/eval-(\d+)/);
      if (match) officialByEvalNum[match[1]] = e;
    }

    const loadBearing = [];
    md += `### Per-Assertion Comparison\n\n`;
    md += `| Eval | Assertion | official | ${args.variant} |\n`;
    md += `|------|-----------|----------|${"—".repeat(args.variant.length + 2)}|\n`;

    for (const evalEntry of benchmark.evals) {
      const evalNum = String(evalEntry.id.match(/eval-(\d+)/)?.[1]);
      const officialEval = officialByEvalNum[evalNum];
      if (!officialEval) continue;

      const currResult = unwrapResults(evalEntry);
      const offResult = unwrapResults(officialEval);
      if (!currResult || !offResult) continue;

      const currFailed = new Set(currResult.failed_assertions || []);
      const offFailed = new Set(offResult.failed_assertions || []);

      // Get all assertion IDs from grading
      const gradingPath = path.join(iterationDir, evalEntry.id, "grading.json");
      let allAssertions = [];
      if (fs.existsSync(gradingPath)) {
        const grading = JSON.parse(fs.readFileSync(gradingPath, "utf-8"));
        allAssertions = grading.assertions.map(a => a.id);
      }

      for (const aid of allAssertions) {
        const offPass = !offFailed.has(aid);
        const currPass = !currFailed.has(aid);
        if (offPass !== currPass) {
          md += `| ${evalEntry.id} | ${aid} | ${offPass ? "pass" : "FAIL"} | ${currPass ? "pass" : "FAIL"} |\n`;
          if (offPass && !currPass) {
            loadBearing.push({ eval: evalEntry.id, assertion: aid });
          }
        }
      }
    }
    md += `\n`;

    if (loadBearing.length > 0) {
      md += `### Load-Bearing Assertions\n\n`;
      md += `Assertions that pass with official skill but fail with ${args.variant} variant:\n\n`;
      for (const lb of loadBearing) {
        md += `- ${lb.eval}: \`${lb.assertion}\`\n`;
      }
      md += `\n`;
    } else {
      md += `No load-bearing assertions found — variant matches official on all comparable assertions.\n\n`;
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
