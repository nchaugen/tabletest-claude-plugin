const { test, describe } = require("node:test");
const assert = require("node:assert/strict");

const { answerShape } = require("./answer-shape.js");
const { storedDraws, evaluateDraw } = require("./shape-report.js");
const {
  EVAL_14_RELATIONS,
  EVAL_18_RELATIONS,
  authoredEvals,
  blankHourColumns,
  combinedScenario,
  errorEdgeCases,
  implementationHeaders,
  overtimeBoundary,
  findInputColumn,
  heldBandValue,
  hourColumns,
  payCases,
  payColumn,
  payForRow,
  rejectionExpectationColumn,
  zeroRateRow,
  ageBoundaryPair,
  bandedAgePair,
  claimBoundaryPair,
  claimEffectBands,
  perClaimTriple,
  internalColumns,
  policyColumns,
  premiumCases,
  relationsFor,
  tablesAssertingBoth,
} = require("./shape-relations.js");

/** eval-18's method under test declares these parameters, in this order. */
const SUT = ["applicantType", "age", "claimCount"];

const premiumTable = (rows, header = "Scenario | Age | Claim Count | Premium?") => `
    @TableTest("""
        ${header}
${rows.map((row) => `        ${row}`).join("\n")}
        """)
    void prices(int age, int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);
        assertEquals(premium, result.premium());
    }
`;

const casesOf = (source) => premiumCases(answerShape(source), SUT);

describe("premiumCases", () => {
  test("enumerates age, claims and premium from the columns", () => {
    const cases = casesOf(premiumTable(["Thirties | 30 | 0 | 106.0", "One claim | 30 | 1 | 136.0"]));
    assert.deepEqual(
      cases.map((one) => [one.age, one.claims, one.premium]),
      [
        [30, 0, 106],
        [30, 1, 136],
      ],
    );
  });

  test("reads an age the body holds as a literal, where the table carries no age column", () => {
    const source = `
      @TableTest("""
          Scenario   | Claim Count | Premium?
          No claims  | 0           | 106.0
          Two claims | 2           | 166.0
          """)
      void pricesAtThirty(int claimCount, double premium) {
          EvaluationResult result = evaluator.evaluateApplication("NEW", 30, claimCount);
          assertEquals(premium, result.premium());
      }
    `;
    assert.deepEqual(
      premiumCases(answerShape(source), SUT).map((one) => [one.age, one.claims]),
      [
        [30, 0],
        [30, 2],
      ],
    );
  });

  test("drops a premium of zero, which is an auto-approval or a rejection and not risk-derived", () => {
    const cases = casesOf(premiumTable(["Renewal | 30 | 0 | 0", "One claim | 30 | 1 | 136.0"]));
    assert.deepEqual(cases.map((one) => one.claims), [1]);
  });

  test("counts a value set as the ages it runs as", () => {
    const cases = casesOf(premiumTable(["Thirties | {30, 39} | 0 | 106.0"]));
    assert.deepEqual(cases.map((one) => one.age), [30, 39]);
  });

  test("ignores a table with no premium expectation column", () => {
    const source = `
      @TableTest("""
          Scenario | Age | Claim Count | Decision?
          Approved | 30  | 0           | APPROVED
          """)
      void decides(int age, int claimCount, String decision) {}
    `;
    assert.deepEqual(premiumCases(answerShape(source), SUT), []);
  });
});

describe("perClaimTriple", () => {
  const at = (age, claims) => claims.map((count) => ({ age, claims: count, premium: 100 + count }));

  test("names three consecutive counts at one age", () => {
    assert.deepEqual(perClaimTriple(at(30, [0, 1, 2])), { age: 30, triple: [0, 1, 2] });
  });

  test("accepts a triple that does not start at zero", () => {
    assert.deepEqual(perClaimTriple(at(30, [1, 2, 3])), { age: 30, triple: [1, 2, 3] });
  });

  test("rejects three counts that are not consecutive — 0, 2 and 4 leave the rate undecided", () => {
    assert.equal(perClaimTriple(at(30, [0, 2, 4])), null);
  });

  test("rejects a triple assembled from different ages", () => {
    assert.equal(perClaimTriple([...at(30, [0, 1]), ...at(65, [2])]), null);
  });
});

