const { test, describe } = require("node:test");
const assert = require("node:assert");

const { relationChecker } = require("./relation-checkers.js");

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
