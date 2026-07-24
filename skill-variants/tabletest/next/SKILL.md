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
    <version>1.2.2</version>
    <scope>test</scope>
</dependency>
```

Gradle:
```groovy
testImplementation "org.tabletest:tabletest-junit:1.2.2"
```

Imports:
```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Description;          // only when adding descriptive text to table
import org.tabletest.junit.Scenario;             // only when binding scenario column
import org.tabletest.junit.TypeConverter;        // only for custom converter methods
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

There is no specific naming pattern required, but `parse<TypeName>` (e.g., `parseLocalDate`, `parseDuration`) is conventional.

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

### State the Rule from the Table Alone

A passing test proves the code works. A published table has a second job: a reader who cannot see the method body must be able to *state the rule* from the table alone. Check this first — several rules below are particular ways of failing it.

**Every table needs at least one row that falsifies "nothing happened."** Values chosen only so the assertions can tell columns apart teach nothing — `1 kg` at `1 mg/kg` giving `1 mg` is consistent with a method that returns any of its inputs. `10 kg | 15 mg/kg | 150 mg` states what the operation *is*. The sharper form of the same check: name the plausible wrong implementation and confirm a row fails under it. If the rule rounds half up, the plausible wrong implementation is truncation, and every whole-number row passes under it — only a row like `2.5 → 3` excludes it. The rows a reader needs and the rows a mutation would kill are the same rows.

This applies to invariants too. "The dose is never rounded below the prescribed minimum", checked only by an assertion in the method body, does not read as spec — give it an expectation column and let a row show the minimum being held.

**Choose the observation that lets the rule be inferred, not merely one that passes.** Any assertion can prove the climate controller ventilated; only the right one lets a reader say what ventilating *does*.

```
// Weak — true of any implementation that touched the vent
Scenario         | Humidity | Setpoint | Vent opened?
Above setpoint   | 78       | 65       | true

// Strong — the reader can state the rule
Scenario         | Humidity | Setpoint | Vent position? | Humidity after 10 min?
Above setpoint   | 78       | 65       | 40%            | 71
At setpoint      | 65       | 65       | 0%             | 65
```

**Keep a constant expectation column when it is the rule's subject; drop it when it is incidental.** A table whose rows vary the roster shape and whose every row reads `Rostered? no` needs that column — it is what the rule claims. A table about which rest period applies, where every row happens to be a long-haul flight, does not need a constant `Long haul? yes` column; that belongs in `@Description`. This is the converse of Make Thresholds Visible, which is about constant *inputs*.

### Cover the Rule's Range Without Multiplying Rows

Coverage is a property of *which behaviours appear*, not of how many rows appear. Before writing rows, list the concern's obligations — the distinct behaviours the rule must demonstrate. Cover each obligation with at least one row, then stop. **A row that re-covers an obligation another row already covers is not extra coverage, it is a duplicate** — and it makes the rule harder to state, not easier.

**A quantifier in the title is a promise the rows must keep.** Titles are naturally universal — "whatever the donation type", "regardless of the donor's age", "with or without a recorded rest period". Once the title quantifies, that domain becomes an obligation: all three donation types must appear, both rest states must appear. A quantified title over three arbitrary rows is a claim the table does not support. Either cover the domain or narrow the title.

**Discharge a "regardless of" obligation with a value set, not with more rows.** Where the values are interchangeable — the same outcome for each — one row holding `{whole blood, plasma, platelets}` covers the domain *and* states the independence; three near-identical rows only imply it. Same rule as Use Value Sets for "Regardless Of" Relationships, seen from the coverage side.

**Boundary rows replace central rows; they do not accumulate on top of them.** A rule is understood only when the reader sees where it stops, so spend the row budget at the limits: the row exactly at the deferral cut-off, the row one tick past it, and the case where *no* rule applies. A tier row already carries its own boundaries inside its value set, so a tier table needs no separate "tier begins" and "tier holds" rows.

**Never cover a combination space by multiplying two concerns together.** Nine rows of three donation types × three deferral triggers is not thoroughness; it is two concerns that should have been two tables, and every row past the first few re-covers an obligation. Cross-multiply only where the *combination itself* has behaviour that neither concern shows alone — a precedence between two rules — and then the table holds only the rows that establish that precedence:

