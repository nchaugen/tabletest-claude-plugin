const { test, describe } = require("node:test");
const assert = require("node:assert");

const path = require("path");

const {
  extractTableTestExamples,
  findViolations,
  lintMarkdown,
  lintSkill,
  compareToBaseline,
  readBaseline,
  distinctiveIdentifiers,
  evalIdentifierTerms,
} = require("./lint-skill-examples.js");

const fence = (body) => "```java\n" + body + "\n```\n";

describe("extractTableTestExamples", () => {
  test("reports the line the example starts on, so a violation is clickable", () => {
    const markdown = "# Title\n\nSome prose.\n\n" + fence('@TableTest("""\n    A | B?\n    1 | 2\n    """)\nvoid addsUp(int a, int b) { }');

    const examples = extractTableTestExamples(markdown);

    assert.equal(examples.length, 1);
    assert.equal(examples[0].startLine, 5);
  });

  test("ignores fenced blocks that carry no table", () => {
    const markdown = fence("@TypeConverter\npublic static Money parse(String value) { return null; }");

    assert.deepEqual(extractTableTestExamples(markdown), []);
  });

  test("keeps each example separate so one bad block does not shadow the next", () => {
    const markdown = fence('@TableTest("""\n    A?\n    1\n    """)\nvoid first(int a) { }')
      + "\nprose\n\n"
      + fence('@TableTest("""\n    B?\n    2\n    """)\nvoid second(int b) { }');

    assert.equal(extractTableTestExamples(markdown).length, 2);
  });
});

describe("findViolations", () => {
  const violationsIn = (body) => findViolations({ startLine: 1, code: body }).map((v) => v.check);

  test("flags a ternary in a @TableTest method body", () => {
    const checks = violationsIn(`@TableTest("""
    Kept? | Tag
    true  | a
    false | b
    """)
void keepsMatchingTags(boolean kept, String tag) {
    List<String> expected = kept ? List.of(tag) : List.of();
    assertThat(filter(tag)).isEqualTo(expected);
}`);

    assert.ok(checks.includes("no-if-switch-in-method"), checks.join(","));
  });

  test("leaves a ternary inside a @TypeConverter alone — converters may branch", () => {
    const checks = violationsIn(`@TableTest("""
    Fare?  | Surcharge
    12.00  | 5.00
    17.00  | 8.50
    """)
void addsSurcharge(BigDecimal fare, BigDecimal surcharge) {
    assertThat(total(surcharge)).isEqualTo(fare);
}

@TypeConverter
public static BigDecimal toAmount(String value) {
    return value.isBlank() ? BigDecimal.ZERO : new BigDecimal(value);
}`);

    assert.equal(checks.includes("no-if-switch-in-method"), false, checks.join(","));
  });

  test("flags @Description written above @DisplayName", () => {
    const checks = violationsIn(`@Description("""
    Context.
    """)
@DisplayName("Charges by duration band")
@TableTest("""
    Hours | Fee?
    1     | 0.00
    4     | 6.00
    """)
void chargesByDurationBand(int hours, BigDecimal fee) { }`);

    assert.ok(checks.includes("annotation-order"), checks.join(","));
  });

  test("flags a method name whose opener carries no information", () => {
    const checks = violationsIn(`@TableTest("""
    Input | Squared?
    2     | 4
    3     | 9
    """)
void testSquares(int input, int squared) { }`);

    assert.ok(checks.includes("uninformative-method-name"), checks.join(","));
  });

  test("accepts a method name that opens with the action the code performs", () => {
    const checks = violationsIn(`@TableTest("""
    Input | Squared?
    2     | 4
    3     | 9
    """)
void squaresItsInput(int input, int squared) { }`);

    assert.deepEqual(checks, []);
  });

  test("flags an expectation column holding one value in every row", () => {
    const checks = violationsIn(`@TableTest("""
    Scenario     | Config          | Expected?
    All defaults | [:]             | OK
    With timeout | [timeout: 5000] | OK
    Full config  | [retry: 3]      | OK
    """)
void appliesConfig(String scenario, RequestConfig config, String expected) { }`);

    assert.ok(checks.includes("constant-expectation-column"), checks.join(","));
  });

  test("accepts an expectation column that moves with the rows", () => {
    const checks = violationsIn(`@TableTest("""
    Credit Hours | Standing?
    {0, 29}      | Freshman
    {30, 59}     | Sophomore
    """)
void setsStandingFromCreditHours(int hours, Standing standing) { }`);

    assert.deepEqual(checks, []);
  });

  test("says nothing about a single-row table — one row cannot show variation", () => {
    const checks = violationsIn(`@TableTest("""
    Priority | Fallback               | Resolved?
    main     | {yaml, empty, missing} | main
    """)
void resolvesFromPriority(String priority, String fallback, String resolved) { }`);

    assert.deepEqual(checks, []);
  });

  test("tolerates a fragment with an elided body rather than inventing a violation", () => {
    const checks = violationsIn(`@TableTest("""
    Scenario | Input | Resolved?
    Plain    | a     | A
    Nested   | b     | B
    """)
void resolvesInput(String scenario, String input, String resolved) { ... }`);

    assert.deepEqual(checks, []);
  });
});

