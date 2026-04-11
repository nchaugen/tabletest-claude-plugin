Here's the complete test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EventRegistrationTest {

    EventRegistrationService registration = new EventRegistrationService();

    @DisplayName("Registration validation")
    @Description("""
            Registration date is 2025-04-01 and group size is 1 for all rows;
            neither affects validation outcomes.
            Open: exact wording of error messages is not specified — table shows
            representative text to be confirmed against the implementation.
            Open: when both name and email are invalid, name is assumed to be
            validated first.
            """)
    @TableTest("""
            Scenario                                  | Name        | Email             | Dietary  | Accessibility | Accepted? | Error?
            Valid, no optional fields                 | Alice Smith | alice@example.com |          |               | true      |
            Valid, with optional fields               | Bob Jones   | bob@example.com   | vegan    | wheelchair    | true      |
            Blank name                                | ''          | alice@example.com |          |               | false     | Name is required
            Null name                                 |             | alice@example.com |          |               | false     | Name is required
            Email missing at sign                     | Alice Smith | notanemail        |          |               | false     | Invalid email
            Email missing domain                      | Alice Smith | alice@            |          |               | false     | Invalid email
            Blank email                               | Alice Smith | ''                |          |               | false     | Invalid email
            Null email                                | Alice Smith |                   |          |               | false     | Invalid email
            Both invalid, name error reported first   | ''          | notanemail        |          |               | false     | Name is required
            """)
    void shouldValidateRegistration(String name, String email, String dietaryRequirements,
                                    String accessibilityNeeds, boolean accepted, String errorMessage) {
        RegistrationResult result = registration.register(
                name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 4, 1), 1);
        assertThat(result.isAccepted()).isEqualTo(accepted);
        assertThat(result.getErrorMessage()).isEqualTo(errorMessage);
    }

    @DisplayName("Registration pricing")
    @Description("""
            Base price is £100.00. Name is "Alice Smith", email is "alice@example.com",
            dietary and accessibility fields are null for all rows.
            Early-bird discount: 20% off (→ £80.00) for registrations strictly before 2025-03-01.
            Group discount: 15% off (→ £85.00) for groups of 5 or more.
            When both apply, only the higher discount is used — early-bird (20%) always wins
            over group (15%); discounts do not stack.
            Open: 2025-03-01 itself is treated as NOT early-bird ("before" is strictly less than).
            """)
    @TableTest("""
            Scenario                                    | Registration date | Group size | Price?
            No discount                                 | 2025-04-01        | 1          | 100.00
            On early-bird cutoff, not eligible          | 2025-03-01        | 1          | 100.00
            Day before cutoff, early-bird applies       | 2025-02-28        | 1          | 80.00
            Early-bird, group just below minimum (4)    | 2025-01-15        | 4          | 80.00
            Group just below minimum (4), no discount   | 2025-04-01        | 4          | 100.00
            Group at minimum (5), no early-bird         | 2025-04-01        | 5          | 85.00
            Large group, no early-bird                  | 2025-06-01        | 10         | 85.00
            Both apply, early-bird wins                 | 2025-02-28        | {5, 10}    | 80.00
            """)
    void shouldCalculatePrice(LocalDate registrationDate, int groupSize, BigDecimal expectedPrice) {
        RegistrationResult result = registration.register(
                "Alice Smith", "alice@example.com", null, null, registrationDate, groupSize);
        assertThat(result.getPrice()).isEqualByComparingTo(expectedPrice);
    }

    @TypeConverter
    static BigDecimal parseMoney(String value) {
        return new BigDecimal(value);
    }
}
```

**Two tables, separated by concern:**

**`shouldValidateRegistration`** — tests all validation rules. `Dietary` and `Accessibility` are blank (null) by default; one row shows they're accepted when present. The `Error?` column is null for accepted rows — `isEqualTo(null)` handles that without any if/switch. Fixed date and group size are documented in `@Description` so they don't clutter the table.

**`shouldCalculatePrice`** — tests the discount rules with explicit boundary rows:
- 2025-03-01 (cutoff, NOT early-bird) vs 2025-02-28 (last eligible day)
- Group size 4 (below minimum, no discount) vs 5 (at minimum)
- `{5, 10}` value set on the "both apply" row asserts that early-bird wins *regardless of* how large the group is — one row, two generated tests

`BigDecimal` needs the `@TypeConverter` since JUnit doesn't convert it automatically. `LocalDate` in ISO format (`yyyy-MM-dd`) is handled automatically.

**Open questions surfaced** (in `@Description`):\
— Exact error message wording needs confirming with the implementation\
— Validation order when both name and email are invalid\
— Inclusive/exclusive treatment of the 2025-03-01 cutoff (assumed exclusive)