package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    @Description("""
        Name is required (null or blank is rejected). Email must be a valid
        address format - missing @, missing local part, and missing domain are
        all rejected. dietaryRequirements and accessibilityNeeds are optional and
        never affect acceptance. registrationDate and groupSize are fixed to
        values that don't trigger any discount, since pricing is covered by a
        separate table.
        """)
    @TableTest("""
        Scenario                     | Name        | Email             | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid, no optional details   | Alice Smith | alice@example.com |                       |                      | true      |
        Valid, with optional details | Bob Jones   | bob@example.com   | Vegetarian            | Wheelchair access    | true      |
        Missing name                 |             | alice@example.com|                       |                      | false     | Name is required
        Blank name                   | '   '       | alice@example.com|                       |                      | false     | Name is required
        Missing email                | Alice Smith |                   |                       |                      | false     | Invalid email format
        Blank email                  | Alice Smith | '   '             |                       |                      | false     | Invalid email format
        Email missing @              | Alice Smith | aliceexample.com  |                       |                      | false     | Invalid email format
        Email missing local part     | Alice Smith | @example.com      |                       |                      | false     | Invalid email format
        Email missing domain         | Alice Smith | alice@            |                       |                      | false     | Invalid email format
        """)
    void validatesRegistration(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                                boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 4, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Base price is £100 for every row. Early-bird gives 20% off when the
        registration date is before the 2025-03-01 cutoff; group discount gives
        15% off for groups of 5 or more. When both apply, only the higher
        discount (early-bird, 20%) is used - the discounts don't stack.
        """)
    @TableTest("""
        Scenario                                 | Registration Date | Group Size | Discount? | Price?
        Early-bird only, small group              | before cutoff     | 1          | 20.00     | 80.00
        On the cutoff date, small group            | on cutoff         | 1          | 0.00      | 100.00
        After cutoff, small group                  | after cutoff      | 1          | 0.00      | 100.00
        After cutoff, group just under threshold   | after cutoff      | 4          | 0.00      | 100.00
        After cutoff, group discount applies       | after cutoff      | 5          | 15.00     | 85.00
        Early-bird and group discount both apply   | before cutoff     | 5          | 20.00     | 80.00
        """)
    void appliesPricingAndDiscounts(LocalDate registrationDate, int groupSize, BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register("Alice Smith", "alice@example.com", null, null,
                registrationDate, groupSize);

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, price.compareTo(result.price()));
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String value) {
        LocalDate cutoff = LocalDate.of(2025, 3, 1);
        return switch (value) {
            case "before cutoff" -> cutoff.minusDays(1);
            case "on cutoff" -> cutoff;
            case "after cutoff" -> cutoff.plusDays(1);
            default -> LocalDate.parse(value);
        };
    }
}
