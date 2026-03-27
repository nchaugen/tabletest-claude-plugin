---
name: tabletest
description: Use when writing or converting JUnit tests in Java or Kotlin with the TableTest library. Trigger whenever the user wants to test multiple scenarios with the same assertion logic, convert repetitive @Test methods into a table, or start a new @TableTest. Also use when the user asks about TableTest syntax, column design, type converters, or value sets — even if they don't say "TableTest" explicitly.
---

# TableTest Skill

Use this skill before converting similar JUnit tests or adding a new TableTest.

## Pre-Check

Before writing any TableTest code, verify two things:

**Dependencies**: Check `pom.xml`/`build.gradle` for `org.tabletest:tabletest-junit` and a JUnit Jupiter version of 5.11 or higher. The TableTest groupId and artifactId are non-obvious and easy to get wrong from memory — if they're missing, read `references/dependency-setup.md` for the exact coordinates before adding anything.

**Test shape**: TableTest shines when 2+ test cases share the same setup and assertion logic and their differences can be expressed as data (inputs/outputs). A single-scenario `@TableTest` is also fine when it's part of a set of focused, single-responsibility tables (e.g., one table per syntactic feature of a parser) — the benefit is structural consistency and easy row addition later.

Stick with standard `@Test` methods when:
- The implementation is trivial (a single delegating call or log statement) and its contract is already tested elsewhere — a TableTest here adds noise without value.
- Test setup is inherently complex and test-specific (latches, embedded servers, thread coordination) and can't be expressed as data columns without obscuring the setup.
- A higher-level integration test already covers the observable contract of this component, making a unit-level TableTest redundant.

## Quick Example

```java
@TableTest("""
    Scenario         | a  | b  | Sum?
    positive numbers | 1  | 2  | 3
    with zero        | 0  | 5  | 5
    negative number  | -3 | 7  | 4
    """)
void shouldAddNumbers(int a, int b, int sum) {
    assertEquals(sum, Calculator.add(a, b));
}
```

**Notes:**
- TableTest works with any assertion library (JUnit, AssertJ, Hamcrest, etc.). Use the project's existing style.
- Table syntax and built-in conversion are identical for Java and Kotlin. Only method signatures differ.
- **Escape sequences differ**: Java text blocks process `\t`, `\n`, etc. Kotlin raw strings do NOT — they remain literal. For special characters in Kotlin, use actual characters or regular strings.

---

## Table Syntax

### Basic Structure and Mapping

Keep a scenario column as the leftmost column. Do not map it to a parameter unless you annotate the parameter with `@Scenario` (needed for referencing scenario description in test method, or when using inject parameters). Suffix expectation columns with `?` to signal intent (e.g., `Expected?`, `Valid?`).

Rules:
- Align parameter order to data column order (scenario column excluded).
- Provide one parameter per data column.
- Expect one invocation per data row.
- Keep methods non-private, non-static, and returning void.

### Single Values and Quoting

Use blank cells for `null` (reference types). Use `''` for empty strings. Use `'   '` for blank strings.

| Value contains or starts with | Action                           |
|-------------------------------|----------------------------------|
| `\|` (pipe)                   | Quote with `"..."`               |
| `"` or `'`                    | Quote with the other quote style |
| Starts with `[`               | Quote to avoid list syntax       |
| Starts with `{`               | Quote to avoid set syntax        |

```java
@TableTest("""
    Value             | Description
    simple            | No quotes needed
    "contains | pipe" | Quotes required for special chars
    ''                | Empty string
                      | Blank cell = null
    "[1,2,3]"         | Quote to avoid list syntax
    "{a,b}"           | Quote to avoid set syntax
    """)
void testValues(String value, String description) { ... }
```

**Strategy**: Apply minimal quoting. Start without quotes; if a test fails with a parsing error, add quotes only around the problematic value. Over-quoting obscures the data.

**Quote inside collection values, not the whole collection.** For a collection element containing a special character, quote only that element: `[path: 'C:\\Users']`, not `'[path: C:\\Users]'`. The quotes wrap the problematic element, not the entire collection.

**Newlines in values**: To include a newline character inside a table value, write `\\n` in the table (keeps the row on one line), then process it manually in the test method: `value.replace("\\n", "\n")`. Do not use a literal newline — it would split the row across lines. Note: Java text blocks process `\n` into a real newline before TableTest sees it, so use double-backslash `\\n` to preserve it as text for manual processing.

