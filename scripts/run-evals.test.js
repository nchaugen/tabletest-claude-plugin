// Tests for the eval runner. Run with: node --test scripts/
//
// Scope is the machinery the instrument's trustworthiness rests on: whether a grader response
// is read correctly, whether repeated gradings collapse to the right verdict, whether a
// transient API failure recovers, and whether a failure that cannot recover stops the run
// instead of writing a benchmark that is quietly missing an eval.
//
// Deliberately no network and no real eval fixtures — the eval definitions here are
// fabricated, so this file carries no answer keys into a worktree.

const { test, describe, beforeEach, afterEach } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");

const {
  contaminationHits,
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
  postGradingRequest,
  isRetryableStatus,
  retryDelayMs,
  unwrapResults,
  unwrapSummary,
  parseEvalIds,
  resolveModel,
  acceptsTemperature,
  extractGradingText,
  extractGradingThinking,
  parseArgs,
  missingAssertionIds,
  computeEvalFingerprint,
  fingerprintsDiffer,
  digestDirectory,
  skillProvenance,
  inheritedProvenance,
  generationEnv,
  generationTimeoutFor,
  GENERATION_TIMEOUT_CEILING_MS,
  THINKING_DISPLAY_REQUEST,
  analysisBaselineOf,
  pairedWithBaseline,
  summariseEvals,
  loadOfficialBenchmark,
  detectRegressions,
  comparisonAgainst,
  movedAssertions,
  analysisTodoMarkdown,
  narrationMarkdown,
  instrumentId,
  ledgerRunLabel,
  ledgerRow,
  appendToLedger,
} = require("./run-evals.js");

// --- helpers ---------------------------------------------------------------

const FAST_RETRY = { maxAttempts: 3, baseDelayMs: 1 };

function jsonResponse(status, body, headers = {}) {
  return {
    ok: status >= 200 && status < 300,
    status,
    headers: { get: (k) => headers[k.toLowerCase()] ?? null },
    text: async () => (typeof body === "string" ? body : JSON.stringify(body)),
    json: async () => (typeof body === "string" ? JSON.parse(body) : body),
  };
}

/** Returns each scripted item in turn; an Error is thrown rather than returned. */
function scriptedFetch(script) {
  let calls = 0;
  const fn = async () => {
    const next = script[Math.min(calls++, script.length - 1)];
    if (next instanceof Error) throw next;
    return next;
  };
  fn.callCount = () => calls;
  return fn;
}

function assertion(id, passed, evidence = "because") {
  return { id, text: `${id} holds`, passed, evidence };
}

// --- grader response parsing ----------------------------------------------
//
// A misparse is the worst failure mode in the instrument: it does not crash, it silently
// scores an eval against a response nobody read.

describe("parseLlmGrading", () => {
  const expected = { assertions: [{ id: "a1", text: "t", passed: true, evidence: "e" }] };

  test("reads a bare JSON object", () => {
    assert.deepEqual(parseLlmGrading(JSON.stringify(expected)), expected);
  });

  test("reads JSON inside a fenced block", () => {
    const text = "Here is my grading:\n```json\n" + JSON.stringify(expected) + "\n```\n";
    assert.deepEqual(parseLlmGrading(text), expected);
  });

  test("reads JSON wrapped in prose without a fence", () => {
    const text = "I judged as follows: " + JSON.stringify(expected) + " Hope that helps.";
    assert.deepEqual(parseLlmGrading(text), expected);
  });

  test("falls back to field-level repair when the response is cut off", () => {
    // Truncation is the realistic failure: the grader hits max_tokens mid-array, so no
    // strategy that needs valid JSON can read it, but the completed assertions are intact.
    const truncated = `{"assertions":[{"id":"a1","text":"t","passed":true,"evidence":"e"}`;
    const result = parseLlmGrading(truncated);
    assert.equal(result.assertions.length, 1);
    assert.equal(result.assertions[0].id, "a1");
    assert.equal(result.assertions[0].passed, true);
  });

  test("returns null rather than guessing when nothing parses", () => {
    assert.equal(parseLlmGrading("I could not grade this response."), null);
  });

  test("returns null on an empty response", () => {
    assert.equal(parseLlmGrading(""), null);
  });
});

describe("repairGradingJson", () => {
  test("recovers every assertion from a truncated response", () => {
    const truncated = `{"assertions":[
      {"id":"a1","text":"first","passed":true,"evidence":"yes"},
      {"id":"a2","text":"second","passed":false,"evidence":"no"}`;
    const result = repairGradingJson(truncated);
    assert.deepEqual(result.assertions.map(a => a.id), ["a1", "a2"]);
    assert.deepEqual(result.assertions.map(a => a.passed), [true, false]);
  });

  test("normalises quotes and newlines in evidence", () => {
    const text = `{"assertions":[{"id":"a1","text":"t","passed":true,"evidence":"line one\nline \\"two\\""}]}`;
    const result = repairGradingJson(text);
    assert.ok(!result.assertions[0].evidence.includes("\n"));
    assert.ok(!result.assertions[0].evidence.includes('"'));
  });

  test("returns null when no assertion survives", () => {
    assert.equal(repairGradingJson("{}"), null);
  });

  // The grader is no longer asked to echo `text` back — that field cost output budget which
  // thinking also draws on, and truncated verdicts mid-token once assertion wording grew.
  test("recovers assertions from a response carrying no text field", () => {
    const truncated = `{"assertions":[
      {"id":"a1","passed":true,"evidence":"yes"},
      {"id":"a2","passed":false,"evidence":"no"}`;
    const result = repairGradingJson(truncated);
    assert.deepEqual(result.assertions.map(a => a.id), ["a1", "a2"]);
    assert.deepEqual(result.assertions.map(a => a.passed), [true, false]);
  });

  test("still parses a stored response from before text was dropped", () => {
    const legacy = `{"assertions":[{"id":"a1","text":"first","passed":true,"evidence":"yes"}]}`;
    assert.deepEqual(repairGradingJson(legacy).assertions.map(a => a.passed), [true]);
  });
});

describe("extractJson", () => {
  test("spans the outermost braces", () => {
    assert.equal(extractJson('noise {"a":{"b":1}} noise'), '{"a":{"b":1}}');
  });

  test("returns null when there is no object", () => {
    assert.equal(extractJson("no braces here"), null);
    assert.equal(extractJson("} opens after it closes {"), null);
  });
});

// --- majority voting -------------------------------------------------------
//
// This is the mechanism that took grader instability from ~4% to ~1.4%, so its verdict
// arithmetic is load-bearing for every measured skill delta.

describe("majorityVote", () => {
  const batch = [{ id: "a1", text: "a1 holds" }];

  test("keeps a single sample's verdict and evidence", () => {
    const sample = [assertion("a1", true)];
    assert.deepEqual(majorityVote([sample], batch), sample);
  });

  // `text` is authoritative from the eval definition, never from the grader's response. The
  // single-sample path used to pass the grader's echo straight through while the voting path
  // already rebuilt it from `batch`, so the two disagreed whenever the grader paraphrased.
  test("takes text from the eval definition, not the grader, on a single sample", () => {
    const sample = [{ id: "a1", passed: true, evidence: "found it" }];
    const [result] = majorityVote([sample], batch);
    assert.equal(result.text, "a1 holds");
    assert.equal(result.passed, true);
    assert.equal(result.evidence, "found it");
  });

  test("overrides a grader that echoes the wrong text back", () => {
    const sample = [{ id: "a1", text: "something the grader made up", passed: false, evidence: "no" }];
    assert.equal(majorityVote([sample], batch)[0].text, "a1 holds");
  });

  test("takes the majority verdict when graders disagree", () => {
    const samples = [[assertion("a1", true)], [assertion("a1", false)], [assertion("a1", true)]];
    assert.equal(majorityVote(samples, batch)[0].passed, true);
  });

  test("a lone dissenter does not carry the vote", () => {
    const samples = [[assertion("a1", false)], [assertion("a1", true)], [assertion("a1", false)]];
    assert.equal(majorityVote(samples, batch)[0].passed, false);
  });

  test("marks a split vote in the evidence so instability stays visible", () => {
    const samples = [[assertion("a1", true)], [assertion("a1", false)], [assertion("a1", true)]];
    assert.match(majorityVote(samples, batch)[0].evidence, /\[split vote 2\/3 pass\]/);
  });

  test("leaves evidence unmarked when the graders agree", () => {
    const samples = [[assertion("a1", true)], [assertion("a1", true)]];
    assert.doesNotMatch(majorityVote(samples, batch)[0].evidence, /split vote/);
  });

  test("keeps the evidence of a grader that voted with the majority", () => {
    const samples = [
      [assertion("a1", true, "found it on line 3")],
      [assertion("a1", false, "could not find it")],
      [assertion("a1", true, "also found it")],
    ];
    assert.match(majorityVote(samples, batch)[0].evidence, /found it/);
  });

  test("an even split does not pass — a tie is not a majority", () => {
    const samples = [[assertion("a1", true)], [assertion("a1", false)]];
    assert.equal(majorityVote(samples, batch)[0].passed, false);
  });

  test("ignores samples missing the assertion entirely", () => {
    const samples = [[assertion("a1", true)], [], [assertion("a1", true)]];
    assert.equal(majorityVote(samples, batch)[0].passed, true);
  });
});

// --- transport -------------------------------------------------------------

describe("isRetryableStatus", () => {
  test("retries rate limits and server errors", () => {
    for (const status of [429, 500, 502, 503, 529]) {
      assert.equal(isRetryableStatus(status), true, `${status} should retry`);
    }
  });

  test("does not retry a request the caller got wrong", () => {
    for (const status of [400, 401, 403, 404, 422]) {
      assert.equal(isRetryableStatus(status), false, `${status} should not retry`);
    }
  });
});

