# Value Sets for Multiple Input Examples for Same Expectation

Use `{...}` to expand a single row into multiple test cases. Expansion happens when the parameter type is NOT `Set<?>`. If the parameter type is `Set<?>`, the value is parsed as a set literal instead.

```java
@TableTest("""
    Scenario                    | Example Years      | Is Leap Year?
    Not divisible by 4          | {2001, 2002, 2003} | false
    Divisible by 4              | {2004, 2008, 2012} | true
    Divisible by 100 not by 400 | {2100, 2200, 2300} | false
    Divisible by 400            | {2000, 2400, 2800} | true
    """)
void testLeapYears(Year year, boolean isLeapYear) {
    assertEquals(isLeapYear, year.isLeap());
}
```

## Cartesian Product

Multiple sets in the same row create a cartesian product:

```java
@TableTest("""
    Scenario | a      | b      | Max Sum?
    Combined | {1, 2} | {3, 4} | 6
    """)
void testCartesianProduct(int a, int b, int maxSum) {
    assertTrue(a + b <= maxSum);
}
```

This generates 4 test cases: (1,3), (1,4), (2,3), (2,4).

## "Doesn't Matter" Pattern

Use value sets when a flag is irrelevant for certain scenarios:

```java
@TableTest("""
    Scenario    | Master | Fallback      | Expected?
    Normal flow | true   | {true, false} | success
    Error path  | true   | true          | fallback
    """)
void handles_errors(boolean master, boolean fallback, String expected) { ... }
```

Row 1: Fallback flag doesn't matter when there's no error, so test both values.
Row 2: Fallback flag is critical for error handling, so specify exact value.

This documents in the table itself which flags matter for which scenarios. When a column has `{true, false}` for a row, it signals "this input is irrelevant to the output for this scenario".
