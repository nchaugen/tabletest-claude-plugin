package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private static final LocalDate DEFAULT_REGISTRATION_DATE = LocalDate.of(2025, 6, 1);
    private static final int DEFAULT_GROUP_SIZE = 1;

    private final EventRegistrationService service = new EventRegistrationService();

    @Description("""
        Name and email are required; dietaryRequirements and accessibilityNeeds
        are optional and null is accepted. Email must match a valid
        local@domain.tld format. Registration date and group size do not
        affect validation, so every row uses a fixed representative date and
        group size - pricing/discount behaviour is covered separately.

        Assumption: a missing name or email is reported as "<Field> is
        required"; a non-blank but malformed email is reported as "Invalid
        email format".
        """)
    @TableTest("""
        Scenario                    | Name        | Email             | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Complete valid registration | Alice Smith | alice@example.com | Vegetarian           | Wheelchair access   | true      |
        Optional fields omitted     | Bob Jones   | bob@example.com   |                      |                     | true      |
        Empty name                  | ''          | carol@example.com |                      |                     | false     | Name is required
        Null name                   |             | dave@example.com  |                      |                     | false     | Name is required
        Empty email                 | Eve Adams   | ''                |                      |                     | false     | Email is required
        Null email                  | Frank Lee   |                   |                      |                     | false     | Email is required
        Missing @ symbol            | Grace Kim   | graceexample.com  |                      |                     | false     | Invalid email format
        Missing domain              | Henry Ford  | henry@            |                      |                     | false     | Invalid email format
        Missing local part          | Iris Park   | @example.com      |                      |                     | false     | Invalid email format
        Missing top-level domain    | Jack Ma     | jack@example      |                      |                     | false     | Invalid email format
        """)
    void validatesRegistration(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                                boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                DEFAULT_REGISTRATION_DATE, DEFAULT_GROUP_SIZE);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Base price is £100 for every row. Early-bird pricing applies to
        registrations made before the 2025-03-01 cutoff (20% off); the group
        discount applies to groups of 5 or more (15% off). When both would
        apply, only the higher discount (early-bird) is used - they don't stack.
        """)
    @TableTest("""
        Scenario                    | Registration Timing | Group Size | Discount? | Price?
        Standard registration       | after cutoff         | 1          | 0.00      | 100.00
        Early-bird registration     | before cutoff        | 1          | 20.00     | 80.00
        On the cutoff date          | on cutoff            | 1          | 0.00      | 100.00
        Just below group threshold  | after cutoff         | 4          | 0.00      | 100.00
        Group discount applies      | after cutoff         | {5, 20}    | 15.00     | 85.00
        Both apply, early-bird wins | before cutoff        | {5, 20}    | 20.00     | 80.00
        """)
    void appliesPricingAndDiscount(LocalDate registrationDate, int groupSize, BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register("Alice Smith", "alice@example.com", null, null,
                registrationDate, groupSize);

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, price.compareTo(result.price()));
    }

    @TypeConverter
    public static LocalDate parseRegistrationTiming(String input) {
        return switch (input) {
            case "before cutoff" -> LocalDate.of(2025, 2, 28);
            case "on cutoff" -> LocalDate.of(2025, 3, 1);
            case "after cutoff" -> LocalDate.of(2025, 3, 15);
            default -> LocalDate.parse(input);
        };
    }
}
