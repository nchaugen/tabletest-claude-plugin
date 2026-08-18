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
    Scenario         | First Term | Second Term | Sum?
    positive numbers | 1          | 2           | 3
    with zero        | 0          | 5           | 5
    negative number  | -3         | 7           | 4
    """)
void addsTwoNumbers(int firstTerm, int secondTerm, int sum) {
    assertEquals(sum, Calculator.add(firstTerm, secondTerm));
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

**A column binds to the test method's parameter list, not to the argument list of the code under
test.** They are two different lists. The test method takes one parameter per data column; the body
passes on whichever of them the call accepts. So a value the API never receives can still be a
column: a policy constant the implementation hardcodes — a cutoff date, a tier threshold, a retention
limit — takes a parameter like any other column, and the body does not pass it anywhere. The column
is what puts the number on the page; the call is unchanged.

```java
@TableTest("""
    Scenario                       | Days Since Archived | Retention Limit (Days) | Purged?
    Day before the retention limit | 89                  | 90                     | false
    On the retention limit         | 90                  | 90                     | true
    """)
void purgesAnArchiveOnceItReachesTheRetentionLimit(
        int daysSinceArchived, int retentionLimitDays, boolean purged) {
    // purge(...) takes no limit — 90 is fixed inside the policy it reads. The column states the
    // boundary these rows straddle; the parameter is what makes the column legal.
    assertEquals(purged, retentionPolicy.purge(daysSinceArchived));
}
```

**"The method under test has no argument for it" is not a reason to move a threshold into the
`@Description`.** Check the test method's signature, not the call's, before deciding a value cannot
be a column. Two objections that look like reasons and are not:

- **"An unused parameter is a code smell."** Here it is the binding that makes the column legal, and
  the column is the point: a threshold only the description states cannot be read off the rows.
- **"Nothing checks that the column matches the constant in the code."** The rows check it. The
  boundary rows at and just past the threshold produce their expected values only if the
  implementation's constant is the one the column names — change either and a row fails.

Neither is a reason to add a parameter to the code under test. What does not bend: every data column
has a parameter — an unbound column does not compile, and this licenses no column without one.

### Single Values and Quoting

Use blank cells for `null` (reference types). Use `''` for empty strings. Use `'   '` for blank strings.

| Where the value sits | Quote it when it contains |
|---|---|
| A whole cell | `\|`, `"` or `'` — or it starts with `[` or `{` |
| Inside `[…]` or `{…}` | `,` `:` `\|` `]` `}` `"` `'` — anywhere in the element |

**The parser does not know your parameter type.** Parsing happens first and conversion after, so
`[a: b]` is a map even when the parameter is `List<String>`. That is why a colon inside brackets
forces quotes and a colon in a whole cell does not: `Alert: condensation risk` needs none.

```java
@TableTest("""
    Value             | Description
    simple            | No quotes needed
    "contains | pipe" | Quotes required for special chars
    ''                | Empty string
                      | Blank cell = null
    "[1,2,3]"         | Quote to avoid list syntax
    "{a,b}"           | Quote to avoid set syntax
    Alert: humid     | A colon in a whole cell needs no quotes
    """)
void quotesSpecialCharacters(String value, String description) { ... }
```

**Quote from the table above, not by trial and error.** Every case is decidable before you run
anything, and over-quoting obscures the data as much as under-quoting costs a build round.

**Quote inside collection values, not the whole collection.** For a collection element containing a
special character, quote only that element: `[path: 'C:\\Users']`, not `'[path: C:\\Users]'`. The quotes
wrap the problematic element, not the entire collection.

**A colon inside brackets is the case that catches people.** `[glass: rinsed, paper: dry]` is a
*map* with keys `glass` and `paper`, whatever the parameter says — and `[glass: rinsed, glass:
soiled]` fails outright with `Duplicate key 'glass'`. For a *list* of such values, quote every
element: `["glass:rinsed", "glass:soiled"]`.

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

Conversion also applies to collection elements, at any depth, and **a custom `@TypeConverter` is
reached the same way a built-in converter is**: `[com/example]` → `List<Path>`, `[Bob: 1980-03-04]`
→ `Map<String, LocalDate>`, `{https://claude.ai}` → `Set<URL>`, and `[[component: plasma, days: 30],
[component: whole blood, days: 90]]` → `List<Donation>` through a converter taking a `Map`. Nested collections work for the
same reason — `List<Set<String>>` converts element by element.

**Date format limitation**: Built-in `LocalDate`/`LocalDateTime` conversion only handles ISO 8601 (`yyyy-MM-dd`). Anything else — a dotted European date (`04.03.1980`), a written month (`4 March 1980`), a locale-specific pattern — fails at runtime, and the failure is a conversion error rather than a wrong value. A column carrying non-ISO dates needs a `@TypeConverter` (see Custom Type Converters below).

```java
@TableTest("""
    Scenario     | Class Name      | Resolved Path?
    With package | com.example.Foo | com/example/Foo
    Nested class | Outer$Inner     | Outer/Inner
    """)
void convertsClassNames(String className, Path expectedPath) {
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
under **Putting a Composite Value in a Cell**. It applies equally to a composed object whose parts
come from one cell, and to one assembled from a fixture plus a single varying value.

**One exception, and it is a last resort:** a converter needs a type to dispatch on, so where the code
under test has no type for the object, its parts go in separate columns and the method assembles them.
Name that in the `@Description` rather than leaving it to look like a choice — see *Putting a
Composite Value in a Cell*.

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

One converter per target type, **per class**, matched on the *erased* type — so `Optional<String>`
and `Optional<Boolean>` collide. If two columns need different parsing for the same type, use a
single converter that handles both formats, or split into columns with different types.

**This is what forces cross-table consistency.** Several tables in one class taking the same domain
type must agree on one cell format for it, so settle that format before the first table rather than
the third — see *Keep the Tables of One Concern Consistent*.

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
    Last value inside   | <50    | 49       | true
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

<!-- BEGIN GENERATED table-design — do not edit here; source is shared/table-design/ -->

### One Rule, One Axis

A table is one rule varying along one axis. The axis is what the rows change; everything else is
either held constant or collapsed into a value set. Most decomposition questions are that one
question asked again — *what is this table's axis, and does every column and row serve it?*

**If you cannot name a behaviour without using "and", it is two concerns.** Split them, and give each
its own table.

**The naming test passes on a conjunction, and a conjunction is still several rules.** A rule of the
form *"X holds only if C1 and C2 and C3"*, where the conditions do not mention one another, names
cleanly in one breath — "sorts waste into bins" — while being three independent claims. Give
each condition its own table, holding the others satisfied. Crossing them instead multiplies rows
without adding a claim, and no row then isolates the condition it was meant to show.

**The test is whether the rule can be *stated* about each condition alone — not whether the inputs
are separate.** Several inputs that are each a *contribution to one answer* are one rule, however
separately they arrive: quantities that are weighted and summed, amounts that accumulate into a
total, parts that combine into a whole. There is no claim to make about one of them by itself,
because the answer is the combination. Splitting those gives one table per input, each holding the
others at nothing, and **no table then shows them combining — which is the only interesting case**.
Keep them in one table with a column each, and let some of its rows carry several contributions
at once. Those rows belong to that table, which owns the combining rule; they are not a second
table run end to end — see *A Combining Table Needs Its Own Rule*.

Hold the inputs belonging to *other* concerns at one obviously-valid value. An input **this** rule
claims not to affect the outcome is the opposite situation and has to vary — see *Show That an
Input Does Not Change the Outcome*.

Separate tables reduce rows by avoiding unnecessary permutations, and the table count guides the
implementation: five concern tables suggest five functions.

```java
// Two concerns, two tables. "Duty eligibility AND rest credit" fails the "and" test.
@TableTest("""
    Scenario       | Hours Since Rest | Max Duty Hours (Policy) | Fit To Fly?
    At the limit   | 13               | 13                      | yes
    Past the limit | 14               | 13                      | no
    """)
void decidesFitnessToFly(int hoursSinceRest, int maxDutyHours, boolean fitToFly) { ... }
```

### Include All Outputs of a Concern

When an operation produces several observable outputs, include them all as expectation columns in one
table. Each row then gives the complete picture of what happens for that scenario. Splitting the
outputs of one concern forces the reader to cross-reference several tables to understand one
behaviour.

Separate tables are for separate **concerns**, never for separate outputs of the same concern.

**Every expectation column must be exercised by the rows the table varies.** A column that is
constant down every row, or that changes only as a side effect of another column, is not being
tested. Two repairs, and which is right depends on the rule:

- **Give it rows that vary it**, when the column does belong to this table's axis and the rows
  were missing.
- **Move it to the table whose axis varies it**, and drop it here rather than keeping it "for
  completeness".

```java
// Good — every output of one climate decision in one table
@TableTest("""
    Scenario             | Humidity % | Temp (C) | Vent Position? | Heater? | Alert?
    Warm and damp        | 80         | 28       | OPEN           | off     |
    Cold and damp        | 80         | 8        | CLOSED         | on      | Condensation risk
    Within target range  | 55         | 21       | CLOSED         | off     |
    """)
void resolvesClimateResponse(int humidity, int temp,
                             VentPosition vent, String heater, String alert) { ... }
```

### Decompose When You See These Signs

*One Rule, One Axis* gives the first test — a behaviour you cannot name without "and" is two
concerns. These are the signs that show up later, once the table exists:

- Some rows need columns that other rows leave blank throughout.
- Scenario names need qualifiers — "…for eligibility" against "…for pricing".
- The table has two groups of expectation columns that never both apply in the same row.

**Missing concern:** an input to one rule is itself derived from raw data. The derivation has its own
edge cases and needs boundary rows of its own. The rule's table then takes the *derived value* as
a direct input column, not the raw data. Two tables, not one.

**Ask of every input column where its value comes from.** Either it arrives from outside, or a rule
computes it — and a rule that computes it is a table you have not written yet. Two shapes say you
skipped it, and they look nothing alike: **the raw data is a column and the derived value is nowhere**,
so the derivation happens inside the rows where no row can put a boundary on it; or **both are
columns of the same table**, so the derived one restates a value already present and the rule
connecting them is legible only by reading the rows against each other. Split either way — the
deriving rule takes the raw data and reports the value, and this table takes that value as an input
column and never sees the raw data. **That the value must be visible is not the question**; which
table it is a column *of* is.

**A column blank throughout for most of its rows is a column decision before it is a table
decision.** Ask what the sparse columns feed. Several feeding the *same* expectation column are one
family: collapse them into one column keyed by member, and the table stays whole. Feeding
*different* expectation columns, they are different concerns and split into separate tables.

**Do not over-split either.** Several tables that fix the same setup, each vary one sub-rule, and all
report the same expectation column are one concern scattered — one table per adjustment, per option,
per flag. That shape is the symptom; the cause is a family you did not name.

**If you can name what several tables have in common in one term, they are one concern — that term is
the table, and its members are a column.** This is the mirror of the "and" test. Renal impairment,
low body weight and an interacting drug all *adjust the standard dose*: three rules, one family, one
table with an adjustment column. Naming the members instead commits to the split before a single
row exists, which is why this is decided when you name the table.

**Members of a family compute differently, and that is not a reason to split.** One adjustment is a
flat reduction, another a percentage, another a recalculation. The differing computation is what the
rows show; it is not what makes them separate tables.

**Collapsing a family means one table, not necessarily one column.** Where the members arrive as
*separate inputs* the system reads independently, a single column keyed by member cannot feed them —
routing one value to the right input would put a decision in the test itself, which is never the
answer. **Give each member its own column in the one table, and leave it blank throughout on the
rows where that member does not apply.** The family is still stated as one rule, the members
still sit side by side, and the sparse columns are what shows which member each row exercises.
Reach for the keyed column when the members are values one input takes; reach for a column each when
they are inputs of their own. **Splitting into a table per member is the wrong answer in both
cases** — and it is the tempting one, because it needs no decision.

**Collapse on a family, never on a bag.** A family is a domain category, not "everything that affects
the answer". The check: the family name works as a column header with the members as its values.
Where no such name exists the tables are genuinely distinct and belong apart — and so they do where
collapsing would cross-multiply, or leave rows whose purpose is no longer legible.

One family, one table with an adjustment column — not three tables each fixing the same setup:
```
Scenario                   | Adjustments                        | Daily Dose?
No adjustment              | [:]                                | 500
Renal impairment           | [renal: severe]                    | 250
Low body weight            | [weightKg: 20]                     | 200
Interacting drug           | [interaction: true]                | 400
Renal and interacting drug | [renal: severe, interaction: true] | 200
```

### A Combining Table Needs Its Own Rule

Once every rule has a table, the pull is to add one more that runs the whole feature end to end. It
re-proves what the single-rule tables already established, and it reads as redundant however clean
those tables are.

**A table that combines concerns earns its place only where the combination behaves in a way neither
concern shows alone** — a precedence, an ordering, an interaction whose result neither parent table
produces — and then it carries only the rows that show it. A table proving that a weight-based
dose is computed *before* the daily maximum caps it is a real table: the question is which rule
applies first, and its expected values appear in no other table. A table whose rows re-run each
dose band through the public entry point is not.

**Collapsing several same-fixture tables into one is not a combining table**, and *Decompose When You
See These Signs* requires it. The difference is what the merged table states: a family table states
one rule with its members as a column, while a combining table re-runs rules other tables have
already established. The first has a rule of its own; the second is a second pass over the ladder.

Two symptoms:

- **The description gives it away.** If the description you would write is "end-to-end scenarios
  combining the rules from the tables above", the table has no rule of its own. Delete it.
- **Wiring is not a rule.** Reaching a rule through the public API rather than the unit under test
  does not make it a new rule. If the wiring genuinely needs showing, that is one row, not a
  second pass over the ladder.

**Salvage its rows before you delete it — and then delete it.** One or two rows of an
end-to-end table often reach a case no single-rule table does. Deleting the table takes those with it
and nothing reports the loss, so list the obligations only its rows discharge and move each into
the table that owns its rule.

**This is a salvage step, not a reprieve — no outcome of it keeps the table.** A row worth
keeping is worth keeping *somewhere else*. Nor does shrinking the table save it: a single test that
runs the whole feature to re-prove one already-proven total is the same combining table with fewer
rows.

Earns its place — the answer appears in no other table:
```
Scenario                        | Body Weight (kg) | Daily Max (mg) | Daily Dose?
Weight-based below the cap      | 40               | 400            | 200
Weight-based above the cap      | 120              | 400            | 400
```

Does not — re-runs each band through the front door:
```
Scenario                | Body Weight (kg) | Daily Dose?
Low weight end to end   | 20               | 100
Adult end to end        | 70               | 350
```

### Separate Rules from Arithmetic

Tables specify the interesting decisions — classifications, eligibility rules, tier lookups, state
transitions — not that multiplication works.

**The symptom is an expectation cell you cannot predict in one step.** If reading a row means
classifying first and then computing, the table has fused two rules and states neither.

Give the classification its own table, whose expectation columns *are* the classification. Give the
arithmetic its own, taking the classification as an input. Each table then states one rule, and every
cell is predictable from its row.

This usually needs a narrower function to call. A table that can only reach the fused result means
the seam is missing, not that the table must fuse.

**Putting the classification in a column of the fused table satisfies this test without splitting
anything.** With the classified value beside the raw data, every cell is predictable in one step
again — and the rule that produces it has still not been stated anywhere. One-step predictability is
necessary, not sufficient; *Decompose When You See These Signs* asks the second question.

**Where you may not add the seam, name it.** Code you cannot change still has the boundary in its
behaviour, and a table that fuses two rules without saying why reads as a design choice. One sentence
on a published surface fixes that — *"the intermediate score is not observable, so the decision and
the amount are verified together; an accessor for it would allow two tables."* Whether the gap gets
closed in the code or bridged here is then the reader's decision to make, which it cannot be while
the gap is invisible.

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

### Give Each Obligation Exactly One Row

**The obligation list comes from the inputs, and producing it is the step most often skipped.**
Before counting rows, take each input the rule reads and ask three questions of it. Where an
answer is not obvious from the requirement, it names rows nothing else will.

- **Counted, or read in bands?** A rule reading an input in bands gives two different values the same
  answer, and the only way to state that is two rows differing in that input and agreeing in the
  expectation. A table whose every value of it carries a different expectation has stated "the answer
  rises with this input", which is a different rule from the one you meant.
- **Per unit of it, or once for having any?** Two rows — none and one — show a difference and are
  equally consistent with both readings, because a flat charge for having any at all is an ordinary
  rule. **A third consecutive value is what decides between them.** Without it the table leaves
  ambiguous the very rule it was written to state.
- **Does its effect depend on another input?** Where it does, the pair showing that effect has to
  appear on **both sides** of the other input's boundary. One pair, however well chosen, states an
  effect that is wrong wherever the other input differs.

Answer all three before writing rows. The questions cost nothing, and the rows they produce
are exactly the ones a reader cannot infer from the others.

The right number of rows is a covering problem. List the concern's **obligations** — the distinct
behaviours the rule must demonstrate — then write the smallest set of rows that covers all of
them. Both errors are real and they do not read alike: a missing obligation lets a wrong
implementation pass, while a repeated one costs the reader time and suggests a distinction that is
not there.

**The test for a redundant row, and it is decidable inside the table in front of you: where two
rows share an expectation, ask whether swapping one's differing input for the other's would
change an expectation cell *in this table*.** If it would not, they are one row — and a value set
is how you say so.

**"Exactly one" is a floor as well as a ceiling, and consolidating is where the floor gets broken.**
Trimming a table is the moment to re-read the obligation list, because the rows that look most
redundant are often the ones carrying an obligation of their own. Three shapes account for nearly
every obligation dropped that way:

- **A second input in a different *state*, mistaken for a larger value of the same one.** Acting on
  something already populated is not a bigger version of acting on something fresh — it is the case
  where existing content has to survive, and nothing else shows it.
- **The transition that empties or fills.** Removing the last member, filling the final slot: the
  row looks like the ordinary case with smaller numbers, and it is the only one that reaches the
  boundary of the container.
- **A distinct branch that shares its expectation with a neighbour.** Two rows agreeing on the
  answer are not redundant when they reach it by different routes — but **the routes have to differ
  in what this table expects, not in what its rule mentions**, which is what the test above decides.
  **A value the rule names is not thereby a branch:** enumerating the members is how a rule gets
  stated, and the table's job is to show which of them the answer turns on. Kinds of a thing that
  another rule tells apart are the usual false positive: three rows for three kinds, where the
  rule under test reads only whether the thing was valid.
  **Collapsing means the value set, not the delete key.** Put every kind in the surviving cell —
  `{percentage, fixed, product-specific}` — because the description will still claim the kind makes
  no difference, and deleting the rows leaves that claim with nothing behind it.

When you cut a row, say which surviving row discharges its obligation. If none does, keep it —
but **a value set discharges every obligation its members carried**, because it expands into one case
per value. Collapsing rows into a value set is not cutting them, and the floor is not in play.

**Two closed sets of inputs are where the floor gets misread.** With m values of one input and n of
another, every one of the m×n combinations is a case the rule names, so every one looks like an
obligation of its own and the rows grow to the full cross-product. The obligations are the
distinct *answers*, not the combinations: group the combinations that share an expectation, give
each group one row, and let the value sets carry the members. This is a row count, not a
table count — one rule still means one table, however its inputs multiply.

And three shapes account for nearly every genuinely redundant row:

- **Further past the same boundary.** A pair that *straddles* a boundary earns both its rows: the
  outcomes differ, and that is the rule. A second row on the same side does not, and the same
  holds for rejections — one row just past a limit rejects, and a row further past it rejects
  for no new reason. It earns its place only where the point *is* that two inputs collapse to one
  behaviour, and then a value set says that in one row; keep two and the scenario names have to carry
  why.
- **A larger n in the same direction.** If two incompatible items force a batch into separate streams,
  three incompatible items force it for the same reason. One obligation, one row.
- **A value the rule ignores.** The redundancy test above, applied directly: one row carrying a
  value set. **Where the value that differs sits inside a composite cell, the collapse needs the
  column reshaped first** — a value set varies a whole cell, never one part of one. Ask what else in
  that cell this table reads. If nothing does, the object does not belong in the cell and the value
  does: give it a column of its own and fix the object's other parts outside the table — bar any
  part a surface makes a claim about, which has to stay visible (*Assume the Table Is Published*).
  If other parts are read too, add a further element instead, so one row carries every state and
  the near-duplicate pair never arises.

**One value can carry two obligations, in two different tables.** A value that is a boundary for one
rule is often the subject of another. A zero duty period is both the accepted end of "duty hours
cannot be negative" *and* the input that should produce no rest requirement whatever the crew size —
two rules, two questions, two rows in two tables. Showing the value once, in whichever table you
reached first, feels like coverage and is not. **Count obligations per rule, never per value.**

```
Scenario                  | Duty Hours | Extra Rest Required?
At the duty limit         | 13         | false
Just past the duty limit  | 13.5       | true
// Well past the duty limit | 20       | true    <- redundant: 13.5 already proved it
```

Keep the straddling pair; drop the row further out.

### Cover Every Tier and Both Sides of Every Boundary

When inputs map to tiers — rate bands, size categories, standings — every tier appears in the
rows, and every boundary is exercised from both sides: the last value inside a tier and the first
value of the next.

**Middle-tier boundaries are the ones most often skipped.** Outer edges alone do not pin down where
the middle tiers change.

**Pick the pair's unit from the finest distinction the rule draws, before writing either value.**
Where the rule separates 29 days 23 hours from 30 days 1 hour, whole-day rows of 30 and 31
straddle nothing — the column is `Hours Ago` and not `Days Ago`. A boundary drawn in a unit coarser
than the rule is not drawn at all, however many rows surround it.

**And express it as an offset from the reference point, not as an absolute value restated in every
row.** `Hours Ago` is the whole example: it fixes the unit *and* keeps the row readable, where
absolute instants pin the same boundary while making the reader subtract before the rule is visible.
*Assume the Table Is Published* sends the reference point itself to a column; this rule owns the unit,
and one choice satisfies both. A boundary win bought with an unreadable cell has been paid for twice.

**A formula behind the tiers does not reduce the tiers.** If you find yourself arguing that two tiers
and the delta between them determine the rest, that is the formula talking: the table pins the tiers
the rule names, and identifying the formula is the implementation's job. Nine tiers stay nine
rows.

**And it does not reduce the boundaries.** Where the tier is decided by a formula over several
inputs, every input still has a value at which the outcome flips, and that pair is what the rows
have to straddle — one just below it, one just above, the other inputs held. Sampling that input at
two comfortable values instead exercises the arithmetic and leaves the boundary untested.

**A ladder repeated across several classes states its boundaries once.** Where one ladder is priced
or graded differently per class — the same usage bands on every tariff, the same age brackets in
every region — exercise both sides of every boundary in **one** class, and give each other class one
row per tier:

```
Standard, at band 1 limit | standard | 100    | 12.00
Standard, band 2 begins   | standard | 100.01 | 18.00
Standard, at band 2 limit | standard | 500    | 18.00
Standard, band 3 begins   | standard | 500.01 | 30.00
Economy, band 1           | economy  | 50     |  9.00
Economy, band 2           | economy  | 300    | 13.50
Economy, band 3           | economy  | 800    | 22.50
```

**The check is whether the boundary positions differ between classes.** If they differ, each class
has its own ladder and owes its own straddling pairs. If they do not, the positions belong to the
ladder and the classes differ only in their values — which is what their one-row-per-tier lines
state.

**Duplicated implementation is not a reason to repeat the pairs.** That the same comparison is
written out once per class is a property of the code, and *Design Black-Box Tables* is where that
argument stops: the table states the rule the specification gives, and a specification with shared
bands declares one ladder. Without this, the obligation above reads as *boundaries × classes* and
the row count multiplies with nothing added.

Where a tier is a range rather than a single value, a value set spanning it carries its own
boundaries — **provided its first and last members are the tier's own first and last values.** The
straddling pair is then already written: the last member of one row's set and the first member of
the next row's. **State the tier's edges, not two comfortable values inside it** — a set of middle
values straddles nothing and the explicit pair is still owed. Done that way a separate "tier begins"
row discharges nothing the "tier holds" row has not, and **one row per tier covers the
whole ladder and every boundary in it**. That is economy inside a row and buys no licence to drop
rows: shortening each tier to one cell makes the ladder look repetitive long before it is
complete.

This is the coverage half of *Give Each Obligation Exactly One Row*, and the two meet at a
boundary: the straddling pair is required here and earns both its rows there. A third row
further past the same boundary is what the other rule removes.

```
Scenario                       | Haemoglobin | Donation Band?
Below the minimum              | 124         | DEFER
At the minimum                 | 125         | STANDARD
Top of the standard band       | 159         | STANDARD
First value of the high band   | 160         | REVIEW
```

Both middle boundaries appear from both sides; outer edges alone would not locate them.

### Show That an Input Does Not Change the Outcome

**You have just concluded that some input does not affect this rule. That conclusion is a rule too,
and it needs a row that could contradict it**: put every value the rule ignores in the cell, as
a value set.

**Dropping the input states nothing.** Leaving the column out reads exactly like having forgotten it
— nothing on the page tells a reader which happened, and no row can contradict a claim the table
never makes. A blank is no better and wrongly suggests the field is absent. The value set makes the
claim explicit — *this rule holds for all these values* — and one row states it more precisely
than two near-identical ones.

**The clearest sign you want one: a column that could carry every one of its values on every
row without changing anything.** That is the rule saying, in data, that it does not read the
column.

**If the operation does not take the input, fix that before the table.** A rule cannot say it ignores
what never reaches it, and leaving the parameter out publishes nothing: the reader sees an operation
that was never offered the value, which is silence rather than a claim. **A column the code never
receives will not do instead** — that route is open to a threshold, which only has to be readable
(*Make Thresholds Visible*), and closed to this rule, whose claim has to be exercised to be
contradictable. Decide it by asking who supplies the value: **if a caller hands it over with the
request it is an input, so keep it and let the table vary it**, even where the code will not read it.

**If the ignored value sits inside a composite cell, the value set is the wrong tool — and it is not
the only one.** `{a, b}` written inside a cell adds no rows: expansion varies a whole cell, never
one part of one. Two routes remain, and the cheap one is easy to miss. **Where the cell holds a list,
vary the ignored value across its elements** — one row, three entries, three different values,
nothing reshaped. Where it holds a single object, give the value its own column if this table reads
nothing else in that cell (*Give Each Obligation Exactly One Row*).

**Fixing the value in the converter is the move to catch yourself making.** It is the cheapest thing
to write and it drops the claim altogether: what reaches the reader is one hard-coded value they
cannot tell from an oversight, and a sentence about a rule that no row exercises. The reshape is
worth it because the claim is; if the claim is not worth a column, it was not worth the sentence
either.

**A value set cannot vary an expectation.** It expands the row into one case per value, and every
expanded case keeps the same expectation cells. Where the answer differs per value, those are
ordinary distinct rows.

**Every value in the set must produce the same result.** If the results differ, the input does matter
and belongs as ordinary distinct rows. Never use a value set as shorthand for "test several
values".

**Two different situations, two different treatments.** An input that *another* rule owns is held at
one obviously-valid value. An input that *this* rule claims not to affect has to vary across the
values it ignores — otherwise no row could ever contradict the claim.

**Value sets work on two axes — check both.** *Within* a row, group input values that produce the
same outcome. *Across* rows, collapse duplicates: when two input values produce the same
expectation cells **in this table**, one row carrying both replaces two identical ones. It is
easy to apply one axis and miss the other.

**Judge that per table, not across the whole class.** Two values that this rule treats alike collapse
here even if a neighbouring rule tells them apart — grouping them says *this* rule does not
distinguish them, which is exactly what the neighbouring table then contradicts, on the record. Ask
only whether the expectation cells match in the rows in front of you. A category you have named
as a catch-all is the easy case and gets collapsed almost automatically; **the one that gets missed
is two values you think of as distinct that this particular rule happens to treat the same.**

```java
@TableTest("""
    Scenario                          | Donor Age | Haemoglobin | Recent Travel | Eligible?
    Below the minimum age             | 16        | {125, 140}  | {yes, no}     | no
    Eligible adult donor              | 35        | 140         | no            | yes
    Travelled recently, otherwise fine| 35        | 140         | yes           | no
    """)
void decidesDonorEligibility(int age, int haemoglobin, boolean recentTravel, boolean eligible) { ... }
```

The first row claims age alone decides it, and varies the two inputs it ignores so a row could
contradict the claim.

### Frame Stateful Features as Transition Rules

When a feature involves state — queues, workflows, inventories — frame each row as a state
transition rule: the state before, the action, the state after, and any message or result.

Each row is independent: given this state, when this action happens, expect this result. No row
depends on a previous one having run.

**Include the before and after columns** even when the description states the operation procedurally.

A sequential path — step 1, then step 2, then step 3 — creates row dependencies and is not a table
at all.

```
Scenario                  | Bin Before                | Action              | Bin After?                | Message?
Accept a labelled item    | [:]                       | deposit cardboard   | [CARDBOARD: 1]            | Accepted
Fill to the bulk limit    | [CARDBOARD: 1]            | deposit cardboard   | [CARDBOARD: 2]            | Accepted
Reject a mismatched item  | [CARDBOARD: 1]            | deposit solvent     | [CARDBOARD: 1]            | Wrong stream
```

### Assume the Table Is Published

Write every table as if a reader will meet it in a published report, never having seen the code. Only
three surfaces reach that reader, and they divide the work:

| Surface | Carries |
|---|---|
| `@DisplayName`, or the method name when there is none | the rule, as an action the code performs |
| `@Description` | the apparatus that cannot be a column — what is held constant, where the data came from |
| the table | the variations the rule ranges over |

**Whatever the table holds constant is silently promoted into the rule.** Readers generalise from
what varies, so a value that never varies is read as part of the rule: a duty-limit table whose every
row assumes a two-pilot crew states, to its reader, a rule about two-pilot crews.

So a constant the outcome depends on is a **column** wherever it can be one — and a value the rule
turns on, such as a threshold or a limit, always can be. The other two surfaces carry what a column
cannot: where the data came from, what the fixture fixes, an assumption the rows cannot state.

**Once a value is a column, it is declared — check the table before writing about it or adding to
it.** A sentence in `@Description` naming a value the rows already show tells the reader
nothing they cannot read off the table, and a column that does not vary is declared just as well as
one that does. Being constant is not on its own a reason to add a row varying it: where a constant
column hides a second axis, *Make Thresholds Visible* owns that question — and when the rows are
given to you, by a conversion or a supplied set of examples, adding one *to vary the constant* changes
the question you were asked. Boundary and tier rows are a separate obligation and are never what
this paragraph is about.

**Which table it is a column of is a separate question, and this rule does not answer it.** Where
another rule derives the value, it is an input column here and an expectation column there — see
*Decompose When You See These Signs*, which owns that split. Making a value visible is never a reason
to absorb the rule that produces it.

**If the declaration says the value does not matter, declaring it is not enough.** *"Held empty
throughout, and it makes no difference"* is not apparatus — it is a claim about the rule, and a claim
no row can contradict is not stated in the table at all. Vary it instead, across the values it
ignores; see *Show That an Input Does Not Change the Outcome*. Write a fixture into the
`@Description` only for what the rule genuinely reads and the rows cannot show.

**Making a value a column does not force everything measured from it into the same form.** Once a
reference point is declared — a clock, an origin, a baseline — the columns measured *from* it read
better as offsets against it than as restatements of it. Both are then visible, and the offsets stay
short enough to scan.

**Choose that unit before shortening anything, and choose it from the boundary rather than from the
offset.** A shorter cell that cannot state the rule has bought nothing — see *Cover Every Tier and
Both Sides of Every Boundary*, which owns the choice.

**Keep a slot in the cell for every field `@Description` makes a claim about.** A compact
cell carries the fields the rule reads and drops the rest, and a dropped field is pinned exactly as it
would be in a conversion helper, with nothing on any surface to say so. When the claim is that the
outcome does not turn on that field, dropping it is what makes the claim uncontradictable — put the
field back as a key or a column, or stop making the claim.

**A field no surface says anything about is the opposite case, and leaving it out is what keeps the
cell readable.** An object with twelve properties whose rule reads two belongs in the table as those
two; a fixture supplies valid values for the rest. The rule above is the whole limit on that — what
must be visible is what something claims about, not everything the object happens to hold.

It is **not** declared when it sits in the test body, in a field, in a conversion helper, or in a
comment — a comment reaches no published surface at all. The helper is the easiest hiding place
because it looks like plumbing: one that builds every entry with the same zone has pinned zone for
the whole table, and no column says so.

**What the assertion tolerates is part of the rule too.** A comparison that sorts either side before
comparing, accepts a subset, matches "contains" rather than equals, or normalises case or whitespace
is *enforcing a rule*: it changes which behaviours the test would accept, and none of it reaches the
reader. Ordering is the usual one, and a shared helper is where it hides — written once, then
invisible at every call site, so a reader cannot tell whether order is part of the behaviour or an
artefact of the comparison. Two repairs, and the second is better where it fits:

- **Name it** — one sentence in the description, or a column that makes it evident.
- **Remove the need for it** — an unordered collection as the expectation says order does not matter
  *in the table itself*, which beats saying so in prose; an ordered one with a canonical sort says it
  does.

Numeric tolerance is not a criterion: a conventional epsilon on a decimal column is exempt. Nor is
constructing the objects the columns name.

Crew size never varies, so a reader takes the rule to be about two-pilot crews. Make it a column:
```
Scenario                 | Duty Hours | Crew Size | Max Duty Hours (Policy) | Fit To Fly?
Two-pilot crew, inside   | 12         | 2         | 13                      | yes
Two-pilot crew, past     | 14         | 2         | 13                      | no
Augmented crew, past 13  | 14         | 3         | 17                      | yes
```

### Write Titles That Form an Index

`@DisplayName`, or the method name when there is none is the line a reader scans in the report index. **Judge titles as a set, never one
at a time:** a title that reads well on its own page can still be an unscannable entry in the list.

**Open each title with something that distinguishes it, and keep one grammatical shape across the
family.** When every title starts with the same word the index becomes a column of identical openers,
and the distinguishing part arrives last, where scanning cannot reach it. Three titles sharing an
uninformative opener is enough to make the list unscannable.

**Write an action the code performs, not a label for a topic.** This is the half that is easy to
miss: a noun phrase can front the varying subject and still say nothing about what the code *does*
with it. "Deferral interval by donation type" names a topic; "Sets the deferral interval from the
donation type" names behaviour. The label form is the more tempting mistake, because it looks tidy in
a list.

One outlier does not break a family — a negative or invariant claim often reads best subject-first.

**A title states what your system does, not an external fact it depends on.** Strike the system under
test from the sentence: if it still reads as true, the title is restating a regulation, a format or a
domain fact instead of naming behaviour.

**This action voice is the title's alone.** Scenario names stay condition phrases naming the row's
variation — see *Name Scenarios by Condition, Not Outcome*. A title says what the rule does; a
scenario name says which case this row is. Writing rows as little sentences is how outcome-echoing
scenario names get in.

| Scans as an index                                    | Does not                           |
|------------------------------------------------------|------------------------------------|
| `Sets the deferral interval from donation type`      | `shouldApplyDeferralInterval`      |
| `Rejects a reading below the haemoglobin minimum`    | `Haemoglobin minimum by donor sex` |
| `Defers a donor returning from a listed destination` | `shouldDeferForTravelDestination`  |

Three distinct verbs, each carrying information, and the subject arrives immediately after.

### Name Scenarios by Condition, Not Outcome

Good scenario names answer "under what circumstances?" — not "what happens?". The outcome is already in the
expectation columns; naming it twice adds nothing, and when the expectation changes the name
silently lies.

Appending the outcome to a condition is still naming the outcome. The name only needs to say
*when*; the row's expectation values say *what*.

Naming the rule or the situation is correct even when it makes the outcome inferable. The failure to
avoid is a name echoing its own expectation cell, and a generic label that names no variation
at all.

**A priority or decision table is where this goes wrong most often**, because such a table almost
always publishes the winner as an expectation column. "Configured wins" beside a `Source?` of
`CONFIGURED` restates its own answer; "Both sources set" and "Input dir absent" say which case the
row is.

| Good (condition)             | Bad (outcome)      |
|------------------------------|--------------------|
| `Unlabelled item`            | `Rejected`         |
| `Solvent in a sealed drum`   | `Goes to hazardous`|
| `Cardboard over the bulk limit` | `Bulky handling` |

`Solvent in a sealed drum` PASSES even though a reader who knows the rule can predict the outcome —
naming the situation is not naming the answer.

### Name Expectation Columns Clearly

End every expectation column with a `?` **suffix**, so a reader can tell at a glance which columns are
outputs being verified and which are inputs being provided. Input columns never take `?` — including
yes/no columns that describe the state a scenario starts in.

**Prefer the rule's direct output.** `Fee?` beats `Total?`: the fee is what the rule decides, while
verifying the total also requires knowing the base amount. If you do expect a derived value, include
its inputs as columns so a reader can trace it.

**One exception, and it is narrow: identity and status varying together in the same output
position.** Where a column answers *which* one and *how it went* at once — the winner of a pair and
whether it succeeded — the two are one value and the cell names it as one, `Primary OK` against
`Secondary ERROR`. Splitting that into "which?" and "did it succeed?" columns doubles them and makes
the reader join the halves back up. This is a domain value with its own type, not an encoding: it
holds only where **both** parts vary in the same position. Where identity is fixed for the row and
only the status varies, the ordinary column design applies.

**A compound result stays a collection.** When the value under test is several items — or items
grouped under a key — the expectation is a native list, set or map, nested where needed, compared
against what the system returns. Do not flatten it into a string assembled by a formatting helper:
that tests the formatter rather than the rule, hides the structure from the reader, and puts
formatting logic back into the test body. Use a set where order is not part of the rule, and a list
with a canonical sort where it is.

**A key is part of the value, so the same holds one level down.** Grouping the result under a key
pasted together from several fields — `HEAT/G3/zone-2` standing for a mode, a unit and a zone — has
flattened the structure into the key instead of the value, and the row still reads as one token
whose parts the reader separates by eye. Nest one level per part, or give the key a type with a name.
**The test is the one that governs any cell: can a reader name each part?** A delimited string is not
the alternative to nesting, at any level.

```
Scenario                  | Items                     | Streams?
Mixed recyclables         | [paper, card]             | [recycling: [paper, card]]
Recyclable and residual   | [paper, foil]             | [recycling: [paper], landfill: [foil]]
```

`Streams?` stays a native map. Flattening it to `"recycling:[paper]"` would test the formatter.

The `?` marks outputs only — never an input, however yes/no it looks:

| Good (input)         | Bad (input)           | Why bad                       |
|----------------------|-----------------------|-------------------------------|
| `Repeat Donor`       | `Repeat Donor?`       | `?` implies this is an output |
| `Within Rest Period` | `Within Rest Period?` | This is a given condition     |
| `Vent Open`          | `Vent?`               | This is an input state        |

### Model Rejection as an Expected Column

When a table covers cases the system rejects, the rejection is an **expectation column** — the error
type, or the reason — never a decision taken in the test body. Each row then states its own
outcome where the reader can see it.

**Whether accepted and rejected rows share a table is decided by what the table is about, and
there is a decidable test for it: remove the rejected rows.** If what remains still states a rule,
the rejection was a separate concern — split it out. If what remains says nothing on its own, the
table is about acceptance and stays whole.

A tier ladder with one rejection row at the end **fails that test**: strike the rejection and the
ladder still states the tiers. It is two concerns, however tempting the last-accepted-beside-
first-rejected pair looks. A validation boundary passes it: strike the rejected row and a single
accepted value is left, which states nothing by itself.

- **The table's whole expectation is whether the call is rejected** — a boundary straddling a
  validation limit, the last accepted value beside the first rejected one. That is *one rule*, and
  splitting it puts the two halves of a single boundary where no reader sees them together. Keep one
  table, leave the rejection column blank where nothing is rejected, and compare the outcome as a
  value.
- **Rejection is one outcome among several** — a parser returning values for good input and rejecting
  malformed input. Those are two concerns and belong in two tables.

**Never branch in the body to choose how to assert.** Picking between a rejection assertion and a
value assertion per row puts the rule back where the table cannot show it, and it is the failure
both shapes above exist to avoid.

One rule — the whole table asks whether the dose is accepted:
```
Scenario                | Dose (mg) | Throws?
At the minimum dose     | 0         |
Just below the minimum  | -0.01     | java.lang.IllegalArgumentException
```

Two concerns — parsing returns values, rejection is its own table:
```
Scenario          | Input      | Parsed?
ISO date          | 2026-07-30 | 2026-07-30
Short year        | 30/07/26   | 2026-07-30
```

### Use Concrete Domain Values

Cell values are concrete, meaningful domain data — not abstract flags, codes, or placeholders. An
expectation value is traceable to the input values in its own row.

**An expectation naming something that appears nowhere in the row is a value hardcoded in the
test, not a value the table states.** The reader then cannot understand the table without reading the
code, which is the one thing the table exists to prevent.

When a value is derived from an input, include the source column so the derivation is visible.

**Prefer the value the system really produces.** Where a sentinel, enum constant or error string is
part of the observable contract, put that in the cell rather than a tidier test-only label — the
row then states what a reader would actually see. Shorten a value only when it is too long to
scan, and shorten the **value**, never the vocabulary: `acme:search:v2` scans as well as a
placeholder and still says what each part is. Single letters cost more than they save, because the
legend that decodes them lives outside the table.

**Where a cell carries several parts, the test is whether a reader can name each one.** That is the
same legend question asked of a compound value: `2 x 5 mg tablet` explains itself, while
`G3/HEAT/zone-2` needs a key that lives somewhere else. It decides how much structure the cell
has to show — spell the parts out where the values alone do not identify them, and let them stand
bare where they do.

Write literal values even when they repeat across rows. Extracting them into named constants
forces the reader to look up every number, which is exactly the indirection the rows exist to
remove.

**Good** — every expectation traceable to the row's own inputs:
```
Scenario                    | Body Weight (kg) | Dose Per Kg (mg) | Daily Dose (mg)?
Standard adult              | 70               | 5                | 350
Paediatric                  | 20               | 5                | 100
```

**Bad** — `standard` and `reduced` appear nowhere in the row:
```
Scenario                    | Heavy | Impaired | Dose?
Normal function             | true  | false    | standard
Impaired function           | true  | true     | reduced
```

### Use Domain Terminology

Column names use domain or feature terminology that readers understand without knowing the
implementation. Avoid parameter names, variable names, and internal API terms.

The table should read as a specification a domain expert could review.

| Good (domain)        | Bad (implementation)   |
|----------------------|------------------------|
| `Body Weight (kg)`   | `weightKg`             |
| `Renal Function`     | `renalFlag`            |
| `Dose Band?`         | `result`               |

### Make Thresholds Visible

When a rule depends on a threshold or limit, include it as a column — even when the value is constant
across every row.

Without the threshold column the number is buried in the code: the reader cannot tell from the table
where the boundary is, or whether the rule is strictly greater than. Boundary rows — at the limit,
just over it — become natural to add once the threshold is visible.

**A constant column often signals configuration.** Ask under what circumstances the value would
differ. The answer may reveal a second axis that belongs as new rows or as a separate table.

**A threshold another rule computes stays a column here, and that rule keeps its table.** Never carry
both the threshold and the input it is derived from — see *Decompose When You See These Signs*.

```
Scenario                | Days Since Last Donation | Min Interval (Policy) | Eligible?
Exactly at the interval | 90                       | 90                    | yes
One day short           | 89                       | 90                    | no
```

### Include Traceability Columns

When a table exercises a pipeline — input, then an intermediate result, then a final result — include
the intermediate as an expectation column. A reader can then trace the logic step by step, and when a
row fails the intermediate column shows where in the pipeline it broke.

The intermediate is usually not strictly necessary: the test could verify only the final value. It
earns its place by making the derivation legible in the row.

**Guard: only for values the system exposes, or that are observable domain concepts.** If populating
the column would mean reimplementing an internal calculation in the test, it does not belong — the
intermediate is pointing at a separate concern that needs its own table. Decompose instead, and the
intermediate becomes an expectation in one table and an input in the next.

```java
@TableTest("""
    Scenario                     | Body Weight (kg) | Renal Function | Dose Band? | Daily Dose (mg)?
    Adult, normal function       | 70               | Normal         | Standard   | 500
    Adult, impaired function     | 70               | Impaired       | Reduced    | 250
    Low weight, normal function  | 40               | Normal         | Low        | 300
    """)
```

`Dose Band?` is not strictly necessary, but it lets a reader trace weight + renal function -> band ->
daily dose, and a failure shows which step broke.

### Blank Means Absent

Use a blank cell when a value is genuinely absent. Blank means **absent** — not zero, not a default,
and not irrelevant.

Three meanings the notation has to keep apart:

| Meaning | Notation |
|---|---|
| The value is missing | blank cell |
| The value exists but does not affect this row | value set |
| The value is present and empty | `''` for a string, `[]` `{}` `[:]` for a collection |

**The system under test decides what an absent value means — never the test.** That decision is part
of the behaviour being specified, and the row exists to pin it down. Writing a baseline value into
the cell is a different scenario; converting a blank to a default on the way in deletes the case the
row was written to show.

Do not fill a genuinely blank cell with filler like `N/A` or `none`.

```java
@TableTest("""
    Scenario                   | Humidity % | Override Setpoint | Vent Position?
    No override configured     | 80         |                   | OPEN
    Override supplied          | 80         | 90                | CLOSED
    """)
void resolvesVentPosition(int humidity, Integer overrideSetpoint, VentPosition vent) { ... }
```

The blank row specifies what the controller does with *no* override. Writing `0` there would specify
something else, and defaulting it in the method body would specify nothing at all. A column whose
type is a number has no present-but-empty value to write: `''` is an empty **string** and fails to
convert.

### Design Black-Box Tables

Model observable inputs and outputs. Avoid internal flags and setup-only columns unless they are part
of the public contract.

Anything the test does beyond arranging, acting and asserting is a rule the table cannot show.
Construction belongs in a conversion helper, the expected error in a column, defaulting and
normalisation outside the body entirely. When you find yourself writing logic in the test, ask which
column or helper it should have been.

```
Scenario                | Humidity % | Temp (C) | Vent Position?
Warm and damp           | 80         | 28       | OPEN
Within target range     | 55         | 21       | CLOSED
```

Observable readings in, observable position out. A `sensorPollCount` or `controllerInitialised`
column would be internal state, not the contract.

### Keep the Tables of One Concern Consistent

Tables that sit together are read together. Within one method set, the same concept takes the
same column name, the same kind of value takes the same notation, and the same failure takes the same
spelling. `Response Time?` in one table and `Timing?` in the next reads as two different things; `<50`
beside a bare `50` leaves the reader deciding whether the second is a maximum or an exact value.

**Decide the shared notation before writing the first table, not while writing the third.** The choice
is cheapest at the start and gets more expensive with every table that fixes it differently.

Four things to hold steady across the set:

- **Column names** — one name per concept, everywhere it appears.
- **Value notation** — one way of writing a bound, a duration, an absent value.
- **Failure vocabulary** — one spelling of an error, not `ERROR` here and `FAIL` there.
- **The helpers behind them** — one parser or fixture shared, rather than a copy per table.

Two tables in one class, one notation for the concept they share:

```java
@TableTest("""
    Scenario           | Upstream Latency | Response Time?
    Healthy upstream   | <10              | <50
    Upstream throttled | <400             | <500
    """)
void answersWithinTheLatencyBudget(Latency upstreamLatency, Latency responseTime) { ... }

@TableTest("""
    Scenario           | Upstream Latency | Report Time?
    Healthy upstream   | <10              | <50
    Upstream throttled | <400             | <500
    """)
void publishesTheReportWithinTheLatencyBudget(Latency upstreamLatency, Latency reportTime) { ... }

@TypeConverter
public static Latency toLatency(String value) { ... }
```

A bare `50` in the second table would leave the reader deciding whether it means a maximum or an
exact value. **One converter per target type, per class**, matched on the *erased* type: two
converters returning `Latency` throw at runtime, so once two columns share a type the shared notation
is not a matter of taste.

<!-- END GENERATED table-design -->

---

## Expressing These Rules in TableTest

The rules above are notation-independent. These are the TableTest mechanics they need.

### Cartesian Product

Multiple value sets in the same row create a cartesian product:

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

### Expressing a Rejection

*Model Rejection as an Expected Column* decides **whether** accepted and rejected rows share a table.
In TableTest the column holds the exception type, and the comparison is a value comparison:

```java
@TableTest("""
    Scenario                | Dose (mg) | Throws?
    At the minimum dose     | 0         |
    Just below the minimum  | -0.01     | java.lang.IllegalArgumentException
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

