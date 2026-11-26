const { test, describe } = require("node:test");
const assert = require("node:assert");

const {
  checkers,
  splitRowCells,
  parseCollectionElements,
  parseParameterList,
  tableTestTables,
  parseTableHeaders,
  countDataRows,
} = require("./assertions.js");

const testSource = (content) => ({
  fileContent: content,
  allFiles: [
    { path: "build.gradle", content: "plugins {\n    id 'java'\n}\n" },
    { path: "src/test/java/com/example/DateParserTest.java", content },
  ],
});

const annotationOrder = (content) => checkers["annotation-order"](testSource(content));

describe("annotation-order", () => {
  test("accepts a method with @DisplayName and no @Description after one that has both", () => {
    const result = annotationOrder(`class DateParserTest {

    @DisplayName("Parses date strings in ISO, slash, and short-year formats")
    @Description("""
        The parser accepts three shapes.
        """)
    @TableTest("""
        Input      | Parsed
        2026-07-30 | 2026-07-30
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsed) {
        assertThat(parse(input)).isEqualTo(parsed);
    }

    @DisplayName("Rejects empty date input")
    @TableTest("""
        Input | Throws?
        ""    | IllegalArgumentException
        """)
    void rejectsEmptyInput(String input, Class<? extends Throwable> throws_) {
        assertThatThrownBy(() -> parse(input)).isInstanceOf(throws_);
    }
}
`);

    assert.equal(result.passed, true, result.evidence);
  });

  test("rejects @Description before @DisplayName within one method", () => {
    const result = annotationOrder(`class DateParserTest {

    @Description("""
        The parser accepts three shapes.
        """)
    @DisplayName("Parses date strings in ISO, slash, and short-year formats")
    @TableTest("""
        Input      | Parsed
        2026-07-30 | 2026-07-30
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsed) {
    }
}
`);

    assert.equal(result.passed, false);
    assert.match(result.evidence, /@DisplayName \(line 6\) after @Description \(line 3\)/);
  });

  test("rejects an out-of-order pair separated by a description longer than 20 lines", () => {
    const prose = Array.from({ length: 25 }, (_, i) => `        Paragraph line ${i + 1}.`).join("\n");
    const result = annotationOrder(`class DateParserTest {

    @Description("""
${prose}
        """)
    @DisplayName("Parses date strings in ISO, slash, and short-year formats")
    @TableTest("""
        Input      | Parsed
        2026-07-30 | 2026-07-30
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsed) {
    }
}
`);

    assert.equal(result.passed, false);
    assert.match(result.evidence, /@DisplayName .* after @Description/);
  });

  test("reports line numbers of the test source file, not of the concatenated outputs", () => {
    const result = annotationOrder(`class DateParserTest {

    @Description("""
        The parser accepts three shapes.
        """)
    @DisplayName("Parses date strings in ISO, slash, and short-year formats")
    @TableTest("""
        Input      | Parsed
        2026-07-30 | 2026-07-30
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsed) {
    }
}
`);

    assert.match(result.evidence, /src\/test\/java\/com\/example\/DateParserTest\.java/);
    assert.match(result.evidence, /line 6/);
  });

  test("rejects @DisplayName placed after @TableTest", () => {
    const result = annotationOrder(`class DateParserTest {

    @TableTest("""
        Input      | Parsed
        2026-07-30 | 2026-07-30
        """)
    @DisplayName("Parses date strings in ISO, slash, and short-year formats")
    void parsesSupportedDateFormats(String input, LocalDate parsed) {
    }
}
`);

    assert.equal(result.passed, false);
    assert.match(result.evidence, /@DisplayName \(line 7\) after @TableTest \(line 3\)/);
  });

  test("accepts a class whose methods carry no title annotations at all", () => {
    const result = annotationOrder(`class DateParserTest {

    @TableTest("""
        Input      | Parsed
        2026-07-30 | 2026-07-30
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsed) {
    }
}
`);

    assert.equal(result.passed, true, result.evidence);
  });
});

describe("no-if-switch-in-method", () => {
  const check = (body) => checkers["no-if-switch-in-method"]({
    fileContent: `@TableTest("""
    Input | Ok?
    a     | true
    b     | false
    """)
void validatesInput(String input, boolean ok) {
${body}
}`,
    allFiles: [],
  });

  test("catches a ternary written across several lines", () => {
    const result = check(`    Result expected = ok
        ? Result.ok(input)
        : Result.error(input);
    assertEquals(expected, validate(input));`);

    assert.equal(result.passed, false, result.evidence);
    assert.match(result.evidence, /ternary/);
  });

  test("catches a ternary written on one line", () => {
    const result = check(`    Result expected = ok ? Result.ok(input) : Result.error(input);
    assertEquals(expected, validate(input));`);

    assert.equal(result.passed, false, result.evidence);
  });

  test("does not read a generic wildcard as a ternary", () => {
    const result = check(`    Class<? extends Throwable> expected = IllegalArgumentException.class;
    assertEquals(expected, thrownBy(() -> validate(input)));`);

    assert.equal(result.passed, true, result.evidence);
  });

  test("does not join a question mark and a colon from separate statements", () => {
    const result = check(`    String prompt = "ready?";
    Map<String, String> labels = Map.of("k", "v");
    assertEquals(ok, validate(input, prompt, labels));`);

    assert.equal(result.passed, true, result.evidence);
  });
});

