#!/usr/bin/env node

const { execSync, spawn } = require("child_process");
const crypto = require("crypto");
const fs = require("fs");
const path = require("path");
const { checkers } = require("./assertions");

function findFiles(dir, pattern) {
  const results = [];
  if (!fs.existsSync(dir)) return results;
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      results.push(...findFiles(full, pattern));
    } else if (pattern.test(entry.name)) {
      results.push(full);
    }
  }
  return results;
}

/**
 * Content fingerprint of an eval definition (prompt, assertions, expected
 * output, project scaffolding). Results stamped with different fingerprints
 * were measured by different eval definitions and must not be compared.
 */
function computeEvalFingerprint(evalDir) {
  const files = [];
  for (const name of ["prompt.md", "eval.json", "expected_output.md"]) {
    const p = path.join(evalDir, name);
    if (fs.existsSync(p)) files.push(p);
  }
  files.push(...findFiles(path.join(evalDir, "project"), /./));

  const hash = crypto.createHash("sha256");
  for (const file of files.sort()) {
    hash.update(path.relative(evalDir, file));
    hash.update("\0");
    hash.update(fs.readFileSync(file));
    hash.update("\0");
  }
  return hash.digest("hex").slice(0, 12);
}

/**
 * True when both entries carry a fingerprint and they differ. Entries without
 * a fingerprint (pre-guard benchmarks) are treated as comparable.
 */
function fingerprintsDiffer(a, b) {
  return Boolean(a && b && a.fingerprint && b.fingerprint && a.fingerprint !== b.fingerprint);
}

/**
 * Per-language conventions for collecting agent output, verifying the build,
 * and detecting a delivered test file. An eval opts in via "language" in
 * eval.json; absent means "jvm" (the original TableTest behaviour).
 */
const LANGUAGE_PROFILES = {
  jvm: {
    testDirs: ["src/test"],
    testFilePattern: /\.(java|kt)$/,
    rootTestFiles: false,
    buildFiles: ["pom.xml", "build.gradle", "build.gradle.kts"],
    deliverablePath: /(^|\/)src\//,
    deliverableContent: /@TableTest/,
    deliverableDescription: "test source containing @TableTest",
  },
  python: {
    testDirs: ["tests", "test"],
    testFilePattern: /(^test_[^/]*|[^/]*_test)\.py$/,
    rootTestFiles: true,
    buildFiles: ["pyproject.toml", "pytest.ini", "conftest.py"],
    deliverablePath: /(test_[^/]*|[^/]*_test)\.py$/,
    deliverableContent: /def test_/,
    deliverableDescription: "pytest test file",
  },
  swift: {
    testDirs: ["Tests"],
    testFilePattern: /\.swift$/,
    rootTestFiles: false,
    buildFiles: ["Package.swift"],
    deliverablePath: /\.swift$/,
    deliverableContent: /@Test/,
    deliverableDescription: "Swift Testing test file",
  },
};

function languageProfile(evalDef) {
  return LANGUAGE_PROFILES[evalDef.language || "jvm"];
}

function collectTestFiles(agentCwd, profile) {
  const files = [];
  for (const dir of profile.testDirs) {
    files.push(...findFiles(path.join(agentCwd, dir), profile.testFilePattern));
  }
  if (profile.rootTestFiles) {
    for (const entry of fs.readdirSync(agentCwd, { withFileTypes: true })) {
      if (entry.isFile() && profile.testFilePattern.test(entry.name)) {
        files.push(path.join(agentCwd, entry.name));
      }
    }
  }
  return files;
}

function runBuildCheck(worktreePath) {
  const hasPom = fs.existsSync(path.join(worktreePath, "pom.xml"));
  const hasGradle = fs.existsSync(path.join(worktreePath, "build.gradle"))
    || fs.existsSync(path.join(worktreePath, "build.gradle.kts"));

  if (hasPom || hasGradle) return runJvmBuildCheck(worktreePath, hasPom);
  if (fs.existsSync(path.join(worktreePath, "Package.swift"))) return runSwiftBuildCheck(worktreePath);
  if (fs.existsSync(path.join(worktreePath, "pyproject.toml"))
    || fs.existsSync(path.join(worktreePath, "pytest.ini"))) return runPytestBuildCheck(worktreePath);

  return null;
}

function runSwiftBuildCheck(worktreePath) {
  const result = { compiles: false, tests_pass: null, compile_output: "", test_output: "" };

  try {
    execSync("swift build --build-tests", { cwd: worktreePath, stdio: "pipe", timeout: 300000 });
    result.compiles = true;
  } catch (err) {
    result.compile_output = (err.stderr || err.stdout || "").toString().slice(0, 2000);
    return result;
  }

  try {
    execSync("swift test", { cwd: worktreePath, stdio: "pipe", timeout: 300000 });
    result.tests_pass = true;
  } catch (err) {
    result.tests_pass = false;
    result.test_output = (err.stderr || err.stdout || "").toString().slice(0, 2000);
  }

  return result;
}

function runPytestBuildCheck(worktreePath) {
  const result = { compiles: false, tests_pass: null, compile_output: "", test_output: "" };

  // Python has no compile step; "compiles" means pytest can import and
  // collect the test modules (exit 5 = nothing collected, also a failure).
  try {
    execSync("pytest --collect-only -q", { cwd: worktreePath, stdio: "pipe", timeout: 120000 });
    result.compiles = true;
  } catch (err) {
    result.compile_output = (err.stderr || err.stdout || "").toString().slice(0, 2000);
    return result;
  }

  try {
    execSync("pytest -q", { cwd: worktreePath, stdio: "pipe", timeout: 120000 });
    result.tests_pass = true;
  } catch (err) {
    result.tests_pass = false;
    result.test_output = (err.stderr || err.stdout || "").toString().slice(0, 2000);
  }

  return result;
}

function runJvmBuildCheck(worktreePath, hasPom) {
  const result = { compiles: false, tests_pass: null, compile_output: "", test_output: "" };

  // Detect Kotlin test files to choose the right compile task
  const hasKotlinTests = findFiles(
    path.join(worktreePath, "src", "test"),
    /\.kt$/
  ).length > 0;

  try {
    if (hasPom) {
      execSync("mvn compile test-compile -q", { cwd: worktreePath, stdio: "pipe", timeout: 120000 });
    } else {
      const compileTask = hasKotlinTests ? "compileTestKotlin" : "compileTestJava";
      execSync(`gradle ${compileTask} -q`, { cwd: worktreePath, stdio: "pipe", timeout: 120000 });
    }
    result.compiles = true;
  } catch (err) {
    result.compile_output = (err.stderr || err.stdout || "").toString().slice(0, 2000);
    return result;
  }

  // Infrastructure flakes (daemon crashes, lock timeouts) are not failing
  // tests — retry once before recording a failure.
  const infraFailure = /Test process encountered an unexpected problem|Gradle build daemon disappeared|Could not connect to the Gradle daemon|Timeout waiting to lock|Could not create service/i;
  const testCmd = hasPom ? "mvn test -q" : "gradle test -q";
  for (let attempt = 1; attempt <= 2; attempt++) {
    try {
      execSync(testCmd, { cwd: worktreePath, stdio: "pipe", timeout: 120000 });
      result.tests_pass = true;
      result.test_output = "";
      break;
    } catch (err) {
      const output = (err.stderr || err.stdout || "").toString();
      result.tests_pass = false;
      result.test_output = output.slice(0, 2000);
      if (attempt === 1 && infraFailure.test(output)) continue;
      break;
    }
  }

  return result;
}

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
    meta.sourceDir = dir;
    meta.fingerprint = computeEvalFingerprint(dir);
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

function resolveModel(shortName) {
  const models = {
    haiku: "claude-haiku-4-5",
    sonnet: "claude-sonnet-5",
    opus: "claude-opus-4-8",
  };
  if (models[shortName]) return models[shortName];
  // Already a full model ID (contains a dash)
  if (shortName.includes("-")) return shortName;
  return shortName;
}

// A grading call that throws costs more than itself: gradeOne discards the eval's
// already-completed batches, so one transient 500 wipes a whole eval's grading and leaves a
// benchmark with a silently missing eval. A full re-baseline makes hundreds of these calls
// over ~73 minutes, and --grade-runs multiplies them, so transient failures are expected
// rather than exceptional. Retry them; fail fast on anything the caller caused.
const DEFAULT_RETRY_POLICY = { maxAttempts: 5, baseDelayMs: 1000 };

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

function isRetryableStatus(status) {
  return status === 429 || status >= 500;
}

/** Honours Retry-After when the server sends one; otherwise exponential backoff, jittered so
 *  parallel graders do not retry in lockstep and re-collide. */