describe("claimBoundaryPair and ageBoundaryPair", () => {
  test("finds the 0-against-1 pair at one age", () => {
    const cases = [
      { age: 30, claims: 0, premium: 106 },
      { age: 30, claims: 1, premium: 136 },
    ];
    assert.deepEqual(claimBoundaryPair(cases), { age: 30 });
  });

  test("does not accept a 0-against-1 pair split across two ages", () => {
    const cases = [
      { age: 30, claims: 0, premium: 106 },
      { age: 40, claims: 1, premium: 138 },
    ];
    assert.equal(claimBoundaryPair(cases), null);
  });

  test("finds 64 against 65 at one claim count", () => {
    const cases = [
      { age: 64, claims: 0, premium: 112 },
      { age: 65, claims: 0, premium: 221 },
    ];
    assert.deepEqual(ageBoundaryPair(cases), { claims: 0 });
  });
});

describe("claimEffectBands", () => {
  test("requires a claim pair on each side of the senior threshold", () => {
    const cases = [
      { age: 30, claims: 0, premium: 106 },
      { age: 30, claims: 1, premium: 136 },
      { age: 65, claims: 0, premium: 221 },
      { age: 65, claims: 1, premium: 273.5 },
    ];
    assert.deepEqual(claimEffectBands(cases), { below: [30], atOrAbove: [65] });
  });

  test("fails on a single pair, however well chosen — one pair cannot show the increment differs", () => {
    const cases = [
      { age: 30, claims: 0, premium: 106 },
      { age: 30, claims: 1, premium: 136 },
      { age: 65, claims: 0, premium: 221 },
    ];
    assert.equal(claimEffectBands(cases), null);
  });
});

describe("bandedAgePair", () => {
  test("names two ages sharing a premium at one claim count", () => {
    const cases = [
      { age: 30, claims: 0, premium: 106 },
      { age: 39, claims: 0, premium: 106 },
    ];
    assert.deepEqual(bandedAgePair(cases), { claims: 0, ages: [30, 39], premium: 106 });
  });

  test("is not satisfied where every age carries its own premium", () => {
    const cases = [
      { age: 30, claims: 0, premium: 106 },
      { age: 40, claims: 0, premium: 108 },
    ];
    assert.equal(bandedAgePair(cases), null);
  });

  test("is not satisfied by 64 against 65, which shows the opposite", () => {
    const cases = [
      { age: 64, claims: 0, premium: 112 },
      { age: 65, claims: 0, premium: 221 },
    ];
    assert.equal(bandedAgePair(cases), null);
  });
});

describe("tablesAssertingBoth", () => {
  test("names a table carrying a decision and a premium expectation together", () => {
    const shape = answerShape(`
      @TableTest("""
          Scenario | Age | Claim Count | Decision? | Premium?
          Approved | 30  | 0           | APPROVED  | 106.0
          Renewal  | 30  | 1           | APPROVED  | 136.0
          """)
      void routes(int age, int claimCount, String decision, double premium) {}
    `);
    assert.deepEqual(tablesAssertingBoth(shape), ["routes"]);
  });

  test("recognises the decision under a header that never says 'decision'", () => {
    const shape = answerShape(`
      @TableTest("""
          Scenario | Age | Claim Count | Status?  | Premium?
          Approved | 30  | 0           | APPROVED | 106.0
          """)
      void routes(int age, int claimCount, String status, double premium) {}
    `);
    assert.deepEqual(tablesAssertingBoth(shape), ["routes"]);
  });

  test("says nothing where the concerns sit in separate tables", () => {
    const shape = answerShape(`
      @TableTest("""
          Scenario | Age | Claim Count | Decision?
          Approved | 30  | 0           | APPROVED
          """)
      void decides(int age, int claimCount, String decision) {}

      @TableTest("""
          Scenario | Age | Claim Count | Premium?
          Priced   | 30  | 0           | 106.0
          """)
      void prices(int age, int claimCount, double premium) {}
    `);
    assert.deepEqual(tablesAssertingBoth(shape), []);
  });
});