describe("retryDelayMs", () => {
  test("honours a Retry-After header in seconds", () => {
    assert.equal(retryDelayMs(0, "2"), 2000);
  });

  test("ignores a header that is not a positive number", () => {
    for (const header of ["soon", "", "0", "-1", null]) {
      assert.ok(retryDelayMs(0, header, 1000) >= 1000, `header ${header} should fall back`);
    }
  });

  test("backs off exponentially", () => {
    // Jitter is bounded at 2x, so attempt 3's floor still clears attempt 0's ceiling.
    assert.ok(retryDelayMs(3, null, 1000) > retryDelayMs(0, null, 1000));
  });

  test("jitters, so parallel graders do not retry in lockstep", () => {
    const delays = new Set(Array.from({ length: 20 }, () => retryDelayMs(1, null, 1000)));
    assert.ok(delays.size > 1, "expected varied delays");
  });
});

describe("postGradingRequest", () => {
  let originalFetch;
  beforeEach(() => { originalFetch = global.fetch; });
  afterEach(() => { global.fetch = originalFetch; });

  test("returns the response when the first call succeeds", async () => {
    global.fetch = scriptedFetch([jsonResponse(200, { ok: true })]);
    const resp = await postGradingRequest("u", {}, "Anthropic", FAST_RETRY);
    assert.equal(resp.status, 200);
    assert.equal(global.fetch.callCount(), 1);
  });

  test("recovers from a transient server error", async () => {
    global.fetch = scriptedFetch([jsonResponse(500, "boom"), jsonResponse(200, { ok: true })]);
    const resp = await postGradingRequest("u", {}, "Anthropic", FAST_RETRY);
    assert.equal(resp.status, 200);
    assert.equal(global.fetch.callCount(), 2);
  });

  test("recovers from a dropped connection", async () => {
    global.fetch = scriptedFetch([new Error("ECONNRESET"), jsonResponse(200, { ok: true })]);
    const resp = await postGradingRequest("u", {}, "Anthropic", FAST_RETRY);
    assert.equal(resp.status, 200);
  });

  test("fails immediately on a client error instead of retrying it four more times", async () => {
    global.fetch = scriptedFetch([jsonResponse(401, "invalid x-api-key")]);
    await assert.rejects(
      () => postGradingRequest("u", {}, "Anthropic", FAST_RETRY),
      (err) => {
        assert.match(err.message, /401/);
        assert.doesNotMatch(err.message, /attempts/);
        return true;
      }
    );
    assert.equal(global.fetch.callCount(), 1);
  });

  test("gives up after the configured number of attempts", async () => {
    global.fetch = scriptedFetch([jsonResponse(500, "still down")]);
    await assert.rejects(
      () => postGradingRequest("u", {}, "Anthropic", FAST_RETRY),
      /after 3 attempts/
    );
    assert.equal(global.fetch.callCount(), 3);
  });

  test("surfaces the underlying network error when retries are exhausted", async () => {
    global.fetch = scriptedFetch([new Error("ENOTFOUND api.anthropic.com")]);
    await assert.rejects(
      () => postGradingRequest("u", {}, "Anthropic", FAST_RETRY),
      /ENOTFOUND/
    );
  });

  test("waits the interval the server asked for", async () => {
    global.fetch = scriptedFetch([
      jsonResponse(429, "slow down", { "retry-after": "1" }),
      jsonResponse(200, { ok: true }),
    ]);
    const started = Date.now();
    await postGradingRequest("u", {}, "Anthropic", FAST_RETRY);
    assert.ok(Date.now() - started >= 1000, "should have waited out Retry-After");
  });
});

// --- aborting on a grading failure ----------------------------------------

describe("regradeCommand", () => {
  const base = {
    skill: "tabletest", iteration: 39, variant: null,
    gradeRuns: 1, gradingSuffix: null, provider: "anthropic",
  };

  test("re-grades only the evals that failed", () => {
    assert.match(regradeCommand([20, 22], base), /--evals 20,22/);
  });

  test("re-grades without regenerating", () => {
    assert.match(regradeCommand([20], base), /--grade-only/);
  });

  test("preserves the flags that decide comparability", () => {
    const cmd = regradeCommand([5], {
      ...base, variant: "next", gradeRuns: 3, gradingSuffix: "m1",
    });
    assert.match(cmd, /--variant next/);
    assert.match(cmd, /--grade-runs 3/);
    assert.match(cmd, /--grading-suffix m1/);
  });

  test("omits flags left at their defaults", () => {
    const cmd = regradeCommand([5], base);
    assert.doesNotMatch(cmd, /--variant/);
    assert.doesNotMatch(cmd, /--grade-runs/);
    assert.doesNotMatch(cmd, /--grading-suffix/);
    assert.doesNotMatch(cmd, /--provider/);
  });
});

// --- rebuilding a benchmark from gradings already on disk -------------------
//
// A fatal grading failure writes no benchmark, stranding the gradings that DID succeed.
// Before --rebuild the only recovery was re-grading all 17 evals: a full pass (20-90 min)
// spent recovering work already on disk. This is the cheap path back.

describe("rebuildCommand", () => {
  const base = { skill: "tabletest", iteration: 40, variant: null, gradingSuffix: null };

  test("rebuilds without grading", () => {
    const cmd = rebuildCommand(base);
    assert.match(cmd, /--rebuild/);
    assert.doesNotMatch(cmd, /--grade-only/);
    assert.doesNotMatch(cmd, /--evals/);
  });

  test("keeps the suffix so it rebuilds the right benchmark", () => {
    assert.match(rebuildCommand({ ...base, gradingSuffix: "t4" }), /--grading-suffix t4/);
  });

  test("omits the suffix when there is none", () => {
    assert.doesNotMatch(rebuildCommand(base), /--grading-suffix/);
  });
});

// --- grading effort --------------------------------------------------------
//
// Effort is part of the grading regime, like the model and the batch size: a comparison across a
// change of effort is not a comparison. Per-eval effort is therefore a property of the eval
// definition (and so of its fingerprint), never an ad-hoc runtime choice.

describe("acceptsEffort", () => {
  test("newer families take effort and reject sampling params", () => {
    assert.equal(acceptsEffort("claude-sonnet-5"), true);
    assert.equal(acceptsEffort("claude-opus-5"), true);
  });

  test("haiku takes sampling params and rejects effort", () => {
    assert.equal(acceptsEffort("claude-haiku-4-5"), false);
    assert.equal(acceptsEffort("claude-haiku-4-5-20251001"), false);
  });
});

describe("gradingEffortFor", () => {
  test("sends nothing by default, preserving the pre-flag regime", () => {
    assert.equal(gradingEffortFor({ id: 7 }, {}), null);
  });

  test("uses the run-wide flag when the eval has no preference", () => {
    assert.equal(gradingEffortFor({ id: 7 }, { gradingEffort: "medium" }), "medium");
  });

  // The whole point of the per-eval knob: cheap evals can sit at medium while hard ones stay
  // high, and each eval still grades at the same effort on every run.
  test("an eval's own effort wins over the run-wide flag", () => {
    assert.equal(
      gradingEffortFor({ id: 18, grading_effort: "high" }, { gradingEffort: "medium" }),
      "high"
    );
  });

  test("rejects a level the API does not accept", () => {
    assert.throws(() => gradingEffortFor({ id: 7, grading_effort: "maximum" }, {}), /not one of/);
    assert.throws(() => gradingEffortFor({ id: 7 }, { gradingEffort: "ultra" }), /not one of/);
  });

  test("accepts every documented level", () => {
    for (const level of GRADING_EFFORT_LEVELS) {
      assert.equal(gradingEffortFor({ id: 7, grading_effort: level }, {}), level);
    }
  });
});

// --- grading cost accounting ----------------------------------------------
//
// Grading cost was folklore until this existed: the runner recorded usage for generation only,
// so "~$0.65 a run" survived unchallenged while assertion texts tripled in length. The rates
// are stamped into the artefact so a stale one is visible rather than silently skewing a figure.

describe("priceGradingUsage", () => {
  test("prices sonnet input and output at their separate rates", () => {
    const p = priceGradingUsage({ input_tokens: 1e6, output_tokens: 1e6 }, "claude-sonnet-5");
    assert.equal(p.cost_usd, 18); // 3 in + 15 out
    assert.equal(p.total_tokens, 2e6);
  });

  // A real haiku run priced at $0 because the table was keyed on a dated id while resolveModel
  // returns the bare alias. The `priced: false` flag made it visible instead of silently wrong.
  test("prices a model whose resolved id is a bare alias", () => {
    const p = priceGradingUsage({ input_tokens: 1e6, output_tokens: 1e6 }, "claude-haiku-4-5");
    assert.equal(p.cost_usd, 6); // 1 in + 5 out
  });

  test("prices a dated id from the same family", () => {
    const p = priceGradingUsage({ input_tokens: 1e6, output_tokens: 0 }, "claude-haiku-4-5-20251001");
    assert.equal(p.cost_usd, 1);
  });

  test("records zero cost but keeps the tokens for an unknown model", () => {
    const p = priceGradingUsage({ input_tokens: 1000, output_tokens: 500 }, "some-future-model");
    assert.equal(p.cost_usd, 0);
    assert.equal(p.total_tokens, 1500);
    assert.equal(p.rates_usd_per_mtok, null);
  });

  test("treats absent usage as zero rather than NaN", () => {
    const p = priceGradingUsage({}, "claude-sonnet-5");
    assert.equal(p.cost_usd, 0);
    assert.equal(p.total_tokens, 0);
  });
});

describe("addUsage", () => {
  test("accumulates tokens and counts calls across batches", () => {
    let t = {};
    t = addUsage(t, { input_tokens: 10, output_tokens: 2 });
    t = addUsage(t, { input_tokens: 5, output_tokens: 3 });
    assert.deepEqual(t, { input_tokens: 15, output_tokens: 5, calls: 2 });
  });

  test("still counts a call that reported no usage", () => {
    assert.equal(addUsage({}, {}).calls, 1);
  });
});

