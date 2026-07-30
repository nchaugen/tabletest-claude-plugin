const { test, describe } = require("node:test");
const assert = require("node:assert");

const { checkers } = require("./assertions.js");

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
