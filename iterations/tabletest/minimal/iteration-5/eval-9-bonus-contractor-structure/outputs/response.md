No Java source files exist in this project — it's a skills/evals repo, not a Java project. I'll write the TableTest based on the rules you've described.

The bonus rules map cleanly to two inputs (`level` and `department`) with one output (`bonusPercentage`). CONTRACTOR is a "regardless of" case — a value set is ideal.

```java
@TableTest("""
    Scenario                   | Level      | Department  | Bonus %?
    Senior Sales               | SENIOR     | SALES       | 15.0
    Senior Engineering         | SENIOR     | ENGINEERING | 12.0
    Junior Sales               | JUNIOR     | SALES       | 8.0
    Junior Engineering         | JUNIOR     | ENGINEERING | 5.0
    Contractor, any department | CONTRACTOR | {SALES, ENGINEERING} | 0.0
    """)
void shouldCalculateBonusPercentage(Level level, Department department, double bonusPercentage) {
    Employee employee = new Employee(level, department);
    assertEquals(bonusPercentage, calculateBonusPercentage(employee));
}
```

Key design decisions:
- **Value set on the CONTRACTOR row** — `{SALES, ENGINEERING}` expresses "regardless of department" declaratively, generating two tests that both assert `0.0`. This is valid because every value in the set produces the same result.
- **`Bonus %?` as the output column** — directly names what the rule decides, traceable to the inputs.
- **Two columns, not one** — `Level` and `Department` are independent inputs; no need to collapse them.

If `Employee` has no direct constructor, swap `new Employee(level, department)` with your actual builder or factory. If `Level`/`Department` are enums, JUnit converts them automatically by name — no `@TypeConverter` needed.