### Collections

Lists use `[]`, sets use `{}`, and maps use `[]` with `key: value` entries.

```java
// List (empty list uses [])
@TableTest("""
    Numbers   | Sum?
    []        | 0
    [1]       | 1
    [1, 2, 3] | 6
    """)
void testSum(List<Integer> numbers, int sum) { ... }

// Set (empty set uses {})
@TableTest("""
    Values       | Size?
    {}           | 0
    {1, 2, 3}    | 3
    {1, 1, 2, 2} | 2
    """)
void testSetSize(Set<Integer> values, int size) { ... }

// Map (empty map uses [:])
@TableTest("""
    Scores               | Highest?
    [:]                  | 0
    [Alice: 95, Bob: 87] | 95
    [x: 1, y: 2, z: 3]   | 3
    """)
void testHighestScore(Map<String, Integer> scores, int highest) { ... }
```

**Common mistake**: Using `[]` for a `Set<>` parameter. Lists use `[]`; sets use `{}`. If a parameter is typed `Set<T>` but the table uses `[]`, JUnit will report a conversion failure. Double-check the brackets match the parameter type.

**Note**: Empty collections are explicit: `[]` for empty list, `{}` for empty set, `[:]` for empty map.

### Built-in Value Conversion

JUnit converts many standard types automatically: primitives, `String`, `Path`, `File`, `URI`, `URL`, `UUID`, `LocalDate`, `LocalTime`, `LocalDateTime`, enums, and more. Prefer direct parameter types that JUnit can convert.

Built-in conversion also applies to collection elements: `[com/example]` → `List<Path>`, `[Bob: 1980-03-04]` → `Map<String, LocalDate>`, `{https://claude.ai}` → `Set<URL>`.

**Date format limitation**: Built-in `LocalDate`/`LocalDateTime` conversion only handles ISO 8601 format (`yyyy-MM-dd`, e.g. `2024-01-15`). Non-standard formats — slash dates (`15/01/2024`), short years (`24-01-15`), locale-specific patterns — will fail at runtime. If any column contains non-ISO date strings, read `references/type-converters.md` before finalising the table and write a `@TypeConverter` method to handle the parsing.

```java
@TableTest("""
    Scenario     | Class Name      | Resolved Path?
    With package | com.example.Foo | com/example/Foo
    Nested class | Outer$Inner     | Outer/Inner
    """)
void converts_class_names(String className, Path expectedPath) {
    assertThat(resolver.resolve(className)).isEqualTo(expectedPath);
}
```

---

## Table Design

### Design Black-Box Tables

Model observable inputs and outputs. Avoid internal flags or setup-only columns unless they are part of the public contract.

```java
@TableTest("""
    Scenario                    | Build Dir | JUnit Property | Configured Dir | Resolved Dir?
    Configured input wins       | build     | report/junit   | tabletest      | tabletest
    JUnit property takes effect | target    | report/junit   |                | report/junit
    Fallback when none set      | build     |                |                | build/junit-jupiter
    """)
void resolvesInputDirectory(String buildDir, String junitProperty, String configuredDir, String resolvedDir) {
    // setup derived from inputs, assert resolvedDir
}
```

### Include All Outputs of a Concern

When an operation produces multiple observable outputs, include them all as expectation columns in one table. Each row should give the complete picture of what happens for a given scenario. Don't split outputs of the same behavioral concern across separate test methods.

```java
// Good — all outputs of priority resolution in one table
@TableTest("""
    Scenario                  | Input Dir | JUnit Dir    | Resolved Path?       | Source?        | Searched Locations?
    Configured input wins     | my-config | report/junit | my-config            | CONFIGURED     | [my-config]
    JUnit property wins       |           | report/junit | report/junit         | JUNIT_PROPERTY | [report/junit, build/junit-jupiter]
    Fallback wins             |           |              | build/junit-jupiter  | FALLBACK       | [build/junit-jupiter]
    """)
void resolvesWithPriority(String inputDir, String junitDir,
                          String resolvedPath, ResolutionSource source, List<String> searchLocations) { ... }
```