### Writing Absent, Empty and Blank Values

*Blank Means Absent* decides **which** of these a cell should be. This is how each is written:

```java
@TableTest("""
    Scenario        | Input | Resolved?
    Normal input    | hello | HELLO
    Null input      |       |
    Empty input     | ''    |
    Blank input     | '   ' |
    """)
void resolvesValues(String input, String resolved) {
    assertThat(transform(input)).isEqualTo(resolved);
}
```

Use **boxed types** (`Integer`, `Long`) rather than primitives, so a blank cell converts to `null`.
**A default for an absent value cannot come from a `@TypeConverter`** — a blank cell never reaches
one (see Handling Null Values). Write the empty value instead — `[:]`, `[]`, `{}`, `''` — where you
want the converter to supply defaults, and otherwise let the system under test decide what `null`
means.

### Use @Description When It Adds Information

Add `@Description` when there is context the table alone cannot convey. Omit it when the table already says everything — a vacuous description adds noise.

Good reasons to add `@Description`:
- **Fixed values the rule reads that cannot be columns** — see *Assume the Table Is Published*, which decides that; a value that can be a column belongs in one
- **Domain context** — where/when the rule applies, who is affected, which market
- **Open questions** — decisions not yet resolved
- **Relationship between tables** — how this table connects to others in the class