describe("internalColumns and policyColumns", () => {
  const shape = answerShape(`
    @TableTest("""
        Scenario | Age | Risk Score? | Rejection Threshold (Policy) | Decision?
        Rejected | 30  | 80          | 75                           | REJECTED
        """)
    void rejects(int age, int riskScore, int threshold, String decision) {}
  `);

  test("names a column exposing an internal the assertion lists", () => {
    assert.deepEqual(internalColumns(shape), [{ method: "rejects", header: "Risk Score?" }]);
  });

  test("reports a policy column separately, since repair 11 teaches it and black-box-columns punishes it", () => {
    assert.deepEqual(policyColumns(shape).map((one) => one.header), ["Rejection Threshold (Policy)"]);
  });
});

describe("the reference answers", () => {
  /**
   * The cheapest check there is on a transcription. A relation the reference fails is either
   * written wrongly or reading the answer wrongly — both times the relation is at fault, never
   * the reference, so this runs for every eval relations are authored for.
   */
  const referenceOf = (slug) => {
    const reference = storedDraws("tabletest", slug).find((draw) => draw.isReference);
    assert.ok(reference, `expected a stored reference answer for ${slug}`);
    return reference;
  };

  test("eval-18's reference satisfies every one of its relations", () => {
    const rows = evaluateDraw(referenceOf("eval-18-convert-from-code"), EVAL_18_RELATIONS, {
      sutParameters: SUT,
    });
    assert.deepEqual(rows.filter((row) => !row.holds).map((row) => `${row.id}: ${row.evidence}`), []);
  });

  test("eval-14's reference satisfies every one of its relations", () => {
    const rows = evaluateDraw(referenceOf("eval-14-weekly-pay"), EVAL_14_RELATIONS, { sutParameters: [] });
    assert.deepEqual(rows.filter((row) => !row.holds).map((row) => `${row.id}: ${row.evidence}`), []);
  });

  test("every authored eval declares relations with an id and an evaluate", () => {
    for (const number of authoredEvals()) {
      const { relations } = relationsFor(Number(number));
      assert.ok(relations.length > 0, `eval ${number} has no relations`);
      for (const relation of relations) {
        assert.equal(typeof relation.id, "string", `eval ${number} relation without an id`);
        assert.equal(typeof relation.evaluate, "function", `${relation.id} has no evaluate`);
      }
    }
  });
});


// ---------------------------------------------------------------------------
// eval-14 weekly-pay
// ---------------------------------------------------------------------------

const payTable = (rows, header = "Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?") => `
    @TableTest("""
        ${header}
${rows.map((row) => `        ${row}`).join("\n")}
        """)
    void paysTheWeek(Integer weekdayHours, Integer sundayHours, Integer holidayHours, int hourlyRate, int weeklyPay) {
        assertEquals(weeklyPay, WeeklyPay.calculate(weekdayHours, sundayHours, holidayHours, hourlyRate));
    }
`;

describe("payForRow", () => {
  test("pays weekday hours up to forty at the base rate", () => {
    assert.equal(payForRow({ weekday: 40, sunday: 0, holiday: 0, rate: 10 }), 400);
  });

  test("pays weekday hours beyond forty at time-and-a-half", () => {
    assert.equal(payForRow({ weekday: 41, sunday: 0, holiday: 0, rate: 10 }), 415);
  });

  test("pays Sunday and holiday hours at double time", () => {
    assert.equal(payForRow({ weekday: 0, sunday: 1, holiday: 1, rate: 10 }), 40);
  });

  test("floors the total at zero, however large the correction", () => {
    assert.equal(payForRow({ weekday: 40, sunday: -21, holiday: 0, rate: 10 }), 0);
  });

  test("pays nothing at a zero rate, whatever the hours", () => {
    assert.equal(payForRow({ weekday: 100, sunday: 8, holiday: 8, rate: 0 }), 0);
  });
});

