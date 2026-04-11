No existing Java project found — I'll provide the dependency and the full test. Here's my table design first:

**Design reasoning:**
- 4 rows for the SENIOR/JUNIOR × SALES/ENGINEERING combinations
- 1 row with a value set for CONTRACTOR (department is irrelevant — value set expresses this declaratively)

```java
import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusCalculatorTest {

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
        assertEquals(expectedBonus, calculateBonusPercentage(employee));
    }
}
```

**Dependency** (add to `pom.xml` / `build.gradle` if not present — check Maven Central for the latest version):
```xml
<!-- Maven -->
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```
```groovy
// Gradle
testImplementation "org.tabletest:tabletest-junit:VERSION"
```

**A few things to verify before running:**
- Replace `Level`, `Department` with your actual enum type names — JUnit's built-in enum conversion will handle the string-to-enum mapping automatically.
- Replace `new Employee(level, department)` with your actual constructor or factory method.
- The `{SALES, ENGINEERING}` value set generates two test cases for the CONTRACTOR row, both asserting `0.0` — this is the right way to express "regardless of department".