```java
// Bad — same outputs split across separate tests
void resolvesPath(...)            // tests Resolved Path? only
void reportsSource(...)           // tests Source? only
void reportsSearchedLocations(...)// tests Searched Locations? only
```

Splitting forces the reader to cross-reference multiple tables to understand one behavior. If the outputs all come from the same operation and concern, they belong together.

Separate tests are appropriate when testing a **different concern** of the same operation (e.g., path normalization vs. priority resolution) or a different method entirely.

### Match Table Structure to the Logic Being Tested

The type of logic under test determines what each row should represent:

- **Decision/priority logic**: Each row is a distinct decision point. Scenario names describe which rule takes precedence (e.g., "X wins over Y").
- **Parsing/validation logic**: Each row is a distinct input variation. Scenario names describe the input condition (e.g., "Empty input", "With special characters").
- **Transformation logic**: Each row is an input/output pair. Scenario names describe the transformation case.

If rows feel out of place — parsing variations in a decision table, or decision branches in a parsing table — this signals the code under test may be mixing responsibilities. Consider whether the method should be split before adding more test rows.

### Name Expectation Columns Clearly

End expectation columns with `?` **suffix** to signal which columns are outputs being verified versus inputs being provided.

Examples: `Valid?`, `Formatted?`, `Result?`, `Throws?`, `Expected?`

**Common mistake** — `?` as prefix instead of suffix:
```
?Source        ← WRONG
Source?        ← CORRECT
```

### Use @Description When It Adds Information

Add `@Description` when there is context the table alone cannot convey. Omit it when the table already says everything — a vacuous description adds noise.

Good reasons to add `@Description`:
- **Fixed values** shared by all rows that are not columns (e.g., "order value is always 100")
- **Domain context** — where/when the rule applies, who is affected, which market
- **Open questions** — decisions not yet resolved
- **Relationship between tables** — how this table connects to others in the class

Do not restate what the table already shows. If the description merely summarises the column names or row outcomes, delete it.

```java
// GOOD — adds context not visible in the table
@Description("""
    Applies to UK market only. Base salary is annual; bonus is percentage
    of base. Open: should contractors in their notice period receive
    a pro-rated bonus?
    """)

// BAD — restates what the table shows
@Description("Bonus percentage is determined by employee level and department")
```

`@DisplayName` serves as a section header in reports. `@Description` provides the explanatory text underneath. Together they make the published test report readable as documentation without the table needing to be self-explanatory on every detail.

### Annotation Order

Annotations on a `@TableTest` method must appear in this order:

1. `@DisplayName` (if present)
2. `@Description` (if present)
3. `@TableTest`

```java
@DisplayName("Weekly pay calculation")
@Description("""
    Regular pay: hours × rate (up to 40 hours).
    Overtime: 1.5× rate for hours above 40.
    Sunday premium: 2× rate. Holiday premium: 2.5× rate.
    """)
@TableTest("""
    Scenario        | Weekday hrs | Sunday hrs | Rate  | Regular pay? | Overtime pay? | Weekly pay?
    Standard week   | 40          |            | 20.00 | 800.00       |               | 800.00
    Five hours OT   | 45          |            | 20.00 | 800.00       | 150.00        | 950.00
    """)
void shouldCalculateWeeklyPay(...) { ... }
```

### Model Exceptions as Expected Columns

When a table covers error/rejection cases, include the exception type as an expected column (`Throws?` or `Exception?`) — don't hardcode the exception class in the method body. This makes each row's expected outcome visible in the table.

```java
@TableTest("""
    Scenario        | Input    | Throws?
    Empty string    | ''       | IllegalArgumentException
    Letters only    | abc      | NumberFormatException
    Negative amount | -10.00   | IllegalArgumentException
    """)
void shouldRejectInvalidInput(String input, Class<? extends Exception> throws_) {
    assertThrows(throws_, () -> parse(input));
}
```

Keep null cases as blank-cell rows in the main table rather than extracting them to separate `@Test` methods:

```java
@TableTest("""
    Scenario     | Input | Result?
    Valid number | 42.50 | 42.50
    Null input   |       |
    """)
void shouldParseAmount(String input, BigDecimal result) {
    assertEquals(result, parse(input));
}
```

### Include Traceability Columns

When a table tests a pipeline (input → intermediate result → final result), include the intermediate result as an expectation column. This lets readers trace the logic step by step:

