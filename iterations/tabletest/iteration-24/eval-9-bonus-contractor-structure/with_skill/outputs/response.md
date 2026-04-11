Now I'll work through the requirements-to-tables.md workflow mentally before writing the code.

**Step 1 — Name the concern:** "Calculate bonus percentage" — one concern, no "and" needed.

**Step 2–3 — Variations:** Five distinct outcomes: SENIOR×SALES, SENIOR×ENGINEERING, JUNIOR×SALES, JUNIOR×ENGINEERING, CONTRACTOR×any.

**Step 4 — Irrelevant inputs:** The requirements explicitly say CONTRACTOR is "regardless of department" — this calls for a value set `{SALES, ENGINEERING}` on the Department column for that row.

**Step 5 — Rules vs arithmetic:** The output IS the percentage (a direct lookup), so no separate arithmetic table needed.

**Step 8 — Open questions:** Behavior for level/department combinations not in the spec (e.g. a new department, a new level) is unspecified — worth noting in `@Description`.

```java
package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Bonus percentage by level and department")
    @Description("""
            Open: bonus percentage for level/department combinations not listed
            here (e.g. a new department or a new level) is not specified.
            """)
    @TableTest("""
            Scenario                    | Level      | Department           | Bonus %?
            Senior in sales             | SENIOR     | SALES                | 15.0
            Senior in engineering       | SENIOR     | ENGINEERING          | 12.0
            Junior in sales             | JUNIOR     | SALES                | 8.0
            Junior in engineering       | JUNIOR     | ENGINEERING          | 5.0
            Contractor, dept irrelevant | CONTRACTOR | {SALES, ENGINEERING} | 0.0
            """)
    void shouldCalculateBonusPercentage(EmployeeLevel level, Department department, double bonusPercentage) {
        Employee employee = new Employee(level, department);
        assertEquals(bonusPercentage, calculator.calculateBonusPercentage(employee));
    }
}
```

A few notes on the design:

- **Value set on row 5** — `{SALES, ENGINEERING}` generates two test executions, both asserting `0.0`. This makes the "regardless of department" rule visible and verifiable in the table itself, rather than just being implied by a single row.
- **`@Description`** — only notes the open question about unspecified combinations. It doesn't restate what the table already shows.
- **No `@TypeConverter` needed** — JUnit's built-in converter handles enum values (`SENIOR`, `SALES`, etc.) automatically.
- **`Employee` constructor assumed** as `new Employee(level, department)` — adjust to match your actual constructor or builder if it differs.