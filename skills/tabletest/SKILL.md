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
void addsTwoNumbers(int a, int b, int sum) {
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
void quotesSpecialCharacters(String value, String description) { ... }
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
void sumsListElements(List<Integer> numbers, int sum) { ... }

// Set (empty set uses {})
@TableTest("""
    Values       | Size?
    {}           | 0
    {1, 2, 3}    | 3
    {1, 1, 2, 2} | 2
    """)
void countsDistinctValues(Set<Integer> values, int size) { ... }

// Map (empty map uses [:])
@TableTest("""
    Scores               | Highest?
    [:]                  | 0
    [Alice: 95, Bob: 87] | 95
    [x: 1, y: 2, z: 3]   | 3
    """)
void findsHighestScore(Map<String, Integer> scores, int highest) { ... }
```

**Common mistake**: Using `[]` for a `Set<>` parameter. Lists use `[]`; sets use `{}`. If a parameter is typed `Set<T>` but the table uses `[]`, JUnit will report a conversion failure. Double-check the brackets match the parameter type.

**Note**: Empty collections are explicit: `[]` for empty list, `{}` for empty set, `[:]` for empty map.

**A collection value cannot hold a null element.** The blank-cell-means-null rule stops at the cell boundary — `[a, , c]`, `[a, b, ]` and `[, a, b]` are parse errors, not lists containing a null. Blank the whole cell to get a null collection.

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

**Any domain object built from a table value belongs in a converter — whatever the construction
idiom.** A constructor, a builder, a static factory, a chain of `with…` calls: they are all
construction, and construction in the test body puts the arrangement between the reader and the rule.
The parameter is the **domain type**, and getting from cell to object is the converter's job.

```java
// WRONG — the body assembles the object the row is about
void appliesRestCredit(String rosterSpec, int restHours) {
    Roster roster = Roster.forCrew(2).withDutyPeriods(rosterSpec).withBase("LHR");
    ...
}

// RIGHT — the parameter is the domain type; assembly is behind @TypeConverter
void appliesRestCredit(Roster roster, int restHours) { ... }
```

This is not only about objects with several optional fields — that case gets its own column shape
under **Collapse Sparse Columns into a Map**. It applies equally to a composed object whose parts
come from one cell, and to one assembled from a fixture plus a single varying value.

### Prefer Built-in Conversion First

JUnit can convert strings to `Class<?>` when the value is a fully-qualified class name. Write `java.lang.RuntimeException` in the table instead of `RuntimeException` plus a custom `@TypeConverter`. Only write a converter method when built-in conversion does not cover the type.

### Writing Custom Converter Methods

A converter turns the cell's text into the domain type, so the table reads in domain terms and the
method body stays arrange-act-assert.

```java
@TableTest("""
    Date     | Days Until?
    today    | 0
    tomorrow | 1
    """)
void countsDaysUntil(LocalDate date, int expected) {
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

**Java**: `public static`, annotated `@TypeConverter`, in a **public** test class or one listed in
`@TypeConverterSources`. A package-private test class is the usual reason a converter is never found.

**Kotlin**: a package-level function is preferred — same body, declared outside the test class:

```kotlin
@TypeConverter
fun parseLocalDate(input: String): LocalDate = when (input) {
    "today" -> LocalDate.now()
    "tomorrow" -> LocalDate.now().plusDays(1)
    else -> LocalDate.parse(input)
}
```

The alternative is a companion object member marked `@JvmStatic`:

```kotlin
class DateTest {
    companion object {
        @JvmStatic @TypeConverter
        fun parseLocalDate(input: String): LocalDate = ...
    }
}
```

`@Nested` inner classes in Kotlin cannot have companion objects, so package-level is the only option
there.

### Sharing Converters with @TypeConverterSources

For shared converter methods across multiple test classes:

```java
@TypeConverterSources(DateConverters.class)
class DateTest {
    @TableTest("""
        ...
        """)
    void usesSharedConverters(LocalDate date, Duration duration) { ... }
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

**A blank cell bypasses your `@TypeConverter` — the converter never runs.** Blank becomes `null`
before any conversion is attempted, so the parameter receives `null` directly and no converter,
built-in or custom, is consulted. A null guard inside a converter is dead code.

When a row means "nothing set" and you still want the converter to supply defaults, write the
**empty value** rather than leaving the cell blank: `[:]` for a map, `[]` for a list, `{}` for a
set, `''` for a string. Those all reach the converter. Reserve the blank cell for rows where `null`
itself is the value under test.

### Domain-Specific Formatting

Converter methods enable readable domain conventions in tables:

```java
@TableTest("""
    Scenario            | Budget | Recorded | Within Budget?
    Comfortably inside  | <50    | 20       | true
    At the cap          | <50    | 50       | false
    """)
void checksResponseBudget(Long budgetMs, long recordedMs, boolean withinBudget) { ... }

@TypeConverter
public static Long parseResponseTime(String value) {
    if (value.startsWith("<")) return Long.valueOf(value.substring(1));
    return Long.parseLong(value);
}
```

**Expectation columns go through custom converters too.** A converter is chosen by parameter type,
not by the column's role. Register a `Yes/No → Boolean` converter and an expectation column written
`true` arrives as **`false`**, because `true` is not `Yes`. A converter must accept every spelling
that appears anywhere in the class, expectation columns included.

Other examples: `5m`/`30s` → milliseconds, `$100` → numeric, `50%` → 0.5, `10KB` → bytes.

**Calendar dates**: Prefer descriptive values like `before cutoff`, `on cutoff`, `after cutoff` with a `@TypeConverter` over raw ISO dates. The reader doesn't need to mentally compare `2025-02-28` against `2025-03-01`. If raw dates are used, include the policy/cutoff date as a separate column so the reader can verify the comparison.

---

## Table Design

### Design Black-Box Tables

Model observable inputs and outputs. Avoid internal flags or setup-only columns unless they are part of the public contract.

```java
@TableTest("""
    Scenario                    | Build Dir | JUnit Property | Configured Dir | Resolved Dir?
    All three set               | build     | report/junit   | tabletest      | tabletest
    Configured dir absent       | target    | report/junit   |                | report/junit
    Neither property nor config | build     |                |                | build/junit-jupiter
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
    Both sources set          | my-config | report/junit | my-config            | CONFIGURED     | [my-config]
    Input dir absent          |           | report/junit | report/junit         | JUNIT_PROPERTY | [report/junit, build/junit-jupiter]
    Neither source set        |           |              | build/junit-jupiter  | FALLBACK       | [build/junit-jupiter]
    """)
void resolvesWithPriority(String inputDir, String junitDir,
                          String resolvedPath, ResolutionSource source, List<String> searchLocations) { ... }
```

Splitting forces the reader to cross-reference multiple tables to understand one behavior. If the outputs all come from the same operation and concern, they belong together.

Separate tests are appropriate when testing a **different concern** of the same operation (e.g., path normalization vs. priority resolution) or a different method entirely. Even when testing a single API method, decompose concerns into separate `@TableTest` methods using default values for irrelevant inputs. Separate tables reduce rows by avoiding unnecessary permutations — and the table count guides implementation: five concern tables suggest five functions.

### Separate Rules from Arithmetic

Tables should specify the interesting decisions — classifications, eligibility rules, tier lookups, state transitions — not test that multiplication works.

**The symptom is an expectation cell you cannot predict in one step.** If reading a row means
classifying first and then computing, the table has fused two rules and states neither. Give the
classification its own table, whose expectation columns *are* the classification:

Table 1 — the classification (how do these duty hours divide?):
```
Scenario           | Duty Hours | Normal Hours? | Extended Hours?
Below the limit    | 8          | 8             | 0
At the limit       | 13         | 13            | 0
Past the limit     | 14         | 13            | 1
```

Table 2 — the arithmetic (extended hours earn rest credit at double rate):
```
Scenario            | Normal Hours | Extended Hours | Rest Credit?
Ordinary duty       | 13           | 0              | 13.0
Duty ran long       | 13           | 1              | 15.0
```

Each table now states one rule, and every cell is predictable from its row. This usually needs a
narrower function to call — see **Let tables drive the API decomposition**; a table that can only
reach the fused result means the seam is missing, not that the table must fuse.

**Every expectation column must be exercised by the rows the table varies.** *Include All Outputs of
a Concern* asks for every output of the same rule, and a response carrying two fields usually keeps
both columns. The question is not the shape of the response — it is whether each column moves for its
own reason along the axis this table varies.

**A column that is constant down every row, or that changes only as a side effect of another column,
is not being tested.** Two repairs, and which one is right depends on the rule:

- Give it rows that vary it, when the column does belong to this table's axis and the rows were
  missing.
- Move it to the table whose axis varies it, and drop the column here rather than keeping it "for
  completeness".

A fit-to-fly table varying rest hours that also asserts a required-rest figure identical in every row
is displaying the second rule, not testing it. A table where the decision and the rest requirement
both change as rest hours change is one rule with two outputs — keep both columns.

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

**Do not over-split either.** Several tables that fix the same setup and each vary one sub-rule, all
reporting the same output column, are one concern scattered across methods — one table per
adjustment, per option, per flag. That shape is the symptom. The cause is a family you did not name.

**If you can name what several tables have in common in one term, they are one concern — that term is
the table, and its members are a column.** This is the mirror of the "and" test above. Renal
impairment, low body weight and an interacting drug all *adjust the standard dose*: three rules, one
family, one table with an adjustment column. Naming the members instead —
`reducesForRenalImpairment`, `adjustsForBodyWeight`, `reducesForInteractingDrug` — commits to the
split before a single row exists, which is why this is decided at the method name.

**Members of a family compute differently, and that is not a reason to split.** One adjustment is a
flat reduction, another a percentage, another a weight-based recalculation. The differing computation
is what the rows show; it is not what makes them separate tables.

```
Scenario                   | Adjustments                        | Daily Dose?
No adjustment              | [:]                                | 500
Renal impairment           | [renal: severe]                    | 250
Low body weight            | [weightKg: 20]                     | 200
Interacting drug           | [interaction: true]                | 400
Renal and interacting drug | [renal: severe, interaction: true] | 200
```

**Collapse on a family, never on a bag.** A family is a domain category, not "everything that affects
the answer". The check: the family name works as a column header with the members as its values —
`Adjustment: renal | weight | interaction` does, `Dose factor: …` does not. Where no such name exists
the tables are genuinely distinct and belong apart, and so they do where collapsing would
cross-multiply or leave rows whose purpose is no longer legible.

### Give Each Obligation Exactly One Row

The right number of rows is a covering problem. List the concern's **obligations** — the distinct
behaviours the rule must demonstrate — then write the smallest set of rows that covers all of them.
Both errors are real, and they are not symmetrical in how they read: a missing obligation lets a
wrong implementation pass, while a repeated one costs the reader time and suggests a distinction
that is not there.

**The test for a redundant row: if two rows share an expectation, the difference between them must
be the thing the rule is about.** If it is not, they are one row — and a value set is how you say so.

Three shapes account for nearly every redundant row:

- **Further past the same boundary.** A pair that *straddles* a boundary earns both its rows: the
  outcomes differ, and that is the rule. A second row on the same side does not.

  ```
  Scenario                  | Duty Hours | Extra Rest Required?
  At the duty limit         | 13         | false
  Just past the duty limit  | 13.5       | true
  Well past the duty limit  | 20         | true      ← redundant: 13.5 already proved it
  ```

  Keep the straddling pair; drop the row further out. This holds for rejections too — one row just
  past a limit rejects, and a row further past it rejects for no new reason.
- **A larger n in the same direction.** If two incompatible items in a batch force it into separate
  collection streams, three incompatible items force it for the same reason. One obligation, one row.
- **A value the rule is indifferent to.** Two rows differing only in an input the rule ignores are
  one row with a value set: `{whole blood, plasma}` where the deferral interval is the same either
  way, or `{manual, scheduled}` where what triggered a climate adjustment does not change it.

A second row on the same side of a boundary earns its place in one case: when the point *is* that two
inputs collapse to one behaviour. Then say so — a value set says it in one row, and if you keep two
rows the scenario names have to carry why.

This is the same rule that makes a tier ladder one row per tier (see Value Sets for Tier Grouping):
a value set spanning the tier's range carries its own boundaries, so a separate "tier begins" row
discharges nothing the "tier holds" row has not.

**One value can carry two obligations, in two different tables.** A value that is a boundary for one
rule is often the subject of another. A zero duty period is both the accepted end of "duty hours
cannot be negative" *and* the input that should produce no rest requirement whatever the crew size —
two rules, two questions, two rows in two tables. Showing the value once, in whichever table you
reached first, feels like coverage and is not: the accepted-boundary row says nothing about what the
other rule then computes. **Count obligations per rule, never per value.**

### A Combining Table Needs Its Own Rule

Once every rule has a table, the pull is to add one more that runs the whole feature end to end. It
re-proves what the single-rule tables already established, and it reads as redundant however clean
those tables are.

**A table that combines concerns earns its place only where the combination behaves in a way neither
concern shows alone** — a precedence, an ordering, an interaction whose result neither parent table
produces — and then it carries only the rows that show it. A table proving that a weight-based dose
is computed *before* the daily maximum caps it is a real table: the question is which rule applies
first, and its expected values appear in no other table. A table whose rows re-run each dose band
through the public entry point is not.

Two symptoms:

- **The description gives it away.** If the `@Description` you would write is "end-to-end scenarios
  combining the rules from the tables above", the table has no rule of its own. Delete it.
- **Wiring is not a rule.** Reaching a rule through the public API rather than the unit under test
  does not make it a new rule. If the wiring genuinely needs showing, that is one row, not a second
  pass over the ladder.

**Salvage its rows before you delete it — and then delete it.** One or two rows of an end-to-end
table often reach a case no single-rule table does: a zero concentration against a nonzero body
weight, an empty roster against a fully configured schedule. Deleting the table takes those with it
and nothing reports the loss, so list the obligations only its rows discharge and move each into the
table that owns its rule.

**This is a salvage step, not a reprieve — no outcome of it keeps the table.** A row worth keeping is
worth keeping *somewhere else*. Nor does shrinking the table save it: a single `@Test` that runs the
whole feature to re-prove one already-proven total is the same combining table with fewer rows.

### Match Table Structure to the Logic Being Tested

The type of logic under test determines what each row should represent:

- **Decision/priority logic**: Each row is a distinct decision point. Name which inputs are present, not which one won — a priority table almost always publishes the winner as an expectation column, so "Configured wins" beside `Source?` `CONFIGURED` restates its own answer. "Both sources set", "Input dir absent" say which case the row is.
- **Parsing/validation logic**: Each row is a distinct input variation. Scenario names describe the input condition (e.g., "Empty input", "With special characters").
- **Transformation logic**: Each row is an input/output pair. Scenario names describe the transformation case.

If rows feel out of place — parsing variations in a decision table, or decision branches in a parsing table — this signals the code under test may be mixing responsibilities. Consider whether the method should be split before adding more test rows.

### Name Expectation Columns Clearly

End expectation columns with `?` **suffix** to signal which columns are outputs being verified versus inputs being provided.

Examples: `Valid?`, `Formatted?`, `Result?`, `Throws?`, `Expected?`

**Prefer the rule's direct output.** Use `Fee?` over `Total?` — the fee is what the rule decides; verifying the total requires knowing the base amount. If you use a derived value like total, include the base as a column so readers can trace it. Input columns never have `?` suffixes — including yes/no flag columns that describe scenario state.

**A compound result stays a collection.** When the value under test is several items — or items grouped under a key — the expectation column is a native list, set, or map, nesting where needed: `[paper, card]`, `{glass, metal}`, `[recycling: [paper, card], landfill: [foil]]`. Compare it against the collection the system returns. Do not flatten it into a quoted string like `"recycling:[paper,card]"` assembled by a stringifying helper: that tests your formatter rather than the rule, hides the structure from the reader, and puts formatting code back in the method body. Use a set where order is not part of the rule, and a list with a canonical sort where it is.

**Common mistake** — `?` as prefix instead of suffix:
```
?Source        ← WRONG
Source?        ← CORRECT
```

### Assume the Table Is Published

Write every table as if a reader will meet it in a published report, never having seen the test body.
Only three surfaces reach that reader, and they divide the work:

| Element        | Carries                                                                     |
|----------------|-----------------------------------------------------------------------------|
| `@DisplayName` | the rule, as an action the code performs                                    |
| `@Description` | the apparatus that cannot be a column — what is held constant, which fixtures or converters are in play, where the data came from |
| the table      | the variations the rule ranges over                                         |

**Whatever the table holds constant is silently promoted into the rule.** Readers generalise from what
varies, so a value that never varies is read as part of the rule: a duty-limit table whose every row
assumes a two-pilot crew states, to its reader, a rule about two-pilot crews.

So a constant the outcome depends on is either a column or declared in the title or description. It is
**not** declared when it sits in the test method body, in a field, in a `@TypeConverter`, or in a `//`
comment — a comment reaches no published surface at all. The converter is the easiest hiding place
because it looks like plumbing: a converter that builds every history entry with the same zone has
pinned zone for the whole table, and no column says so.

Declaring a held constant in `@Description` is not redundancy. The other description rules forbid
restating what the rows already show; a held constant is exactly what the rows cannot show.

**What the assertion tolerates is part of the rule too.** A comparison that sorts either side before
comparing, accepts a subset, matches "contains" rather than equals, or normalises case or whitespace
is enforcing a rule: it changes which behaviours the test would accept. None of it reaches the
reader. Ordering is the usual one, and a helper is where it hides — written once, then invisible at
every call site, so a reader cannot tell whether order is part of the behaviour or an artefact of the
comparison.

Two repairs, and the second is better where it fits:

- **Name it** — one sentence in the `@Description` ("bins are compared without regard to order"), or
  a column that makes it evident.
- **Remove the need for it** — a `Set` expectation column says order does not matter *in the table
  itself*, which beats saying so in prose; a list with a canonical sort says it does. See **A
  compound result stays a collection**.

Numeric hygiene is not a criterion: a conventional epsilon on a decimal column, or
`BigDecimal.compareTo`, is exempt. Neither is constructing the objects the columns name — that is the
converter's job.

**An input the rule is indifferent to must still be shown varying** — as a value set, never as a
fixed value pinned in a converter, a field or the method body. "Indifferent" is a claim about
behaviour, and pinning the value makes that claim unfalsifiable, which is the opposite of what it
needs. Mechanics under **Use Value Sets for "Regardless Of" Relationships**.

### Write Titles That Form an Index

`@DisplayName` — or the method name when there is none — is the line a reader scans in the report
index. Judge titles as a set, never one at a time: a title that reads well on its own page can still
be an unscannable entry in the list.

**Open each title with something that distinguishes it, and keep one grammatical shape across the
family.** When every title starts with the same word, the index becomes a column of `should…` and the
distinguishing part arrives last, where scanning cannot reach it. Three titles sharing an
uninformative opener is enough to make the list unscannable.

**Write an action the code performs, not a label for a topic.** This is the half that is easy to
miss: a noun phrase can front the varying subject and still say nothing about what the code *does*
with it. `Deferral interval by donation type` names a topic; `Sets the deferral interval from the
donation type` names behaviour. The label form is the more tempting mistake, because it looks tidy
in a list.

| Scans as an index                                    | Does not                           |
|------------------------------------------------------|------------------------------------|
| `Sets the deferral interval from donation type`      | `shouldApplyDeferralInterval`      |
| `Rejects a reading below the haemoglobin minimum`    | `Haemoglobin minimum by donor sex`  |
| `Defers a donor returning from a listed destination` | `shouldDeferForTravelDestination`  |

Three distinct verbs, each carrying information, and the subject arrives immediately after. One
outlier does not break a family — a negative or invariant claim (`Donation type does not affect the
haemoglobin minimum`) often reads best subject-first.

**A title states what your system does, not an external fact it depends on.** Strike the system under
test from the sentence: if it still reads as true, the title is restating a regulation, a format or a
domain fact instead of naming behaviour. This is why the action form is safer than the topic form —
`Donation type sets the deferral interval` survives the strike and reads as policy, while `Sets the
deferral interval from the donation type` does not stand alone without the system that does it.

**This action voice is the title's alone.** Scenario names stay condition phrases naming the row's
variation — see **Name Scenarios Descriptively**. A title says what the rule does; a scenario name
says which case this row is. Writing rows as little sentences is how outcome-echoing names get in.

### Use @Description When It Adds Information

Add `@Description` when there is context the table alone cannot convey. Omit it when the table already says everything — a vacuous description adds noise.

Good reasons to add `@Description`:
- **Fixed values** shared by all rows that are not columns (e.g., "order value is always 100")
- **Domain context** — where/when the rule applies, who is affected, which market
- **Open questions** — decisions not yet resolved
- **Relationship between tables** — how this table connects to others in the class

Do not restate what the table already shows. If the description merely summarises the column names or row outcomes, delete it. Don't include irrelevant fixed values — "Fixed for all rows: donor name = 'A. Nolan'" is noise unless the name affects behaviour. Values hardcoded in the method body that affect outcomes should be columns.

**A description must not publish the algorithm.** Restating the internal formula — "the dose index is
body weight divided by ten plus four per severity grade, capped once it passes 75" — turns a
black-box table into a white-box one and pins the test to an implementation the rows never observe.
A **threshold the rule is about**
is different: name it, or better, make it a column (see **Make Thresholds Visible**). The line is
whether a reader could recompute every expectation cell from the description alone. If they could,
the description is doing the code's job.

```java
// GOOD — adds context not visible in the table
@Description("""
    Applies to the paediatric formulary only. Dose is per administration,
    not per day. Open: should a missed dose be added to the next one once
    the interval has already elapsed?
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
@DisplayName("Charges parking by duration band")
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
void calculatesParkingFee(...) { ... }
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
void rejectsInvalidInput(String input, Class<? extends Throwable> throws_) {
    assertThrows(throws_, () -> parse(input));
}
```

**When some rows throw and some do not, keep one assertion.** A boundary straddling a validation
limit always produces this shape — the accepted value beside the first rejected one. Leave `Throws?`
blank where nothing is thrown, and compare the thrown type rather than branching:

```java
@TableTest("""
    Scenario                | Dose (mg) | Throws?
    At the minimum dose     | 0         |
    Just below the minimum  | -0.01     | IllegalArgumentException
    """)
void rejectsDoseBelowMinimum(BigDecimal dose, Class<? extends Throwable> throws_) {
    assertEquals(throws_, thrownBy(() -> validateDose(dose)));
}

// with the other helpers, at the bottom of the class
private static Class<? extends Throwable> thrownBy(Executable action) {
    try {
        action.execute();
        return null;
    } catch (Throwable thrown) {
        return thrown.getClass();
    }
}
```

Branching on the row to pick between `assertThrows` and `assertDoesNotThrow` is what this avoids: it
puts the rule back in the method body, where the table cannot show it.

### Collapse Sparse Columns into a Map

**Decide this from the signature, before drafting columns.** When one parameter of the method under
test is an object with several optional fields, it is *one* map column with a `@TypeConverter` that
constructs it — never one column per field. Deciding after the table is drafted is too late: by
then every field has a column, most rows carry a blank or a `false` in it, and the sea of near-empty
cells reads as deliberate.

The "nothing set" row is **`[:]`, not a blank cell.** A blank bypasses the converter and hands the
method `null` (see Handling Null Values); `[:]` calls the converter with an empty map, which returns
the defaults.

```java
@TableTest("""
    Scenario         | Config                        | Timeout Used?
    All defaults     | [:]                           | 3000
    Explicit timeout | [timeout: 5000]               | 5000
    Several options  | [method: POST, timeout: 1000] | 1000
    """)
void appliesConfiguredTimeout(RequestConfig config, int timeoutMs) {
    assertEquals(timeoutMs, gateway.timeoutFor(config));
}

@TypeConverter
public static RequestConfig parseRequestConfig(Map<String, String> config) {
    return new RequestConfig(
        config.getOrDefault("method", "GET"),
        Integer.parseInt(config.getOrDefault("timeout", "3000")),
        Integer.parseInt(config.getOrDefault("retry", "1")));
}
```

**The converter returns the domain object, not the map.** Declaring the parameter `Map<String,
String>` and building the object with a private helper in the test class leaves construction in the
test and defeats the point — the converter *is* the construction.

**A map column is a column decision, not a table decision.** Choosing a map for one parameter says
nothing about where the concern boundary lies, and it must not become the boundary. If another input
drives the same rule to the same output column, it is another column in the same table — not a table
of its own. A vent position driven by the measured humidity and a vent position driven by the
configured climate overrides are one concern with one output: one table, a humidity column beside the
overrides map column. Splitting them because one input arrives as a map and the other does not is the
over-split described under Decompose When You See These Signs.

The map keeps the table compact, each row states only what differs from the defaults, and all construction and defaulting logic lives in the converter — never in the test method body. This applies however the object is normally built (constructor, setters, or builder), and even when a table exercises only one or two of the optional fields: if a method body news up a parameter object and mutates it, that construction belongs in a `@TypeConverter` behind a map column.

### Include Traceability Columns

When a table tests a pipeline (input → intermediate result → final result), include the intermediate result as an expectation column. This lets readers trace the logic step by step:

```java
@TableTest("""
    Scenario                     | Body Weight (kg) | Renal Function | Dose Band? | Daily Dose (mg)?
    Adult, normal function       | 70               | Normal         | Standard   | 500
    Adult, impaired function     | 70               | Impaired       | Reduced    | 250
    Low weight, normal function  | 40               | Normal         | Low        | 300
    """)
```

The `Dose Band?` column is not strictly necessary (the test could verify only `Daily Dose (mg)?`), but it lets the reader trace: weight + renal function → dose band → daily dose. When a row fails, the intermediate column shows where in the pipeline the error occurred.

**Guard:** Only use traceability columns for values the system under test exposes or that represent observable domain concepts. If you would need to reimplement an internal calculation in the test body to populate the column, it doesn't belong — the intermediate likely points to a separate concern that needs its own `@TableTest` method. Decompose into multiple tables instead; the intermediate becomes an output in one table and an input in the next.

### Name Scenarios Descriptively

Describe the condition being tested, not the expected outcome. Good scenario names answer "under what circumstances?" rather than "what happens?".

| Good                         | Bad             |
|------------------------------|-----------------|
| `Negative input`             | `Returns error` |
| `Empty list`                 | `Sum is zero`   |
| `User without licence`       | `Cannot rent`   |
| `Divisible by 4 but not 100` | `Is leap year`  |

**The mistake to watch for is not a bare outcome — it is a name that states the condition and then adds the outcome.** Such a name looks right, because a condition really is in there. Point at the expectation cell the name restates: if you can, cut that clause and keep the rest.

| Written                                 | Restates              | Keep                        |
|-----------------------------------------|-----------------------|-----------------------------|
| `Low haemoglobin defers the donor`      | `Deferred?` `true`    | `Haemoglobin below minimum` |
| `Short rest means the pilot cannot fly` | `Fit to Fly?` `false` | `Rest below minimum`        |
| `Three waste types force three bins`    | the `Bins?` map       | `Three waste types`         |

Two variants of the same mistake are easy to miss. A name saying *nothing changed* still restates the
answer — `Unlisted destination keeps the donor eligible`, beside an `Eligible After?` equal to
`Eligible Before?`. And a verdict-led prefix publishes the verdict column outright — `Deferred:
donation 30 days ago`. Cut the clause in both cases; the names still distinguish the rows.

Naming the rule or the situation stays correct even when it makes the outcome obvious — `At the minimum rest period, not below it`, `Night duty, two-pilot crew`, `Missing haemoglobin reading` are all good names. The check is whether the name repeats a cell in an expectation column of its own row, not whether a reader who knows the rule could predict the answer.

Scenario names appear in test failure messages, so clarity helps diagnose failures quickly.

### Use Concrete Domain Values

Column values should be concrete, meaningful data — not abstract flags or codes. Expectation column values should be traceable to input column values.

**Good** — directory names as inputs, resolved dir traceable to an input column:
```java
@TableTest("""
    Scenario             | Configured Dir | JUnit Dir    | Fallback State | Resolved Dir? | Source?
    All three set        | my-config      | report/junit | yaml           | my-config     | CONFIGURED
    Configured dir absent|                | report/junit | yaml           | report/junit  | JUNIT_PROPERTY
    Only fallback set    |                |              | yaml           | target/junit  | FALLBACK
    """)
```

**Bad** — abstract flags, expectation values not traceable to inputs:
```java
@TableTest("""
    Scenario                | Has Config | Override State | Fallback State | Resolved?
    Config present          | true       | yaml           | yaml           | configured
    No config, override set | false      | yaml           | yaml           | override
    No config, no override  | false      |                | yaml           | fallback
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

**A value set asserts that the result is identical for every value in it.** So the question is always
the rule's own granularity — not how different the inputs look to you.

- **The rule tells them apart: separate rows.** A sorter answering `WRONG_MATERIAL`, `CONTAMINATED`
  and `OVERSIZE` is making three decisions; a value set would collapse three outcomes into one cell
  and lose the *why*.
- **The rule does not: one row.** A sorter answering `REJECTED` however the item fails is making one
  decision. Pick a representative input or two and let a value set carry the rest.

```java
// WRONG — 20 kg at 5 mg/kg is 100, but at 8 mg/kg is 160; results differ
Standard course   | 20 | {5, 8} | *

// CORRECT — separate rows when results differ
Standard strength | 20 | 5      | 100
Double strength   | 20 | 8      | 160

// CORRECT — a value set where the result is genuinely identical
No doses due      | 0  | {5, 8} | 0
```

Two things follow. **An input the rule ignores** is this same case from the other side: two rows
differing only in that input are one row with a value set over it. And **enumerating every way an
input can be malformed is coverage of the format, not of the rule** — ten inputs producing one
undifferentiated rejection are one obligation, however different the ten look on the page (see Give
Each Obligation Exactly One Row).

#### Cartesian Product

Multiple sets in the same row create a cartesian product:

```java
@TableTest("""
    Scenario | a      | b      | Max Sum?
    Combined | {1, 2} | {3, 4} | 6
    """)
void combinesTwoValueSets(int a, int b, int maxSum) {
    assertTrue(a + b <= maxSum);
}
```

This generates 4 test cases: (1,3), (1,4), (2,3), (2,4).

#### Value Sets for Tier Grouping

When multiple input values produce the same output (a tier), group them into a value set:

```
Scenario         | Credit Hours    | Standing?
Under 30 hours   | {0, 10, 20, 29} | Freshman
30 to 59 hours   | {30, 45, 59}    | Sophomore
60 to 89 hours   | {60, 75, 89}    | Junior
90 hours and up  | {90, 100, 120}  | Senior
```

This makes the tier structure a first-class concept — each row IS a tier.

**Every tier gets a row, and every tier gets only one.** Two failures follow from breaking this, and
a ladder usually shows both at once:

- **Do not sample the ladder.** However many tiers the rule defines, that many rows. Showing the
  first tiers, the last, and trusting the reader to interpolate leaves the middle ones unproven — an
  implementation that mis-maps them passes. "The pattern is obvious" is not coverage.
- **Do not split a tier in two.** A "tier begins" row beside a "tier holds" row is one tier over two
  rows:

  ```
  At the 30-hour boundary | 30       | Sophomore
  Mid-band hours          | {45, 59} | Sophomore    ← same tier, second row
  ```

  The value set already spans the tier, so it already carries the boundary. Write
  `{30, 45, 59} | Sophomore` and the first row has nothing left to prove. Put both of the tier's
  boundary values *inside* the set.

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

**Blank cells mean absent, not irrelevant**: when an input is genuinely *absent* for a scenario, use a blank cell — not `0` or a default value — and use boxed types (`Integer`, `Long`) instead of primitives so the cell converts to `null`. **A default for an absent value cannot come from a `@TypeConverter`**: a blank cell never reaches one (see Handling Null Values). Write the empty value instead — `[:]`, `[]`, `{}`, `''` — where you want the converter to supply defaults, and otherwise let the system under test decide what `null` means. Never default in the test method body.

**Blank vs value set**: Blank cells mean the input is genuinely absent (null). When the input exists but is irrelevant to the outcome, use a value set instead: `{UK, Ireland, Other}` for destination means "destination exists but doesn't affect this result". Don't use blanks for "doesn't matter" — blanks mean null.

**Note**: These are syntax examples, not test design patterns. Null/empty/blank variants of an input belong as rows in the table that covers the feature, not in a separate test method — but **one row per distinct outcome, not one per representation.** Where null, `''` and `'   '` all produce the same rejection, that is one obligation: a single row, or `{'', '   '}` as a value set with a blank-cell row only where the null case must be visible on its own. Three rows are right only when the three actually behave differently.

---

## Workflow

**Budget your reasoning.** If concerns are already listed in the prompt, use them directly — don't re-derive what's already stated. If you find yourself re-analyzing the same concern, stop and write code. Working code you can revise beats perfect analysis that times out.

**Write incrementally.** For multi-concern features, write one `@TableTest` method at a time using the Write tool. Don't attempt to generate the entire test class in a single response — each method written is a checkpoint that can't be lost to a timeout.

### Converting Existing Tests

1. Identify tests with identical structure but different data.
2. Extract the varying parts as columns (inputs and expected values). Wherever the originals build a domain object — constructor, setters, builder, static factory, `with…` chain — that construction moves into a `@TypeConverter` and the parameter becomes the domain type; it never stays in the method body. Where the object has several optional fields, the column that feeds the converter is a map (see Collapse Sparse Columns into a Map).
3. Create table with scenario column first, inputs next, expectations last (suffix with `?`).
4. Align method parameters to column order; do not bind the scenario column unless annotated with `@Scenario`.
5. Verify all rows use the same assertion logic.
6. After building table with multiple rows, check for column consolidation opportunities (see Quality Checks).
7. Remove the original `@Test` methods the table now covers — run the tests before and after removal to confirm coverage is preserved.
8. When converting from another framework (Spock, Kotest, TestNG, JUnit 4), finish the migration: replace the old framework's assertion/matcher style (`shouldBe`, `expect:`, TestNG asserts) with the project's JUnit-compatible style, remove its imports, and remove its dependencies from the build file. Leftover matcher calls or a leftover build dependency both mean the conversion is incomplete.

### Writing New TableTest from a Feature Description

When there is no existing code (empty `src/main/java`), write the tests first — the table design drives the API shape. After the tests are written, add stub implementation code so they compile.

1. **Read the feature description** and identify the rules/concerns
2. **Write the test class** with `@TableTest` methods following the design principles in this skill
3. **Add stub implementation** — create the class and methods referenced by the tests with signatures only (return defaults, throw `UnsupportedOperationException`, etc.). Do not implement the logic unless specifically instructed. The user may want to iterate on the test design before committing to an implementation.

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
- [ ] **Domain language**: column names use the business vocabulary, not parameter or field names
- [ ] **No name restates its own row's answer**: read each scenario name beside the expectation cells of that row — no name states or paraphrases one of them, and none carries a verdict-led prefix
- [ ] **Uniform assertions**: all rows use the same assertion logic; split into separate TableTests if logic differs per row
- [ ] **Straightforward method**: no `if`/`switch`/ternary — not even null-guards or defaulting, which belong in a `@TypeConverter` or helper; the method only arranges, acts, and asserts
- [ ] **Parameter alignment**: parameters match data columns left-to-right (excluding scenario column)
- [ ] **Parameter conversion**: custom type converter methods (annotated `@TypeConverter`) or JUnit converters handle type conversion, keeping the test method free of parsing code
- [ ] **Valid syntax**: values requiring quotes are quoted, collections use correct bracket syntax, empty collections are explicit (`[]`, `{}`, `[:]`)
- [ ] **Expectation columns present**: at least one column uses `?` suffix (not prefix)
- [ ] **Concrete values**: expectation values are traceable to input column values where applicable
- [ ] **Thresholds visible**: rules that depend on a threshold or limit show it as a column, with boundary rows at and just past the threshold; for date cutoffs, prefer descriptive relative values (`before cutoff`, `on cutoff`) via a `@TypeConverter` — or include the cutoff date as a column if literal dates are used
- [ ] **Correct expected values**: arithmetic in expected columns verified independently; every row's output matches the stated rules
- [ ] **Value set semantics**: value sets only used where every value produces the same result; not used as shorthand for "test multiple values"
- [ ] **Irrelevant inputs use value sets**: an input the rule ignores appears as a value set spanning the values it ignores — never a fixed placeholder pinned in a converter, a field or the method body, and never merely asserted in `@Description`
- [ ] **One row per obligation**: every row discharges a behaviour no other row in that table reaches; where two rows share an expectation, what differs between them is what the rule is about — not a value further past the same boundary, a larger n in the same direction, or an input the rule ignores
- [ ] **Every tier once**: a tier ladder has one row per tier — all of them, none twice, no "tier begins" row beside a "tier holds" row; each row's value set spans its tier, both boundaries included
- [ ] **Combining tables prove an interaction**: any table exercising several rules together shows behaviour the single-rule tables cannot (a precedence, an ordering), not the earlier rules re-run end to end
- [ ] **Blank means absent**: a column whose input is genuinely absent for a row uses a blank cell (not 0 or a default), with a parameter type that accepts null — an input that is present but irrelevant is a value set instead
- [ ] **Traceability columns**: intermediate expected values included only when the value is observable from the public API — never reimplemented from internal logic; if you need to reimplement a formula to populate the column, decompose into separate tables instead
- [ ] **Held constants declared**: every value the outcome depends on that the table fixes for all rows is a column, or is named in the `@DisplayName`/`@Description` as held fixed — never left in the method body, a field, a `@TypeConverter`, or a `//` comment
- [ ] **Titles form an index**: read the class's titles as a sorted list — each states an action the code performs (not a label for a topic), one grammatical shape runs across them, and no three share an uninformative opener (`should…`, `test…`, `verify…`)
- [ ] **@Description free of internals**: no internal formula or algorithm; a reader must not be able to recompute the expectation cells from the description alone
- [ ] **One rule per table**: all observable outputs of the same rule sit in one table, and every expectation column there is exercised by the rows that table varies — one constant down all rows, or moving only as a side effect of another, belongs to a different rule's table
- [ ] **@Description adds information**: if present, `@Description` provides context beyond what the table shows (fixed values, domain context, open questions) — not a restatement of columns or rows. Omit `@Description` if there is nothing to add.
- [ ] **@Description uses text block**: `@Description` uses `"""` text blocks, not string concatenation with `+`
- [ ] **Annotation order**: `@DisplayName` → `@Description` → `@TableTest` (no other order)
- [ ] **Exception column**: error/rejection tables have a `Throws?` or `Exception?` column, not hardcoded exception classes in the method body
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
