# Consolidating Redundant Tests

After building a comprehensive table test, look for individual `@Test` methods that are now redundant:

## Workflow

1. List what scenarios the table test covers
2. For each standalone `@Test`, ask: "Does the table test cover this scenario?"
3. If yes, check: "Does the `@Test` verify something the table doesn't?" (e.g., mock interactions, exception details, suppressed exceptions)
4. If fully redundant, remove it
5. Run full test suite before and after removal to verify coverage

## Example

After expanding table test with fallback scenarios, these became redundant:
```java
@Test
void testApply_whenPrimaryThrowsAndFallbackEnabled_executesSecondaryFallback() {
    // Covered by table row: "Primary fails, fallback ok"
}

@Test
void testApply_whenPrimaryThrowsAndFallbackDisabled_throwsWithoutFallback() {
    // Covered by table row: "Primary fails, no fallback"
}
```

This kept (tests suppressed exceptions detail):
```java
@Test
void testApply_whenBothThrowAndFallbackEnabled_throwsPrimaryWithSuppressed() {
    assertEquals(1, thrown.getSuppressed().length); // Not in table
}
```

## Benefits of Removing Redundant Tests

- Less maintenance (one place to update when behavior changes)
- Clearer test intent (table shows the pattern, special tests show special cases)
- Faster test runs
