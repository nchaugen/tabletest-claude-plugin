Now writing the complete Java class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventRegistrationTest {

    // Replace with your actual class name
    private final EventRegistrationService service = new EventRegistrationService();

    @DisplayName("Registration input validation")
    @Description("""
            Fixed for all rows: registrationDate = 2025-06-01, groupSize = 1,
            dietaryRequirements = null, accessibilityNeeds = null.
            Open: adjust the Error message? values to match your implementation's
            exact message strings.
            """)
    @TableTest("""
            Scenario                       | Name        | Email             | Accepted? | Error message?
            Valid registration             | Alice Smith | alice@example.com | true      |
            Null name                      |             | alice@example.com | false     | Name is required
            Blank name                     | '   '       | alice@example.com | false     | Name is required
            Invalid email - no at sign     | Alice Smith | notanemail        | false     | Invalid email format
            Invalid email - missing domain | Alice Smith | alice@            | false     | Invalid email format
            Null email                     | Alice Smith |                   | false     | Invalid email format
            """)
    void shouldValidateRegistrationInput(String name, String email, boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(
                name, email, null, null, LocalDate.of(2025, 6, 1), 1);
        assertEquals(accepted, result.isAccepted());
        assertEquals(errorMessage, result.getErrorMessage());
    }

    @DisplayName("Registration pricing and discounts")
    @Description("""
            Base price: £100.00. All rows use valid inputs (validation is tested separately).
            Fixed for all rows: name = "Alice Smith", email = "alice@example.com",
            dietaryRequirements = null, accessibilityNeeds = null.
            Early-bird: before 2025-03-01 → 20% off → £80.00.
            Group discount: 5 or more → 15% off → £85.00.
            When both apply, only the larger discount is used; they do not stack.
            """)
    @TableTest("""
            Scenario                              | Registration date | Group size | Price?
            Early-bird (day before cutoff)        | 2025-02-28        | 1          | 80.00
            Cutoff day is not early-bird          | 2025-03-01        | 1          | 100.00
            Group of 4 - no group discount        | 2025-06-01        | 4          | 100.00
            Group of 5 - group discount applies   | 2025-06-01        | 5          | 85.00
            Both apply - early-bird wins (higher) | 2025-02-28        | 5          | 80.00
            """)
    void shouldApplyPricingDiscounts(LocalDate registrationDate, int groupSize, BigDecimal price) {
        RegistrationResult result = service.register(
                "Alice Smith", "alice@example.com", null, null, registrationDate, groupSize);
        assertTrue(result.isAccepted());
        assertEquals(0, price.compareTo(result.getPrice()),
                () -> "Expected £" + price + " but was £" + result.getPrice());
    }
}
```

---

A few notes for wiring this up:

**Dependency** (if not already present — replace `VERSION` with the latest from Maven Central):
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**Things to adjust to match your implementation:**
- `EventRegistrationService` → your actual class name
- `result.isAccepted()` / `result.getErrorMessage()` / `result.getPrice()` → your actual `RegistrationResult` method names
- Error message strings in the validation table (`"Name is required"`, `"Invalid email format"`) → your implementation's exact messages
- The `compareTo` assertion handles `BigDecimal` scale differences (e.g., `100` vs `100.00`); if your `getPrice()` returns a `double`, replace the assertion with `assertEquals(price.doubleValue(), result.getPrice(), 0.001)`

**Boundary decisions made explicit by the table:**
- 2025-02-28 (day before) → early-bird; 2025-03-01 (cutoff day itself) → no early-bird — confirming "before" is exclusive of the cutoff
- Group=4 → no discount; Group=5 → discount — confirming 5 is inclusive