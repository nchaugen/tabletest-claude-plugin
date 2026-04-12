---
name: tabletest
description: Use when writing or converting JUnit tests in Java or Kotlin with the TableTest library. Trigger whenever the user wants to test multiple scenarios with the same assertion logic, convert repetitive @Test methods into a table, or start a new @TableTest. Also trigger when the user asks to write tests for a feature — even without mentioning TableTest. Also use when the user asks about TableTest syntax, column design, type converters, or value sets — even if they don't say "TableTest" explicitly.
---

# TableTest Skill

Use this skill before converting similar JUnit tests or adding a new TableTest.

## Pre-Check

Before writing any TableTest code, verify two things:

**Dependencies**: Check `pom.xml`/`build.gradle` for `org.tabletest:tabletest-junit` and a JUnit Jupiter version of 5.11 or higher. If the dependency is missing, add it:

Maven:
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Gradle:
```groovy
testImplementation "org.tabletest:tabletest-junit:VERSION"
```

Imports:
```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Scenario;         // only when binding scenario column
import org.tabletest.junit.TypeConverter;     // only for custom converter methods
import org.tabletest.junit.TypeConverterSources; // only for shared converter sources
```

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
| `:` (colon)                   | Quote to avoid map key:value syntax |
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

**Date format limitation**: Built-in `LocalDate`/`LocalDateTime` conversion only handles ISO 8601 format (`yyyy-MM-dd`, e.g. `2024-01-15`). Non-standard formats — slash dates (`15/01/2024`), short years (`24-01-15`), locale-specific patterns — will fail at runtime. If any column contains non-ISO date strings, write a `@TypeConverter` method to handle the parsing (see Custom Type Converters section below).

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

#### Non-obvious Built-in Conversions

| Table value | Parameter type | Notes |
|---|---|---|
| `"SECONDS"` | `TimeUnit` (any enum) | Enum name, case-sensitive |
| `"0xF"`, `"017"` | `int`/`long` (and boxed) | Hex and octal literals work |
| `"java.lang.Integer"` | `Class<?>` | Fully-qualified name required |
| `"java.lang.Thread$State"` | `Class<?>` | `$` for nested classes |
| `"byte"` | `Class<?>` | Primitive type names work |
| `"PT3S"`, `"PT1H30M"` | `Duration` | ISO 8601 duration format |
| `"P2M6D"` | `Period` | ISO 8601 period format |
| `"JPY"` | `Currency` | ISO 4217 currency code |
| `"en-US"` | `Locale` | IETF BCP 47 language tag |

**Enums**: Write the enum constant name — `"SECONDS"` not `"TimeUnit.SECONDS"`. The parameter type tells JUnit which enum to use.

---

## Custom Type Converters

When JUnit's built-in converters don't support your parameter type, add custom type converter methods annotated with `@TypeConverter`.

### Prefer Built-in Conversion First

JUnit can convert strings to `Class<?>` when the value is a fully-qualified class name. Write `java.lang.RuntimeException` in the table instead of `RuntimeException` plus a custom `@TypeConverter`. Only write a converter method when built-in conversion does not cover the type.

### Writing Custom Converter Methods

#### Java

Place custom converter methods as `@TypeConverter`-annotated `public static` methods in **a public test class** or a class listed in `@TypeConverterSources`.

**IMPORTANT:** The test class must be declared `public` for TableTest to discover converter methods:
```java
public class MyTest {  // Must be public, not package-private
    // Converter methods here will be found
}
```

```java
@TableTest("""
    Date       | Days Until?
    today      | 0
    tomorrow   | 1
    """)
void testDaysUntil(LocalDate date, int expected) {
    assertEquals(expected, ChronoUnit.DAYS.between(LocalDate.now(), date));
}

@TypeConverter
public static LocalDate parseLocalDate(String input) {
    return switch (input) {
        case "today" -> LocalDate.now();
        case "tomorrow" -> LocalDate.now().plusDays(1);
        default -> LocalDate.parse(input);
    };
}
```

#### Kotlin

Two options for Kotlin:

