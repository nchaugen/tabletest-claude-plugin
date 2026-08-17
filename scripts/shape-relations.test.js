const { test, describe } = require("node:test");
const assert = require("node:assert/strict");

const { answerShape } = require("./answer-shape.js");
const { storedDraws, evaluateDraw } = require("./shape-report.js");
const {
  EVAL_18_RELATIONS,
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

describe("the reference answer", () => {
  test("satisfies every eval-18 relation — a relation it fails is transcribed wrongly", () => {
    const draws = storedDraws("tabletest", "eval-18-convert-from-code");
    const reference = draws.find((draw) => draw.isReference);
    assert.ok(reference, "expected a stored reference answer for eval-18");

    const rows = evaluateDraw(reference, EVAL_18_RELATIONS, { sutParameters: SUT });
    const failed = rows.filter((row) => !row.holds).map((row) => `${row.id}: ${row.evidence}`);
    assert.deepEqual(failed, []);
  });
});

