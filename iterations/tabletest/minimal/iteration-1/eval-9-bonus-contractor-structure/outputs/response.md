Working through the design steps before writing code.

**Step 1 — One concern**: "Determine bonus percentage by level and department"

**Step 2-3 — Variations**: 4 level+department combinations, plus the CONTRACTOR override

**Step 4 — Irrelevant inputs**: CONTRACTOR is "regardless of department" → value set `{SALES, ENGINEERING}`

**Step 5 — No arithmetic**: Pure lookup, one table is sufficient

**Step 9 — Code**:

JUnit's built-in enum conversion handles `Level` and `Department` directly by name — no `@TypeConverter` needed. The test method constructs `Employee` from two enum parameters.

```java
import org.tabletest.junit.TableTest;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeBonusTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Bonus percentage by level and department")
    @TableTest("""
        Scenario                         | Level      | Department           | Bonus?
        Senior in sales                  | SENIOR     | SALES                | 15.0
        Senior in engineering            | SENIOR     | ENGINEERING          | 12.0
        Junior in sales                  | JUNIOR     | SALES                | 8.0
        Junior in engineering            | JUNIOR     | ENGINEERING          | 5.0
        Contractor regardless of dept    | CONTRACTOR | {SALES, ENGINEERING} | 0.0
        """)
    void calculatesBonusPercentage(Level level, Department department, double bonus) {
        Employee employee = new Employee(level, department);
        assertEquals(bonus, calculator.calculateBonusPercentage(employee));
    }
}
```

A few things to adapt to your codebase:
- Replace `Level` / `Department` with your actual enum types (e.g. `Employee.Level`, `EmployeeLevel`, etc.)
- Replace `new Employee(level, department)` with however your `Employee` is constructed
- Replace `BonusCalculator` / `calculator` with the actual class under test

The CONTRACTOR row generates two test cases — one per department — both asserting `0.0`.