function retryDelayMs(attempt, retryAfterHeader, baseDelayMs = DEFAULT_RETRY_POLICY.baseDelayMs) {
  const retryAfterSeconds = Number(retryAfterHeader);
  if (Number.isFinite(retryAfterSeconds) && retryAfterSeconds > 0) {
    return retryAfterSeconds * 1000;
  }
  const backoff = baseDelayMs * 2 ** attempt;
  return backoff + Math.random() * backoff;
}

/**
 * POSTs to a grading API, retrying transient failures (429, 5xx, network errors).
 *
 * A 4xx other than 429 is a bad request — a missing API key, a malformed model id — and
 * retrying it just delays the same error five times over, so those fail immediately.
 *
 * `retryPolicy` exists so tests can exercise exhaustion without waiting out real backoff;
 * production always uses the default.
 */
async function postGradingRequest(url, options, apiName, retryPolicy = DEFAULT_RETRY_POLICY) {
  const { maxAttempts, baseDelayMs } = retryPolicy;

  for (let attempt = 0; ; attempt++) {
    const isLastAttempt = attempt === maxAttempts - 1;
    let resp = null;
    let networkError = null;

    try {
      resp = await fetch(url, options);
    } catch (err) {
      networkError = err;
    }

    if (resp && resp.ok) return resp;

    const retryable = networkError !== null || isRetryableStatus(resp.status);
    if (!retryable || isLastAttempt) {
      if (networkError) throw networkError;
      const body = await resp.text();
      const exhausted = retryable ? ` after ${maxAttempts} attempts` : "";
      throw new Error(`${apiName} API error ${resp.status}${exhausted}: ${body.slice(0, 300)}`);
    }

    const retryAfter = resp ? resp.headers.get("retry-after") : null;
    const delay = retryDelayMs(attempt, retryAfter, baseDelayMs);
    if (resp) await resp.text().catch(() => {}); // release the socket before waiting
    const cause = networkError ? networkError.message : `HTTP ${resp.status}`;
    log(`  ${apiName} grading call failed (${cause}) — retry ${attempt + 1}/${maxAttempts - 1} in ${Math.round(delay)}ms`);
    await sleep(delay);
  }
}

// Grading is a judgement, not a generation: sampling variance in it is pure measurement noise.
// The API default is 1.0, which was what `--grade-runs` majority voting was averaging out.
const GRADING_TEMPERATURE = 0;

// Sampling parameters were removed from the newer model families: a non-default `temperature`
// is rejected with `400 temperature is deprecated for this model`, while omitting it is accepted.
//
// The consequence is a regime fact, not a detail. On these models the grading regime CANNOT be
// "temperature 0" — there is no knob, so grading runs at the model's default sampling and is
// inherently less repeatable than haiku-at-0. A grading-model change is therefore a bigger regime
// change than it looks: it moves level AND the achievable precision floor at the same time.
// Expect to need `--grade-runs 3` majority voting to recover stability on these models, and never
// compare a temperature-0 number against one of these runs.
const MODELS_WITHOUT_SAMPLING_PARAMS = [
  "claude-sonnet-5",
  "claude-opus-5",
  "claude-opus-4-7",
  "claude-opus-4-8",
  "claude-fable-5",
  "claude-mythos-5",
];

function acceptsTemperature(resolvedModel) {
  return !MODELS_WITHOUT_SAMPLING_PARAMS.some((id) => resolvedModel.startsWith(id));
}

// The same newer families also run adaptive thinking by DEFAULT, so the response opens with a
// thinking block and the verdict JSON is no longer content[0]. Thinking is billed against
// max_tokens too, so a budget sized for a pure JSON answer truncates the verdicts instead.
// Grading keeps thinking on — a judgement is exactly the work it helps — and pays for the room.
const GRADING_MAX_TOKENS_WITH_THINKING = 16000;
const GRADING_MAX_TOKENS = 4096;

/** The grader's verdict text, wherever the model put it among thinking/text blocks. */
function extractGradingText(data) {
  if (!data || !Array.isArray(data.content)) {
    throw new Error(`Grading response had no content array: ${JSON.stringify(data).slice(0, 300)}`);
  }
  const text = data.content.find((block) => block.type === "text" && typeof block.text === "string");
  if (!text) {
    const kinds = data.content.map((b) => b.type).join(", ") || "none";
    throw new Error(`Grading response carried no text block (blocks: ${kinds})`);
  }
  return text.text;
}

async function gradeViaApi(systemPrompt, userPrompt, model, provider = "anthropic") {
  if (provider === "ollama") {
    const resp = await postGradingRequest("http://localhost:11434/api/chat", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({
        model: model,
        messages: [
          { role: "system", content: systemPrompt },
          { role: "user", content: userPrompt },
        ],
        stream: false,
        options: { temperature: GRADING_TEMPERATURE },
      }),
    }, "Ollama");
    const data = await resp.json();
    return data.message.content;
  }

  const resolved = resolveModel(model);
  const resp = await postGradingRequest("https://api.anthropic.com/v1/messages", {
    method: "POST",
    headers: {
      "x-api-key": process.env.ANTHROPIC_API_KEY,
      "anthropic-version": "2023-06-01",
      "content-type": "application/json",
    },
    body: JSON.stringify({
      model: resolved,
      max_tokens: acceptsTemperature(resolved) ? GRADING_MAX_TOKENS : GRADING_MAX_TOKENS_WITH_THINKING,
      ...(acceptsTemperature(resolved) ? { temperature: GRADING_TEMPERATURE } : {}),
      system: systemPrompt,
      messages: [{ role: "user", content: userPrompt }],
    }),
  }, "Anthropic");
  const data = await resp.json();
  return extractGradingText(data);
}

// Assertions judged per grading call. Ten keeps every judgement close to the instructions;
// the largest evals carry nearly twenty LLM assertions.
//
// batch=1 was tried and reverted: grading each assertion in isolation is systematically STRICTER
// than judging them in a batch, and it collapsed every eval's score uniformly (eval-22 27/27 ->
// 23/27, eval-30 18 -> 12) — a calibration shift, not the stabilisation it was meant to buy. The
// batching's cross-contamination (a batch-mate's verdict nudging another) is the price of the
// calibration the whole suite is tuned against; do not lower this without re-tuning every eval.
const LLM_GRADING_BATCH_SIZE = 10;

/**
 * Collapses repeated gradings of one batch into a single verdict per assertion.
 *
 * Grader disagreement is response-level, not assertion-level: a whole batch swings
 * together, so sharper assertion wording does not settle it but a second opinion does.
 * A split vote is recorded in the evidence, making an unreliable assertion visible
 * instead of silently contributing noise to the score.
 */
function majorityVote(samples, batch) {
  if (samples.length === 1) return samples[0];
  return batch.map(a => {
    const votes = samples.map(s => s.find(r => r.id === a.id)).filter(Boolean);
    const passes = votes.filter(v => v.passed).length;
    const passed = passes * 2 > votes.length;
    const winner = votes.find(v => v.passed === passed) || {};
    const split = passes !== 0 && passes !== votes.length;
    return {
      id: a.id,
      text: a.text,
      passed,
      evidence: split
        ? `[split vote ${passes}/${votes.length} pass] ${winner.evidence || ""}`
        : (winner.evidence || ""),
    };
  });
}

