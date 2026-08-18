const { test, describe } = require("node:test");
const assert = require("node:assert/strict");
const path = require("path");
const os = require("node:os");

const fs = require("fs");

const {
  disagreements,
  evalDir,
  gradesAnOlderSource,
  parseArgs,
  storedDraws,
  sutParameterNames,
  selfGradedAssertions,
} = require("./shape-report.js");

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


describe("gradesAnOlderSource", () => {
  const scratch = fs.mkdtempSync(path.join(os.tmpdir(), "shape-report-"));
  const draw = (name, gradingOffsetMs, sourceOffsetMs) => {
    const dir = path.join(scratch, name);
    fs.mkdirSync(dir, { recursive: true });
    const grading = path.join(dir, "grading.json");
    const source = path.join(dir, "Answer.java");
    fs.writeFileSync(grading, "{}");
    fs.writeFileSync(source, "class Answer {}");
    const base = Date.now();
    fs.utimesSync(grading, new Date(base + gradingOffsetMs), new Date(base + gradingOffsetMs));
    fs.utimesSync(source, new Date(base + sourceOffsetMs), new Date(base + sourceOffsetMs));
    return { grading, sources: [source] };
  };

  test("says nothing when a run wrote both files in the same pass", () => {
    // Measured across the corpus: 29 of 30 such draws differ by 16 ms or less, and the order
    // within the second is incidental.
    const { grading, sources } = draw("same-run", 0, 16);
    assert.equal(gradesAnOlderSource(grading, sources), false);
  });

  test("names a grading written before an answer that was later edited", () => {
    // eval-7's reference: graded, edited 42 seconds later, regraded under a suffix.
    const { grading, sources } = draw("edited-after", 0, 42_000);
    assert.equal(gradesAnOlderSource(grading, sources), true);
  });

  test("says nothing when the grading is the newer file, which is the ordinary case", () => {
    const { grading, sources } = draw("graded-after", 5_000, 0);
    assert.equal(gradesAnOlderSource(grading, sources), false);
  });
});

describe("selfGradedAssertions", () => {
  test("names the assertions the eval now grades with its own relation", () => {
    const scratch = fs.mkdtempSync(path.join(os.tmpdir(), "self-graded-"));
    fs.writeFileSync(
      path.join(scratch, "eval.json"),
      JSON.stringify({
        assertions: [
          { id: "converted", type: "deterministic" },
          { id: "still-judged", type: "llm" },
          { id: "untyped-is-llm" },
        ],
      }),
    );
    const converted = selfGradedAssertions(scratch);
    assert.deepEqual([...converted], ["converted"]);
  });

  test("returns nothing for a directory with no eval definition", () => {
    const scratch = fs.mkdtempSync(path.join(os.tmpdir(), "self-graded-"));
    assert.equal(selfGradedAssertions(scratch).size, 0);
  });
});
