Here's a TableTest for `calculateBonus`. The CONTRACTOR rule is a natural fit for a value set since it holds regardless of department:

```java
@Description("Bonus rates vary by seniority and department. "
    + "Contractors always receive 0% regardless of department.")
@TableTest("""
    Scenario               | Level      | Department           | Bonus rate?
    Senior in Sales        | SENIOR     | SALES                | 0.15
    Senior in Engineering  | SENIOR     | ENGINEERING          | 0.12
    Junior in Sales        | JUNIOR     | SALES                | 0.08
    Junior in Engineering  | JUNIOR     | ENGINEERING          | 0.05
    Contractor             | CONTRACTOR | {SALES, ENGINEERING} | 0.00
    """)
void shouldCalculateBonus(Level level, Department department, double expectedRate) {
    Employee employee = new Employee(level, department);
    assertEquals(expectedRate, calculateBonus(employee), 0.001);
}
```

A few notes:

- **Value set on CONTRACTOR**: `{SALES, ENGINEERING}` generates two rows, both asserting `0.00` — directly expressing "regardless of department" without listing every combination manually.
- **`0.001` delta**: guards against floating-point noise; adjust to `0.0` if you prefer exact matching with a method that returns literal constants.
- **`Employee` construction**: assumes a constructor taking `(Level, Department)`. If your API differs (e.g., a builder), adjust the test body accordingly — the table columns stay the same.
- JUnit's built-in enum conversion handles `Level` and `Department` automatically, so no `@TypeConverter` is needed.