package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private static final LocalDate NON_EARLY_BIRD_DATE = LocalDate.of(2025, 6, 1);
    private static final int NON_GROUP_SIZE = 1;

    private final EventRegistrationService service = new EventRegistrationService();

    @Description("""
        Name is required and email must be a valid format. Dietary requirements and
        accessibility needs are optional (null is accepted). Registration date and
        group size do not affect validation, so every row uses a fixed date
        (2025-06-01) and group size (1) that would neither be early-bird nor a group.
        Assumption: rejected registrations report either "Name is required" or
        "Invalid email format" as the error message.
        """)
    @TableTest("""
        Scenario                 | Name        | Email              | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration       | Alice Smith | alice@example.com  |                       |                      | true      |
        Optional fields provided | Bob Jones   | bob@example.com    | Vegetarian            | Wheelchair access    | true      |
        Missing name             |             | alice@example.com |                       |                      | false     | Name is required
        Blank name               | '   '       | alice@example.com |                       |                      | false     | Name is required
        Missing email            | Alice Smith |                    |                       |                      | false     | Invalid email format
        Missing @ symbol         | Alice Smith | aliceexample.com   |                       |                      | false     | Invalid email format
        Missing local part       | Alice Smith | @example.com       |                       |                      | false     | Invalid email format
        Missing domain           | Alice Smith | alice@             |                       |                      | false     | Invalid email format
        Missing top-level domain | Alice Smith | alice@example      |                       |                      | false     | Invalid email format
        """)
    void validatesRegistration(
        String name,
        String email,
        String dietaryRequirements,
        String accessibilityNeeds,
        boolean accepted,
        String errorMessage
    ) {
        RegistrationResult result = service.register(
            name, email, dietaryRequirements, accessibilityNeeds, NON_EARLY_BIRD_DATE, NON_GROUP_SIZE
        );

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Base price is £100 for every row. Early-bird applies to registrations made
        before the 2025-03-01 cutoff (20% off); group discount applies to groups of
        5 or more (15% off). When both would apply, only the higher discount
        (early-bird) is used - discounts never stack.
        """)
    @TableTest("""
        Scenario                       | Registration Date | Group Size | Discount? | Price?
        No discount applies            | after cutoff       | 1          | 0.00      | 100.00
        Early-bird applies             | before cutoff      | 1          | 20.00     | 80.00
        On cutoff date, not early-bird | on cutoff          | 1          | 0.00      | 100.00
        Group discount applies         | after cutoff       | 5          | 15.00     | 85.00
        Just below group threshold     | after cutoff       | 4          | 0.00      | 100.00
        Both apply, higher rate wins   | before cutoff      | 5          | 20.00     | 80.00
        """)
    void appliesEarlyBirdAndGroupDiscount(
        LocalDate registrationDate,
        int groupSize,
        BigDecimal discount,
        BigDecimal price
    ) {
        RegistrationResult result = service.register(
            "Alice Smith", "alice@example.com", null, null, registrationDate, groupSize
        );

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, price.compareTo(result.price()));
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String value) {
        return switch (value) {
            case "before cutoff" -> LocalDate.of(2025, 2, 28);
            case "on cutoff" -> LocalDate.of(2025, 3, 1);
            case "after cutoff" -> LocalDate.of(2025, 3, 2);
            default -> LocalDate.parse(value);
        };
    }
}
