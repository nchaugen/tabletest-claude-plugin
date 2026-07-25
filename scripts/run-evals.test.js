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
  parseLlmGrading,
  repairGradingJson,
  extractJson,
  majorityVote,
  gradeResponses,
  regradeCommand,
  rebuildCommand,
  evalsMissingGrading,
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
  missingAssertionIds,
  computeEvalFingerprint,
  fingerprintsDiffer,
  digestDirectory,
  skillProvenance,
  inheritedProvenance,
  analysisBaselineOf,
  movedAssertions,
  analysisTodoMarkdown,
  narrationMarkdown,
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
});

describe("inheritedProvenance", () => {
  let iterationDir;
  beforeEach(() => { iterationDir = fs.mkdtempSync(path.join(os.tmpdir(), "inherit-test-")); });
  afterEach(() => { fs.rmSync(iterationDir, { recursive: true, force: true }); });

  function writeBenchmark(name, body) {
    fs.writeFileSync(path.join(iterationDir, name), JSON.stringify(body));
  }

  test("carries the generating run's provenance forward", () => {
    writeBenchmark("benchmark.json", { skill_commit: "abc123", skill_digest: "deadbeef" });

    assert.deepEqual(inheritedProvenance(iterationDir, ""), {
      skill_commit: "abc123",
      skill_digest: "deadbeef",
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

describe("analysisTodoMarkdown", () => {
  const context = { iteration: 6, label: "tabletest variant=next", baselineLabel: "official iteration 40" };

  test("gives every moved verdict an artefact list and an unfilled cause", () => {
    const md = analysisTodoMarkdown(
      [{ eval: "eval-18-y", assertion: "rule-falsifiable-by-a-row", direction: "lost" }],
      context
    );
    assert.match(md, /LOST `rule-falsifiable-by-a-row` — eval-18-y/);
    assert.match(md, /eval-18-y\/outputs\//);
    assert.match(md, /eval-18-y\/narration\.md/);
    assert.match(md, /Cause \(from artefact\): *$/m);
  });

  test("quotes the grader's own words when they are available, as a claim to check", () => {
    const md = analysisTodoMarkdown(
      [{ eval: "eval-18-y", assertion: "a", direction: "lost" }],
      context,
      () => "both rows have identical inputs"
    );
    assert.match(md, /Grader said: _both rows have identical inputs_/);
  });

  test("survives a grading file it cannot read", () => {
    const md = analysisTodoMarkdown(
      [{ eval: "eval-18-y", assertion: "a", direction: "lost" }],
      context,
      () => null
    );
    assert.doesNotMatch(md, /Grader said/);
  });

  test("says there is nothing to attribute when no verdict moved", () => {
    const md = analysisTodoMarkdown([], context);
    assert.match(md, /No assertion verdicts moved/);
    assert.doesNotMatch(md, /Cause \(from artefact\)/);
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
