Here's the TableTest. I'm assuming `Employee` takes a level enum and department enum; adjust the constructor call to match your actual class.

```java
@TableTest("""
    Scenario                   | Level      | Department            | Bonus %?
    Senior in Sales            | SENIOR     | SALES                 | 15.0
    Senior in Engineering      | SENIOR     | ENGINEERING           | 12.0
    Junior in Sales            | JUNIOR     | SALES                 | 8.0
    Junior in Engineering      | JUNIOR     | ENGINEERING           | 5.0
    Contractor, any department | CONTRACTOR | {SALES, ENGINEERING}  | 0.0
    """)
void shouldCalculateBonusPercentage(Level level, Department department, double expectedBonus) {
    Employee employee = new Employee(level, department);
    assertEquals(expectedBonus, calculateBonusPercentage(employee));
}
```

A few notes on the design:

- **Enums are converted automatically** by JUnit — no `@TypeConverter` needed for `Level` or `Department`.
- **Value set on the CONTRACTOR row** — `{SALES, ENGINEERING}` generates two test cases, both asserting `0.0`. This directly expresses "regardless of department" rather than picking an arbitrary value or repeating the row.
- **Constructor call in method body** — `new Employee(level, department)` is simple setup, not parsing logic, so it's fine in the test method.