const GRADING_SYSTEM_PROMPT = `You are an eval grader. You will receive a model response and a list of assertions.
For each assertion, determine whether it passes or fails based on the response content.

TableTest syntax you are expected to know (these are built-in library notation, not code smells or
"unexplained values" — never fail an assertion merely because a cell or column uses them):
- A column header ending in "?" marks an expected-output column (e.g. "Total?", "Shipments?"). This is
  the TableTest convention; it is correct business/domain naming, not a violation.
- "[a, b]" is a list; "[k: v, k2: v2]" is a map; "[:]" is an empty map; "[]" an empty list. They nest
  ("[[a, b], [c]]", "[W1: [camera, lens]]") and render as structured values.
- "{a, b}" is a value set: the row runs once per listed value, each an independent case sharing the same
  expectation. It is not a magic constant and needs no explanation in the description.
- A blank cell denotes an absent / null value.
- A @TypeConverter method turns a cell (string, list, or map) into a domain object, so such a parameter
  arrives already converted and the method body needs no construction code for it.

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
    noSkill: false,
    compareOfficial: false,
    compareIteration: null,
    evals: null,       // null = all, or array of ids
    model: "sonnet",
    gradingModel: "sonnet",
    provider: "anthropic",
    gradingSuffix: null,
    // Single grading run is the standard regime. At temperature 0 (GRADING_TEMPERATURE) a
    // measured 138-slot re-grade found single-run as stable as `--grade-runs 3` (2/138 both);
    // majority voting pays 3x for no level change. `--grade-runs 3` stays a flag, reserved for
    // adjudicating a result inside the MDE. See decision grading-regime-and-promotion-batching.
    gradeRuns: 1,
    nudgeSkill: false,
    parallel: 4,
    gradeOnly: false,
    reportOnly: false,
    timeoutMs: null,   // null = use each eval's timeout_ms (or runClaude default)
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
      case "--no-skill":
        args.noSkill = true;
        break;
      case "--compare-official":
        args.compareOfficial = true;
        break;
      case "--compare-iteration":
        args.compareIteration = parseInt(argv[++i], 10);
        break;
      case "--evals":
        args.evals = parseEvalIds(argv[++i]);
        break;
      case "--model":
        args.model = argv[++i];
        break;
      case "--provider":
        args.provider = argv[++i];
        break;
      case "--grading-model":
        args.gradingModel = argv[++i];
        break;
      case "--grading-suffix":
        args.gradingSuffix = argv[++i];
        break;
      case "--grade-runs":
        args.gradeRuns = Math.max(1, parseInt(argv[++i], 10) || 1);
        break;
      case "--nudge-skill":
        args.nudgeSkill = true;
        break;
      case "--parallel":
        args.parallel = parseInt(argv[++i], 10);
        break;
      case "--timeout": {
        const seconds = parseInt(argv[++i], 10);
        if (!Number.isFinite(seconds) || seconds <= 0) {
          console.error("Error: --timeout requires a positive number of seconds");
          process.exit(1);
        }
        args.timeoutMs = seconds * 1000;
        break;
      }
      case "--grade-only":
        args.gradeOnly = true;
        break;
      case "--report-only":
        args.reportOnly = true;
        break;
      default:
        console.error(`Unknown argument: ${argv[i]}`);
        process.exit(1);
    }
  }

  if (!args.iteration || !args.skill) {
    console.error("Usage: node scripts/run-evals.js --skill SKILL --iteration N [options]");
    console.error("Options:");
    console.error("  --skill SKILL       Skill to evaluate (required; a directory under evals/)");
    console.error("  --variant NAME      Run a skill variant instead of the official skill");
    console.error("  --no-skill          Baseline run with the skill removed from the plugin");
    console.error("  --compare-iteration N Compare against iteration N instead of the previous one");
    console.error("  --compare-official   Compare variant results against latest official benchmark");
    console.error("  --evals 1,2,3       Run specific evals (supports ranges: 1-13)");
    console.error("  --provider PROV     Provider to use (anthropic, ollama) (default: anthropic)");
    console.error("  --model MODEL       Model to use (default: sonnet)");
    console.error("  --grading-model M   Model for grading (default: sonnet)");
    console.error("  --nudge-skill       Append a prompt nudge to invoke the relevant skill (for models that don't trigger skills on their own)");
    console.error("  --grading-suffix S  Isolate a re-grade: write grading-S.json, benchmark-S.json, eval-review-S.md");
    console.error("  --parallel N        Max parallel evals (default: 4)");
    console.error("  --timeout SECONDS   Override each eval's generation timeout (useful for slow local LLMs)");
    console.error("  --grade-runs N      Grade each assertion N times and take the majority verdict (default 1)");
    console.error("  --grade-only        Re-grade existing outputs");
    console.error("  --report-only       Regenerate report from existing benchmark.json");
    process.exit(1);
  }

  if (args.variant && !args.skill) {
    console.error("Error: --variant requires --skill");
    process.exit(1);
  }

  if (args.noSkill && args.variant) {
    console.error("Error: --no-skill and --variant are mutually exclusive");
    process.exit(1);
  }
  // A no-skill baseline behaves like a variant named "no-skill" for results
  // directories, labels, and comparisons; the worktree deletes the skill
  // instead of swapping its files.
  if (args.noSkill) args.variant = "no-skill";

  if (args.compareOfficial && !args.variant) {
    console.error("Error: --compare-official requires --variant");
    process.exit(1);
  }

  return args;
}

async function main() {
  const args = parseArgs(process.argv);

  const repoRoot = execSync("git rev-parse --show-toplevel", {
    encoding: "utf-8",
  }).trim();

  // Determine iteration directory based on variant
  const iterBase = args.variant
    ? variantIterationsDir(args.skill, args.variant)
    : iterationsDir(args.skill);
  const iterationDir = path.join(repoRoot, iterBase, `iteration-${args.iteration}`);
  fs.mkdirSync(iterationDir, { recursive: true });

  // Set up log file (sync writes so output survives crashes)
  logFile = path.join(iterationDir, "run.log");
  fs.appendFileSync(logFile, `\n--- Run started at ${new Date().toISOString()} ---\n`);

  // Report-only mode: regenerate report from existing benchmark.json
  if (args.reportOnly) {
    const benchPath = path.join(iterationDir, "benchmark.json");
    if (!fs.existsSync(benchPath)) {
      console.error(`Error: No benchmark.json found at ${benchPath}`);
      process.exit(1);
    }
    const benchmark = JSON.parse(fs.readFileSync(benchPath, "utf-8"));
    const previousBenchmark = loadPreviousBenchmark(repoRoot, args);
    const officialBenchmark = args.compareOfficial ? loadOfficialBenchmark(repoRoot, args.skill) : null;
    generateReport(benchmark, previousBenchmark, officialBenchmark, iterationDir, args);
    const regeneratedBaseline = analysisBaselineOf(args, previousBenchmark, officialBenchmark);
    writeAnalysisTodo(benchmark, regeneratedBaseline.benchmark, regeneratedBaseline.label, iterationDir, args);
    log("\nReport regenerated. Results in:", iterationDir);
    logFile = null;
    return;
  }

  if (args.provider === "anthropic" && !process.env.ANTHROPIC_API_KEY) {
    console.error("Error: ANTHROPIC_API_KEY environment variable is not set.");
    console.error("The eval script requires an API key to avoid consuming interactive plan usage.");
    console.error("Set it with: ANTHROPIC_API_KEY=sk-... node scripts/run-evals.js ...");
    process.exit(1);
  }

  let evals = loadEvalsFromDir(path.join(repoRoot, evalsDir(args.skill)));
  if (args.evals) {
    evals = evals.filter((e) => args.evals.includes(e.id));
  }

  const label = args.variant ? `variant=${args.variant}` : "official";
  log(
    `\nEval run: ${args.skill} (${label}), iteration ${args.iteration}, ${evals.length} evals, model ${args.model}, provider ${args.provider}${args.nudgeSkill ? ", nudge-skill" : ""}`
  );

  // Validate variant directory exists
  if (args.variant && !args.noSkill) {
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
    const benchmark = aggregateResults(evals, iterationDir, args, worktreePath, repoRoot);
    const previousBenchmark = loadPreviousBenchmark(repoRoot, args);
    const officialBenchmark = args.compareOfficial ? loadOfficialBenchmark(repoRoot, args.skill) : null;
    generateReport(benchmark, previousBenchmark, officialBenchmark, iterationDir, args);
    const analysisBaseline = analysisBaselineOf(args, previousBenchmark, officialBenchmark);
    writeAnalysisTodo(benchmark, analysisBaseline.benchmark, analysisBaseline.label, iterationDir, args);
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
  for (const file of ["README.md", "CLAUDE.md", "AGENTS.md", "CHANGELOG.md"]) {
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
  if (args.variant && !args.noSkill) {
    applyVariant(worktreePath, args.skill, path.join(repoRoot, variantSkillDir(args.skill, args.variant)));
    removals.push(`applied variant: ${args.variant}`);
  }

  // No-skill baseline: remove the skill under test; the rest of the plugin
  // (other skills) stays, so routing behaves as it would for current users
  if (args.noSkill) {
    const skillDir = path.join(worktreePath, "skills", args.skill);
    if (fs.existsSync(skillDir)) fs.rmSync(skillDir, { recursive: true });
    removals.push(`skills/${args.skill}/ (no-skill baseline)`);
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

function runClaude({ prompt, systemPrompt, model, provider, cwd, pluginDir, timeoutMs = 600000 }) {
  return new Promise((resolve, reject) => {
    let settled = false;
    const settle = (fn, value) => {
      if (settled) return;
      settled = true;
      fn(value);
    };

    const args = ["--print", "--output-format", "stream-json", "--verbose", "--model", model];

    args.push("--disallowedTools", "Bash(git:*)");
    args.push("--dangerously-skip-permissions");
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

    const env = { ...process.env };
    if (provider === "ollama") {
      env.ANTHROPIC_BASE_URL = "http://localhost:11434";
      env.ANTHROPIC_AUTH_TOKEN = "ollama";
    }

    const proc = spawn("claude", args, {
      cwd,
      env,
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
      const events = [];
      for (const line of stdout.split("\n")) {
        if (!line.trim()) continue;
        try { events.push(JSON.parse(line)); } catch { /* ignore non-JSON lines */ }
      }
      const resultLine = events.filter(o => o.type === "result").pop();
      if (code !== 0) {
        const detail = resultLine && resultLine.result
          ? `\n  result: ${String(resultLine.result).slice(0, 300)}`
          : "";
        const err = new Error(`claude exited with code ${code}:${detail}\n  stderr: ${stderr.slice(0, 500)}`);
        err.stdout = stdout;
        err.stderr = stderr;
        settle(reject, err);
        return;
      }
      if (!resultLine) {
        settle(reject, new Error(`No result line found in stream-json output\n${stdout.slice(0, 500)}`));
        return;
      }
      const init = events.find(o => o.type === "system" && o.subtype === "init");
      resultLine._conversationJsonl = stdout;
      resultLine._model = (init && init.model) || null;
      settle(resolve, resultLine);
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
        generateOne(evalDef, worktreePath, iterationDir, args)
      )
    );
    completedJobs += batch.length;
  }
}

// Appended to every eval prompt: evals are single-turn, so ending the turn
// with clarifying questions is a non-delivery, not professional caution.
const BATCH_MODE_NOTE =
  "\n\nThis is a non-interactive run: you cannot ask follow-up questions. " +
  "If anything is ambiguous, state your assumptions and deliver the complete result.";

// Appended to the user prompt with --nudge-skill: models that are not
// trained to trigger Claude Code skills on their own (e.g. local models via
// ollama) need an explicit pointer to the Skill tool. Injected into the user
// prompt rather than the system prompt because weak models ignore
// instructions buried in a large system prompt. Deliberately does not name a
// skill so routing between skills is still measured.
const SKILL_NUDGE =
  "\n\nIMPORTANT: This project provides skills via the Skill tool (see the available skills list). " +
  "Before writing or modifying any code, invoke the skill relevant to this task and follow its instructions.";

async function generateOne(evalDef, worktreePath, iterationDir, args) {
  const model = args.model;
  const provider = args.provider;
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

  const startedAt = Date.now();

  // Use a per-eval working directory if project scaffolding exists, otherwise use worktree root
  const projectDir = path.join(evalDef.sourceDir, "project");
  const hasProject = fs.existsSync(projectDir);
  const agentCwd = hasProject
    ? path.join(worktreePath, `eval-${evalDef.id}-work`)
    : worktreePath;

  if (hasProject) {
    fs.mkdirSync(agentCwd, { recursive: true });
    // Copy skill files so the agent can discover them
    const skillsDir = path.join(worktreePath, "skills");
    if (fs.existsSync(skillsDir)) {
      fs.cpSync(skillsDir, path.join(agentCwd, "skills"), { recursive: true });
    }
    // Copy plugin manifest
    const pluginDir = path.join(worktreePath, ".claude-plugin");
    if (fs.existsSync(pluginDir)) {
      fs.cpSync(pluginDir, path.join(agentCwd, ".claude-plugin"), { recursive: true });
    }
    // Copy project scaffolding
    fs.cpSync(projectDir, agentCwd, { recursive: true });
  }

  try {
    const result = await runClaude({
      prompt: evalDef.prompt + BATCH_MODE_NOTE + (args.nudgeSkill ? SKILL_NUDGE : ""),
      model,
      provider,
      cwd: agentCwd,
      pluginDir: agentCwd,
      timeoutMs: args.timeoutMs || evalDef.timeout_ms,
    });

    fs.writeFileSync(
      path.join(evalDir, "outputs", "response.md"),
      result.result || "",
      "utf-8"
    );

    const usage = result.usage || {};
    const timing = {
      model: result._model || null,
      models_used: Object.keys(result.modelUsage || {}),
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
      fs.writeFileSync(
        path.join(evalDir, "narration.md"),
        narrationMarkdown(result._conversationJsonl, evalDef.id),
        "utf-8"
      );
    }

    // Collect generated test files from agent working directory
    const profile = languageProfile(evalDef);
    const testFiles = collectTestFiles(agentCwd, profile);
    for (const tf of testFiles) {
      const rel = path.relative(agentCwd, tf);
      const dest = path.join(evalDir, "outputs", rel);
      fs.mkdirSync(path.dirname(dest), { recursive: true });
      fs.cpSync(tf, dest);
    }

    // Collect build files from agent working directory (for dependency assertions)
    for (const buildFile of profile.buildFiles) {
      const src = path.join(agentCwd, buildFile);
      if (fs.existsSync(src)) {
        fs.cpSync(src, path.join(evalDir, "outputs", buildFile));
      }
    }

    // Run build verification if project scaffolding was present
    if (hasProject) {
      const buildResult = runBuildCheck(agentCwd);
      if (buildResult) {
        fs.writeFileSync(
          path.join(evalDir, "outputs", "build-result.json"),
          JSON.stringify(buildResult, null, 2),
          "utf-8"
        );
      }
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
      fs.writeFileSync(
        path.join(evalDir, "narration.md"),
        narrationMarkdown(err.stdout, evalDef.id),
        "utf-8"
      );
    }
    fs.writeFileSync(
      path.join(evalDir, "outputs", "response.md"),
      `ERROR: ${err.message}`,
      "utf-8"
    );
    fs.writeFileSync(
      path.join(evalDir, "timing.json"),
      JSON.stringify({ error: err.message, duration_ms: Date.now() - startedAt }, null, 2),
      "utf-8"
    );
  } finally {
    // Clean up per-eval working directory
    if (hasProject && fs.existsSync(agentCwd)) {
      fs.rmSync(agentCwd, { recursive: true });
    }
  }
}

function buildGradingPrompt(evalDef, response, generatedFiles, { noDeliverable = false } = {}) {
  const assertionsList = evalDef.assertions
    .map((a) => `- [${a.id}] ${a.text}`)
    .join("\n");

  let filesSection = "";
  if (generatedFiles && generatedFiles.length > 0) {
    const fileBlocks = generatedFiles.map(f =>
      `### ${f.path}\n\`\`\`\n${f.content}\n\`\`\``
    ).join("\n\n");
    filesSection = `\n\n## Generated Files\n\nThe agent wrote these files (grade against these, not the conversational response):\n\n${fileBlocks}`;
  }

  if (noDeliverable) {
    filesSection += `\n\n## IMPORTANT: No Test Code Was Delivered\n\nThe agent did not deliver any test source file. Assertions about the content, structure, or quality of test code must FAIL — a description, mockup, or plan in the response does not satisfy them. Only assertions explicitly about the conversational response itself may pass.`;
  }

  return `## Assertions

${assertionsList}

## Expected Output Description

${evalDef.expected_output}${filesSection}

## Model Response

${response}`;
}

