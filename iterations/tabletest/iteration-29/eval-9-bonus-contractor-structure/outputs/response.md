Working through the requirements analysis steps before writing any code.

**Step 1 — Name each concern:** One concern: "Calculate bonus percentage." No "and" needed — single `@TableTest`.

**Step 2 — First example:** SENIOR + SALES → 15.0%

**Step 3 — Variations:** All four level+department combinations, plus the CONTRACTOR override.

**Step 4 — Irrelevant inputs:** CONTRACTOR is "regardless of department" — use a value set `{SALES, ENGINEERING}` to make that assertion explicit and visible, rather than listing separate rows.

**Step 5 — Rules vs arithmetic:** Pure lookup table, no arithmetic to separate.

**Step 8 — Open questions:** None — requirements are fully specified for all combinations.

Here is the complete `@TableTest`:

```java
class BonusCalculationTest {

    // Replace with however you obtain the class under test
    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Bonus percentage by level and department")
    @TableTest("""
        Scenario                      | Level      | Department           | Bonus %?
        Senior in Sales               | SENIOR     | SALES                | 15.0
        Senior in Engineering         | SENIOR     | ENGINEERING          | 12.0
        Junior in Sales               | JUNIOR     | SALES                | 8.0
        Junior in Engineering         | JUNIOR     | ENGINEERING          | 5.0
        Contractor regardless of dept | CONTRACTOR | {SALES, ENGINEERING} | 0.0
        """)
    void shouldCalculateBonusPercentage(Level level, Department department, double bonusPercentage) {
        Employee employee = new Employee(level, department);
        assertEquals(bonusPercentage, calculator.calculateBonusPercentage(employee));
    }
}
```

Key design decisions:

- **Value set on CONTRACTOR row** — `{SALES, ENGINEERING}` generates two tests and asserts that department is irrelevant for contractors, rather than just having one row that silently omits the department.
- **No `@Description`** — the table is fully self-explanatory; adding one would only restate what the rows already show.
- **`Bonus %?` suffix** — marks the expectation column clearly.

You'll need to adjust `BonusCalculator`, `Level`, `Department`, and the `Employee` constructor to match your actual class names.