const { test, describe } = require("node:test");
const assert = require("node:assert/strict");

const {
  answerShape,
  cellValues,
  constantExpectationColumns,
  findColumn,
  numericValue,
} = require("./answer-shape.js");

/** A table with a scenario column, one value set, and two expectation columns. */
const twoConcerns = `
class InsuranceEvaluatorTest {
    @TableTest("""
        Scenario        | Applicant Type | Age      | Claim Count | Decision? | Premium?
        In the thirties | NEW            | {30, 39} | 0           | APPROVED  | 106.0
        One claim       | NEW            | 30       | 1           | APPROVED  | 136.0
        """)
    void pricesAnApplication(String applicantType, int age, int claimCount, String decision, double premium) {
    }
}
`;

describe("answerShape", () => {
  test("reads a table's columns, marking the scenario column and the expectation columns", () => {
    const [table] = answerShape(twoConcerns).tables;
    assert.equal(table.method, "pricesAnApplication");
    assert.deepEqual(
      table.columns.map((column) => [column.header, column.isScenario, column.isExpectation]),
      [
        ["Scenario", true, false],
        ["Applicant Type", false, false],
        ["Age", false, false],
        ["Claim Count", false, false],
        ["Decision?", false, true],
        ["Premium?", false, true],
      ],
    );
  });

  test("maps each column to the parameter it feeds, past the scenario column", () => {
    const [table] = answerShape(twoConcerns).tables;
    const age = table.columns.find((column) => column.header === "Age");
    assert.deepEqual(age.param, { name: "age", type: "int" });
  });

  test("expands a value set into the cases the row runs as", () => {
    const [table] = answerShape(twoConcerns).tables;
    assert.equal(table.rows.length, 2);
    assert.deepEqual(
      table.cases.map((one) => [one["Age"], one["Claim Count"], one["Premium?"]]),
      [
        ["30", "0", "106.0"],
        ["39", "0", "106.0"],
        ["30", "1", "136.0"],
      ],
    );
  });

  test("counts a row whose cell count disagrees with the header rather than dropping it silently", () => {
    const ragged = `
      @TableTest("""
          Scenario | Age | Premium?
          Short    | 30
          Whole    | 40  | 108.0
          """)
      void prices(int age, double premium) {}
    `;
    const [table] = answerShape(ragged).tables;
    assert.equal(table.rows.length, 1);
    assert.equal(table.malformedRows, 1);
  });

  test("returns no tables for a source that never reached a @TableTest", () => {
    assert.deepEqual(answerShape("class Nothing {}").tables, []);
  });
});

describe("cellValues", () => {
  test("runs a braced cell once per value where the parameter is not a Set", () => {
    assert.deepEqual(cellValues("{30, 39}", { name: "age", type: "int" }), ["30", "39"]);
  });

  test("keeps a braced cell whole where the parameter is declared a Set, which is the literal", () => {
    assert.deepEqual(cellValues("{a, b}", { name: "tags", type: "Set<String>" }), ["{a, b}"]);
  });

  test("keeps a list cell whole — brackets are never a value set", () => {
    assert.deepEqual(cellValues("[a, b]", { name: "items", type: "List<String>" }), ["[a, b]"]);
  });

  test("passes an ordinary cell through", () => {
    assert.deepEqual(cellValues("APPROVED", { name: "decision", type: "String" }), ["APPROVED"]);
  });
});

describe("constantExpectationColumns", () => {
  test("names an expectation column holding one value across every row", () => {
    const [table] = answerShape(`
      @TableTest("""
          Scenario | Age | Decision? | Premium?
          Younger  | 30  | APPROVED  | 106.0
          Older    | 64  | APPROVED  | 112.0
          """)
      void prices(int age, String decision, double premium) {}
    `).tables;
    assert.deepEqual(constantExpectationColumns(table), [{ header: "Decision?", value: "APPROVED" }]);
  });

  test("says nothing about a single-row table, where every column is trivially constant", () => {
    const [table] = answerShape(`
      @TableTest("""
          Scenario | Age | Decision?
          Only row | 30  | APPROVED
          """)
      void decides(int age, String decision) {}
    `).tables;
    assert.deepEqual(constantExpectationColumns(table), []);
  });
});

describe("findColumn", () => {
  test("finds a column by its header", () => {
    const [table] = answerShape(twoConcerns).tables;
    assert.equal(findColumn(table, /claim/i).header, "Claim Count");
  });

  test("falls back to the parameter name where the header does not say it", () => {
    const [table] = answerShape(`
      @TableTest("""
          A  | B?
          30 | 106.0
          """)
      void prices(int age, double premium) {}
    `).tables;
    assert.equal(findColumn(table, /age/i).header, "A");
  });

  test("returns null where no column carries the role", () => {
    const [table] = answerShape(twoConcerns).tables;
    assert.equal(findColumn(table, /zone/i), null);
  });
});

describe("numericValue", () => {
  test("reads an integer and a decimal", () => {
    assert.equal(numericValue("64"), 64);
    assert.equal(numericValue("273.5"), 273.5);
  });

  test("returns null for a blank, a name, or a value set left unexpanded", () => {
    assert.equal(numericValue(""), null);
    assert.equal(numericValue("APPROVED"), null);
    assert.equal(numericValue("{30, 39}"), null);
  });
});