function loadOutputFiles(evalDir) {
  const outputsDir = path.join(evalDir, "outputs");
  const files = [];
  if (!fs.existsSync(outputsDir)) return files;

  function walk(dir) {
    for (const entry of fs.readdirSync(dir)) {
      const full = path.join(dir, entry);
      if (fs.statSync(full).isDirectory()) {
        walk(full);
      } else if (/\.(java|kt|py|swift)$/.test(entry)
        || ["pom.xml", "build.gradle", "build.gradle.kts", "pyproject.toml", "pytest.ini", "conftest.py", "Package.swift"].includes(entry)) {
        files.push({ path: path.relative(outputsDir, full), content: fs.readFileSync(full, "utf-8") });
      }
    }
  }
  walk(outputsDir);
  return files;
}

function runDeterministicAssertions(assertions, fileContent, allFiles) {
  const results = [];
  for (const a of assertions) {
    const checker = checkers[a.id];
    if (!checker) {
      results.push({ id: a.id, text: a.text, passed: false, evidence: `No deterministic checker for "${a.id}"` });
      continue;
    }
    try {
      const result = checker({ fileContent, allFiles });
      results.push({ id: a.id, text: a.text, passed: result.passed, evidence: result.evidence });
    } catch (err) {
      results.push({ id: a.id, text: a.text, passed: false, evidence: `Checker error: ${err.message}` });
    }
  }
  return results;
}

function runBuildAssertions(assertions, evalDir) {
  const buildResultPath = path.join(evalDir, "outputs", "build-result.json");
  if (!fs.existsSync(buildResultPath)) {
    return assertions.map(a => ({
      id: a.id, text: a.text, passed: false, evidence: "No build-result.json found",
    }));
  }

  const buildResult = JSON.parse(fs.readFileSync(buildResultPath, "utf-8"));
  return assertions.map(a => {
    if (a.id === "compiles") {
      return { id: a.id, text: a.text, passed: !!buildResult.compiles, evidence: buildResult.compiles ? "Compilation succeeded" : `Compilation failed: ${(buildResult.compile_output || "").slice(0, 200)}` };
    }
    if (a.id === "tests-pass") {
      if (buildResult.tests_pass === null) {
        return { id: a.id, text: a.text, passed: true, evidence: "Test check skipped (spec-only eval)" };
      }
      return { id: a.id, text: a.text, passed: !!buildResult.tests_pass, evidence: buildResult.tests_pass ? "Tests passed" : `Tests failed: ${(buildResult.test_output || "").slice(0, 200)}` };
    }
    return { id: a.id, text: a.text, passed: false, evidence: `Unknown build assertion "${a.id}"` };
  });
}

