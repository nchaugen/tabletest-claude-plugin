const { test, describe } = require("node:test");
const assert = require("node:assert");

const {
  render,
  matchCase,
  findAssertionDrift,
  loadSharedAssertions,
  loadVocabulary,
} = require("./build-skills.js");

const vocabulary = {
  names: "scenario ids",
  row: "case",
  rows: "cases",
  every_table: "every parametrized test in the file",
};

describe("placeholder case", () => {
  test("renders the entry as written for a lowercase placeholder", () => {
    assert.equal(matchCase("rows", "cases"), "cases");
  });

  test("capitalises for a sentence-initial placeholder", () => {
    assert.equal(matchCase("Rows", "cases"), "Cases");
  });

  test("upper-cases for a shouted placeholder", () => {
    assert.equal(matchCase("ROWS", "cases"), "CASES");
  });

  test("capitalises only the first letter of a multi-word phrase", () => {
    assert.equal(matchCase("Names", "scenario ids"), "Scenario ids");
  });
});

describe("render", () => {
  test("substitutes every placeholder from the suite's vocabulary", () => {
    assert.equal(
      render("Judge {{every_table}}; a {{row}} states one rule.", vocabulary, "test"),
      "Judge every parametrized test in the file; a case states one rule."
    );
  });

  test("resolves a cased placeholder from the same lowercase entry", () => {
    assert.equal(render("{{ROWS}} and {{Rows}} and {{rows}}", vocabulary, "test"), "CASES and Cases and cases");
  });

  test("throws on an unknown placeholder rather than sending a literal brace to the grader", () => {
    assert.throws(
      () => render("Judge {{columns}} in isolation.", vocabulary, "shared/assertions/x.md"),
      /Unknown placeholder \{\{columns\}\} in shared\/assertions\/x\.md/
    );
  });

  test("leaves text with no placeholders untouched", () => {
    assert.equal(render("PASSES otherwise.", vocabulary, "test"), "PASSES otherwise.");
  });
});

describe("the shared sources and the copies generated from them", () => {
  test("every eval carrying a shared assertion id has the text its source renders to", () => {
    const drift = findAssertionDrift();
    assert.deepEqual(
      drift.map(d => `${d.file} — ${d.id}`),
      [],
      "run: node scripts/build-skills.js"
    );
  });

  test("every suite declares the same vocabulary keys", () => {
    const vocabularies = loadVocabulary();
    const suites = Object.keys(vocabularies);
    const keysOf = (suite) => Object.keys(vocabularies[suite]).sort();
    for (const suite of suites.slice(1)) {
      assert.deepEqual(keysOf(suite), keysOf(suites[0]), `${suite} declares different keys from ${suites[0]}`);
    }
  });

  test("every placeholder a shared text uses is declared by every suite", () => {
    const vocabularies = loadVocabulary();
    for (const [id, template] of Object.entries(loadSharedAssertions())) {
      for (const [suite, words] of Object.entries(vocabularies)) {
        assert.doesNotThrow(() => render(template, words, `shared/assertions/${id}.md`), `${id} in ${suite}`);
      }
    }
  });
});