describe("payCases", () => {
  test("reads a blank hours cell as no hours worked", () => {
    const { cases, unresolved } = payCases(answerShape(payTable(["One weekday hour | 1 | | | 10 | 10"])));
    assert.equal(unresolved, 0);
    assert.deepEqual(cases.map((one) => [one.sunday, one.holiday, one.pay]), [[0, 0, 10]]);
  });

  test("counts a row as unresolved rather than assuming zero where the rate is held in the body", () => {
    const source = `
      @TableTest("""
          Scenario | Weekday Hours | Weekly Pay?
          A week   | 40            | 400
          """)
      void paysTheWeek(Integer weekdayHours, int weeklyPay) {
          assertEquals(weeklyPay, WeeklyPay.calculate(weekdayHours, null, null, 10));
      }
    `;
    const { cases, unresolved } = payCases(answerShape(source));
    assert.deepEqual(cases, []);
    assert.equal(unresolved, 1);
  });

  test("takes the total pay column rather than an intermediate one", () => {
    const source = `
      @TableTest("""
          Scenario | Weekday Hours | Hourly Rate | Overtime Pay? | Weekly Pay?
          Overtime | 41            | 10          | 15            | 415
          """)
      void paysTheWeek(Integer weekdayHours, int hourlyRate, int overtimePay, int weeklyPay) {}
    `;
    const { cases } = payCases(answerShape(source));
    assert.deepEqual(cases.map((one) => one.stated), [415]);
  });
});

describe("overtimeBoundary", () => {
  test("finds forty beside forty-one in one column of one table", () => {
    const found = overtimeBoundary(answerShape(payTable([
      "At the threshold   | 40 | | | 10 | 400",
      "Past the threshold | 41 | | | 10 | 415",
    ])));
    assert.equal(found.past, 41);
  });

  test("accepts a half hour past the threshold", () => {
    const found = overtimeBoundary(answerShape(payTable([
      "At the threshold | 40   | | | 10 | 400",
      "Half an hour on  | 40.5 | | | 10 | 407.5",
    ])));
    assert.equal(found.past, 40.5);
  });

  test("rejects a jump well past the threshold, which pins nothing", () => {
    assert.equal(
      overtimeBoundary(answerShape(payTable([
        "At the threshold | 40 | | | 10 | 400",
        "A long week      | 50 | | | 10 | 550",
      ]))),
      null,
    );
  });
});

describe("combinedScenario", () => {
  test("finds a row working all three hour types", () => {
    const found = combinedScenario(answerShape(payTable(["Every band | 41 | 8 | 8 | 10 | 735"])));
    assert.deepEqual(found.hours, [41, 8, 8]);
  });

  test("does not accept a row that zeroes two of the three", () => {
    assert.equal(combinedScenario(answerShape(payTable(["Weekdays only | 41 | 0 | 0 | 10 | 415"]))), null);
  });
});

describe("blankHourColumns", () => {
  test("names a blank column and the boxed parameter it feeds", () => {
    const found = blankHourColumns(answerShape(payTable(["One weekday hour | 1 | | | 10 | 10"])));
    assert.deepEqual(found.map((one) => [one.header, one.type, one.boxed]), [
      ["Sunday Hours", "Integer", true],
      ["Holiday Hours", "Integer", true],
    ]);
  });

  test("marks a blank cell on a primitive parameter unboxed, which cannot accept null", () => {
    const source = `
      @TableTest("""
          Scenario | Sunday Hours | Hourly Rate | Weekly Pay?
          No Sunday|              | 10          | 0
          """)
      void paysTheWeek(double sundayHours, int hourlyRate, int weeklyPay) {}
    `;
    assert.deepEqual(blankHourColumns(answerShape(source)).map((one) => one.boxed), [false]);
  });

  test("finds nothing where every cell writes a zero instead", () => {
    assert.deepEqual(blankHourColumns(answerShape(payTable(["A week | 40 | 0 | 0 | 10 | 400"]))), []);
  });
});