function extractJson(text) {
  const start = text.indexOf("{");
  const end = text.lastIndexOf("}");
  if (start === -1 || end <= start) return null;
  return text.slice(start, end + 1);
}

function repairGradingJson(text) {
  const json = extractJson(text) || text;
  const assertions = [];
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

function parseLlmGrading(gradingText) {
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
      return attempt();
    } catch {
      // try next strategy
    }
  }
  return null;
}

async function gradeOne(evalDef, iterationDir, model, gradingSuffix = null, provider = "anthropic", gradeRuns = 1) {
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

  // Load generated test files for deterministic checking
  const allFiles = loadOutputFiles(evalDir);
  const fileContent = allFiles.length > 0
    ? allFiles.map(f => f.content).join('\n\n')
    : response;

  // Split assertions by type
  const deterministicAssertions = evalDef.assertions.filter(a => a.type === "deterministic");
  const buildAssertions = evalDef.assertions.filter(a => a.type === "build");
  const llmAssertions = evalDef.assertions.filter(a => !a.type || a.type === "llm");

  // Delivery gate: an eval that verifies the build expects actual test code.
  // Without a delivered test source (per the eval's language profile), format
  // and build assertions must not pass vacuously (e.g. "compiles" with
  // nothing to compile).
  const profile = languageProfile(evalDef);
  const expectsTestCode = evalDef.assertions.some(a => a.id === "compiles");
  const hasDeliverable = allFiles.some(f => profile.deliverablePath.test(f.path) && profile.deliverableContent.test(f.content));
  const gated = expectsTestCode && !hasDeliverable;

  const gate = (a) => ({ id: a.id, text: a.text, passed: false, evidence: `No ${profile.deliverableDescription} was delivered` });

  // Run deterministic assertions
  const deterministicResults = gated
    ? deterministicAssertions.map(gate)
    : runDeterministicAssertions(deterministicAssertions, fileContent, allFiles);

  // Run build assertions
  const buildResults = gated
    ? buildAssertions.map(gate)
    : runBuildAssertions(buildAssertions, evalDir);

  // Run LLM assertions, in batches. A grader asked to judge twenty criteria in one
  // response degrades on the later ones; batching keeps each judgement close to the
  // instructions. Grading is a rounding error against generation cost, so the extra
  // calls are free in practice.
  let llmResults = [];
  for (let i = 0; i < llmAssertions.length; i += LLM_GRADING_BATCH_SIZE) {
    const batch = llmAssertions.slice(i, i + LLM_GRADING_BATCH_SIZE);
    const llmEvalDef = { ...evalDef, assertions: batch };
    const gradingPrompt = buildGradingPrompt(llmEvalDef, response, allFiles, { noDeliverable: gated });

    const samples = [];
    for (let run = 0; run < gradeRuns; run++) {
      const gradingText = await gradeViaApi(GRADING_SYSTEM_PROMPT, gradingPrompt, model, provider);
      const llmGrading = parseLlmGrading(gradingText);
      if (!llmGrading) {
        throw new Error(
          `Failed to parse grading JSON for eval ${evalDef.id} (assertions ${i + 1}-${i + batch.length}, run ${run + 1}): ${gradingText.slice(0, 200)}`
        );
      }
      samples.push(llmGrading.assertions);
    }
    llmResults = llmResults.concat(majorityVote(samples, batch));
  }

  // Merge all results in original assertion order
  const allResults = [];
  const resultMap = {};
  for (const r of [...deterministicResults, ...buildResults, ...llmResults]) {
    resultMap[r.id] = r;
  }
  for (const a of evalDef.assertions) {
    allResults.push(resultMap[a.id] || { id: a.id, text: a.text, passed: false, evidence: "Not graded" });
  }

  const grading = { assertions: allResults };
  grading.assertions_passed = allResults.filter(a => a.passed).length;
  grading.assertions_total = allResults.length;
  grading.pass_rate = grading.assertions_total > 0
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

/**
 * Grading failed for at least one eval, so no benchmark may be written.
 *
 * An ungraded eval is not scored 0 — generation succeeded, so there is no `timing.error` and
 * `aggregateResults` gives it empty results. It then leaves the totals silently, and the run
 * reads as a smaller suite that happened to score differently. That is indistinguishable from
 * a real change, which is why this aborts the run instead of reporting.
 *
 * Carries no stack: an unreachable grading API is an expected outcome, not a crash.
 */
class GradingIncompleteError extends Error {
  constructor(message) {
    super(message);
    this.name = "GradingIncompleteError";
    this.stack = undefined;
  }
}

/** Rebuilds the invocation that re-grades only what failed, preserving the run's own flags. */
function regradeCommand(failedIds, args) {
  const flags = [
    `--skill ${args.skill}`,
    `--iteration ${args.iteration}`,
    args.variant ? `--variant ${args.variant}` : null,
    "--grade-only",
    `--evals ${failedIds.join(",")}`,
    args.gradeRuns > 1 ? `--grade-runs ${args.gradeRuns}` : null,
    args.gradingSuffix ? `--grading-suffix ${args.gradingSuffix}` : null,
    args.provider !== "anthropic" ? `--provider ${args.provider}` : null,
  ].filter(Boolean);
  return `node scripts/run-evals.js ${flags.join(" ")}`;
}

async function gradeResponses(evals, iterationDir, args) {
  log(`\nGrading responses (model=${args.gradingModel}, parallel=${args.parallel})...`);

  const failures = [];

  for (let i = 0; i < evals.length; i += args.parallel) {
    const batch = evals.slice(i, i + args.parallel);
    await Promise.all(
      batch.map((evalDef) =>
        gradeOne(evalDef, iterationDir, args.gradingModel, args.gradingSuffix, args.provider, args.gradeRuns).catch((err) => {
          logError(`  ✗ Eval ${evalDef.id} — GRADING FAILED: ${err.message}`);
          failures.push({ evalDef, message: err.message });
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

  // Every eval is attempted before aborting: generation is the expensive half and its output
  // is already on disk, so the gradings that did succeed are kept and only the rest re-run.
  if (failures.length > 0) {
    const failedIds = failures.map((f) => f.evalDef.id);
    const detail = failures
      .map((f) => `  eval-${f.evalDef.id} (${f.evalDef.slug}): ${f.message}`)
      .join("\n");
    throw new GradingIncompleteError(
      `grading failed for ${failures.length} of ${evals.length} evals — no benchmark or report written.\n\n` +
      `${detail}\n\n` +
      `A benchmark missing these evals would read as a score change rather than an error.\n` +
      `Successful gradings are saved; re-grade only the failures (no generation cost), then re-run:\n\n` +
      `  ${regradeCommand(failedIds, args)}\n`
    );
  }
}
// Unwrap results from old benchmark format (results.with_skill) or new flat format
function unwrapResults(evalEntry) {
  if (!evalEntry || !evalEntry.results) return null;
  if (evalEntry.results.assertions_passed !== undefined) return evalEntry.results;
  return evalEntry.results.with_skill || evalEntry.results.no_skill || evalEntry.results.old_skill || Object.values(evalEntry.results)[0] || null;
}

/**
 * Content hash of a directory tree: sorted relative paths and their bytes, so the
 * digest changes when any file's name or content does and not otherwise.
 */
function digestDirectory(dir) {
  const hash = crypto.createHash("sha256");
  const walk = (current, prefix) => {
    for (const entry of fs.readdirSync(current).sort()) {
      const full = path.join(current, entry);
      const rel = prefix ? `${prefix}/${entry}` : entry;
      if (fs.statSync(full).isDirectory()) {
        walk(full, rel);
      } else {
        hash.update(rel);
        hash.update("\0");
        hash.update(fs.readFileSync(full));
        hash.update("\0");
      }
    }
  };
  walk(dir, "");
  return hash.digest("hex").slice(0, 16);
}

/**
 * Identifies the skill state that produced a run's outputs, so a stored benchmark
 * still answers "which skill was this?" after the iteration dirs around it are trimmed.
 *
 * Digests the skill directory the agent was actually handed — the worktree copy, after
 * any variant has been applied — so official and variant runs are described the same
 * way, and the HEAD-versus-working-tree difference is captured rather than assumed.
 */
function skillProvenance(worktreePath, skillName, repoRoot) {
  const skillDir = path.join(worktreePath, "skills", skillName);
  let commit = "unknown";
  try {
    commit = execSync("git rev-parse HEAD", { cwd: repoRoot, stdio: ["ignore", "pipe", "ignore"] })
      .toString().trim();
  } catch {
    // Not a git checkout, or git unavailable: the digest still identifies the content.
  }
  return {
    skill_commit: commit,
    skill_digest: fs.existsSync(skillDir) ? digestDirectory(skillDir) : "absent",
  };
}

/**
 * Provenance belongs to the generation, not the grading. A re-grade builds a new
 * benchmark from outputs an earlier run produced, so it inherits that run's provenance
 * instead of stamping the skill as it stands now — which would claim today's skill
 * wrote yesterday's answers.
 */
function inheritedProvenance(iterationDir, suffix) {
  const candidates = [`benchmark${suffix}.json`, "benchmark.json"];
  for (const name of candidates) {
    const file = path.join(iterationDir, name);
    if (!fs.existsSync(file)) continue;
    try {
      const prior = JSON.parse(fs.readFileSync(file, "utf-8"));
      if (prior.skill_commit || prior.skill_digest) {
        return { skill_commit: prior.skill_commit, skill_digest: prior.skill_digest };
      }
    } catch {
      // Unreadable benchmark: fall through to the next candidate.
    }
  }
  return { skill_commit: "unknown", skill_digest: "unknown" };
}

function unwrapSummary(benchmark) {
  if (!benchmark || !benchmark.summary) return null;
  if (benchmark.summary.assertions_passed !== undefined) return benchmark.summary;
  return benchmark.summary.with_skill || benchmark.summary.no_skill || Object.values(benchmark.summary)[0] || null;
}

function aggregateResults(evals, iterationDir, args, worktreePath, repoRoot) {
  // A grading suffix isolates an entire re-grade: read grading-<suffix>.json
  // and write benchmark-<suffix>.json so the original results stay intact.
  const suffix = args.gradingSuffix ? `-${args.gradingSuffix}` : "";

  const benchmark = {
    skill_name: args.variant ? `${args.skill} (${args.variant})` : args.skill,
    iteration: args.iteration,
    model: args.model,
    provider: args.provider,
    ...(args.nudgeSkill ? { nudge_skill: true } : {}),
    grading_model: resolveModel(args.gradingModel),
    ...(args.gradeOnly
      ? inheritedProvenance(iterationDir, suffix)
      : skillProvenance(worktreePath, args.skill, repoRoot)),
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
      fingerprint: evalDef.fingerprint,
      results: {},
    };

    const gradingPath = path.join(evalDir, `grading${suffix}.json`);
    const timingPath = path.join(evalDir, "timing.json");

    if (fs.existsSync(gradingPath)) {
      const grading = JSON.parse(fs.readFileSync(gradingPath, "utf-8"));
      const timing = fs.existsSync(timingPath)
        ? JSON.parse(fs.readFileSync(timingPath, "utf-8"))
        : {};

      evalEntry.results = {
        model: timing.model || null,
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
    } else {
      // No grading exists. If generation errored (timeout, crash), score the
      // eval 0/N instead of dropping it — a timed-out eval must not improve
      // the aggregate pass rate. Token/cost usage is typically unrecorded.
      const timing = fs.existsSync(timingPath)
        ? JSON.parse(fs.readFileSync(timingPath, "utf-8"))
        : {};
      if (timing.error) {
        evalEntry.results = {
          model: timing.model || null,
          assertions_passed: 0,
          assertions_total: evalDef.assertions.length,
          pass_rate: 0,
          failed_assertions: evalDef.assertions.map((a) => a.id),
          total_tokens: timing.total_tokens || 0,
          duration_ms: timing.duration_ms || 0,
          cost_usd: timing.cost_usd || 0,
          error: timing.error,
        };
      }
    }

    regradedEntries.push(evalEntry);
  }

  // When re-grading a subset, merge with existing benchmark instead of replacing
  const benchPath = path.join(iterationDir, `benchmark${suffix}.json`);
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

  // Resolved model version(s) actually used, as reported by the CLI's init event
  const resolvedModels = [...new Set(results.map(r => r.model).filter(Boolean))];
  benchmark.model_resolved = resolvedModels.length === 1 ? resolvedModels[0] : resolvedModels;

  // Costs come from the Claude Code CLI's own estimate (list price); actual
  // billing may differ (e.g. promotional pricing).
  benchmark.cost_basis = "cli-list-price-estimate";

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

  fs.writeFileSync(benchPath, JSON.stringify(benchmark, null, 2), "utf-8");

  log("\nBenchmark saved:", benchPath);
  return benchmark;
}

function loadPreviousBenchmark(repoRoot, args) {
  const iterBase = args.variant
    ? variantIterationsDir(args.skill, args.variant)
    : iterationsDir(args.skill);
  const compareIter = args.compareIteration != null ? args.compareIteration : args.iteration - 1;
  const prevDir = path.join(repoRoot, iterBase, `iteration-${compareIter}`);
  const prevPath = path.join(prevDir, "benchmark.json");

  if (!fs.existsSync(prevPath)) {
    return null;
  }

  return JSON.parse(fs.readFileSync(prevPath, "utf-8"));
}

function loadOfficialBenchmark(repoRoot, skill) {
  // Merge evals from all official iterations, using the latest result for each eval.
  // This handles partial runs (e.g. iteration-28 with only 2 evals) by filling in
  // older results for evals not present in the latest iteration.
  const officialDir = path.join(repoRoot, iterationsDir(skill));
  if (!fs.existsSync(officialDir)) return null;

  const iterations = fs.readdirSync(officialDir)
    .filter(e => e.startsWith("iteration-") && fs.statSync(path.join(officialDir, e)).isDirectory())
    .sort((a, b) => {
      const numA = parseInt(a.split("-")[1], 10);
      const numB = parseInt(b.split("-")[1], 10);
      return numB - numA; // descending — latest first
    });

  const mergedEvals = {};
  let latestBenchmark = null;

  for (const iter of iterations) {
    const benchPath = path.join(officialDir, iter, "benchmark.json");
    if (!fs.existsSync(benchPath)) continue;

    const benchmark = JSON.parse(fs.readFileSync(benchPath, "utf-8"));
    if (!latestBenchmark) {
      latestBenchmark = benchmark;
    }

    for (const evalEntry of benchmark.evals) {
      const match = evalEntry.id.match(/eval-(\d+)/);
      if (match && !mergedEvals[match[1]]) {
        mergedEvals[match[1]] = { ...evalEntry, _fromIteration: iter };
      }
    }
  }

  if (!latestBenchmark) return null;

  const mergedBenchmark = { ...latestBenchmark };
  mergedBenchmark.evals = Object.values(mergedEvals).sort((a, b) => {
    const numA = parseInt(a.id.match(/eval-(\d+)/)[1], 10);
    const numB = parseInt(b.id.match(/eval-(\d+)/)[1], 10);
    return numA - numB;
  });
  const iterNums = iterations.filter(i => fs.existsSync(path.join(officialDir, i, "benchmark.json"))).map(i => i.split("-")[1]);
  mergedBenchmark._iterationName = `iterations ${iterNums.join(", ")} merged`;

  // Recompute summary from merged evals
  const results = mergedBenchmark.evals.map(e => unwrapResults(e)).filter(Boolean);
  mergedBenchmark.summary = {
    assertions_passed: results.reduce((s, r) => s + r.assertions_passed, 0),
    assertions_total: results.reduce((s, r) => s + r.assertions_total, 0),
    pass_rate: results.reduce((s, r) => s + r.assertions_passed, 0) / (results.reduce((s, r) => s + r.assertions_total, 0) || 1),
    total_tokens: results.reduce((s, r) => s + r.total_tokens, 0),
    total_duration_ms: results.reduce((s, r) => s + r.duration_ms, 0),
    total_cost_usd: results.reduce((s, r) => s + (r.cost_usd || 0), 0),
  };

  return mergedBenchmark;
}

function detectRegressions(benchmark, previousBenchmark) {
  if (!previousBenchmark) return { regressions: [], improvements: [], notComparable: [] };

  const regressions = [];
  const improvements = [];
  const notComparable = [];

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

    if (fingerprintsDiffer(evalEntry, prevEval)) {
      notComparable.push({ eval: evalEntry.id });
      continue;
    }

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

  return { regressions, improvements, notComparable };
}

/**
 * Every assertion whose verdict differs between a run and the baseline it is compared
 * against, in a single list. A moved verdict is the unit of analysis: it is what a reader
 * of the report wants explained, and the direction matters less than the fact it moved.
 */
/**
 * The baseline a run's verdicts should be attributed against: the official skill when the
 * run asked to be compared with it, otherwise the previous iteration. Named so the report
 * and the analysis to-do cannot silently disagree about what "moved" means.
 */
function analysisBaselineOf(args, previousBenchmark, officialBenchmark) {
  if (args.compareOfficial && officialBenchmark) {
    return { benchmark: officialBenchmark, label: `official iteration ${officialBenchmark.iteration}` };
  }
  if (previousBenchmark) {
    const compareIter = args.compareIteration != null ? args.compareIteration : args.iteration - 1;
    return { benchmark: previousBenchmark, label: `iteration ${compareIter}` };
  }
  return { benchmark: null, label: "no baseline" };
}

function movedAssertions(benchmark, baselineBenchmark) {
  const { regressions, improvements } = detectRegressions(benchmark, baselineBenchmark);
  return [
    ...regressions.map((r) => ({ ...r, direction: "lost" })),
    ...improvements.map((i) => ({ ...i, direction: "won" })),
  ].sort((a, b) => a.eval.localeCompare(b.eval) || a.assertion.localeCompare(b.assertion));
}

/**
 * The artefact-review form for a run: one entry per moved assertion, naming the files that
 * can explain it and leaving the cause blank. A score delta is not an attribution — the
 * grader's justification names the assertion, not reliably the cause — so this exists to
 * make the reading step visible work rather than remembered advice.
 *
 * `evidenceFor` looks up the grader's own words for an assertion; it is injected so this
 * stays a pure function of the two benchmarks.
 */
function analysisTodoMarkdown(moved, context, evidenceFor = () => null) {
  const { iteration, label, baselineLabel } = context;
  let md = `# Analysis to-do — ${label}, iteration ${iteration}\n\n`;
  md += `Compared against **${baselineLabel}**. `;
  if (moved.length === 0) {
    md += `No assertion verdicts moved, so there is nothing to attribute.\n`;
    return md;
  }
  md += `**${moved.length} assertion verdict${moved.length === 1 ? "" : "s"} moved.**\n\n`;
  md += `These are deltas, not attributions. Before explaining any of them, read the generated\n`;
  md += `output for that eval and its narration — a grader justification can name the right\n`;
  md += `assertion and still name the wrong cause, and graders do misfire outright. Fill in the\n`;
  md += `cause line from the artefact, not from the justification.\n\n`;
  md += `Do not start the next iteration until every line below has a cause.\n`;
  for (const m of moved) {
    md += `\n## ${m.direction === "lost" ? "LOST" : "WON"} \`${m.assertion}\` — ${m.eval}\n\n`;
    const evidence = evidenceFor(m);
    if (evidence) md += `Grader said: _${evidence.replace(/\s+/g, " ").trim()}_\n\n`;
    md += `- Output: \`${m.eval}/outputs/\`\n`;
    md += `- Narration: \`${m.eval}/narration.md\`\n`;
    md += `- Raw transcript: \`${m.eval}/conversation.jsonl\` (gitignored, trimmed each cycle — mine it now)\n`;
    md += `- Cause (from artefact): \n`;
  }
  return md;
}

/**
 * The agent's own account of a run — its visible narration and the order in which it wrote
 * files — distilled from the raw transcript. Worth keeping because the transcript is
 * gitignored and trimmed each cycle, while this is small, and because the write order shows
 * drafts and revisions the final output no longer contains.
 *
 * Thinking text is encrypted (signature only) for Claude 5-family models, so it appears
 * only for models that return it in the clear.
 */
function narrationMarkdown(conversationJsonl, evalId) {
  const steps = [];
  for (const line of String(conversationJsonl).split("\n")) {
    if (!line.trim()) continue;
    let event;
    try { event = JSON.parse(line); } catch { continue; }
    if (event?.message?.role !== "assistant") continue;
    const content = event.message.content;
    if (!Array.isArray(content)) continue;
    for (const block of content) {
      if (block.type === "text" && block.text?.trim()) {
        steps.push({ kind: "says", body: block.text.trim() });
      } else if (block.type === "thinking" && block.thinking?.trim()) {
        steps.push({ kind: "thinks", body: block.thinking.trim() });
      } else if (block.type === "tool_use" && (block.name === "Write" || block.name === "Edit")) {
        const file = block.input?.file_path || "?";
        const written = block.input?.content;
        const size = typeof written === "string" ? `${written.split("\n").length} lines` : "edit";
        steps.push({ kind: "writes", body: `${block.name} ${file} (${size})` });
      }
    }
  }

  let md = `# Narration — ${evalId}\n\n`;
  md += `The agent's visible narration and file writes, in order, distilled from\n`;
  md += `\`conversation.jsonl\`. Thinking text is absent unless the model returns it in the\n`;
  md += `clear (Claude 5-family models encrypt it).\n\n`;
  if (steps.length === 0) {
    md += `_No narration recorded._\n`;
    return md;
  }
  for (const step of steps) {
    if (step.kind === "writes") md += `**${step.body}**\n\n`;
    else if (step.kind === "thinks") md += `> (thinking) ${step.body.replace(/\n/g, "\n> ")}\n\n`;
    else md += `${step.body}\n\n`;
  }
  return md;
}

function graderEvidenceLookup(iterationDir, gradingSuffix) {
  const suffix = gradingSuffix ? `-${gradingSuffix}` : "";
  return ({ eval: evalId, assertion }) => {
    try {
      const grading = JSON.parse(
        fs.readFileSync(path.join(iterationDir, evalId, `grading${suffix}.json`), "utf-8")
      );
      const hit = (grading.results || grading.assertions || []).find(
        (r) => (r.id || r.assertion) === assertion
      );
      return hit?.evidence || hit?.reasoning || null;
    } catch {
      return null;
    }
  };
}

function writeAnalysisTodo(benchmark, baselineBenchmark, baselineLabel, iterationDir, args) {
  const moved = movedAssertions(benchmark, baselineBenchmark);
  const label = args.variant ? `${args.skill} variant=${args.variant}` : args.skill;
  const md = analysisTodoMarkdown(
    moved,
    { iteration: args.iteration, label, baselineLabel },
    graderEvidenceLookup(iterationDir, args.gradingSuffix)
  );
  const suffix = args.gradingSuffix ? `-${args.gradingSuffix}` : "";
  const todoPath = path.join(iterationDir, `analysis-todo${suffix}.md`);
  fs.writeFileSync(todoPath, md, "utf-8");
  log(`Analysis to-do saved: ${todoPath} (${moved.length} moved)`);
  return moved.length;
}

function generateReport(benchmark, previousBenchmark, officialBenchmark, iterationDir, args) {
  const { regressions, improvements, notComparable } = detectRegressions(
    benchmark,
    previousBenchmark
  );
  const label = args.variant ? `${args.skill} variant=${args.variant}` : args.skill;
  const modelResolved = [].concat(benchmark.model_resolved || []).join(", ");
  const modelLabel = modelResolved && modelResolved !== args.model
    ? `${args.model} (${modelResolved})`
    : args.model;
  let md = `# Eval Review — ${label}, Iteration ${args.iteration}\n\n`;
  md += `**Model:** ${modelLabel} · **Grading:** ${benchmark.grading_model || "?"} · **Date:** ${new Date().toISOString().split("T")[0]} · **Evals:** ${benchmark.evals.length}\n\n`;

  // Summary
  md += `## Summary\n\n`;
  const s = unwrapSummary(benchmark);
  if (s) {
    md += `${s.assertions_passed}/${s.assertions_total} (${(s.pass_rate * 100).toFixed(1)}%)`;
    md += ` · ${s.total_tokens} tokens · ${(s.total_duration_ms / 1000).toFixed(1)}s`;
    if (s.total_cost_usd > 0) md += ` · $${s.total_cost_usd.toFixed(4)}`;
    md += `\n\n`;
    if (s.total_cost_usd > 0) {
      md += `_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._\n\n`;
    }
  }

  // Attribution warning — the numbers below are deltas, not causes
  const analysisBaseline = analysisBaselineOf(args, previousBenchmark, officialBenchmark);
  if (analysisBaseline.benchmark) {
    const movedCount = movedAssertions(benchmark, analysisBaseline.benchmark).length;
    if (movedCount > 0) {
      md += `> ⚠️ **${movedCount} assertion verdict${movedCount === 1 ? "" : "s"} moved** vs ${analysisBaseline.label}. `;
      md += `These are deltas, not attributions: read each eval's \`outputs/\` and \`narration.md\` `;
      md += `before explaining any of them, and do not start the next iteration until every entry in `;
      md += `\`analysis-todo.md\` has a cause.\n\n`;
    }
  }

  // Regression summary
  const compareIter = args.compareIteration != null ? args.compareIteration : args.iteration - 1;
  if (previousBenchmark) {
    md += `## Delta vs Iteration ${compareIter}\n\n`;
    const prevModel = [].concat(previousBenchmark.model_resolved || []).join(", ");
    if (prevModel && modelResolved && prevModel !== modelResolved) {
      md += `⚠️ **Model changed since iteration ${compareIter}:** ${prevModel} → ${modelResolved} — deltas reflect model and skill changes combined.\n\n`;
    }
    if (regressions.length === 0 && improvements.length === 0 && notComparable.length === 0) {
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
      if (notComparable.length > 0) {
        md += `**Eval definition changed — not comparable (${notComparable.length}):**\n`;
        for (const nc of notComparable) {
          md += `- ⚠️ ${nc.eval}: fingerprint differs from iteration ${compareIter}; re-baseline to compare\n`;
        }
        md += `\n`;
      }
    }
  }

  // Resource comparison with previous iteration
  if (previousBenchmark) {
    md += `## Resource Comparison vs Iteration ${compareIter}\n\n`;
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

    // Build eval lookup by number (shared across sections)
    const officialByEvalNum = {};
    for (const e of officialBenchmark.evals) {
      const match = e.id.match(/eval-(\d+)/);
      if (match) officialByEvalNum[match[1]] = e;
    }

    // Per-eval resource comparison
    const fmtDelta = (curr, off) => {
      if (!off) return "—";
      const pct = ((curr - off) / off * 100).toFixed(0);
      return (pct >= 0 ? "+" : "") + pct + "%";
    };

    md += `### Per-Eval Resource Comparison\n\n`;
    md += `| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |\n`;
    md += `|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|\n`;

    const attentionItems = [];
    const changedEvals = [];
    let totOffTokens = 0, totVarTokens = 0, totOffCost = 0, totVarCost = 0, totOffTime = 0, totVarTime = 0;
    let totOffPass = 0, totOffTotal = 0, totVarPass = 0, totVarTotal = 0;
    let comparableCount = 0;

    for (const evalEntry of benchmark.evals) {
      const evalNum = String(evalEntry.id.match(/eval-(\d+)/)?.[1]);
      const officialEval = officialByEvalNum[evalNum];
      const curr = unwrapResults(evalEntry);
      const off = officialEval ? unwrapResults(officialEval) : null;
      if (!curr) continue;

      const evalChanged = fingerprintsDiffer(evalEntry, officialEval);
      if (evalChanged) changedEvals.push(evalEntry.id);

      const currPass = `${curr.assertions_passed}/${curr.assertions_total}`;
      const offPass = off ? `${off.assertions_passed}/${off.assertions_total}` : "—";
      const offTok = off ? String(off.total_tokens) : "—";
      const offCost = off ? `$${off.cost_usd.toFixed(4)}` : "—";
      const offTime = off ? `${(off.duration_ms / 1000).toFixed(1)}s` : "—";
      const tokDelta = off ? fmtDelta(curr.total_tokens, off.total_tokens) : "—";
      const costDelta = off ? fmtDelta(curr.cost_usd, off.cost_usd) : "—";
      const timeDelta = off ? fmtDelta(curr.duration_ms, off.duration_ms) : "—";

      md += `| ${evalEntry.id}${evalChanged ? " ⚠️" : ""} | ${offPass} | ${currPass} | ${offTok} | ${curr.total_tokens} | ${tokDelta} | ${offCost} | $${curr.cost_usd.toFixed(4)} | ${costDelta} | ${offTime} | ${(curr.duration_ms / 1000).toFixed(1)}s | ${timeDelta} |\n`;

      if (off && !evalChanged) {
        comparableCount++;
        totOffTokens += off.total_tokens; totVarTokens += curr.total_tokens;
        totOffCost += off.cost_usd; totVarCost += curr.cost_usd;
        totOffTime += off.duration_ms; totVarTime += curr.duration_ms;
        totOffPass += off.assertions_passed; totOffTotal += off.assertions_total;
        totVarPass += curr.assertions_passed; totVarTotal += curr.assertions_total;

        // Flag high resource usage
        if (curr.total_tokens > off.total_tokens * 2) {
          attentionItems.push({ eval: evalEntry.id, issue: "High tokens", details: `${curr.total_tokens} vs ${off.total_tokens} (${fmtDelta(curr.total_tokens, off.total_tokens)})` });
        }
        if (curr.cost_usd > off.cost_usd * 2) {
          attentionItems.push({ eval: evalEntry.id, issue: "High cost", details: `$${curr.cost_usd.toFixed(4)} vs $${off.cost_usd.toFixed(4)} (${fmtDelta(curr.cost_usd, off.cost_usd)})` });
        }
      }
    }

    if (comparableCount > 0) {
      md += `| **Totals (${comparableCount} comparable)** | **${totOffPass}/${totOffTotal}** | **${totVarPass}/${totVarTotal}** | **${totOffTokens}** | **${totVarTokens}** | **${fmtDelta(totVarTokens, totOffTokens)}** | **$${totOffCost.toFixed(4)}** | **$${totVarCost.toFixed(4)}** | **${fmtDelta(totVarCost, totOffCost)}** | **${(totOffTime / 1000).toFixed(1)}s** | **${(totVarTime / 1000).toFixed(1)}s** | **${fmtDelta(totVarTime, totOffTime)}** |\n`;
    }
    md += `\n`;

    if (changedEvals.length > 0) {
      md += `⚠️ Eval definition changed since the official baseline — excluded from totals and per-assertion comparison; re-baseline to compare: ${changedEvals.join(", ")}\n\n`;
    }

    // Summary comparison (uses comparable totals from resource table above)
    if (comparableCount > 0) {
      const varOffRate = (totOffPass / totOffTotal * 100).toFixed(1);
      const varVarRate = (totVarPass / totVarTotal * 100).toFixed(1);
      md += `**Comparable summary (${comparableCount} evals in both):**\n\n`;
      md += `| Source | Pass Rate | Tokens | Cost | Time |\n`;
      md += `|--------|-----------|--------|------|------|\n`;
      md += `| ${args.variant} (iter ${args.iteration}) | ${totVarPass}/${totVarTotal} (${varVarRate}%) | ${totVarTokens} | $${totVarCost.toFixed(4)} | ${(totVarTime / 1000).toFixed(1)}s |\n`;
      md += `| official | ${totOffPass}/${totOffTotal} (${varOffRate}%) | ${totOffTokens} | $${totOffCost.toFixed(4)} | ${(totOffTime / 1000).toFixed(1)}s |\n`;
      md += `| **Δ** | | **${fmtDelta(totVarTokens, totOffTokens)}** | **${fmtDelta(totVarCost, totOffCost)}** | **${fmtDelta(totVarTime, totOffTime)}** |\n`;
      md += `\n`;
    }

    // Per-assertion diff
    const loadBearing = [];
    md += `### Per-Assertion Comparison\n\n`;
    md += `| Eval | Assertion | official | ${args.variant} |\n`;
    md += `|------|-----------|----------|${"-".repeat(args.variant.length + 2)}|\n`;

    for (const evalEntry of benchmark.evals) {
      const evalNum = String(evalEntry.id.match(/eval-(\d+)/)?.[1]);
      const officialEval = officialByEvalNum[evalNum];
      if (!officialEval) continue;
      if (fingerprintsDiffer(evalEntry, officialEval)) continue;

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
          md += `| ${evalEntry.id} | ${aid} | ${offPass ? "✅" : "❌"} | ${currPass ? "✅" : "❌"} |\n`;
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

      // Add load-bearing assertions to attention items
      const lbByEval = {};
      for (const lb of loadBearing) {
        if (!lbByEval[lb.eval]) lbByEval[lb.eval] = [];
        lbByEval[lb.eval].push(lb.assertion);
      }
      for (const [evalId, assertions] of Object.entries(lbByEval)) {
        attentionItems.push({ eval: evalId, issue: "Failed assertions", details: assertions.map(a => `\`${a}\``).join(", ") });
      }
    } else {
      md += `No load-bearing assertions found — variant matches official on all comparable assertions.\n\n`;
    }

    // Attention needed summary
    if (attentionItems.length > 0) {
      md += `### Attention Needed\n\n`;
      md += `Evals with failed assertions or disproportionate resource usage (>2x official):\n\n`;
      md += `| Eval | Issue | Details |\n`;
      md += `|------|-------|---------|\n`;
      for (const item of attentionItems) {
        md += `| ${item.eval} | ${item.issue} | ${item.details} |\n`;
      }
      md += `\n`;
    }
  }

  const reportSuffix = args.gradingSuffix ? `-${args.gradingSuffix}` : "";
  const reportPath = path.join(iterationDir, `eval-review${reportSuffix}.md`);
  fs.writeFileSync(reportPath, md, "utf-8");
  log("Report saved:", reportPath);
}

if (require.main === module) {
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
}

// Exported for scripts/run-evals.test.js. Everything here is either pure or takes its
// collaborators as arguments, so the suite needs no network and no eval fixtures.
module.exports = {
  computeEvalFingerprint,
  fingerprintsDiffer,
  loadEvalsFromDir,
  detectRegressions,
  generateReport,
  // analysis protocol
  analysisBaselineOf,
  movedAssertions,
  analysisTodoMarkdown,
  narrationMarkdown,
  // grading pipeline
  parseLlmGrading,
  repairGradingJson,
  extractJson,
  majorityVote,
  gradeResponses,
  regradeCommand,
  GradingIncompleteError,
  // transport
  postGradingRequest,
  isRetryableStatus,
  retryDelayMs,
  // benchmark shaping
  unwrapResults,
  unwrapSummary,
  // provenance
  digestDirectory,
  skillProvenance,
  inheritedProvenance,
  // cli
  parseEvalIds,
  resolveModel,
  acceptsTemperature,
  extractGradingText,
};
