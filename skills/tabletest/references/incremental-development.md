# Incremental Development

Build TableTests iteratively, with fast feedback loops:

1. **Design thoughtfully**: Understand the code and sketch table structure (see Design Phase in SKILL.md)
2. **Implement fully**: Write complete first table with all planned scenarios
3. **Run immediately**: Get fast feedback on structure, conversions, and helper needs
4. **Fix infrastructure**: Address conversion errors, add factory methods, create test helpers
5. **Refine iteratively**: Improve names and structure after tests pass

**For simple cases**: You may start with 1-2 rows and build up gradually.

**For complex cases**: Design the full table upfront, implement it completely, then run.

**Key insight**: "Incremental" means fast feedback cycles, not necessarily one row at a time. Design -> Implement -> Run -> Refine is the core cycle.

## Benefits

- Fast feedback reveals infrastructure needs immediately
- Test failures guide helper class design
- Complete tables show patterns that inform refactoring
- Refinement happens with full context

## Example Progression

```java
// Step 1: Single row
@TableTest("""
    Scenario    | Master | Response?
    MDC master  | true   | mdc
    """)

// Step 2: Add contrasting case
@TableTest("""
    Scenario       | Master | Response?
    MDC master     | true   | mdc
    Legacy master  | false  | legacy
    """)

// Step 3: Add async scenarios
@TableTest("""
    Scenario       | Master | Dual Dispatch | Response? | Execution Order?
    MDC master     | true   | false         | mdc       | [mdc]
    Legacy master  | false  | false         | legacy    | [legacy]
    MDC pilot      | true   | true          | mdc       | [mdc, legacy]
    """)
```

## Column Evolution

As you add scenarios, column structure often needs adjustment. This is natural:

```java
// Phase 1: Just timing
@TableTest("""
    Scenario | MDC Time | Legacy Time | Response Within?
    ...      | 10       | 10          | 50
    """)

// Phase 2: Add error cases - need status too
// First attempt uses combined format
@TableTest("""
    Scenario     | MDC Response    | Legacy Response | Response?
    Normal case  | OK in 10ms      | OK in 10ms      | OK in <50ms
    MDC fails    | ERROR in 10ms   | OK in 10ms      | OK in <50ms
    """)

// Phase 3: Refactor to separate columns for readability
@TableTest("""
    Scenario     | MDC   | MDC ms | Legacy | Legacy ms | Response? | Response ms?
    Normal case  | OK    | 10     | OK     | 10        | OK        | <50
    MDC fails    | ERROR | 10     | OK     | 10        | OK        | <50
    """)

// Phase 4: Add "not implemented" routes - blank cells work naturally
@TableTest("""
    Scenario        | MDC   | MDC ms | Legacy | Legacy ms | Response? | Response ms?
    MDC in prod     | OK    | 10     |        |           | OK        | <50
    Legacy in prod  |       |        | OK     | 10        | OK        | <50
    """)
```

**Key insight:** Separate columns are easier to read than combined formats. See `references/column-design.md` for detailed guidance.

## Column Consolidation

As you add rows, watch for consolidation opportunities:
- Multiple columns that are mutually exclusive (one populated => others null)
- Both identity AND status varying together in same position
- Example: `Master Response?` and `Master Error?` -> `Master Response?` with values like `MDC OK`, `MDC ERROR`

See `references/common-patterns.md` for the "Consolidating Identity + Status" pattern.

## Learning from Test Failures

Test failures guide infrastructure design. Embrace the feedback loop.

**Pattern**: Helper class observes too much or too little
```
expected: <[O-a-e-Cd/s/m]> but was: <[O-a-e-Cd/s/m, O-a-e-*d/s/m, ...]>
```

This failure reveals: the recorder doesn't know when to stop.

**Solution**: Add control mechanism
```java
private static class QueryRecorder {
    private final List<String> expectedQueries;

    QueryRecorder(List<String> expectedQueries) {
        this.expectedQueries = expectedQueries;
    }

    Boolean resolve(String key) {
        queries.add(key);
        if (queries.size() == expectedQueries.size()) {
            return true;  // Signal to stop
        }
        return null;
    }
}
```

**Pattern**: Conversion failures reveal missing factory methods
```
Built-in conversion of value "true" to type java.util.Optional failed
```

**Solution**: Add factory method for the type (see `references/factory-methods.md`).

**Key insight**: Don't fight test failures. They show you what infrastructure you need. Add the control, conversion, or helper that the failure asks for.

## Progressive Enhancement

Don't try to design all tables upfront. Add tables progressively as you understand the code.

**Pattern**: First table reveals other aspects worth testing
```java
// First table: Results and counts
@TableTest("""
    Scenario | Feature Toggles | Query Count? | Result?
    ...
    """)
void finds_feature_toggles(...)

// Enhancement: Query sequences (added after first table works)
@TableTest("""
    Scenario | Expected Queries?
    ...
    """)
void queries_feature_toggles_in_precedence_order(...)
```

**When to add another table**:
- Different observable aspect of same method (timing, sequence order, error details)
- Complementary view that clarifies behavior
- Specific edge case that doesn't fit main table structure

**Not the same as orthogonal concerns**: These are complementary aspects of the same method, not separate features.