```java
@TableTest("""
    Scenario                    | Customer type | Order value | Loyalty years | Discount tier? | Final price?
    New customer, small order   | Regular       | 50.00       | 0             | None           | 50.00
    Loyal customer, small order | Regular       | 50.00       | 5             | Silver         | 45.00
    VIP, large order            | VIP           | 200.00      | 10            | Gold           | 160.00
    """)
```

The `Discount tier?` column is not strictly necessary (the test could verify only `Final price?`), but it lets the reader trace: customer + order + loyalty → discount tier → price. When a row fails, the intermediate column shows where in the pipeline the error occurred.

### Name Scenarios Descriptively

Describe the condition being tested, not the expected outcome. Good scenario names answer "under what circumstances?" rather than "what happens?".

| Good                         | Bad             |
|------------------------------|-----------------|
| `Negative input`             | `Returns error` |
| `Empty list`                 | `Sum is zero`   |
| `User without licence`       | `Cannot rent`   |
| `Divisible by 4 but not 100` | `Is leap year`  |

Scenario names appear in test failure messages, so clarity helps diagnose failures quickly.

### Use Concrete Domain Values

Column values should be concrete, meaningful data — not abstract flags or codes. Expectation column values should be traceable to input column values.

**Good** — directory names as inputs, resolved dir traceable to an input column:
```java
@TableTest("""
    Scenario             | Configured Dir | JUnit Dir    | Fallback State | Resolved Dir? | Source?
    Configured wins      | my-config      | report/junit | yaml           | my-config     | CONFIGURED
    JUnit property wins  |                | report/junit | yaml           | report/junit  | JUNIT_PROPERTY
    Fallback wins        |                |              | yaml           | target/junit  | FALLBACK
    """)
```

**Bad** — abstract flags, expectation values not traceable to inputs:
```java
@TableTest("""
    Scenario             | Has Config | Override State | Fallback State | Resolved?
    Configured wins      | true       | yaml           | yaml           | configured
    Override wins        | false      | yaml           | yaml           | override
    Fallback wins        | false      |                | yaml           | fallback
    """)
```
In the bad example, `configured`, `override`, and `fallback` in Resolved? are names hardcoded in the test body, not visible in the table. The reader cannot understand the table without reading the test code.

When a value is derived from an input column (e.g., fallback path = Build Dir + "/junit-jupiter"), include the source column so readers can trace the derivation:
```java
@TableTest("""
    Scenario        | Build Dir | Build State | Resolved Dir?
    Maven fallback  | target    | yaml        | target/junit-jupiter
    Gradle fallback | build     | yaml        | build/junit-jupiter
    """)
```
Here `target/junit-jupiter` is visibly derived from `Build Dir = target`.

### Use Domain Terminology

Column names should use domain or feature terminology that readers understand without knowing the implementation. Avoid parameter names, variable names, or internal API terms.

| Good (Domain)          | Bad (Implementation)      |
|------------------------|---------------------------|
| `JUnit Dir`            | `Override`                |
| `Build Output`         | `junitOutputDirOverride`  |
| `Search Locations?`    | `Candidates?`             |

### Use Value Sets for "Regardless Of" Relationships

When one input takes precedence regardless of other inputs, use value sets to express this declaratively instead of listing every combination. Each `{...}` column generates a test per value.

```java
@TableTest("""
    Scenario                   | Priority | Fallback State         | Resolved?
    Priority wins regardless   | main     | {yaml, empty, missing} | main
    """)
```

This single row generates 3 tests, all asserting `main` wins regardless of fallback state. See `references/value-sets.md` for full syntax.

**Value set semantics: every value must produce the same expected result.** A value set `{A, B, C}` asserts that the result is identical regardless of which value is chosen. Do not use value sets where results differ:

```java
// WRONG — 40 × 15.00 = 600, but 40 × 20.00 = 800; results differ
Standard week | 40 | {15.00, 20.00} | *

// CORRECT — use separate rows when results differ
Standard week               | 40 | 15.00 | 600.00
Standard week, higher rate  | 40 | 20.00 | 800.00

// CORRECT — value set is fine when result is genuinely identical
Zero hours | 0 | {15.00, 20.00} | 0.00
```

### Null, Empty, and Blank Values

Use blank cells for null, `''` for empty strings, and `'   '` for blank strings.

