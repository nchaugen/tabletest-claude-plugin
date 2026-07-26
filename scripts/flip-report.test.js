const { test, describe } = require("node:test");
const assert = require("node:assert/strict");

const { flipReport, flipsByAssertion } = require("./flip-report.js");

const run = (...slots) => ({ failedSlots: slots });

describe("flipReport", () => {
  test("reports a slot that failed in some runs and passed in others", () => {
    const { flipped } = flipReport([run("29\tquantifier"), run(), run()]);
    assert.equal(flipped.length, 1);
    assert.deepEqual(flipped[0], {
      evalNumber: "29",
      assertion: "quantifier",
      pattern: [true, false, false],
    });
  });

  test("counts a slot failing in every run as a stable failure, not a flip", () => {
    const { flipped, stableFailures } = flipReport([
      run("14\tminimal-rows"),
      run("14\tminimal-rows"),
      run("14\tminimal-rows"),
    ]);
    assert.deepEqual(flipped, []);
    assert.equal(stableFailures, 1);
  });

  // A slot passing everywhere never enters the map at all — the probe measures disagreement, and a
  // unanimous pass is agreement just as much as a unanimous failure.
  test("ignores a slot that passed in every run", () => {
    const { flipped, stableFailures } = flipReport([run(), run(), run()]);
    assert.deepEqual(flipped, []);
    assert.equal(stableFailures, 0);
  });

  test("keeps the pattern aligned with the order the runs were given in", () => {
    const { flipped } = flipReport([run(), run("30\theld-constants"), run("30\theld-constants")]);
    assert.deepEqual(flipped[0].pattern, [false, true, true]);
  });

  test("orders slots so the report is stable across invocations", () => {
    const { flipped } = flipReport([run("30\tb", "14\ta"), run()]);
    assert.deepEqual(flipped.map((f) => f.evalNumber), ["14", "30"]);
  });
});

describe("flipsByAssertion", () => {
  test("ranks the assertions that flip on the most evals first", () => {
    const { flipped } = flipReport([run("14\twobbly", "22\twobbly", "29\tsteady"), run()]);
    assert.deepEqual(flipsByAssertion(flipped), [
      ["wobbly", 2],
      ["steady", 1],
    ]);
  });
});
