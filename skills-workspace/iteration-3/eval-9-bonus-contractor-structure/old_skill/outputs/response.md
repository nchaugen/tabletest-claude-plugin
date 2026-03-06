# TableTest for `calculateBonus(Employee employee)`

```java
@TableTest("""
    Scenario                        | Level      | Department  | Bonus%?
    Senior in Sales                 | SENIOR     | SALES       | 15.0
    Senior in Engineering           | SENIOR     | ENGINEERING | 12.0
    Junior in Sales                 | JUNIOR     | SALES       | 8.0
    Junior in Engineering           | JUNIOR     | ENGINEERING | 5.0
    Contractor in Sales             | CONTRACTOR | SALES       | 0.0
    Contractor in Engineering       | CONTRACTOR | ENGINEERING | 0.0
    """)
void testCalculateBonus(String scenario, Level level, Department department, double expectedBonus) {
    Employee employee = new Employee(level, department);
    double bonus = bonusCalculator.calculateBonus(employee);
    assertThat(bonus).isEqualTo(expectedBonus);
}
```

### Notes

- The `Bonus%?` column ends with `?` to mark it as the output/result column.
- CONTRACTOR rows show 0.0 for both SALES and ENGINEERING departments, demonstrating that the department has no effect on the outcome for contractors.
- All four level×department combinations for SENIOR and JUNIOR are covered.
- The test method has no if/switch logic — all variation is driven by the table data.
