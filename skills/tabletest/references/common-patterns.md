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
    Scenario                   | Modern Is Master | Master Response?  | Other Response?   |
    Modern master, both OK     | true             | Modern OK         | Legacy OK         |
    Modern master, other fails | true             | Modern OK         | Legacy ERROR      |
    Legacy master, both OK     | false            | Legacy OK         | Modern OK         |
    Legacy master, Modern fails| false            | Legacy OK         | Modern ERROR      |
    """)
```

In `Master Response?` column:
- **Identity varies**: Could be Modern or Legacy (depends on `Modern Is Master`)
- **Status varies**: Could be OK or ERROR

Both pieces of information vary together in the same position, so consolidate them.

**Without consolidation (harder to read):**
```java
| Master Is? | Master Success? | Master Error? | Other Is? | Other Success? | Other Error? |
| Modern     | yes             |               | Legacy    | yes            |              |
| Modern     | yes             |               | Legacy    |                | yes          |
| Legacy     | yes             |               | Modern    | yes            |              |
```

Requires 6 columns and cross-referencing to understand "Modern succeeded" or "Legacy failed."

### Format
`"<Identity> <Status>"` where both identity and status vary:
- `Modern OK` / `Modern ERROR` / `Legacy OK` / `Legacy ERROR`
- `Cache HIT` / `Database MISS` / `Service TIMEOUT`
- `Primary SUCCESS` / `Fallback SUCCESS` / `Primary FAILURE`

### Implementation

**Custom converter method for parsing:**
```java
@TypeConverter
public static RouteResponse parseRouteResponse(String value) {
    if (value == null || value.isBlank()) return null;

    return switch (value.trim()) {
        case "Modern OK" -> RouteResponse.MODERN_OK;
        case "Modern ERROR" -> RouteResponse.MODERN_ERROR;
        case "Legacy OK" -> RouteResponse.LEGACY_OK;
        case "Legacy ERROR" -> RouteResponse.LEGACY_ERROR;
        case "ROUTE_NOT_IMPLEMENTED" -> RouteResponse.ROUTE_NOT_IMPLEMENTED;
        default -> throw new IllegalArgumentException("Unknown response: " + value);
    };
}

public enum RouteResponse {
    MODERN_OK,
    MODERN_ERROR,
    LEGACY_OK,
    LEGACY_ERROR,
    ROUTE_NOT_IMPLEMENTED
}
```

**Assertion helper:**
```java
private void assertRouteResponse(boolean isMaster, RouteResponse expected, Report report) {
    Object actualResponse = isMaster ? report.masterResponse() : report.otherResponse();
    String actualError = isMaster ? report.masterError() : report.otherError();

    switch (expected) {
        case MODERN_OK -> {
            assertEquals("Modern", actualResponse);
            assertNull(actualError);
        }
        case MODERN_ERROR -> {
            assertNull(actualResponse);
            assertNotNull(actualError);
            assertTrue(actualError.contains("Modern system error"));
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
- **Natural language**: Reads like "Modern succeeded" or "Legacy failed"
- **Clearer intent**: Combinations are explicit, not inferred

---

## Pattern: Relative Positions

### Problem
System uses positional fields (first/second, master/other, primary/secondary) that vary based on configuration.

**Example:** A report has `masterResponse` and `otherResponse` fields. Which system is "master" depends on a configuration flag.

### Solution
Use fixed identities (Modern, Legacy) in input columns, relative roles (Master, Other) in expectation columns, and map between them in test logic.

```java
@TableTest("""
    Scenario          | Modern Is Master | Modern | Legacy | Master Response? | Other Response? |
    Modern is master  | true             | OK     | ERROR  | Modern OK        | Legacy ERROR    |
    Legacy is master  | false            | OK     | ERROR  | Legacy OK        | Modern ERROR    |
    """)
void reports_with_positional_fields(
    boolean modernIsMaster,
    String modernStatus,
    String legacyStatus,
    RouteResponse masterResponse,
    RouteResponse otherResponse
) {
    // ... execute system

    // Verify master position (isMaster=true)
    assertRouteResponse(true, masterResponse, report);

    // Verify other position (isMaster=false)
    assertRouteResponse(false, otherResponse, report);
}

private void assertRouteResponse(boolean isMaster, RouteResponse expected, Report report) {
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
- **Input columns stable**: Fixed system names (Modern, Legacy)
- **Expectation columns match API**: Report fields (master, other)
- **Test logic handles mapping**: Avoids duplicating scenarios
- **Table reads naturally**: "When Modern is master, master response is Modern OK"

---

## Pattern: Production Constants in Tables

### Problem
Tests create abstractions (test-only enums, simplified labels) that hide actual system behavior.

**Bad:**
```java
| Other Response? |
| NOT_IMPL        |  // Test-only abbreviation
```

**Good:**
```java
| Other Response?           |
| ROUTE_NOT_IMPLEMENTED     |  // Actual production constant
```

### Guidance

**Prefer production constants** when they're part of the observable contract:
- Sentinel values: `ROUTE_NOT_IMPLEMENTED`, `NOT_FOUND`, `UNSET`
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
- Combining multiple values: `RouteResponse.MODERN_OK` instead of checking two fields separately

**Example from real session:**
We used the actual production constant `ROUTE_NOT_IMPLEMENTED` directly in table cells rather than inventing a test-only abbreviation like `NO_ROUTE`. This made the table match the system's actual behavior.

---

## Pattern: Timing Thresholds with Upper-Bound Assertions

### Problem
Tests verify timing but use either vague assertions (`>= 0`, `not null`) or complex range assertions that are still flaky.

### Solution
Use upper-bound assertions with `<` notation to express "completes within X milliseconds."

```java
@TableTest("""
    Scenario          | Modern ms | Legacy ms | Master ms? | Other ms? |
    Both fast         | 10        | 60        | <50        | <100      |
    Master slow       | 100       | 10        | <150       | <50       |
    Not implemented   | 10        |           | <50        |           |
    """)
void records_response_times(
    Long modernMs,
    Long legacyMs,
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

// Converter for < notation
@TypeConverter
public static Long parseResponseTime(String value) {
    if (value == null || value.isBlank()) {
        return null;
    }
    String trimmed = value.trim();
    return trimmed.startsWith("<")
        ? Long.valueOf(trimmed.substring(1))
        : Long.parseLong(trimmed);
}
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
    Scenario        | Primary ms | Secondary ms | Response ms? |
    Primary fast    | 10         | 100          | <50          |  // Proves primary (10+buffer < 50 < 100)
    Secondary fast  | 100        | 10           | <50          |  // Proves secondary (10+buffer < 50 < 100)
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
    Scenario        | Feature Toggles      | Query Count? | Result?
    Specific match  | [O-a-e-Cd/s/m: true] | 1            | true
    Wild customer   | [O-a-e-*d/s/m: true] | 2            | true
    Not found       | [:]                  | 12           | empty
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
    Scenario       | Expected Queries?
    Specific       | [O-a-e-Cd/s/m]
    Wild customer  | [O-a-e-Cd/s/m, O-a-e-*d/s/m]
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
expected: <[O-a-e-Cd/s/m]>
but was:  <[O-a-e-Cd/s/m, O-a-e-*d/s/m, ..., *-a-e-*d/*/*]>
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

## Pattern: One-Letter Values for Composite Strings

### Problem
Testing systems that construct complex strings from multiple inputs produces unreadable table values.

**Unreadable:**
```java
| organizationId | customerId | action   | Feature Toggle
| Organization-1 | Customer-1 | activate | Organization-1-activate-prod-Customer-1domain/service/method
```

### Solution
Use single-letter values as placeholders when testing string composition logic.

**Readable:**
```java
@TableTest("""
    Scenario       | orgId | custId | action | env | domain | svc | method | Feature Toggles
    Specific match | O     | C      | a      | e   | d      | s   | m      | [O-a-e-Cd/s/m: true]
    Wild customer  | O     | C      | a      | e   | d      | s   | m      | [O-a-e-*d/s/m: true]
    """)
```

**Format**: `O-a-e-Cd/s/m` clearly shows:
- Organization: `O`
- Action: `a`
- Environment: `e`
- Customer: `C`
- Domain/Service/Method: `d/s/m`

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
- Use consistent mapping: `O` always means orgId, `C` always means custId
- Document the mapping in a comment if not obvious
- Keep pattern visible in expected output
- Use wildcards `*` for "any" when relevant

**Example with wildcards:**
```java
| orgId | Feature Toggle        | Meaning
| O     | O-a-e-Cd/s/m         | Specific organization O
|       | *-a-e-Cd/s/m         | Any organization (null → wildcard)
| O     | O-a-e-*d/s/m         | Organization O, any customer
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
8. **One-Letter Values**: Improve readability for composite string testing

When facing similar challenges, consider these patterns before creating custom solutions.
