# Advanced Table Design

## Orthogonal Concerns - When NOT to Combine

When two features are independent (orthogonal), don't cross-multiply them in one table:

**Orthogonal features:**
- Don't affect each other's behavior
- Can be tested separately
- Combining them creates explosion of rows without adding insight

**Example:**
```java
// Routing logic (primary selection, dual dispatch, fallback)
@TableTest("""
    Scenario           | Primary | Dual Dispatch | Fallback | Response?
    Primary in prod    | true    | false         | false    | primary
    ...
    """)
void routes_requests(...)  // reporting=false for all rows

// Reporting logic (what gets reported, when)
@Test
void reports_when_enabled(...) // reporting=true

@Test
void does_not_report_when_disabled(...) // tests across scenarios
```

Routing works the same whether reporting is on or off. Testing all routing scenarios x 2 reporting states adds 100% more rows for 0% more insight about routing behavior.

**Red flags for orthogonal concerns:**
- One column could be `{true, false}` for every single row
- Column doesn't affect any expectation columns
- "We should test X with and without Y" - ask if Y changes X's behavior

## Evolving Scenario Names as Table Grows

Scenario names should evolve as you add rows. Early names may be adequate initially but become unclear when similar scenarios are added. Refine names to highlight what differentiates each scenario.

**Initial table** (2 scenarios):
```java
@TableTest("""
    Scenario                 | Primary | Secondary | Response?
    Secondary fails          | OK      | ERROR     | OK
    Primary fails            | ERROR   | OK        | OK
    Both fail                | ERROR   | ERROR     | ERROR
    """)
```
Names are clear because they're the only failure scenarios.

**Adding fallback scenarios** (4 scenarios):
```java
@TableTest("""
    Scenario                       | Primary | Secondary | Fallback Enabled | Response?
    Secondary fails                | OK      | ERROR     | true             | OK
    Primary fails                  | ERROR   | OK        | true             | OK
    Secondary fails, no fallback   | OK      | ERROR     | false            | ERROR
    Primary fails, no fallback     | ERROR   | OK        | false            | ERROR
    """)
```
Adding ", no fallback" suffix clarifies the first two have fallback enabled.

**Adding both-fail scenarios** (6 scenarios):
```java
@TableTest("""
    Scenario                       | Primary | Secondary | Fallback Enabled | Response?
    Secondary fails, fallback ok   | OK      | ERROR     | true             | OK
    Primary fails, fallback ok     | ERROR   | OK        | true             | OK
    Secondary fails, no fallback   | OK      | ERROR     | false            | ERROR
    Primary fails, no fallback     | ERROR   | OK        | false            | ERROR
    Primary and fallback fail      | ERROR   | ERROR     | true             | ERROR
    Secondary and fallback fail    | ERROR   | ERROR     | true             | ERROR
    """)
```
Changed "Secondary fails" to "Secondary fails, fallback ok" to distinguish from "Primary and fallback fail".

**Naming patterns that clarify differences:**

1. **Condition qualifiers**: "fails, fallback ok" vs "and fallback fail" — these name which inputs are in which state, never what the table answers
2. **Explicit absence**: "no fallback" makes contrast clear
3. **Compound conditions**: "Primary and fallback fail" shows both parts fail

**Red flags for unclear names:**

- Multiple scenarios with identical names (ambiguous when test fails)
- Generic names like "Test 1", "Case A" (meaningless in failure messages)
- Names describing outcome rather than condition ("Returns error" vs "Invalid input")
- Names too similar to distinguish ("Fails" vs "Fails with error" - what's the difference?)

**Evolution trigger:** When you add a new scenario and find yourself confused which existing scenario is which, that's the signal to refine all related scenario names.

## Collapse a Sparse Column Before Splitting the Table

A column blank for more than half its rows is a signal, but the repair is a **column** decision
first. Ask what the sparse columns feed:

- **The same output column.** They are members of one family, so they belong in one column — a map
  column keyed by member (main skill file, *Collapse Sparse Columns into a Map*). Splitting instead
  gives you several tables that fix the same setup and report the same output, which is the
  over-split under *Decompose When You See These Signs*.
- **A different output column.** Different concerns; separate tables.

**Example:** a resolver supports four sources — configured value, JUnit property, properties file,
and a built-in fallback. A column each leaves most rows blank in most of them:

```java
@TableTest("""
    Scenario                 | Configured Dir | JUnit Property | Properties Value | Resolved Dir?
    Configured value set     | my-config      | report/junit   |                  | my-config
    Only the JUnit property  |                | report/junit   |                  | report/junit
    No source set            |                |                |                  | build/junit-jupiter
    Only the properties file |                |                | props/reports    | props/reports
    """)
```

Every row answers one question — which source wins — so the sources are one column:

```java
@TableTest("""
    Scenario                 | Sources                                      | Resolved Dir?
    Configured value set     | [configured: my-config, junit: report/junit] | my-config
    Only the JUnit property  | [junit: report/junit]                        | report/junit
    Only the properties file | [properties: props/reports]                  | props/reports
    No source set            | [:]                                          | build/junit-jupiter
    """)
void resolvesFromTheHighestPrioritySource(Map<String, String> sources, String resolvedDir) { ... }
```

Each row names which sources are set, the precedence reads down the `Resolved Dir?` column, and a
fifth source adds a row rather than a column.

The parameter is a `Map` here because the resolver takes the source map itself. Where a map column
stands in for a domain object, the `@TypeConverter` returns **that object** and the parameter is its
type — main skill file, *Collapse Sparse Columns into a Map*.

**A second table is right when the output differs.** If the properties file also decided *when* the
directory is re-read, that is a second output of a second rule, and it belongs in its own table with
its own expectation column — not as a fifth source in this one.

This is different again from orthogonal concerns above, where the features do not affect each other
at all. Here they answer the same question, which is why they collapse rather than split.
