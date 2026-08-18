const { test, describe } = require("node:test");
const assert = require("node:assert");

const { candidacy, rank, gradingVerdicts } = require("./conversion-candidates.js");

/** One relation's row against a set of stored draws, as `evaluateDraw` returns them. */
const rows = (pairs, extra = {}) =>
  pairs.map(([holds, graded]) => ({ id: "slot", holds, graded, agrees: graded === null ? null : graded === holds, ...extra }));

describe("candidacy", () => {
  test("recommends a slot that agrees on every draw and has failed at least once", () => {
    const result = candidacy("slot", rows([[true, true], [false, false], [true, true]]), {});
    assert.equal(result.verdict, "convert");
    assert.equal(result.comparisons, 3);
    assert.equal(result.agreeing, 3);
  });

  test("withholds a slot that has only ever passed, which has caught nothing", () => {
    const result = candidacy("slot", rows([[true, true], [true, true]]), {});
    assert.equal(result.verdict, "one-directional");
    assert.match(result.why, /only ever/i);
  });

  test("withholds a slot that has only ever failed, for the same reason", () => {
    assert.equal(candidacy("slot", rows([[false, false], [false, false]]), {}).verdict, "one-directional");
  });

  test("withholds a slot the relation and grader disagree on", () => {
    const result = candidacy("slot", rows([[true, true], [false, true], [false, false]]), {});
    assert.equal(result.verdict, "disagrees");
    assert.match(result.why, /1 of 3/);
  });

  test("refuses a relation carrying an exemption it cannot decide", () => {
    const result = candidacy("slot", rows([[true, true], [false, false]]), { judgement: "a domain reading" });
    assert.equal(result.verdict, "advisory");
    assert.match(result.why, /a domain reading/);
  });

  test("reports a slot already converted rather than recommending it again", () => {
    const result = candidacy("slot", rows([[true, true], [false, false]]), { converted: true });
    assert.equal(result.verdict, "converted");
  });

  test("withholds a slot with too few stored comparisons to read", () => {
    const result = candidacy("slot", rows([[true, true], [false, false]]).slice(0, 1), {});
    assert.equal(result.verdict, "too-few");
  });

  test("ignores draws with no stored grading, which are not comparisons", () => {
    const result = candidacy("slot", rows([[true, true], [false, null], [false, false], [true, true]]), {});
    assert.equal(result.comparisons, 3);
    assert.equal(result.verdict, "convert");
  });
});

describe("rank", () => {
  test("puts convertible slots first, then the reasons they were withheld", () => {
    const ordered = rank([
      { id: "b", verdict: "advisory" },
      { id: "a", verdict: "convert" },
      { id: "c", verdict: "converted" },
      { id: "d", verdict: "convert" },
    ]);
    assert.deepEqual(ordered.map((one) => one.id), ["a", "d", "b", "c"]);
  });
});

describe("gradingVerdicts", () => {
  const draws = [{ source: "a" }, { source: "b" }];
  const relationRows = [
    { id: "scenario-names-describe-conditions", holds: true, graded: true, agrees: true },
    { id: "scenario-names-describe-conditions", holds: true, graded: false, agrees: false },
  ];

  test("measures the hand-written checker where one exists, not the relation", () => {
    const { rows, path } = gradingVerdicts("scenario-names-describe-conditions", draws, relationRows, "permission-check");
    assert.equal(path, "checker");
    // Neither source is a table, so the checker fails both — the point is that its verdicts, not
    // the relation's, are what got measured.
    assert.deepEqual(rows.map((row) => row.holds), [false, false]);
    assert.deepEqual(rows.map((row) => row.graded), [true, false]);
  });

  test("falls back to the relation's rows for an assertion no checker implements", () => {
    const { rows, path } = gradingVerdicts("no-such-checker", draws, relationRows, "permission-check");
    assert.equal(path, "relation");
    assert.equal(rows, relationRows);
  });
});