```
// The per-concern tables already cover the duty cap and the missing rest record.
// This table exists only for what happens when both apply at once.
Scenario                                  | Duty Hours | Daily Cap | Rest Recorded | Roster?
Cap exceeded outranks missing rest record | 14         | 13        |               | REJECTED
Missing rest record within the cap        | 11         | 13        |               | MANUAL_REVIEW
```

A combining table that re-runs the per-concern cases with one more column added is the commonest way to lose a decomposition you had already got right.

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

### Separate Rules from Arithmetic

Tables should specify the interesting decisions — classifications, eligibility rules, tier lookups, state transitions — not test that multiplication works.

**Good decomposition** — separate the rule from the calculation:

Table 1 — the rule (which bracket?):
```
Scenario          | Taxable Income | Filing Status | Bracket?   | Rate?
Bottom bracket    | 15000          | Single        | 10%        | 0.10
Middle bracket    | 55000          | Single        | 22%        | 0.22
Joint middle      | 55000          | Joint         | 12%        | 0.12
```

Table 2 — the arithmetic (what does the taxpayer owe?):
```
Scenario       | Income | Rate | Deduction | Tax Owed?
No deduction   | 50000  | 0.22 |           | 11000.00
With deduction | 50000  | 0.22 | 5000      | 9900.00
```

### Frame Stateful Features as Rules

When a feature involves state (queues, workflows, inventories), frame each row as a state transition rule:

```
Scenario              | Board Before             | Action              | Board After?                  | Message?
Assign first task     | [TODO: Deploy v2]        | assign Deploy v2    | [IN_PROGRESS: Deploy v2]      | Assigned
Complete task         | [IN_PROGRESS: Deploy v2] | complete Deploy v2  | [DONE: Deploy v2]             | Completed
Complete unknown task | [TODO: Deploy v2]        | complete Hotfix     | [TODO: Deploy v2]             | Not found
```

Each row is independent: given this state, when this action happens, expect this result. **Include before and after columns** — even when the prompt describes the operation procedurally.

### Decompose When You See These Signs

**If you cannot name a behaviour without using "and", it is two concerns** — split them. Each concern becomes its own `@TableTest` method.

Other signs that concerns are mixed:
- Some rows need columns that other rows leave blank throughout
- Scenario names require qualifiers like "...for eligibility" vs "...for pricing"
- The table has two groups of output columns that never both apply in the same row

**Missing concern:** An input to one rule is itself derived from raw data. The derivation has its own edge cases and needs boundary testing in a separate table. The rule table then takes the derived value as a direct input column, not the raw data. Two tables, not one.

### Match Table Structure to the Logic Being Tested

The type of logic under test determines what each row should represent:

- **Decision/priority logic**: Each row is a distinct decision point. Scenario names describe which rule takes precedence (e.g., "X wins over Y").
- **Parsing/validation logic**: Each row is a distinct input variation. Scenario names describe the input condition (e.g., "Empty input", "With special characters").
- **Transformation logic**: Each row is an input/output pair. Scenario names describe the transformation case.

If rows feel out of place — parsing variations in a decision table, or decision branches in a parsing table — this signals the code under test may be mixing responsibilities. Consider whether the method should be split before adding more test rows.

**Vary values along the axis the rule is about and hold everything else constant.** If the rule decides *which* actuator responds, give every row the same sensor readings and vary only the actuator; the reader then sees one situation in three forms rather than three unrelated cases. Rows that differ in several ways at once force the reader to work out which difference caused the different result.

### Name Expectation Columns Clearly

End expectation columns with `?` **suffix** to signal which columns are outputs being verified versus inputs being provided.

Examples: `Valid?`, `Formatted?`, `Result?`, `Throws?`, `Expected?`

**Prefer the rule's direct output.** Use `Fee?` over `Total?` — the fee is what the rule decides; verifying the total requires knowing the base amount. If you use a derived value like total, include the base as a column so readers can trace it. Input columns never have `?` suffixes — including yes/no flag columns that describe scenario state.

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