describe("scenario-names-describe-conditions", () => {
  const table = (rows) => `
    @TableTest("""
      Scenario | Role | Action | Allowed?
      ${rows.join("\n      ")}
      """)
    void grantsPermissionsAccordingToRole(Role role, Action action, boolean allowed) {}
  `;
  const verdict = (rows, evalSlug = "permission-check") =>
    checkers["scenario-names-describe-conditions"]({ fileContent: table(rows), allFiles: [], evalSlug });

  test("fails a name paraphrasing its own row's expectation, the assertion's worked example", () => {
    const result = verdict(["User cannot delete | USER | DELETE | false"]);
    assert.equal(result.passed, false, result.evidence);
    assert.match(result.evidence, /User cannot delete/);
  });

  test("fails a name echoing a positive outcome", () => {
    assert.equal(verdict(["Admin can perform any action | ADMIN | DELETE | true"]).passed, false);
  });

  test("passes a name stating the situation, even when the outcome is inferable", () => {
    assert.equal(verdict(["Missing name | USER | DELETE | false"]).passed, true);
    assert.equal(verdict(["Viewer on a restricted action | VIEWER | DELETE | false"]).passed, true);
  });

  test("fails a generic label, which names no variation", () => {
    assert.equal(verdict(["Test 1 | USER | READ | true"]).passed, false);
  });

  test("does not fail a name whose echo belongs to the other outcome", () => {
    assert.equal(verdict(["Cannot be reached by a viewer | VIEWER | DELETE | true"]).passed, true);
  });

  test("passes eval-7's reference answer, whose names state the action without the modal", () => {
    const reference = [
      "Admin performs any action | ADMIN | {READ, WRITE, DELETE} | true",
      "User reads or writes | USER | {READ, WRITE} | true",
      "User deletes | USER | DELETE | false",
      "Guest reads | GUEST | READ | true",
      "Guest writes or deletes | GUEST | {WRITE, DELETE} | false",
    ];
    assert.equal(verdict(reference).passed, true, "the reference answer must pass its own assertion");
  });

  test("fails loudly for an eval with no registered vocabulary, rather than passing vacuously", () => {
    const result = verdict(["User cannot delete | USER | DELETE | false"], "some-other-eval");
    assert.equal(result.passed, false);
    assert.match(result.evidence, /vocabulary/i);
  });
});

describe("table cell tokenizer", () => {
  test("keeps a trailing blank cell, which edge-pipe trimming must not swallow", () => {
    assert.deepEqual(splitRowCells("Empty means blank | "), ["Empty means blank", ""]);
  });

  test("does not split on a pipe inside a quoted collection element", () => {
    const cells = splitRowCells('Keeps a pipe | ["tech:milestone|v2", "biz:sales"] | tech');
    assert.deepEqual(cells, ['Keeps a pipe', '["tech:milestone|v2", "biz:sales"]', 'tech']);
  });

  test("preserves a blank cell instead of dropping it", () => {
    assert.deepEqual(splitRowCells("No optional set |  | [a]"), ["No optional set", "", "[a]"]);
  });

  test("treats an apostrophe inside a value as an ordinary character", () => {
    assert.deepEqual(splitRowCells("Adult's discount | ADULT | 5"), ["Adult's discount", "ADULT", "5"]);
  });

  test("splits a row whose scenario name ends in a possessive plural", () => {
    assert.deepEqual(splitRowCells("Correction exceeds the categories' pay | -100 | 0"), [
      "Correction exceeds the categories' pay",
      "-100",
      "0",
    ]);
  });

  test("still quotes a value that opens with a quote", () => {
    assert.deepEqual(splitRowCells("Pipe in a quoted cell | 'a|b' | 2"), ["Pipe in a quoted cell", "'a|b'", "2"]);
  });

  test("splits collection elements on commas at the collection's own depth", () => {
    assert.deepEqual(parseCollectionElements('["a, still one", b]'), ['"a, still one"', "b"]);
    assert.deepEqual(parseCollectionElements("[[a, b], [c]]"), ["[a, b]", "[c]"]);
  });

  test("reports an empty collection as no elements and a blank element as one", () => {
    assert.deepEqual(parseCollectionElements("[]"), []);
    assert.deepEqual(parseCollectionElements("[a, , c]"), ["a", "", "c"]);
  });

  test("is not a collection when the cell is a scalar", () => {
    assert.equal(parseCollectionElements("tech"), null);
  });

  test("reads parameter types from both Java and Kotlin signatures", () => {
    assert.deepEqual(parseParameterList("List<String> tags, String category"), [
      { name: "tags", type: "List<String>" },
      { name: "category", type: "String" },
    ]);
    assert.deepEqual(parseParameterList("tags: List<String>, category: String"), [
      { name: "tags", type: "List<String>" },
      { name: "category", type: "String" },
    ]);
  });
});

describe("eval-20 collection and quoting checkers", () => {
  const table = (body, signature) => ({
    fileContent: `class T {\n  @TableTest("""\n${body}\n  """)\n  void ${signature} { }\n}`,
    allFiles: [],
  });

  test("list-syntax-correct fails a bare comma-separated cell in a List column", () => {
    const bad = table("Scenario | Tags\nNo brackets | tech:java, dev:backend", "t(List<String> tags)");
    assert.equal(checkers["list-syntax-correct"](bad).passed, false);
    const good = table("Scenario | Tags\nBrackets | [\"tech:java\"]", "t(List<String> tags)");
    assert.equal(checkers["list-syntax-correct"](good).passed, true);
  });

  test("set-syntax-correct fails bracket syntax in a Set column but ignores a value set on a String column", () => {
    const bad = table("Scenario | Opt\nBrackets | [a, b]", "t(Set<String> opt)");
    assert.equal(checkers["set-syntax-correct"](bad).passed, false);
    const valueSet = table("Scenario | Category\nRuns thrice | {tech, biz}", "t(String category)");
    assert.equal(checkers["set-syntax-correct"](valueSet).passed, true);
  });

  test("empty-list-explicit fails when no List cell is written as []", () => {
    const blank = table("Scenario | Tags\nEmpty means blank | ", "t(List<String> tags)");
    assert.equal(checkers["empty-list-explicit"](blank).passed, false);
    const explicit = table("Scenario | Tags\nEmpty is explicit | []", "t(List<String> tags)");
    assert.equal(checkers["empty-list-explicit"](explicit).passed, true);
  });

  test("special-chars-quoted fails an unquoted colon in a List column, which would parse as a map", () => {
    const bad = table("Scenario | Tags\nUnquoted | [tech:java, dev:ci]", "t(List<String> tags)");
    assert.equal(checkers["special-chars-quoted"](bad).passed, false);
    const good = table('Scenario | Tags\nQuoted | ["tech:java"]', "t(List<String> tags)");
    assert.equal(checkers["special-chars-quoted"](good).passed, true);
  });

  test("special-chars-quoted leaves a Map column's colons alone", () => {
    const map = table("Scenario | Opts\nMap cell | [fragile: true, insured: 500]", "t(Map<String,String> opts)");
    assert.equal(checkers["special-chars-quoted"](map).passed, true);
  });

  test("special-chars-quoted leaves a map entry whose value is a nested map alone", () => {
    const nested = table(
      "Scenario | Stock\nTwo warehouses | [W1: [p1: IN_STOCK, p2: IN_STOCK], W2: [p1: IN_STOCK]]",
      "t(Map<String,Map<String,String>> stock)"
    );
    assert.equal(checkers["special-chars-quoted"](nested).passed, true);
  });

  test("special-chars-quoted still fails a stray bracket in a map entry's value", () => {
    const stray = table("Scenario | Opts\nStray | [label: a]b]", "t(Map<String,String> opts)");
    assert.equal(checkers["special-chars-quoted"](stray).passed, false);
  });

  test("pipe-quoted fails an unquoted pipe inside a collection", () => {
    const bad = table("Scenario | Tags\nUnquoted pipe | [biz:hr|recruiting]", "t(List<String> tags)");
    assert.equal(checkers["pipe-quoted"](bad).passed, false);
    const good = table('Scenario | Tags\nQuoted pipe | ["biz:hr|recruiting"]', "t(List<String> tags)");
    assert.equal(checkers["pipe-quoted"](good).passed, true);
  });

  test("no-blank-collection-elements fails a blank element in any position", () => {
    for (const cell of ["[a, , c]", "[a, b, ]", "[, a, b]"]) {
      const bad = table(`Scenario | Tags\nBlank element | ${cell}`, "t(List<String> tags)");
      assert.equal(checkers["no-blank-collection-elements"](bad).passed, false, cell);
    }
    const quoted = table("Scenario | Tags\nQuoted empty | ['', a]", "t(List<String> tags)");
    assert.equal(checkers["no-blank-collection-elements"](quoted).passed, true);
  });

  test("newline-in-cell fails a row whose cell count disagrees with the header", () => {
    const broken = table("Scenario | Tags | Category\nSplit row | [a]", "t(List<String> tags, String category)");
    assert.equal(checkers["newline-in-cell"](broken).passed, false);
    const intact = table("Scenario | Tags | Category\nIntact | [a] | tech", "t(List<String> tags, String category)");
    assert.equal(checkers["newline-in-cell"](intact).passed, true);
  });
});

