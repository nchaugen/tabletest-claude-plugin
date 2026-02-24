# Testing Async and Performance Behavior

## Testing Non-Blocking Execution

When testing that operations don't block, use response time thresholds as observable expectations:

```java
@TableTest("""
    Scenario          | Primary Delay | Async Delay | Response Within?
    Fast sync         | 10            | 10          | 100
    Slow async task   | 10            | 2000        | 100
    """)
void async_operations_do_not_block(long primaryMs, long asyncMs, long maxResponseMs) {
    CountDownLatch asyncLatch = new CountDownLatch(asyncMs > 0 ? 1 : 0);
    Executor asyncExecutor = task -> new Thread(() -> {
        task.run();
        asyncLatch.countDown();
    }).start();

    long start = System.nanoTime();
    Result result = service.execute(
        () -> { Thread.sleep(primaryMs); return "primary"; },
        () -> { Thread.sleep(asyncMs); return "async"; }
    );
    long actualMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

    assertTrue(asyncLatch.await(5, TimeUnit.SECONDS));
    assertTrue(actualMs < maxResponseMs);
}
```

**Row 2 proves async**: Async task takes 2000ms, but response returns in <100ms.

## Tracking Execution Order

Use thread-safe collections to capture execution order:

```java
void tracks_execution_order(..., List<String> expectedOrder) {
    List<String> actualOrder = new CopyOnWriteArrayList<>();

    service.execute(
        () -> { actualOrder.add("first"); return "A"; },
        () -> { actualOrder.add("second"); return "B"; }
    );

    assertEquals(expectedOrder, actualOrder);
}
```

**Thread-safe collection choices:**
- `CopyOnWriteArrayList` - Best for write-then-read patterns (test scenarios)
- `Collections.synchronizedList()` - Use when reads happen concurrently with writes

## Helper Methods for Suppliers

Extract helpers to avoid duplication:

```java
private static Supplier<String> delayedSupplier(String value, long delayMs, List<String> tracker) {
    return () -> {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        tracker.add(value);
        return value;
    };
}
```

Then use in test:
```java
String result = router.apply(
    "method",
    delayedSupplier("mdc", mdcDelayMs, executionOrder),
    delayedSupplier("legacy", legacyDelayMs, executionOrder)
);
```

## Using Maps for Composite Request/Response Data

When requests have multiple properties (status, timing, data), use maps to avoid parameter explosion:

```java
@TableTest("""
    Scenario          | MDC Request            | Legacy Request          | Response?                     | Responder? | Execution Order?
    MDC ok in prod    | [status: OK, ms: 10]   | [:]                     | [status: OK, withinMs: 50]    | MDC        | [MDC]
    MDC fail in prod  | [status: ERROR, ms: 10]| [:]                     | [status: ERROR, withinMs: 50] | MDC        | [MDC]
    Shadow, MDC fails | [status: ERROR, ms: 100]| [status: OK, ms: 10]   | [status: OK, withinMs: 50]    | Legacy     | [Legacy, MDC]
    """)
void routes_with_error_handling(
        Map<String, String> mdcRequest,
        Map<String, String> legacyRequest,
        Map<String, String> expectedResponse,
        String expectedResponder,
        List<String> executionOrder
) throws InterruptedException {
    // ... setup ...

    Supplier<String> routerInvocation = () -> router.apply(
        "method",
        "context",
        "request",
        createResponder("MDC", mdcRequest, actualExecutionOrder),
        createResponder("Legacy", legacyRequest, actualExecutionOrder)
    );

    String expectedStatus = expectedResponse.get("status");
    if ("OK".equals(expectedStatus)) {
        assertEquals(expectedResponder, routerInvocation.get());
    } else {
        assertThrows(RuntimeException.class, routerInvocation::get);
    }

    // ... timing and order assertions ...
}

private static Supplier<String> createResponder(String responder, Map<String, String> request, List<String> actualExecutionOrder) {
    if (request.isEmpty()) return () -> null;  // Route not implemented

    final long responseTime = Long.parseLong(request.get("ms"));
    final String status = request.get("status");
    return () -> {
        try {
            Thread.sleep(responseTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        actualExecutionOrder.add(responder);
        if ("ERROR".equals(status)) {
            throw new RuntimeException(responder + " system error");
        }
        return responder;
    };
}
```

**Benefits:**
- Empty map `[:]` naturally represents "route not implemented"
- Status field controls whether supplier succeeds or throws
- Timing field simulates realistic delays
- Single column represents the complete request characteristics

## Dynamic Async Coordination Based on Scenarios