When several columns are mostly blank, collapse them into a single map column with a `@TypeConverter` that constructs the target object. This is especially appropriate when the sparse columns correspond to a single parameter of the method under test — typically an object with several optional fields where each row sets only one or two. A blank cell means "all defaults"; the converter supplies them:

```java
@TableTest("""
    Scenario         | Config                        | Timeout Used?
    All defaults     |                               | 3000
    Explicit timeout | [timeout: 5000]               | 5000
    Several options  | [method: POST, timeout: 1000] | 1000
    """)
void appliesConfiguredTimeout(RequestConfig config, int timeoutMs) {
    assertEquals(timeoutMs, gateway.timeoutFor(config));
}

@TypeConverter
public static RequestConfig parseRequestConfig(Map<String, String> config) {
    if (config == null) return RequestConfig.defaults();
    return new RequestConfig(
        config.getOrDefault("method", "GET"),
        Integer.parseInt(config.getOrDefault("timeout", "3000")),
        Integer.parseInt(config.getOrDefault("retry", "1")));
}
```

The map keeps the table compact, each row states only what differs from the defaults, and all construction and null-defaulting logic lives in the converter — never in the test method body. This applies however the object is normally built (constructor, setters, or builder), and even when a table exercises only one or two of the optional fields: if a method body news up a parameter object and mutates it, that construction belongs in a `@TypeConverter` behind a map column.

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

**Name the variation, not the data.** "At the cut-off", "Rest not recorded", "Second donation the same week", "Sensor unreachable" — read that column top to bottom on its own and you get the table's coverage argument. A scenario column reading `Yes / No` beside an input column reading `Yes / No` satisfies the letter of the convention and adds nothing.

**A name that leads with the verdict is an outcome name in disguise.** "Deferred: donated last week" and "Accepted: interval elapsed" put the result first and the condition second, but the expectation column already carries the verdict — the prefix buys nothing and hides the coverage argument. The temptation peaks exactly when a table covers a domain, because the labels start to feel like an index of outcomes. Name the condition that puts the row in the domain.

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

### Make Thresholds Visible

When a rule depends on a threshold or limit, include it as a column — even when the value is constant across every row:

```
Scenario            | Customer Age | Max Age (Policy) | Eligible?
Standard customer   | 30           | 75               | yes
At the limit        | 75           | 75               | yes
Just over the limit | 76           | 75               | no
```

Without the threshold column, the number 75 is buried in the code — the reader cannot tell from the table where the boundary is, or whether the rule is strictly greater than. Boundary rows (at the limit, just over) also become natural to add once the threshold is visible.

A constant column often signals configuration. Ask: "Under what circumstances would this value differ?" The answer may reveal a second axis (e.g., the limit varies by category) that belongs as new rows or a separate table.

**The rule generalises past thresholds: if the rule quantifies over it, it must be a column** — even when it normally lives in the method signature, an annotation attribute, or configuration. The deferral window, the active formulary edition, the applicable regulation set, the declared target type: anything the rule ranges over belongs in a column where the reader can see it, not fixed in the method body. If varying it in a row is impossible because it is configured out of band, route the table through the entry point that accepts it as an argument — a condition you cannot vary in a row is not a rule the table can hold.

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

This makes the tier structure a first-class concept — each row IS a tier. Cover every tier as one row (value set holding at least both boundary counts), not a sample of tiers or separate "tier begins"/"tier holds" rows.

**Value sets work on two axes — check both.** Within a row, group input values that produce the same outcome (`{30, 45, 59}` → one tier). Across rows, collapse duplicates: when two input kinds follow identical rules everywhere (two categories treated alike by every rule), one row with `{A, B}` replaces two identical rows. It is easy to apply one axis and miss the other.

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

**Blank cells for irrelevant inputs**: When an input is not relevant to a scenario, use a blank cell — not `0` or a default value. Use boxed types (`Integer`, `Long`) instead of primitives so blank cells convert to `null`. Then handle null-to-default conversion in a `@TypeConverter` or helper, not in the test method body.