Do not restate what the table already shows. If the description merely summarises the column names or row outcomes, delete it. A fixed value is noise either way — one the rule never reads is irrelevant, and one it does read belongs in a column — so "Fixed for all rows: donor name = 'A. Nolan'" earns a place here only when it cannot be a column.

**A description must not publish the algorithm.** Restating the internal formula — "the dose index is
body weight divided by ten plus four per severity grade, capped once it passes 75" — turns a
black-box table into a white-box one and pins the test to an implementation the rows never observe.
A **threshold the rule is about** is different, and it goes in a column rather than here (see *Make
Thresholds Visible*). The line is whether a reader could recompute every expectation cell from the
description alone. If they could, the description is doing the code's job.

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
@DisplayName("Defers a donor inside the interval for their donation type")
@Description("""
    Days Since Last counts from the end of the previous donation, not its start.
    """)
@TableTest("""
    Scenario                        | Donation Type | Days Since Last | Interval (days) | Deferred?
    Whole blood, day before interval| whole blood   | 89              | 90              | true
    Whole blood, at the interval    | whole blood   | 90              | 90              | false
    Plasma, day before interval     | plasma        | 13              | 14              | true
    Plasma, at the interval         | plasma        | 14              | 14              | false
    """)
void defersDonorInsideTheInterval(String donationType, int daysSinceLast, int intervalDays, boolean deferred) { ... }
```

### Putting a Composite Value in a Cell

Start from the map and simplify, one step at a time, while the cell still says everything the table
has to say. **The test at every step is the reader's: could someone who has never seen the code name
each part of this cell?**

| The cell holds | Write it | Parameter |
|---|---|---|
| An object — the default, and always safe | a map, `[k: v]` | the domain type, converter takes `Map` |
| …whose values name themselves without keys | a domain notation (`2 x 5 mg tablet`) | the domain type, converter takes `String` |
| …reduced to one part | that value alone (`<50`, `90d`) | the domain type, converter takes `String` |
| **Several objects** | **a list of any of those, `[[k: v], [k: v]]`** | **`List<DomainType>`** |

Keys are never *wrong*; they are the shape that always works. The lower rows are what you may drop to
when the shorter cell costs the reader nothing.

**Invent a notation only when its legend is in the cell** — *Use Concrete Domain Values* asks that
question of every compound value, and applying it to the shape you are choosing is what decides this
one. **The discriminator is the legend, not the punctuation.** Read your own cell back cold: where
you cannot say what a part is, the keys were doing work, so put them back. **A separator between
repeats of one shape is always wrong** — `30d, 10d, 15d` packed as `30d;10d;15d` is a list the
notation already has.

**Decide this from the signature, before drafting columns.** Where a parameter is an object with
several optional fields, it is one column — never one column per field. Deciding after the table
is drafted is too late: by then every field has a column, most rows carry a blank or a `false`, and
the sea of near-empty cells reads as deliberate.

**A table that moves one part of the value still gets the one column.** The trigger is the
parameter's shape, not what this table varies: if the signature takes one object, or one collection,
the column holds that whole value, however few of its parts this table touches. Three dimensions
reaching the method as a `List<Integer>` are one `[10, 10, 10]` cell in a table that moves only the
first of them. For an object, write `[:]` where it is empty and one key where the row sets
something — not a blank cell, which becomes `null` and never reaches the converter at all:

```
Scenario           | Config          | Timeout Used?
All defaults       | [:]             | 3000
Explicit timeout   | [timeout: 5000] | 5000
```

**"Each table exercises a different field, so a map is overkill" is the reasoning to reject.** One
object split across per-table columns gets a different column vocabulary in every table and its
construction moves into the method body, which *Custom Type Converters* rules out whatever the
construction idiom — a one-line constructor is still construction. The exception stated there is
mechanical and narrow: it is for a value the code under test has **no type** for, not for a type
whose assembly looks easy enough to inline.

**The one field that does earn its own column is one you need a value set on.** Value sets expand per
column only, so `{X, Y}` inside a map cell adds no rows — see *What the Notation Cannot Express*.
That is a limit of the notation, and it is the only reason on this page to break an object apart.
Ease of construction is not one.

**Leave out what this table says nothing about.** An object with twelve properties whose rule reads
two is a two-part cell, and the converter supplies valid values for the rest. The only field that may
not leave is one some surface makes a claim about — see *Assume the Table Is Published*.

```java
@TableTest("""
    Scenario                          | Donations So Far                     | Deferred?
    First-time donor                  | []                                   | no
    Last day inside the plasma window | [[component: plasma, days: 13]]      | yes
    First day past it                 | [[component: plasma, days: 14]]      | no
    Whole blood defers for longer     | [[component: whole blood, days: 14]] | yes
    """)