When async execution depends on scenario data (e.g., master success/failure), calculate latch count dynamically:

```java
Map<String, String> masterRequest = isMdcMaster ? mdcRequest : legacyRequest;
boolean masterWillFail = "ERROR".equals(masterRequest.get("status"));
boolean asyncWillExecute = dualDispatch && !masterWillFail;

CountDownLatch asyncLatch = new CountDownLatch(asyncWillExecute ? 1 : 0);
```

**Why:** If the master fails without fallback enabled, the secondary never executes (to avoid data inconsistencies). The latch must reflect this to avoid timeouts.

**Scenarios where secondary doesn't execute async:**
- Master succeeds, no dual dispatch (single path only)
- Master fails, dual dispatch enabled, but fallback disabled (secondary skipped to prevent inconsistency)

**Scenarios where secondary executes async:**
- Master succeeds, dual dispatch enabled (fire-and-forget secondary)
- Master fails, fallback enabled (but runs synchronously to return result)

Note: When fallback executes, it runs synchronously (not async) because the router must return its result.

## Complete Example

```java
@TableTest("""
    Scenario       | Master | Dual Dispatch | MDC Time | Legacy Time | Response Within? | Responder? | Execution Order?
    MDC in prod    | true   | false         | 10       | 10          | 50               | mdc        | [mdc]
    Legacy in prod | false  | false         | 10       | 10          | 50               | legacy     | [legacy]
    MDC in pilot   | true   | true          | 10       | 1000        | 50               | mdc        | [mdc, legacy]
    MDC in shadow  | false  | true          | 1000     | 10          | 50               | legacy     | [legacy, mdc]
    """)
void routes_requests_based_on_context_flags(
        boolean isMdcMaster,
        boolean dualDispatch,
        long mdcResponseTime,
        long legacyResponseTime,
        long responseWithinMs,
        String expectedResponder,
        List<String> executionOrder
) throws InterruptedException {
    Map<String, String> masterRequest = isMdcMaster ? mdcRequest : legacyRequest;
    boolean masterWillFail = "ERROR".equals(masterRequest.get("status"));
    boolean asyncWillExecute = dualDispatch && !masterWillFail;

    CountDownLatch asyncLatch = new CountDownLatch(asyncWillExecute ? 1 : 0);
    Executor asyncExecutor = task -> new Thread(() -> {
        task.run();
        asyncLatch.countDown();
    }).start();

    router = new Router(isMdcMaster, dualDispatch, asyncExecutor);

    List<String> actualExecutionOrder = new CopyOnWriteArrayList<>();

    long startTime = System.nanoTime();
    String result = router.apply(
        createResponder("mdc", mdcResponseTime, actualExecutionOrder),
        createResponder("legacy", legacyResponseTime, actualExecutionOrder)
    );
    long actualDurationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

    assertTrue(asyncLatch.await(5, TimeUnit.SECONDS), "Async execution should complete");

    assertEquals(expectedResponder, result);
    assertEquals(executionOrder, actualExecutionOrder);
    assertTrue(actualDurationMs < responseWithinMs,
        String.format("Expected response within %dms but took %dms", responseWithinMs, actualDurationMs));
}

private static Supplier<String> createResponder(String response, long responseTime, List<String> actualExecutionOrder) {
    return () -> {
        try {
            Thread.sleep(responseTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        actualExecutionOrder.add(response);
        return response;
    };
}
```

**What this table proves:**
- Rows 1-2: Single execution (no dual dispatch)
- Rows 3-4: Both suppliers execute (dual dispatch)
- Rows 3-4: Secondary executes async without blocking (slow secondary but fast response)

## Timing Assertions: Upper-Bound Pattern

### Problem
Range-based timing assertions (`actualMs >= expectedMs - 5 && actualMs <= expectedMs + 50`) are complex and still subject to flakiness. They test "approximately X milliseconds" when what we care about is "completes within X milliseconds."

### Solution
Use upper-bound assertions with `<` notation for clearer semantics and simpler logic.

**Table notation:**
```java
@TableTest("""
    Scenario        | Operation | Response Time?
    Fast path       | cached    | <50
    Database query  | query     | <200
    Async operation | async     | <100
    """)
void completes_within_threshold(String operation, Long maxMs) {
    long start = System.nanoTime();
    performOperation(operation);
    long actualMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

    assertTrue(actualMs < maxMs,
        String.format("Expected within %dms but took %dms", maxMs, actualMs));
}
```

