Here's the TableTest. The CONTRACTOR rule ("regardless of department") maps cleanly to a value set — one row, two generated tests:

```java
@TableTest("""
    Scenario               | Level      | Department            | Bonus%?
    Senior in Sales        | SENIOR     | SALES                 | 15.0
    Senior in Engineering  | SENIOR     | ENGINEERING           | 12.0
    Junior in Sales        | JUNIOR     | SALES                 | 8.0
    Junior in Engineering  | JUNIOR     | ENGINEERING           | 5.0
    Contractor             | CONTRACTOR | {SALES, ENGINEERING}  | 0.0
    """)
void shouldCalculateBonusPercentage(Level level, Department department, double expectedBonus) {
    Employee employee = new Employee(level, department);
    assertEquals(expectedBonus, calculateBonusPercentage(employee));
}
```

A few design notes:

- **Value set on CONTRACTOR**: `{SALES, ENGINEERING}` generates two tests, both asserting `0.0`. This directly expresses "regardless of department" as data rather than duplicating rows.
- **No `@Description` needed**: the table is self-contained. The value set already communicates the contractor rule clearly.
- **Enum conversion**: JUnit converts `SENIOR`/`JUNIOR`/`CONTRACTOR` → `Level` and `SALES`/`ENGINEERING` → `Department` automatically — no `@TypeConverter` needed.
- **Column name `Bonus%?`**: the `%` makes the unit of measure clear without a separate description.