package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    @TableTest("""
        Scenario                            | Name        | Email               | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration, all fields set  | Alice Smith | alice@example.com   | Vegetarian           | Wheelchair access    | true      |
        Valid registration, optional fields | Bob Jones   | bob@example.com     |                      |                      | true      |
        Missing name                        |             | alice@example.com   |                      |                      | false     | Name is required
        Blank name                          | '   '       | alice@example.com   |                      |                      | false     | Name is required
        Missing email                       | Alice Smith |                     |                      |                      | false     | Invalid email format
        Email missing @                     | Alice Smith | aliceexample.com    |                      |                      | false     | Invalid email format
        Email missing local part            | Alice Smith | @example.com        |                      |                      | false     | Invalid email format
        Email missing domain                | Alice Smith | alice@              |                      |                      | false     | Invalid email format
        """)
    void shouldValidateRegistration(String name, String email, String dietaryRequirements,
            String accessibilityNeeds, boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(
                name, email, dietaryRequirements, accessibilityNeeds, LocalDate.of(2025, 6, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Base price is £100 for every registration. Early-bird applies to registrations
        made strictly before the 2025-03-01 cutoff. Group discount applies to groups of
        5 or more attendees. When both would apply, only the higher discount (early-bird,
        20%, vs. group, 15%) is used - they never stack.
        """)
    @TableTest("""
        Scenario                                       | Registration Timing | Group Size | Discount? | Price?
        Early-bird, small group                        | before cutoff       | 1          | 20.00     | 80.00
        On the cutoff date, small group                 | on cutoff           | 1          | 0.00      | 100.00
        After cutoff, small group                        | after cutoff        | 1          | 0.00      | 100.00
        After cutoff, group just under threshold         | after cutoff        | 4          | 0.00      | 100.00
        After cutoff, group at threshold                 | after cutoff        | 5          | 15.00     | 85.00
        After cutoff, large group                        | after cutoff        | 10         | 15.00     | 85.00
        Early-bird and group discount both apply          | before cutoff       | 10         | 20.00     | 80.00
        Early-bird and group discount, group at threshold | before cutoff       | 5          | 20.00     | 80.00
        """)
    void shouldApplyPricingAndDiscount(LocalDate registrationDate, int groupSize,
            BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register(
                "Alice Smith", "alice@example.com", null, null, registrationDate, groupSize);

        assertEquals(discount, result.discount());
        assertEquals(price, result.price());
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
