package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    @Description("""
        dietaryRequirements and accessibilityNeeds are optional on every row and never
        affect acceptance; registrationDate and groupSize are fixed to values that would
        otherwise be discount-eligible so only the name/email rules are exercised here.
        Assumption: name is validated before email, so a registration invalid on both
        counts reports the name error (last row).
        """)
    @TableTest("""
        Scenario                      | Name        | Email             | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        All fields provided           | Alice Smith | alice@example.com | Vegetarian           | Wheelchair access   | true      |
        Optional fields omitted       | Alice Smith | alice@example.com |                      |                     | true      |
        Missing name                  |             | alice@example.com |                      |                     | false     | Name is required
        Blank name                    | '   '       | alice@example.com |                      |                     | false     | Name is required
        Missing email                 | Alice Smith |                   |                      |                     | false     | Invalid email address
        Email missing @               | Alice Smith | aliceexample.com  |                      |                     | false     | Invalid email address
        Email missing local part      | Alice Smith | @example.com      |                      |                     | false     | Invalid email address
        Email missing domain          | Alice Smith | alice@            |                      |                     | false     | Invalid email address
        Email missing TLD             | Alice Smith | alice@example     |                      |                     | false     | Invalid email address
        Name and email both invalid   |             | not-an-email      |                      |                     | false     | Name is required
        """)
    void validatesRegistration(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                                boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 6, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Base price is always £100. Registration timing is relative to the early-bird
        cutoff of 2025-03-01 (registrations on the cutoff date itself are not early-bird).
        All rows use a valid name and email so only pricing/discount rules are exercised.
        """)
    @TableTest("""
        Scenario                                    | Registration Timing | Group Size | Discount? | Price?
        No discount, late registration, small group | day after cutoff    | 1          | 0.00      | 100.00
        Just under group threshold                  | day after cutoff    | 4          | 0.00      | 100.00
        Group threshold met                          | day after cutoff    | 5          | 15.00     | 85.00
        Large group                                  | well after cutoff   | 20         | 15.00     | 85.00
        Early-bird registration                      | well before cutoff  | 1          | 20.00     | 80.00
        Day before cutoff still early-bird           | day before cutoff   | 1          | 20.00     | 80.00
        On cutoff date, early-bird no longer applies | cutoff date         | 1          | 0.00      | 100.00
        Both early-bird and group apply, higher wins | well before cutoff  | 10         | 20.00     | 80.00
        Group threshold met with early-bird timing   | day before cutoff   | 5          | 20.00     | 80.00
        """)
    void calculatesPriceAndDiscount(LocalDate registrationTiming, int groupSize,
                                     BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register("Alice Smith", "alice@example.com", null, null,
                registrationTiming, groupSize);

        assertTrue(result.accepted());
        assertEquals(discount, result.discount());
        assertEquals(price, result.price());
    }

    @TypeConverter
    public static LocalDate parseRegistrationTiming(String value) {
        LocalDate cutoff = LocalDate.of(2025, 3, 1);
        return switch (value) {
            case "well before cutoff" -> cutoff.minusMonths(2);
            case "day before cutoff" -> cutoff.minusDays(1);
            case "cutoff date" -> cutoff;
            case "day after cutoff" -> cutoff.plusDays(1);
            case "well after cutoff" -> cutoff.plusMonths(3);
            default -> LocalDate.parse(value);
        };
    }
}