describe("summariseGradingUsage", () => {
  const usage = (i, o, cost) => ({
    grading_usage: {
      input_tokens: i, output_tokens: o, total_tokens: i + o, calls: 1,
      cost_usd: cost, rates_usd_per_mtok: { input: 3, output: 15 },
    },
  });

  test("sums spend across evals", () => {
    const s = summariseGradingUsage([usage(100, 10, 0.5), usage(200, 20, 1.5)]);
    assert.equal(s.cost_usd, 2);
    assert.equal(s.input_tokens, 300);
    assert.equal(s.evals_measured, 2);
    assert.equal(s.priced, true);
  });

  // A benchmark rebuilt from gradings written before usage was recorded must read as
  // unknown, never as free — a zero would look like a measurement.
  test("reports null when nothing recorded usage", () => {
    assert.equal(summariseGradingUsage([{}, {}]), null);
  });

  test("counts only the evals that carry usage", () => {
    const s = summariseGradingUsage([usage(100, 10, 0.5), {}]);
    assert.equal(s.evals_measured, 1);
    assert.equal(s.cost_usd, 0.5);
  });
});

describe("evalsMissingGrading", () => {
  const evals = [{ id: 1, slug: "a" }, { id: 2, slug: "b" }, { id: 3, slug: "c" }];
  let dir;

  beforeEach(() => {
    dir = fs.mkdtempSync(path.join(os.tmpdir(), "rebuild-guard-"));
  });
  afterEach(() => fs.rmSync(dir, { recursive: true, force: true }));

  const writeGrading = (id, slug, suffix) => {
    const d = path.join(dir, `eval-${id}-${slug}`);
    fs.mkdirSync(d, { recursive: true });
    fs.writeFileSync(path.join(d, `grading${suffix}.json`), "{}");
  };

  test("names the evals with nothing to rebuild from", () => {
    writeGrading(1, "a", "-t4");
    assert.deepEqual(evalsMissingGrading(evals, dir, "-t4"), [2, 3]);
  });

  test("reports none when every eval has a grading", () => {
    evals.forEach((e) => writeGrading(e.id, e.slug, "-t4"));
    assert.deepEqual(evalsMissingGrading(evals, dir, "-t4"), []);
  });

  // A grading for a *different* suffix must not satisfy the guard, or a rebuild would
  // silently mix two regrades into one benchmark.
  test("ignores gradings written under another suffix", () => {
    evals.forEach((e) => writeGrading(e.id, e.slug, "-t3"));
    assert.deepEqual(evalsMissingGrading(evals, dir, "-t4"), [1, 2, 3]);
  });

  test("distinguishes the unsuffixed baseline from a suffixed regrade", () => {
    evals.forEach((e) => writeGrading(e.id, e.slug, ""));
    assert.deepEqual(evalsMissingGrading(evals, dir, ""), []);
    assert.deepEqual(evalsMissingGrading(evals, dir, "-t4"), [1, 2, 3]);
  });
});

describe("gradeResponses", () => {
  // A grading failure must stop the run. An ungraded eval is not scored zero — generation
  // succeeded, so there is no timing.error — it just leaves the totals, and the run reads as
  // a smaller suite that scored differently.
  // Real responses always carry a block `type`; the grader now selects the text block by it
  // rather than trusting content[0], since thinking-by-default models put a thinking block first.
  const GOOD_GRADING = {
    content: [{ type: "text", text: '{"assertions":[{"id":"a1","text":"t","passed":true,"evidence":"e"}]}' }],
  };

  let iterationDir;
  let originalFetch;

  const args = {
    gradingModel: "haiku", parallel: 4, gradingSuffix: null, provider: "anthropic",
    gradeRuns: 1, skill: "tabletest", iteration: 39, variant: null,
  };

  const evalDef = (id, slug) => ({
    id, slug, skill: "tabletest",
    assertions: [{ id: "a1", text: "something is judged", type: "llm" }],
  });

  beforeEach(() => {
    originalFetch = global.fetch;
    iterationDir = fs.mkdtempSync(path.join(os.tmpdir(), "run-evals-test-"));
  });

  afterEach(() => {
    global.fetch = originalFetch;
    fs.rmSync(iterationDir, { recursive: true, force: true });
  });

  // The response text carries the slug so the stubbed grader can tell the evals apart —
  // gradeResponses runs them concurrently, so call order cannot be relied on.
  function seed(evalDefs) {
    for (const e of evalDefs) {
      const outputs = path.join(iterationDir, `eval-${e.id}-${e.slug}`, "outputs");
      fs.mkdirSync(outputs, { recursive: true });
      fs.writeFileSync(path.join(outputs, "response.md"), `a generated response for ${e.slug}`);
    }
  }

  /** Fails the grader for the `doomed` eval; grades everything else successfully. */
  function fetchFailingDoomed() {
    return async (_url, options) => {
      const prompt = JSON.stringify(JSON.parse(options.body).messages);
      return prompt.includes("doomed")
        ? jsonResponse(400, "simulated grading outage")
        : jsonResponse(200, GOOD_GRADING);
    };
  }

  test("writes a grading for every eval when all succeed", async () => {
    const evals = [evalDef(1, "alpha"), evalDef(2, "beta")];
    seed(evals);
    global.fetch = async () => jsonResponse(200, GOOD_GRADING);

    await gradeResponses(evals, iterationDir, args);

    for (const e of evals) {
      const written = path.join(iterationDir, `eval-${e.id}-${e.slug}`, "grading.json");
      assert.ok(fs.existsSync(written), `expected grading for eval-${e.id}`);
    }
  });

  test("aborts the run when an eval cannot be graded", async () => {
    const evals = [evalDef(1, "alpha"), evalDef(2, "doomed")];
    seed(evals);
    global.fetch = fetchFailingDoomed();

    await assert.rejects(
      () => gradeResponses(evals, iterationDir, args),
      (err) => {
        assert.ok(err instanceof GradingIncompleteError);
        assert.match(err.message, /eval-2 \(doomed\)/);
        return true;
      }
    );
  });

  test("explains why a partial benchmark is worse than a failure", async () => {
    const evals = [evalDef(2, "doomed")];
    seed(evals);
    global.fetch = fetchFailingDoomed();

    await assert.rejects(
      () => gradeResponses(evals, iterationDir, args),
      /read as a score change/
    );
  });

  test("tells the operator how to recover", async () => {
    const evals = [evalDef(2, "doomed")];
    seed(evals);
    global.fetch = fetchFailingDoomed();

    await assert.rejects(
      () => gradeResponses(evals, iterationDir, args),
      /--grade-only --evals 2/
    );
  });

  test("keeps the gradings that succeeded, so recovery costs only the failures", async () => {
    const evals = [evalDef(1, "alpha"), evalDef(2, "doomed")];
    seed(evals);
    global.fetch = fetchFailingDoomed();

    await assert.rejects(() => gradeResponses(evals, iterationDir, args));

    assert.ok(fs.existsSync(path.join(iterationDir, "eval-1-alpha", "grading.json")));
    assert.ok(!fs.existsSync(path.join(iterationDir, "eval-2-doomed", "grading.json")));
  });

  test("records the failure for the run log", async () => {
    const evals = [evalDef(2, "doomed")];
    seed(evals);
    global.fetch = fetchFailingDoomed();

    await assert.rejects(() => gradeResponses(evals, iterationDir, args));

    assert.ok(fs.existsSync(path.join(iterationDir, "eval-2-doomed", "error.log")));
  });

  test("carries no stack — an unreachable grader is an outcome, not a crash", async () => {
    const evals = [evalDef(2, "doomed")];
    seed(evals);
    global.fetch = fetchFailingDoomed();

    await assert.rejects(
      () => gradeResponses(evals, iterationDir, args),
      (err) => err.stack === undefined
    );
  });

  test("skips an eval whose generation produced no response", async () => {
    const evals = [evalDef(1, "alpha")];
    fs.mkdirSync(path.join(iterationDir, "eval-1-alpha", "outputs"), { recursive: true });
    global.fetch = async () => { throw new Error("should not be called"); };

    await gradeResponses(evals, iterationDir, args);

    assert.ok(!fs.existsSync(path.join(iterationDir, "eval-1-alpha", "grading.json")));
  });
});

// --- benchmark shaping -----------------------------------------------------

describe("unwrapResults", () => {
  test("reads the flat format", () => {
    const results = { assertions_passed: 12, assertions_total: 15 };
    assert.deepEqual(unwrapResults({ results }), results);
  });

  test("reads the legacy nested format", () => {
    const withSkill = { assertions_passed: 12, assertions_total: 15 };
    assert.deepEqual(unwrapResults({ results: { with_skill: withSkill } }), withSkill);
  });

  test("returns null for an eval with no results at all", () => {
    assert.equal(unwrapResults({ results: {} }), null);
    assert.equal(unwrapResults({}), null);
    assert.equal(unwrapResults(null), null);
  });
});

describe("unwrapSummary", () => {
  test("reads the flat format", () => {
    const summary = { assertions_passed: 279, assertions_total: 308 };
    assert.deepEqual(unwrapSummary({ summary }), summary);
  });

  test("reads the legacy nested format", () => {
    const withSkill = { assertions_passed: 279, assertions_total: 308 };
    assert.deepEqual(unwrapSummary({ summary: { with_skill: withSkill } }), withSkill);
  });

  test("returns null when there is no summary", () => {
    assert.equal(unwrapSummary({}), null);
    assert.equal(unwrapSummary(null), null);
  });
});

// --- comparability guard ---------------------------------------------------

describe("fingerprintsDiffer", () => {
  test("flags an eval whose definition changed", () => {
    assert.equal(fingerprintsDiffer({ fingerprint: "aaa" }, { fingerprint: "bbb" }), true);
  });

  test("treats an unchanged definition as comparable", () => {
    assert.equal(fingerprintsDiffer({ fingerprint: "aaa" }, { fingerprint: "aaa" }), false);
  });

  test("treats a pre-guard benchmark as comparable rather than changed", () => {
    assert.equal(fingerprintsDiffer({ fingerprint: "aaa" }, {}), false);
    assert.equal(fingerprintsDiffer({}, { fingerprint: "bbb" }), false);
    assert.equal(fingerprintsDiffer(null, { fingerprint: "bbb" }), false);
  });
});