**Parser for `<` notation:**
```java
@SuppressWarnings("unused")
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

### Choosing Operation Times vs Assertion Thresholds

**Key principle:** Use actual operation times that create clear separation, then assert on thresholds that prove behavior.

```java
@TableTest("""
    Scenario          | MDC ms | Legacy ms | Response? | Response ms?
    Fast MDC          | 10     | 100       | OK        | <50
    Fast Legacy       | 100    | 10        | OK        | <50
    Both slow         | 60     | 100       | OK        | <100
    """)
```

**Design choices:**
- **Operation times** (10ms, 60ms, 100ms): Concrete delays in test setup
  - Must be large enough to be measurable
  - Must have clear separation (10ms vs 60ms, not 10ms vs 15ms)
  - Represent realistic timing differences

- **Assertion thresholds** (<50ms, <100ms): Observable behavior expectations
  - Must prove the operation completed
  - Must have buffer for test execution overhead
  - 10ms operation → <50ms threshold gives 40ms buffer

**Example showing separation:**
```java
// Good separation
| MDC ms | Legacy ms | Response ms? |
| 10     | 60        | <50          |  // Proves MDC responded (10+40 buffer < 50)
| 60     | 10        | <100         |  // Proves system responded (60+40 buffer < 100)

// Bad separation (too close)
| MDC ms | Legacy ms | Response ms? |
| 10     | 15        | <50          |  // Both well under threshold - can't prove which responded
```

### Benefits Over Range Assertions

**Simpler logic:**
```java
// Complex range assertion
assertTrue(actualMs >= expectedMs - 5 && actualMs <= expectedMs + 50);

// Simple upper-bound assertion
assertTrue(actualMs < expectedMs);
```

**Clearer semantics:**
- Range: "approximately X milliseconds" (tests timing accuracy)
- Upper-bound: "completes within X milliseconds" (tests performance threshold)

**More robust:**
- Range assertions fail if timing shifts by small amounts
- Upper-bound assertions only fail if operation takes too long
- Upper-bound tolerates timing variations below threshold

### When to Use Each

**Use upper-bound** (<) for:
- Performance requirements ("must respond within 100ms")
- Async behavior verification (proves non-blocking)
- Service-level objectives (SLOs)

**Use exact values** for:
- Fixed delays (Thread.sleep in test)
- Controlled simulations
- When precision matters (not common in integration tests)

**Use range assertions** for:
- Verifying recorded timing matches actual timing
- When system measures and reports durations
- Example: "reported 10ms in log, verify it took 10±50ms"

### Consistent Notation Across Tables

When multiple TableTests exist in the same test class, use consistent notation:

```java
// Table 1: Routing behavior
@TableTest("""
    Scenario | Response ms?
    Fast     | <50
    Slow     | <150
    """)
void routes_requests(...) { }

// Table 2: Reporting behavior
@TableTest("""
    Scenario | Report ms?
    Fast     | <50       // Consistent with Table 1
    Slow     | <150      // Consistent with Table 1
    """)
void reports_requests(...) { }

// Share the same parser
public static Long parseResponseTime(String value) { ... }
```

**Inconsistency to avoid:**
```java
// Table 1 uses < notation
| Response ms? |
| <50          |

// Table 2 uses exact values (inconsistent!)
| Report ms? |
| 50         |
```

## Focus on Observable Behavior, Not Implementation

Design tables around **what** happens, not **how** it happens internally.

**Observable:**
- Which response is returned
- What order operations execute in
- Performance characteristics (response time < threshold)
- What gets logged/reported
- What errors are thrown

**Implementation details:**
- Internal variable values (AtomicInteger counters)
- Thread mechanics (unless testing thread safety)
- Cache hit/miss (unless that's the feature)

**Making implementation observable:**
Sometimes you need to make internal behavior observable for testing:
- Execution order → Track with shared list: `List<String> executionOrder`
- Async non-blocking → Verify response time: `responseDuration < threshold`
- Lazy evaluation → Count invocations via side effects

**Example - Testing async behavior:**
```java
@TableTest("""
    Scenario     | Primary Time | Secondary Time | Response Within?
    Sync case    | 10           | 10             | 50
    Async case   | 10           | 1000           | 50
    """)
void proves_non_blocking_execution(long primaryMs, long secondaryMs, long maxResponseMs) {
    long start = System.nanoTime();
    String result = router.route(
        () -> { sleep(primaryMs); return "primary"; },
        () -> { sleep(secondaryMs); return "secondary"; }
    );
    long actualMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

    assertTrue(actualMs < maxResponseMs,
        "Expected response within " + maxResponseMs + "ms but took " + actualMs + "ms");
}
```

The async test proves secondary executes without blocking: secondary takes 1000ms but response comes back in <50ms.
