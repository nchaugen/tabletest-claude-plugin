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
        Registration date is fixed to a date after the early-bird cutoff and group size is
        fixed to 1, so neither pricing rule is in effect here — pricing and discount behaviour
        is covered separately below. Assumes a blank/whitespace-only name or email is rejected
        with the same message as a missing (null) value, and any non-blank email that does not
        match a valid email format is rejected with a generic format error message.
        """)
    @TableTest("""
        Scenario                          | Name        | Email             | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration                | Alice Smith | alice@example.com |                       |                      | true      |
        Valid registration with optionals | Bob Jones   | bob@example.com   | Vegetarian            | Wheelchair access    | true      |
        Missing name (null)               |             | alice@example.com |                       |                      | false     | Name is required
        Missing name (blank)              | {'', '   '} | alice@example.com |                       |                      | false     | Name is required
        Missing email (null)              | Alice Smith |                   |                       |                      | false     | Email is required
        Missing email (blank)             | Alice Smith | {'', '   '}       |                       |                      | false     | Email is required
        Email missing @                   | Alice Smith | aliceexample.com  |                       |                      | false     | Email must be a valid email address
        Email missing local part          | Alice Smith | @example.com      |                       |                      | false     | Email must be a valid email address
        Email missing domain              | Alice Smith | alice@            |                       |                      | false     | Email must be a valid email address
        Email missing TLD                 | Alice Smith | alice@example     |                       |                      | false     | Email must be a valid email address
        """)
    void validatesRegistration(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                                boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 6, 1), 1);

        assertEquals(accepted, result.isAccepted());
        assertEquals(errorMessage, result.getErrorMessage());
    }

    @Description("""
        Base price is £100. Early-bird applies strictly before the 2025-03-01 cutoff; group
        discount applies to groups of 5 or more. When both would apply, only the higher discount
        (early-bird, 20%) is used — they do not stack. Name and email are fixed to a valid
        registration throughout; validation is covered separately above.
        """)
    @TableTest("""
        Scenario                                                    | Registration Date | Group Size | Price? | Discount?
        No discount below both thresholds                          | after cutoff       | {1, 4}     | 100.00 | 0.00
        Early-bird registration                                    | before cutoff      | 1          | 80.00  | 20.00
        On cutoff date, early-bird no longer applies               | on cutoff          | 1          | 100.00 | 0.00
        Group discount applies                                     | after cutoff       | {5, 8, 20} | 85.00  | 15.00
        Both early-bird and group apply, early-bird (higher) wins  | before cutoff      | {5, 10}    | 80.00  | 20.00
        """)
    void calculatesPriceAndDiscount(LocalDate registrationDate, int groupSize, BigDecimal price, BigDecimal discount) {
        RegistrationResult result = service.register("Alice Smith", "alice@example.com", null, null,
                registrationDate, groupSize);

        assertTrue(result.isAccepted());
        assertEquals(0, price.compareTo(result.getPrice()));
        assertEquals(0, discount.compareTo(result.getDiscount()));
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String value) {
        return switch (value) {
            case "before cutoff" -> LocalDate.of(2025, 2, 28);
            case "on cutoff" -> LocalDate.of(2025, 3, 1);
            case "after cutoff" -> LocalDate.of(2025, 6, 1);
            default -> LocalDate.parse(value);
        };
    }
}
