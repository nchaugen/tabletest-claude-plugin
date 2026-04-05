Working through the requirements analysis now before writing the code.

**Step 1 — One concern:** "Calculate bonus percentage" — single behavior, no "and".

**Steps 2–3 — Variations:** Five distinct rule combinations (SENIOR/JUNIOR × SALES/ENGINEERING) plus the CONTRACTOR override.

**Step 4 — Irrelevant inputs:** The requirement explicitly says CONTRACTOR is 0% *regardless of department* — this must be a value set column, not a silent omission.

**Step 5 — Rules vs arithmetic:** Pure lookup — no arithmetic to separate.

**Step 8 — Open questions:** None; all combinations are fully specified.

**Step 9 — Code:**

```java
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.tabletest.junit.TableTest;

class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Bonus percentage by level and department")
    @TableTest("""
        Scenario                  | Level      | Department            | Bonus %?
        Senior in Sales           | SENIOR     | SALES                 | 15.0
        Senior in Engineering     | SENIOR     | ENGINEERING           | 12.0
        Junior in Sales           | JUNIOR     | SALES                 | 8.0
        Junior in Engineering     | JUNIOR     | ENGINEERING           | 5.0
        Contractor                | CONTRACTOR | {SALES, ENGINEERING}  | 0.0
        """)
    void shouldCalculateBonusPercentage(Level level, Department department, double expectedBonus) {
        Employee employee = new Employee(level, department);
        assertEquals(expectedBonus, calculator.calculateBonusPercentage(employee), 0.001);
    }
}
```

Key design decisions:
- **`Level` and `Department` as separate columns** — JUnit converts enum names automatically, keeping the test method free of parsing logic.
- **Value set `{SALES, ENGINEERING}` on the CONTRACTOR row** — makes the "regardless of department" assertion explicit and verifiable, rather than hiding it by testing only one department.
- **No `@Description`** — the table is self-explanatory; adding one would only restate the column names.