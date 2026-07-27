const { test, describe } = require("node:test");
const assert = require("node:assert");

const { costsByEval } = require("./eval-costs.js");

const benchmark = (evals) => ({ evals });
const evalResult = (id, costUsd, gradingUsd, durationMs) => ({
  id,
  results: {
    cost_usd: costUsd,
    duration_ms: durationMs,
    grading_usage: gradingUsd === null ? null : { cost_usd: gradingUsd },
  },
});

describe("costsByEval", () => {
  test("orders evals cheapest first, so a loop can be picked by price", () => {
    const costs = costsByEval([
      benchmark([evalResult("eval-30-dear", 1.75, 0.19, 548000), evalResult("eval-7-cheap", 0.35, 0.02, 58000)]),
    ]);

    assert.deepEqual(
      costs.map((entry) => entry.id),
      ["eval-7-cheap", "eval-30-dear"],
    );
  });

  test("averages an eval across every run that measured it", () => {
    const costs = costsByEval([
      benchmark([evalResult("eval-14", 0.80, 0.10, 200000)]),
      benchmark([evalResult("eval-14", 1.00, 0.20, 300000)]),
    ]);

    assert.equal(costs[0].runs, 2);
    assert.equal(costs[0].generation.toFixed(2), "0.90");
    assert.equal(costs[0].grading.toFixed(2), "0.15");
    assert.equal(costs[0].seconds, 250);
  });

  test("reports grading as unknown rather than free when no run recorded it", () => {
    const costs = costsByEval([benchmark([evalResult("eval-14", 0.80, null, 200000)])]);

    assert.equal(costs[0].grading, null);
    assert.equal(costs[0].total, 0.80);
  });

  test("averages grading over the runs that recorded it, ignoring the ones that did not", () => {
    const costs = costsByEval([
      benchmark([evalResult("eval-14", 0.80, null, 200000)]),
      benchmark([evalResult("eval-14", 0.80, 0.30, 200000)]),
    ]);

    assert.equal(costs[0].grading, 0.30);
  });

  test("has nothing to say about an empty set of benchmarks", () => {
    assert.deepEqual(costsByEval([]), []);
  });
});
