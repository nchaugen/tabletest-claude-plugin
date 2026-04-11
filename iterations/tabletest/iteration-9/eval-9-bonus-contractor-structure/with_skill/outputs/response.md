Here's the table design. The CONTRACTOR row uses a value set to declare "regardless of department" directly in the table:

```
Scenario                       | Level      | Department           | Bonus %?
Senior in Sales                | SENIOR     | SALES                | 15.0
Senior in Engineering          | SENIOR     | ENGINEERING          | 12.0
Junior in Sales                | JUNIOR     | SALES                | 8.0
Junior in Engineering          | JUNIOR     | ENGINEERING          | 5.0
Contractor regardless of dept  | CONTRACTOR | {SALES, ENGINEERING} | 0.0
```

The `{SALES, ENGINEERING}` on the CONTRACTOR row generates two test cases and signals in the table itself that department is irrelevant for contractors.

Here's the full test:

```java
@TableTest("""
    Scenario                       | Level      | Department           | Bonus %?
    Senior in Sales                | SENIOR     | SALES                | 15.0
    Senior in Engineering          | SENIOR     | ENGINEERING          | 12.0
    Junior in Sales                | JUNIOR     | SALES                | 8.0
    Junior in Engineering          | JUNIOR     | ENGINEERING          | 5.0
    Contractor regardless of dept  | CONTRACTOR | {SALES, ENGINEERING} | 0.0
    """)
void shouldCalculateBonusPercentage(Level level, Department department, double bonusPercentage) {
    Employee employee = new Employee(level, department);
    assertEquals(bonusPercentage, calculateBonusPercentage(employee));
}
```

Two things to verify before this compiles:

1. **`Level` and `Department` are enums** — JUnit's built-in enum conversion handles them automatically, so no `@TypeConverter` needed as long as the names match exactly.
2. **`Employee` construction** — the test assumes `new Employee(level, department)`. Adjust to match your actual constructor or builder (e.g., `Employee.builder().level(level).department(department).build()`).

If `tabletest-junit` isn't in your build file yet, let me know and I'll add the dependency.