void defersDonorInsideAnyWindow(List<Donation> donationsSoFar, boolean deferred) {
    assertEquals(deferred, deferralPolicy.isDeferred(donationsSoFar));
}

@TypeConverter
public static Donation toDonation(Map<String, String> fields) {
    return new Donation(Component.valueOf(fields.get("component").toUpperCase()),
                        Integer.parseInt(fields.get("days")));
}
```

The converter takes **one element's map**, not the whole list — conversion recurses into the
collection and calls it per element (see *Built-in Value Conversion*).

For the one-object case the same converter takes the map directly, and supplies the defaults:

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

The "nothing set" row is **`[:]`, not a blank cell** — a blank bypasses the converter entirely (see
*Handling Null Values*). And **the converter returns the domain object, not the map**: declaring the
parameter `Map<String, String>` and building the object with a private helper leaves construction in
the test, which is the thing the converter exists to remove.

**Choose the parts from what the table has to say, not from what the converter needs.** A field the
`@Description` makes a claim about has to be visible, and so does a unit finer than the one the cell
carries. See *Assume the Table Is Published*.

**Every shape here needs a type of its own.** Converters dispatch on return type, one per erased type
per class (see *One Converter Per Target Type*), so an object written as a plain value needs a domain
type rather than `String` — and two objects both written plainly need two types. **That constraint is
what makes the simplification available**, not merely a limit on it: the type you declare to hold
`2 x 5 mg tablet` is the same type that lets the cell be that short.

**Last resort — several columns for one object.** Where no domain type exists to pin a converter to,
spread the object across columns and assemble it in the method. It costs exactly what the converter
was buying: the arrangement moves back between the reader and the rule. Take it only when the type is
genuinely absent, and **say so in the `@Description`** — *"there is no order-line type, so the columns
carry its parts and the method assembles them"*. A missing type is usually a gap in the code under
test rather than in the table, and whether it gets closed there or bridged here is the reader's
decision; it cannot be, while the gap is invisible. Same move as naming a seam you may not add — see
*Separate Rules from Arithmetic*.

**An object with two collection-shaped parts is not the last-resort case.** A converter takes one
cell, so an object holding, say, a stock map and a set of product groups cannot be built from two
columns — and that is a fact about converters, not a missing type. **Ask which part this table
varies.** That part is the cell and the converter builds the object from it; the parts no row varies
are fixed in the test method and declared in the `@Description`, which is what *Leave out what this
table says nothing about* asks one level up. Do not deepen the cell to fit both parts in either: a
third level of nesting has no legend the reader can follow.

```java
@Description("""
    Zones G3 and G4 are one linked pair in every row; the rows vary which boiler stocks capacity.
    """)