describe("computeEvalFingerprint", () => {
  let dir;
  beforeEach(() => { dir = fs.mkdtempSync(path.join(os.tmpdir(), "fingerprint-test-")); });
  afterEach(() => { fs.rmSync(dir, { recursive: true, force: true }); });

  test("is stable for unchanged content", () => {
    fs.writeFileSync(path.join(dir, "prompt.md"), "write a test");
    assert.equal(computeEvalFingerprint(dir), computeEvalFingerprint(dir));
  });

  test("changes when the prompt changes", () => {
    fs.writeFileSync(path.join(dir, "prompt.md"), "write a test");
    const before = computeEvalFingerprint(dir);
    fs.writeFileSync(path.join(dir, "prompt.md"), "write a different test");
    assert.notEqual(computeEvalFingerprint(dir), before);
  });

  test("changes when the assertions change", () => {
    fs.writeFileSync(path.join(dir, "prompt.md"), "write a test");
    fs.writeFileSync(path.join(dir, "eval.json"), '{"assertions":[]}');
    const before = computeEvalFingerprint(dir);
    fs.writeFileSync(path.join(dir, "eval.json"), '{"assertions":[{"id":"a1"}]}');
    assert.notEqual(computeEvalFingerprint(dir), before);
  });

  test("changes when the scaffolded project changes", () => {
    fs.writeFileSync(path.join(dir, "prompt.md"), "write a test");
    fs.mkdirSync(path.join(dir, "project"));
    fs.writeFileSync(path.join(dir, "project", "pom.xml"), "<project/>");
    const before = computeEvalFingerprint(dir);
    fs.writeFileSync(path.join(dir, "project", "pom.xml"), "<project> </project>");
    assert.notEqual(computeEvalFingerprint(dir), before);
  });

  test("ignores files that do not define the eval", () => {
    fs.writeFileSync(path.join(dir, "prompt.md"), "write a test");
    const before = computeEvalFingerprint(dir);
    fs.writeFileSync(path.join(dir, "notes.md"), "a stray note");
    assert.equal(computeEvalFingerprint(dir), before);
  });
});

// --- cli -------------------------------------------------------------------

describe("parseEvalIds", () => {
  test("reads a single id", () => {
    assert.deepEqual(parseEvalIds("15"), [15]);
  });

  test("reads a comma-separated list", () => {
    assert.deepEqual(parseEvalIds("15,18,22"), [15, 18, 22]);
  });

  test("expands a range", () => {
    assert.deepEqual(parseEvalIds("25-28"), [25, 26, 27, 28]);
  });

  test("mixes lists and ranges", () => {
    assert.deepEqual(parseEvalIds("2,15-17,29"), [2, 15, 16, 17, 29]);
  });
});

describe("resolveModel", () => {
  test("expands the short names used on the command line", () => {
    assert.equal(resolveModel("haiku"), "claude-haiku-4-5");
    assert.equal(resolveModel("sonnet"), "claude-sonnet-5");
    assert.equal(resolveModel("opus"), "claude-opus-4-8");
  });

  test("passes a full model id through unchanged", () => {
    assert.equal(resolveModel("claude-sonnet-5"), "claude-sonnet-5");
  });
});

describe("digestDirectory", () => {
  let dir;
  beforeEach(() => { dir = fs.mkdtempSync(path.join(os.tmpdir(), "digest-test-")); });
  afterEach(() => { fs.rmSync(dir, { recursive: true, force: true }); });

  function writeSkill(root, files) {
    for (const [rel, content] of Object.entries(files)) {
      const full = path.join(root, rel);
      fs.mkdirSync(path.dirname(full), { recursive: true });
      fs.writeFileSync(full, content);
    }
  }

  test("is stable across repeated digests of the same content", () => {
    writeSkill(dir, { "SKILL.md": "guidance", "references/a.md": "detail" });
    assert.equal(digestDirectory(dir), digestDirectory(dir));
  });

  test("changes when a file's content changes", () => {
    writeSkill(dir, { "SKILL.md": "guidance" });
    const before = digestDirectory(dir);
    writeSkill(dir, { "SKILL.md": "guidance revised" });
    assert.notEqual(digestDirectory(dir), before);
  });

  test("changes when a file is added", () => {
    writeSkill(dir, { "SKILL.md": "guidance" });
    const before = digestDirectory(dir);
    writeSkill(dir, { "references/new.md": "extra" });
    assert.notEqual(digestDirectory(dir), before);
  });

  test("distinguishes identical content under different names", () => {
    const other = fs.mkdtempSync(path.join(os.tmpdir(), "digest-test-"));
    try {
      writeSkill(dir, { "SKILL.md": "same bytes" });
      writeSkill(other, { "RENAMED.md": "same bytes" });
      assert.notEqual(digestDirectory(dir), digestDirectory(other));
    } finally {
      fs.rmSync(other, { recursive: true, force: true });
    }
  });

  test("ignores the order the filesystem happens to list entries in", () => {
    const other = fs.mkdtempSync(path.join(os.tmpdir(), "digest-test-"));
    try {
      writeSkill(dir, { "a.md": "one", "b.md": "two" });
      writeSkill(other, { "b.md": "two", "a.md": "one" });
      assert.equal(digestDirectory(dir), digestDirectory(other));
    } finally {
      fs.rmSync(other, { recursive: true, force: true });
    }
  });
});

describe("skillProvenance", () => {
  let worktree;
  beforeEach(() => { worktree = fs.mkdtempSync(path.join(os.tmpdir(), "provenance-test-")); });
  afterEach(() => { fs.rmSync(worktree, { recursive: true, force: true }); });

  test("digests the skill the agent was handed, not the whole worktree", () => {
    fs.mkdirSync(path.join(worktree, "skills", "tabletest"), { recursive: true });
    fs.writeFileSync(path.join(worktree, "skills", "tabletest", "SKILL.md"), "guidance");
    const expected = digestDirectory(path.join(worktree, "skills", "tabletest"));

    assert.equal(skillProvenance(worktree, "tabletest", worktree).skill_digest, expected);
  });

  test("reports an absent skill rather than throwing", () => {
    // The no-skill baseline deletes the skill under test; that run still needs a benchmark.
    assert.equal(skillProvenance(worktree, "tabletest", worktree).skill_digest, "absent");
  });

  test("records unknown when the repo root is not a git checkout", () => {
    fs.mkdirSync(path.join(worktree, "skills", "tabletest"), { recursive: true });
    assert.equal(skillProvenance(worktree, "tabletest", worktree).skill_commit, "unknown");
  });

  test("records which account paid for the generation", () => {
    fs.mkdirSync(path.join(worktree, "skills", "tabletest"), { recursive: true });
    assert.equal(
      skillProvenance(worktree, "tabletest", worktree, "subscription").generation_auth,
      "subscription",
    );
  });
});

describe("inheritedProvenance", () => {
  let iterationDir;
  beforeEach(() => { iterationDir = fs.mkdtempSync(path.join(os.tmpdir(), "inherit-test-")); });
  afterEach(() => { fs.rmSync(iterationDir, { recursive: true, force: true }); });

  function writeBenchmark(name, body) {
    fs.writeFileSync(path.join(iterationDir, name), JSON.stringify(body));
  }

  test("carries the auth mode forward, since a regrade pays for no generation", () => {
    writeBenchmark("benchmark.json", { skill_commit: "abc", skill_digest: "d", generation_auth: "api" });

    assert.equal(inheritedProvenance(iterationDir, "").generation_auth, "api");
  });

  test("carries the generating run's provenance forward", () => {
    writeBenchmark("benchmark.json", { skill_commit: "abc123", skill_digest: "deadbeef" });

    assert.deepEqual(inheritedProvenance(iterationDir, ""), {
      skill_commit: "abc123",
      skill_digest: "deadbeef",
      generation_auth: "unknown",
    });
  });

  test("prefers the suffixed benchmark when re-grading an existing re-grade", () => {
    writeBenchmark("benchmark.json", { skill_commit: "original", skill_digest: "one" });
    writeBenchmark("benchmark-m3.json", { skill_commit: "regraded", skill_digest: "two" });

    assert.equal(inheritedProvenance(iterationDir, "-m3").skill_commit, "regraded");
  });

  test("falls back to the unsuffixed benchmark on a first suffixed re-grade", () => {
    writeBenchmark("benchmark.json", { skill_commit: "original", skill_digest: "one" });

    assert.equal(inheritedProvenance(iterationDir, "-t0").skill_commit, "original");
  });

  test("reports unknown for outputs generated before provenance was recorded", () => {
    writeBenchmark("benchmark.json", { skill_name: "tabletest" });

    assert.deepEqual(inheritedProvenance(iterationDir, ""), {
      skill_commit: "unknown",
      skill_digest: "unknown",
      generation_auth: "unknown",
    });
  });

  test("survives an unreadable benchmark", () => {
    fs.writeFileSync(path.join(iterationDir, "benchmark.json"), "{ truncated");

    assert.equal(inheritedProvenance(iterationDir, "").skill_commit, "unknown");
  });
});

// --- the analysis protocol -------------------------------------------------
//
// A score delta is not an attribution. These cover the machinery that turns a run into a
// reading obligation: which baseline a verdict moved against, which verdicts moved, and the
// two artefacts that explain them surviving in a readable form.

function benchmarkWith(evals) {
  return { iteration: 40, evals };
}

function evalResult(id, failed, fingerprint = "fp1") {
  return {
    id,
    fingerprint,
    results: { assertions_passed: 0, assertions_total: 0, failed_assertions: failed },
  };
}