describe("errorEdgeCases and zeroRateRow", () => {
  test("finds negative hours and a negative rate", () => {
    const shape = answerShape(payTable([
      "A correction  | -10 | 0 | 0 | 10 | 0",
      "A bad rate    | 40  | 0 | 0 | -1 | 0",
    ]));
    const { negativeHours, negativeRate } = errorEdgeCases(shape);
    assert.equal(negativeHours.value, -10);
    assert.equal(negativeRate.value, -1);
  });

  test("finds a zero rate stated against a zero pay", () => {
    const found = zeroRateRow(answerShape(payTable(["Any hours at no rate | 41 | 8 | 8 | 0 | 0"])));
    assert.equal(found.rate, 0);
  });

  test("does not accept a zero rate whose row states no pay at all", () => {
    const source = `
      @TableTest("""
          Scenario    | Hourly Rate | Throws?
          A zero rate | 0           |
          """)
      void rejects(int hourlyRate, Class<? extends Throwable> thrown) {}
    `;
    assert.equal(zeroRateRow(answerShape(source)), null);
  });
});

describe("rejectionExpectationColumn", () => {
  test("finds the exception column on the table carrying the negative rate", () => {
    const source = `
      @TableTest("""
          Scenario   | Hourly Rate | Throws?
          Below zero | -1          | java.lang.IllegalArgumentException
          """)
      void rejects(int hourlyRate, Class<? extends Throwable> thrown) {}
    `;
    assert.equal(rejectionExpectationColumn(answerShape(source)).column.header, "Throws?");
  });

  test("reports a rejection row with the outcome nowhere in the table", () => {
    const source = `
      @TableTest("""
          Scenario   | Hourly Rate
          Below zero | -1
          """)
      void rejects(int hourlyRate) {}
    `;
    assert.equal(rejectionExpectationColumn(answerShape(source)).column, null);
  });
});

describe("implementationHeaders", () => {
  test("names a camelCase header", () => {
    const source = `
      @TableTest("""
          Scenario | weekdayHours | Weekly Pay?
          A week   | 40           | 400
          """)
      void pays(int weekdayHours, int weeklyPay) {}
    `;
    assert.deepEqual(implementationHeaders(answerShape(source)).map((one) => one.header), ["weekdayHours"]);
  });

  test("names an abbreviated header", () => {
    const source = `
      @TableTest("""
          Scenario | Weekday Hrs | Weekly Pay?
          A week   | 40          | 400
          """)
      void pays(int weekdayHours, int weeklyPay) {}
    `;
    assert.deepEqual(implementationHeaders(answerShape(source)).map((one) => one.header), ["Weekday Hrs"]);
  });

  test("exempts the exception column, which 1.2 requires by that name", () => {
    const source = `
      @TableTest("""
          Scenario   | Hourly Rate | Throws?
          Below zero | -1          | java.lang.IllegalArgumentException
          """)
      void rejects(int hourlyRate, Class<? extends Throwable> thrown) {}
    `;
    assert.deepEqual(implementationHeaders(answerShape(source)), []);
  });
});

describe("the classified-hours vocabulary", () => {
  const classifiedTable = (rows) => `
    @TableTest("""
        Scenario | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
${rows.map((row) => `        ${row}`).join("\n")}
        """)
    void pricesBands(double regularHours, double overtimeHours, double sundayHours,
                     double holidayHours, double hourlyRate, double totalPay) {
        assertEquals(totalPay, Pay.of(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate), 0.001);
    }
  `;

  test("prices pre-classified bands without applying the threshold a second time", () => {
    const { cases } = payCases(answerShape(classifiedTable(["All bands | 40 | 5 | 8 | 8 | 20.00 | 1590.00"])));
    assert.deepEqual(cases.map((one) => [one.pay, one.stated]), [[1590, 1590]]);
  });

  test("floors a negative correction in a classified table", () => {
    const { cases } = payCases(answerShape(classifiedTable(["A correction | -50 | 0 | 0 | 0 | 20.00 | 0.00"])));
    assert.deepEqual(cases.map((one) => [one.pay, one.stated]), [[0, 0]]);
  });

  test("counts a classified row as working weekday hours for the combined scenario", () => {
    const found = combinedScenario(answerShape(classifiedTable(["All bands | 40 | 5 | 8 | 8 | 20.00 | 1590.00"])));
    assert.deepEqual(found.hours, [40, 8, 8]);
  });

  test("finds a negative correction stated in a regular-hours column", () => {
    const { negativeHours } = errorEdgeCases(answerShape(classifiedTable(["A correction | -50 | 0 | 0 | 0 | 20.00 | 0.00"])));
    assert.equal(negativeHours.value, -50);
  });
});