describe("column-to-parameter mapping", () => {
  const cls = (body, signature) =>
    `class T {\n  @TableTest("""\n${body}\n  """)\n  void ${signature} { }\n}`;

  test("maps column 0 to parameter 0 when the table has no scenario column", () => {
    const [t] = tableTestTables(cls("Numbers | Sum?\n[1, 2] | 3", "sums(List<Integer> numbers, int sum)"));
    assert.equal(t.hasScenarioColumn, false);
    assert.deepEqual(t.headers, ["Numbers", "Sum?"]);
  });

  test("skips the scenario column when the table has one", () => {
    const [t] = tableTestTables(cls("Scenario | Numbers | Sum?\nTwo | [1, 2] | 3", "sums(List<Integer> numbers, int sum)"));
    assert.equal(t.hasScenarioColumn, true);
  });

  test("does not mistake an int expectation column for the List column beside it", () => {
    const noScenario = { fileContent: cls("Numbers | Sum?\n[1, 2] | 3", "sums(List<Integer> numbers, int sum)"), allFiles: [] };
    assert.equal(checkers["list-syntax-correct"](noScenario).passed, true);
    const set = { fileContent: cls("Values | Size?\n{1, 2} | 2", "counts(Set<Integer> values, int size)"), allFiles: [] };
    assert.equal(checkers["set-syntax-correct"](set).passed, true);
  });
});

describe("annotation-order vacuity", () => {
  const src = (content) => ({ fileContent: content, allFiles: [{ path: "src/test/java/T.java", content }] });

  test("marks the verdict VACUOUS when no method carries an orderable annotation", () => {
    const r = checkers["annotation-order"](src('class T {\n  @TableTest("""\n  A | B?\n  1 | 2\n  """)\n  void t(int a, int b) { }\n}'));
    assert.equal(r.passed, true);
    assert.match(r.evidence, /^VACUOUS/);
  });

  test("counts the annotated methods it actually ordered", () => {
    const r = checkers["annotation-order"](src('class T {\n  @DisplayName("x")\n  @TableTest("""\n  A | B?\n  1 | 2\n  """)\n  void t(int a, int b) { }\n}'));
    assert.equal(r.passed, true);
    assert.doesNotMatch(r.evidence, /VACUOUS/);
    assert.match(r.evidence, /1 annotated method/);
  });

  test("still fails a genuine ordering violation", () => {
    const r = checkers["annotation-order"](src('class T {\n  @Description("d")\n  @DisplayName("x")\n  @TableTest("""\n  A | B?\n  1 | 2\n  """)\n  void t(int a, int b) { }\n}'));
    assert.equal(r.passed, false);
  });
});

describe("tables in markdown", () => {
  const markdown = (content) => ({ fileContent: content, allFiles: [] });

  const loanTable = `## Loan approval

| Scenario | Age | Credit score | Approved? |
|---|---|---|---|
| Standard applicant | 40 | 700 | yes |
| Score at threshold | 40 | 650 | no |
`;

  test("reads the header of a markdown table, ignoring the alignment row", () => {
    assert.deepEqual(parseTableHeaders(loanTable), [["Scenario", "Age", "Credit score", "Approved?"]]);
  });

  test("counts markdown data rows without counting the alignment row", () => {
    assert.equal(countDataRows(loanTable), 2);
  });

  test("separates two markdown tables split by prose", () => {
    const two = `${loanTable}
Some prose between the tables.

| Scenario | Tier | Rate? |
| --- | :--: | ---: |
| Senior | senior | 0.8 |
`;
    assert.deepEqual(parseTableHeaders(two).map(h => h[0]), ["Scenario", "Scenario"]);
    assert.equal(countDataRows(two), 3);
  });

  test("does not read a prose line containing a pipe as a table", () => {
    const prose = "Write the value set as `{yes | no}` when the input does not matter.\n";
    assert.deepEqual(parseTableHeaders(prose), []);
    assert.equal(countDataRows(prose), 0);
  });

  test("tolerates a table written without leading and trailing pipes", () => {
    const bare = `Scenario | Approved?
--- | ---
Standard applicant | yes
`;
    assert.deepEqual(parseTableHeaders(bare), [["Scenario", "Approved?"]]);
    assert.equal(countDataRows(bare), 1);
  });

  test("reads a @TableTest source exactly once, not a second time as markdown", () => {
    const java = `class T {
  @TableTest("""
      Scenario | Age | Approved?
      Standard | 40  | yes
      Senior   | 70  | no
      """)
  void approves(String scenario, int age, boolean approved) { }
}`;
    assert.deepEqual(parseTableHeaders(java), [["Scenario", "Age", "Approved?"]]);
    assert.equal(countDataRows(java), 2);
  });

  test("scenario-column-present judges the leftmost markdown column", () => {
    assert.equal(checkers["scenario-column-present"](markdown(loanTable)).passed, true);
    const outputFirst = "| Approved? | Age |\n|---|---|\n| yes | 40 |\n";
    assert.equal(checkers["scenario-column-present"](markdown(outputFirst)).passed, false);
  });

  test("output-column-has-question-mark finds a markdown output column", () => {
    assert.equal(checkers["output-column-has-question-mark"](markdown(loanTable)).passed, true);
    const noQuestion = "| Scenario | Age | Approved |\n|---|---|---|\n| Standard | 40 | yes |\n";
    assert.equal(checkers["output-column-has-question-mark"](markdown(noQuestion)).passed, false);
  });

  test("produces-markdown-table needs a rendered table, not just pipes", () => {
    assert.equal(checkers["produces-markdown-table"](markdown(loanTable)).passed, true);
    assert.equal(checkers["produces-markdown-table"](markdown("No table here, only prose.")).passed, false);
  });

  test("produces-multiple-tables needs two separate tables", () => {
    assert.equal(checkers["produces-multiple-tables"](markdown(loanTable)).passed, false);
    const two = `${loanTable}\n| Scenario | Rate? |\n|---|---|\n| Senior | 0.8 |\n`;
    assert.equal(checkers["produces-multiple-tables"](markdown(two)).passed, true);
  });

  test("output-columns-have-question-marks requires one in every table", () => {
    const both = `${loanTable}\n| Scenario | Rate? |\n|---|---|\n| Senior | 0.8 |\n`;
    assert.equal(checkers["output-columns-have-question-marks"](markdown(both)).passed, true);
    const one = `${loanTable}\n| Scenario | Rate |\n|---|---|\n| Senior | 0.8 |\n`;
    assert.equal(checkers["output-columns-have-question-marks"](markdown(one)).passed, false);
  });
});