describe("analysisBaselineOf", () => {
  const previous = benchmarkWith([]);
  const official = { iteration: 40, evals: [] };

  test("attributes against the official skill when the run asked to compare with it", () => {
    const chosen = analysisBaselineOf({ compareOfficial: true, iteration: 6 }, previous, official);
    assert.equal(chosen.benchmark, official);
    assert.match(chosen.label, /official iteration 40/);
  });

  test("falls back to the previous iteration when no official comparison was asked for", () => {
    const chosen = analysisBaselineOf({ iteration: 6 }, previous, official);
    assert.equal(chosen.benchmark, previous);
    assert.equal(chosen.label, "iteration 5");
  });

  test("names the explicitly requested comparison iteration", () => {
    const chosen = analysisBaselineOf({ iteration: 6, compareIteration: 4 }, previous, official);
    assert.equal(chosen.label, "iteration 4");
  });

  test("reports no baseline rather than inventing one", () => {
    const chosen = analysisBaselineOf({ iteration: 1 }, null, null);
    assert.equal(chosen.benchmark, null);
    assert.equal(chosen.label, "no baseline");
  });

  test("names the merge when the official baseline is one", () => {
    const merged = { iteration: 40, evals: [], _iterationName: "iterations 40, 39 merged" };
    const chosen = analysisBaselineOf({ compareOfficial: true, iteration: 6 }, previous, merged);
    assert.equal(chosen.label, "official (iterations 40, 39 merged)");
  });

  test("says so when --compare-official could not be honoured, instead of quietly substituting", () => {
    const chosen = analysisBaselineOf({ compareOfficial: true, iteration: 6 }, previous, null);
    assert.equal(chosen.benchmark, previous);
    assert.match(chosen.warning, /`--compare-official` was requested but no official benchmark was found/);
    assert.match(chosen.warning, /Compared against iteration 5 instead/);
  });

  test("carries no warning when the requested baseline is the one used", () => {
    assert.equal(analysisBaselineOf({ compareOfficial: true, iteration: 6 }, previous, official).warning, null);
    assert.equal(analysisBaselineOf({ iteration: 6 }, previous, official).warning, null);
  });
});

describe("movedAssertions", () => {
  test("reports verdicts moving in both directions as one list", () => {
    const moved = movedAssertions(
      benchmarkWith([evalResult("eval-15-x", ["b-fails-now"])]),
      benchmarkWith([evalResult("eval-15-x", ["a-passed-now"])])
    );
    assert.deepEqual(moved, [
      { eval: "eval-15-x", assertion: "a-passed-now", direction: "won" },
      { eval: "eval-15-x", assertion: "b-fails-now", direction: "lost" },
    ]);
  });

  test("is empty when every verdict held", () => {
    const same = [evalResult("eval-15-x", ["still-failing"])];
    assert.deepEqual(movedAssertions(benchmarkWith(same), benchmarkWith(same)), []);
  });

  test("excludes an eval whose definition changed, since its verdicts are not comparable", () => {
    const moved = movedAssertions(
      benchmarkWith([evalResult("eval-15-x", ["fails"], "fp2")]),
      benchmarkWith([evalResult("eval-15-x", [], "fp1")])
    );
    assert.deepEqual(moved, []);
  });
});

describe("pairedWithBaseline", () => {
  test("matches a run's evals to the baseline by number, not by slug", () => {
    const pairs = pairedWithBaseline(
      benchmarkWith([evalResult("eval-26-renamed-since", [])]),
      benchmarkWith([evalResult("eval-26-convert-from-kotest", [])])
    );
    assert.equal(pairs.length, 1);
    assert.equal(pairs[0].comparable, true);
  });

  test("names a changed definition as the reason a pair cannot be compared", () => {
    const pairs = pairedWithBaseline(
      benchmarkWith([evalResult("eval-26-x", [], "fp2")]),
      benchmarkWith([evalResult("eval-26-x", [], "fp1")])
    );
    assert.equal(pairs[0].comparable, false);
    assert.equal(pairs[0].reason, "definition-changed");
  });

  test("names an eval the baseline never ran, rather than dropping it", () => {
    const pairs = pairedWithBaseline(benchmarkWith([evalResult("eval-31-new", [])]), benchmarkWith([]));
    assert.equal(pairs[0].comparable, false);
    assert.equal(pairs[0].reason, "absent-from-baseline");
  });
});

describe("comparisonAgainst", () => {
  test("counts how many of the run's evals were actually comparable", () => {
    const comparison = comparisonAgainst(
      benchmarkWith([evalResult("eval-20-a", [], "fp1"), evalResult("eval-26-b", [], "fp2")]),
      benchmarkWith([evalResult("eval-20-a", [], "fp1"), evalResult("eval-26-b", [], "fp-old")])
    );
    assert.equal(comparison.comparableEvals, 1);
    assert.equal(comparison.totalEvals, 2);
    assert.deepEqual(comparison.notComparable, [{ eval: "eval-26-b", reason: "definition-changed" }]);
  });

  test("distinguishes a void comparison from a clean one — both move zero verdicts", () => {
    const cleanRun = comparisonAgainst(
      benchmarkWith([evalResult("eval-20-a", ["still-failing"], "fp1")]),
      benchmarkWith([evalResult("eval-20-a", ["still-failing"], "fp1")])
    );
    const voidRun = comparisonAgainst(
      benchmarkWith([evalResult("eval-20-a", ["still-failing"], "fp2")]),
      benchmarkWith([evalResult("eval-20-a", [], "fp1")])
    );
    assert.deepEqual(cleanRun.moved, []);
    assert.deepEqual(voidRun.moved, []);
    assert.equal(cleanRun.comparableEvals, 1);
    assert.equal(voidRun.comparableEvals, 0);
  });

  test("reports no comparable evals rather than failing when there is no baseline at all", () => {
    const comparison = comparisonAgainst(benchmarkWith([evalResult("eval-20-a", [])]), null);
    assert.deepEqual(comparison.moved, []);
    assert.equal(comparison.comparableEvals, 0);
    assert.equal(comparison.totalEvals, 1);
  });
});

