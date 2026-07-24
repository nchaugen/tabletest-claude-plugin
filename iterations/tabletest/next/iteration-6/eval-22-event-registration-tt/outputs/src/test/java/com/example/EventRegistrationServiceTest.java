package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private static final LocalDate CUTOFF = LocalDate.of(2025, 3, 1);
    private static final LocalDate BASELINE_REGISTRATION_DATE = LocalDate.of(2025, 6, 1);
    private static final int BASELINE_GROUP_SIZE = 1;

    private final EventRegistrationService service = new EventRegistrationService();

    @Description("""
        Registration date and group size are fixed to values that qualify for
        no discount (baseline, unrelated to this concern); pricing/discount
        behaviour is covered separately in shouldCalculateDiscountAndPrice.
        """)
    @TableTest("""
        Scenario                                     | Name         | Email              | Dietary Requirements | Accessibility Needs | Accepted? | Error?
        Valid registration, optional fields omitted  | Alice Smith  | alice@example.com  |                      |                      | true      |
        Valid registration, optional fields provided | Bob Jones    | bob@example.com    | Vegetarian           | Wheelchair access    | true      |
        Missing name                                 |              | alice@example.com  |                      |                      | false     | Name is required
        Blank name                                   | {'', '   '}  | alice@example.com  |                      |                      | false     | Name is required
        Missing email                                | Alice Smith  |                     |                      |                      | false     | Email is required
        Email missing @ symbol                       | Alice Smith  | alice.example.com  |                      |                      | false     | Invalid email format
        Email missing local part                     | Alice Smith  | @example.com       |                      |                      | false     | Invalid email format
        Email missing domain                          | Alice Smith  | alice@             |                      |                      | false     | Invalid email format
        Email with no TLD                            | Alice Smith  | alice@example      |                      |                      | false     | Invalid email format
        """)
    void shouldValidateRegistration(String name, String email, String dietaryRequirements,
                                     String accessibilityNeeds, boolean accepted, String error) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                BASELINE_REGISTRATION_DATE, BASELINE_GROUP_SIZE);

        assertEquals(accepted, result.isAccepted());
        assertEquals(error, result.getErrorMessage());
    }

    @Description("""
        Base price is £100 for all rows. Name/email are fixed to a valid baseline
        registration; validation is covered separately in shouldValidateRegistration.
        Discount and Price are absolute currency amounts (not percentages);
        Price = Base Price - Discount. Cutoff date is 2025-03-01 - early-bird
        applies strictly before this date.
        """)
    @TableTest("""
        Scenario                                     | Registration Date   | Group Size    | Discount? | Price?
        No discount for standard registration        | after cutoff        | {1, 2, 3, 4}  | 0.00      | 100.00
        Early-bird registration                       | just before cutoff  | 1             | 20.00     | 80.00
        On cutoff date does not qualify for early-bird| on cutoff           | 1             | 0.00      | 100.00
        Group of 5 or more qualifies for group discount| after cutoff       | {5, 6, 20}    | 15.00     | 85.00
        Early-bird and group discount don't stack     | just before cutoff  | 5             | 20.00     | 80.00
        """)
    void shouldCalculateDiscountAndPrice(LocalDate registrationDate, int groupSize,
                                          BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register("Alice Smith", "alice@example.com", null, null,
                registrationDate, groupSize);

        assertEquals(0, discount.compareTo(result.getDiscount()));
        assertEquals(0, price.compareTo(result.getPrice()));
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String input) {
        return switch (input) {
            case "just before cutoff" -> CUTOFF.minusDays(1);
            case "on cutoff" -> CUTOFF;
            case "after cutoff" -> CUTOFF.plusMonths(3);
            default -> LocalDate.parse(input);
        };
    }
}
