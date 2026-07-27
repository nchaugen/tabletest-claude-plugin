package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private static final String VALID_NAME = "Alice Smith";
    private static final String VALID_EMAIL = "alice@example.com";
    private static final LocalDate AFTER_CUTOFF_DATE = LocalDate.of(2025, 3, 15);
    private static final int BELOW_GROUP_THRESHOLD = 1;

    private final EventRegistrationService service = new EventRegistrationService();

    @DisplayName("Registration validation")
    @Description("""
        Name and email are required; dietaryRequirements and accessibilityNeeds are optional
        and may be null. Assumes a blank (empty or whitespace-only) name is rejected with the
        same message as a missing name, and that a missing (null) email is rejected with the
        same message as a malformed one, since the spec only states an email must be valid
        format. Registration date and group size do not affect this concern, so they are held
        fixed (after the early-bird cutoff, below the group-discount threshold) for every row.
        """)
    @TableTest("""
        Scenario                                    | Name        | Email              | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration, no optional details     | Alice Smith | alice@example.com  |                      |                      | true      |
        Valid registration, optional details given  | Bob Jones   | bob@example.com    | Vegetarian           | Wheelchair access    | true      |
        Missing name                                |             | carol@example.com  |                      |                      | false     | Name is required
        Blank name                                  | {'', '   '} | dave@example.com   |                      |                      | false     | Name is required
        Missing email                               | Erin Lee    |                    |                      |                      | false     | Invalid email format
        Email missing @ symbol                      | Frank Ito   | frank.example.com  |                      |                      | false     | Invalid email format
        Email missing domain                        | Grace Kim   | grace@             |                      |                      | false     | Invalid email format
        """)
    void validatesRegistration(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                                boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                AFTER_CUTOFF_DATE, BELOW_GROUP_THRESHOLD);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @DisplayName("Early-bird pricing")
    @Description("""
        Base price is £100. Registrations dated before 2025-03-01 receive a 20% early-bird
        discount. Group size is held below the group-discount threshold so it does not also apply.
        """)
    @TableTest("""
        Scenario              | Registration Date | Discount? | Price?
        Day before the cutoff | before cutoff      | 20.00     | 80.00
        On the cutoff date    | on cutoff          | 0.00      | 100.00
        """)
    void appliesEarlyBirdDiscount(LocalDate registrationDate, BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register(VALID_NAME, VALID_EMAIL, null, null,
                registrationDate, BELOW_GROUP_THRESHOLD);

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, price.compareTo(result.price()));
    }

    @DisplayName("Group pricing")
    @Description("""
        Base price is £100. Groups of 5 or more receive a 15% group discount. Registration date
        is held after the early-bird cutoff so it does not also apply.
        """)
    @TableTest("""
        Scenario                                | Group Size | Discount? | Price?
        Just below the group discount threshold | 4          | 0.00      | 100.00
        At the group discount threshold         | 5          | 15.00     | 85.00
        """)
    void appliesGroupDiscount(int groupSize, BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register(VALID_NAME, VALID_EMAIL, null, null,
                AFTER_CUTOFF_DATE, groupSize);

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, price.compareTo(result.price()));
    }

    @DisplayName("Discount precedence")
    @Description("""
        Early-bird (20%) and group (15%) discounts do not stack; when both would apply, only the
        higher discount is used. Since the two rates are fixed, the higher one is always the
        early-bird rate, so a single row demonstrates the precedence: the result is neither the
        early-bird rate alone, the group rate alone, nor their sum.
        """)
    @TableTest("""
        Scenario                                 | Registration Date | Group Size | Discount? | Price?
        Early-bird and group discount both apply | before cutoff     | 5          | 20.00     | 80.00
        """)
    void appliesHigherDiscountWhenBothApply(LocalDate registrationDate, int groupSize,
                                             BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register(VALID_NAME, VALID_EMAIL, null, null,
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
            case "after cutoff" -> cutoff.plusMonths(1);
            default -> LocalDate.parse(value);
        };
    }
}
