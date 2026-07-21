package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private static final LocalDate EARLY_BIRD_CUTOFF = LocalDate.of(2025, 3, 1);

    // Registration date and group size are fixed to values that trigger neither
    // discount, since this table is only concerned with field validation.
    private static final LocalDate NON_DISCOUNTED_DATE = LocalDate.of(2025, 6, 1);
    private static final int NON_DISCOUNTED_GROUP_SIZE = 1;

    private final EventRegistrationService service = new EventRegistrationService();

    @Description("""
        Name is required (blank/whitespace-only names are treated as missing).
        Email must be a valid format; a null or empty email is reported as
        missing rather than malformed. dietaryRequirements and
        accessibilityNeeds are optional and do not affect acceptance.
        Registration date and group size are fixed to non-discounted values
        here since pricing is covered separately.
        """)
    @TableTest("""
        Scenario                          | Name  | Email               | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration                | Alice | alice@example.com   |                       |                      | true      |
        Valid with optional fields        | Bob   | bob@example.com     | Vegetarian            | Wheelchair access    | true      |
        Missing name (null)               |       | alice@example.com   |                       |                      | false     | Name is required
        Missing name (empty)              | ''    | alice@example.com   |                       |                      | false     | Name is required
        Missing name (blank)              | '   ' | alice@example.com   |                       |                      | false     | Name is required
        Missing email (null)              | Alice |                     |                       |                      | false     | Email is required
        Missing email (empty)             | Alice | ''                  |                       |                      | false     | Email is required
        Invalid email, missing @          | Alice | alice.example.com  |                       |                      | false     | Invalid email format
        Invalid email, missing local part | Alice | @example.com        |                       |                      | false     | Invalid email format
        Invalid email, missing domain     | Alice | alice@              |                       |                      | false     | Invalid email format
        Invalid email, missing TLD        | Alice | alice@example       |                       |                      | false     | Invalid email format
        """)
    void validatesRegistration(String name, String email, String dietaryRequirements,
                               String accessibilityNeeds, boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                NON_DISCOUNTED_DATE, NON_DISCOUNTED_GROUP_SIZE);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Base price is £100. Early-bird registrations (before 2025-03-01) get
        20% off; groups of 5 or more get 15% off. When both would apply, only
        the higher discount (early-bird) is used - they never stack. Name and
        email are fixed to a valid registration here since validation is
        covered separately.
        """)
    @TableTest("""
        Scenario                     | Registration Date | Group Size | Discount? | Price?
        No discount applies          | after cutoff       | 1          | 0.00      | 100.00
        Early-bird discount applies  | before cutoff      | 1          | 20.00     | 80.00
        On cutoff is not early-bird  | on cutoff          | 1          | 0.00      | 100.00
        Just under group threshold   | after cutoff       | 4          | 0.00      | 100.00
        Group discount applies       | after cutoff       | {5, 20}    | 15.00     | 85.00
        Both apply, early-bird wins  | before cutoff      | {5, 20}    | 20.00     | 80.00
        """)
    void calculatesPricingAndDiscount(LocalDate registrationDate, int groupSize,
                                       BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register("Alice", "alice@example.com", null, null,
                registrationDate, groupSize);

        assertEquals(discount, result.discount());
        assertEquals(price, result.price());
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String value) {
        return switch (value) {
            case "before cutoff" -> EARLY_BIRD_CUTOFF.minusDays(1);
            case "on cutoff" -> EARLY_BIRD_CUTOFF;
            case "after cutoff" -> EARLY_BIRD_CUTOFF.plusDays(1);
            default -> LocalDate.parse(value);
        };
    }
}
