Here's the complete test class — paste this into your project:

```java
package com.example.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class EventRegistrationTest {

    private EventRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new EventRegistrationService();
    }

    @DisplayName("Input validation")
    @Description("""
            registrationDate = 2025-03-15, groupSize = 1 for all rows.
            dietaryRequirements and accessibilityNeeds are optional — null is always accepted
            and does not affect validation outcomes.
            """)
    @TableTest("""
            Scenario               | Name        | Email             | Accepted? | Error?
            Valid details          | Alice Smith | alice@example.com | true      |
            Null name              |             | alice@example.com | false     | name is required
            Blank name             | '   '       | alice@example.com | false     | name is required
            Missing @ in email     | Alice Smith | notanemail        | false     | invalid email
            No domain in email     | Alice Smith | user@             | false     | invalid email
            No local part in email | Alice Smith | @example.com      | false     | invalid email
            """)
    void validates_name_and_email(String name, String email, boolean accepted, String error) {
        RegistrationResult result = service.register(name, email, null, null, LocalDate.of(2025, 3, 15), 1);
        assertThat(result.isAccepted()).isEqualTo(accepted);
        if (error != null) {
            assertThat(result.getErrorMessage()).containsIgnoringCase(error);
        }
    }

    @DisplayName("Registration pricing")
    @Description("""
            Base price is £100. Early-bird discount (20% off) applies for registrations before
            2025-03-01. Group discount (15% off) applies for groups of 5 or more. When both
            qualify, only the higher discount (early-bird at 20%) is used — they do not stack.
            Fixed for all rows: name = 'Alice Smith', email = 'alice@example.com'.
            """)
    @TableTest("""
            Scenario                      | Registration date | Group size | Price?
            No discount                   | after cutoff      | 1          | 100.00
            Early-bird only               | before cutoff     | 1          |  80.00
            Group discount only           | after cutoff      | 5          |  85.00
            Both qualify, early-bird wins | before cutoff     | 5          |  80.00
            Cutoff day, no early-bird     | on cutoff         | 1          | 100.00
            Group of 4, no group discount | after cutoff      | 4          | 100.00
            """)
    void prices_registration(LocalDate registrationDate, int groupSize, BigDecimal expectedPrice) {
        RegistrationResult result = service.register(
                "Alice Smith", "alice@example.com", null, null, registrationDate, groupSize);
        assertThat(result.isAccepted()).isTrue();
        assertThat(result.getPrice()).isEqualByComparingTo(expectedPrice);
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String value) {
        return switch (value) {
            case "before cutoff" -> LocalDate.of(2025, 2, 28);
            case "on cutoff"     -> LocalDate.of(2025, 3, 1);
            case "after cutoff"  -> LocalDate.of(2025, 3, 15);
            default              -> LocalDate.parse(value);
        };
    }
}
```

---

**Two tables, two concerns:**

**`validates_name_and_email`** — covers all validation rules. Date/group size are fixed at non-special values (via `@Description`) so validation is the only variable. Blank cell in `Error?` → null → skips the error assertion for the valid row.

**`prices_registration`** — covers all discount combinations. Key boundary rows:
- "on cutoff" (2025-03-01) confirms the condition is *strictly before*, not on-or-before
- "Group of 4" confirms the threshold is exactly 5
- "Both qualify" confirms early-bird (20%) beats group (15%) and they don't stack

The `@TypeConverter` maps descriptive date labels to concrete dates, so readers see the intent ("before cutoff") rather than a raw date they'd have to compare mentally against 2025-03-01.

**Assumptions about `RegistrationResult`**: `isAccepted()` → boolean, `getErrorMessage()` → String, `getPrice()` → BigDecimal. Adjust method names to match your actual API.