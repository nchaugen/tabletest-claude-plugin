package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    @DisplayName("Rejects registrations with an invalid email or a missing name")
    @Description("""
        Registration date and group size are held at one valid value (2025-01-01, group size 1)
        since pricing is covered separately. dietaryRequirements and accessibilityNeeds are
        optional - the "Optional fields omitted" row confirms a null value for either does not
        affect acceptance.
        """)
    @TableTest("""
        Scenario                 | Name     | Email          | Dietary Requirements | Accessibility Needs | Accepted? | Has Error Message?
        Valid name and email     | Jo Ellis | jo@example.com | vegetarian            | wheelchair access    | true      | false
        Optional fields omitted  | Jo Ellis | jo@example.com |                       |                      | true      | false
        Missing name             | ''       | jo@example.com | vegetarian            | wheelchair access    | false     | true
        Missing email            | Jo Ellis | ''             | vegetarian            | wheelchair access    | false     | true
        Malformed email          | Jo Ellis | not-an-email   | vegetarian            | wheelchair access    | false     | true
        """)
    void validatesEmailAndName(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                                boolean accepted, boolean hasErrorMessage) {
        RegistrationResult result = service.register(
                name, email, dietaryRequirements, accessibilityNeeds, LocalDate.of(2025, 1, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(hasErrorMessage, result.errorMessage() != null);
    }

    @DisplayName("Applies the higher of the early-bird or group discount to the base price")
    @Description("""
        Base price is £100 for every row. Discount is the fraction taken off the base price
        (0.20 = 20% off). Early-bird applies when the registration date is strictly before the
        2025-03-01 cutoff; the group discount applies at a group size of 5 or more. The two
        never stack - when both apply, the higher rate (early-bird, 20%) is used. Name and email
        are held at one valid value since validation is covered separately.
        """)
    @TableTest("""
        Scenario                          | Registration Timing | Group Size | Discount? | Price?
        Early registration, small group   | before cutoff        | 1          | 0.20      | 80.00
        On the cutoff date, small group    | on cutoff            | 1          | 0.00      | 100.00
        Late registration, below group size| after cutoff         | 4          | 0.00      | 100.00
        Late registration, at group size   | after cutoff         | 5          | 0.15      | 85.00
        Early registration, large group    | before cutoff        | 5          | 0.20      | 80.00
        """)
    void appliesEarlyBirdAndGroupDiscount(LocalDate registrationTiming, int groupSize,
                                           BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register(
                "Jo Ellis", "jo@example.com", null, null, registrationTiming, groupSize);

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, price.compareTo(result.price()));
    }

    @TypeConverter
    public static LocalDate parseRegistrationTiming(String input) {
        return switch (input) {
            case "before cutoff" -> LocalDate.of(2025, 2, 28);
            case "on cutoff" -> LocalDate.of(2025, 3, 1);
            case "after cutoff" -> LocalDate.of(2025, 3, 2);
            default -> LocalDate.parse(input);
        };
    }
}