**Blank vs value set**: Blank cells mean the input is genuinely absent (null). When the input exists but is irrelevant to the outcome, use a value set instead: `{UK, Ireland, Other}` for destination means "destination exists but doesn't affect this result". Don't use blanks for "doesn't matter" — blanks mean null.

**Note**: These are syntax examples, not test design patterns. Null/empty/blank variants of an input should typically be additional rows in the test that covers the feature, not in a separate test method.

---

## Workflow

**Budget your reasoning.** If concerns are already listed in the prompt, use them directly — don't re-derive what's already stated. If you find yourself re-analyzing the same concern, stop and write code. Working code you can revise beats perfect analysis that times out.

**Write incrementally.** For multi-concern features, write one `@TableTest` method at a time using the Write tool. Don't attempt to generate the entire test class in a single response — each method written is a checkpoint that can't be lost to a timeout.

### Converting Existing Tests

1. Identify tests with identical structure but different data.
2. Extract the varying parts as columns (inputs and expected values). If the originals build an object with several optional fields — via constructor arguments, setters, or a builder — collapse those into one map column with a `@TypeConverter` (see Collapse Sparse Columns into a Map) instead of constructing the object in the method body.
3. Create table with scenario column first, inputs next, expectations last (suffix with `?`).
4. Align method parameters to column order; do not bind the scenario column unless annotated with `@Scenario`.
5. Verify all rows use the same assertion logic.
6. After building table with multiple rows, check for column consolidation opportunities (see Quality Checks).
7. Remove the original `@Test` methods the table now covers — run the tests before and after removal to confirm coverage is preserved.
8. When converting from another framework (Spock, Kotest, TestNG, JUnit 4), finish the migration: replace the old framework's assertion/matcher style (`shouldBe`, `expect:`, TestNG asserts) with the project's JUnit-compatible style, remove its imports, and remove its dependencies from the build file. Leftover matcher calls or a leftover build dependency both mean the conversion is incomplete.

### Writing New TableTest from a Feature Description

When there is no existing code (empty `src/main/java`), write the tests first — the table design drives the API shape. After the tests are written, add stub implementation code so they compile.

1. **Read the feature description** and identify the rules/concerns
2. **Commit to one `@TableTest` method per concern before writing any table.** List the concerns you found, then name the method each will become. A feature stated as a list of policy rules is not one concern because it resolves to one verdict — eligibility conditions, tier or category boundaries, and status handling are separate concerns even when a single method combines them into the final answer. Add a combining table for that final verdict when one is needed. Decide this now: once a single table is written, the pull is to keep extending it with rows rather than split it. Decompose *concerns into methods* — never split one domain value (a coupon's type/amount/target, an address's fields) across sparse columns; keep it as one column with a `@TypeConverter`.
   - **Where a per-concern constant goes depends on what it does.** A threshold or limit the row's value is tested against stays visible — as its own column beside the value it gates (see Make Thresholds Visible) — because seeing it is how the reader checks the rule. A value the concern merely holds irrelevant (a representative age while testing the credit rule, a base you avoid entirely by expecting the rule's direct output) belongs in `@Description` or the method body, not a column.
3. **Write the test class** with `@TableTest` methods following the design principles in this skill
4. **Add stub implementation** — create the class and methods referenced by the tests with signatures only (return defaults, throw `UnsupportedOperationException`, etc.). Do not implement the logic unless specifically instructed. The user may want to iterate on the test design before committing to an implementation.

**Let tables drive the API decomposition.** Each concern's table should call a function whose parameters are exactly the table's input columns. If a test method needs a loop or helper to fabricate raw data so a derived input reaches a target value (e.g. generating n records so that a count equals n), the table is targeting too high in the stack — stub a narrower function that takes the derived value directly, and cover the raw-data derivation in its own table. Cheap rows are the sign of a well-placed table; when adding a row feels expensive, the API needs another seam.