describe("Swift @TableTest", () => {
  const swiftFile = (members) => `import TableTest
import Testing
@testable import Lighting

struct DimmerTests {
${members}
}
`;
  const swift = (members) => ({
    fileContent: swiftFile(members),
    allFiles: [
      { path: "Package.swift", content: "// swift-tools-version: 6.2\n" },
      { path: "Tests/LightingTests/DimmerTests.swift", content: swiftFile(members) },
    ],
  });
  const titled = (body, signature, bodyCode = "#expect(true)") => `    @TableTest("Dims the lamp", table: """
${body}
        """)
    func ${signature} {
        ${bodyCode}
    }`;

  test("reads a titled table with the parameters of its function", () => {
    const [t] = tableTestTables(swiftFile(titled(
      "        Scenario | Scene   | Levels   | Brightness?\n        evening  | evening | [10, 20] | 20",
      "dims(scene: String, levels: [Int], brightness: Int?)",
    )));
    assert.equal(t.method, "dims");
    assert.deepEqual(t.params, [
      { name: "scene", type: "String" },
      { name: "levels", type: "[Int]" },
      { name: "brightness", type: "Int?" },
    ]);
    assert.equal(t.hasScenarioColumn, true);
    assert.deepEqual(t.rows, [["evening", "evening", "[10, 20]", "20"]]);
  });

  test("reads a table with no title", () => {
    const [t] = tableTestTables(swiftFile(`    @TableTest("""
        Watts | Lumens?
        60    | 800
        """)
    func converts(watts: Int, lumens: Int) {
        #expect(true)
    }`));
    assert.equal(t.method, "converts");
    assert.deepEqual(t.params.map((p) => p.name), ["watts", "lumens"]);
    assert.equal(t.hasScenarioColumn, false);
  });

  test("reads a table after traits, above a function with attributes and modifiers", () => {
    const [t] = tableTestTables(swiftFile(`    @TableTest(.tags(.fast), table: """
        Value | Others
        1     | [a: 2]
        """)
    @MainActor
    private func reads(_ value: Int, labelled others: [String: Int]) {
        #expect(true)
    }`));
    assert.equal(t.method, "reads");
    assert.deepEqual(t.params, [
      { name: "value", type: "Int" },
      { name: "others", type: "[String: Int]" },
    ]);
  });

  test("splits parameters on commas outside brackets and parentheses", () => {
    assert.deepEqual(parseParameterList("pair: (Int, Int), counts: [String: Int], scale: Int = 3"), [
      { name: "pair", type: "(Int, Int)" },
      { name: "counts", type: "[String: Int]" },
      { name: "scale", type: "Int" },
    ]);
  });

  test("reads a title that holds a parenthesis", () => {
    const [t] = tableTestTables(swiftFile(`    @TableTest("Dims (or not) the lamp", table: """
        Watts | Lumens?
        60    | 800
        """)
    func converts(watts: Int, lumens: Int) {
        #expect(true)
    }`));
    assert.equal(t.method, "converts");
    assert.deepEqual(t.headers, ["Watts", "Lumens?"]);
  });

  test("reads the headers and counts the rows of every table", () => {
    const content = swiftFile([
      titled("        Watts | Lumens?\n        60    | 800\n        100   | 1600", "a(watts: Int, lumens: Int)"),
      titled("        Scene | Level?\n        night | 5", "b(scene: String, level: Int)"),
    ].join("\n\n"));
    assert.deepEqual(parseTableHeaders(content), [["Watts", "Lumens?"], ["Scene", "Level?"]]);
    assert.equal(countDataRows(content), 3);
  });

  test("list-syntax-correct judges a column of a Swift array type", () => {
    const bad = swift(titled("        Scenario | Tags\n        bare     | warm, dim", "t(tags: [String])"));
    assert.equal(checkers["list-syntax-correct"](bad).passed, false);
    const good = swift(titled("        Scenario | Tags\n        listed   | [warm, dim]", "t(tags: [String]?)"));
    assert.equal(checkers["list-syntax-correct"](good).passed, true);
  });

  test("list-syntax-correct leaves a Swift dictionary column alone", () => {
    const map = swift(titled("        Scenario | Levels\n        map      | [warm: 2]", "t(levels: [String: Int])"));
    assert.equal(checkers["list-syntax-correct"](map).evidence, "No List-typed column in any table — nothing to check.");
  });

  test("set-syntax-correct judges a Set column but not a value set", () => {
    const bad = swift(titled("        Scenario | Scenes\n        listed   | [warm, dim]", "t(scenes: Set<String>)"));
    assert.equal(checkers["set-syntax-correct"](bad).passed, false);
    const valueSet = swift(titled("        Scenario | Scene\n        either   | {warm, dim}", "t(scene: String)"));
    assert.equal(checkers["set-syntax-correct"](valueSet).passed, true);
  });

  describe("no-if-switch-in-method", () => {
    const body = (code) =>
      checkers["no-if-switch-in-method"](swift(titled("        Watts | Lumens?\n        60    | 800", "t(watts: Int, lumens: Int)", code)));

    test("catches if, guard, switch and a ternary", () => {
      for (const code of [
        "if watts > 50 { #expect(true) }",
        "guard watts > 0 else { return }",
        "switch watts { default: break }",
        "#expect(lumens == (watts > 50 ? 800 : 400))",
      ]) {
        assert.equal(body(code).passed, false, code);
      }
    });

    test("does not read optional chaining, try?, as?, an optional type or ?? as a ternary", () => {
      const code = [
        "let lamp: Lamp? = try? Lamp(watts: watts)",
        "let bright = lamp?.lumens ?? 0",
        "let named = (lamp as? Named)?.name",
        "#expect(bright == lumens)",
      ].join("\n        ");
      assert.equal(body(code).passed, true, body(code).evidence);
      assert.match(body(code).evidence, /^1 method\(s\) checked/);
    });
  });

  describe("no-decoder-in-app-code", () => {
    const files = (...pairs) => ({ fileContent: "", allFiles: pairs.map(([path, content]) => ({ path, content })) });
    const decoder = "extension Scene {\n    init(from decoder: any Decoder) throws {\n        self.init()\n    }\n}\n";

    test("fails an init(from:) written into app code", () => {
      const result = checkers["no-decoder-in-app-code"](files(["Sources/Lighting/Scene.swift", decoder]));
      assert.equal(result.passed, false);
      assert.match(result.evidence, /Sources\/Lighting\/Scene\.swift/);
    });

    test("passes a conversion in the test target and a one-word conformance in the app", () => {
      const result = checkers["no-decoder-in-app-code"](files(
        ["Sources/Lighting/Scene.swift", "enum Scene: String, Codable, CaseIterable { case warm, dim }\n"],
        ["Tests/LightingTests/TableCells.swift", "extension Scene: @retroactive TableValueDecodable {\n    public init(tableValue: TableValue) throws { self = .warm }\n}\n" + decoder],
      ));
      assert.equal(result.passed, true, result.evidence);
    });
  });

  describe("tabletest-function-in-a-type", () => {
    const inType = checkers["tabletest-function-in-a-type"];
    const testFile = (content) => ({ fileContent: content, allFiles: [
      { path: "Package.swift", content: "// swift-tools-version: 6.2\n" },
      { path: "Tests/LightingTests/DimmerTests.swift", content },
    ] });
    const table = (name) => `@TableTest("Dims the lamp", table: """
    Scene        | Levels      | Brightness?
    '{evening}'  | {10, 20}    | 20
    """)
func ${name}(scene: String, levels: Int, brightness: Int) {
    #expect(true)
}`;

    test("passes a table inside a struct and one inside an extension of it", () => {
      const result = inType(testFile(`import TableTest\n\nstruct DimmerTests {\n${table("dims")}\n}\n\nextension DimmerTests {\n${table("brightens")}\n}\n`));
      assert.equal(result.passed, true, result.evidence);
    });

    test("fails a table at file scope and names its function", () => {
      const result = inType(testFile(`import TableTest\n\nstruct DimmerTests {\n${table("dims")}\n}\n\n${table("brightens")}\n`));
      assert.equal(result.passed, false);
      assert.match(result.evidence, /brightens/);
      assert.doesNotMatch(result.evidence, /\bdims\b/);
    });
  });
});

