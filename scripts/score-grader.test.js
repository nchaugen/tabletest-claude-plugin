const { test, describe } = require("node:test");
const assert = require("node:assert/strict");

const { keyAppliesTo } = require("./score-grader.js");

// The key's verdicts were read off one specific set of stored outputs. Scoring a different
// iteration does not error — it returns a confident percentage about outputs nobody graded.
describe("keyAppliesTo", () => {
  const key = { scored_against: { iteration: "iterations/tabletest/iteration-40" } };

  test("accepts the iteration the key was read against", () => {
    assert.equal(keyAppliesTo(key, "tabletest", "40").ok, true);
  });

  test("refuses an iteration whose outputs the key never described", () => {
    const applies = keyAppliesTo(key, "tabletest", "41");
    assert.equal(applies.ok, false);
    assert.equal(applies.boundTo, "iterations/tabletest/iteration-40");
    assert.equal(applies.requested, "iterations/tabletest/iteration-41");
  });

  test("refuses a different skill's iteration of the same number", () => {
    assert.equal(keyAppliesTo(key, "other-skill", "40").ok, false);
  });

  test("accepts anything when a key declares no binding, rather than blocking older keys", () => {
    assert.equal(keyAppliesTo({}, "tabletest", "41").ok, true);
  });
});
