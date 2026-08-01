const { test, describe } = require("node:test");
const assert = require("node:assert");

const {
  checkers,
  splitRowCells,
  parseCollectionElements,
  parseParameterList,
  tableTestTables,
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
