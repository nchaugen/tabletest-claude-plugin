#!/usr/bin/env node

const { execSync, exec, spawn } = require("child_process");
const { promisify } = require("util");
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

const execAsync = promisify(exec);

/**
 * Runs one build command and reports the outcome, without blocking the parent's event loop.
 *
 * `execSync` here froze the whole runner: build verification is called from `generateOne`, which
 * runs inside the generation phase's `Promise.all`, so one eval's JVM build stopped every other
 * eval's generation timer from firing. `iteration-57`'s two timeouts reported 1194435ms and
 * 1194427ms against a 900000ms ceiling — a stopped clock, 294s late, ~5 wasted minutes per stuck
 * eval. Everything here awaits instead, so a build only occupies its own eval.
 */
async function runBuildCommand(command, worktreePath, timeoutMs) {
  try {
    await execAsync(command, { cwd: worktreePath, timeout: timeoutMs });
    return { ok: true, output: "" };
  } catch (err) {
    return { ok: false, output: (err.stderr || err.stdout || "").toString() };
  }
}

async function runBuildCheck(worktreePath) {
  const hasPom = fs.existsSync(path.join(worktreePath, "pom.xml"));
  const hasGradle = fs.existsSync(path.join(worktreePath, "build.gradle"))
    || fs.existsSync(path.join(worktreePath, "build.gradle.kts"));

  if (hasPom || hasGradle) return runJvmBuildCheck(worktreePath, hasPom);
  if (fs.existsSync(path.join(worktreePath, "Package.swift"))) return runSwiftBuildCheck(worktreePath);
  if (fs.existsSync(path.join(worktreePath, "pyproject.toml"))
    || fs.existsSync(path.join(worktreePath, "pytest.ini"))) return runPytestBuildCheck(worktreePath);

  return null;
}

async function runSwiftBuildCheck(worktreePath) {
  const result = { compiles: false, tests_pass: null, compile_output: "", test_output: "" };

  const compile = await runBuildCommand("swift build --build-tests", worktreePath, 300000);
  if (!compile.ok) {
    result.compile_output = compile.output.slice(0, 2000);
    return result;
  }
  result.compiles = true;

  const tests = await runBuildCommand("swift test", worktreePath, 300000);
  result.tests_pass = tests.ok;
  if (!tests.ok) result.test_output = tests.output.slice(0, 2000);

  return result;
}

async function runPytestBuildCheck(worktreePath) {
  const result = { compiles: false, tests_pass: null, compile_output: "", test_output: "" };

  // Python has no compile step; "compiles" means pytest can import and
  // collect the test modules (exit 5 = nothing collected, also a failure).
  const collect = await runBuildCommand("pytest --collect-only -q", worktreePath, 120000);
  if (!collect.ok) {
    result.compile_output = collect.output.slice(0, 2000);
    return result;
  }
  result.compiles = true;

  const tests = await runBuildCommand("pytest -q", worktreePath, 120000);
  result.tests_pass = tests.ok;
  if (!tests.ok) result.test_output = tests.output.slice(0, 2000);

  return result;
}

async function runJvmBuildCheck(worktreePath, hasPom) {
  const result = { compiles: false, tests_pass: null, compile_output: "", test_output: "" };

  // Detect Kotlin test files to choose the right compile task
  const hasKotlinTests = findFiles(
    path.join(worktreePath, "src", "test"),
    /\.kt$/
  ).length > 0;

  const compileTask = hasKotlinTests ? "compileTestKotlin" : "compileTestJava";
  const compileCmd = hasPom ? "mvn compile test-compile -q" : `gradle ${compileTask} -q`;
  const compile = await runBuildCommand(compileCmd, worktreePath, 120000);
  if (!compile.ok) {
    result.compile_output = compile.output.slice(0, 2000);
    return result;
  }
  result.compiles = true;

  // Infrastructure flakes (daemon crashes, lock timeouts) are not failing
  // tests — retry once before recording a failure.
  const infraFailure = /Test process encountered an unexpected problem|Gradle build daemon disappeared|Could not connect to the Gradle daemon|Timeout waiting to lock|Could not create service/i;
  const testCmd = hasPom ? "mvn test -q" : "gradle test -q";
  for (let attempt = 1; attempt <= 2; attempt++) {
    const tests = await runBuildCommand(testCmd, worktreePath, 120000);
    if (tests.ok) {
      result.tests_pass = true;
      result.test_output = "";
      break;
    }
    result.tests_pass = false;
    result.test_output = tests.output.slice(0, 2000);
    if (attempt === 1 && infraFailure.test(tests.output)) continue;
    break;
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

/** Effort levels `output_config.effort` accepts, cheapest first. */
const GRADING_EFFORT_LEVELS = ["low", "medium", "high", "xhigh", "max"];

/**
 * Whether a model accepts `output_config.effort`.
 *
 * The same newer families that dropped sampling parameters are the ones that gained effort, so
 * one predicate covers both. haiku-4-5 takes `temperature` and rejects `effort`; sonnet-5 is the
 * reverse. Grading omits `output_config` entirely unless `--grading-effort` is passed, so the
 * default regime stays byte-identical to every run recorded before this flag existed — the API
 * default is `high`, which is what every measurement to date was taken at.
 */
function acceptsEffort(resolvedModel) {
  return !acceptsTemperature(resolvedModel);
}

/**
 * The grading effort for one eval: its own `grading_effort`, else the run-wide `--grading-effort`,
 * else null (send nothing — the API default, `high`).
 *
 * Per-eval effort is deliberately a property of the eval *definition*, not a runtime choice, and it
 * feeds the fingerprint like `timeout_ms` does: an eval always grades at the same effort, so
 * comparing an eval to itself across runs stays valid even when the suite mixes levels. Choosing
 * levels ad hoc per run would silently make two runs incomparable.
 *
 * Effort is per-request and assertions are batched, so this is the finest granularity available
 * without regrouping assertions across calls — and regrouping is exactly what LLM_GRADING_BATCH_SIZE
 * is pinned to prevent, batch composition having already been measured to move verdicts.
 */
function gradingEffortFor(evalDef, args) {
  const effort = (evalDef && evalDef.grading_effort) || args.gradingEffort || null;
  if (effort && !GRADING_EFFORT_LEVELS.includes(effort)) {
    throw new Error(
      `eval ${evalDef && evalDef.id}: grading_effort "${effort}" is not one of ${GRADING_EFFORT_LEVELS.join(", ")}`
    );
  }
  return effort;
}

// The same newer families also run adaptive thinking by DEFAULT, so the response opens with a
// thinking block and the verdict JSON is no longer content[0]. Thinking is billed against
// max_tokens too, so a budget sized for a pure JSON answer truncates the verdicts instead.
// Grading keeps thinking on — a judgement is exactly the work it helps — and pays for the room.
// 16000 was not enough: on eval-23 the grader spent the entire budget thinking and returned a
// thinking block with no verdict at all (`stop_reason: max_tokens`). The budget has to cover the
// thinking AND the JSON, so it is sized for the worst batch rather than the average one.
// The cheaper lever would be `output_config.effort`, but grader accuracy was measured at the
// default effort — changing it invalidates that measurement, so buy room instead and re-measure
// accuracy first if effort ever needs lowering.
const GRADING_MAX_TOKENS_WITH_THINKING = 32000;
const GRADING_MAX_TOKENS = 4096;

/** Assertion ids the grader was asked about but did not return a verdict for. */
function missingAssertionIds(batch, returned) {
  const seen = new Set((returned || []).map((a) => a && a.id));
  return batch.map((a) => a.id).filter((id) => !seen.has(id));
}

/** The grader's verdict text, wherever the model put it among thinking/text blocks. */
function extractGradingText(data) {
  if (!data || !Array.isArray(data.content)) {
    throw new Error(`Grading response had no content array: ${JSON.stringify(data).slice(0, 300)}`);
  }
  const text = data.content.find((block) => block.type === "text" && typeof block.text === "string");
  if (!text) {
    // Name the cause, not just the symptom: `stop_reason: max_tokens` with only a thinking block
    // means the budget was spent reasoning and the verdict never got written — raise
    // GRADING_MAX_TOKENS_WITH_THINKING rather than hunting for a parsing bug.
    const kinds = data.content.map((b) => b.type).join(", ") || "none";
    const cause = data.stop_reason === "max_tokens"
      ? " — stop_reason=max_tokens, so the token budget was exhausted before the verdict was written"
      : ` — stop_reason=${data.stop_reason}`;
    throw new Error(`Grading response carried no text block (blocks: ${kinds})${cause}`);
  }
  return text.text;
}

/**
 * The grader's own reasoning for this call, or null when the response carried none.
 *
 * Sonnet-5 grades with a thinking budget (`GRADING_MAX_TOKENS_WITH_THINKING`), so these blocks are
 * produced and billed on every call — they were simply dropped, because `extractGradingText` takes
 * the first text block and discards the rest. Keeping them costs nothing at request time and is
 * what makes an unstable slot diagnosable: with two passes over identical bytes stored, a flip can
 * be read as "the grader looked at a different part of the output" or "it applied a different
 * clause of the same assertion", which need opposite fixes.
 *
 * **This reads the response only.** The request is untouched, so no verdict, comparison or baseline
 * is affected by capturing it — unusually for anything in the grading path.
 */
function extractGradingThinking(data) {
  if (!data || !Array.isArray(data.content)) return null;
  const blocks = data.content
    .filter((block) => block && block.type === "thinking" && typeof block.thinking === "string")
    .map((block) => block.thinking);
  return blocks.length > 0 ? blocks.join("\n\n") : null;
}

async function gradeViaApi(systemPrompt, userPrompt, model, provider = "anthropic", effort = null, captureThinking = false) {
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
      ...(effort && acceptsEffort(resolved) ? { output_config: { effort } } : {}),
      // Ask for the grader's reasoning back as a readable summary — OPT-IN via --capture-thinking.
      //
      // **Off by default because it is not free.** The summary text is additional billed output: on
      // the suite's largest LLM-assertion eval it took grading output from 15,783 tokens to 28,553.
      // Grading posts a NON-STREAMING request at max_tokens 32000, and Anthropic's guidance is that
      // requests above ~16K output must stream or the connection drops — so the extra volume pushed
      // eval-18 past the line and it began failing reproducibly with `fetch failed` after a ~10-minute
      // hang, while every smaller eval succeeded. Turning it on globally would therefore have made the
      // biggest eval flaky and changed the default regime under every future comparison.
      //
      // Keep it for diagnosis (a variance probe, an unstable slot), where reading *why* the grader
      // flipped is worth the cost and the run is deliberate. The real fix for the transport is to
      // stream grading requests, which is a void-comparison change and belongs in its own window.
      //
      // Original note follows.
      //
      // On this model family adaptive thinking is already on when `thinking` is omitted, and
      // `display` defaults to `"omitted"` — so thinking blocks were arriving with an EMPTY text
      // field, which is why nothing was captured before. The reasoning was being generated and
      // billed either way; the default just refused to show it.
      //
      // **This changes the request, not only the response**, so it is not free of instrument risk
      // the way a pure response-side change would be. It is low risk — `display` is documented as
      // controlling visibility only, with thinking happening and billing the same under every
      // setting — but "low" is not "none", so it was validated by re-grading stored outputs and
      // comparing verdicts before being kept.
      //
      // Gated on the same predicate as `effort`: the older family (haiku-4-5) takes
      // `{type: "enabled", budget_tokens: N}` and rejects `adaptive`.
      ...(captureThinking && acceptsEffort(resolved)
        ? { thinking: { type: "adaptive", display: "summarized" } }
        : {}),
      system: systemPrompt,
      messages: [{ role: "user", content: userPrompt }],
    }),
  }, "Anthropic");
  const data = await resp.json();
  return {
    text: extractGradingText(data),
    thinking: extractGradingThinking(data),
    usage: data.usage || {},
  };
}