describe("no-branch-on-table-enum-outside-it", () => {
  const check = checkers["no-branch-on-table-enum-outside-it"];
  const testFile = (members) => ({
    fileContent: "",
    allFiles: [
      { path: "Package.swift", content: "// swift-tools-version: 6.2\n" },
      { path: "Sources/Archive/Record.swift", content: "public enum Kind: String { case a, b }\nfunc pick(_ kind: Kind) -> Int { switch kind { case .a: 1; case .b: 2 } }\n" },
      {
        path: "Tests/ArchiveTests/ArchiveTests.swift",
        content: `import TableTest
import Testing
@testable import Archive

struct ArchiveTests {
    enum Insertion: String {
        case inOrder = "in order"
        case reversed

        var records: [Int] {
            switch self {
            case .inOrder: [1, 2]
            case .reversed: [2, 1]
            }
        }
    }

${members}
}
`,
      },
    ],
  });
  const tableTest = (body) => `    @TableTest("""
        Insertion | Newest?
        in order  | 2
        """)
    func findsTheNewest(insertion: Insertion, newest: Int) {
        ${body}
    }`;

  test("passes a branch in a member of the test-local enum", () => {
    const result = check(testFile(tableTest("#expect(Archive(insertion.records).newest == newest)")));
    assert.equal(result.passed, true, result.evidence);
  });

  test("fails a helper of the test type that switches on the enum, and names it", () => {
    const result = check(testFile(`${tableTest("#expect(Archive(Self.records(insertion)).newest == newest)")}

    static func records(_ insertion: Insertion) -> [Int] {
        switch insertion {
        case .inOrder: [1, 2]
        case .reversed: [2, 1]
        }
    }`));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /records/);
    assert.match(result.evidence, /Insertion/);
  });

  test("fails a fixture method that picks with a ternary on a case, the shape found in the field", () => {
    const result = check(testFile(`${tableTest("#expect(fixture(insertion).newest == newest)")}

    func fixture(_ inserted: Insertion) -> Archive {
        Archive(inserted == .inOrder ? [1, 2] : [2, 1])
    }`));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /fixture/);
  });

  test("fails an if or a guard that compares the value with a case", () => {
    for (const body of [
      "if insertion == .reversed { return Archive([2, 1]) }\n        return Archive([1, 2])",
      "guard insertion != .reversed else { return Archive([2, 1]) }\n        return Archive([1, 2])",
      "if case .reversed = insertion { return Archive([2, 1]) }\n        return Archive([1, 2])",
    ]) {
      const result = check(testFile(`${tableTest("#expect(true)")}

    func archive(for insertion: Insertion) -> Archive {
        ${body}
    }`));
      assert.equal(result.passed, false, body);
    }
  });

  test("fails a helper that branches on a stored property of the enum's type", () => {
    const result = check(testFile(`${tableTest("#expect(true)")}

    let order: Insertion = .inOrder

    func archive() -> Archive {
        switch order {
        case .inOrder: Archive([1, 2])
        case .reversed: Archive([2, 1])
        }
    }`));
    assert.equal(result.passed, false, result.evidence);
  });

  test("passes a comparison with a case that is passed on as a flag, not branched on", () => {
    const result = check(testFile(tableTest("#expect(Archive(insertion.records, sorted: insertion == .inOrder).newest == newest)")));
    assert.equal(result.passed, true, result.evidence);
  });

  test("passes an extension of the enum, which is a member", () => {
    const result = check(testFile(`${tableTest("#expect(true)")}
}

extension ArchiveTests.Insertion {
    func label() -> String {
        switch self {
        case .inOrder: "in order"
        case .reversed: "reversed"
        }
    }`));
    assert.equal(result.passed, true, result.evidence);
  });

  test("ignores an enum in the app code, and an enum without String raw values", () => {
    const result = check(testFile(`${tableTest("#expect(true)")}

    enum Step { case one, two }

    func size(_ step: Step) -> Int {
        switch step {
        case .one: 1
        case .two: 2
        }
    }`));
    assert.equal(result.passed, true, result.evidence);
  });

  test("passes vacuously, and says so, when the tests declare no test-local enum", () => {
    const result = check({ fileContent: "", allFiles: [{ path: "Tests/ATests/ATests.swift", content: "struct ATests {}\n" }] });
    assert.equal(result.passed, true);
    assert.match(result.evidence, /VACUOUS/);
  });
});

