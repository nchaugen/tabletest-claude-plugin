# Cross-Table Consistency

When a test class contains multiple `@TableTest` methods, maintain consistency in notation, patterns, and shared infrastructure.

## Use Consistent Notation for Similar Concerns

```java
// Good - Both tables use same timing notation
@TableTest("""
    Scenario     | Response Time?
    Fast path    | <50
    Slow path    | <150
    """)
void routes_requests(Long maxMs) {
    assertTrue(actualMs < maxMs);
}

@TableTest("""
    Scenario     | Report Time?
    Fast path    | <50           // Consistent with routes_requests
    Slow path    | <150          // Consistent with routes_requests
    """)
void reports_requests(Long maxMs) {
    assertTrue(actualMs < maxMs);
}

// Shared parser used by both tests
public static Long parseResponseTime(String value) {
    return value.startsWith("<")
        ? Long.valueOf(value.substring(1))
        : Long.parseLong(value);
}
```

## Inconsistency to Avoid

```java
// Bad - Table 1 uses < notation
@TableTest("""
    | Response Time? |
    | <50            |
    """)
void test1(Long maxMs) { ... }

// Bad - Table 2 uses exact values (inconsistent!)
@TableTest("""
    | Response Time? |
    | 50             |
    """)
void test2(Long exactMs) { ... }  // Different assertion logic!
```

## Areas Requiring Consistency

1. **Timing notation**: If one table uses `<50` for thresholds, others should too
2. **Error representation**: If one table uses `ERROR+1` for suppressed exceptions, others should too
3. **Special values**: If one table uses `ROUTE_NOT_IMPLEMENTED`, others should use the same constant
4. **Parsers**: Share factory methods across tables (`parseResponseTime`, `parseRouteResponse`)
5. **Helper methods**: Reuse setup code (`createResponder`, `createAsyncSetup`)

## Benefits

- Easier to maintain (change once, affects all tables)
- Easier to understand (same patterns throughout)
- Fewer bugs (shared infrastructure is tested once)
- Clear intent (consistency signals related concerns)

## When Reviewing Multiple Tables

- Look for similar columns with different notations
- Extract common parsers and helpers
- Align assertion patterns (all upper-bound or all range-based, not mixed)