describe("exception-column-fully-qualified", () => {
  const violationsIn = (body) => findViolations({ startLine: 1, code: body }).map((v) => v.check);

  test("flags a bare class name, which fails at run time with ClassNotFoundException", () => {
    const checks = violationsIn(`@TableTest("""
    Scenario   | Dose | Throws?
    At minimum | 0    |
    Below      | -1   | IllegalArgumentException
    """)
void rejectsDoseBelowMinimum(int dose, Class<? extends Throwable> thrown) {
    assertEquals(thrown, thrownBy(() -> validate(dose)));
}`);
    assert.ok(checks.includes("exception-column-fully-qualified"), checks.join(","));
  });

  test("accepts the fully-qualified name JUnit can actually convert", () => {
    const checks = violationsIn(`@TableTest("""
    Scenario   | Dose | Throws?
    At minimum | 0    |
    Below      | -1   | java.lang.IllegalArgumentException
    """)
void rejectsDoseBelowMinimum(int dose, Class<? extends Throwable> thrown) {
    assertEquals(thrown, thrownBy(() -> validate(dose)));
}`);
    assert.ok(!checks.includes("exception-column-fully-qualified"), checks.join(","));
  });

  test("leaves an ordinary expectation column alone, however capitalised its values", () => {
    const checks = violationsIn(`@TableTest("""
    Scenario | Item      | Bin?
    Dry      | newspaper | PAPER
    Soiled   | pizza box | RESIDUAL
    """)
void sortsItemIntoBin(String item, Bin bin) { assertEquals(bin, sorter.sort(item)); }`);
    assert.ok(!checks.includes("exception-column-fully-qualified"), checks.join(","));
  });
});

describe("eval-domain-in-skill", () => {
  test("flags a domain the eval suite owns, wherever in the file it appears", () => {
    const hits = lintMarkdown("SKILL.md", "Include rows at the boundary (40 hours for an overtime threshold).");
    assert.deepEqual(hits.map((h) => h.check), ["eval-domain-in-skill"]);
  });

  test("passes a reserved domain, which is what illustrations are supposed to use", () => {
    const hits = lintMarkdown("SKILL.md", "Include rows at the boundary (13 hours for a duty-time limit).");
    assert.deepEqual(hits, []);
  });

  test("catches the term in prose, not only inside a fenced example", () => {
    const hits = lintMarkdown("SKILL.md", "A shopping cart is a familiar case.");
    assert.equal(hits.length, 1);
  });
});

describe("lintMarkdown", () => {
  test("anchors each violation to the line the example starts on", () => {
    const markdown = "# Heading\n\n" + fence('@TableTest("""\n    Input | Squared?\n    2     | 4\n    3     | 9\n    """)\nvoid testSquares(int input, int squared) { }');

    const violations = lintMarkdown("references/large-tables.md", markdown);

    assert.equal(violations.length, 1);
    assert.equal(violations[0].file, "references/large-tables.md");
    assert.equal(violations[0].line, 3);
    assert.equal(violations[0].check, "uninformative-method-name");
  });
});

// --- the shipped skill ------------------------------------------------------
//
// The baseline is the 2026-07-30 sweep, machine-checked. It exists so the linter can land
// green against a corpus that already has 40 violations, and so the count can only fall on
// purpose. A fix that forgets the baseline fails here, which is the point: the entry is the
// record that the defect was real.

describe("the tabletest skill's own examples", () => {
  const repoRoot = path.resolve(__dirname, "..");
  const violations = lintSkill(path.join(repoRoot, "skills", "tabletest"), repoRoot);

  test("introduces no violation the sweep did not already record", () => {
    const { introduced } = compareToBaseline(violations, readBaseline());

    assert.deepEqual(introduced, [], `New violations:\n  ${introduced.join("\n  ")}`);
  });

  test("has a baseline no larger than the violations still present", () => {
    const { fixed } = compareToBaseline(violations, readBaseline());

    assert.deepEqual(fixed, [], `Fixed — remove from scripts/skill-example-baseline.json:\n  ${fixed.join("\n  ")}`);
  });

  test("keeps SKILL.md itself clean, since that is the file every run loads", () => {
    const inSkillMd = violations.filter((v) => v.file.endsWith("skills/tabletest/SKILL.md"));

    assert.deepEqual(inSkillMd, []);
  });
});


describe("eval-identifier-in-skill", () => {
  const suite = new Set(["widget", "delivery"]);

  test("flags an eval's data value used as data in the skill", () => {
    const hits = lintMarkdown("SKILL.md", "Write it as `2 x Widget @ £5.00` in the cell.", suite);
    assert.deepEqual(hits.map((h) => h.check), ["eval-identifier-in-skill"]);
  });

  test("flags a token buried in an opaque composite code", () => {
    const hits = lintMarkdown("SKILL.md", "An opaque `W12/DELIVERY/addr-1` needs a legend.", suite);
    assert.deepEqual(hits.map((h) => h.check), ["eval-identifier-in-skill"]);
  });

  test("ignores prose, where every Title-Case sentence opener would match", () => {
    assert.deepEqual(lintMarkdown("SKILL.md", "Delivery of the value happens later.", suite), []);
  });

  test("passes a reserved-domain replacement", () => {
    assert.deepEqual(lintMarkdown("SKILL.md", "Write it as `2 x 5 mg tablet` in the cell.", suite), []);
  });

  test("does not flag generic vocabulary a Title-Case scan picks up", () => {
    const generic = new Set(["scenario", "expected", "senior"]);
    assert.deepEqual(evalIdentifierTerms("| Senior donor | Expected? |", generic), []);
  });

  test("reads table rows and fenced code as data, so a cell value is seen", () => {
    assert.ok(distinctiveIdentifiers("| Gadget | 3 |").has("gadget"));
    assert.ok(!distinctiveIdentifiers("Gadget is a word in a sentence.").has("gadget"));
  });
});
