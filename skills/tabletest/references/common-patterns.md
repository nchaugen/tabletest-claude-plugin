# Common TableTest Patterns

This guide documents patterns that emerge when testing real-world systems with TableTest. These patterns solve common design challenges and improve table readability.

## Table of Contents
- [Pattern: Consolidating Identity + Status](#pattern-consolidating-identity--status)
- [Pattern: Relative Positions](#pattern-relative-positions)
- [Pattern: Production Constants in Tables](#pattern-production-constants-in-tables)
- [Pattern: Timing Thresholds with Upper-Bound Assertions](#pattern-timing-thresholds-with-upper-bound-assertions)
- [Pattern: Async Execution in Multi-Concern Tables](#pattern-async-execution-in-multi-concern-tables)
- [Pattern: Test Helpers for Observing Behavior](#pattern-test-helpers-for-observing-behavior)
- [Pattern: Recording Sequences with Controlled Stopping](#pattern-recording-sequences-with-controlled-stopping)
- [Pattern: Composed Keys Stay Legible with Short Real Values](#pattern-composed-keys-stay-legible-with-short-real-values)
- [Pattern: A Type for Values a Table Cannot Show](#pattern-a-type-for-values-a-table-cannot-show)
- [Summary](#summary)

## Pattern: Consolidating Identity + Status

### Problem
When **both identity AND status** vary together in the same output position, separate columns force cross-referencing.

### NOT This Pattern
If identity is fixed per row and only status varies, that's normal column design:

```java
@TableTest("""
    Scenario          | Responder | Result?  |
    Alice succeeds    | Alice     | SUCCESS  |
    Bob fails         | Bob       | FAILURE  |
    Service error     | Service   | ERROR    |
    """)
```

This is fine! `Responder` is stable in each row; only `Result?` varies. No consolidation needed.

### When to Consolidate
Consolidate when **both identity AND status vary in the same column**.

**Example: Positional outputs**
```java
@TableTest("""
    Scenario                         | Primary Is Master | Master Response?   | Other Response?     |
    Primary master, both ok          | true              | Primary OK         | Secondary OK        |
    Primary master, secondary fails  | true              | Primary OK         | Secondary ERROR     |
    Secondary master, both ok        | false             | Secondary OK       | Primary OK          |
    Secondary master, primary fails  | false             | Secondary OK       | Primary ERROR       |
    """)
```

In `Master Response?` column:
- **Identity varies**: Could be Primary or Secondary (depends on `Primary Is Master`)
- **Status varies**: Could be OK or ERROR

Both pieces of information vary together in the same position, so consolidate them.

**Without consolidation (harder to read):**
```java
| Master Is? | Master Success? | Master Error? | Other Is?  | Other Success? | Other Error? |
| Primary    | yes             |               | Secondary  | yes            |              |
| Primary    | yes             |               | Secondary  |                | yes          |
| Secondary  | yes             |               | Primary    | yes            |              |
```

Requires 6 columns and cross-referencing to understand "Primary succeeded" or "Secondary failed."

### Format
`"<Identity> <Status>"` where both identity and status vary:
- `Primary OK` / `Primary ERROR` / `Secondary OK` / `Secondary ERROR`
- `Cache HIT` / `Database MISS` / `Service TIMEOUT`
- `Master SUCCESS` / `Fallback SUCCESS` / `Master FAILURE`

### Implementation

**Custom converter method for parsing:**
```java
@TypeConverter
public static ServiceResponse parseServiceResponse(String value) {
    if (value.isBlank()) return null;

    return switch (value.trim()) {
        case "Primary OK" -> ServiceResponse.PRIMARY_OK;
        case "Primary ERROR" -> ServiceResponse.PRIMARY_ERROR;
        case "Secondary OK" -> ServiceResponse.SECONDARY_OK;
        case "Secondary ERROR" -> ServiceResponse.SECONDARY_ERROR;
        case "NOT_IMPLEMENTED" -> ServiceResponse.NOT_IMPLEMENTED;
        default -> throw new IllegalArgumentException("Unknown response: " + value);
    };
}

public enum ServiceResponse {
    PRIMARY_OK,
    PRIMARY_ERROR,
    SECONDARY_OK,
    SECONDARY_ERROR,
    NOT_IMPLEMENTED
}
```

**Assertion helper:**
```java
private void assertServiceResponse(boolean isMaster, ServiceResponse expected, Report report) {
    Object actualResponse = isMaster ? report.masterResponse() : report.otherResponse();
    String actualError = isMaster ? report.masterError() : report.otherError();

    switch (expected) {
        case PRIMARY_OK -> {
            assertEquals("Primary", actualResponse);
            assertNull(actualError);
        }
        case PRIMARY_ERROR -> {
            assertNull(actualResponse);
            assertNotNull(actualError);
            assertTrue(actualError.contains("Primary system error"));
        }
        // ... other cases
    }
}
```

### When to Use

Use consolidation when:
- ✅ Both identity AND status vary in the same output position
- ✅ They form natural combinations (responder + outcome)
- ✅ Position is role-based (master/other, primary/secondary, first/second)

Don't consolidate when:
- ❌ Identity is fixed per row (use separate columns)
- ❌ Identity and status are independent (no natural pairing)
- ❌ Only one of the two varies

### Benefits
- **One cell = complete story**: No cross-referencing needed
- **Fewer columns**: 2 consolidated vs 6 separate
- **Natural language**: Reads like "Primary succeeded" or "Secondary failed"
- **Clearer intent**: Combinations are explicit, not inferred

---

## Pattern: Relative Positions

### Problem
System uses positional fields (first/second, master/other, primary/secondary) that vary based on configuration.

**Example:** A report has `masterResponse` and `otherResponse` fields. Which system is "master" depends on a configuration flag.

### Solution
Use fixed identities (Primary, Secondary) in input columns, relative roles (Master, Other) in expectation columns, and map between them in test logic.

```java
@TableTest("""
    Scenario                 | Primary Is Master | Primary | Secondary | Master Response? | Other Response? |
    Primary is master        | true              | OK      | ERROR     | Primary OK       | Secondary ERROR |
    Secondary is master      | false             | OK      | ERROR     | Secondary OK     | Primary ERROR   |
    """)
void reports_with_positional_fields(
    boolean primaryIsMaster,
    String primaryStatus,
    String secondaryStatus,
    ServiceResponse masterResponse,
    ServiceResponse otherResponse
) {
    // ... execute system

    // Verify master position (isMaster=true)
    assertServiceResponse(true, masterResponse, report);

    // Verify other position (isMaster=false)
    assertServiceResponse(false, otherResponse, report);
}

private void assertServiceResponse(boolean isMaster, ServiceResponse expected, Report report) {
    Object actualResponse = isMaster ? report.masterResponse() : report.otherResponse();
    String actualError = isMaster ? report.masterError() : report.otherError();

    // ... assertions based on expected
}
```

### When to Use

Use this pattern when:
- Report/output uses positional fields (first/second, master/other)
- Position depends on runtime configuration
- Same test scenarios apply regardless of which is master

### Benefits
- **Input columns stable**: Fixed system names (Primary, Secondary)
- **Expectation columns match API**: Report fields (master, other)
- **Test logic handles mapping**: Avoids duplicating scenarios
- **Table reads naturally**: "When Primary is master, master response is Primary OK"

---

## Pattern: Production Constants in Tables

### Problem
Tests create abstractions (test-only enums, simplified labels) that hide actual system behavior.

**Bad:**
```java
| Other Response? |
| NO_ROUTE        |  // Test-only abbreviation
```

**Good:**
```java
| Other Response?   |
| NOT_IMPLEMENTED   |  // Actual production constant
```

### Guidance

**Prefer production constants** when they're part of the observable contract:
- Sentinel values: `NOT_IMPLEMENTED`, `NOT_FOUND`, `UNSET`
- Enum values: `Status.PENDING`, `Role.ADMIN`
- Special strings: `"<empty>"`, `"N/A"`

**Why:**
- Table matches actual behavior exactly
- Reader sees what system really produces
- No need to learn test-specific vocabulary
- Documents the public contract

**When to create test abstractions:**
- Value is too verbose for table: `VeryLongProductionConstantName` → `LONG_NAME`
- Value varies by environment: database IDs, timestamps
- Combining multiple values: `ServiceResponse.PRIMARY_OK` instead of checking two fields separately

---

## Pattern: Timing Thresholds with Upper-Bound Assertions

### Problem
Tests verify timing but use either vague assertions (`>= 0`, `not null`) or complex range assertions that are still flaky.

### Solution
Use upper-bound assertions with `<` notation to express "completes within X milliseconds."

```java
@TableTest("""
    Scenario          | Primary ms | Secondary ms | Master ms? | Other ms? |
    Both fast         | 10         | 60           | <50        | <100      |
    Master slow       | 100        | 10           | <150       | <50       |
    Not implemented   | 10         |              | <50        |           |
    """)
void records_response_times(
    Long primaryMs,
    Long secondaryMs,
    Long expectedMasterMs,
    Long expectedOtherMs
) {
    // ... execute with timing

    assertRespondedWithin(expectedMasterMs, report.masterResponseTime());
    assertRespondedWithin(expectedOtherMs, report.otherResponseTime());
}

// with the other helpers, at the bottom of the class — a blank budget means the route
// was never called, so there is no duration to bound
private static void assertRespondedWithin(Long budgetMs, Duration actual) {
    assertEquals(budgetMs == null, actual == null, "tracked-ness does not match the budget cell");
    if (budgetMs == null) return;
    assertTrue(actual.toMillis() < budgetMs,
        String.format("Expected within %dms but was %dms", budgetMs, actual.toMillis()));
}

// parseResponseTime (handles the <50 format) is in SKILL.md § Domain-Specific Formatting
```

### Operation Times vs Assertion Thresholds

**Key insight:** Separate the actual operation timing from the assertion threshold.

- **Operation times** (10ms, 60ms, 100ms): Concrete delays in test
  - Must have clear separation for disambiguation
  - Example: 10ms vs 60ms (not 10ms vs 15ms)

- **Assertion thresholds** (<50ms, <100ms, <150ms): Observable behavior
  - Must prove which operation completed
  - Must include buffer for test overhead
  - Example: 10ms operation → <50ms threshold (40ms buffer)

```java
@TableTest("""
    Scenario          | Primary ms | Secondary ms | Response ms? |
    Primary fast      | 10         | 100          | <50          |  // Proves primary (10+buffer < 50 < 100)
    Secondary fast    | 100        | 10           | <50          |  // Proves secondary (10+buffer < 50 < 100)
    Both slow         | 60         | 100          | <150         |  // Threshold tracks the operation times
    """)
```

### When to Use

Use this pattern when:
- System tracks execution time / duration
- Test controls timing via delays/sleeps
- Want to verify timing is tracked correctly
- Testing performance thresholds or SLOs

### Benefits
- **Traceability**: Expected times traceable to input times
- **Simple logic**: `actualMs < expectedMs` instead of range checks
- **Clear semantics**: "completes within" rather than "approximately"
- **More robust**: Tolerates timing variations below threshold
- **Documents timing contract**: What gets measured, when
- **Blank cells for null**: Clear when timing not tracked

### Comparison with Range Assertions

**Range assertion** (old pattern):
```java
assertTrue(actualMs >= expectedMs - 5 && actualMs <= expectedMs + 50);
// Complex logic, tests "approximately X ms"
```

**Upper-bound assertion** (preferred):
```java
assertTrue(actualMs < expectedMs);
// Simple logic, tests "within X ms"
```

**A range still has one use**: checking a system's *own recorded* duration against the real one —
"the log says 10ms, verify it actually took 10ms give or take". That is a different rule from
"completes within X", and it is the only one a range states better.

---

## Pattern: Waiting for Async Work Before Asserting

### Problem
The system finishes its async work after the assertions have already run, so the table fails
intermittently or passes for the wrong reason.

### Solution
Gate the assertions on a `CountDownLatch` sized for the row. **Sizing it is arrangement, so it
belongs in a helper** — a `?:` in the method body is a rule the table cannot show, and this one is
not even about the behaviour under test.

```java
@TableTest("""
    Scenario           | Dual Dispatch | Master | Reports Sent?
    Single dispatch    | false         | OK     | 0
    Dual dispatch      | true          | OK     | 1
    Master failed      | true          | ERROR  | 0
    """)
void reportsEventsWhenDispatchedToBoth(
    boolean dualDispatch,
    String masterStatus,
    int reportsSent
) {
    CountDownLatch asyncLatch = latchFor(dualDispatch, masterStatus);
    Executor asyncExecutor = task -> new Thread(() -> {
        task.run();
        asyncLatch.countDown();
    }).start();

    // ... execute system

    assertTrue(asyncLatch.await(5, TimeUnit.SECONDS));
    verify(reporter, times(reportsSent)).report(any());
}

// with the other helpers, at the bottom of the class — the secondary is skipped when the
// master fails without fallback, so waiting on a latch it will never count down times out
private static CountDownLatch latchFor(boolean dualDispatch, String masterStatus) {
    boolean asyncWillExecute = dualDispatch && !"ERROR".equals(masterStatus);
    return new CountDownLatch(asyncWillExecute ? 1 : 0);
}
```

**A count, not a flag.** `verify(reporter, times(reportsSent))` asserts every row with one call;
branching between `verifyNoInteractions` and `verify(times(1))` puts the rule back in the body.

### When to Use

Use this pattern when:
- The system does work on another thread that the assertions depend on
- Whether that work happens at all varies by row

**One concern still means one table.** This is a technique for waiting, not a licence to test routing
and reporting together — if you cannot name the behaviour without "and", it is two tables (SKILL.md
§ Decompose When You See These Signs).

---

## Pattern: Test Helpers for Observing Behavior

### Problem
Need to observe behavior beyond return values: query counts, call sequences, invocation order, side effects.

### Solution
Create focused test helper classes that spy on or record behavior during test execution.

**Common helper types:**

1. **Counter** - Counts invocations
2. **Recorder** - Records sequence of calls
3. **Capture** - Captures argument values
4. **Spy** - Wraps real implementation with observation

### Example: Query Counter

```java
@TableTest("""
    Scenario        | Feature Toggles                | Query Count? | Result?
    Specific match  | [org-search-v2-cust1: true]    | 1            | true
    Wild customer   | [org-search-v2-*: true]        | 2            | true
    Not found       | [:]                            | 12           | empty
    """)
void finds_feature_toggles(
    Map<String, Boolean> toggles,
    int expectedQueryCount,
    Optional<Boolean> result
) {
    QueryCounter counter = new QueryCounter(toggles);

    Optional<Boolean> actual = resolver.find(counter::resolve);

    assertEquals(result, actual);
    assertEquals(expectedQueryCount, counter.queryCount);
}

private static class QueryCounter {
    private final Map<String, Boolean> registered;
    private int queryCount = 0;

    QueryCounter(Map<String, Boolean> registered) {
        this.registered = registered;
    }

    Boolean resolve(String key) {
        queryCount++;
        return registered.get(key);
    }
}
```

### Example: Sequence Recorder

```java
@TableTest("""
    Scenario        | Expected Queries?
    Specific        | [org-search-v2-cust1]
    Wild customer   | [org-search-v2-cust1, org-search-v2-*]
    """)
void queries_in_precedence_order(List<String> expectedQueries) {
    QueryRecorder recorder = new QueryRecorder(expectedQueries);

    resolver.find(recorder::resolve);

    assertEquals(expectedQueries, recorder.queries);
}

private static class QueryRecorder {
    private final List<String> queries = new ArrayList<>();
    private final List<String> expectedQueries;

    QueryRecorder(List<String> expectedQueries) {
        this.expectedQueries = expectedQueries;
    }

    Boolean resolve(String key) {
        queries.add(key);
        // Stop when we've reached the last expected query
        if (queries.size() == expectedQueries.size()) {
            return true;
        }
        return null;
    }
}
```

**Key insight**: The recorder needs to know when to stop. Pass expected list so it can signal completion.

### Helper Class Guidelines

**Organization:**
- Place at bottom of test class after all test methods
- Name clearly: `QueryCounter`, `QueryRecorder` (not `Helper`, `Utils`)
- Keep simple: one focused responsibility per helper

**When to create:**
- After table design reveals what needs observing
- Don't design helpers upfront - let table drive the need
- Extract to separate file only when reused across test classes

**Naming patterns:**
- `*Counter` - counts something
- `*Recorder` - records a sequence
- `*Capture` - captures argument values
- `*Spy` - wraps with observation

### When to Use
- Testing query precedence and fallback behavior
- Verifying call order in async systems
- Counting invocations for rate limiting
- Capturing arguments for validation

**Not converter methods**: These are test infrastructure, not `@TypeConverter` methods. They observe behavior during test execution.

---

## Pattern: Recording Sequences with Controlled Stopping

### Problem
When recording call sequences, the recorder doesn't know when to stop and records all attempts instead of stopping at the match.

**Example failure:**
```
expected: <[org-search-v2-cust1]>
but was:  <[org-search-v2-cust1, org-search-v2-*, org-*-v2-*, *-*-*-*]>
```

The system continues searching after finding a match because the recorder always returns `null`.

### Solution
Pass the expected sequence to the recorder so it knows when to signal completion.

```java
private static class QueryRecorder {
    private final List<String> queries = new ArrayList<>();
    private final List<String> expectedQueries;

    QueryRecorder(List<String> expectedQueries) {
        this.expectedQueries = expectedQueries;
    }

    Boolean resolve(String key) {
        queries.add(key);
        // Stop when we've reached the last expected query
        if (queries.size() == expectedQueries.size()) {
            return true;  // Signal: stop searching
        }
        return null;  // Signal: keep searching
    }
}
```

**Why this works:**
- Recorder adds each query to the list
- When list reaches expected size, returns non-null (signals match found)
- System stops searching
- Recorded sequence matches expected sequence

### Alternative: Count-Based Stopping

If you only care about count, not exact sequence:

```java
private static class QueryRecorder {
    private final List<String> queries = new ArrayList<>();
    private final int expectedCount;

    QueryRecorder(int expectedCount) {
        this.expectedCount = expectedCount;
    }

    Boolean resolve(String key) {
        queries.add(key);
        if (queries.size() == expectedCount) {
            return true;  // Stop after N queries
        }
        return null;
    }
}
```

### Key Insight
The test failure showed what control was missing. Add the stopping condition that the failure message revealed.

---

## Pattern: Composed Keys Stay Legible with Short Real Values

### Problem
A system builds a lookup key out of several inputs, and the composed key is too long to scan in a
cell.

### Solution
Shorten the **values**, not the vocabulary: `acme:search:v2` is as scannable as a placeholder and
still says what each part is.

```java
@TableTest("""
    Scenario               | Organisation | Feature | Version | Registered Toggles     | Found?
    Exact key registered   | acme         | search  | v2      | [acme:search:v2: true] | true
    Any version registered | acme         | search  | v2      | [acme:search:*: true]  | true
    Nothing registered     | acme         | search  | v2      | [:]                    | false
    """)
void findsToggleByExactKeyOrWildcard(String organisation, String feature, String version,
                                     Map<String, Boolean> registered, boolean found) { ... }
```

`*` stands for any value of that part, so the second row proves the wildcard entry is reached by a
lookup for a specific version.

**Single letters cost more than they save.** `O:F:V` needs a legend, and the legend lives outside the
table — the reader has to learn a private vocabulary before any row means anything (main skill file,
*Use Concrete Domain Values*). Column names take the same rule: `Organisation`, not `orgId`.

### When to Use
- Keys, cache entries, query strings or routes composed from several inputs
- Wildcard or precedence rules over those keys

---

## Pattern: A Type for Values a Table Cannot Show

### Problem
An expected value is encoding-specific — an ANSI escape, Base64, raw bytes — and putting it in a cell
destroys the table.

### Solution
Give the column a **type** whose constants carry the raw value. The table names the constant, and
nothing is translated in the test body:

```java
@TableTest("""
    Scenario      | Input     | Color?
    XML tag       | <root>    | CYAN
    XML attribute | id="x"    | GREEN
    XML value     | some text | YELLOW
    """)
void colorizesEachXmlTokenType(String input, AnsiColor color) {
    assertThat(colorizer.colorize(input)).contains(color.escape());
}

enum AnsiColor {
    CYAN("\u001B[36m"), GREEN("\u001B[32m"), YELLOW("\u001B[33m");

    private final String escape;

    AnsiColor(String escape) { this.escape = escape; }

    String escape() { return escape; }
}
```

JUnit converts an enum constant by name, so this needs no `@TypeConverter` at all.

**Do not translate in the method body.** `String expected = COLOR.get(colorName)` puts the mapping
where no reader of the published table can follow it — the cell then names something only the test
code can resolve, which is what *Use Concrete Domain Values* rules out.

**A `String` column with a converter does not work either.** Converters are chosen by parameter type,
so a `String → String` converter would rewrite the `Input` column too (main skill file,
*Domain-Specific Formatting*). The distinct type is what confines the conversion to one column.

### When to Use
- Encoding-specific expectations: escape codes, Base64, binary
- A small closed set of values shared across rows

---

## Summary

These patterns emerged from real-world TableTest usage. They solve common challenges:

1. **Consolidating Identity + Status**: When both vary together in same position
2. **Relative Positions**: Handle positional APIs with fixed + relative columns
3. **Production Constants**: Show actual system values in tables
4. **Timing with Concrete Milliseconds**: Verifiable, traceable timing
5. **Async Execution**: Wait for off-thread work before asserting
6. **Test Helpers**: Observe behavior beyond return values (counts, sequences, side effects)
7. **Recording Sequences**: Control stopping with expected sequences
8. **Composed Keys**: Short real values, never single-letter placeholders
9. **A Type for Unshowable Values**: An enum carries the escape code; the table names the constant

When facing similar challenges, consider these patterns before creating custom solutions.
