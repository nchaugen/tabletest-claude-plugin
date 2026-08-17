const { test, describe } = require("node:test");
const assert = require("node:assert/strict");

const { confoundedEvals, scoreOf, parseArgs } = require("./compare-official.js");
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
    const withAll = loadOfficialBenchmark(repoRoot, "tabletest");
    const without88 = loadOfficialBenchmark(repoRoot, "tabletest", { excludeIteration: 88 });
    const from = (benchmark, id) => benchmark.evals.find((e) => e.id === id)._fromIteration;
    assert.equal(from(withAll, "eval-18-convert-from-code"), "iteration-88");
    assert.notEqual(from(without88, "eval-18-convert-from-code"), "iteration-88");
  });

  test("is unchanged when no iteration is excluded", () => {
    const explicit = loadOfficialBenchmark(repoRoot, "tabletest", {});
    const implicit = loadOfficialBenchmark(repoRoot, "tabletest");
    assert.deepEqual(explicit.evals.map((e) => e._fromIteration), implicit.evals.map((e) => e._fromIteration));
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
