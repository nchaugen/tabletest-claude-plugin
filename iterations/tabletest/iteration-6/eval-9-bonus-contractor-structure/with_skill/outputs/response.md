Here's a clean TableTest for `calculateBonus`. The CONTRACTOR rule ("regardless of department") is a natural fit for a value set.

```java
@Description("Bonus rates by employee level and department. "
    + "CONTRACTOR always receives 0% regardless of department.")
@TableTest("""
    Scenario              | Level      | Department             | Bonus %?
    Senior in Sales       | SENIOR     | SALES                  | 15.0
    Senior in Engineering | SENIOR     | ENGINEERING            | 12.0
    Junior in Sales       | JUNIOR     | SALES                  | 8.0
    Junior in Engineering | JUNIOR     | ENGINEERING            | 5.0
    Contractor            | CONTRACTOR | {SALES, ENGINEERING}   | 0.0
    """)
void shouldCalculateBonus(Level level, Department department, double expectedBonus) {
    Employee employee = new Employee(level, department);
    assertEquals(expectedBonus, calculateBonus(employee), 0.0001);
}
```

Key design decisions:

- **Value set on the CONTRACTOR row** — `{SALES, ENGINEERING}` generates two tests, both asserting `0.0`. This directly encodes the "regardless of department" rule rather than picking an arbitrary department or adding a redundant second row.
- **`@Description`** captures the rule that isn't visible in the table (the "why" for CONTRACTOR being 0%).
- **`delta` in `assertEquals`** — `double` arithmetic can produce floating-point noise; the small delta guards against that without masking real failures.

One assumption to verify: the method returns the bonus as a percentage value (e.g. `15.0` for 15%), not a decimal multiplier (`0.15`). Adjust the `Bonus %?` column values if your implementation returns a different unit.