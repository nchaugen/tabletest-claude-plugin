const { test, describe } = require("node:test");
const assert = require("node:assert/strict");

const { confoundedEvals, scoreOf, parseArgs, priorVerdicts, baseRateNote } = require("./compare-official.js");
const { loadOfficialBenchmark } = require("./run-evals.js");
const path = require("path");

const repoRoot = path.join(__dirname, "..");

describe("confoundedEvals", () => {
  test("names an eval whose before-draw came from a different skill state", () => {
    const pairs = [{ eval: "eval-18", from: "iteration-80", baselineDigest: "1d5b863abf" }];
    assert.deepEqual(confoundedEvals(pairs, "1e0dfc3c68"), [
      { eval: "eval-18", from: "iteration-80", digest: "1d5b863abf" },
    ]);
  });

  test("stays silent where the before-draw is the state under test", () => {
    const pairs = [{ eval: "eval-18", from: "iteration-88", baselineDigest: "1e0dfc3c68" }];
    assert.deepEqual(confoundedEvals(pairs, "1e0dfc3c68"), []);
  });

  test("says nothing about an eval with no before-draw, which is an exclusion rather than a confound", () => {
    const pairs = [{ eval: "eval-18", from: null, baselineDigest: null }];
    assert.deepEqual(confoundedEvals(pairs, "1e0dfc3c68"), []);
  });
});

describe("scoreOf", () => {
  test("renders a graded eval as passed over total", () => {
    assert.equal(scoreOf({ results: { assertions_passed: 22, assertions_total: 34 } }), "22/34");
  });

  test("returns null for an eval whose generation failed, so the caller can mark it absent", () => {
    assert.equal(scoreOf({ results: { error: "timeout" } }), null);
    assert.equal(scoreOf(undefined), null);
  });
});

describe("loadOfficialBenchmark with excludeIteration", () => {
  test("leaves the named iteration out, so a run is not compared against itself", () => {
    const newest = loadOfficialBenchmark(repoRoot, "tabletest");
    const source = newest.evals.find((e) => e.id === "eval-18-convert-from-code")._fromIteration;
    const excluded = loadOfficialBenchmark(repoRoot, "tabletest", {
      excludeIteration: parseInt(source.split("-")[1], 10),
    });
    assert.notEqual(excluded.evals.find((e) => e.id === "eval-18-convert-from-code")._fromIteration, source);
  });

  test("is unchanged when no iteration is excluded", () => {
    const explicit = loadOfficialBenchmark(repoRoot, "tabletest", {});
    const implicit = loadOfficialBenchmark(repoRoot, "tabletest");
    assert.deepEqual(explicit.evals.map((e) => e._fromIteration), implicit.evals.map((e) => e._fromIteration));
  });
});

describe("baseRateNote", () => {
  test("warns where the slot has gone both ways before", () => {
    assert.match(baseRateNote(["F", "P", "F"]), /prior FPF — moves on its own/);
  });

  test("states the record plainly where every prior draw agrees", () => {
    assert.equal(baseRateNote(["F", "F", "F"]), "prior FFF");
  });

  test("says so when the slot has no prior draw to judge against", () => {
    assert.match(baseRateNote([]), /no prior draw/);
  });
});

describe("priorVerdicts", () => {
  test("reads a real slot's record, excluding the run being judged", () => {
    const prior = priorVerdicts(
      "tabletest", "eval-18-convert-from-code", "c9ea8e1ad891", "premium-charge-is-per-claim", 89
    );
    assert.deepEqual(prior, ["F", "P"]);
  });

  test("ignores draws that used a different eval definition", () => {
    const prior = priorVerdicts(
      "tabletest", "eval-18-convert-from-code", "c9ea8e1ad891", "rule-falsifiable-by-a-row", 89
    );
    assert.ok(prior.length < 8, `expected only same-fingerprint draws, got ${prior.length}`);
  });
});

describe("parseArgs", () => {
  test("reads the skill and iteration", () => {
    assert.deepEqual(parseArgs(["--skill", "tabletest", "--iteration", "89"]), {
      skill: "tabletest",
      iteration: "89",
    });
  });
});