describe("analysisTodoMarkdown", () => {
  const context = {
    iteration: 6,
    label: "tabletest variant=next",
    baselineLabel: "official iteration 40",
    hasBaseline: true,
  };
  const over = (evals, moved, notComparable = []) => ({
    moved,
    notComparable,
    comparableEvals: evals - notComparable.length,
    totalEvals: evals,
  });

  test("gives every moved verdict an artefact list and an unfilled cause", () => {
    const md = analysisTodoMarkdown(
      over(1, [{ eval: "eval-18-y", assertion: "rule-falsifiable-by-a-row", direction: "lost" }]),
      context
    );
    assert.match(md, /LOST `rule-falsifiable-by-a-row` — eval-18-y/);
    assert.match(md, /eval-18-y\/outputs\//);
    assert.match(md, /eval-18-y\/narration\.md/);
    assert.match(md, /Cause \(from artefact\): *$/m);
  });

  test("quotes the grader's own words when they are available, as a claim to check", () => {
    const md = analysisTodoMarkdown(
      over(1, [{ eval: "eval-18-y", assertion: "a", direction: "lost" }]),
      context,
      () => "both rows have identical inputs"
    );
    assert.match(md, /Grader said: _both rows have identical inputs_/);
  });

  test("survives a grading file it cannot read", () => {
    const md = analysisTodoMarkdown(
      over(1, [{ eval: "eval-18-y", assertion: "a", direction: "lost" }]),
      context,
      () => null
    );
    assert.doesNotMatch(md, /Grader said/);
  });

  test("says there is nothing to attribute when no verdict moved", () => {
    const md = analysisTodoMarkdown(over(1, []), context);
    assert.match(md, /No assertion verdicts moved/);
    assert.doesNotMatch(md, /Cause \(from artefact\)/);
  });

  test("states how much of the run the comparison covered, even when it covered all of it", () => {
    assert.match(analysisTodoMarkdown(over(4, []), context), /\*\*4 of 4 evals comparable\.\*\*/);
  });

  // The failure this whole file exists to prevent: iteration-40's stale benchmark excluded all
  // four evals of the cluster-1 loop, and the to-do reported it as "nothing to attribute".
  test("refuses to read a comparison that covered no evals as a clean run", () => {
    const md = analysisTodoMarkdown(
      over(4, [], [
        { eval: "eval-20-a", reason: "definition-changed" },
        { eval: "eval-26-b", reason: "definition-changed" },
        { eval: "eval-29-c", reason: "definition-changed" },
        { eval: "eval-30-d", reason: "definition-changed" },
      ]),
      context
    );
    assert.match(md, /Nothing was compared/);
    assert.match(md, /\*\*0 of 4 evals comparable\.\*\*/);
    assert.match(md, /Nothing below is evidence/);
    assert.doesNotMatch(md, /nothing to attribute/);
  });

  test("names every excluded eval and why, so silence is never read as agreement", () => {
    const md = analysisTodoMarkdown(
      over(2, [{ eval: "eval-20-a", assertion: "x", direction: "won" }], [
        { eval: "eval-31-new", reason: "absent-from-baseline" },
      ]),
      context
    );
    assert.match(md, /Partial comparison/);
    assert.match(md, /\*\*1 of 2 evals comparable\.\*\*/);
    assert.match(md, /`eval-31-new` — the baseline never ran this eval/);
    assert.match(md, /WON `x` — eval-20-a/);
  });

  test("warns when the baseline was graded under a different regime", () => {
    const md = analysisTodoMarkdown(over(1, []), {
      ...context,
      regime: "claude-sonnet-5/medium",
      baselineRegime: "claude-haiku-4-5/default",
    });
    assert.match(md, /graded under a different regime/);
    assert.match(md, /claude-haiku-4-5\/default vs claude-sonnet-5\/medium/);
  });

  // A first iteration, or a re-grade with nothing on disk before it, is ordinary. Dressing it in
  // the void-comparison warning would train the reader to skip that warning.
  test("does not cry void when there was simply no baseline to compare against", () => {
    const md = analysisTodoMarkdown(over(17, []), {
      ...context,
      baselineLabel: "no baseline",
      hasBaseline: false,
    });
    assert.match(md, /no baseline to compare against/);
    assert.doesNotMatch(md, /Nothing below is evidence/);
    assert.doesNotMatch(md, /0 of 17 evals comparable/);
  });

  test("surfaces a --compare-official request that could not be honoured", () => {
    const md = analysisTodoMarkdown(over(1, []), {
      ...context,
      warning: "`--compare-official` was requested but no official benchmark was found. Compared against iteration 5 instead.",
    });
    assert.match(md, /`--compare-official` was requested but no official benchmark was found/);
  });
});

describe("instrumentId", () => {
  const suite = benchmarkWith([evalResult("eval-20-a", [], "fpA"), evalResult("eval-26-b", [], "fpB")]);

  test("is stable across the order evals happen to appear in", () => {
    const reordered = benchmarkWith([...suite.evals].reverse());
    assert.equal(instrumentId(suite), instrumentId(reordered));
  });

  test("changes when any eval's definition changes", () => {
    const edited = benchmarkWith([evalResult("eval-20-a", [], "fpA"), evalResult("eval-26-b", [], "fpB2")]);
    assert.notEqual(instrumentId(suite), instrumentId(edited));
  });

  test("does not match a subset of itself, since a loop measures a different question", () => {
    const loop = benchmarkWith([evalResult("eval-20-a", [], "fpA")]);
    assert.notEqual(instrumentId(suite), instrumentId(loop));
  });

  test("is independent of the verdicts — it identifies the instrument, not the result", () => {
    const sameSuiteWorseRun = benchmarkWith([
      evalResult("eval-20-a", ["now-failing"], "fpA"),
      evalResult("eval-26-b", ["also-failing"], "fpB"),
    ]);
    assert.equal(instrumentId(suite), instrumentId(sameSuiteWorseRun));
  });
});

describe("ledgerRunLabel", () => {
  test("names an official run by its iteration", () => {
    assert.equal(ledgerRunLabel({ iteration: 41 }), "iteration-41");
  });

  test("names a variant run by its variant directory", () => {
    assert.equal(ledgerRunLabel({ iteration: 8, variant: "next" }), "next/iteration-8");
  });

  test("distinguishes a regrade from the run it re-grades", () => {
    assert.equal(ledgerRunLabel({ iteration: 40, gradingSuffix: "t5" }), "iteration-40 [t5]");
  });
});

describe("ledgerRow", () => {
  const benchmark = {
    timestamp: "2026-07-26T10:00:00.000Z",
    skill_digest: "85686277bf31ae4a",
    grading_model: "claude-sonnet-5",
    evals: [evalResult("eval-20-a", [], "fpA")],
    summary: {
      assertions_passed: 88,
      assertions_total: 96,
      total_cost_usd: 4.91,
      total_duration_ms: 26 * 60 * 1000,
      grading: { cost_usd: 0.57, priced: true },
    },
  };
  const comparison = { moved: [{}, {}], notComparable: [], comparableEvals: 4, totalEvals: 4 };

  test("records what was spent, against what, and whether it was comparable", () => {
    const row = ledgerRow(benchmark, comparison, {
      runLabel: "next/iteration-7",
      baselineLabel: "official iteration 40",
      isRegrade: false,
    });
    assert.match(row, /\| 2026-07-26 \|/);
    assert.match(row, /`next\/iteration-7`/);
    assert.match(row, /\| run \|/);
    assert.match(row, /88\/96/);
    assert.match(row, /\$4\.91/);
    assert.match(row, /\$0\.57/);
    assert.match(row, /26m/);
    assert.match(row, /vs official iteration 40: 4\/4 comparable, 2 moved/);
  });

  // A regrade re-uses stored outputs; repeating their generation cost would imply a tuning effort
  // spent twenty times what it did.
  test("omits generation cost and duration on a regrade, which spent neither", () => {
    const row = ledgerRow(benchmark, comparison, {
      runLabel: "iteration-40 [t5]",
      baselineLabel: null,
      isRegrade: true,
    });
    assert.doesNotMatch(row, /\$4\.91/);
    assert.doesNotMatch(row, /26m/);
    assert.match(row, /\$0\.57/);
    assert.match(row, /\| regrade \|/);
  });

  // A rebuild makes no API calls. Repeating the grading cost of the files it reads would add
  // spend to the ledger that was already recorded by the run that produced them.
  test("marks a rebuild as such and claims no spend for it", () => {
    const row = ledgerRow(benchmark, comparison, {
      runLabel: "iteration-41",
      baselineLabel: null,
      isRegrade: true,
      isRebuild: true,
    });
    assert.match(row, /\| rebuild \|/);
    assert.doesNotMatch(row, /\$0\.57/);
    assert.doesNotMatch(row, /\$4\.91/);
  });

  test("reports an unpriced grading cost as unknown rather than as free", () => {
    const unpriced = { ...benchmark, summary: { ...benchmark.summary, grading: { cost_usd: 0, priced: false } } };
    const row = ledgerRow(unpriced, comparison, { runLabel: "x", baselineLabel: null, isRegrade: true });
    assert.doesNotMatch(row, /\$0\.00/);
  });

  test("keeps the note column empty when there was no baseline to compare against", () => {
    const row = ledgerRow(benchmark, comparison, { runLabel: "x", baselineLabel: null, isRegrade: false });
    assert.match(row, /\| \|$/);
  });
});

describe("appendToLedger", () => {
  const benchmark = {
    timestamp: "2026-07-26T10:00:00.000Z",
    grading_model: "claude-sonnet-5",
    evals: [evalResult("eval-20-a", [], "fpA")],
    summary: { assertions_passed: 1, assertions_total: 1 },
  };
  const comparison = { moved: [], notComparable: [], comparableEvals: 1, totalEvals: 1 };
  const context = { runLabel: "iteration-41", baselineLabel: null, isRegrade: false };

  let repoRoot;
  beforeEach(() => {
    repoRoot = fs.mkdtempSync(path.join(os.tmpdir(), "ledger-"));
    fs.mkdirSync(path.join(repoRoot, "docs"));
  });
  afterEach(() => fs.rmSync(repoRoot, { recursive: true, force: true }));

  const ledger = () => fs.readFileSync(path.join(repoRoot, "docs", "measurement-ledger.md"), "utf-8");
  const writeLedger = (body) => fs.writeFileSync(path.join(repoRoot, "docs", "measurement-ledger.md"), body);

  test("adds exactly one row and leaves everything above it untouched", () => {
    const before = "# Measurement ledger\n\nprose\n\n| Date |\n|---|\n| old row |\n";
    writeLedger(before);

    appendToLedger(benchmark, comparison, context, repoRoot);

    const lines = ledger().trimEnd().split("\n");
    assert.equal(lines.length, before.trimEnd().split("\n").length + 1);
    assert.equal(lines.slice(0, -1).join("\n"), before.trimEnd());
    assert.match(lines[lines.length - 1], /`iteration-41`/);
  });

  test("appends one row per run, never rewriting an earlier one", () => {
    writeLedger("| Date |\n|---|\n");

    appendToLedger(benchmark, comparison, context, repoRoot);
    appendToLedger(benchmark, comparison, { ...context, runLabel: "iteration-42" }, repoRoot);

    assert.match(ledger(), /`iteration-41`/);
    assert.match(ledger(), /`iteration-42`/);
  });

  test("does not create a ledger that is not there — an absent one is a deliberate absence", () => {
    appendToLedger(benchmark, comparison, context, repoRoot);
    assert.equal(fs.existsSync(path.join(repoRoot, "docs", "measurement-ledger.md")), false);
  });
});

describe("narrationMarkdown", () => {
  const line = (content) => JSON.stringify({ message: { role: "assistant", content } }) + "\n";

  test("keeps the agent's visible narration in order", () => {
    const jsonl =
      line([{ type: "text", text: "First the counting concern." }]) +
      line([{ type: "text", text: "Now the ladder concern." }]);
    const md = narrationMarkdown(jsonl, "eval-15-x");
    assert.ok(md.indexOf("First the counting concern.") < md.indexOf("Now the ladder concern."));
  });

  test("records each file write so drafts and revisions stay visible", () => {
    const jsonl = line([
      { type: "tool_use", name: "Write", input: { file_path: "/w/T.java", content: "a\nb\nc" } },
    ]);
    assert.match(narrationMarkdown(jsonl, "eval-15-x"), /Write \/w\/T\.java \(3 lines\)/);
  });

  test("includes thinking when the model returned it in the clear", () => {
    const jsonl = line([{ type: "thinking", thinking: "weighing two tables" }]);
    assert.match(narrationMarkdown(jsonl, "eval-15-x"), /\(thinking\) weighing two tables/);
  });

  test("omits the encrypted thinking Claude 5 models return as signature only", () => {
    const jsonl = line([{ type: "thinking", thinking: "", signature: "abc" }]);
    assert.match(narrationMarkdown(jsonl, "eval-15-x"), /No narration recorded/);
  });

  test("ignores tool results, user turns, and unparseable lines", () => {
    const jsonl =
      "not json\n" +
      JSON.stringify({ message: { role: "user", content: [{ type: "text", text: "prompt" }] } }) + "\n" +
      line([{ type: "tool_use", name: "Bash", input: { command: "ls" } }]);
    const md = narrationMarkdown(jsonl, "eval-15-x");
    assert.doesNotMatch(md, /\bprompt\b|\bls\b/);
    assert.match(md, /No narration recorded/);
  });

  const thinkingDelta = (estimated) =>
    JSON.stringify({ type: "system", subtype: "thinking_tokens", estimated_tokens: estimated }) + "\n";

  test("collapses a run of thinking deltas into one line carrying the block's cost", () => {
    const jsonl = thinkingDelta(50) + thinkingDelta(9000) + thinkingDelta(31800);
    assert.match(narrationMarkdown(jsonl, "eval-30-x"), /~31,800 thinking tokens \(3 deltas\)/);
  });

  test("separates the thinking blocks either side of an interruption", () => {
    const jsonl =
      thinkingDelta(50) + thinkingDelta(31800) +
      line([{ type: "text", text: "Now writing the table." }]) +
      thinkingDelta(50) + thinkingDelta(900);
    const md = narrationMarkdown(jsonl, "eval-30-x");
    assert.match(md, /~31,800 thinking tokens \(2 deltas\)/);
    assert.match(md, /~900 thinking tokens \(2 deltas\)/);
  });

  // The retry is what turned eval-30's timeout from a mystery into a diagnosis: the second
  // thinking block was the first one being redone after the connection dropped.
  test("names an API retry, so a repeated thinking block is not read as fresh work", () => {
    const jsonl =
      thinkingDelta(31800) +
      JSON.stringify({
        type: "system", subtype: "api_retry", attempt: 1, max_retries: 10,
        retry_delay_ms: 509, error: "unknown", error_status: null,
      }) + "\n" +
      thinkingDelta(23700);
    const md = narrationMarkdown(jsonl, "eval-30-x");
    assert.match(md, /API retry 1\/10 after 509ms \(unknown\)/);
    assert.ok(md.indexOf("31,800") < md.indexOf("API retry"));
    assert.ok(md.indexOf("API retry") < md.indexOf("23,700"));
  });
});

describe("generationTimeoutFor", () => {
  test("clamps an eval's own budget to the ceiling", () => {
    assert.equal(
      generationTimeoutFor({ timeout_ms: 1500000 }, {}),
      GENERATION_TIMEOUT_CEILING_MS
    );
  });

  test("leaves a budget already under the ceiling alone", () => {
    assert.equal(generationTimeoutFor({ timeout_ms: 600000 }, {}), 600000);
  });

  // The flag exists for slow local models, which need more room than any definition anticipates,
  // so it has to beat the ceiling rather than be clipped by it.
  test("lets an explicit --timeout exceed the ceiling", () => {
    assert.equal(generationTimeoutFor({ timeout_ms: 1500000 }, { timeoutMs: 3600000 }), 3600000);
  });

  test("falls through to runClaude's own default when the eval sets no budget", () => {
    assert.equal(generationTimeoutFor({}, {}), undefined);
  });
});

describe("THINKING_DISPLAY_REQUEST", () => {
  // The CLI validates both fields together: thinking_display must be "summarized", "omitted" or
  // null, and max_thinking_tokens an integer or null. A thinking budget is rejected by the API on
  // Claude 5-family models, so it stays null and depth stays --effort's business.
  test("asks for a summary without setting a thinking budget", () => {
    assert.equal(THINKING_DISPLAY_REQUEST.request.subtype, "set_max_thinking_tokens");
    assert.equal(THINKING_DISPLAY_REQUEST.request.thinking_display, "summarized");
    assert.equal(THINKING_DISPLAY_REQUEST.request.max_thinking_tokens, null);
  });

  test("serialises to one line, since the CLI reads stdin newline-delimited", () => {
    assert.doesNotMatch(JSON.stringify(THINKING_DISPLAY_REQUEST), /\n/);
  });
});

describe("acceptsTemperature", () => {
  test("keeps temperature 0 on models that still accept sampling parameters", () => {
    assert.equal(acceptsTemperature("claude-haiku-4-5"), true);
    assert.equal(acceptsTemperature("claude-sonnet-4-6"), true);
  });

  test("omits temperature on models that reject non-default sampling parameters", () => {
    // A non-default temperature is a 400 on these; omitting it is accepted. Grading there
    // runs at model-default sampling, so the regime cannot be "temperature 0".
    assert.equal(acceptsTemperature("claude-sonnet-5"), false);
    assert.equal(acceptsTemperature("claude-opus-5"), false);
    assert.equal(acceptsTemperature("claude-opus-4-8"), false);
    assert.equal(acceptsTemperature("claude-fable-5"), false);
  });

  test("matches dated snapshots of a rejecting model, not just the bare alias", () => {
    assert.equal(acceptsTemperature("claude-sonnet-5-20260115"), false);
  });
});

describe("extractGradingText", () => {
  test("reads the verdict from a plain text-only response", () => {
    const data = { content: [{ type: "text", text: '{"assertions":[]}' }] };
    assert.equal(extractGradingText(data), '{"assertions":[]}');
  });

  test("skips a leading thinking block", () => {
    // Newer models run adaptive thinking by default, so content[0] is a thinking
    // block (empty text, since display defaults to omitted) and the verdict follows.
    const data = {
      content: [
        { type: "thinking", thinking: "", signature: "abc" },
        { type: "text", text: '{"assertions":[{"id":"x","passed":true}]}' },
      ],
    };
    assert.match(extractGradingText(data), /"passed":true/);
  });

  test("throws a diagnosable error when no text block came back", () => {
    const data = { content: [{ type: "thinking", thinking: "", signature: "abc" }] };
    assert.throws(() => extractGradingText(data), /no text block \(blocks: thinking\)/);
  });

  test("throws when the response has no content array at all", () => {
    assert.throws(() => extractGradingText({ error: "boom" }), /no content array/);
  });
});

describe("missingAssertionIds", () => {
  const batch = [{ id: "a" }, { id: "b" }, { id: "c" }];

  test("reports nothing when every assertion came back", () => {
    const returned = [{ id: "c" }, { id: "a" }, { id: "b" }];
    assert.deepEqual(missingAssertionIds(batch, returned), []);
  });

  test("names the assertions the grader dropped", () => {
    // A dropped verdict used to become a fabricated failure, which reads like a finding.
    assert.deepEqual(missingAssertionIds(batch, [{ id: "a" }]), ["b", "c"]);
  });

  test("treats a missing or malformed assertion list as everything missing", () => {
    assert.deepEqual(missingAssertionIds(batch, undefined), ["a", "b", "c"]);
    assert.deepEqual(missingAssertionIds(batch, [null]), ["a", "b", "c"]);
  });
});

describe("extractGradingText — budget exhaustion", () => {
  test("names max_tokens as the cause when only thinking came back", () => {
    // The realistic failure on a thinking-by-default grader: the whole budget went on
    // reasoning and the verdict was never written. The error must point at the budget.
    const data = {
      stop_reason: "max_tokens",
      content: [{ type: "thinking", thinking: "", signature: "abc" }],
    };
    assert.throws(() => extractGradingText(data), /stop_reason=max_tokens/);
    assert.throws(() => extractGradingText(data), /budget was exhausted/);
  });
});

// --- who pays for generation ----------------------------------------------
//
// Generation is ~90% of a run's cost and already goes through the `claude` CLI, so the
// only thing deciding whether it bills the API is whether the child process can see an
// API key. That makes this a one-line switch with a silent failure mode: pass the key by
// accident and the run is billed without saying so.

describe("generationEnv", () => {
  test("withholds the API key so the agent authenticates with the subscription", () => {
    const env = generationEnv({ ANTHROPIC_API_KEY: "sk-test", PATH: "/usr/bin" });

    assert.equal("ANTHROPIC_API_KEY" in env, false);
    assert.equal(env.PATH, "/usr/bin");
  });

  test("passes the key through with --api-generation, for a machine with no subscription", () => {
    const env = generationEnv({ ANTHROPIC_API_KEY: "sk-test" }, { apiGeneration: true });

    assert.equal(env.ANTHROPIC_API_KEY, "sk-test");
  });

  test("points at the local server for ollama and still withholds the key", () => {
    const env = generationEnv({ ANTHROPIC_API_KEY: "sk-test" }, { provider: "ollama" });

    assert.equal(env.ANTHROPIC_BASE_URL, "http://localhost:11434");
    assert.equal(env.ANTHROPIC_AUTH_TOKEN, "ollama");
    assert.equal("ANTHROPIC_API_KEY" in env, false);
  });

  test("does not mutate the environment it was given", () => {
    const base = { ANTHROPIC_API_KEY: "sk-test" };
    generationEnv(base);

    assert.equal(base.ANTHROPIC_API_KEY, "sk-test");
  });
});

// --- a generation failure is unknown, not zero ------------------------------
//
// iteration-49's eval-20 timed out, was scored 0/17, dropped the total from 71 to 54 and
// produced 17 phantom moved verdicts — one per assertion it owns. A failed generation
// produced no answer, so there is nothing to compare and nothing to average. It is excluded
// the way a changed definition is, and named in the report so the gap is visible.

describe("evals whose generation failed", () => {
  const evalEntry = (id, results) => ({ id, fingerprint: "abc", results });
  const scored = (passed, total, failed = []) => ({
    assertions_passed: passed, assertions_total: total, failed_assertions: failed,
    total_tokens: 0, duration_ms: 0, cost_usd: 0,
  });
  const errored = (total, failed) => ({ ...scored(0, total, failed), error: "Timed out after 900000ms" });

  test("are not comparable, so their assertions raise no regressions", () => {
    const benchmark = { evals: [evalEntry("eval-20-tags", errored(17, ["a", "b"]))] };
    const baseline = { evals: [evalEntry("eval-20-tags", scored(17, 17))] };

    const [pair] = pairedWithBaseline(benchmark, baseline);

    assert.equal(pair.comparable, false);
    assert.equal(pair.reason, "generation-failed");
    assert.deepEqual(detectRegressions(benchmark, baseline).regressions, []);
  });

  test("are not comparable when the failure is on the baseline side either", () => {
    const benchmark = { evals: [evalEntry("eval-20-tags", scored(17, 17))] };
    const baseline = { evals: [evalEntry("eval-20-tags", errored(17, ["a"]))] };

    const [pair] = pairedWithBaseline(benchmark, baseline);

    assert.equal(pair.comparable, false);
    assert.equal(pair.reason, "generation-failed");
    assert.deepEqual(detectRegressions(benchmark, baseline).improvements, []);
  });

  test("still compare normally when neither side errored", () => {
    const benchmark = { evals: [evalEntry("eval-20-tags", scored(16, 17, ["a"]))] };
    const baseline = { evals: [evalEntry("eval-20-tags", scored(17, 17))] };

    const [pair] = pairedWithBaseline(benchmark, baseline);

    assert.equal(pair.comparable, true);
    assert.equal(detectRegressions(benchmark, baseline).regressions.length, 1);
  });

  test("are left out of the summary totals rather than counted as zero", () => {
    const summary = summariseEvals([
      evalEntry("eval-7-permission", scored(12, 13)),
      evalEntry("eval-20-tags", errored(17, ["a"])),
    ]);

    assert.equal(summary.assertions_passed, 12);
    assert.equal(summary.assertions_total, 13);
    assert.deepEqual(summary.errored_evals, ["eval-20-tags"]);
  });

  test("report no errored evals when every generation succeeded", () => {
    const summary = summariseEvals([evalEntry("eval-7-permission", scored(13, 13))]);

    assert.deepEqual(summary.errored_evals, []);
    assert.equal(summary.assertions_total, 13);
  });
});

describe("loadOfficialBenchmark with a failed generation in the newest iteration", () => {
  let root;
  beforeEach(() => { root = fs.mkdtempSync(path.join(os.tmpdir(), "official-test-")); });
  afterEach(() => { fs.rmSync(root, { recursive: true, force: true }); });

  function writeIteration(n, evals) {
    const dir = path.join(root, "iterations", "tabletest", `iteration-${n}`);
    fs.mkdirSync(dir, { recursive: true });
    fs.writeFileSync(path.join(dir, "benchmark.json"), JSON.stringify({ evals }));
  }

  test("keeps the older real result rather than inheriting the error", () => {
    writeIteration(47, [{ id: "eval-20-tags", results: { assertions_passed: 17, assertions_total: 17 } }]);
    writeIteration(49, [{ id: "eval-20-tags", results: { assertions_passed: 0, assertions_total: 17, error: "Timed out" } }]);

    const merged = loadOfficialBenchmark(root, "tabletest");
    const entry = merged.evals.find((e) => e.id === "eval-20-tags");

    assert.equal(entry.results.assertions_passed, 17);
    assert.equal(entry.results.error, undefined);
  });
});

describe("extractGradingThinking", () => {
  test("returns null when the response carries no thinking block", () => {
    assert.equal(extractGradingThinking({ content: [{ type: "text", text: "{}" }] }), null);
  });

  test("returns the reasoning when the response carries one", () => {
    const data = { content: [{ type: "thinking", thinking: "weighing clause (2)" }, { type: "text", text: "{}" }] };
    assert.equal(extractGradingThinking(data), "weighing clause (2)");
  });

  test("joins several thinking blocks in order", () => {
    const data = {
      content: [
        { type: "thinking", thinking: "first" },
        { type: "thinking", thinking: "second" },
        { type: "text", text: "{}" },
      ],
    };
    assert.equal(extractGradingThinking(data), "first\n\nsecond");
  });

  test("is safe on a malformed response rather than throwing, since it never gates a verdict", () => {
    assert.equal(extractGradingThinking(null), null);
    assert.equal(extractGradingThinking({}), null);
    assert.equal(extractGradingThinking({ content: [{ type: "thinking" }] }), null);
  });
});

describe("--capture-thinking", () => {
  const base = { gradingModel: "sonnet", parallel: 1, gradingSuffix: null, provider: "anthropic", gradeRuns: 1 };

  test("is off by default, so the request carries no thinking parameter", () => {
    assert.equal(parseArgs(["node", "x", "--skill", "tabletest", "--iteration", "1"]).captureThinking, false);
  });

  test("is set by the flag", () => {
    assert.equal(
      parseArgs(["node", "x", "--skill", "tabletest", "--iteration", "1", "--capture-thinking"]).captureThinking,
      true
    );
  });
});

describe("harvesting a failed generation", () => {
  const { harvestGeneratedFiles } = require("./run-evals.js");

  test("copies the files the agent wrote before it ran out of budget", (t) => {
    if (!harvestGeneratedFiles) return t.skip("harvestGeneratedFiles not exported");
    const root = fs.mkdtempSync(path.join(os.tmpdir(), "harvest-"));
    const agentCwd = path.join(root, "work");
    const evalDir = path.join(root, "eval");
    fs.mkdirSync(path.join(agentCwd, "src/test/java/com/example"), { recursive: true });
    fs.mkdirSync(path.join(evalDir, "outputs"), { recursive: true });
    fs.writeFileSync(path.join(agentCwd, "src/test/java/com/example/FeeTest.java"), "class FeeTest {}");
    fs.writeFileSync(path.join(agentCwd, "pom.xml"), "<project/>");

    harvestGeneratedFiles(agentCwd, evalDir, { id: 1, slug: "x", skill: "tabletest" });

    assert.ok(fs.existsSync(path.join(evalDir, "outputs/src/test/java/com/example/FeeTest.java")),
      "a test file written before the timeout must survive — the skills promise it is a checkpoint");
    assert.ok(fs.existsSync(path.join(evalDir, "outputs/pom.xml")));
    fs.rmSync(root, { recursive: true });
  });
});

describe("classifying a generation failure", () => {
  const { classifyGenerationFailure, lastAssistantText } = require("./run-evals.js");

  const withOutputs = (files) => {
    const root = fs.mkdtempSync(path.join(os.tmpdir(), "classify-"));
    for (const [rel, body] of Object.entries(files)) {
      const dest = path.join(root, "outputs", rel);
      fs.mkdirSync(path.dirname(dest), { recursive: true });
      fs.writeFileSync(dest, body);
    }
    fs.mkdirSync(path.join(root, "outputs"), { recursive: true });
    return root;
  };
  const evalDef = { id: 1, slug: "x", skill: "tabletest", assertions: [] };
  const delivered = { "src/test/java/T.java": "class T { @TableTest(\"\"\"a|b\"\"\") void t() {} }" };

  test("a timeout that delivered test code is countable — that is the whole point", () => {
    const dir = withOutputs(delivered);
    const r = classifyGenerationFailure({ message: "Timed out after 900000ms", stdout: "" }, dir, evalDef);
    assert.equal(r.kind, "timeout-after-delivery");
    assert.equal(r.countable, true);
    fs.rmSync(dir, { recursive: true });
  });

  test("a timeout that delivered nothing is not countable", () => {
    const dir = withOutputs({});
    const r = classifyGenerationFailure({ message: "Timed out after 900000ms", stdout: "" }, dir, evalDef);
    assert.equal(r.kind, "timeout-no-delivery");
    assert.equal(r.countable, false);
    fs.rmSync(dir, { recursive: true });
  });

  test("a dropped connection is never countable, however much it delivered", () => {
    const dir = withOutputs(delivered);
    const stdout = JSON.stringify({ type: "system", subtype: "api_retry", attempt: 1 });
    const r = classifyGenerationFailure({ message: "Timed out after 900000ms", stdout }, dir, evalDef);
    assert.equal(r.kind, "timeout-after-api-retry");
    assert.equal(r.countable, false, "network weather must never be attributed to the skill");
    fs.rmSync(dir, { recursive: true });
  });

  test("a crash is not a timeout", () => {
    const dir = withOutputs(delivered);
    const r = classifyGenerationFailure({ message: "spawn ENOENT", stdout: "" }, dir, evalDef);
    assert.equal(r.kind, "crash");
    assert.equal(r.countable, false);
    fs.rmSync(dir, { recursive: true });
  });

  test("recovers the agent's last visible prose from a run that never returned a result", () => {
    const lines = [
      JSON.stringify({ message: { role: "assistant", content: [{ type: "text", text: "first" }] } }),
      JSON.stringify({ message: { role: "assistant", content: [{ type: "text", text: "last" }] } }),
    ].join("\n");
    assert.equal(lastAssistantText(lines), "last");
    assert.equal(lastAssistantText(""), "");
  });
});

describe("contaminationHits", () => {
  const toolCall = (name, input) => JSON.stringify({
    type: "assistant",
    message: { content: [{ type: "tool_use", name, input }] },
  });

  test("reports an agent that reached the host checkout the worktree was copied from", () => {
    const jsonl = toolCall("Bash", { command: 'grep -rl "x" /repo/evals/tabletest' });
    const hits = contaminationHits(jsonl, { repoRoot: "/repo", configDir: "/tmp/cfg" });
    assert.deepEqual(hits.map((h) => h.kind), ["host-checkout"]);
    assert.equal(hits[0].tool, "Bash");
  });

  test("reports a read of an answer key", () => {
    const jsonl = toolCall("Read", { file_path: "/elsewhere/eval-1/expected_output.md" });
    const hits = contaminationHits(jsonl, { repoRoot: "/repo", configDir: "/tmp/cfg" });
    assert.deepEqual(hits.map((h) => h.kind), ["answer-key"]);
  });

  test("stays silent on a run that only touched its own workspace", () => {
    const jsonl = [
      toolCall("Read", { file_path: "/tmp/wt/eval-1-work/src/Main.java" }),
      toolCall("Bash", { command: "gradle test" }),
    ].join("\n");
    assert.deepEqual(contaminationHits(jsonl, { repoRoot: "/repo", configDir: "/tmp/cfg" }), []);
  });

  test("watches the memory directory only when the run was not given an isolated config dir", () => {
    const jsonl = toolCall("Bash", { command: "cat /Users/x/.claude/projects/p/memory/m.md" });
    assert.equal(contaminationHits(jsonl, { repoRoot: "/repo", configDir: null }).length, 1);
    assert.equal(contaminationHits(jsonl, { repoRoot: "/repo", configDir: "/tmp/cfg" }).length, 0);
  });

  test("ignores the pattern outside a tool call, so a prompt mentioning it is not a breach", () => {
    const jsonl = JSON.stringify({
      type: "assistant",
      message: { content: [{ type: "text", text: "I will not read expected_output.md" }] },
    });
    assert.deepEqual(contaminationHits(jsonl, { repoRoot: "/repo", configDir: "/tmp/cfg" }), []);
  });
});

describe("generationEnv config isolation", () => {
  test("points the agent at the run's own config dir, so it cannot reach this project's memory", () => {
    const env = generationEnv({ PATH: "/bin" }, { configDir: "/tmp/eval-x-config" });
    assert.equal(env.CLAUDE_CONFIG_DIR, "/tmp/eval-x-config");
  });

  test("leaves the variable alone when no config dir is given, as in a grade-only run", () => {
    const env = generationEnv({ PATH: "/bin" }, {});
    assert.equal("CLAUDE_CONFIG_DIR" in env, false);
  });
});
