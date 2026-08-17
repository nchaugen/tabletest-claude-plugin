const { test, describe } = require("node:test");
const assert = require("node:assert/strict");

const { answerShape } = require("./answer-shape.js");
const { storedDraws, evaluateDraw } = require("./shape-report.js");
const {
  EVAL_14_RELATIONS,
  EVAL_15_RELATIONS,
  EVAL_22_RELATIONS,
  EVAL_23_RELATIONS,
  EVAL_25_RELATIONS,
  ageBandEffect,
  bracketingPair,
  descriptionPinsAColumn,
  echoedInputValues,
  eval23Inputs,
  heldConstantIncomePair,
  incomeEffectCases,
  loanCases,
  namesStatingTheOutcome,
  rowsRediscarging,
  splitIncomeColumns,
  undeclaredHeldValues,
  eval22Concerns,
  optionalColumns,
  wordsForAbsent,
  EVAL_29_RELATIONS,
  EVAL_30_RELATIONS,
  boundaryPairs,
  dimensionsOf,
  publishedSurface,
  publishesValue,
  unpublishedConstants,
  volumetricWeight,
  bespokeMapCells,
  companionBreaksATie,
  enumeratingMessages,
  eval29Concerns,
  isStandardMap,
  quantityCore,
  sharedMutableState,
  spreadCouponColumns,
  compoundKeys,
  exercisingTable,
  isNativeCollection,
  minimalCovers,
  quotedStructureIn,
  setMembers,
  stringEncodedOutputs,
  tieComputable,
  undeclaredCriteria,
  EVAL_18_RELATIONS,
  historyEntries,
  historyMixesKinds,
  ladderTable,
  ladderTables,
  percentValue,
  reisTier,
  zonesMentioned,
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

  test("eval-15's reference satisfies every one of its relations", () => {
    const rows = evaluateDraw(referenceOf("eval-15-reis-discount"), EVAL_15_RELATIONS, { sutParameters: [] });
    assert.deepEqual(rows.filter((row) => !row.holds).map((row) => `${row.id}: ${row.evidence}`), []);
  });

  test("eval-22's reference satisfies every one of its relations", () => {
    const rows = evaluateDraw(referenceOf("eval-22-event-registration-tt"), EVAL_22_RELATIONS, { sutParameters: [] });
    const failed = rows.filter((row) => !row.holds && !row.advisory).map((row) => `${row.id}: ${row.evidence}`);
    assert.deepEqual(failed, []);
  });

  test("eval-25's reference satisfies every one of its relations", () => {
    const rows = evaluateDraw(referenceOf("eval-25-convert-from-spock"), EVAL_25_RELATIONS, { sutParameters: [] });
    const failed = rows.filter((row) => !row.holds && !row.advisory).map((row) => `${row.id}: ${row.evidence}`);
    assert.deepEqual(failed, []);
  });

  test("eval-29's reference satisfies every one of its relations", () => {
    const rows = evaluateDraw(referenceOf("eval-29-shopping-cart-tt"), EVAL_29_RELATIONS, { sutParameters: [] });
    // `consistent-quantity-naming` is advisory: whether `Message?` and `Message Mentions?` are one
    // quantity is a domain reading, and the reference treats them as two.
    const failed = rows.filter((row) => !row.holds && !row.advisory).map((row) => `${row.id}: ${row.evidence}`);
    assert.deepEqual(failed, []);
  });

  test("eval-30's reference satisfies every one of its relations", () => {
    const rows = evaluateDraw(referenceOf("eval-30-order-splitting-tt"), EVAL_30_RELATIONS, { sutParameters: [] });
    assert.deepEqual(rows.filter((row) => !row.holds).map((row) => `${row.id}: ${row.evidence}`), []);
  });

  test("eval-23's reference satisfies every one of its relations", () => {
    const rows = evaluateDraw(referenceOf("eval-23-loan-approval-tt"), EVAL_23_RELATIONS, { sutParameters: [] });
    const failed = rows.filter((row) => !row.holds && !row.advisory).map((row) => `${row.id}: ${row.evidence}`);
    assert.deepEqual(failed, []);
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

// ---------------------------------------------------------------------------
// eval-15 reis-discount
// ---------------------------------------------------------------------------

const ladderOf = (rows, header = "Scenario | Ticket Number | Discount %?") => answerShape(`
    @TableTest("""
        ${header}
${rows.map((row) => `        ${row}`).join("\n")}
        """)
    void climbsTheLadder(int ticketNumber, int discount) {}
`);

describe("reisTier", () => {
  test("pays nothing below the fifth ticket", () => {
    assert.equal(reisTier(1), 0);
    assert.equal(reisTier(4), 0);
  });

  test("raises a rung every fifth ticket", () => {
    assert.deepEqual([5, 9, 10, 14, 15, 39].map(reisTier), [5, 5, 10, 10, 15, 35]);
  });

  test("stops at forty per cent however far the count runs", () => {
    assert.equal(reisTier(40), 40);
    assert.equal(reisTier(400), 40);
  });
});

describe("percentValue", () => {
  test("reads a percentage with or without its sign", () => {
    assert.equal(percentValue("20"), 20);
    assert.equal(percentValue("20%"), 20);
    assert.equal(percentValue("20 %"), 20);
  });
});

describe("ladderTables", () => {
  test("finds the table mapping the most rungs", () => {
    const shape = ladderOf(["A | {1, 4} | 0", "B | {5, 9} | 5", "C | {10, 14} | 10"]);
    assert.equal(ladderTables(shape).length, 1);
    assert.equal(ladderTable(shape).rungs.size, 3);
  });

  test("is not a ladder below three rungs — a scheme table shows a representative rung", () => {
    const shape = ladderOf(["A | 1 | 0", "B | 5 | 5"]);
    assert.deepEqual(ladderTables(shape), []);
  });

  test("passes over a child's flat rate, which is not a rung of the ladder", () => {
    const shape = answerShape(`
      @TableTest("""
          Scenario | Traveler Category | Ticket Number | Discount?
          Child    | CHILD             | {1, 5, 40}    | 20
          No Reis  | ADULT             | 1             | 0
          First    | {ADULT, SENIOR}   | 5             | 5
          """)
      void routes(TravelerCategory category, int ticketNumber, int discount) {}
    `);
    assert.deepEqual(ladderTables(shape), [], "two adult rungs is a scheme table, not a ladder");
  });
});

describe("historyEntries and historyMixesKinds", () => {
  test("splits a compact history on its semicolons", () => {
    assert.deepEqual(historyEntries("5d WEEKLY;10d MONTHLY;15d"), ["5d WEEKLY", "10d MONTHLY", "15d"]);
  });

  test("keeps a bracketed entry whole, commas inside it being its fields", () => {
    assert.deepEqual(historyEntries("[[daysAgo: 5, type: WEEKLY], [daysAgo: 5, type: MONTHLY]]"), [
      "[daysAgo: 5, type: WEEKLY]",
      "[daysAgo: 5, type: MONTHLY]",
    ]);
  });

  test("reads an entry naming no period type as the counting kind, since notations leave it implicit", () => {
    assert.equal(historyMixesKinds("5d WEEKLY;10d MONTHLY;15d"), true);
  });

  test("is not mixed where a single bracketed entry names a period ticket", () => {
    assert.equal(historyMixesKinds("[[purchasedAt: '2026-07-26T12:00:00', ticketType: WEEKLY]]"), false);
  });

  test("is not mixed where every entry is the same kind", () => {
    assert.equal(historyMixesKinds("[[daysAgo: 5, type: WEEKLY], [daysAgo: 5, type: MONTHLY]]"), false);
    assert.equal(historyMixesKinds("[[daysAgo: 5, type: SINGLE]]"), false);
  });

  test("reads an empty history as no entries", () => {
    assert.deepEqual(historyEntries("[]"), []);
  });
});

describe("zonesMentioned", () => {
  test("finds zones written in a value set", () => {
    const shape = answerShape(`
      @TableTest("""
          Scenario | Zone                     | Counts?
          Any zone | {ZONE_1, ZONE_2, ZONE_3} | true
          """)
      void counts(ZoneValidity zone, boolean counts) {}
    `);
    assert.deepEqual([...zonesMentioned(shape.tables[0])].sort(), ["1", "2", "3"]);
  });

  test("finds zones buried inside a history cell", () => {
    const shape = answerShape(`
      @TableTest("""
          Scenario | Purchase History                                          | Count?
          Zones    | [[daysAgo: 5, zone: ZONE_1], [daysAgo: 6, zone: ZONE_3]]  | 2
          """)
      void counts(List<PastPurchase> history, int count) {}
    `);
    assert.deepEqual([...zonesMentioned(shape.tables[0])].sort(), ["1", "3"]);
  });
});

describe("the eval-15 tier relations", () => {
  const verdict = (id, shape) => EVAL_15_RELATIONS.find((one) => one.id === id).evaluate(shape, {});

  test("2.19 names the rungs a ladder is missing", () => {
    const shape = ladderOf(["A | {1, 4} | 0", "B | {5, 9} | 5", "C | {10, 14} | 10"]);
    const result = verdict("2.19-depth-all-tiers", shape);
    assert.equal(result.holds, false);
    assert.match(result.evidence, /missing 15, 20, 25, 30, 35, 40/);
  });

  test("2.9 is vacuous where the ladder carries no value set — that is 2.15's question", () => {
    const shape = ladderOf(["A | 4 | 0", "B | 5 | 5", "C | 9 | 5", "D | 10 | 10"]);
    const result = verdict("2.9-correctness-value-set-tier-semantics", shape);
    assert.equal(result.holds, true);
    assert.match(result.evidence, /VACUOUS/);
  });

  test("2.9 catches a value set spanning two tiers", () => {
    const shape = ladderOf(["A | {1, 4} | 0", "B | {5, 12} | 5", "C | {15, 19} | 15"]);
    assert.equal(verdict("2.9-correctness-value-set-tier-semantics", shape).holds, false);
  });

  test("2.9 accepts a table counting prior purchases rather than including this one", () => {
    const shape = ladderOf(["A | {0, 3} | 0", "B | {4, 8} | 5", "C | {9, 13} | 10"]);
    const result = verdict("2.9-correctness-value-set-tier-semantics", shape);
    assert.equal(result.holds, true);
    assert.match(result.evidence, /prior purchases only/);
  });

  test("2.20 fails a tier split over two rows", () => {
    const shape = ladderOf(["Kicks in | 5 | 5", "Holds | 9 | 5", "Next | {10, 14} | 10", "Third | {15, 19} | 15"]);
    const result = verdict("2.20-readability-one-row-per-tier", shape);
    assert.equal(result.holds, false);
    assert.match(result.evidence, /5% over 2 rows/);
  });

  test("2.15 accepts one bare rung among nine grouped ones", () => {
    const rows = [
      "A | {1, 4} | 0", "B | {5, 9} | 5", "C | {10, 14} | 10", "D | {15, 19} | 15", "E | {20, 24} | 20",
      "F | {25, 29} | 25", "G | {30, 34} | 30", "H | {35, 39} | 35", "I | 40 | 40",
    ];
    assert.equal(verdict("2.15-ticket-count-uses-value-sets", ladderOf(rows)).holds, true);
  });

  test("2.15 refuses a table that enumerates boundaries with one exception", () => {
    const rows = ["A | 4 | 0", "B | 5 | 5", "C | 9 | 5", "D | 10 | 10", "E | {15, 19} | 15"];
    assert.equal(verdict("2.15-ticket-count-uses-value-sets", ladderOf(rows)).holds, false);
  });
});

// ---------------------------------------------------------------------------
// eval-30 order-splitting
// ---------------------------------------------------------------------------

describe("minimalCovers and companionBreaksATie", () => {
  test("finds the single smallest cover where one warehouse holds everything", () => {
    const covers = minimalCovers(["camera", "lens"], { W1: ["camera", "lens"], W2: ["camera"] });
    assert.deepEqual(covers, [["W1"]]);
  });

  test("finds every smallest cover where two tie", () => {
    const covers = minimalCovers(["camera", "lens", "mic"], {
      W1: ["camera", "lens"],
      W2: ["camera", "mic"],
      W3: ["lens"],
    });
    assert.deepEqual(covers.map((cover) => cover.join("+")).sort(), ["W1+W2", "W2+W3"]);
  });

  test("says the companion rule decides a tie where one cover keeps the pair together", () => {
    const found = companionBreaksATie(
      ["camera", "lens", "mic"],
      { W1: ["camera", "lens"], W2: ["camera", "mic"], W3: ["lens"] },
      ["camera", "lens"],
    );
    assert.equal(found.kept, "W1+W2");
  });

  test("decides nothing where only one cover is minimal", () => {
    assert.equal(
      companionBreaksATie(["camera", "lens"], { W1: ["camera"], W2: ["lens"] }, ["camera", "lens"]),
      null,
    );
  });

  test("decides nothing where every minimal cover already keeps the pair together", () => {
    assert.equal(
      companionBreaksATie(["camera", "lens"], { W1: ["camera", "lens"], W2: ["camera", "lens"] }, ["camera", "lens"]),
      null,
    );
  });
});

describe("setMembers", () => {
  test("reads a set cell's members and an empty set", () => {
    assert.deepEqual(setMembers("{camera, lens}"), ["camera", "lens"]);
    assert.deepEqual(setMembers("{}"), []);
  });
});

describe("native collection outputs", () => {
  const expectationOf = (cell, header = "Shipments?") => answerShape(`
    @TableTest("""
        Scenario | Items | ${header}
        A row    | [x]   | ${cell}
        Another  | [y]   | ${cell === "1" ? "2" : "[W9: {z}]"}
        """)
    void splits(List<String> items, Object result) {}
  `);

  test("a native map of sets is native", () => {
    assert.equal(isNativeCollection("[W1: {camera, lens}]"), true);
  });

  test("a quoted scalar that encodes structure is not", () => {
    assert.equal(isNativeCollection('"W1:[camera,lens]"'), false);
  });

  test("finds a quoted string encoding a structure inside a native list", () => {
    assert.equal(quotedStructureIn('["W1:[camera,lens]"]'), '"W1:[camera,lens]"');
  });

  test("finds nothing to complain about in a native map of sets", () => {
    assert.equal(quotedStructureIn("[W1: {camera, lens}]"), null);
  });

  test("fails an expectation whose elements are hand-rolled strings", () => {
    const found = stringEncodedOutputs(expectationOf('["W1:[camera,lens]"]'));
    assert.equal(found.length, 1);
    assert.match(found[0].quoted, /W1:\[camera,lens\]/);
  });

  test("passes a native expectation", () => {
    assert.deepEqual(stringEncodedOutputs(expectationOf("[W1: {camera, lens}]")), []);
  });

  test("passes a scalar expectation", () => {
    assert.deepEqual(stringEncodedOutputs(expectationOf("1", "Shipment Count?")), []);
  });

  test("counts the facets a map key joins, for a human to judge", () => {
    const found = compoundKeys(expectationOf("[DELIVERY@addr-1@W1@IMMEDIATE: {p1}]"));
    assert.equal(found[0].parts, 4);
  });

  test("says nothing about a two-part key, which the reference uses and the grader passes", () => {
    assert.deepEqual(compoundKeys(expectationOf("[DELIVERY@Addr-A: {camera}]")), []);
  });
});

describe("exercisingTable", () => {
  const facet = { header: /companion/i, token: /companion/i, surface: /companion|together/i };

  test("accepts a facet held constant where the table declares the concern", () => {
    const shape = answerShape(`
      @DisplayName("Keeps companions together when possible")
      @TableTest("""
          Scenario  | Items    | Companions     | Shipments?
          A tie     | [a, b]   | {a, b}         | [W1: {a, b}]
          No shared | [a, b]   | {a, b}         | [W1: {a}, W2: {b}]
          """)
      void keeps(List<String> items, Set<String> companions, Object shipments) {}
    `);
    const found = exercisingTable(shape, facet);
    assert.ok(found);
    assert.equal(found.viaSurface, true);
  });

  test("refuses a table whose outcome never varies", () => {
    const shape = answerShape(`
      @DisplayName("Keeps companions together when possible")
      @TableTest("""
          Scenario | Items  | Companions | Shipments?
          One      | [a, b] | {a, b}     | [W1: {a, b}]
          Two      | [a, b] | {a, b}     | [W1: {a, b}]
          """)
      void keeps(List<String> items, Set<String> companions, Object shipments) {}
    `);
    assert.equal(exercisingTable(shape, facet), null);
  });
});

describe("undeclaredCriteria", () => {
  const classWith = (body, description = "") => answerShape(`
      @DisplayName("Splits the order")
      ${description ? `@Description("""\n        ${description}\n        """)` : ""}
      @TableTest("""
          Scenario | Items | Shipments?
          A row    | [x]   | [W1: {x}]
          Another  | [y]   | [W2: {y}]
          """)
      void splits(List<String> items, Object shipments) {
          ${body}
      }
  `);

  test("names an unordered comparison the surface does not declare", () => {
    const found = undeclaredCriteria(classWith("assertEquals(Set.copyOf(shipments), groupsOf(actual));"));
    assert.deepEqual(found.map((one) => one.criterion), ["unordered comparison"]);
  });

  test("accepts it once a description says order is not part of the rule", () => {
    const found = undeclaredCriteria(
      classWith("assertEquals(Set.copyOf(shipments), groupsOf(actual));", "Shipments are compared regardless of order."),
    );
    assert.deepEqual(found, []);
  });

  test("does not read an empty-set default as a criterion", () => {
    const found = undeclaredCriteria(classWith("assertEquals(shipments, actual.getOrDefault(IMMEDIATE, Set.of()));"));
    assert.deepEqual(found, []);
  });

  test("does not read a TreeSet built inside a helper as sorting before comparing", () => {
    const found = undeclaredCriteria(classWith("Set<String> addresses = new TreeSet<>();\n assertEquals(shipments, actual);"));
    assert.deepEqual(found, []);
  });

  test("names sorting applied before the comparison, wherever the helper lives", () => {
    const found = undeclaredCriteria(classWith("assertEquals(shipments, actual.stream().sorted().toList());"));
    assert.deepEqual(found.map((one) => one.criterion), ["ordering"]);
  });

  test("is not fooled by the domain noun — a description mentioning an order declares nothing", () => {
    const found = undeclaredCriteria(
      classWith("assertEquals(shipments, actual.stream().sorted().toList());", "The order is split across warehouses."),
    );
    assert.deepEqual(found.map((one) => one.criterion), ["ordering"]);
  });
});

// ---------------------------------------------------------------------------
// eval-29 shopping-cart
// ---------------------------------------------------------------------------

const cartClass = (tables) => answerShape(tables.map(({ name, header, rows, body = "" }) => `
    @TableTest("""
        ${header}
${rows.map((row) => `        ${row}`).join("\n")}
        """)
    void ${name}(Object a, Object b, Object c, Object d) { ${body} }
`).join("\n"));

describe("quantityCore", () => {
  test("reduces a before/after pair to the quantity it names", () => {
    assert.equal(quantityCore("Cart Before"), "cart");
    assert.equal(quantityCore("Cart After?"), "cart");
    assert.equal(quantityCore("Cart"), "cart");
  });

  test("keeps a qualified name distinct, since it may be a different quantity", () => {
    assert.equal(quantityCore("Message Mentions?"), "message mentions");
  });
});

describe("eval29Concerns", () => {
  test("classifies the four concerns from the columns each table carries", () => {
    const shape = cartClass([
      { name: "adds", header: "Scenario | Cart Before | Product Id | Quantity | Cart After?", rows: ["A | [:] | widget | 1 | [widget: 1]"] },
      { name: "applies", header: "Scenario | Active Coupon Before | Coupon Code | Active Coupon After?", rows: ["A | | SAVE10 | SAVE10"] },
      { name: "totals", header: "Scenario | Cart | Catalogue | Total?", rows: ["A | [widget: 1] | [widget: 2.50] | 2.50"] },
      { name: "checksOut", header: "Scenario | Cart | Stock | Success?", rows: ["A | [widget: 1] | [widget: 5] | true"] },
    ]);
    const concerns = eval29Concerns(shape);
    assert.deepEqual(
      Object.entries(concerns).map(([name, tables]) => [name, tables.map((one) => one.method)]),
      [["item", ["adds"]], ["coupon", ["applies"]], ["total", ["totals"]], ["checkout", ["checksOut"]]],
    );
  });

  test("finds the coupon concern where the post-state is the coupon, not the cart", () => {
    const shape = cartClass([
      { name: "applies", header: "Scenario | Active Coupon Before | Coupon Code | Active Coupon After?", rows: ["A | | SAVE10 | SAVE10"] },
    ]);
    assert.deepEqual(eval29Concerns(shape).coupon.map((one) => one.method), ["applies"]);
  });

  test("reads a cart headed plainly Items as the cart", () => {
    const shape = cartClass([
      { name: "adds", header: "Scenario | Items Before | Product Id | Quantity | Items After?", rows: ["A | [:] | widget | 1 | [widget: 1]"] },
    ]);
    assert.deepEqual(eval29Concerns(shape).item.map((one) => one.method), ["adds"]);
  });
});

describe("isStandardMap", () => {
  test("accepts TableTest's own map notation and the empty map", () => {
    assert.equal(isStandardMap("[widget: 2, gadget: 1]"), true);
    assert.equal(isStandardMap("[:]"), true);
  });

  test("accepts a value set over maps, which runs the row once per map", () => {
    assert.equal(isStandardMap("{[:], [widget: 5]}"), true);
  });

  test("refuses a bespoke grammar", () => {
    assert.equal(isStandardMap("{widget=2}"), false);
    assert.equal(isStandardMap("widget:2;gadget:1"), false);
  });
});

describe("bespokeMapCells", () => {
  test("names a map-valued cell written outside TableTest's notation", () => {
    const shape = cartClass([
      { name: "totals", header: "Scenario | Cart | Total?", rows: ["A | {widget=2} | 5.00"] },
    ]);
    assert.deepEqual(bespokeMapCells(shape).map((one) => one.cell), ["{widget=2}"]);
  });

  test("passes over a column of scalars, whatever it is called", () => {
    const shape = cartClass([
      { name: "totals", header: "Scenario | Cart Total? | Total?", rows: ["A | 5.00 | 5.00"] },
    ]);
    assert.deepEqual(bespokeMapCells(shape), []);
  });
});

describe("spreadCouponColumns", () => {
  test("names a table spreading a coupon across facet columns", () => {
    const shape = cartClass([
      { name: "totals", header: "Scenario | Coupon Type | Coupon Amount | Total?", rows: ["A | PERCENTAGE | 10 | 9.00"] },
    ]);
    assert.equal(spreadCouponColumns(shape).length, 1);
  });

  test("says nothing about one converted coupon column", () => {
    const shape = cartClass([
      { name: "totals", header: "Scenario | Coupon | Total?", rows: ["A | 10% off | 9.00"] },
    ]);
    assert.deepEqual(spreadCouponColumns(shape), []);
  });
});

describe("sharedMutableState", () => {
  test("names a static field a table body writes to", () => {
    const shape = answerShape(`
      class CartTest {
          private static Cart cart = new Cart();
          @TableTest("""
              Scenario | Product | Cart After?
              A row    | widget  | [widget: 1]
              """)
          void adds(String product, Object after) {
              cart.add(product);
              assertEquals(after, cart);
          }
      }
    `);
    assert.deepEqual(sharedMutableState(shape).map((one) => one.field), ["cart"]);
  });

  test("says nothing where the table builds its own state each row", () => {
    const shape = answerShape(`
      class CartTest {
          @TableTest("""
              Scenario | Product | Cart After?
              A row    | widget  | [widget: 1]
              """)
          void adds(String product, Object after) {
              Cart cart = new Cart();
              assertEquals(after, CartService.addItem(cart, product));
          }
      }
    `);
    assert.deepEqual(sharedMutableState(shape), []);
  });
});

describe("enumeratingMessages", () => {
  test("reports a message listing several entities, without deciding it", () => {
    const shape = cartClass([
      {
        name: "checksOut",
        header: "Scenario | Cart | Stock | Message?",
        rows: ["Short | [w: 5] | [w: 2] | Insufficient stock for widget: requested 5, available 2; gadget: requested 4, available 1"],
      },
    ]);
    assert.equal(enumeratingMessages(shape).length, 1);
  });

  test("says nothing about an ordinary message carrying a colon", () => {
    const shape = cartClass([
      { name: "adds", header: "Scenario | Product | Message?", rows: ["A | gadget | Unknown product: gadget"] },
    ]);
    assert.deepEqual(enumeratingMessages(shape), []);
  });
});

describe("quotedStructureIn", () => {
  test("fires on a quoted collection", () => {
    assert.equal(quotedStructureIn('["W1:[camera,lens]"]'), '"W1:[camera,lens]"');
  });

  test("fires on several key-and-value segments packed into one quoted scalar", () => {
    assert.match(quotedStructureIn('"widget: 5, gadget: 4"'), /widget/);
  });

  test("does not fire on a quoted prose message carrying one colon", () => {
    assert.equal(quotedStructureIn('"Unknown product: bogus"'), null);
  });
});

// ---------------------------------------------------------------------------
// eval-25 convert-from-spock
// ---------------------------------------------------------------------------

const shippingClass = (tables) => answerShape(tables.map(({ name, title = "", description = "", header, rows }) => `
    ${title ? `@DisplayName("${title}")` : ""}
    ${description ? `@Description("""\n        ${description}\n        """)` : ""}
    @TableTest("""
        ${header}
${rows.map((row) => `        ${row}`).join("\n")}
        """)
    fun ${name}(a: String, b: List<Int>, c: BigDecimal) {
        assertEquals(c, calculator.calculateShippingCost(ShippingZone("EU", "standard"), 3.0, b, PackageOptions(), Carrier.DHL))
    }
`).join("\n"));

describe("volumetricWeight and dimensionsOf", () => {
  test("divides the volume by the divisor", () => {
    assert.equal(volumetricWeight([70, 50, 10], 5000), 7);
  });

  test("reads a three-number dimensions cell", () => {
    const shape = shippingClass([
      { name: "ships", header: "Scenario | Dimensions (cm) | Cost?", rows: ["A | [70, 50, 10] | 12.50"] },
    ]);
    assert.deepEqual(dimensionsOf(shape.tables[0], shape.tables[0].rows[0]), [70, 50, 10]);
  });

  test("returns nothing where the cell is not three numbers", () => {
    const shape = shippingClass([
      { name: "ships", header: "Scenario | Dimensions (cm) | Cost?", rows: ["A | big | 12.50"] },
    ]);
    assert.deepEqual(dimensionsOf(shape.tables[0], shape.tables[0].rows[0]), []);
  });
});

describe("unpublishedConstants", () => {
  test("names the divisor where a row's volumetric weight wins and nothing states it", () => {
    const shape = shippingClass([
      {
        name: "override",
        title: "Uses the greater of actual and dimensional weight",
        header: "Scenario | Dimensions (cm) | Cost?",
        rows: ["Bulky | [70, 50, 10] | 12.50"],
      },
    ]);
    assert.deepEqual(unpublishedConstants(shape).map((one) => one.constant), ["the volumetric divisor"]);
  });

  test("accepts a column that names the divisor, since its cells carry the value", () => {
    const shape = shippingClass([
      {
        name: "override",
        header: "Scenario | Dimensions (cm) | Volumetric divisor (cm3 per kg) | Cost?",
        rows: ["Bulky | [70, 50, 10] | 5000 | 12.50"],
      },
    ]);
    assert.deepEqual(unpublishedConstants(shape), []);
  });

  test("does not read a description naming the concept as publishing the value", () => {
    const shape = shippingClass([
      {
        name: "override",
        description: "Dimensions are fixed at 10x10x10 cm (dimensional weight 0.2 kg).",
        header: "Scenario | Dimensions (cm) | Cost?",
        rows: ["Bulky | [70, 50, 10] | 12.50"],
      },
    ]);
    assert.deepEqual(unpublishedConstants(shape).map((one) => one.constant), ["the volumetric divisor"]);
  });

  test("does not read a weight of 3 kg as publishing the 3.00 insurance minimum", () => {
    const shape = shippingClass([
      {
        name: "insures",
        description: "Every row ships a 3 kg package.",
        header: "Scenario | Options | Cost?",
        rows: ["Insured | [insuredValue: 200] | 10.50"],
      },
    ]);
    const named = unpublishedConstants(shape).map((one) => one.constant);
    assert.ok(named.includes("the minimum insurance premium"), `expected the minimum premium among ${named}`);
  });

  test("accepts the decimal forms a publishing description uses", () => {
    const shape = shippingClass([
      {
        name: "insures",
        description: "Insurance adds 0.6% of the insured value or 3.00, whichever is larger.",
        header: "Scenario | Options | Cost?",
        rows: ["Insured | [insuredValue: 200] | 10.50"],
      },
    ]);
    assert.deepEqual(unpublishedConstants(shape), []);
  });

  test("says nothing about a table whose rows rely on no constant", () => {
    const shape = shippingClass([
      { name: "rates", header: "Scenario | Dimensions (cm) | Cost?", rows: ["Light | [10, 10, 10] | 7.50"] },
    ]);
    assert.deepEqual(unpublishedConstants(shape), []);
  });
});

describe("boundaryPairs", () => {
  const weights = (rows) => answerShape(`
    @TableTest("""
        Scenario | Weight (kg) | Cost?
${rows.map((row) => `        ${row}`).join("\n")}
        """)
    fun rates(weight: Double, cost: BigDecimal) {}
  `).tables[0];

  test("pairs adjacent values that straddle a change of cost", () => {
    const pairs = boundaryPairs(weights(["A | 1 | 5.00", "B | 1.01 | 7.50", "C | 5 | 7.50", "D | 5.01 | 12.50"]));
    assert.deepEqual(pairs.map((one) => one.values), [[1, 1.01], [5, 5.01]]);
  });

  test("does not pair two ordinary rows far apart", () => {
    const pairs = boundaryPairs(weights(["A | 1.01 | 7.50", "B | 5.01 | 12.50"]));
    assert.deepEqual(pairs.map((one) => one.values), [[1.01, 5.01]], "adjacent in this table, so still a pair");
  });

  test("says nothing where the cost does not move", () => {
    assert.deepEqual(boundaryPairs(weights(["A | 1.01 | 7.50", "B | 5 | 7.50"])), []);
  });
});

describe("publishesValue", () => {
  test("matches a numeral on a digit boundary", () => {
    assert.equal(publishesValue("a flat 10.00 fee", "10.00"), true);
    assert.equal(publishesValue("dimension 100 cm", "10.00"), false);
  });

  test("trims trailing zeros before matching, so 3.0 held in a body finds '3 kg'", () => {
    assert.equal(publishesValue("a 3 kg package", "3.0"), true);
  });
});

// ---------------------------------------------------------------------------
// eval-22 event-registration
// ---------------------------------------------------------------------------

const registrationClass = (tables) => answerShape(tables.map(({ name, header, rows }) => `
    @TableTest("""
        ${header}
${rows.map((row) => `        ${row}`).join("\n")}
        """)
    void ${name}(String a, String b, String c, String d) {}
`).join("\n"));

const verdictOf = (id, shape) => EVAL_22_RELATIONS.find((one) => one.id === id).evaluate(shape, {});

describe("eval22Concerns", () => {
  test("tells the validation table from the pricing one", () => {
    const shape = registrationClass([
      { name: "validates", header: "Scenario | Name | Email | Accepted? | Error Message?", rows: ["A | Ann | a@b.co | true | "] },
      { name: "prices", header: "Scenario | Registration Timing | Group Size | Discount? | Price?", rows: ["A | before cutoff | 1 | 0.20 | 80.00"] },
    ]);
    const { validation, pricing } = eval22Concerns(shape);
    assert.deepEqual([validation.map((one) => one.method), pricing.map((one) => one.method)], [["validates"], ["prices"]]);
  });
});

describe("validation-rules-covered", () => {
  test("finds a malformed email and a blank name", () => {
    const shape = registrationClass([
      {
        name: "validates",
        header: "Scenario | Name | Email | Accepted? | Error Message?",
        rows: ["Bad email | Ann | ann.example.com | false | Email format is invalid", "No name |  | ann@example.com | false | Name is required"],
      },
    ]);
    assert.equal(verdictOf("validation-rules-covered", shape).holds, true);
  });

  test("counts an explicit empty string as a missing name", () => {
    const shape = registrationClass([
      {
        name: "validates",
        header: "Scenario | Name | Email | Accepted? | Error Message?",
        rows: ["Bad email | Ann | ann.example.com | false | Email format is invalid", "No name | '' | ann@example.com | false | Name is required"],
      },
    ]);
    assert.equal(verdictOf("validation-rules-covered", shape).holds, true);
  });

  test("fails where every email is well formed", () => {
    const shape = registrationClass([
      {
        name: "validates",
        header: "Scenario | Name | Email | Accepted? | Error Message?",
        rows: ["Fine | Ann | ann@example.com | true | ", "No name |  | ben@example.com | false | Name is required"],
      },
    ]);
    assert.match(verdictOf("validation-rules-covered", shape).evidence, /malformed email/);
  });
});

describe("validation-includes-optional-fields", () => {
  test("accepts a dedicated acceptance table carrying only the optionals", () => {
    const shape = registrationClass([
      { name: "rejectsMissingName", header: "Scenario | Name | Accepted? | Error Message?", rows: ["A |  | false | Name is required"] },
      {
        name: "acceptsRegardlessOfOptionalDetails",
        header: "Scenario | Dietary Requirements | Accessibility Needs | Accepted?",
        rows: ["Supplied | vegetarian | wheelchair access | true", "Omitted |  |  | true"],
      },
    ]);
    assert.equal(verdictOf("validation-includes-optional-fields", shape).holds, true);
  });

  test("fails where an optional never varies", () => {
    const shape = registrationClass([
      {
        name: "validates",
        header: "Scenario | Name | Dietary Requirements | Accepted?",
        rows: ["A | Ann | vegetarian | true", "B | Ben | vegetarian | true"],
      },
    ]);
    assert.equal(verdictOf("validation-includes-optional-fields", shape).holds, false);
  });
});

describe("blank-for-absent-optional and wordsForAbsent", () => {
  test("names a cell standing in for absence with a word", () => {
    const shape = registrationClass([
      { name: "validates", header: "Scenario | Name | Dietary Requirements | Accepted?", rows: ["A | Ann | N/A | true"] },
    ]);
    assert.deepEqual(wordsForAbsent(shape).map((one) => one.cell), ["N/A"]);
  });

  test("accepts a blank cell", () => {
    const shape = registrationClass([
      { name: "validates", header: "Scenario | Name | Dietary Requirements | Accepted?", rows: ["A | Ann |  | true"] },
    ]);
    assert.deepEqual(wordsForAbsent(shape), []);
  });
});

describe("the date and discount relations", () => {
  const pricing = (rows, header = "Scenario | Registration Timing | Group Size | Discount? | Price?") =>
    registrationClass([{ name: "prices", header, rows }]);

  test("descriptive timing passes, a literal date fails", () => {
    assert.equal(verdictOf("descriptive-registration-date", pricing(["A | before cutoff | 1 | 0.20 | 80.00"])).holds, true);
    assert.equal(verdictOf("descriptive-registration-date", pricing(["A | 2025-02-28 | 1 | 0.20 | 80.00"])).holds, false);
  });

  test("a literal date needs the cutoff beside it — repair 11's shape", () => {
    assert.equal(verdictOf("cutoff-date-column-if-literal-dates", pricing(["A | 2025-02-28 | 1 | 0.20 | 80.00"])).holds, false);
    const withCutoff = pricing(
      ["A | 2025-02-28 | 2025-03-01 | 1 | 0.20 | 80.00"],
      "Scenario | Registration Date | Early-Bird Cutoff (Policy) | Group Size | Discount? | Price?",
    );
    assert.equal(verdictOf("cutoff-date-column-if-literal-dates", withCutoff).holds, true);
  });

  test("descriptive values pass the cutoff relation automatically", () => {
    assert.equal(verdictOf("cutoff-date-column-if-literal-dates", pricing(["A | before cutoff | 1 | 0.20 | 80.00"])).holds, true);
  });

  test("a price with no discount needs a base price column", () => {
    const priceOnly = pricing(["A | before cutoff | 1 | 80.00"], "Scenario | Registration Timing | Group Size | Price?");
    assert.equal(verdictOf("discount-column-preferred", priceOnly).holds, false);
    const withBase = pricing(
      ["A | before cutoff | 100.00 | 1 | 80.00"],
      "Scenario | Registration Timing | Base price | Group Size | Price?",
    );
    assert.equal(verdictOf("discount-column-preferred", withBase).holds, true);
  });
});


// ---------------------------------------------------------------------------
// eval-23 loan-approval
// ---------------------------------------------------------------------------

/** A one-table answer whose body calls the evaluator with the three columns, in signature order. */
const loanClass = (rows, header = "Scenario | Age | Credit Score | Stable Income | Decision?", description = "") => `
public class LoanEvaluatorTest {
${description ? `    @Description("""\n        ${description}\n        """)` : ""}
    @TableTest("""
        ${header}
${rows.map((row) => `        ${row}`).join("\n")}
        """)
    void decides(int age, int creditScore, Boolean stableIncome, ApprovalResult decision) {
        assertEquals(decision, evaluator.evaluateLoan(age, creditScore, stableIncome));
    }
}
`;

const loanShape = (...args) => answerShape(loanClass(...args));
const loanVerdict = (id, shape) => EVAL_23_RELATIONS.find((one) => one.id === id).evaluate(shape, {});

describe("eval23Inputs", () => {
  test("reads a quantity the body holds, by the position the signature gives it", () => {
    const shape = answerShape(`
      @Description("Credit score is fixed at 700, comfortably above both thresholds.")
      @TableTest("""
          Scenario        | Customer Age | Stable Income | Result?
          Stable income   | {30, 70}     | true          | APPROVED
          """)
      void decidesFromIncome(int customerAge, Boolean hasStableIncome, ApprovalResult result) {
          assertEquals(result, evaluator.evaluateLoan(customerAge, 700, hasStableIncome));
      }
    `);
    const inputs = eval23Inputs(shape.tables[0]);
    assert.equal(inputs.score.held, "700");
    assert.equal(inputs.age.column.header, "Customer Age");
    assert.equal(inputs.income.column.header, "Stable Income");
  });

  test("falls back to the headers where the body makes no call it can read", () => {
    const shape = answerShape(`
      @TableTest("""
          Scenario | Applicant Age | Credit Score | Stable Income | Result?
          A        | 64            | 651          | true          | APPROVED
          """)
      void decides(int age, int score, Boolean income, ApprovalResult result) {}
    `);
    const inputs = eval23Inputs(shape.tables[0]);
    assert.deepEqual(
      ["age", "score", "income"].map((role) => inputs[role].column.header),
      ["Applicant Age", "Credit Score", "Stable Income"],
    );
  });
});

describe("loanCases", () => {
  test("runs a value set in the income cell as the two cases it means", () => {
    const cases = loanCases(loanShape(["At the cut | 40 | 650 | {true, false} | REJECTED"]));
    assert.deepEqual(cases.map((one) => one.income), ["stable", "unstable"]);
  });

  test("reads a blank income cell as unknown, and the word for it as unresolved", () => {
    const cases = loanCases(loanShape(["Unknown | 40 | 700 |  | PENDING_REVIEW", "Worded | 40 | 700 | UNKNOWN | PENDING_REVIEW"]));
    assert.deepEqual(cases.map((one) => one.income), ["unknown", null]);
  });
});

describe("bracketingPair", () => {
  const cases = (rows) => loanCases(loanShape(rows));

  test("locates the cut where the failing row sits on the threshold", () => {
    const pair = bracketingPair(cases(["At | 40 | 650 | true | REJECTED", "Above | 40 | 651 | true | APPROVED"]), "standard");
    assert.deepEqual([pair.below.score, pair.above.score], [650, 651]);
  });

  test("refuses a pair that only says the cut is somewhere in between", () => {
    assert.equal(bracketingPair(cases(["Low | 40 | 500 | true | REJECTED", "High | 40 | 700 | true | APPROVED"]), "standard"), null);
  });

  test("refuses a pair that also moves income, which brackets nothing", () => {
    assert.equal(bracketingPair(cases(["At | 40 | 650 | false | REJECTED", "Above | 40 | 651 | true | APPROVED"]), "standard"), null);
  });

  test("names the tightest pair where several bracket the same cut", () => {
    const pair = bracketingPair(
      cases([
        "At | 40 | 650 | true | REJECTED",
        "Well above | 40 | 700 | true | APPROVED",
        "Just above | 40 | 651 | true | APPROVED",
      ]),
      "standard",
    );
    assert.equal(pair.above.score, 651);
  });
});

describe("ageBandEffect", () => {
  test("finds a pair either side of 65 holding the score and income", () => {
    const found = ageBandEffect(
      loanCases(loanShape(["Under | 64 | 601 | true | REJECTED", "Senior | 65 | 601 | true | APPROVED"])),
    );
    assert.match(found, /age 64 is REJECTED and age 65 is APPROVED/);
  });

  test("accepts two brackets at different scores, which is how the reference states it", () => {
    const found = ageBandEffect(
      loanCases(
        loanShape([
          "Standard at | 64 | 650 | true | REJECTED",
          "Standard above | 64 | 651 | true | APPROVED",
          "Senior at | 65 | 600 | true | REJECTED",
          "Senior above | 65 | 601 | true | APPROVED",
        ]),
      ),
    );
    assert.match(found, /bracket at different scores/);
  });

  test("says nothing where only one band is exercised", () => {
    assert.equal(
      ageBandEffect(loanCases(loanShape(["At | 40 | 650 | true | REJECTED", "Above | 40 | 651 | true | APPROVED"]))),
      null,
    );
  });
});

describe("depth-stable-income-effect", () => {
  test("finds two rows holding the age and score cells while income moves", () => {
    const shape = loanShape(["Stable | 64 | 651 | true | APPROVED", "Unstable | 64 | 651 | false | REJECTED"]);
    assert.ok(heldConstantIncomePair(shape));
    assert.equal(loanVerdict("depth-stable-income-effect", shape).holds, true);
  });

  test("refuses a comparison available only after expanding a value set", () => {
    const shape = loanShape([
      "Unstable | 30 | 700 | false | REJECTED",
      "Unknown  | {30, 70} | {700, 500} |  | PENDING_REVIEW",
    ]);
    assert.equal(heldConstantIncomePair(shape), null);
    // The case is in there, and the relation says so rather than reporting nothing at all.
    assert.match(incomeEffectCases(loanCases(shape)), /at age 30 and score 700/);
    assert.match(loanVerdict("depth-stable-income-effect", shape).evidence, /only after expanding a value set/);
  });

  test("holds where the score is held in the body, since it is then equal by construction", () => {
    const shape = answerShape(`
      @Description("Credit score is fixed at 700.")
      @TableTest("""
          Scenario  | Customer Age | Stable Income | Result?
          Stable    | {30, 70}     | true          | APPROVED
          Unstable  | {30, 70}     | false         | REJECTED
          """)
      void decidesFromIncome(int customerAge, Boolean hasStableIncome, ApprovalResult result) {
          assertEquals(result, evaluator.evaluateLoan(customerAge, 700, hasStableIncome));
      }
    `);
    assert.equal(loanVerdict("depth-stable-income-effect", shape).holds, true);
  });
});

describe("blank-for-unknown-income", () => {
  test("passes a blank cell on a Boolean parameter, and quotes the row", () => {
    const verdict = loanVerdict("blank-for-unknown-income", loanShape(["Unknown | 64 | 651 |  | PENDING_REVIEW"]));
    assert.equal(verdict.holds, true);
    assert.match(verdict.evidence, /Unknown \| 64 \| 651/);
  });

  test("fails a word standing in for the blank", () => {
    const verdict = loanVerdict("blank-for-unknown-income", loanShape(["Unknown | 64 | 651 | null | PENDING_REVIEW"]));
    assert.equal(verdict.holds, false);
    assert.match(verdict.evidence, /Stable Income = "null"/);
  });

  test("fails a primitive parameter, which no blank can reach as null", () => {
    const shape = answerShape(`
      @TableTest("""
          Scenario | Age | Credit Score | Stable Income | Decision?
          Unknown  | 64  | 651          |               | PENDING_REVIEW
          """)
      void decides(int age, int creditScore, boolean stableIncome, ApprovalResult decision) {}
    `);
    assert.match(loanVerdict("blank-for-unknown-income", shape).evidence, /primitive parameter/);
  });

  test("names a second column splitting the one Boolean input in two", () => {
    const shape = answerShape(`
      @TableTest("""
          Scenario | Age | Credit Score | Income Known? | Stable Income | Decision?
          Unknown  | 64  | 651          | false         | true          | PENDING_REVIEW
          """)
      void decides(int age, int creditScore, boolean incomeKnown, boolean stableIncome, ApprovalResult decision) {}
    `);
    assert.deepEqual(splitIncomeColumns(shape).map((one) => one.header), ["Income Known?"]);
  });
});

describe("held-constants-declared", () => {
  test("names a held value the table's own surface never states", () => {
    const shape = answerShape(`
      @DisplayName("Decides from income")
      @TableTest("""
          Scenario | Customer Age | Stable Income | Result?
          Stable   | 30           | true          | APPROVED
          """)
      void decidesFromIncome(int customerAge, Boolean hasStableIncome, ApprovalResult result) {
          assertEquals(result, evaluator.evaluateLoan(customerAge, 700, hasStableIncome));
      }
    `);
    assert.deepEqual(undeclaredHeldValues(shape).map((one) => [one.role, one.value]), [["score", "700"]]);
  });

  test("accepts the same value once the description states it", () => {
    const shape = answerShape(`
      @Description("Credit score is fixed at 700, above both thresholds.")
      @TableTest("""
          Scenario | Customer Age | Stable Income | Result?
          Stable   | 30           | true          | APPROVED
          """)
      void decidesFromIncome(int customerAge, Boolean hasStableIncome, ApprovalResult result) {
          assertEquals(result, evaluator.evaluateLoan(customerAge, 700, hasStableIncome));
      }
    `);
    assert.deepEqual(undeclaredHeldValues(shape), []);
  });
});

describe("description-no-redundant-field-values", () => {
  test("fires on a scenario name echoing a value incidental to its row", () => {
    const found = echoedInputValues(loanShape(["Age 35 applicant | 35 | 700 | true | APPROVED"]));
    assert.deepEqual(found.map((one) => [one.header, one.value]), [["Age", "35"]]);
  });

  test("exempts a name stating the rule's own boundary, even where the cell holds it", () => {
    assert.deepEqual(echoedInputValues(loanShape(["At the 650 threshold | 40 | 650 | true | REJECTED"])), []);
  });

  test("fires where the description pins a column to the one value every row shows", () => {
    const shape = loanShape(
      ["A | 35 | 700 | true | APPROVED", "B | 35 | 500 | true | REJECTED"],
      "Scenario | Age | Credit Score | Stable Income | Decision?",
      "Every row is a 35 year old applicant.",
    );
    assert.deepEqual(descriptionPinsAColumn(shape).map((one) => one.header), ["Age"]);
  });

  test("says nothing about a constant that is not a column", () => {
    const shape = loanShape(
      ["A | 35 | 700 | true | APPROVED", "B | 40 | 500 | true | REJECTED"],
      "Scenario | Age | Credit Score | Stable Income | Decision?",
      "Applicants above the 650 threshold are approved when income is stable.",
    );
    assert.deepEqual(descriptionPinsAColumn(shape), []);
  });
});

describe("scenario-names-describe-conditions", () => {
  test("fires on a name paraphrasing its own row's decision", () => {
    const found = namesStatingTheOutcome(loanShape(["Below threshold rejects regardless of income | 40 | 500 | true | REJECTED"]));
    assert.deepEqual(found.map((one) => one.why), ["paraphrases REJECTED"]);
  });

  test("says nothing about a name describing an input, however close it sounds", () => {
    assert.deepEqual(namesStatingTheOutcome(loanShape(["Qualifying score, income not stable | 64 | 651 | false | REJECTED"])), []);
  });

  test("fires on a label naming no variation at all", () => {
    assert.deepEqual(namesStatingTheOutcome(loanShape(["Test 1 | 64 | 651 | true | APPROVED"])).map((one) => one.why), [
      "names no variation",
    ]);
  });
});

describe("rowsRediscarging", () => {
  test("names a row re-showing an income rule an earlier row already stated", () => {
    const found = rowsRediscarging(
      loanShape([
        "Standard unstable | 40 | 651 | false | REJECTED",
        "Senior unstable   | 65 | 700 | false | REJECTED",
      ]),
    );
    assert.deepEqual(found.map((one) => one.row), [2]);
  });

  test("exempts the two halves of an age crossing, which state the band rule", () => {
    const found = rowsRediscarging(
      loanShape([
        "Standard above | 40 | 651 | true | APPROVED",
        "Under the cut  | 64 | 620 | true | REJECTED",
        "At the cut     | 65 | 620 | true | APPROVED",
      ]),
    );
    assert.deepEqual(found, []);
  });

  test("exempts a boundary row whose other side disagrees with it", () => {
    const found = rowsRediscarging(
      loanShape([
        "Standard above | 40 | 651 | true | APPROVED",
        "Senior at      | 65 | 600 | true | REJECTED",
        "Senior above   | 65 | 601 | true | APPROVED",
      ]),
    );
    assert.deepEqual(found, []);
  });

  test("counts a value set as the obligations it discharges, not as a repeat", () => {
    const found = rowsRediscarging(
      loanShape([
        "Below, stable | 64 | 500 | true | REJECTED",
        "Below, either | 40 | 500 | {true, false} | REJECTED",
      ]),
    );
    assert.deepEqual(found, []);
  });
});