**Package-level functions** (preferred):
```kotlin
@TypeConverter
fun parseLocalDate(input: String): LocalDate = when (input) {
    "today" -> LocalDate.now()
    "tomorrow" -> LocalDate.now().plusDays(1)
    else -> LocalDate.parse(input)
}

class DateTest {
    @TableTest("""
        Date       | Days Until?
        today      | 0
        tomorrow   | 1
        """)
    fun testDaysUntil(date: LocalDate, expected: Int) {
        assertEquals(expected, ChronoUnit.DAYS.between(LocalDate.now(), date))
    }
}
```

**Companion object with @JvmStatic**:
```kotlin
class DateTest {
    companion object {
        @JvmStatic
        @TypeConverter
        fun parseLocalDate(input: String): LocalDate = when (input) {
            "today" -> LocalDate.now()
            "tomorrow" -> LocalDate.now().plusDays(1)
            else -> LocalDate.parse(input)
        }
    }
}
```

**Note**: `@Nested` inner classes in Kotlin cannot have companion objects. Use package-level functions or outer class companion object instead.

### Sharing Converters with @TypeConverterSources

For shared converter methods across multiple test classes:

```java
@TypeConverterSources(DateConverters.class)
class DateTest {
    @TableTest("""
        ...
        """)
    void testWithSharedConverters(LocalDate date, Duration duration) { ... }
}
```

Kotlin — use an `object` declaration with `@JvmStatic` and `@TypeConverter`:
```kotlin
object DateConverters {
    @JvmStatic
    @TypeConverter
    fun parseLocalDate(input: String): LocalDate = ...
}

@TypeConverterSources(DateConverters::class)
class DateTest { ... }
```

### Converter Method Requirements

A converter method will be used when it:
1. Is annotated with `@TypeConverter`
2. Is defined as a `public static` method in a `public class`
3. Accepts exactly one parameter
4. Returns an object of the target parameter type
5. **Is the only `@TypeConverter` method matching the above criteria in the class**

There is no specific naming pattern required, but `parse<TypeName>` (e.g., `parseLocalDate`, `parseMoney`) is conventional.

### One Converter Per Target Type

You cannot have multiple converter methods with the same return type. If two columns need different parsing for the same type, use a single converter that handles both formats, or split into columns with different types.

### Handling Null Values

When table cells can be blank (representing null), use boxed types instead of primitives:

```java
// WRONG - primitives cannot be null
void test(String value, long time) { ... }  // blank cell causes error

// CORRECT - use boxed type
void test(String value, Long time) { ... }  // blank cell becomes null
```

Converter methods receive `null` for blank cells — handle this explicitly:
```java
@TypeConverter
public static Long parseResponseTime(String value) {
    if (value == null || value.isBlank()) {
        return null;
    }
    if (value.startsWith("<")) {
        return Long.valueOf(value.substring(1));
    }
    return Long.parseLong(value);
}
```

### Domain-Specific Formatting

Converter methods enable readable domain conventions in tables:

```java
@TableTest("""
    Scenario     | Response Time?
    Fast         | <50
    Acceptable   | <150
    Slow         | <500
    """)
void testResponseTime(Long maxResponseTimeMs) { ... }

@TypeConverter
public static Long parseResponseTime(String value) {
    if (value == null || value.isBlank()) return null;
    if (value.startsWith("<")) return Long.valueOf(value.substring(1));
    return Long.parseLong(value);
}
```

Other examples: `5m`/`30s` → milliseconds, `$100` → numeric, `50%` → 0.5, `10KB` → bytes.

**Calendar dates**: Prefer descriptive values like `before cutoff`, `on cutoff`, `after cutoff` with a `@TypeConverter` over raw ISO dates. The reader doesn't need to mentally compare `2025-02-28` against `2025-03-01`. If raw dates are used, include the policy/cutoff date as a separate column so the reader can verify the comparison.

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

Splitting forces the reader to cross-reference multiple tables to understand one behavior. If the outputs all come from the same operation and concern, they belong together.