```java
@TableTest("""
    Scenario        | Input | Resolved?
    Normal input    | hello | HELLO
    Null input      |       |
    Empty input     | ''    |
    Blank input     | '   ' |
    """)
void resolves_values(String input, String resolved) {
    assertThat(transform(input)).isEqualTo(resolved);
}
```

**Note**: These are syntax examples, not test design patterns. Null/empty/blank variants of an input should typically be additional rows in the test that covers the feature, not in a separate test method. For example, if a resolver ignores blank JUnit dir values, add those as rows in the main resolution test rather than creating a separate "handles blank values" test.

### Empty Cells for Optional Inputs

When an input column is not relevant to certain scenarios (e.g., a peak surcharge when testing off-peak rides), use blank cells to signal "not part of this scenario" — do not fill with 0 or a default value. This makes it immediately clear which inputs matter for each row.

```java
@TableTest("""
    Scenario              | Base fare | Peak surcharge | Airport fee | Total fare?
    Off-peak city ride    | 12.00     |                |             | 12.00
    Peak-hour city ride   | 12.00     | 5.00           |             | 17.00
    Airport pickup        | 12.00     |                | 8.00        | 20.00
    Peak airport pickup   | 12.00     | 5.00           | 8.00        | 25.00
    """)
void shouldCalculateTotalFare(BigDecimal baseFare, BigDecimal peakSurcharge,
        BigDecimal airportFee, BigDecimal totalFare) {
    // Blank cells → null (BigDecimal is a reference type); null treated as 0.00
    BigDecimal peak = peakSurcharge != null ? peakSurcharge : BigDecimal.ZERO;
    BigDecimal airport = airportFee != null ? airportFee : BigDecimal.ZERO;
    // ...
}
```

Use `Integer` (not `int`) for parameters that may be blank — primitive `int` cannot represent null. Handle null-to-default conversion in the test method body.

---

## Workflow

### Pair Programming Flow

When writing TableTests with a pair, the most important habit is showing a mockup before writing any code — a 30-second table sketch prevents 5 minutes of rework. The full collaborative cadence is in `references/pair-programming.md`; read it when pairing or when the user wants a structured walkthrough.

### Design Phase (Before Writing Code)

Resist the urge to start coding immediately. The time spent understanding the code under test pays off in a cleaner table structure:

1. **Trace the logic**: Map decision trees, loops, or state transitions. Identify what actually varies between scenarios — this directly determines your columns.
2. **Sketch the table**: What inputs vary? What outputs do you observe? How many scenarios do you need?
3. **Show a mockup** with 2-3 rows before implementing — agree on column structure, naming, and coverage first:
   ```
   | Scenario        | orgId | featureId | version | Feature Toggles | Query Count? | Result?
   | Specific match  | O     | F         | V       | [O:F:V: true]   | 1            | true
   | Wild version    | O     | F         | V       | [O:F:*: true]   | 2            | true
   ```

### Converting Existing Tests

1. Identify tests with identical structure but different data.
2. Extract the varying parts as columns (inputs and expected values).
3. Create table with scenario column first, inputs next, expectations last (suffix with `?`).
4. Align method parameters to column order; do not bind the scenario column unless annotated with `@Scenario`.
5. Verify all rows use the same assertion logic.
6. After building table with multiple rows, check for column consolidation opportunities (see Quality Checks).

### Writing New TableTest

1. **Understand phase**: Read the code, trace logic, identify variations
2. **Design phase**: Sketch table structure, discuss with pair
3. **Confirm**: Show mockup with 2-3 rows, get agreement
4. **Implement**: Create full table with all scenarios
5. **Run immediately**: Get fast feedback on structure and conversions
6. **Refine**: Improve names after tests pass (see `references/incremental-development.md` for full refinement workflow)
7. **Enhance**: Consider additional tables for other aspects (see `references/incremental-development.md` for progressive enhancement)

### Refinement Phase

After tests pass, improve names and structure — see `references/pair-programming.md` for the full refinement workflow. The short version: names emerge from understanding, so don't expect perfect column or scenario names on the first implementation. Replace implementation terms with domain language once the table is working.

---

## Quality Checks

