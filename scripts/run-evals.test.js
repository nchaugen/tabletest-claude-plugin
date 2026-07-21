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
  GradingIncompleteError,
  postGradingRequest,
  isRetryableStatus,
  retryDelayMs,
  unwrapResults,
  unwrapSummary,
  parseEvalIds,
  resolveModel,
  computeEvalFingerprint,
  fingerprintsDiffer,
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

  test("passes a single sample through untouched", () => {
    const sample = [assertion("a1", true)];
    assert.deepEqual(majorityVote([sample], batch), sample);
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

describe("gradeResponses", () => {
  // A grading failure must stop the run. An ungraded eval is not scored zero — generation
  // succeeded, so there is no timing.error — it just leaves the totals, and the run reads as
  // a smaller suite that scored differently.
  const GOOD_GRADING = {
    content: [{ text: '{"assertions":[{"id":"a1","text":"t","passed":true,"evidence":"e"}]}' }],
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
