const { test, describe } = require("node:test");
const assert = require("node:assert");

const fs = require("node:fs");
const path = require("node:path");

const { relationChecker } = require("./relation-checkers.js");
const { evalDir, evaluateDraw, sutParameterNames } = require("./shape-report.js");
const { relationsFor } = require("./shape-relations.js");

/** eval-14's shape: hours columns, a rate, and a pay expectation. */
const weeklyPay = (rows, signature = "Integer sunday, Integer holiday, double rate") => `
  @TableTest("""
    Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Pay?
    ${rows.join("\n    ")}
    """)
  void calculatesWeeklyPay(int weekday, ${signature}, double pay) {
    assertEquals(pay, calculator.calculateWeeklyPay(weekday, sunday, holiday, rate));
  }
`;

describe("relation-backed checkers", () => {
  test("passes an answer the eval's relation holds for", () => {
    const source = weeklyPay([
      "Weekday hours only          | 10 |    |    | 20.00 | 200.00",
      "Any hours at a zero rate    | {1, 41, 100} |  |  | 0.00 | 0.00",
    ]);
    const result = relationChecker("1.14-depth-zero-rate", 14, source);
    assert.equal(result.passed, true, result.evidence);
  });

  test("fails an answer the relation does not hold for, and says why", () => {
    const source = weeklyPay(["Weekday hours only | 10 |  |  | 20.00 | 200.00"]);
    const result = relationChecker("1.14-depth-zero-rate", 14, source);
    assert.equal(result.passed, false);
    assert.ok(result.evidence.length > 0, "a failing verdict must carry its evidence");
  });

  test("refuses an assertion the eval has no relation for, rather than passing it", () => {
    const result = relationChecker("no-such-assertion", 14, weeklyPay(["A | 1 |  |  | 1 | 1"]));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /no relation/i);
  });

  test("refuses an eval that has no relations at all", () => {
    const result = relationChecker("1.14-depth-zero-rate", 999, weeklyPay(["A | 1 |  |  | 1 | 1"]));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /no relation/i);
  });

  test("refuses a relation carrying an exemption it cannot decide", () => {
    // eval-22's rule-falsifiable-by-a-row is marked `judgement`: its stated-invariance exemption
    // is a reading of the title and description, so its verdict is advisory and must never grade.
    const result = relationChecker("rule-falsifiable-by-a-row", 22, "@TableTest(\"\"\"\nA | B?\n1 | 2\n\"\"\")\nvoid t(int a, int b) {}");
    assert.equal(result.passed, false);
    assert.match(result.evidence, /exemption|advisory|judgement/i);
  });
});

describe("the context a relation is evaluated with", () => {
  /** eval-18's reference answer, the one artefact whose verdicts are pinned elsewhere. */
  const referenceSource = () => {
    const base = path.join(__dirname, "..", "iterations", "tabletest", "reference");
    for (const iteration of fs.readdirSync(base)) {
      const outputs = path.join(base, iteration, "eval-18-convert-from-code", "outputs");
      if (!fs.existsSync(outputs)) continue;
      const files = [];
      (function walk(dir) {
        for (const entry of fs.readdirSync(dir)) {
          const full = path.join(dir, entry);
          if (fs.statSync(full).isDirectory()) walk(full);
          else if (/\.(java|kt)$/.test(entry)) files.push(fs.readFileSync(full, "utf8"));
        }
      })(outputs);
      if (files.length > 0) return files.join("\n\n");
    }
    return null;
  };

  test("a relation reading the system under test's parameters gets them, not an empty context", (t) => {
    const source = referenceSource();
    if (!source) return t.skip("eval-18 reference answer not in the tree");

    // Graded without the context, these relations return the wrong verdict silently — which is what
    // failed `premium-claim-boundary` against a stored PASS before the context was passed through.
    for (const id of ["premium-charge-is-per-claim", "premium-claim-boundary", "premium-age-is-banded"]) {
      const result = relationChecker(id, 18, source, "tabletest");
      assert.equal(result.passed, true, `${id}: the reference answer must pass — ${result.evidence}`);
    }
  });

  test("grades a slot exactly as the shape report reads it, so the two cannot diverge", (t) => {
    const source = referenceSource();
    if (!source) return t.skip("eval-18 reference answer not in the tree");

    const authored = relationsFor(18);
    const context = { sutParameters: sutParameterNames(evalDir("tabletest", 18), authored.call) };
    const rows = evaluateDraw({ source, graded: new Map() }, authored.relations, context);
    for (const row of rows.filter((one) => !one.judgement)) {
      const graded = relationChecker(row.id, 18, source, "tabletest");
      assert.equal(graded.passed, row.holds, `${row.id} grades differently from how the report reads it`);
    }
  });
});