Separate tests are appropriate when testing a **different concern** of the same operation (e.g., path normalization vs. priority resolution) or a different method entirely. Even when testing a single API method, decompose concerns into separate `@TableTest` methods using default values for irrelevant inputs. Separate tables reduce rows by avoiding unnecessary permutations — and the table count guides implementation: five concern tables suggest five functions.

### Match Table Structure to the Logic Being Tested

The type of logic under test determines what each row should represent:

- **Decision/priority logic**: Each row is a distinct decision point. Scenario names describe which rule takes precedence (e.g., "X wins over Y").
- **Parsing/validation logic**: Each row is a distinct input variation. Scenario names describe the input condition (e.g., "Empty input", "With special characters").
- **Transformation logic**: Each row is an input/output pair. Scenario names describe the transformation case.

If rows feel out of place — parsing variations in a decision table, or decision branches in a parsing table — this signals the code under test may be mixing responsibilities. Consider whether the method should be split before adding more test rows.

### Name Expectation Columns Clearly

End expectation columns with `?` **suffix** to signal which columns are outputs being verified versus inputs being provided.

Examples: `Valid?`, `Formatted?`, `Result?`, `Throws?`, `Expected?`

**Prefer the rule's direct output.** Use `Discount?` over `Price?` — the discount is what the rule decides; verifying the price requires knowing the base price. If you use a derived value like price, include the base as a column so readers can trace it. Input columns never have `?` suffixes — including yes/no flag columns that describe scenario state.

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

Do not restate what the table already shows. If the description merely summarises the column names or row outcomes, delete it. Don't include irrelevant fixed values — "Fixed for all rows: name = 'Alice Smith'" is noise unless the name affects behaviour. Values hardcoded in the method body that affect outcomes should be columns, not description text.

```java
// GOOD — adds context not visible in the table
@Description("""
    Applies to domestic flights only. Baggage weight is per piece,
    not cumulative. Open: should frequent flyers in downgraded
    cabins retain their original baggage allowance?
    """)

// BAD — restates what the table shows
@Description("Tax bracket is determined by income range and filing status")
```

`@DisplayName` serves as a section header in reports. `@Description` provides the explanatory text underneath. Together they make the published test report readable as documentation without the table needing to be self-explanatory on every detail.

### Annotation Order

Annotations on a `@TableTest` method must appear in this order:

1. `@DisplayName` (if present)
2. `@Description` (if present)
3. `@TableTest`

```java
@DisplayName("Parking fee calculation")
@Description("""
    First 2 hours are free. Hours 3-5 are charged at the standard rate.
    Hours beyond 5 are charged at 2× the standard rate.
    Weekend parking is always free regardless of duration.
    """)
@TableTest("""
    Scenario           | Day      | Hours | Rate  | Free hrs? | Standard hrs? | Surcharge hrs? | Total fee?
    Within free window | Monday   | 1     | 3.00  | 1         |               |                | 0.00
    Standard rate      | Tuesday  | 4     | 3.00  | 2         | 2             |                | 6.00
    With surcharge     | Wednesday| 7     | 3.00  | 2         | 3             | 2              | 21.00
    """)
void shouldCalculateParkingFee(...) { ... }
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

### Collapse Sparse Columns into a Map

When several columns are mostly blank, consider collapsing them into a single map column (e.g. `[key: value, key: value]`) with a `@TypeConverter` to construct the target object. This is especially appropriate when the sparse columns correspond to a single parameter in the method under test. The map keeps the table compact and moves construction logic out of the test method.

### Include Traceability Columns

When a table tests a pipeline (input → intermediate result → final result), include the intermediate result as an expectation column. This lets readers trace the logic step by step:

```java
@TableTest("""
    Scenario                      | Property type | Square metres | Flood zone | Risk rating? | Annual premium?
    Small residential, safe area  | Residential   | 80            | None       | Low          | 400.00
    Large residential, flood risk | Residential   | 200           | Zone A     | High         | 1200.00
    Commercial, moderate risk     | Commercial    | 500           | Zone B     | Medium       | 2500.00
    """)