describe("no-default-in-test-function", () => {
  const check = checkers["no-default-in-test-function"];
  const swiftTable = (signature, body) => {
    const content = `import TableTest
import Testing
@testable import Signs

struct SignTests {
    @TableTest("""
        Text  | Style | Colour?
        Stop  | bold  | red
        """)
    func ${signature} {
        ${body}
    }
}
`;
    return { fileContent: content, allFiles: [{ path: "Tests/SignsTests/SignTests.swift", content }] };
  };

  test("fails a default with ?? in the body, the shape found in the field", () => {
    const result = check(swiftTable("paints(text: String, style: Style?, colour: Colour)",
      "#expect(Sign(text: text, style: style ?? .red).colour == colour)"));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /\?\?/);
    assert.match(result.evidence, /paints/);
  });

  test("fails .map or .flatMap on a parameter", () => {
    for (const body of [
      "#expect(Sign(text: text, style: style.map { Style(name: $0) }).colour == colour)",
      "#expect(Sign(text: text, style: style?.flatMap(Style.init)).colour == colour)",
    ]) {
      const result = check(swiftTable("paints(text: String, style: String?, colour: Colour)", body));
      assert.equal(result.passed, false, body);
      assert.match(result.evidence, /style/);
    }
  });

  test("passes .map on the result, which is a projection, not a default", () => {
    const result = check(swiftTable("paints(text: String, style: Style?, colour: Colour)",
      "#expect(Sign(text: text, style: style).layers.map(\\.colour) == [colour])"));
    assert.equal(result.passed, true, result.evidence);
  });

  test("passes optional chaining, try? and as?", () => {
    const result = check(swiftTable("paints(text: String, style: Style?, colour: Colour)",
      "let sign = try? Sign(text: text, style: style)\n        #expect((sign as? Painted)?.colour == colour)"));
    assert.equal(result.passed, true, result.evidence);
  });
});

describe("untouched-tables-unchanged", () => {
  const check = checkers["untouched-tables-unchanged"];
  const file = (tables) => `import TableTest
import Testing

struct KennelTests {
${tables.join("\n\n")}
}
`;
  const table = (name, rows) => `    @TableTest("""
        Size  | Pen?
${rows.map((r) => `        ${r}`).join("\n")}
        """)
    func ${name}(size: String, pen: Int) {
        #expect(true)
    }`;
  const scaffold = [{ path: "Tests/KennelTests/KennelTests.swift", content: file([
    table("assignsAPen", ["small | 1", "large | 2"]),
    table("chargesTheFee", ["small | 30"]),
  ]) }];
  const output = (tables) => ({ fileContent: "", allFiles: [{ path: "Tests/KennelTests/KennelTests.swift", content: file(tables) }] });
  const assertion = { id: "untouched-tables-unchanged", functions: ["chargesTheFee"] };

  test("passes when the named table is byte-equal to the scaffold's, whatever else changed", () => {
    const result = check({ ...output([
      table("assignsAPen", ["small | 1", "medium | 2", "large | 2"]),
      table("chargesTheFee", ["small | 30"]),
    ]), assertion, scaffoldFiles: scaffold });
    assert.equal(result.passed, true, result.evidence);
  });

  test("fails when a named table gained a row, and names it", () => {
    const result = check({ ...output([
      table("assignsAPen", ["small | 1", "large | 2"]),
      table("chargesTheFee", ["small | 30", "large | 40"]),
    ]), assertion, scaffoldFiles: scaffold });
    assert.equal(result.passed, false);
    assert.match(result.evidence, /chargesTheFee/);
  });

  test("fails when a named table is gone from the answer", () => {
    const result = check({ ...output([table("assignsAPen", ["small | 1"])]), assertion, scaffoldFiles: scaffold });
    assert.equal(result.passed, false);
    assert.match(result.evidence, /chargesTheFee/);
  });

  test("fails loudly when the assertion names no function or the scaffold lacks one", () => {
    assert.equal(check({ ...output([]), assertion: { id: "untouched-tables-unchanged" }, scaffoldFiles: scaffold }).passed, false);
    const missing = check({ ...output([]), assertion: { ...assertion, functions: ["nope"] }, scaffoldFiles: scaffold });
    assert.equal(missing.passed, false);
    assert.match(missing.evidence, /scaffold/);
  });
});

const meterTests = (tables) => {
  const content = `import TableTest
import Testing
@testable import Parking

struct MeterTests {
${tables}
}
`;
  return { fileContent: content, allFiles: [{ path: "Tests/ParkingTests/MeterTests.swift", content }] };
};

const LONG_SENTENCE = "You have parked for 19 minutes. Parking is free for the first 20 minutes, after which the meter charges by the hour.";