After writing, verify:
- [ ] **Multiple rows**: table has 2+ rows; use `@Test` for single cases
- [ ] **Black-box design**: columns represent observable inputs and outputs, not internal flags or implementation details
- [ ] **Clear communication**: scenario names describe conditions (not outcomes), column names use domain language (not parameter names)
- [ ] **Uniform assertions**: all rows use the same assertion logic; split into separate TableTests if logic differs per row
- [ ] **Straightforward method**: no `if`/`switch` statements, no parsing or conversion logic; the method only arranges, acts, and asserts
- [ ] **Parameter alignment**: parameters match data columns left-to-right (excluding scenario column)
- [ ] **Parameter conversion**: custom type converter methods (annotated `@TypeConverter`) or JUnit converters handle type conversion, keeping the test method free of parsing code
- [ ] **Valid syntax**: values requiring quotes are quoted, collections use correct bracket syntax, empty collections are explicit (`[]`, `{}`, `[:]`)
- [ ] **Expectation columns present**: at least one column uses `?` suffix (not prefix)
- [ ] **Concrete values**: expectation values are traceable to input column values where applicable
- [ ] **Correct expected values**: arithmetic in expected columns verified independently; every row's output matches the stated rules
- [ ] **Value set semantics**: value sets only used where every value produces the same result; not used as shorthand for "test multiple values"
- [ ] **Optional inputs blank**: columns not relevant to a scenario use blank cells (not 0 or defaults); parameter types support null
- [ ] **Traceability columns**: intermediate expected values included where they help trace multi-step logic
- [ ] **@Description adds information**: if present, `@Description` provides context beyond what the table shows (fixed values, domain context, open questions) — not a restatement of columns or rows. Omit `@Description` if there is nothing to add.
- [ ] **@Description uses text block**: `@Description` uses `"""` text blocks, not string concatenation with `+`
- [ ] **Annotation order**: `@DisplayName` → `@Description` → `@TableTest` (no other order)
- [ ] **Exception column**: error/rejection tables have a `Throws?` or `Exception?` column, not hardcoded exception classes in the method body
- [ ] **Complete outputs**: all observable outputs of the same behavioral concern are in one table, not split across separate tests
- [ ] **Row coherence**: rows match the type of logic being tested (decision points for priority logic, input variations for parsing logic); out-of-place rows may signal mixed responsibilities in the code under test
- [ ] **Column consolidation**: if multiple columns are mutually exclusive (both identity and status vary together), consider consolidating into single column with composite values (e.g., `Primary OK`, `Secondary ERROR`)
- [ ] **Cross-table consistency**: if multiple TableTests exist in the same class, use consistent notation for similar concerns (timing, errors, special values); share parsers and helper methods
- [ ] **Test helpers organized**: helper classes placed at bottom of test file with clear names (`QueryCounter`, not `Helper`); only extract to separate file when reused across test classes

---

## Advanced References

**READ these references when the condition applies. DO NOT proceed without reading:**

| Reference                                | When to use                                                                |
|------------------------------------------|----------------------------------------------------------------------------|
| `references/dependency-setup.md`         | Project lacks TableTest dependency                                         |
| `references/value-sets.md`               | Multiple example inputs map to same expectation                            |
| `references/type-converters.md`          | Custom types need parsing logic; non-ISO date formats appear in the table (`dd/MM/yyyy`, `yy-MM-dd`, etc.); or any column value won't convert automatically |
| `references/column-design.md`            | Deciding whether to split, combine, or use maps for columns; cross-table consistency |
| `references/common-patterns.md`          | Consolidating identity+status, positional fields, timing, async testing    |
| `references/large-tables.md`             | Need comments, grouping, or external table files                           |
| `references/example-patterns.md`         | Need inspiration for table design (business rules, boundaries, exceptions) |
| `references/async-and-performance.md`    | Testing async/non-blocking behavior or tracking execution order            |
| `references/provided-parameters.md`      | Using `@TempDir` or other injected parameters                              |
| `references/table-design-advanced.md`    | Table has rows that don't fit; mixed concerns suspected; scenario names unclear |
| `references/incremental-development.md`  | Building a complex table iteratively; learning from test failures          |
| `references/consolidating-tests.md`      | Removing @Test methods covered by table                                    |
| `references/testing-reveals-bugs.md`     | Test design feels wrong; suspecting implementation bug                     |
| `references/pair-programming.md`         | Pairing with a colleague; need structured collaborative cadence            |
