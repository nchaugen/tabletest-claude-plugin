No Java files yet, so I'll write the test based purely on the rules you described. Following the Requirements to Tables workflow:

**Step 1 — One concern:** "Determine bonus percentage by employee level and department"

**Step 2-3 — Examples and variations:** 4 level×department combinations, plus CONTRACTOR as a "regardless of" case.

**Step 4 — Irrelevant inputs:** CONTRACTOR's department is irrelevant → value set `{SALES, ENGINEERING}`.

**Step 5-8 — No arithmetic, no state, no open questions.**

Here's the TableTest:

```java
import org.tabletest.junit.TableTest;

public class BonusCalculatorTest {

    @DisplayName("Bonus percentage by employee level and department")
    @TableTest("""
        Scenario                    | Level      | Department             | Bonus %?
        Senior sales rep            | SENIOR     | SALES                  | 15.0
        Senior engineer             | SENIOR     | ENGINEERING            | 12.0
        Junior sales rep            | JUNIOR     | SALES                  | 8.0
        Junior engineer             | JUNIOR     | ENGINEERING            | 5.0
        Contractor regardless of dept | CONTRACTOR | {SALES, ENGINEERING} | 0.0
        """)
    void shouldCalculateBonusPercentage(Level level, Department department, double expectedBonus) {
        Employee employee = new Employee(level, department);
        assertEquals(expectedBonus, calculateBonusPercentage(employee));
    }
}
```

**Key design decisions:**
- `Level` and `Department` as separate columns — the rule is a 2D lookup, so both inputs must be visible
- Value set `{SALES, ENGINEERING}` on the CONTRACTOR row asserts that department is irrelevant for contractors — this is a first-class assertion, not just a convenience
- `Bonus %?` uses a `double` (JUnit converts it directly), keeping the method body free of parsing logic
- The CONTRACTOR row generates 2 test cases, one per department value