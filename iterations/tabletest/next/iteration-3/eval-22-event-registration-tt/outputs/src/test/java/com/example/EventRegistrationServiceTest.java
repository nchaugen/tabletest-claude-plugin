package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private static final LocalDate DATE_AFTER_CUTOFF = LocalDate.of(2025, 6, 1);
    private static final int NON_GROUP_SIZE = 1;

    private final EventRegistrationService service = new EventRegistrationService();

    @Description("""
        Name must be non-blank; email must match a standard user@domain.tld format.
        dietaryRequirements and accessibilityNeeds are optional and may be null on any
        row, valid or invalid. Registration date and group size are fixed at values that
        earn no discount, since pricing is a separate concern covered in other tables.
        Assumed error messages: "Name is required" for a missing/blank name and
        "Invalid email address" for a missing or malformed email.
        """)
    @TableTest("""
        Scenario                          | Name        | Email                     | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration, no optionals  | Alice Smith | alice@example.com         |                      |                      | true      |
        Valid registration with optionals | Bob Jones   | bob@example.com           | Vegetarian           | Wheelchair access    | true      |
        Missing name                      |             | alice@example.com        |                      |                      | false     | Name is required
        Empty name                        | ''          | alice@example.com        |                      |                      | false     | Name is required
        Blank name                        | '   '       | alice@example.com        |                      |                      | false     | Name is required
        Missing email                     | Alice Smith |                           |                      |                      | false     | Invalid email address
        Empty email                       | Alice Smith | ''                        |                      |                      | false     | Invalid email address
        Missing @ symbol                  | Alice Smith | aliceexample.com          |                      |                      | false     | Invalid email address
        Missing domain                    | Alice Smith | alice@                   |                      |                      | false     | Invalid email address
        Missing top-level domain          | Alice Smith | alice@example             |                      |                      | false     | Invalid email address
        Contains spaces                   | Alice Smith | alice smith@example.com  |                      |                      | false     | Invalid email address
        """)
    void shouldValidateRegistration(
            String name,
            String email,
            String dietaryRequirements,
            String accessibilityNeeds,
            boolean accepted,
            String errorMessage
    ) {
        RegistrationResult result = service.register(
                name, email, dietaryRequirements, accessibilityNeeds, DATE_AFTER_CUTOFF, NON_GROUP_SIZE);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Base price is £100. Group size is fixed at 1, below the group discount
        threshold, so only the early-bird rule is exercised here. The cutoff is
        exclusive: a registration made on the cutoff date itself does not qualify.
        """)
    @TableTest("""
        Scenario              | Registration Date | Cutoff Date | Discount? | Price?
        Well before cutoff    | 2025-01-15         | 2025-03-01  | 20.00     | 80.00
        Just before cutoff    | 2025-02-28         | 2025-03-01  | 20.00     | 80.00
        On the cutoff date    | 2025-03-01         | 2025-03-01  | 0.00      | 100.00
        Just after cutoff     | 2025-03-02         | 2025-03-01  | 0.00      | 100.00
        Well after cutoff     | 2025-06-01         | 2025-03-01  | 0.00      | 100.00
        """)
    void shouldApplyEarlyBirdDiscount(
            LocalDate registrationDate,
            LocalDate cutoffDate,
            BigDecimal discount,
            BigDecimal price
    ) {
        RegistrationResult result = service.register(
                "Alice Smith", "alice@example.com", null, null, registrationDate, NON_GROUP_SIZE);

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, price.compareTo(result.price()));
    }

    @Description("""
        Base price is £100. Registration date is fixed well after the early-bird
        cutoff so only the group discount rule is exercised here. Groups of 5 or
        more qualify.
        """)
    @TableTest("""
        Scenario             | Group Size | Group Threshold | Discount? | Price?
        Single registrant    | 1          | 5               | 0.00      | 100.00
        Just below threshold | 4          | 5               | 0.00      | 100.00
        At the threshold     | 5          | 5               | 15.00     | 85.00
        Above the threshold  | 10         | 5               | 15.00     | 85.00
        """)
    void shouldApplyGroupDiscount(
            int groupSize,
            int groupThreshold,
            BigDecimal discount,
            BigDecimal price
    ) {
        RegistrationResult result = service.register(
                "Alice Smith", "alice@example.com", null, null, DATE_AFTER_CUTOFF, groupSize);

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, price.compareTo(result.price()));
    }

    @Description("""
        Base price is £100. When a registration qualifies for both the early-bird
        (20%) and group (15%) discounts, only the higher discount applies - they
        do not stack.
        """)
    @TableTest("""
        Scenario                                  | Registration Date | Group Size | Discount? | Price?
        Neither discount applies                  | 2025-06-01        | 1          | 0.00      | 100.00
        Only early-bird applies                    | 2025-01-15        | 1          | 20.00     | 80.00
        Only group discount applies                | 2025-06-01        | 5          | 15.00     | 85.00
        Both apply, early-bird wins regardless of group size | 2025-01-15 | {5, 10} | 20.00     | 80.00
        """)
    void shouldResolveDiscountWhenBothApply(
            LocalDate registrationDate,
            int groupSize,
            BigDecimal discount,
            BigDecimal price
    ) {
        RegistrationResult result = service.register(
                "Alice Smith", "alice@example.com", null, null, registrationDate, groupSize);

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, price.compareTo(result.price()));
    }
}