// List prices per million tokens for the models this project grades with. Grading cost was
// folklore ("~$0.65 a run") until 2026-07-25 because the runner recorded usage for generation
// only — the grading API returns token counts and no cost, so nothing added them up.
//
// Rates are stamped into every benchmark alongside the totals (`grading_rates_usd_per_mtok`),
// so a stale entry here shows up in the artefact instead of silently skewing a figure. Cache
// reads and writes are not priced: grading sends a fresh prompt per batch and neither field has
// ever come back non-zero.
// Keyed by prefix, not exact id: resolveModel returns bare aliases ("claude-haiku-4-5") for some
// families and dated ids for others, and an exact-match table silently priced a real run at $0.
const GRADING_PRICES_USD_PER_MTOK = {
  "claude-sonnet-5": { input: 3, output: 15 },
  "claude-haiku-4-5": { input: 1, output: 5 },
};

/** Sums grading token usage and prices it, returning zeros for a model with no known rate. */
function priceGradingUsage(usage, model) {
  const key = Object.keys(GRADING_PRICES_USD_PER_MTOK)
    .filter((k) => (model || "").startsWith(k))
    .sort((a, b) => b.length - a.length)[0];
  const rate = key ? GRADING_PRICES_USD_PER_MTOK[key] : undefined;
  const input = usage.input_tokens || 0;
  const output = usage.output_tokens || 0;
  return {
    input_tokens: input,
    output_tokens: output,
    total_tokens: input + output,
    calls: usage.calls || 0,
    cost_usd: rate ? (input * rate.input + output * rate.output) / 1e6 : 0,
    rates_usd_per_mtok: rate || null,
  };
}