describe("no-long-text-cell", () => {
  const check = checkers["no-long-text-cell"];

  test("fails a table whose expectation cell is a long sentence", () => {
    const result = check(meterTests(`    @TableTest("""
        Scenario       | Minutes | Message?
        Inside the gap | 19      | ${LONG_SENTENCE}
        """)
    func explainsTheCharge(minutes: Int, message: String) {
        #expect(Meter.message(minutes: minutes) == message)
    }`));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /explainsTheCharge/);
    assert.match(result.evidence, /\d{3} characters/);
  });

  test("fails one long row even when every other row is short", () => {
    const result = check(meterTests(`    @TableTest("""
        Scenario       | Minutes | Message?
        Before parking | 0       | Welcome
        Inside the gap | 19      | ${LONG_SENTENCE}
        """)
    func explainsTheCharge(minutes: Int, message: String) {
        #expect(Meter.message(minutes: minutes) == message)
    }`));
    assert.equal(result.passed, false);
  });

  test("passes a table of decisions, with the sentence in a plain test", () => {
    const result = check(meterTests(`    @TableTest("""
        Scenario          | Minutes | Charge?
        Last free minute  | 19      | free
        First paid minute | 20      | paid(hours: 1)
        """)
    func decidesTheCharge(minutes: Int, charge: Charge) {
        #expect(Meter.charge(minutes: minutes) == charge)
    }

    @Test func wordsAFreeStay() {
        #expect(Meter.message(minutes: 19) == "${LONG_SENTENCE}")
    }`));
    assert.equal(result.passed, true, result.evidence);
  });

  test("takes its width from the assertion", () => {
    const table = meterTests(`    @TableTest("""
        Scenario          | Minutes | Charge?
        First paid minute | 20      | paid(hours: 1)
        """)
    func decidesTheCharge(minutes: Int, charge: Charge) {
        #expect(Meter.charge(minutes: minutes) == charge)
    }`);
    const result = check({ ...table, assertion: { id: "no-long-text-cell", max_cell_chars: 10 } });
    assert.equal(result.passed, false);
  });

  test("fails an answer with no table", () => {
    const result = check(meterTests(`    @Test func wordsAFreeStay() {
        #expect(Meter.message(minutes: 19) == "${LONG_SENTENCE}")
    }`));
    assert.equal(result.passed, false);
  });
});

describe("rules-tested-on-the-decision", () => {
  const check = checkers["rules-tested-on-the-decision"];
  const assertion = { id: "rules-tested-on-the-decision", decides: "charge(minutes:", boundaries: [[19, 20], [139, 140]] };
  const withAssertion = (tables) => ({ ...meterTests(tables), assertion });

  test("passes a table that calls the deciding function on both sides of each line", () => {
    const result = check(withAssertion(`    @TableTest("""
        Scenario          | Minutes   | Charge?
        Free stay         | {0, 19}   | free
        First paid minute | 20        | paid(hours: 1)
        Last short stay   | 139       | paid(hours: 2)
        First long stay   | 140       | dailyRate
        """)
    func decidesTheCharge(minutes: Int, charge: Charge) {
        #expect(Meter.charge(minutes: minutes) == charge)
    }`));
    assert.equal(result.passed, true, result.evidence);
  });

  test("fails boundaries tested only through the text function", () => {
    const result = check(withAssertion(`    @TableTest("""
        Scenario          | Minutes | Message?
        Last free minute  | 19      | free
        First paid minute | 20      | one hour
        Last short stay   | 139     | two hours
        First long stay   | 140     | daily rate
        """)
    func wordsTheCharge(minutes: Int, message: String) {
        #expect(Meter.message(minutes: minutes) == message)
    }`));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /charge\(minutes:/);
  });

  test("fails a decision table that misses one side of a line", () => {
    const result = check(withAssertion(`    @TableTest("""
        Scenario          | Minutes | Charge?
        Last free minute  | 19      | free
        First paid minute | 20      | paid(hours: 1)
        Long stay         | 200     | dailyRate
        """)
    func decidesTheCharge(minutes: Int, charge: Charge) {
        #expect(Meter.charge(minutes: minutes) == charge)
    }`));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /139/);
  });

  test("does not count a call inside a comment", () => {
    const result = check(withAssertion(`    @TableTest("""
        Scenario          | Minutes | Message?
        Last free minute  | 19      | free
        First paid minute | 20      | one hour
        Last short stay   | 139     | two hours
        First long stay   | 140     | daily rate
        """)
    func wordsTheCharge(minutes: Int, message: String) {
        // Meter.charge(minutes:) decides; this tests the words.
        #expect(Meter.message(minutes: minutes) == message)
    }`));
    assert.equal(result.passed, false);
  });

  test("names the missing parameters when the assertion has none", () => {
    const result = check({ ...meterTests(""), assertion: { id: "rules-tested-on-the-decision" } });
    assert.equal(result.passed, false);
    assert.match(result.evidence, /decides/);
  });
});

describe("constant-text-not-restated", () => {
  const check = checkers["constant-text-not-restated"];
  const SECOND_SENTENCE = "Parking is charged by the hour from the twentieth minute. Please keep your ticket until you leave the car park.";
  const wordingTest = (name, sentence) => `    @Test func ${name}() {
        #expect(Meter.message(minutes: 19) == "${sentence}")
    }`;

  test("fails a test for each constant sentence", () => {
    const result = check(meterTests([wordingTest("wordsAFreeStay", LONG_SENTENCE), wordingTest("wordsAPaidStay", SECOND_SENTENCE)].join("\n\n")));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /2 string literals/);
  });

  test("passes one sentence asserted as the wiring check", () => {
    const result = check(meterTests(wordingTest("wordsAFreeStay", LONG_SENTENCE)));
    assert.equal(result.passed, true, result.evidence);
  });

  test("passes a wiring check that compares with the rendering of the decided key", () => {
    const result = check(meterTests(`    @Test func rendersTheDecidedCharge() {
        #expect(Meter.message(minutes: 19) == Meter.text(of: .free))
    }`));
    assert.equal(result.passed, true, result.evidence);
  });

  test("measures a multi-line literal by its joined text", () => {
    const split = (sentence) => `"""
            ${sentence.slice(0, 60)}\\
            ${sentence.slice(60)}
            """`;
    const result = check(meterTests(`    @Test func wordsAFreeStay() {
        #expect(Meter.message(minutes: 19) == ${split(LONG_SENTENCE)})
    }

    @Test func wordsAPaidStay() {
        #expect(Meter.message(minutes: 20) == ${split(SECOND_SENTENCE)})
    }`));
    assert.equal(result.passed, false);
  });

  test("leaves the cells of a table to the width check", () => {
    const result = check(meterTests(`    @TableTest("""
        Scenario       | Minutes | Message?
        Inside the gap | 19      | ${LONG_SENTENCE}
        Paid           | 20      | ${SECOND_SENTENCE}
        """)
    func explainsTheCharge(minutes: Int, message: String) {
        #expect(Meter.message(minutes: minutes) == message)
    }`));
    assert.equal(result.passed, true, result.evidence);
  });

  test("ignores a sentence in a comment", () => {
    const result = check(meterTests(`    // "${LONG_SENTENCE}"
    // "${SECOND_SENTENCE}"
    @Test func rendersTheDecidedCharge() {
        #expect(Meter.message(minutes: 19) == Meter.text(of: .free))
    }`));
    assert.equal(result.passed, true, result.evidence);
  });

  test("takes its limits from the assertion", () => {
    const result = check({
      ...meterTests(wordingTest("wordsAFreeStay", LONG_SENTENCE)),
      assertion: { id: "constant-text-not-restated", max_literal_chars: 100, max_long_literals: 0 },
    });
    assert.equal(result.passed, false);
  });
});