@TableTest("""
    Scenario                     | Boiler Capacity (kW)             | Heated Together?
    One boiler serves both zones | [B1: [G3: 12, G4: 12]]           | true
    Neither boiler serves both   | [B1: [G3: 12], B2: [G4: 12]]     | false
    """)
void heatsLinkedZonesFromOneBoilerWhenItCan(BoilerCapacity capacity, boolean heatedTogether) {
    Site site = new Site(zones(), Set.of("G3", "G4"));   // held for every row, and declared above
    assertEquals(heatedTogether, scheduler.plan(site, capacity).isSingleBoiler());
}
```

**Fixing a held part in the method is not the construction the converter rule forbids.** That rule is
about what a *column* feeds: a column's value reaches the method as its domain type, never as fields
the body assembles. A part no row varies has no column to feed it. **The violation is a helper that
takes two varying column values and returns one object** — if you are writing one, one of those two
columns is not this table's concern.

### What the Notation Cannot Express

Four limits worth knowing before you design around them, because each is otherwise found by a
failing build or by a table that will not come out right:

- **Value-set members are separated by commas**, so a member containing one has to be quoted:
  `{"a,b", c}`.
- **A value set expands per column only.** `{a, b}` written *inside* a map or list cell is a `Set`
  value in that position and produces no extra rows — expansion tests one whole cell against the
  column's parameter type. A set of whole composites (`{[k: v], [k: v]}`) does expand, but it repeats
  the composite once per member to vary one part of it; reshape the column instead — see *Give Each
  Obligation Exactly One Row*.
- **A collection cell cannot hold a null element.** Blank the whole cell to get a null collection.
- **One converter per target type, per class** — see *One Converter Per Target Type*, which is what
  forces several tables in one class onto one cell format for a shared domain type.

**A map column is a column decision, not a table decision.** Choosing a map for one parameter says
nothing about where the concern boundary lies, and it must not become the boundary. If another input
drives the same rule to the same output column, it is another column in the same table — not a table
of its own. A vent position driven by the measured humidity and a vent position driven by the
configured climate overrides are one concern with one output: one table, a humidity column beside the
overrides map column. Splitting them because one input arrives as a map and the other does not is the
over-split described under Decompose When You See These Signs.

The map keeps the table compact, each row states only what differs from the defaults, and all construction and defaulting logic lives in the converter — never in the test method body. This applies however the object is normally built (constructor, setters, or builder), and even when a table exercises only one or two of the optional fields: if a method body news up a parameter object and mutates it, that construction belongs in a `@TypeConverter` behind a map column.

## Workflow

**Budget your reasoning.** If concerns are already listed in the prompt, use them directly — don't re-derive what's already stated. If you find yourself re-analyzing the same concern, stop and write code. Working code you can revise beats perfect analysis that times out.

**Write incrementally.** For multi-concern features, write one `@TableTest` method at a time using the Write tool. Don't attempt to generate the entire test class in a single response — each method written is a checkpoint that can't be lost to a timeout.

**Align the table before you finish.** Run

```
${CLAUDE_PLUGIN_ROOT}/skills/tabletest/scripts/format-table.sh <file>
```

It pads the columns and lines up the pipes; it ships with this skill, and that variable is the only
reliable way to reach it — the path is not relative to your working directory. It also tells you
whether a table parses at all — see *Checking a Table Parses Without Running the Build* below.

### Converting Existing Tests

1. Identify tests with identical structure but different data.
2. Extract the varying parts as columns (inputs and expected values). Wherever the originals build a domain object — constructor, setters, builder, static factory, `with…` chain — that construction moves into a `@TypeConverter` and the parameter becomes the domain type; it never stays in the method body. Where the object has several optional fields, the column that feeds the converter is a map (see Putting a Composite Value in a Cell).
3. Create table with scenario column first, inputs next, expectations last (suffix with `?`).
4. Align method parameters to column order; do not bind the scenario column unless annotated with `@Scenario`.
5. Verify all rows use the same assertion logic.
6. Remove the original `@Test` methods the table now covers — run the tests before and after removal to confirm coverage is preserved.
7. When converting from another framework (Spock, Kotest, TestNG, JUnit 4), finish the migration: replace the old framework's assertion/matcher style (`shouldBe`, `expect:`, TestNG asserts) with the project's JUnit-compatible style, remove its imports, and remove its dependencies from the build file. Leftover matcher calls or a leftover build dependency both mean the conversion is incomplete.

### Writing New TableTest from a Feature Description

When there is no existing code (empty `src/main/java`), write the tests first — the table design drives the API shape. After the tests are written, add stub implementation code so they compile.

1. **Read the feature description** and identify the rules/concerns
2. **Write the test class** with `@TableTest` methods following the design principles in this skill
3. **Add stub implementation** — create the class and methods referenced by the tests with signatures only (return defaults, throw `UnsupportedOperationException`, etc.). Do not implement the logic unless specifically instructed. The user may want to iterate on the test design before committing to an implementation.

**Let tables drive the API decomposition.** Each concern's table should call a function whose parameters are exactly the table's input columns. If a test method needs a loop or helper to fabricate raw data so a derived input reaches a target value (e.g. generating n records so that a count equals n), the table is targeting too high in the stack — stub a narrower function that takes the derived value directly, and cover the raw-data derivation in its own table. Cheap rows are the sign of a well-placed table; when adding a row feels expensive, the API needs another seam.

**Ambiguity policy — deliver, don't ask.** Feature descriptions rarely answer every question. Choose the most reasonable interpretation, record it — along with any open question — in the affected table's `@Description`, and deliver complete tests. Never end the task with clarifying questions in place of tests: documented assumptions in delivered tests are how you raise them. This holds even if you have just read the spec-by-example skill — its clarify-first workshop style is for requirements discussions, not for a request to write tests.

### Writing New TableTest from Existing Code

1. **Understand**: Read the code, trace the logic, identify what varies
2. **Design**: Decide the concerns and each table's axis before writing a row
3. **Implement**: Create the full table with all scenarios
4. **Run immediately**: Get fast feedback on structure and conversions
5. **Refine**: Re-read the table once it passes and fix anything the rules above catch. **Do not defer naming to this step** — write domain names into the first draft, because a table you hand over is the draft someone reads.

**The ambiguity policy above applies here too.** Where the code leaves a question open — a boundary
it never reaches, a branch no caller exercises — choose the reasonable reading, record it in the
`@Description`, and deliver. Do not stop to get agreement on a mockup.

---

### Checking a Table Parses Without Running the Build

The formatter returns a table unchanged when it cannot parse it — silently, by design, so formatting
never breaks a build. That makes it a parse check if you give it something to change:

1. Knock one column out of alignment (add or drop a space before a `|`).
2. Run `${CLAUDE_PLUGIN_ROOT}/skills/tabletest/scripts/format-table.sh --check <file>`.
3. **Exit 1 — it parsed** and wants to realign. **Exit 0 — it did not parse**, so the table is
   malformed and the row it choked on is the one to look at.

**Read stderr before you trust an exit 0.** A missing formatter also exits 0, after printing
`WARNING: tabletest-formatter not found`. That warning means the check did not run — it is not a
verdict on your table.

Use it whenever you have invented a cell format, nested a collection, or quoted something you were
unsure about. It answers in under a second what a `gradle test` round answers in minutes.

## Quality Checks

**Table design** — the shared rules above, in checklist form:

<!-- BEGIN GENERATED table-design-checks — do not edit here; source is shared/table-design/ -->

- [ ] **One rule per table**: every row and column serves this table's one axis; a behaviour you cannot name without "and" has been split, and a rule that is a conjunction of independent conditions has one table per condition rather than their cross-product — but inputs that are contributions to one combined answer stay in one table, with rows that show them combining
- [ ] **Complete outputs**: all observable outputs of the same rule sit in one table, and every expectation column there is exercised by the rows that table varies — one constant down all rows, or moving only as a side effect of another, belongs to a different rule's table
- [ ] **Decomposed, not over-split**: no table mixes concerns (blank-throughout columns, qualified scenario names, two groups of expectation columns), no table carries both a value and the raw data another rule derives it from, and no set of same-fixture tables reports one expectation column that a family column would collapse
- [ ] **Combining tables prove an interaction**: any table exercising several rules together shows behaviour the single-rule tables cannot (a precedence, an ordering), not the earlier rules re-run end to end
- [ ] **Rules separated from arithmetic**: every expectation cell is predictable from its row in one step; a classification and the calculation that follows it are two tables
- [ ] **One row per obligation**: every obligation of the concern is discharged by some row, and every row discharges one no other row in that table reaches; where two rows share an expectation, swapping what differs between them would change an expectation cell in that table — not a value further past the same boundary, a larger n in the same direction, or an input the rule ignores even though it names it
- [ ] **Every tier once**: a tier ladder has one row per tier — all of them, none twice — and every boundary is exercised from both sides at the finest unit the rule distinguishes, whether by two rows or by a value set whose end members are the tier's own edges, middle tiers included, and a boundary an input reaches through a formula straddled like any other; where one ladder repeats across classes that share its boundary positions, the straddling pairs appear in one class and the rest carry one row per tier
- [ ] **Value set semantics**: value sets appear only where every value produces the same result, never as shorthand for "test several values"; an input this rule claims not to affect the outcome varies across the values it ignores, while an input another rule owns is held at one valid value
- [ ] **Stateful rows independent**: transition rows carry their own before-state and after-state; no row depends on another having run
- [ ] **Held constants declared**: every value the outcome depends on that the table fixes for all rows is a column where it can be one — always so for a threshold or limit the rule turns on — and otherwise named in the title or description as held fixed; never left only in the test body, a field, a conversion helper, or a comment; a value already shown as a column needs nothing further on any surface
- [ ] **Titles form an index**: read the titles as a sorted list — each states an action the code performs (not a label for a topic), one grammatical shape runs across them, and no three share an uninformative opener
- [ ] **No scenario name restates its own row's answer**: read each scenario name beside the expectation cells of that row — none states or paraphrases one of them, and none is a generic label
- [ ] **Expectation columns marked**: at least one column uses the `?` suffix (never a prefix), no input column does, and a compound result stays a native collection rather than a flattened string
- [ ] **Rejection expressed as data**: rejected rows carry the error type or reason in an expectation column, never a hardcoded outcome in the body; accepted and rejected rows share a table only where striking the rejected rows would leave a table stating nothing, and no row branches the assertion
- [ ] **Concrete values**: expectation values are literal domain values traceable to the input columns of their own row — not abstract codes, and not hidden behind named constants
- [ ] **Domain language**: column names use the business vocabulary, not parameter names, field names or internal API terms
- [ ] **Thresholds visible**: a rule that depends on a threshold or limit shows it as a column, with boundary rows at and just past it
- [ ] **Traceability columns**: an intermediate expectation appears only where the value is observable from the public API — never reimplemented from internal logic; if a formula would have to be reimplemented to fill it, decompose instead
- [ ] **Blank means absent**: a column whose input is genuinely absent for a row uses a blank cell, not 0 or a default; an input that is present but irrelevant is a value set instead, and nothing converts a blank to a default on the way in
- [ ] **Black-box design**: columns represent observable inputs and outputs, not internal flags or implementation details
- [ ] **Consistent across tables**: within one method set, one concept has one column name, one kind of value has one notation, one failure has one spelling, and the rows are served by shared helpers rather than per-table copies

<!-- END GENERATED table-design-checks -->

**TableTest mechanics** — what a `@TableTest` needs beyond a well-designed table:

- [ ] **Multiple rows**: table has 2+ rows; use `@Test` only for a genuinely standalone single case — a lone error, null, or empty-input case related to an existing table belongs in that table as a row (with a `Throws?` column if it throws), not in a separate `@Test`. **This is about `@Test` methods, not tables:** it never overrides *Model Rejection as an Expected Column*, so where the strike test says two tables, it is two tables
- [ ] **Uniform assertions**: all rows use the same assertion logic; split into separate TableTests if logic differs per row
- [ ] **Straightforward method**: no `if`/`switch`/ternary — not even null-guards or defaulting, which belong in a `@TypeConverter` or helper; the method only arranges, acts, and asserts
- [ ] **Parameter alignment**: parameters match data columns left-to-right (excluding scenario column)
- [ ] **Parameter conversion**: custom type converter methods (annotated `@TypeConverter`) or JUnit converters handle type conversion, keeping the test method free of parsing code
- [ ] **Valid syntax**: values requiring quotes are quoted, collections use correct bracket syntax, empty collections are explicit (`[]`, `{}`, `[:]`)
- [ ] **Boxed types for absent inputs**: a column that can be blank uses `Integer`/`Long`/`Boolean`, not a primitive
- [ ] **Correct expected values**: arithmetic in expected columns verified independently; every row's output matches the stated rules
- [ ] **Exception column**: error/rejection tables have a `Throws?` or `Exception?` column, not hardcoded exception classes in the method body
- [ ] **@Description free of internals**: no internal formula or algorithm; a reader must not be able to recompute the expectation cells from the description alone
- [ ] **@Description adds information**: if present, `@Description` provides context beyond what the table shows (fixed values, domain context, open questions) — not a restatement of columns or rows. Omit `@Description` if there is nothing to add.
- [ ] **@Description uses text block**: `@Description` uses `"""` text blocks, not string concatenation with `+`
- [ ] **Annotation order**: `@DisplayName` → `@Description` → `@TableTest` (no other order)
- [ ] **Test helpers organized**: helper classes placed at bottom of test file with clear names (`QueryCounter`, not `Helper`); only extract to separate file when reused across test classes
- [ ] **Old framework removed** (conversions only): no imports, matcher/assertion calls, or build-file dependencies from the framework being replaced

## Advanced References

This skill file is complete for standard tables — do not read references speculatively. **Every condition below is something you can see in your own task before opening the file**: a type you must convert, an annotation you must use, a shape the code under test has. A reference you would have to *judge* you need is one you do not need.

| Reference                                | When to use                                                                 |
|------------------------------------------|-----------------------------------------------------------------------------|
| `references/type-converters.md`          | Converting wrapper types (`Optional`, `Result`/`Either`); converter methods with defaults; converter not being discovered (search order); an expectation that is an escape code, Base64 or raw bytes |
| `references/async-and-positional-apis.md`| The code hands work to another thread that the assertions depend on; output fields are positional (`master`/`other`) and which system fills which depends on configuration |
| `references/large-tables.md`             | Table needs comments, grouping, or external table files                     |
| `references/provided-parameters.md`      | Using `@TempDir` or other JUnit-injected parameters                         |
