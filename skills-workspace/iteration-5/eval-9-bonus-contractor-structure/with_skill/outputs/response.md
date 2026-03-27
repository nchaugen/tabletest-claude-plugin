Here's a TableTest for `calculateBonus`:

```java
@TableTest("""
    Scenario                      | Level      | Department           | Salary    | Bonus?
    Senior in Sales               | SENIOR     | SALES                | 100000.00 | 15000.00
    Senior in Engineering         | SENIOR     | ENGINEERING          | 100000.00 | 12000.00
    Junior in Sales               | JUNIOR     | SALES                | 100000.00 | 8000.00
    Junior in Engineering         | JUNIOR     | ENGINEERING          | 100000.00 | 5000.00
    Contractor regardless of dept | CONTRACTOR | {SALES, ENGINEERING} | 100000.00 | 0.00
    """)
@Description("Bonus rates: SENIOR 15% Sales / 12% Engineering; JUNIOR 8% Sales / 5% Engineering; CONTRACTOR always 0% regardless of department")
void shouldCalculateBonus(Level level, Department department, double salary, double expectedBonus) {
    Employee employee = new Employee(level, department, salary);
    assertThat(calculator.calculateBonus(employee)).isEqualTo(expectedBonus);
}
```

A few design notes:

- **Uniform salary of 100,000** makes the expected values directly traceable to the rules (15% × 100k = 15,000, etc.) — no mental arithmetic required.
- **Value set `{SALES, ENGINEERING}`** on the CONTRACTOR row expresses "regardless of department" declaratively, generating two tests from one row rather than duplicating the row.
- **`@Description`** captures the rate rules so the table doesn't need to encode them as raw percentages.

Two assumptions I made — adjust as needed:
1. `Employee` can be constructed with `(level, department, salary)`.
2. `Level` and `Department` are enums that JUnit can convert from their string names automatically.