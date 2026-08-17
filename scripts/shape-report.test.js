const { test, describe } = require("node:test");
const assert = require("node:assert/strict");
const path = require("path");

const { disagreements, evalDir, parseArgs, storedDraws, sutParameterNames } = require("./shape-report.js");

describe("sutParameterNames", () => {
  test("reads the method under test's parameters from the eval's own project", () => {
    assert.deepEqual(sutParameterNames(evalDir("tabletest", 18), "evaluateApplication"), [
      "applicantType",
      "age",
      "claimCount",
    ]);
  });

  test("returns none for an eval whose project ships no implementation to call", () => {
    assert.deepEqual(sutParameterNames(evalDir("tabletest", 14), "calculateWeeklyPay"), []);
  });

  test("returns none where no call name is configured", () => {
    assert.deepEqual(sutParameterNames(evalDir("tabletest", 18), null), []);
  });
});

describe("storedDraws", () => {
  test("finds every stored answer for an eval, with the reference among them", () => {
    const draws = storedDraws("tabletest", "eval-18-convert-from-code");
    assert.ok(draws.length >= 12, `expected at least 12 draws, got ${draws.length}`);
    assert.equal(draws.filter((draw) => draw.isReference).length, 1);
    assert.ok(draws.every((draw) => draw.source.includes("@TableTest")));
  });

  test("returns nothing for a slug no iteration carries", () => {
    assert.deepEqual(storedDraws("tabletest", "eval-999-nothing"), []);
  });
});

describe("disagreements", () => {
  test("reports only the rows where the grader and the mechanical read differ", () => {
    const draws = [{ label: "iteration-1" }];
    const evaluations = [
      [
        { id: "a", holds: true, graded: true, agrees: true },
        { id: "b", holds: false, graded: true, agrees: false },
        { id: "c", holds: true, graded: null, agrees: null },
      ],
    ];
    assert.deepEqual(
      disagreements(draws, evaluations).map((one) => one.id),
      ["b"],
    );
  });
});

describe("evalDir and parseArgs", () => {
  test("resolves an eval number to its slugged directory", () => {
    assert.equal(path.basename(evalDir("tabletest", 18)), "eval-18-convert-from-code");
  });

  test("returns null for an eval number the suite does not have", () => {
    assert.equal(evalDir("tabletest", 999), null);
  });

  test("defaults the skill and reads the eval number", () => {
    assert.deepEqual(parseArgs(["--eval", "18"]), { skill: "tabletest", eval: 18, json: false });
  });
});