```

The `Risk rating?` column is not strictly necessary (the test could verify only `Annual premium?`), but it lets the reader trace: property + area + flood zone → risk rating → premium. When a row fails, the intermediate column shows where in the pipeline the error occurred.

**Guard:** Only use traceability columns for values the system under test exposes or that represent observable domain concepts. If you would need to reimplement an internal calculation in the test body to populate the column, it doesn't belong — the intermediate likely points to a separate concern that needs its own `@TableTest` method. Decompose into multiple tables instead; the intermediate becomes an output in one table and an input in the next.

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

This single row generates 3 tests, all asserting `main` wins regardless of fallback state.

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

#### Cartesian Product

Multiple sets in the same row create a cartesian product:

```java
@TableTest("""
    Scenario | a      | b      | Max Sum?
    Combined | {1, 2} | {3, 4} | 6
    """)
void testCartesianProduct(int a, int b, int maxSum) {
    assertTrue(a + b <= maxSum);
}
```

This generates 4 test cases: (1,3), (1,4), (2,3), (2,4).

#### "Doesn't Matter" Pattern

Use value sets when a flag is irrelevant for certain scenarios:

```java
@TableTest("""
    Scenario    | Master | Fallback      | Expected?
    Normal flow | true   | {true, false} | success
    Error path  | true   | true          | fallback
    """)
