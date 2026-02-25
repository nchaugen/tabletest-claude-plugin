# Common TableTest Patterns

This guide documents patterns that emerge when testing real-world systems with TableTest. These patterns solve common design challenges and improve table readability.

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
    if (value == null || value.isBlank()) return null;

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

    if (expectedMasterMs != null) {
        long actualMs = report.masterResponseTime().toMillis();
        assertTrue(actualMs < expectedMasterMs,
            String.format("Expected within %dms but was %dms", expectedMasterMs, actualMs));
    } else {
        assertNull(report.masterResponseTime());
    }
}

// See references/type-converters.md for parseResponseTime implementation (handles <50 format)
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

**See also:** `references/async-and-performance.md` for detailed timing assertion guidance.

---

## Pattern: Async Execution in Multi-Concern Tables

### Problem
Table tests both sync behavior (routing) and async behavior (reporting).

### Solution
Calculate async execution dynamically and use CountDownLatch.

```java
@TableTest("""
    Scenario         | Dual Dispatch | Master | Report Events | Report Sent? |
    Sync, no report  | false         | OK     | false         | false        |
    Async, report    | true          | OK     | true          | true         |
    """)
void combines_sync_and_async_concerns(
    boolean dualDispatch,
    String masterStatus,
    boolean reportEvents,
    boolean reportSent
) {
    // Calculate if async will execute
    boolean asyncWillExecute = dualDispatch && !"ERROR".equals(masterStatus);
    CountDownLatch asyncLatch = new CountDownLatch(asyncWillExecute ? 1 : 0);

    Executor asyncExecutor = task -> new Thread(() -> {
        task.run();
        asyncLatch.countDown();
    }).start();

    // ... execute system

    // Wait for async completion
    assertTrue(asyncLatch.await(5, TimeUnit.SECONDS));

    // Verify both sync and async outcomes
    if (!reportSent) {
        verifyNoInteractions(reporter);
    } else {
        verify(reporter, times(1)).report(any());
    }
}
```

### When to Use

Use this pattern when:
- Testing system with both sync and async behavior
- Single table covers multiple concerns (e.g., routing + reporting)
- Async execution depends on sync outcome

### Benefits
- **Single table**: Don't need separate sync/async tables
- **Reliable**: CountDownLatch ensures completion before assertions
- **Dynamic**: Async behavior adapts to scenario
- **Clear**: Each row specifies if async happens

**See also:** `references/async-and-performance.md` for more async testing patterns.

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

## Pattern: One-Letter Values for Composite Keys

### Problem
Testing systems that construct complex lookup keys from multiple inputs produces unreadable table values.

**Unreadable:**
```java
| orgId         | featureId | version | Lookup Key
| Organization1 | search    | v2      | Organization1:search:v2
```

### Solution
Use single-letter values as placeholders when testing key composition logic.

**Readable:**
```java
@TableTest("""
    Scenario       | orgId | featureId | version | Feature Toggles
    Specific match | O     | F         | V       | [O:F:V: true]
    Wild version   | O     | F         | V       | [O:F:*: true]
    """)
```

**Format**: `O:F:V` clearly shows:
- Organization: `O`
- Feature: `F`
- Version: `V`

With wildcards: `O:F:*` means "any version for this org+feature".

### Benefits
- **Readable**: Can see the pattern at a glance
- **Traceable**: Each letter maps to an input column
- **Concise**: Fits in table without wrapping
- **Pattern-focused**: Tests composition logic, not specific values

### When to Use
- Testing feature flags, keys, IDs constructed from multiple parts
- Validating query strings or cache keys
- Testing routing patterns or URL construction
- Any system that concatenates inputs into formatted output

### When Not to Use
- Production values matter (use real values)
- Testing validation logic (need realistic invalid inputs)
- Single complex input (no composition to show)

### Guidelines
- Use consistent mapping: `O` always means orgId, `F` always means featureId
- Document the mapping in a comment if not obvious
- Keep pattern visible in expected output
- Use wildcards `*` for "any" when relevant

**Example with wildcards:**
```java
| orgId | Feature Toggle  | Meaning
| O     | O:F:V           | Specific version for org O
|       | *:F:V           | Any org (null → wildcard)
| O     | O:F:*           | Org O, any version
```

---

## Summary

These patterns emerged from real-world TableTest usage. They solve common challenges:

1. **Consolidating Identity + Status**: When both vary together in same position
2. **Relative Positions**: Handle positional APIs with fixed + relative columns
3. **Production Constants**: Show actual system values in tables
4. **Timing with Concrete Milliseconds**: Verifiable, traceable timing
5. **Async Execution**: Combine sync/async concerns reliably
6. **Test Helpers**: Observe behavior beyond return values (counts, sequences, side effects)
7. **Recording Sequences**: Control stopping with expected sequences
8. **One-Letter Values**: Improve readability for composite key testing

When facing similar challenges, consider these patterns before creating custom solutions.