**Ambiguity policy — deliver, don't ask.** Feature descriptions rarely answer every question. Choose the most reasonable interpretation, record it — along with any open question — in the affected table's `@Description`, and deliver complete tests. Never end the task with clarifying questions in place of tests: documented assumptions in delivered tests are how you raise them. This holds even if you have just read the spec-by-example skill — its clarify-first workshop style is for requirements discussions, not for a request to write tests.

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
- [ ] **Multiple rows**: table has 2+ rows; use `@Test` only for a genuinely standalone single case — a lone error, null, or empty-input case related to an existing table belongs in that table as a row (with a `Throws?` column if it throws), not in a separate `@Test`
- [ ] **Black-box design**: columns represent observable inputs and outputs, not internal flags or implementation details
- [ ] **Clear communication**: scenario names describe conditions (not outcomes), column names use domain language (not parameter names)
- [ ] **Uniform assertions**: all rows use the same assertion logic; split into separate TableTests if logic differs per row
- [ ] **Straightforward method**: no `if`/`switch`/ternary — not even null-guards or defaulting, which belong in a `@TypeConverter` or helper; the method only arranges, acts, and asserts
- [ ] **Parameter alignment**: parameters match data columns left-to-right (excluding scenario column)
- [ ] **Parameter conversion**: custom type converter methods (annotated `@TypeConverter`) or JUnit converters handle type conversion, keeping the test method free of parsing code
- [ ] **Valid syntax**: values requiring quotes are quoted, collections use correct bracket syntax, empty collections are explicit (`[]`, `{}`, `[:]`)
- [ ] **Expectation columns present**: at least one column uses `?` suffix (not prefix)
- [ ] **Concrete values**: expectation values are traceable to input column values where applicable
- [ ] **Thresholds visible**: rules that depend on a threshold or limit show it as a column, with boundary rows at and just past the threshold; for date cutoffs, prefer descriptive relative values (`before cutoff`, `on cutoff`) via a `@TypeConverter` — or include the cutoff date as a column if literal dates are used
- [ ] **Rule statable from the table**: cover the method body with your hand — can a reader state the rule from the table alone? The operation must be visible, not only its endpoints
- [ ] **A falsifying row**: name the plausible wrong implementation (truncation instead of rounding, returning an input unchanged) and confirm at least one row fails under it
- [ ] **Obligations covered once**: every distinct behaviour the rule must demonstrate has a row — title quantifiers enumerated (via a value set where the values are interchangeable), boundary rows at and just past each limit, the no-rule-applies case present — and no row re-covers an obligation another row already covers
- [ ] **Concerns not cross-multiplied**: no table multiplies two concerns' values together; a combining table holds only the rows that establish precedence between rules already covered elsewhere
- [ ] **Correct expected values**: arithmetic in expected columns verified independently; every row's output matches the stated rules
- [ ] **Value set semantics**: value sets only used where every value produces the same result; not used as shorthand for "test multiple values"
- [ ] **Irrelevance and tiers use value sets**: inputs that don't affect a row's outcome appear as value sets (not a fixed placeholder value mentioned in `@Description`); when a range of input values maps to one tier, the row groups representative values (including both boundaries) into a value set
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
- [ ] **Old framework removed** (conversions only): no imports, matcher/assertion calls, or build-file dependencies from the framework being replaced

---

## Advanced References

This skill file is complete for standard tables — do not read references speculatively. Read a reference only when its condition applies to your task.

| Reference                                | When to use                                                                 |
|------------------------------------------|-----------------------------------------------------------------------------|
| `references/type-converters.md`          | Converting wrapper types (`Optional`, `Result`/`Either`); converter methods with defaults; converter not being discovered (search order) |
| `references/column-design.md`            | Torn between maps and separate columns; encoding composite values; column naming evolution |
| `references/common-patterns.md`          | Consolidating identity+status, relative positions, timing thresholds, test helpers, sequence recording |
| `references/table-design-advanced.md`    | Suspected orthogonal concerns (a column that could be `{true, false}` in every row); scenario names unclear; column sets diverging |
| `references/large-tables.md`             | Table needs comments, grouping, or external table files                     |
| `references/async-and-performance.md`    | Testing async/non-blocking behavior or tracking execution order             |
| `references/provided-parameters.md`      | Using `@TempDir` or other JUnit-injected parameters                         |
| `references/testing-reveals-bugs.md`     | Test design feels wrong; suspecting implementation bug                      |
| `references/pair-programming.md`         | Pairing with a colleague; need structured collaborative cadence             |
