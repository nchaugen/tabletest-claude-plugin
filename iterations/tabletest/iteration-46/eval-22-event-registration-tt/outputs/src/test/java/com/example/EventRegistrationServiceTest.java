package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    @DisplayName("Rejects a registration missing a required field or with a malformed email")
    @Description("""
        registrationDate = 2025-01-01 and groupSize = 1 for every row: both are early-bird
        eligible on their own, but validation is checked before pricing, so acceptance here
        does not depend on either. Pricing and discount behaviour is covered separately below.
        Assumption: when both name and email are invalid, the name error is reported first.
        """)
    @TableTest("""
        Scenario                              | Name          | Email                                              | Dietary Requirements | Accessibility Needs | Valid? | Error Message?
        Valid registration, no optional fields| Ada Lovelace  | ada@example.com                                    |                       |                     | true   |
        Valid registration, optional fields   | Grace Hopper  | grace@example.com                                  | Vegetarian            | Wheelchair access   | true   |
        Missing name                          |               | ada@example.com                                    |                       |                     | false  | Name is required
        Blank name                            | {'', '   '}   | ada@example.com                                    |                       |                     | false  | Name is required
        Missing email                         | Ada Lovelace  |                                                     |                       |                     | false  | Invalid email format
        Malformed email format                | Ada Lovelace  | {plainaddress, missing-at-sign.com, user@@example} |                       |                     | false  | Invalid email format
        Both name and email invalid           |               | plainaddress                                       |                       |                     | false  | Name is required
        """)
    void validatesRegistration(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                                boolean valid, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.parse("2025-01-01"), 1);

        assertEquals(valid, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @DisplayName("Applies the higher of the early-bird and group discount, without stacking")
    @Description("""
        Base price is always £100. Early-bird applies when registering before 2025-03-01;
        group discount applies to groups of 5 or more. "on cutoff" is 2025-03-01 itself,
        which does not qualify for early-bird since the rule is strictly "before" that date.
        """)
    @TableTest("""
        Scenario                              | Registration Timing | Group Size | Price?  | Discount?
        No discount applies                   | on cutoff            | {1, 4}     | 100.00  | 0.00
        Early-bird discount applies            | before cutoff        | {1, 4}     | 80.00   | 20.00
        Group discount applies                 | on cutoff            | {5, 10}    | 85.00   | 15.00
        Both thresholds met, early-bird wins    | before cutoff        | {5, 10}    | 80.00   | 20.00
        """)
    void appliesPricingAndDiscount(LocalDate registrationDate, int groupSize, BigDecimal price, BigDecimal discount) {
        RegistrationResult result = service.register("Ada Lovelace", "ada@example.com", null, null,
                registrationDate, groupSize);

        assertTrue(result.accepted());
        assertEquals(0, price.compareTo(result.price()));
        assertEquals(0, discount.compareTo(result.discount()));
    }

    @TypeConverter
    public static LocalDate parseRegistrationTiming(String input) {
        return switch (input) {
            case "before cutoff" -> LocalDate.parse("2025-02-28");
            case "on cutoff" -> LocalDate.parse("2025-03-01");
            default -> LocalDate.parse(input);
        };
    }
}
