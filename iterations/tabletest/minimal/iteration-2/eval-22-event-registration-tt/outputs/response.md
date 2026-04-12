Looking at the requirements, I'll work through the design steps before writing code.

**Step 1 — Name each concern:**
1. Validate registration inputs (name required, email format)
2. Optional fields are accepted (null/non-null)
3. Apply the correct discount and compute final price

**Step 2–4 — Sketch tables, find variations, probe irrelevant inputs:**

*Validation:* null name, blank name, various email formats. Date/group irrelevant — fix them as constants.

*Pricing:* cutoff is 2025-03-01 (before = early-bird eligible). Group threshold is 5. Key boundaries: groupSize 4 vs 5; date 2025-02-28 vs 2025-03-01. Value sets apply where "regardless of group size" or similar.

**Step 5 — Separate rules from arithmetic:** The discount selection is the rule; the price is derivable from it. Since `RegistrationResult` likely doesn't expose the discount directly, I'll verify price as the observable output and make the rule visible through scenario names and @Description.

**Step 9 — Write the code:**

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class EventRegistrationTest {

    private final EventRegistrationService service = new EventRegistrationService();

    private static final String VALID_NAME = "Alice Smith";
    private static final String VALID_EMAIL = "alice@example.com";

    @DisplayName("Optional fields accepted when null or populated")
    @Description("""
            name is "Alice Smith", email is "alice@example.com", registrationDate is 2025-04-01, groupSize is 1.
            """)
    @TableTest("""
            Scenario                    | Dietary Requirements | Accessibility Needs
            Both optional fields null   |                      |
            Dietary requirements given  | Vegan                |
            Accessibility needs given   |                      | Wheelchair access
            Both optional fields given  | Gluten-free          | Hearing loop
            """)
    void acceptsOptionalFields(String dietaryRequirements, String accessibilityNeeds) {
        RegistrationResult result = service.register(
                VALID_NAME, VALID_EMAIL, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 4, 1), 1);
        assertTrue(result.isAccepted());
    }

    @DisplayName("Validation rejects registrations with missing name or invalid email")
    @Description("""
            dietaryRequirements and accessibilityNeeds are null, registrationDate is 2025-04-01, groupSize is 1.
            Open: when both name and email are invalid, does the error message mention name, email, or both?
            The "Both invalid" row assumes name is validated first.
            """)
    @TableTest("""
            Scenario                    | Name        | Email             | Error?
            Null name                   |             | alice@example.com | name
            Blank name                  | ''          | alice@example.com | name
            Email missing @             | Alice Smith | notanemail        | email
            Email missing domain part   | Alice Smith | alice@            | email
            Email missing local part    | Alice Smith | @example.com      | email
            Both name and email invalid |             | notanemail        | name
            """)
    void rejectsInvalidInputs(String name, String email, String errorSubstring) {
        RegistrationResult result = service.register(
                name, email, null, null, LocalDate.of(2025, 4, 1), 1);
        assertFalse(result.isAccepted());
        assertThat(result.getErrorMessage()).containsIgnoringCase(errorSubstring);
    }

    @DisplayName("Pricing applies early-bird and group discounts")
    @Description("""
            Base price is £100.00.
            Early-bird: 20% off (£80.00) when registrationDate is before 2025-03-01 (cutoff is exclusive).
            Group: 15% off (£85.00) when groupSize is 5 or more.
            When both apply, only the higher discount (early-bird 20%) is used — discounts do not stack.
            name is "Alice Smith" and email is "alice@example.com" for all rows.
            """)
    @TableTest("""
            Scenario                             | Registration Date | Group Size | Price?
            No discount                          | after cutoff      | 1          | 100.00
            Just below group threshold           | after cutoff      | 4          | 100.00
            No early-bird on cutoff date         | 2025-03-01        | 1          | 100.00
            Early-bird only                      | before cutoff     | {1, 4}     | 80.00
            Early-bird on day before cutoff      | 2025-02-28        | 1          | 80.00
            Group discount only                  | after cutoff      | {5, 10}    | 85.00
            Both apply, early-bird wins          | before cutoff     | {5, 10}    | 80.00
            """)
    void appliesDiscount(LocalDate registrationDate, int groupSize, BigDecimal price) {
        RegistrationResult result = service.register(
                VALID_NAME, VALID_EMAIL, null, null, registrationDate, groupSize);
        assertTrue(result.isAccepted());
        assertThat(result.getPrice()).isEqualByComparingTo(price);
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String value) {
        return switch (value) {
            case "before cutoff" -> LocalDate.of(2025, 2, 1);
            case "after cutoff" -> LocalDate.of(2025, 4, 1);
            default -> LocalDate.parse(value);
        };
    }

    @TypeConverter
    public static BigDecimal parsePrice(String value) {
        return new BigDecimal(value);
    }
}
```

**Key design decisions:**

- **Three tables** for three distinct concerns — optional fields, validation, and pricing. Validation and pricing are separate because they use different inputs and different assertions.
- **Value sets** on group size: `{1, 4}` in "Early-bird only" asserts that early-bird applies *regardless of* group size (as long as group < 5); `{5, 10}` in group/both rows asserts the group threshold effect holds across group sizes.
- **Boundary rows**: groupSize 4 (below threshold) and 5 (at threshold); 2025-02-28 (day before cutoff) and 2025-03-01 (on cutoff, no early-bird).
- **@TypeConverter** handles the human-readable date labels alongside raw ISO dates in the same column, keeping the table readable without losing precision at boundaries.
- **Open question in @Description**: the "both invalid" scenario's error message ordering is implementation-defined and flagged rather than silently resolved.