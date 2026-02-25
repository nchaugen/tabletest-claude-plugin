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
    """)
```
Names are clear because they're the only failure scenarios.

**Adding fallback scenarios** (4 scenarios):
```java
@TableTest("""
    Scenario                       | Primary | Secondary | Fallback? | Response?
    Secondary fails                | OK      | ERROR     | true      | OK
    Primary fails                  | ERROR   | OK        | true      | OK
    Secondary fails, no fallback   | OK      | ERROR     | false     | ERROR
    Primary fails, no fallback     | ERROR   | OK        | false     | ERROR
    """)
```
Adding ", no fallback" suffix clarifies the first two have fallback enabled.

**Adding both-fail scenarios** (6 scenarios):
```java
@TableTest("""
    Scenario                       | Primary | Secondary | Fallback? | Response?
    Secondary fails, fallback ok   | OK      | ERROR     | true      | OK
    Primary fails, fallback ok     | ERROR   | OK        | true      | OK
    Secondary fails, no fallback   | OK      | ERROR     | false     | ERROR
    Primary fails, no fallback     | ERROR   | OK        | false     | ERROR
    Primary and fallback fail      | ERROR   | ERROR     | true      | ERROR
    Secondary and fallback fail    | ERROR   | ERROR     | true      | ERROR
    """)
```
Changed "Secondary fails" to "Secondary fails, fallback ok" to distinguish from "Primary and fallback fail".

**Naming patterns that clarify differences:**

1. **Outcome qualifiers**: "fails, fallback ok" vs "and fallback fail"
2. **Explicit absence**: "no fallback" makes contrast clear
3. **Compound conditions**: "Primary and fallback fail" shows both parts fail

**Red flags for unclear names:**

- Multiple scenarios with identical names (ambiguous when test fails)
- Generic names like "Test 1", "Case A" (meaningless in failure messages)
- Names describing outcome rather than condition ("Returns error" vs "Invalid input")
- Names too similar to distinguish ("Fails" vs "Fails with error" - what's the difference?)

**Evolution trigger:** When you add a new scenario and find yourself confused which existing scenario is which, that's the signal to refine all related scenario names.