void handles_errors(boolean master, boolean fallback, String expected) { ... }
```

Row 1: Fallback flag doesn't matter when there's no error, so test both values.
Row 2: Fallback flag is critical for error handling, so specify exact value.

**Don't use value sets when scenario descriptions add context.** Email validation patterns like "missing local part", "no TLD", "missing @" each test a different structural rule — grouping them as `{@missing.com, user@.com}` loses the *why*.

#### Value Sets for Tier Grouping

When multiple input values produce the same output (a tier), group them into a value set:

```
Scenario   | Credit Hours         | Standing?
Freshman   | {0, 10, 20, 29}     | Freshman
Sophomore  | {30, 45, 59}        | Sophomore
Junior     | {60, 75, 89}        | Junior
Senior     | {90, 100, 120}      | Senior
```

This makes the tier structure a first-class concept — each row IS a tier.

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

**Blank vs value set for irrelevant inputs**: Blank cells mean the input is genuinely absent (null). When the input exists but is irrelevant to the outcome, use a value set instead: `{UK, Ireland, Other}` for destination means "destination exists but doesn't affect this result". Don't use blanks for "doesn't matter" — blanks mean null.

**Note**: These are syntax examples, not test design patterns. Null/empty/blank variants of an input should typically be additional rows in the test that covers the feature, not in a separate test method.

---

## Workflow

Pre-formed table designs tend to miss value sets, traceability columns, and concern separation. Even if you've already explored the codebase or sketched a structure, run through the Design Phase below — it often surfaces design improvements that aren't obvious until you work through the steps.

### Design Phase (Before Writing Code)

Resist the urge to start coding immediately. The approach depends on what you are starting from:

**From existing code or tests** (there is code to trace or tests to convert):
1. **Trace the logic**: Map decision trees, loops, or state transitions. Identify what actually varies between scenarios — this directly determines your columns.
2. **Sketch the table**: What inputs vary? What outputs do you observe? How many scenarios do you need?
3. **Show a mockup** with 2-3 rows before implementing — agree on column structure, naming, and coverage first:
   ```
   | Scenario        | orgId | featureId | version | Feature Toggles | Query Count? | Result?
   | Specific match  | O     | F         | V       | [O:F:V: true]   | 1            | true
   | Wild version    | O     | F         | V       | [O:F:*: true]   | 2            | true
   ```

**From natural-language requirements** (the prompt describes a feature, not existing code):
Follow the Requirements to Tables workflow below.

### Requirements to Tables

Use this workflow when writing tests from natural-language requirements, vague feature descriptions, or when it is not clear how to decompose the behaviour into tables.

**Your deliverable is a Java test class with `@TableTest` methods.** Do not output markdown tables. Do not stop for user feedback between steps. Work through every step below, then write the Java class directly as your final output.

Each step is an analysis step that shapes the table structure. You do not output anything until step 9, where you write the complete Java `@TableTest` class.

#### Step 1. Name Each Concern

Identify the distinct behaviours in the requirement. Name each one as a verb phrase:
- "Assess water quality"
- "Determine parking zone"
- "Check voter eligibility"

**If you cannot name a behaviour without using "and", it is two concerns** — split them. Each concern becomes a candidate for its own `@TableTest` method.

Signs that concerns are mixed:
- Some rows need columns that other rows leave blank throughout
- Scenario names require qualifiers like "...for eligibility" vs "...for pricing"
- The table has two groups of output columns that never both apply in the same row

**Signs of a missing concern:** An input to one rule is itself derived from raw data. The derivation has its own edge cases and needs boundary testing in a separate table. The rule table then takes the derived value as a direct input column, not the raw data. Two tables, not one.

#### Step 2. Find the First Example

For each concern, start with the simplest, most obvious case:
- What does a typical successful case look like?
- What are the concrete values — not abstractions, but real numbers, names, dates?

Write this as the first data row. Column naming can be rough at this stage.

#### Step 3. Probe for Variations

Work through these systematically:

**Different outcomes** — what makes the decision go the other way?
- "What makes the answer change from yes to no?"

**Boundary conditions** — where exactly do rules trigger?
- Include rows at the threshold, just above, and just below
- When a boundary is ambiguous (inclusive vs exclusive?), add rows on both sides AND mark the expected output as an open question

**Special cases** — situations that may surprise:
- "What is a common misunderstanding about this behaviour?"

**Absent inputs** — what happens when information is not provided?
- "Is there a sensible default, or does absence cause rejection?"

#### Step 4. Probe for Irrelevant Inputs

When one input clearly does not affect the outcome, use a value set to express "regardless of":

```
Scenario                        | Customer Age | Car Category       | Eligible?
Underage regardless of category | 17           | {Economy, Premium} | no
```

Check every input dimension mentioned in the requirements. If the requirement says "regardless of X", X must appear as a column with a value set — omitting it silently hides the assertion that X is irrelevant.

**Value sets for identical entity types:** When two categories follow identical rules (e.g. `{Manager, Director}` both have the same expense approval limit), express them as a value set in one row rather than separate rows.

#### Step 5. Separate Rules from Arithmetic

Tables should specify the interesting decisions — classifications, eligibility rules, tier lookups, state transitions — not test that multiplication works.

**Good decomposition** — separate the rule from the calculation:

Table 1 — the rule (which tier?):
```
Scenario       | Weight | Zone     | Category?  | Surcharge?
Light domestic | 0.5    | Domestic | Standard   | none
Heavy domestic | 12     | Domestic | Oversize   | 5.00
```

Table 2 — the arithmetic (what does it cost?):
```
Scenario       | Base rate | Surcharge | Total?
No surcharge   | 10.00     |           | 10.00
With surcharge | 10.00     | 5.00      | 15.00
```

#### Step 6. Frame Stateful Features as Rules

When a feature involves state (playlists, queues, workflows), frame each row as a state transition rule:

```
Scenario              | Playlist before  | Action         | Playlist after?  | Message?
Add to empty playlist | []               | add Revolver   | [Revolver x1]    | Added
Remove last item      | [Revolver x1]    | remove Revolver| []               | Removed
Remove unknown item   | [Revolver x1]    | remove Abbey   | [Revolver x1]    | Not found
```

Each row is independent: given this state, when this action happens, expect this result. **Include before and after columns** — even when the prompt describes the operation procedurally.

#### Step 7. Make Thresholds Visible

When a rule depends on a threshold or limit, include it as a column — even when the value is constant across every row:

```
Scenario            | Customer Age | Max Age (Policy) | Eligible?
Standard customer   | 30           | 75               | yes
At the limit        | 75           | 75               | yes
Just over the limit | 76           | 75               | no
```

A constant column often signals configuration. Ask: "Under what circumstances would this value differ?"

#### Step 8. Note What Is Still Open

Not everything needs to be resolved before coding. Note open questions internally — they will go in `@Description` annotations in the Java code (step 9), not in a separate markdown section.

**Do not silently resolve ambiguities.** When two interpretations of a rule are plausible, note both as an open question rather than picking one.

#### Step 9. Write the `@TableTest` Code

This is the only step that produces output. Everything above was analysis. Write the complete Java test class now — each concern becomes a `@TableTest` method. Follow the Table Design section above for syntax and the Quality Checks below for verification.

For each `@TableTest` method:
- Add `@DisplayName` with a clear title derived from the concern name (step 1)
- Add `@Description` with context the table alone cannot convey — fixed values, formulas, domain context, or open questions from step 8
- Use `@TypeConverter` methods for any human-readable values that need conversion
- Follow annotation order: `@DisplayName` → `@Description` → `@TableTest`

**Do not present markdown tables for review.** Go directly to the Java code. Open questions from step 8 belong in `@Description` annotations inside the code.

##### Final checklist

- [ ] Each concern has its own `@TableTest` method with a clear name
- [ ] Boundary values are tested at the threshold (not just mid-range values)
- [ ] Value sets group same-outcome values where applicable
- [ ] Rules and arithmetic are in separate `@TableTest` methods
- [ ] Stateful features use before/action/after columns
- [ ] Policy thresholds are visible as columns
- [ ] Open questions are in `@Description`, not silently resolved
- [ ] Each row is independently executable — no row depends on a prior row
- [ ] `@DisplayName`, `@Description`, `@TableTest` annotations in correct order
- [ ] `@TypeConverter` methods handle any non-standard type conversions

### Converting Existing Tests

1. Identify tests with identical structure but different data.
2. Extract the varying parts as columns (inputs and expected values).
3. Create table with scenario column first, inputs next, expectations last (suffix with `?`).
4. Align method parameters to column order; do not bind the scenario column unless annotated with `@Scenario`.
5. Verify all rows use the same assertion logic.
6. After building table with multiple rows, check for column consolidation opportunities (see Quality Checks).

### Writing New TableTest from Existing Code

1. **Understand phase**: Read the code, trace logic, identify variations
2. **Design phase**: Sketch table structure, discuss with pair
3. **Confirm**: Show mockup with 2-3 rows, get agreement
4. **Implement**: Create full table with all scenarios
5. **Run immediately**: Get fast feedback on structure and conversions
6. **Refine**: Improve names after tests pass — names emerge from understanding, so don't expect perfect column or scenario names on the first implementation. Replace implementation terms with domain language once the table is working.

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
- [ ] **Traceability columns**: intermediate expected values included only when the value is observable from the public API — never reimplemented from internal logic; if you need to reimplement a formula to populate the column, decompose into separate tables instead
- [ ] **@Description adds information**: if present, `@Description` provides context beyond what the table shows (fixed values, domain context, open questions) — not a restatement of columns or rows. Omit `@Description` if there is nothing to add.
- [ ] **@Description uses text block**: `@Description` uses `"""` text blocks, not string concatenation with `+`
- [ ] **Annotation order**: `@DisplayName` → `@Description` → `@TableTest` (no other order)
- [ ] **Exception column**: error/rejection tables have a `Throws?` or `Exception?` column, not hardcoded exception classes in the method body
- [ ] **Complete outputs**: all observable outputs of the same behavioral concern are in one table, not split across separate tests
- [ ] **Row coherence**: rows match the type of logic being tested (decision points for priority logic, input variations for parsing logic); out-of-place rows may signal mixed responsibilities in the code under test
- [ ] **Column consolidation**: if multiple columns are mutually exclusive (both identity and status vary together), consider consolidating into single column with composite values (e.g., `Primary OK`, `Secondary ERROR`)
- [ ] **Cross-table consistency**: if multiple TableTests exist in the same class, use consistent notation for similar concerns (timing, errors, special values); share parsers and helper methods
- [ ] **Test helpers organized**: helper classes placed at bottom of test file with clear names (`QueryCounter`, not `Helper`); only extract to separate file when reused across test classes
