No existing Java source to work from, so I'll write the test as a standalone file. Here's the complete test class:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class EventRegistrationTest {

    @DisplayName("Registration validation")
    @Description("""
            Registration date is 2025-03-15 and group size is 1 for all rows (standard pricing, no discounts).
            Error? shows the exact error message returned for rejected registrations; blank means no error.
            """)
    @TableTest("""
            Scenario                    | Name        | Email             | Dietary req. | Accessibility needs | Accepted? | Error?
            Valid, no optional fields   | Alice Smith | alice@example.com |              |                     | true      |
            Valid, with optional fields | Alice Smith | alice@example.com | Vegan        | Wheelchair ramp     | true      |
            Null name                   |             | alice@example.com |              |                     | false     | Name is required
            Empty name                  | ''          | alice@example.com |              |                     | false     | Name is required
            Null email                  | Alice Smith |                   |              |                     | false     | Invalid email format
            Empty email                 | Alice Smith | ''                |              |                     | false     | Invalid email format
            Email missing @             | Alice Smith | aliceexample.com  |              |                     | false     | Invalid email format
            Email missing domain        | Alice Smith | alice@            |              |                     | false     | Invalid email format
            """)
    void validateRegistration(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                              boolean accepted, String error) {
        RegistrationResult result = register(name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 3, 15), 1);
        assertThat(result.isAccepted()).isEqualTo(accepted);
        assertThat(result.getErrorMessage()).isEqualTo(error);
    }

    @DisplayName("Registration pricing")
    @Description("""
            Base price is £100. Name is 'Alice Smith', email is 'alice@example.com',
            dietary requirements and accessibility needs are null for all rows.
            Early-bird (20% off) applies for registrations before 2025-03-01.
            Group discount (15% off) applies for 5 or more attendees.
            When both discounts qualify, only the higher discount (early-bird) is applied.
            """)
    @TableTest("""
            Scenario                     | Registration date | Group size | Price?
            No discount                  | after cutoff      | 1          | 100.00
            Early-bird only              | before cutoff     | 1          | 80.00
            Group only                   | after cutoff      | 5          | 85.00
            Both apply - early-bird wins | before cutoff     | 5          | 80.00
            On cutoff date - no discount | on cutoff         | 1          | 100.00
            Just below group threshold   | after cutoff      | 4          | 100.00
            """)
    void registrationPricing(LocalDate registrationDate, int groupSize, BigDecimal price) {
        RegistrationResult result = register("Alice Smith", "alice@example.com", null, null,
                registrationDate, groupSize);
        assertThat(result.isAccepted()).isTrue();
        assertThat(result.getPrice()).isEqualByComparingTo(price);
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String input) {
        return switch (input) {
            case "before cutoff" -> LocalDate.of(2025, 2, 28);
            case "on cutoff"     -> LocalDate.of(2025, 3, 1);
            case "after cutoff"  -> LocalDate.of(2025, 3, 15);
            default              -> LocalDate.parse(input);
        };
    }
}
```

**Key design decisions:**

- **Validation and pricing are separate tables** — they test different concerns with different fixed values. Each table only varies what's relevant to its rule.

- **`Error?` holds the exact expected error message** (or blank = null for accepted). This lets the method body be a plain `isEqualTo` with no conditional — the table drives both the accepted and rejected assertions symmetrically.

- **Descriptive date labels** (`before cutoff`, `on cutoff`, `after cutoff`) via `@TypeConverter` instead of raw ISO dates. The reader can immediately see which side of the boundary each row is on without mentally comparing `2025-02-28` against `2025-03-01`.

- **Both discount boundaries are explicit rows** — "on cutoff" verifies the cutoff is exclusive (2025-03-01 is NOT early-bird), and "just below group threshold" verifies 4 does not qualify. The "both apply" row explicitly proves the higher-discount-wins rule.

- **Price math** — all values independently verifiable: £100 × 0.80 = £80 (early-bird), £100 × 0.85 = £85 (group), £100 × 1.00 = £100 (none).