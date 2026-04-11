Working through the requirements-to-tables workflow:

**Step 1 — Concern:** "Determine bonus percentage by employee level and department" — one concern.

**Steps 3-4 — Variations & irrelevant inputs:** The CONTRACTOR rule says "regardless of department" — that's a value set. The four non-contractor rules are a 2×2 matrix.

**Step 5 — Rules vs arithmetic:** Pure lookup, no arithmetic to separate.

**Step 8 — Open questions:** No specification for level/department combinations beyond those listed.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Description;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Bonus percentage by employee level and department")
    @Description("""
        CONTRACTOR level receives 0% regardless of department.
        Open: behaviour for level/department combinations not listed here
        (e.g., new departments or levels added in future) is unspecified.
        """)
    @TableTest("""
        Scenario                   | Level      | Department           | Bonus %?
        Senior in Sales            | SENIOR     | SALES                | 15.0
        Senior in Engineering      | SENIOR     | ENGINEERING          | 12.0
        Junior in Sales            | JUNIOR     | SALES                | 8.0
        Junior in Engineering      | JUNIOR     | ENGINEERING          | 5.0
        Contractor, any department | CONTRACTOR | {SALES, ENGINEERING} | 0.0
        """)
    void shouldCalculateBonusPercentage(Level level, Department department, double expectedBonus) {
        Employee employee = new Employee(level, department);
        assertEquals(expectedBonus, calculator.calculateBonusPercentage(employee));
    }
}
```

A few things worth noting:

- **Value set on the CONTRACTOR row** (`{SALES, ENGINEERING}`) makes the "regardless of department" rule explicit and machine-verifiable — it generates two test cases, both asserting 0%.
- **JUnit auto-converts** `String → Level` and `String → Department` since they're enums — no `@TypeConverter` needed.
- **`@Description`** records the open question about future level/department combinations rather than silently ignoring it.

Adjust `BonusCalculator`, `Level`, `Department`, and the `Employee` constructor to match your actual class names.