describe("a policy column is not an hours column", () => {
  const withThreshold = `
    @TableTest("""
        Scenario | Weekday Hours | Overtime Threshold (hrs) | Hourly Rate | Weekly Pay?
        A week   | 39            | 40                       | 20          | 780
        """)
    void pays(double weekdayHours, double overtimeThreshold, double hourlyRate, double weeklyPay) {}
  `;

  test("does not read a declared threshold as forty overtime hours", () => {
    assert.equal(findInputColumn(answerShape(withThreshold).tables[0], /overtime/i), null);
  });

  test("prices the row from the hours alone, so the stated pay is right", () => {
    const { cases } = payCases(answerShape(withThreshold));
    assert.deepEqual(cases.map((one) => [one.pay, one.stated]), [[780, 780]]);
  });

  test("does not fail the header for its parenthesised unit", () => {
    assert.deepEqual(implementationHeaders(answerShape(withThreshold)), []);
  });
});

describe("payColumn", () => {
  const tableWith = (header, params) => answerShape(`
    @TableTest("""
        ${header}
        A week | -5 | 20 | -100
        """)
    void prices(${params}) {}
  `).tables[0];

  test("takes a column that names the week's total", () => {
    const table = tableWith("Scenario | Weekday Hours | Hourly Rate | Weekly Pay?", "double weekdayHours, double hourlyRate, double weeklyPay");
    assert.equal(payColumn(table).header, "Weekly Pay?");
  });

  test("refuses a band's own pay, which the floor does not apply to", () => {
    const table = tableWith("Scenario | Weekday Hours | Hourly Rate | Weekday Pay?", "double weekdayHours, double hourlyRate, double weekdayPay");
    assert.equal(payColumn(table), null);
  });
});

describe("heldBandValue", () => {
  const tableOf = (source) => answerShape(source).tables[0];

  test("reads a null argument as no hours of that band worked", () => {
    const table = tableOf(`
      @TableTest("""
          Scenario | Weekday Hours | Sunday Hours | Hourly Rate | Weekly Pay?
          A week   | 40            | -10          | 10          | 200
          """)
      void pays(Integer weekdayHours, Integer sundayHours, int hourlyRate, int weeklyPay) {
          int pay = WeeklyPay.calculate(weekdayHours, sundayHours, null, hourlyRate);
          assertEquals(weeklyPay, pay);
      }
    `);
    assert.deepEqual(heldBandValue(table, hourColumns(table)), { band: "holiday", value: 0, ambiguous: false });
  });

  test("reads a numeric argument as that many hours held for every row", () => {
    const table = tableOf(`
      @TableTest("""
          Scenario  | Sunday Hours | Hourly Rate | Weekly Pay?
          Floored   | -10          | 20          | 400
          """)
      void floors(Integer sundayHours, int hourlyRate, int weeklyPay) {
          assertEquals(weeklyPay, WeeklyPay.calculate(40, sundayHours, 0, hourlyRate));
      }
    `);
    const held = heldBandValue(table, hourColumns(table));
    assert.equal(held.ambiguous, true, "two literals against two missing bands cannot be mapped");
  });

  test("holds nothing where the act call passes no literal at all", () => {
    const table = tableOf(`
      @TableTest("""
          Scenario | Weekday Hours | Hourly Rate | Weekly Pay?
          A week   | 40            | 10          | 400
          """)
      void pays(Integer weekdayHours, int hourlyRate, int weeklyPay) {
          assertEquals(weeklyPay, WeeklyPay.calculate(weekdayHours, hourlyRate));
      }
    `);
    assert.deepEqual(heldBandValue(table, hourColumns(table)), { band: null, value: 0, ambiguous: false });
  });
});