/** Accumulates one grading response's usage into a running per-eval total. */
function addUsage(total, usage) {
  return {
    input_tokens: (total.input_tokens || 0) + (usage.input_tokens || 0),
    output_tokens: (total.output_tokens || 0) + (usage.output_tokens || 0),
    calls: (total.calls || 0) + 1,
  };
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
// `text` always comes from the eval definition, never from the grader's response: the grader is no
// longer asked to echo it, and even when it volunteers one the definition is the authoritative copy.
// This also removes an old inconsistency — the single-sample path used to pass the grader's echo
// through verbatim while the voting path already rebuilt it from `batch`.
function majorityVote(samples, batch) {
  if (samples.length === 1) {
    return batch.map(a => {
      const r = samples[0].find(x => x.id === a.id) || {};
      return { id: a.id, text: a.text, passed: !!r.passed, evidence: r.evidence || "" };
    });
  }
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

/**
 * Keychain service holding the grading key, read when `ANTHROPIC_API_KEY` is unset.
 *
 * Grading posts to the API and is billed out of pocket; generation runs on the subscription and
 * strips the variable deliberately (see `generationEnv`). Keeping the key out of the environment
 * costs nothing and removes the only way it has ever escaped.
 */
const GRADING_KEY_SERVICE = "tabletest-eval-grading";

function readGradingKeyFromKeychain() {
  if (process.platform !== "darwin") return null;
  try {
    return execSync(`security find-generic-password -s ${GRADING_KEY_SERVICE} -w`, {
      stdio: ["ignore", "pipe", "ignore"],
    }).toString().trim() || null;
  } catch {
    return null; // not stored — the caller reports how to store it
  }
}

function parseEvalIds(str) {
  const ids = [];
  for (const part of str.split(",")) {
    const trimmed = part.trim();
    const range = trimmed.match(/^(\d+)-(\d+)$/);
    if (range) {
      const start = parseInt(range[1], 10);
      const end = parseInt(range[2], 10);
      for (let i = start; i <= end; i++) ids.push(i);
    } else if (/^\d+$/.test(trimmed)) {
      ids.push(Number(trimmed));
    } else {
      // A directory name is the tempting wrong answer: eval.json carries `"id": 15` while the
      // directory is eval-15-reis-discount. Without this it parsed to NaN, matched nothing, and
      // the run wrote an empty iteration and a ledger row (2026-08-02).
      throw new Error(
        `--evals takes eval numbers, not names: "${trimmed}". Use --evals 15, or 15,20,29, or a range 14-16.`
      );
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
    gradingEffort: null,
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
    // Generation authenticates with the subscription unless asked otherwise; see generationEnv.
    apiGeneration: false,
    rebuild: false,
    captureThinking: false,
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
      case "--capture-thinking":
        args.captureThinking = true;
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
      // Run-wide grading effort. Omitted by default so the request stays byte-identical to every
      // measurement taken before this flag existed (the API default is `high`). An eval's own
      // `grading_effort` overrides this.
      case "--grading-effort": {
        const level = argv[++i];
        if (!GRADING_EFFORT_LEVELS.includes(level)) {
          console.error(`Error: --grading-effort must be one of ${GRADING_EFFORT_LEVELS.join(", ")}`);
          process.exit(1);
        }
        args.gradingEffort = level;
        break;
      }
      case "--grade-only":
        args.gradeOnly = true;
        break;
      case "--api-generation":
        args.apiGeneration = true;
        break;
      // Rebuild the benchmark from gradings already on disk, calling no API at all.
      // A fatal grading failure deliberately writes no benchmark, so the gradings that DID
      // succeed are stranded: the only way to get a whole-suite benchmark was to re-grade
      // all 17 evals, which costs a full pass (20-90 min) to recover work already done.
      // aggregateResults already reads grading{suffix}.json off disk — this just lets you
      // reach it without paying for gradeResponses again.
      case "--rebuild":
        args.rebuild = true;
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
    console.error("  --timeout SECONDS   Override each eval's generation timeout, ceiling included (slow local LLMs)");
    console.error("  --grade-runs N      Grade each assertion N times and take the majority verdict (default 1)");
    console.error("  --grade-only        Re-grade existing outputs");
    console.error("  --api-generation    Bill generation to ANTHROPIC_API_KEY instead of the subscription");
    console.error("  --report-only       Regenerate report from existing benchmark.json");
    console.error("");
    console.error("Exit codes: 0 ok · 1 the run failed · 2 the run succeeded but its comparison was");
    console.error("void (no eval comparable against the baseline) — results are saved, do not re-run");
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
    const regeneratedComparison = writeAnalysisTodo(benchmark, regeneratedBaseline, iterationDir, args);
    log("\nReport regenerated. Results in:", iterationDir);
    const regeneratedVoid = reportVoidComparison(regeneratedComparison, regeneratedBaseline, iterationDir);
    logFile = null;
    if (regeneratedVoid) process.exit(2);
    return;
  }

  // --rebuild reads gradings off disk and calls nothing, so it must not require a key.
  // Grading posts to the API directly, so it needs the key even though generation no longer does.
  // Resolve the grading key without it having to live in the shell environment. An exported key
  // leaks into every child process and into any transcript that echoes the environment — which is
  // how one was disclosed on 2026-08-02. The keychain entry is read only when the variable is unset,
  // so an explicit `ANTHROPIC_API_KEY=… node scripts/run-evals.js` still wins.
  if (!process.env.ANTHROPIC_API_KEY) {
    const fromKeychain = readGradingKeyFromKeychain();
    if (fromKeychain) process.env.ANTHROPIC_API_KEY = fromKeychain;
  }

  if (args.provider === "anthropic" && !args.rebuild && !process.env.ANTHROPIC_API_KEY) {
    console.error("Error: ANTHROPIC_API_KEY environment variable is not set (grading needs it).");
    console.error("The eval script requires an API key to avoid consuming interactive plan usage.");
    console.error("Store it once, in the login keychain, so it never sits in the environment:");
    console.error(`  security add-generic-password -a "$USER" -s ${GRADING_KEY_SERVICE} -w`);
    console.error("(paste the key at the prompt; it is not echoed and does not reach shell history)");
    console.error("Or, for one run: ANTHROPIC_API_KEY=sk-... node scripts/run-evals.js ...");
    process.exit(1);
  }

  let evals = loadEvalsFromDir(path.join(repoRoot, evalsDir(args.skill)));
  if (args.evals) {
    const known = evals.map((e) => e.id);
    const missing = args.evals.filter((id) => !known.includes(id));
    if (missing.length > 0) {
      console.error(`Error: no eval ${missing.join(", ")} in ${args.skill}. Available: ${known.join(", ")}`);
      process.exit(1);
    }
    evals = evals.filter((e) => args.evals.includes(e.id));
  }
  if (evals.length === 0) {
    console.error(`Error: no evals selected for ${args.skill} — nothing to run.`);
    process.exit(1);
  }

  const label = args.variant ? `variant=${args.variant}` : "official";
  log(
    `\nEval run: ${args.skill} (${label}), iteration ${args.iteration}, ${evals.length} evals, model ${args.model}, provider ${args.provider}${args.nudgeSkill ? ", nudge-skill" : ""}`
  );

  // Validate variant directory exists. A --grade-only run never hands a skill to an agent
  // (no worktree is set up below), so it has no skill directory to validate — requiring one
  // would block grading stored outputs under a variant label that holds results, not a skill.
  if (args.variant && !args.noSkill && !args.gradeOnly) {
    const variantDir = path.join(repoRoot, variantSkillDir(args.skill, args.variant));
    if (!fs.existsSync(path.join(variantDir, "SKILL.md"))) {
      console.error(`Error: Variant skill file not found: ${path.join(variantDir, "SKILL.md")}`);
      process.exit(1);
    }
  }

  const worktreeMode = args.variant || "skill";
  const worktreePath = args.gradeOnly ? null : setupWorktree(repoRoot, worktreeMode, args);
  let comparisonWasVoid = false;

  try {
    if (!args.gradeOnly) {
      await generateResponses(evals, worktreePath, iterationDir, args, repoRoot);
    }
    if (args.rebuild) {
      const suffix = args.gradingSuffix ? `-${args.gradingSuffix}` : "";
      const missing = evalsMissingGrading(evals, iterationDir, suffix);
      if (missing.length > 0) {
        throw new Error(
          `--rebuild found no grading${suffix}.json for ${missing.length} eval(s): ${missing.join(", ")}. ` +
          `Grade them first (--grade-only --evals ${missing.join(",")}${args.gradingSuffix ? ` --grading-suffix ${args.gradingSuffix}` : ""}), then rebuild.`
        );
      }
      log(`Rebuilding benchmark from ${evals.length} stored grading${suffix}.json file(s) — no API calls.`);
    } else {
      await gradeResponses(evals, iterationDir, args);
    }
    const benchmark = aggregateResults(evals, iterationDir, args, worktreePath, repoRoot);
    const previousBenchmark = loadPreviousBenchmark(repoRoot, args);
    const officialBenchmark = args.compareOfficial ? loadOfficialBenchmark(repoRoot, args.skill) : null;
    generateReport(benchmark, previousBenchmark, officialBenchmark, iterationDir, args);
    const analysisBaseline = analysisBaselineOf(args, previousBenchmark, officialBenchmark);
    const comparison = writeAnalysisTodo(benchmark, analysisBaseline, iterationDir, args);
    // Only a run that measured something gets a row. --report-only re-reads a benchmark already
    // recorded, and appending again would double-count a measurement that happened once.
    appendToLedger(
      benchmark,
      comparison,
      {
        runLabel: ledgerRunLabel(args),
        baselineLabel: analysisBaseline.benchmark ? analysisBaseline.label : null,
        isRegrade: Boolean(args.gradeOnly || args.rebuild),
        isRebuild: Boolean(args.rebuild),
      },
      repoRoot
    );
    log("\nDone. Results in:", iterationDir);
    // Reported after the results are saved and before the worktree is torn down, so the last
    // thing on screen is the reason the comparison says nothing rather than the success line.
    comparisonWasVoid = reportVoidComparison(comparison, analysisBaseline, iterationDir);
  } finally {
    if (worktreePath) {
      cleanupWorktree(worktreePath);
    }
    logFile = null;
  }
  if (comparisonWasVoid) process.exit(2);
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

  // Last, so every git-dependent step above has already run.
  severWorktreeFromRepo(worktreePath);
  removals.push("the .git link (agent memory follows project identity, which follows git)");

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
    // The .git link is removed for isolation, so `git worktree remove` no longer recognises the
    // directory; delete it and let prune clear the administrative files.
    fs.rmSync(worktreePath, { recursive: true, force: true });
    execSync("git worktree prune", { cwd: repoRoot, stdio: "pipe" });
    // Clean up the temporary branch
    try { execSync(`git branch -D "${branch}"`, { cwd: repoRoot, stdio: "pipe" }); } catch {}
  } catch {
    // Fallback: just remove the directory
    try { fs.rmSync(worktreePath, { recursive: true, force: true }); } catch {}
    logError("Warning: could not cleanly remove worktree at", worktreePath);
  }
}

/**
 * Environment for the eval agent.
 *
 * Generation runs on the Claude subscription by default: the CLI authenticates with
 * the logged-in account when no API key is in the environment, and generation is ~90%
 * of a run's cost. Nothing else about the run changes — same CLI, same model id, same
 * prompts — so a comparison across this switch stays valid. `--api-generation` keeps
 * the key, for a machine with no subscription login.
 *
 * Grading is unaffected: it posts to the API directly and still needs the key.
 */
function generationEnv(baseEnv, { apiGeneration = false, provider = "anthropic" } = {}) {
  const env = { ...baseEnv };
  if (!apiGeneration) delete env.ANTHROPIC_API_KEY;
  if (provider === "ollama") {
    env.ANTHROPIC_BASE_URL = "http://localhost:11434";
    env.ANTHROPIC_AUTH_TOKEN = "ollama";
  }
  return env;
}

/**
 * Severs the eval workspace from this repository, so the agent cannot inherit its identity.
 *
 * `git worktree add` leaves a `.git` file pointing back here, and the CLI resolves a session's
 * project through git — which is why the agent's auto-memory was this project's, holding
 * development notes about the evals, one of them contradicting a graded rule. eval-24 read from it
 * mid-run on 2026-08-01. Without the link the project resolves to the throwaway workspace path,
 * whose memory directory does not exist.
 *
 * Relocating `CLAUDE_CONFIG_DIR` was tried first and reverted: OAuth is bound to the config
 * directory, so a relocated run is "Not logged in" unless it carries an `ANTHROPIC_API_KEY`, which
 * subscription generation deliberately strips.
 */
function severWorktreeFromRepo(worktreePath) {
  const gitLink = path.join(worktreePath, ".git");
  if (fs.existsSync(gitLink)) fs.rmSync(gitLink, { recursive: true, force: true });
}

/**
 * Answer keys the agent could have reached, found by reading what it actually did.
 *
 * The worktree strip removes `expected_output.md` from the *copy*; it cannot remove it from the
 * checkout the copy was made from, and generation runs with permissions bypassed, so a deny rule
 * would not hold. Detection is the honest control: a run that reaches outside its workspace is
 * visible instead of silent. eval-15 addressed the host checkout directly on 2026-08-01 — it
 * searched only `*.java` and found nothing, but the answer keys were one glob away.
 *
 * Returns one entry per distinct kind of reach, each with the text that proves it.
 */
function contaminationHits(conversationJsonl, { repoRoot }) {
  const probes = [
    { kind: "answer-key", pattern: "expected_output" },
    { kind: "host-checkout", pattern: repoRoot },
    // Stays armed: severing the .git link is what should keep the agent out of this project's
    // memory, and this probe is the check that it did.
    { kind: "agent-memory", pattern: "/.claude/projects/" },
  ];
  const hits = [];
  for (const { kind, pattern } of probes) {
    if (!pattern) continue;
    for (const line of conversationJsonl.split("\n")) {
      if (!line.includes(pattern)) continue;
      // Only the agent's own actions count. The prompt and the harness's own frames mention
      // neither, so any occurrence in a tool call is the agent reaching.
      let frame;
      try { frame = JSON.parse(line); } catch { continue; }
      const content = frame?.message?.content;
      if (!Array.isArray(content)) continue;
      const use = content.find(
        (b) => b.type === "tool_use" && JSON.stringify(b.input || {}).includes(pattern)
      );
      if (!use) continue;
      hits.push({ kind, tool: use.name, evidence: JSON.stringify(use.input).slice(0, 300) });
      break;
    }
  }
  return hits;
}

/**
 * Ceiling on an eval's generation timeout, in ms.
 *
 * Across the 79 successful runs stored under `iterations/tabletest` the slowest was 665s and the
 * 95th percentile 518s, so 900s is ~1.35x the worst case ever observed. The evals that carried
 * `timeout_ms: 1500000` were budgeted at 2.3x that, and iteration-50 spent the difference: eval-30
 * hit a dropped connection mid-thinking at ~31,800 thinking tokens, the CLI retried, and the retry
 * re-thought from zero for another ten minutes before the timeout fired. A tighter ceiling would
 * have bought the same (absent) answer for 40% less wall-clock and tokens.
 *
 * The clamp lives here rather than in each `eval.json` because `computeEvalFingerprint` hashes
 * `eval.json` wholesale — editing `timeout_ms` in seven definitions would change seven
 * fingerprints and force a re-baseline to buy nothing.
 */
const GENERATION_TIMEOUT_CEILING_MS = 900000;

/**
 * The generation timeout for one eval: an explicit `--timeout` wins outright, since its documented
 * purpose is giving a slow local model more room than any eval definition anticipates. Absent that,
 * the eval's own `timeout_ms` applies, clamped to the ceiling above.
 */
function generationTimeoutFor(evalDef, args) {
  if (args.timeoutMs) return args.timeoutMs;
  if (!evalDef.timeout_ms) return undefined;   // runClaude's own default
  return Math.min(evalDef.timeout_ms, GENERATION_TIMEOUT_CEILING_MS);
}

/**
 * Ask the CLI to return a readable summary of the model's reasoning.
 *
 * `thinking.display` is a request parameter, and Claude Code — not this script — builds the
 * request, so no CLI flag, settings key or env var reaches it (and `--setting-sources ""` would
 * bar the settings route anyway). The control protocol does: with `--input-format stream-json`
 * the CLI accepts control requests on stdin, and `set_max_thinking_tokens` carries
 * `thinking_display`. Verified against CLI 2.1.220 — the control response is a success and the
 * thinking blocks come back with text instead of an empty string.
 *
 * **This does not change what is measured.** `display` governs visibility only: the model thinks
 * the same and is billed the same under either setting. `max_thinking_tokens` is left null
 * deliberately — the API rejects a thinking budget on Claude 5-family models, so a number here
 * would at best be dropped silently and at worst move the regime. Depth is `--effort`'s job.
 */
const THINKING_DISPLAY_REQUEST = {
  type: "control_request",
  request_id: "thinking-display",
  request: {
    subtype: "set_max_thinking_tokens",
    max_thinking_tokens: null,
    thinking_display: "summarized",
  },
};

function runClaude({ prompt, systemPrompt, model, provider, cwd, pluginDir, apiGeneration = false, timeoutMs = 600000 }) {
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
    // The prompt travels on stdin rather than `-p` so the control request above can precede it.
    args.push("--input-format", "stream-json");

    let stdout = "";
    let stderr = "";

    const env = generationEnv(process.env, { apiGeneration, provider });

    const proc = spawn("claude", args, {
      cwd,
      env,
      stdio: ['pipe', 'pipe', 'pipe'],
    });

    proc.stdin.on("error", () => { /* the close handler reports what the process actually did */ });
    proc.stdin.write(`${JSON.stringify(THINKING_DISPLAY_REQUEST)}\n`);
    proc.stdin.write(`${JSON.stringify({
      type: "user",
      message: { role: "user", content: [{ type: "text", text: prompt }] },
    })}\n`);
    proc.stdin.end();

    // When output last arrived, so a halted run can be told from a working one. See
    // GENERATION_SILENCE_LIMIT_MS — the timestamps inside the stream cannot answer this.
    let lastOutputAt = Date.now();

    const timer = setTimeout(() => {
      proc.kill("SIGTERM");
      const err = new Error(`Timed out after ${timeoutMs}ms`);
      err.stdout = stdout;
      err.stderr = stderr;
      err.silenceMs = Date.now() - lastOutputAt;
      settle(reject, err);
    }, timeoutMs);

    proc.stdout.on("data", (data) => { stdout += data; lastOutputAt = Date.now(); });
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

async function generateResponses(evals, worktreePath, iterationDir, args, repoRoot) {
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
        generateOne(evalDef, worktreePath, iterationDir, args, repoRoot)
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

async function generateOne(evalDef, worktreePath, iterationDir, args, repoRoot) {
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
    // Copy hooks, so the agent runs against the same plugin a user installs.
    // Without this the bundled PostToolUse auto-format hook never fires, and the
    // formatting the skill promises is absent from every eval output.
    const hooksDir = path.join(worktreePath, "hooks");
    if (fs.existsSync(hooksDir)) {
      fs.cpSync(hooksDir, path.join(agentCwd, "hooks"), { recursive: true });
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
      apiGeneration: args.apiGeneration,
      timeoutMs: generationTimeoutFor(evalDef, args),
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

      const reached = contaminationHits(result._conversationJsonl, { repoRoot });
      if (reached.length > 0) {
        fs.writeFileSync(
          path.join(evalDir, "contamination.json"),
          JSON.stringify(reached, null, 2),
          "utf-8"
        );
        log(`  ⚠️  ISOLATION BREACH in ${evalDef.id} — this run's result is not trustworthy:`);
        for (const hit of reached) log(`      ${hit.kind} via ${hit.tool}: ${hit.evidence}`);
      }
    }

    harvestGeneratedFiles(agentCwd, evalDir, evalDef);

    // Run build verification if project scaffolding was present
    if (hasProject) {
      const buildResult = await runBuildCheck(agentCwd);
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
    // Salvage whatever the agent had written before it ran out of budget, so the work is
    // inspectable rather than silently deleted in `finally` below.
    let harvested = 0;
    try {
      harvestGeneratedFiles(agentCwd, evalDir, evalDef);
      harvested = collectTestFiles(agentCwd, languageProfile(evalDef)).length;
    } catch { /* harvesting is best-effort — never mask the original failure */ }

    const failure = classifyGenerationFailure(err, evalDir, evalDef, generationTimeoutFor(evalDef, args));

    // A truncated run that delivered test code is graded on what it delivered. A transient failure
    // or an empty one is not — it produced no answer, and scoring it would attribute network
    // weather to the skill.
    fs.writeFileSync(
      path.join(evalDir, "outputs", "response.md"),
      failure.countable ? lastAssistantText(err.stdout) : `ERROR: ${err.message}`,
      "utf-8"
    );

    if (failure.countable && hasProject) {
      const buildResult = await runBuildCheck(agentCwd);
      if (buildResult) {
        fs.writeFileSync(
          path.join(evalDir, "outputs", "build-result.json"),
          JSON.stringify(buildResult, null, 2),
          "utf-8"
        );
      }
    }

    logError(
      `    ${failure.kind}${harvested ? `, salvaged ${harvested} file(s)` : ""}` +
      `${failure.countable ? " — graded on what it delivered" : " — excluded from totals"}`
    );

    fs.writeFileSync(
      path.join(evalDir, "timing.json"),
      JSON.stringify({
        ...(failure.countable ? { truncated: err.message } : { error: err.message }),
        failure_kind: failure.kind,
        duration_ms: Date.now() - startedAt,
        harvested_files: harvested,
      }, null, 2),
      "utf-8"
    );
  } finally {
    // Clean up per-eval working directory
    if (hasProject && fs.existsSync(agentCwd)) {
      fs.rmSync(agentCwd, { recursive: true });
    }
  }
}

/**
 * Copy whatever the agent wrote out of its working directory into `outputs/`.
 *
 * **Called on the timeout path as well as the success path, and that is the point.** The skills tell
 * the agent that each test written is "a checkpoint that can't be lost to a timeout"
 * (`tabletest/SKILL.md`), but the working directory is deleted in `finally`, so before this the
 * harness discarded exactly those checkpoints and left `response.md` reading `ERROR:`. A timed-out
 * eval is still **excluded from the totals** — a partial answer is not a score — but its files now
 * survive, so you can see how far it got and decide whether a re-run is worth buying.
 */
function harvestGeneratedFiles(agentCwd, evalDir, evalDef) {
  const profile = languageProfile(evalDef);
  for (const tf of collectTestFiles(agentCwd, profile)) {
    const dest = path.join(evalDir, "outputs", path.relative(agentCwd, tf));
    fs.mkdirSync(path.dirname(dest), { recursive: true });
    fs.cpSync(tf, dest);
  }
  for (const buildFile of profile.buildFiles) {
    const src = path.join(agentCwd, buildFile);
    if (fs.existsSync(src)) {
      fs.cpSync(src, path.join(evalDir, "outputs", buildFile));
    }
  }
}

/**
 * Why a generation produced no final answer — the distinction that decides whether it scores.
 *
 * **A transient failure and a budget overrun are not the same event and must not be treated alike.**
 * A dropped connection or a rate limit says nothing about the guidance, and scoring it once took a
 * five-eval run from 71/72 to 54/72. An agent that worked its whole budget and *delivered* test code
 * before time ran out has partly succeeded — and every skill here tells it to work that way ("each
 * method written is a checkpoint"), so whether it does is a property of the wording and belongs in
 * the score.
 *
 * Three signals, all already available: the error is a timeout rather than a transport failure, an
 * `api_retry` actually *cost* the run (see `retryProfile` — the mere presence of one does not), and
 * the harvested files satisfy the language profile's deliverable test — the same predicate
 * `gradeOne` uses to stop empty responses passing format assertions vacuously.
 */
/**
 * How long the child may emit nothing before the run counts as halted rather than working.
 *
 * An API error can stop the CLI dead mid-run — the process stays up and produces no further output
 * until the timeout kills it. That looks identical to a budget overrun from the outside, and it was
 * scored as one: `iteration-57`'s eval-15 delivered two files, went silent for 898s, and its 16/34
 * reached the ledger before the trailing silence was noticed. Interactively this is the failure that
 * needs a "Please continue" to unstick.
 *
 * **Silence is measured from when stdout last arrived, not from the timestamps inside it.** Thinking
 * deltas carry no timestamp, so a long think looks like silence in the JSON while the pipe is busy.
 *
 * The threshold sits well above legitimate quiet: an agent running its own build under a Bash call
 * emits nothing for as long as the build takes (`runBuildCheck` alone allows 120s), and the worst gap
 * on a healthy run was 90s. It sits well below the 900s ceiling, so a genuine overrun — an agent
 * working right up to the deadline — still scores.
 */
const GENERATION_SILENCE_LIMIT_MS = 300000;

/** True when the child stopped producing output long before the timeout fired. */
function halted(err) {
  return Number.isFinite(err.silenceMs) && err.silenceMs >= GENERATION_SILENCE_LIMIT_MS;
}

/**
 * Share of the transcript that must follow the last `api_retry` for the run to count as recovered.
 *
 * **A retry is only the cause of a timeout when the run never came back from it.** Measured over
 * the seven archived runs that retried, this separates them cleanly: `iteration-54`'s eval-30 emitted
 * **nothing** after its retry and died there, while every other run emitted 8%–99% of its transcript
 * afterwards and either finished or was killed much later doing real work.
 */
const RETRY_RECOVERY_SHARE = 0.02;

/**
 * Share of the generation budget that summed retry backoff must reach to be blamed on its own.
 *
 * This has never fired and is here for the case that would justify it. Backoff across the archive
 * tops out at **4,046 ms against a 900,000 ms budget** — 0.4% — so a threshold on delay alone would
 * be dead code today. It stops being dead the moment a server sends a large `Retry-After`, which
 * `retryDelayMs` honours verbatim.
 */
const RETRY_BUDGET_SHARE = 0.1;

/**
 * What an `api_retry` actually cost a run, rather than whether one happened.
 *
 * **The presence test this replaces was wrong in the expensive direction.** `iteration-72`'s eval-25
 * retried twice for a combined **1,084 ms of a 900,000 ms budget**, recovered, and worked for the
 * remaining 80% of its transcript before the deadline killed it — a plain budget overrun. Filed as a
 * transport flake, it read as "just re-run it", and the re-run cost a second full generation.
 *
 * Recovery is measured by transcript position rather than by wall clock because the stream carries
 * no timestamp on thinking deltas: what a recovered run leaves behind is *output after the retry*.
 */
function retryProfile(stdout, timeoutMs) {
  const text = stdout || "";
  let delayMs = 0;
  let retries = 0;
  let lastOffset = -1;
  let offset = 0;
  for (const line of text.split("\n")) {
    if (line.includes('"api_retry"')) {
      let event = null;
      try { event = JSON.parse(line); } catch { /* a truncated line still counts as a retry */ }
      if (!event || event.subtype === "api_retry") {
        retries += 1;
        delayMs += Number(event && event.retry_delay_ms) || 0;
        // The *end* of the retry line, so "output after the retry" excludes the retry itself. Using
        // its start would read a transcript ending in a retry as fully recovered.
        lastOffset = offset + line.length + 1;
      }
    }
    offset += line.length + 1;
  }
  if (!retries) return { retries: 0, delayMs: 0, recoveredShare: 1, blamed: false };

  const after = Math.max(0, text.length - lastOffset);
  const recoveredShare = text.length ? after / text.length : 0;
  const ateTheBudget = Boolean(timeoutMs) && delayMs >= timeoutMs * RETRY_BUDGET_SHARE;
  return {
    retries,
    delayMs,
    recoveredShare,
    blamed: ateTheBudget || recoveredShare < RETRY_RECOVERY_SHARE,
  };
}

function classifyGenerationFailure(err, evalDir, evalDef, timeoutMs) {
  const timedOut = /Timed out after \d+ms/.test(err.message || "");
  const budgetMs = timeoutMs || Number((/Timed out after (\d+)ms/.exec(err.message || "") || [])[1]) || 0;
  const retry = retryProfile(err.stdout, budgetMs);
  const profile = languageProfile(evalDef);
  const delivered = loadOutputFiles(evalDir).some(
    (f) => profile.deliverablePath.test(f.path) && profile.deliverableContent.test(f.content)
  );

  if (!timedOut) return { kind: "crash", countable: false, delivered, retry };
  if (retry.blamed) return { kind: "timeout-after-api-retry", countable: false, delivered, retry };
  if (halted(err)) return { kind: "timeout-after-silent-halt", countable: false, delivered, retry };
  if (!delivered) return { kind: "timeout-no-delivery", countable: false, delivered, retry };
  return { kind: "timeout-after-delivery", countable: true, delivered, retry };
}

/** The agent's last piece of visible prose, for a run that never returned a final result. */
function lastAssistantText(stdout) {
  if (!stdout) return "";
  let text = "";
  for (const line of stdout.split("\n")) {
    if (!line.trim()) continue;
    let event;
    try { event = JSON.parse(line); } catch { continue; }
    const content = event?.message?.role === "assistant" && event.message.content;
    if (!Array.isArray(content)) continue;
    for (const block of content) {
      if (block.type === "text" && block.text?.trim()) text = block.text.trim();
    }
  }
  return text;
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

// The grader is no longer asked to echo each assertion's `text` back (it cost output budget that
// thinking also draws on, and truncated verdicts mid-token once the assertion wording grew). An
// optional `text` group is still tolerated here so a stored response from before that change, or a
// grader that volunteers the field anyway, still parses. `text` is authoritative from the eval
// definition regardless — see majorityVote.
function repairGradingJson(text) {
  const json = extractJson(text) || text;
  const assertions = [];
  const pattern = /"id"\s*:\s*"([^"]*)"[\s\S]*?(?:"text"\s*:\s*"(?:(?:[^"\\]|\\.)*)"[\s\S]*?)?"passed"\s*:\s*(true|false)[\s\S]*?"evidence"\s*:\s*"([\s\S]*?)"\s*\n?\s*[}\]]/g;
  let m;
  while ((m = pattern.exec(json)) !== null) {
    assertions.push({
      id: m[1],
      passed: m[2] === "true",
      evidence: m[3].replace(/\\"/g, '"').replace(/"/g, "'").replace(/\n/g, " ").trim(),
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

async function gradeOne(evalDef, iterationDir, model, gradingSuffix = null, provider = "anthropic", gradeRuns = 1, effort = null, captureThinking = false) {
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
  let gradingUsage = {};
  // The grader's reasoning, one record per API call, written beside the grading rather than inside
  // it: it is bulky, and nothing that reads `grading.json` (`--rebuild`, the report, the answer key)
  // needs it. Kept in git because it is measurement data — the material for diagnosing a flip.
  const thinkingRecords = [];

  for (let i = 0; i < llmAssertions.length; i += LLM_GRADING_BATCH_SIZE) {
    const batch = llmAssertions.slice(i, i + LLM_GRADING_BATCH_SIZE);
    const llmEvalDef = { ...evalDef, assertions: batch };
    const gradingPrompt = buildGradingPrompt(llmEvalDef, response, allFiles, { noDeliverable: gated });

    const samples = [];
    for (let run = 0; run < gradeRuns; run++) {
      // A grader that silently omits an assertion from its JSON used to fall through to
      // `passed: false, evidence: "Not graded"` — a fabricated failure that reads exactly
      // like a finding. Retry the batch once, then fail loudly: the caller already keeps
      // the gradings that succeeded, so a hard error costs only this eval's re-grade.
      let llmGrading = null;
      let missing = [];
      for (let attempt = 1; attempt <= 2; attempt++) {
        const { text: gradingText, thinking, usage } = await gradeViaApi(GRADING_SYSTEM_PROMPT, gradingPrompt, model, provider, effort, captureThinking);
        gradingUsage = addUsage(gradingUsage, usage);
        if (thinking) {
          thinkingRecords.push({
            assertions: batch.map((a) => a.id),
            run: run + 1,
            attempt,
            thinking,
          });
        }
        llmGrading = parseLlmGrading(gradingText);
        if (!llmGrading) {
          throw new Error(
            `Failed to parse grading JSON for eval ${evalDef.id} (assertions ${i + 1}-${i + batch.length}, run ${run + 1}): ${gradingText.slice(0, 200)}`
          );
        }
        missing = missingAssertionIds(batch, llmGrading.assertions);
        if (missing.length === 0) break;
        log(`  ⟳ Eval ${evalDef.id} — grader omitted ${missing.join(", ")}; retrying batch`);
      }
      if (missing.length > 0) {
        throw new Error(
          `Grader omitted ${missing.length} assertion(s) twice for eval ${evalDef.id}: ${missing.join(", ")}`
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
  grading.grading_usage = priceGradingUsage(gradingUsage, resolveModel(model));

  const gradingFile = gradingSuffix ? `grading-${gradingSuffix}.json` : "grading.json";
  fs.writeFileSync(
    path.join(evalDir, gradingFile),
    JSON.stringify(grading, null, 2),
    "utf-8"
  );

  const thinkingFile = gradingSuffix ? `grading-${gradingSuffix}-thinking.json` : "grading-thinking.json";
  const thinkingPath = path.join(evalDir, thinkingFile);
  if (thinkingRecords.length > 0) {
    fs.writeFileSync(
      thinkingPath,
      JSON.stringify({ eval: evalDef.id, model: resolveModel(model), calls: thinkingRecords }, null, 2),
      "utf-8"
    );
  } else if (fs.existsSync(thinkingPath)) {
    // A re-grade that captured nothing must not leave the previous run's reasoning behind, where it
    // would read as this grading's own.
    fs.unlinkSync(thinkingPath);
  }

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

/** The follow-up to regradeCommand: assemble the whole-suite benchmark without re-grading anything. */
function rebuildCommand(args) {
  const flags = [
    `--skill ${args.skill}`,
    `--iteration ${args.iteration}`,
    args.variant ? `--variant ${args.variant}` : null,
    "--rebuild",
    args.gradingSuffix ? `--grading-suffix ${args.gradingSuffix}` : null,
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
        gradeOne(evalDef, iterationDir, args.gradingModel, args.gradingSuffix, args.provider, args.gradeRuns, gradingEffortFor(evalDef, args), args.captureThinking).catch((err) => {
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
      `The gradings that succeeded are on disk. Grade only the failures, then REBUILD — do not\n` +
      `re-run the full command, which re-grades all ${evals.length} evals and throws that work away:\n\n` +
      `  ${regradeCommand(failedIds, args)}\n` +
      `  ${rebuildCommand(args)}\n`
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
function skillProvenance(worktreePath, skillName, repoRoot, generationAuth) {
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
    // Whether the cost figures below were billed or are equivalent list price.
    generation_auth: generationAuth,
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
        return {
          skill_commit: prior.skill_commit,
          skill_digest: prior.skill_digest,
          generation_auth: prior.generation_auth || "unknown",
        };
      }
    } catch {
      // Unreadable benchmark: fall through to the next candidate.
    }
  }
  return { skill_commit: "unknown", skill_digest: "unknown", generation_auth: "unknown" };
}

function unwrapSummary(benchmark) {
  if (!benchmark || !benchmark.summary) return null;
  if (benchmark.summary.assertions_passed !== undefined) return benchmark.summary;
  return benchmark.summary.with_skill || benchmark.summary.no_skill || Object.values(benchmark.summary)[0] || null;
}

/**
 * Evals selected for a `--rebuild` that have no grading file to rebuild from.
 *
 * Rebuilding silently over a missing grading would produce a benchmark short of those evals —
 * indistinguishable from a score change, which is the very hazard the fatal-grading-failure
 * guard exists to prevent. So the caller refuses rather than writing a partial benchmark.
 */
function evalsMissingGrading(evals, iterationDir, suffix) {
  return evals
    .filter((e) => {
      const dir = path.join(iterationDir, `eval-${e.id}-${e.slug}`);
      return !fs.existsSync(path.join(dir, `grading${suffix}.json`));
    })
    .map((e) => e.id);
}

/**
 * Whole-run grading spend, so "what does a regrade cost?" is answerable from the artefact.
 *
 * Reports `null` when nothing recorded usage — a benchmark rebuilt from gradings written before
 * this existed must read as unknown, never as free.
 */
function summariseGradingUsage(results) {
  const usages = results.map((r) => r.grading_usage).filter(Boolean);
  if (usages.length === 0) return null;
  const sum = (k) => usages.reduce((s, u) => s + (u[k] || 0), 0);
  const rates = usages.find((u) => u.rates_usd_per_mtok)?.rates_usd_per_mtok || null;
  return {
    input_tokens: sum("input_tokens"),
    output_tokens: sum("output_tokens"),
    total_tokens: sum("total_tokens"),
    calls: sum("calls"),
    cost_usd: usages.reduce((s, u) => s + (u.cost_usd || 0), 0),
    evals_measured: usages.length,
    rates_usd_per_mtok: rates,
    priced: rates !== null,
  };
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
    // Part of the grading regime: a comparison across a change of effort is not a comparison.
    // null means the API default (high) — what every run before the flag existed used.
    grading_effort: args.gradingEffort || null,
    ...(args.gradeOnly
      ? inheritedProvenance(iterationDir, suffix)
      : skillProvenance(worktreePath, args.skill, repoRoot, args.apiGeneration ? "api" : "subscription")),
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
        // Generation cost above is inherited from the run that produced the outputs; grading
        // cost is this pass's own, and a --grade-only run spends only the latter.
        ...(grading.grading_usage ? { grading_usage: grading.grading_usage } : {}),
        // A truncated run IS scored — it delivered test code before its budget ran out — but the
        // score came from a partial answer, and nothing else in the entry would say so.
        ...(timing.truncated ? { truncated: timing.truncated, failure_kind: timing.failure_kind } : {}),
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

  benchmark.summary = summariseEvals(benchmark.evals);

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

function loadOfficialBenchmark(repoRoot, skill, { excludeIteration } = {}) {
  // Merge evals from all official iterations, using the latest result for each eval.
  // This handles partial runs (e.g. iteration-28 with only 2 evals) by filling in
  // older results for evals not present in the latest iteration.
  // `excludeIteration` leaves one iteration out, so a run can be compared against the
  // baseline it is about to become rather than against itself.
  const officialDir = path.join(repoRoot, iterationsDir(skill));
  if (!fs.existsSync(officialDir)) return null;

  const iterations = fs.readdirSync(officialDir)
    .filter(e => e !== `iteration-${excludeIteration}`)
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
      // A failed generation is not a result. Taking it as the newest one would drop the eval
      // out of every comparison against this baseline until it happened to be re-run.
      if (generationFailed(evalEntry)) continue;
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

/**
 * The eval number an id carries (`eval-26-convert-from-kotest` → `"26"`). Runs are matched to
 * baselines by number rather than by id because slugs get renamed and numbers do not.
 */
function evalNumberOf(id) {
  return id.match(/eval-(\d+)/)?.[1] ?? null;
}

/**
 * Each of a run's evals paired with its counterpart in the baseline, and whether that pair can be
 * compared at all. Both reasons a pair cannot be are named rather than dropped: a changed
 * definition makes two verdicts incommensurable, and an eval the baseline never ran has nothing to
 * be compared against. A caller that sees only the moved verdicts cannot tell either case from
 * agreement, which is the whole hazard.
 */
/**
 * True when this eval produced no answer at all — a generation timeout or crash.
 */
function generationFailed(evalEntry) {
  const results = evalEntry && unwrapResults(evalEntry);
  return Boolean(results && results.error);
}

/**
 * Summary over the evals that produced an answer.
 *
 * A failed generation is **unknown, not zero**. Averaging it in as 0/N once took a five-eval
 * run from 71/72 to 54/72 and turned all 17 assertions it owns into phantom moved verdicts
 * (`iteration-49`). Cost and duration still count every eval — the failed attempt was paid
 * for — but the score counts only what was actually answered, and the gap is named in
 * `errored_evals` rather than buried in the denominator.
 */
function summariseEvals(evalEntries) {
  const all = evalEntries.map(unwrapResults).filter(Boolean);
  const scored = all.filter((r) => !r.error);
  const passed = scored.reduce((sum, r) => sum + r.assertions_passed, 0);
  const total = scored.reduce((sum, r) => sum + r.assertions_total, 0);

  return {
    assertions_passed: passed,
    assertions_total: total,
    pass_rate: total > 0 ? passed / total : 0,
    errored_evals: evalEntries.filter(generationFailed).map((e) => e.id),
    // Scored, unlike errored_evals — but scored on a partial answer, so a reader comparing two
    // runs knows one of these numbers came from an agent that ran out of budget mid-task.
    truncated_evals: evalEntries.filter((e) => unwrapResults(e)?.truncated).map((e) => e.id),
    total_tokens: all.reduce((sum, r) => sum + (r.total_tokens || 0), 0),
    total_duration_ms: all.reduce((sum, r) => sum + (r.duration_ms || 0), 0),
    total_cost_usd: all.reduce((sum, r) => sum + (r.cost_usd || 0), 0),
    grading: summariseGradingUsage(all),
  };
}

function pairedWithBaseline(benchmark, baselineBenchmark) {
  const baselineByNumber = {};
  for (const baselineEntry of baselineBenchmark.evals) {
    const number = evalNumberOf(baselineEntry.id);
    if (number) baselineByNumber[number] = baselineEntry;
  }

  return benchmark.evals.map((evalEntry) => {
    const baselineEntry = baselineByNumber[evalNumberOf(evalEntry.id)];
    if (!baselineEntry) {
      return { evalEntry, baselineEntry: null, comparable: false, reason: "absent-from-baseline" };
    }
    if (generationFailed(evalEntry) || generationFailed(baselineEntry)) {
      return { evalEntry, baselineEntry, comparable: false, reason: "generation-failed" };
    }
    if (fingerprintsDiffer(evalEntry, baselineEntry)) {
      return { evalEntry, baselineEntry, comparable: false, reason: "definition-changed" };
    }
    return { evalEntry, baselineEntry, comparable: true, reason: null };
  });
}

function detectRegressions(benchmark, previousBenchmark) {
  if (!previousBenchmark) return { regressions: [], improvements: [], notComparable: [] };

  const regressions = [];
  const improvements = [];
  const notComparable = [];

  for (const pair of pairedWithBaseline(benchmark, previousBenchmark)) {
    if (!pair.comparable) {
      notComparable.push({ eval: pair.evalEntry.id, reason: pair.reason });
      continue;
    }

    const currResult = unwrapResults(pair.evalEntry);
    const prevResult = unwrapResults(pair.baselineEntry);
    if (!currResult || !prevResult) continue;

    const prevFailed = new Set(prevResult.failed_assertions || []);
    const currFailed = new Set(currResult.failed_assertions || []);

    for (const assertionId of currFailed) {
      if (!prevFailed.has(assertionId)) {
        regressions.push({
          eval: pair.evalEntry.id,
          assertion: assertionId,
        });
      }
    }

    for (const assertionId of prevFailed) {
      if (!currFailed.has(assertionId)) {
        improvements.push({
          eval: pair.evalEntry.id,
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
/**
 * What to call the official baseline. `loadOfficialBenchmark` merges the newest result per eval
 * across every official iteration, so "iteration 40" can silently mean "40 for most evals and 39
 * — a different grading regime — for the rest". Name the merge when there is one.
 */
function officialBaselineLabel(officialBenchmark) {
  return officialBenchmark._iterationName
    ? `official (${officialBenchmark._iterationName})`
    : `official iteration ${officialBenchmark.iteration}`;
}

/** The grading regime a benchmark was produced under, as one readable token. */
function regimeOf(benchmark) {
  if (!benchmark) return "unknown";
  return `${benchmark.grading_model || "unknown"}/${benchmark.grading_effort || "default"}`;
}

function analysisBaselineOf(args, previousBenchmark, officialBenchmark) {
  if (args.compareOfficial && officialBenchmark) {
    return { benchmark: officialBenchmark, label: officialBaselineLabel(officialBenchmark), warning: null };
  }
  // Asking for the official baseline and silently getting a different one is the same class of
  // failure as an unreported exclusion: the run answers a question nobody asked.
  const unhonoured = args.compareOfficial
    ? "`--compare-official` was requested but no official benchmark was found. "
    : "";
  if (previousBenchmark) {
    const compareIter = args.compareIteration != null ? args.compareIteration : args.iteration - 1;
    return {
      benchmark: previousBenchmark,
      label: `iteration ${compareIter}`,
      warning: unhonoured ? `${unhonoured}Compared against iteration ${compareIter} instead.` : null,
    };
  }
  return {
    benchmark: null,
    label: "no baseline",
    warning: unhonoured ? `${unhonoured}Nothing was compared.` : null,
  };
}

/**
 * Everything comparing a run against a baseline yielded: how many of the run's evals could be
 * compared at all, which could not and why, and which verdicts moved among those that could.
 *
 * These belong in one value because the moved list alone is ambiguous in the one direction that
 * matters. Zero moved verdicts reads as "the run changed nothing"; it is equally the shape of a
 * comparison where no eval was comparable and nothing was actually measured. Reported as a bare
 * count, a void comparison is indistinguishable from a clean one, and it reads as reassurance.
 */
function comparisonAgainst(benchmark, baselineBenchmark) {
  if (!baselineBenchmark) {
    return { moved: [], notComparable: [], comparableEvals: 0, totalEvals: benchmark.evals.length };
  }

  const pairs = pairedWithBaseline(benchmark, baselineBenchmark);
  const { regressions, improvements, notComparable } = detectRegressions(benchmark, baselineBenchmark);
  const moved = [
    ...regressions.map((r) => ({ ...r, direction: "lost" })),
    ...improvements.map((i) => ({ ...i, direction: "won" })),
  ].sort((a, b) => a.eval.localeCompare(b.eval) || a.assertion.localeCompare(b.assertion));

  return {
    moved,
    notComparable,
    comparableEvals: pairs.filter((pair) => pair.comparable).length,
    totalEvals: pairs.length,
  };
}

function movedAssertions(benchmark, baselineBenchmark) {
  return comparisonAgainst(benchmark, baselineBenchmark).moved;
}

const EXCLUSION_REASONS = {
  "definition-changed": "definition changed since the baseline; the two verdicts are not commensurable",
  "absent-from-baseline": "the baseline never ran this eval; there is nothing to compare against",
  "generation-failed": "generation failed (timeout or crash), so this eval produced no answer to compare — re-run it before reading anything into the gap",
};

/**
 * How much of the run the comparison actually covered, stated before any delta is shown. An
 * excluded eval contributes no moved verdicts, so silence about exclusions is read as agreement —
 * which is exactly how a comparison covering none of the run once passed for a clean one.
 */
function exclusionsMarkdown(notComparable, comparableEvals, totalEvals) {
  if (notComparable.length === 0) {
    return `**${comparableEvals} of ${totalEvals} evals comparable.**\n\n`;
  }
  const nothingCompared = comparableEvals === 0;
  let md = nothingCompared ? `## ⛔ Nothing was compared\n\n` : `## ⚠️ Partial comparison\n\n`;
  md += `**${comparableEvals} of ${totalEvals} evals comparable.** `;
  md += nothingCompared
    ? `Every eval was excluded from the deltas in this file:\n\n`
    : `The rest were excluded from them:\n\n`;
  for (const excluded of notComparable) {
    md += `- \`${excluded.eval}\` — ${EXCLUSION_REASONS[excluded.reason] || excluded.reason}\n`;
  }
  md += `\n`;
  return md;
}

/**
 * The artefact-review form for a run: one entry per moved assertion, naming the files that
 * can explain it and leaving the cause blank. A score delta is not an attribution — the
 * grader's justification names the assertion, not reliably the cause — so this exists to
 * make the reading step visible work rather than remembered advice.
 *
 * It takes the whole comparison rather than the moved list because the gate has to be able to
 * say "nothing was compared", which no count of moved verdicts can express.
 *
 * `evidenceFor` looks up the grader's own words for an assertion; it is injected so this
 * stays a pure function of the two benchmarks.
 */
function analysisTodoMarkdown(comparison, context, evidenceFor = () => null) {
  const { moved, notComparable, comparableEvals, totalEvals } = comparison;
  const { iteration, label, baselineLabel, regime, baselineRegime, warning, hasBaseline } = context;

  let md = `# Analysis to-do — ${label}, iteration ${iteration}\n\n`;
  md += `Compared against **${baselineLabel}**`;
  if (regime) md += `, grading ${regime}`;
  md += `.\n\n`;
  if (warning) md += `> ⚠️ ${warning}\n\n`;

  // Having no baseline is not the same failure as having one that matched nothing. The first is
  // ordinary — a first iteration, or a re-grade with no predecessor on disk — and must not be
  // dressed in the warning the second one earns, or the warning stops meaning anything.
  if (!hasBaseline) {
    md += `There was no baseline to compare against, so this run records a measurement rather than\n`;
    md += `evaluating a change. Nothing here is a delta.\n`;
    return md;
  }

  if (regime && baselineRegime && regime !== baselineRegime) {
    md += `> ⚠️ **The baseline was graded under a different regime** (${baselineRegime} vs ${regime}).\n`;
    md += `> A comparison is void across a change of grading regime — re-baseline rather than interpret this.\n\n`;
  }

  md += exclusionsMarkdown(notComparable, comparableEvals, totalEvals);

  if (comparableEvals === 0 && totalEvals > 0) {
    md += `**Nothing below is evidence.** This run measured nothing against this baseline; an empty\n`;
    md += `moved list here is the shape of a void comparison, not of a clean one. Re-baseline, or\n`;
    md += `compare against a benchmark whose definitions match, before reading this run at all.\n`;
    return md;
  }

  if (moved.length === 0) {
    md += `No assertion verdicts moved among the evals that could be compared, so there is nothing\n`;
    md += `to attribute.\n`;
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
 * A hash over which evals ran and what their definitions were. Two measurements sharing one ran
 * against the same instrument; comparing across a change of it is comparing two different questions.
 * A partial run hashes only its own evals, so a loop never shares an id with the full suite — the
 * per-eval fingerprint remains the authority, and this is the convenience that makes a table of
 * measurements readable at a glance.
 */
function instrumentId(benchmark) {
  const definition = (benchmark.evals || [])
    .map((entry) => `${entry.id}:${entry.fingerprint || "none"}`)
    .sort()
    .join("|");
  return crypto.createHash("sha256").update(definition).digest("hex").slice(0, 8);
}

/**
 * One measurement as a ledger row. Everything here is already known at the end of a run; the only
 * column left blank is the one only a reader can fill in.
 *
 * A regrade's `cost_usd` and `duration_ms` are inherited from the generation that produced the
 * outputs, not spent again — reporting them as this run's would imply a tuning effort cost twenty
 * times what it did, so they are omitted rather than repeated.
 */
/**
 * How a run names itself in the ledger — the same path a reader would type to find its artefacts,
 * with the grading suffix in brackets so a regrade is distinguishable from the run it re-grades.
 */
function ledgerRunLabel(args) {
  const base = args.variant
    ? `${args.variant}/iteration-${args.iteration}`
    : `iteration-${args.iteration}`;
  return args.gradingSuffix ? `${base} [${args.gradingSuffix}]` : base;
}

function ledgerRow(benchmark, comparison, context) {
  const { runLabel, baselineLabel, isRegrade, isRebuild } = context;
  const summary = benchmark.summary || {};
  const money = (amount) => (amount == null ? "—" : `$${amount.toFixed(2)}`);
  const grading = summary.grading;
  const cells = [
    (benchmark.timestamp || "").slice(0, 10) || "—",
    `\`${runLabel}\``,
    isRebuild ? "rebuild" : isRegrade ? "regrade" : "run",
    String((benchmark.evals || []).length),
    benchmark.skill_digest && benchmark.skill_digest !== "unknown"
      ? `\`${benchmark.skill_digest.slice(0, 10)}\``
      : "—",
    `${benchmark.grading_model || "?"}/${benchmark.grading_effort || "default"}`,
    `\`${instrumentId(benchmark)}\``,
    `${summary.assertions_passed}/${summary.assertions_total}`,
    isRegrade ? "—" : money(summary.total_cost_usd),
    isRebuild || !(grading && grading.priced) ? "—" : money(grading.cost_usd),
    isRegrade || !summary.total_duration_ms ? "—" : `${Math.round(summary.total_duration_ms / 60000)}m`,
    baselineLabel
      ? `vs ${baselineLabel}: ${comparison.comparableEvals}/${comparison.totalEvals} comparable, ${comparison.moved.length} moved. `
      : "",
  ];
  return `| ${cells.join(" | ")}|`;
}

/**
 * Appends the row for this run. The ledger outlives every iteration directory, so it is the durable
 * answer to "what did this cost, against what, and was it comparable" once the artefacts are trimmed
 * — which is exactly why appending cannot be a step someone has to remember.
 */
function appendToLedger(benchmark, comparison, context, repoRoot) {
  const ledgerPath = path.join(repoRoot, "docs", "measurement-ledger.md");
  if (!fs.existsSync(ledgerPath)) return;
  const existing = fs.readFileSync(ledgerPath, "utf-8").replace(/\n+$/, "");
  fs.writeFileSync(ledgerPath, `${existing}\n${ledgerRow(benchmark, comparison, context)}\n`, "utf-8");
  log(`Ledger row appended: ${ledgerPath}`);
}

/**
 * The agent's own account of a run — its visible narration and the order in which it wrote
 * files — distilled from the raw transcript. Worth keeping because the transcript is
 * gitignored and trimmed each cycle, while this is small, and because the write order shows
 * drafts and revisions the final output no longer contains.
 *
 * Thinking *cost* is recorded even when thinking *text* is not. The CLI emits a
 * `system/thinking_tokens` event per delta, and consecutive ones are collapsed here into one line
 * per thinking block. That line is what makes a run that spent everything and produced nothing
 * readable at a glance: iteration-50's eval-30 shows a block reaching ~31,800 tokens, then an
 * `api_retry`, then a second block re-thinking from zero until the timeout — a diagnosis that
 * previously took hand-parsing the gitignored transcript. `api_retry` is carried for the same
 * reason: it was the other half of that story.
 */
function narrationMarkdown(conversationJsonl, evalId) {
  const steps = [];
  // A thinking block arrives as a run of per-delta events; hold the open run until something
  // else interrupts it, so one block is one line rather than several hundred.
  let thinkingRun = null;
  const closeThinkingRun = () => {
    if (!thinkingRun) return;
    steps.push({
      kind: "thinking-cost",
      body: `~${thinkingRun.tokens.toLocaleString()} thinking tokens (${thinkingRun.deltas} deltas)`,
    });
    thinkingRun = null;
  };

  for (const line of String(conversationJsonl).split("\n")) {
    if (!line.trim()) continue;
    let event;
    try { event = JSON.parse(line); } catch { continue; }

    if (event?.type === "system" && event.subtype === "thinking_tokens") {
      if (!thinkingRun) thinkingRun = { tokens: 0, deltas: 0 };
      thinkingRun.tokens = Math.max(thinkingRun.tokens, event.estimated_tokens || 0);
      thinkingRun.deltas += 1;
      continue;
    }
    closeThinkingRun();

    if (event?.type === "system" && event.subtype === "api_retry") {
      const reason = event.error_status || event.error || "unknown";
      steps.push({
        kind: "retry",
        body: `API retry ${event.attempt}/${event.max_retries} after ${event.retry_delay_ms}ms (${reason})`,
      });
      continue;
    }

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
  closeThinkingRun();

  let md = `# Narration — ${evalId}\n\n`;
  md += `The agent's visible narration, thinking cost and file writes, in order, distilled from\n`;
  md += `\`conversation.jsonl\`. Thinking text appears when the run asked the CLI for a summary\n`;
  md += `(\`thinking_display: "summarized"\`); the per-block token counts appear either way.\n\n`;
  if (steps.length === 0) {
    md += `_No narration recorded._\n`;
    return md;
  }
  for (const step of steps) {
    if (step.kind === "writes") md += `**${step.body}**\n\n`;
    else if (step.kind === "thinks") md += `> (thinking) ${step.body.replace(/\n/g, "\n> ")}\n\n`;
    else if (step.kind === "thinking-cost") md += `_[${step.body}]_\n\n`;
    else if (step.kind === "retry") md += `⚠️ _${step.body}_\n\n`;
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

function writeAnalysisTodo(benchmark, analysisBaseline, iterationDir, args) {
  const comparison = comparisonAgainst(benchmark, analysisBaseline.benchmark);
  const label = args.variant ? `${args.skill} variant=${args.variant}` : args.skill;
  const md = analysisTodoMarkdown(
    comparison,
    {
      iteration: args.iteration,
      label,
      baselineLabel: analysisBaseline.label,
      hasBaseline: Boolean(analysisBaseline.benchmark),
      regime: regimeOf(benchmark),
      baselineRegime: analysisBaseline.benchmark ? regimeOf(analysisBaseline.benchmark) : null,
      warning: analysisBaseline.warning,
    },
    graderEvidenceLookup(iterationDir, args.gradingSuffix)
  );
  const suffix = args.gradingSuffix ? `-${args.gradingSuffix}` : "";
  const todoPath = path.join(iterationDir, `analysis-todo${suffix}.md`);
  fs.writeFileSync(todoPath, md, "utf-8");
  log(
    `Analysis to-do saved: ${todoPath} ` +
      `(${comparison.comparableEvals}/${comparison.totalEvals} evals comparable, ${comparison.moved.length} moved)`
  );
  return comparison;
}

/**
 * A comparison that covered none of the run is not a result — it is a measurement that did not
 * happen, and its empty delta list reads as reassurance. Say so where it cannot be skimmed past,
 * and exit non-zero. The run's own artefacts are already written and are perfectly good; the thing
 * that failed is the comparison, so this must never read as a reason to re-run and pay again.
 */
function reportVoidComparison(comparison, analysisBaseline, iterationDir) {
  if (comparison.totalEvals === 0 || comparison.comparableEvals > 0) return false;
  if (!analysisBaseline.benchmark) return false;

  log(`\n⛔ VOID COMPARISON — none of the ${comparison.totalEvals} evals could be compared against ` +
      `${analysisBaseline.label}.`);
  for (const excluded of comparison.notComparable) {
    log(`   ${excluded.eval}: ${EXCLUSION_REASONS[excluded.reason] || excluded.reason}`);
  }
  log(`   Nothing was measured against this baseline. The empty delta is not evidence of no change.`);
  log(`   Generation and grading SUCCEEDED and are saved in ${iterationDir} — do not re-run.`);
  log(`   Re-baseline, or re-report against a matching benchmark (--report-only --compare-iteration M).`);
  return true;
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
  if (analysisBaseline.warning) {
    md += `> ⚠️ ${analysisBaseline.warning}\n\n`;
  }
  if (analysisBaseline.benchmark) {
    const comparison = comparisonAgainst(benchmark, analysisBaseline.benchmark);
    if (comparison.comparableEvals === 0 && comparison.totalEvals > 0) {
      md += `> ⛔ **Void comparison — none of the ${comparison.totalEvals} evals could be compared** vs `;
      md += `${analysisBaseline.label}. Every delta below is computed over nothing; an absence of `;
      md += `movement here is not evidence that nothing moved. See \`analysis-todo.md\`.\n\n`;
    } else if (comparison.moved.length > 0) {
      md += `> ⚠️ **${comparison.moved.length} assertion verdict${comparison.moved.length === 1 ? "" : "s"} moved** vs ${analysisBaseline.label}`;
      md += comparison.comparableEvals < comparison.totalEvals
        ? ` (over ${comparison.comparableEvals} of ${comparison.totalEvals} evals — the rest were not comparable). `
        : `. `;
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
        md += `**Not comparable (${notComparable.length}) — excluded from the deltas above:**\n`;
        for (const nc of notComparable) {
          md += nc.reason === "absent-from-baseline"
            ? `- ⚠️ ${nc.eval}: iteration ${compareIter} did not run this eval; nothing to compare against\n`
            : `- ⚠️ ${nc.eval}: fingerprint differs from iteration ${compareIter}; re-baseline to compare\n`;
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
  classifyGenerationFailure,
  contaminationHits,
  parseEvalIds,
  lastAssistantText,
  harvestGeneratedFiles,
  computeEvalFingerprint,
  fingerprintsDiffer,
  loadEvalsFromDir,
  detectRegressions,
  generateReport,
  // analysis protocol
  analysisBaselineOf,
  pairedWithBaseline,
  summariseEvals,
  generationFailed,
  comparisonAgainst,
  movedAssertions,
  instrumentId,
  ledgerRunLabel,
  ledgerRow,
  appendToLedger,
  loadOfficialBenchmark,
  analysisTodoMarkdown,
  narrationMarkdown,
  // grading pipeline
  parseLlmGrading,
  repairGradingJson,
  extractJson,
  majorityVote,
  gradeResponses,
  regradeCommand,
  rebuildCommand,
  evalsMissingGrading,
  acceptsEffort,
  gradingEffortFor,
  GRADING_EFFORT_LEVELS,
  priceGradingUsage,
  addUsage,
  summariseGradingUsage,
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
  generationEnv,
  // generation
  runClaude,
  runBuildCommand,
  generationTimeoutFor,
  GENERATION_TIMEOUT_CEILING_MS,
  THINKING_DISPLAY_REQUEST,
  // cli
  parseEvalIds,
  resolveModel,
  acceptsTemperature,
  extractGradingText,
  extractGradingThinking,
  parseArgs,
  missingAssertionIds,
};
