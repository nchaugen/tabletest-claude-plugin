# Example Patterns

## Business Rules

```java
@TableTest("""
    Scenario              | Age | Has Licence | Can Rent Car?
    Too young             | 17  | true        | false
    Adult with licence    | 25  | true        | true
    Adult without licence | 30  | false       | false
    Senior with licence   | 70  | true        | true
    """)
void testCarRentalEligibility(int age, boolean hasLicence, boolean canRent) {
    assertEquals(canRent, isEligibleToRentCar(age, hasLicence));
}
```

## Boundary Testing

```java
@TableTest("""
    Scenario      | Input | Valid?
    Below minimum | -1    | false
    At minimum    | 0     | true
    Normal range  | 50    | true
    At maximum    | 100   | true
    Above maximum | 101   | false
    """)
void testValidRange(int input, boolean expectedValid) {
    assertEquals(expectedValid, isInRange(input, 0, 100));
}
```

## Exception Testing

```java
@TableTest("""
    Scenario       | Input | Exception?
    Negative age   | -1    | java.lang.IllegalArgumentException
    Empty name     | ''    | java.lang.IllegalArgumentException
    """)
void testExceptions(String input, Class<? extends Throwable> expectedException) {
    assertThrows(expectedException, () -> validateInput(input));
}
```

## Value Transformations

```java
@TableTest("""
    Scenario              | Input       | Formatted?
    Normalize spacing     | "[1,2,3]"   | "[1, 2, 3]"
    Remove extra spaces   | "[ [] ]"    | "[[]]"
    Nested lists          | "[[1,2]]"   | "[[1, 2]]"
    Empty collection      | "[]"        | "[]"
    """)
void shouldFormatCollection(String input, String formatted) {
    assertThat(format(input)).isEqualTo(formatted);
}
```

## Consolidating Similar Tests

**Before** - Multiple separate tests:
```java
@Test
void whenMasterIsMdc_returnsMdc() {
    assertEquals("mdc", router.route(true, false));
}

@Test
void whenMasterIsLegacy_returnsLegacy() {
    assertEquals("legacy", router.route(false, false));
}

@Test
void whenDualDispatch_executesInOrder() {
    // Complex setup with latches and execution tracking...
    assertEquals(Arrays.asList("mdc", "legacy"), executionOrder);
}
```

**After** - Single consolidated table:
```java
@TableTest("""
    Scenario       | Master | Dual Dispatch | Response? | Execution Order?
    MDC master     | true   | false         | mdc       | [mdc]
    Legacy master  | false  | false         | legacy    | [legacy]
    MDC dual       | true   | true          | mdc       | [mdc, legacy]
    Legacy dual    | false  | true          | legacy    | [legacy, mdc]
    """)
void routing_behavior(boolean isMdcMaster, boolean dualDispatch,
                      String response, List<String> order) {
    List<String> actualOrder = new CopyOnWriteArrayList<>();

    String result = router.route(
        isMdcMaster,
        dualDispatch,
        () -> { actualOrder.add("mdc"); return "mdc"; },
        () -> { actualOrder.add("legacy"); return "legacy"; }
    );

    assertEquals(response, result);
    assertEquals(order, actualOrder);
}
```

**Benefits:**
- See all routing rules in one place
- Easy to spot missing scenarios (e.g., legacy dual dispatch)
- Add new cases without new test methods
- Clear relationship between inputs and outputs
