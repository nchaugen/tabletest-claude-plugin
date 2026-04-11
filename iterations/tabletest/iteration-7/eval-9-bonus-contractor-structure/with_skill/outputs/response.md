Here's the TableTest. The CONTRACTOR row uses a value set since the result is 0% regardless of department — exactly the "regardless of" pattern the skill describes.

```java
@Description("Bonus percentage is determined by employee level and department. "
    + "Contractors always receive 0% regardless of department.")
@TableTest("""
    Scenario                     | Level      | Department            | Bonus%?
    Senior in Sales              | SENIOR     | SALES                 | 15.0
    Senior in Engineering        | SENIOR     | ENGINEERING           | 12.0
    Junior in Sales              | JUNIOR     | SALES                 | 8.0
    Junior in Engineering        | JUNIOR     | ENGINEERING           | 5.0
    Contractor regardless        | CONTRACTOR | {SALES, ENGINEERING}  | 0.0
    """)
void shouldCalculateBonusPercentage(Level level, Department department, double expectedBonus) {
    Employee employee = new Employee(level, department);
    assertEquals(expectedBonus, calculateBonusPercentage(employee));
}
```

A few notes:

- **Value set on CONTRACTOR**: `{SALES, ENGINEERING}` generates two test invocations, both asserting `0.0`. This is correct here because the result is genuinely identical regardless of which department is chosen.
- **`Level` and `Department` as enums**: JUnit's built-in converter handles enum conversion automatically, so no `@TypeConverter` is needed.
- **Parameter order**: aligns left-to-right with the data columns (scenario excluded), so `level`, `department`, `expectedBonus`.
- **`double` for expected**: matches the return type of the method directly.

If `Employee` doesn't have a two-arg constructor, adjust the test body to use whatever builder or setter pattern your model provides — the table structure stays the same.