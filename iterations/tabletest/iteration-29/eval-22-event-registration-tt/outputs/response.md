Working through the requirements:

**Concerns identified:**
1. **Validate registration** — name required, email format, optional fields
2. **Apply pricing** — early-bird vs group discount, higher wins

**Boundary analysis:**
- Early-bird: "before 2025-03-01" → 2025-02-28 qualifies, 2025-03-01 does not
- Group: "5 or more" → 4 does not qualify, 5 does
- Both discounts: early-bird (20%) always beats group (15%) — one "both apply" row covers it

**Fixed inputs per table:** date/group size don't affect validation (hardcoded in method body); name/email don't affect pricing (hardcoded in method body).

**TypeConverter needed:** `BigDecimal` for prices — no built-in JUnit converter.

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventRegistrationTest {

    private EventRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new EventRegistrationService();
    }

    @DisplayName("Registration validation")
    @Description("""
            Registration date (2025-05-01) and group size (1) are fixed — they do not affect validation.
            Error message wording is the expected contract; adjust to match implementation.
            Open: should a whitespace-only name be rejected?
            Open: what pattern constitutes a valid email format?
            Open: which error is returned when both name and email are invalid?
            """)
    @TableTest("""
            Scenario                    | Name       | Email            | Dietary Reqs | Accessibility | Accepted? | Error Message?
            Valid, no optional fields   | John Smith | john@example.com |              |               | true      |
            With dietary requirements   | John Smith | john@example.com | Vegetarian   |               | true      |
            With accessibility needs    | John Smith | john@example.com |              | Wheelchair    | true      |
            Name is null                |            | john@example.com |              |               | false     | Name is required
            Name is empty               | ''         | john@example.com |              |               | false     | Name is required
            Email is null               | John Smith |                  |              |               | false     | Invalid email format
            Email missing @ symbol      | John Smith | notanemail       |              |               | false     | Invalid email format
            Email with no domain part   | John Smith | user@            |              |               | false     | Invalid email format
            """)
    void shouldValidateRegistration(String name, String email, String dietaryRequirements,
                                    String accessibilityNeeds, boolean accepted, String errorMessage) {
        var result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 5, 1), 1);
        assertEquals(accepted, result.isAccepted());
        assertEquals(errorMessage, result.getErrorMessage());
    }

    @DisplayName("Registration pricing")
    @Description("""
            Base price is £100. Early-bird (before 2025-03-01): 20% off → £80.
            Group (5 or more): 15% off → £85.
            When both apply, only the higher discount (early-bird, 20%) is used — they do not stack.
            Name ("John Smith") and email ("john@example.com") are fixed valid values for all rows.
            """)
    @TableTest("""
            Scenario                       | Registration Date | Group Size | Final Price?
            No discount                    | 2025-05-01        | 1          | 100.00
            Early-bird, day before cutoff  | 2025-02-28        | 1          | 80.00
            On cutoff date, no early-bird  | 2025-03-01        | 1          | 100.00
            Group of 4, below threshold    | 2025-05-01        | 4          | 100.00
            Group of 5, discount applies   | 2025-05-01        | 5          | 85.00
            Both apply, early-bird wins    | 2025-02-28        | 5          | 80.00
            """)
    void shouldApplyPricing(LocalDate registrationDate, int groupSize, BigDecimal finalPrice) {
        var result = service.register("John Smith", "john@example.com", null, null,
                registrationDate, groupSize);
        assertTrue(result.isAccepted());
        assertEquals(0, finalPrice.compareTo(result.getPrice()));
    }

    @TypeConverter
    static BigDecimal parseBigDecimal(String value) {
        return new BigDecimal(value);
    }
}
```

A few things to adjust for your codebase:

- **`EventRegistrationService`** — replace with your actual class name
- **`result.isAccepted()` / `result.getErrorMessage()` / `result.getPrice()`** — adjust to match your `RegistrationResult` API
- **`BigDecimal.compareTo`** is used instead of `assertEquals` to avoid scale mismatches (e.g. `100.00` vs `100`)
- The three open questions in the validation `@Description` are worth resolving before implementing — particularly the email format question, since that affects which "invalid" rows pass