describe("rules-tested-on-the-decision, with the deciding function named by the agent", () => {
  const check = checkers["rules-tested-on-the-decision"];
  const assertion = { id: "rules-tested-on-the-decision", decided_inside: "message(for:", boundaries: [[19, 20]] };
  const meterApp = (body, extra = "") => `public enum Charge: Equatable { case free, paid }
public enum Meter {
${extra}
    public static func message(for minutes: Int) -> String {
${body}
    }
}
`;
  const answer = (app, table) => ({
    fileContent: "",
    allFiles: [
      { path: "Sources/Parking/Meter.swift", content: app },
      ...meterTests(table).allFiles,
    ],
    assertion,
  });
  const decisionTable = (call) => `    @TableTest("""
        Scenario          | Minutes | Charge?
        Last free minute  | 19      | free
        First paid minute | 20      | paid
        """)
    func decidesTheCharge(minutes: Int, charge: Charge) {
        #expect(${call} == charge)
    }`;

  test("passes a table of a function that the text calls, with both sides of the line", () => {
    const app = meterApp(`        charge(for: minutes) == .free ? "Free" : "Paid"`,
      `    public static func charge(for minutes: Int) -> Charge { minutes < 20 ? .free : .paid }`);
    const result = check(answer(app, decisionTable("Meter.charge(for: minutes)")));
    assert.equal(result.passed, true, result.evidence);
  });

  test("passes a table of a computed property that the text reads", () => {
    const app = `public enum Charge: Equatable { case free, paid }
extension Int {
    var charge: Charge { self < 20 ? .free : .paid }
}
public enum Meter {
    public static func message(for minutes: Int) -> String {
        minutes.charge == .free ? "Free" : "Paid"
    }
}
`;
    const result = check(answer(app, decisionTable("minutes.charge")));
    assert.equal(result.passed, true, result.evidence);
  });

  test("fails when the text holds the decision itself, as the scaffold does", () => {
    const app = meterApp(`        minutes < 20 ? "Free" : "Paid"`);
    const result = check(answer(app, decisionTable(`Meter.message(for: minutes) == "Free" ? Charge.free : Charge.paid`)));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /calls no function/);
  });

  test("fails a new function that the text does not call, since the decision is then in two places", () => {
    const app = meterApp(`        minutes < 20 ? "Free" : "Paid"`,
      `    public static func charge(for minutes: Int) -> Charge { minutes < 20 ? .free : .paid }`);
    const result = check(answer(app, decisionTable("Meter.charge(for: minutes)")));
    assert.equal(result.passed, false);
  });

  test("fails a table of the new function that misses a side of the line", () => {
    const app = meterApp(`        charge(for: minutes) == .free ? "Free" : "Paid"`,
      `    public static func charge(for minutes: Int) -> Charge { minutes < 20 ? .free : .paid }`);
    const result = check(answer(app, `    @TableTest("""
        Scenario   | Minutes | Charge?
        Paid       | 20      | paid
        """)
    func decidesTheCharge(minutes: Int, charge: Charge) {
        #expect(Meter.charge(for: minutes) == charge)
    }`));
    assert.equal(result.passed, false);
    assert.match(result.evidence, /19/);
  });
});

describe("rules-tested-on-the-decision, on an app the answer left unchanged", () => {
  const check = checkers["rules-tested-on-the-decision"];
  const assertion = { id: "rules-tested-on-the-decision", decided_inside: "message(for:", boundaries: [[19, 20]] };
  const scaffold = (body, extra = "") => [{ path: "Sources/Parking/Meter.swift", content: `public enum Charge: Equatable { case free, paid }
public enum Meter {
${extra}
    public static func message(for minutes: Int) -> String {
${body}
    }
}
` }];
  const table = `    @TableTest("""
        Scenario          | Minutes | Charge?
        Last free minute  | 19      | free
        First paid minute | 20      | paid
        """)
    func decidesTheCharge(minutes: Int, charge: Charge) {
        #expect(Meter.charge(for: minutes) == charge)
    }`;

  test("reads the renderer from the scaffold, and finds no seam where the scaffold has none", () => {
    const result = check({ ...meterTests(table), assertion, scaffoldAppFiles: scaffold(`        minutes < 20 ? "Free" : "Paid"`) });
    assert.equal(result.passed, false);
    assert.match(result.evidence, /no seam/);
  });

  test("passes where the scaffold's renderer already calls the deciding function", () => {
    const app = scaffold(`        charge(for: minutes) == .free ? "Free" : "Paid"`,
      `    public static func charge(for minutes: Int) -> Charge { minutes < 20 ? .free : .paid }`);
    const result = check({ ...meterTests(table), assertion, scaffoldAppFiles: app });
    assert.equal(result.passed, true, result.evidence);
  });

  test("reads the answer's copy of an app file in place of the scaffold's", () => {
    const changed = scaffold(`        charge(for: minutes) == .free ? "Free" : "Paid"`,
      `    public static func charge(for minutes: Int) -> Charge { minutes < 20 ? .free : .paid }`);
    const answer = meterTests(table);
    const result = check({ ...answer, allFiles: [...changed, ...answer.allFiles], assertion,
      scaffoldAppFiles: scaffold(`        minutes < 20 ? "Free" : "Paid"`) });
    assert.equal(result.passed, true, result.evidence);
  });
});
