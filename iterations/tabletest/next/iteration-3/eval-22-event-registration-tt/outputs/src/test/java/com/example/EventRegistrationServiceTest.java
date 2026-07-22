package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private static final String VALID_NAME = "Ada Lovelace";
    private static final String VALID_EMAIL = "ada@example.com";
    private static final LocalDate NEUTRAL_DATE = LocalDate.of(2025, 6, 1);
    private static final int NEUTRAL_GROUP_SIZE = 1;

    private final EventRegistrationService service = new EventRegistrationService();

    @Description("""
        Registration date and group size are fixed to values that do not
        trigger any discount, since this table covers validation only.
        """)
    @TableTest("""
        Scenario                        | Name           | Email             | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration, no optional | Ada Lovelace   | ada@example.com   |                      |                     | true      |
        Valid registration with optional| Ada Lovelace   | ada@example.com   | Vegetarian           | Wheelchair access   | true      |
        Missing name                    |                | ada@example.com   |                      |                     | false     | Name is required
        Blank name                      | '   '          | ada@example.com   |                      |                     | false     | Name is required
        Missing email                   | Ada Lovelace   |                   |                      |                     | false     | Invalid email format
        Missing @ symbol                | Ada Lovelace   | adaexample.com    |                      |                     | false     | Invalid email format
        Missing local part              | Ada Lovelace   | @example.com      |                      |                     | false     | Invalid email format
        Missing domain                  | Ada Lovelace   | ada@              |                      |                     | false     | Invalid email format
        Missing top-level domain        | Ada Lovelace   | ada@example       |                      |                     | false     | Invalid email format
        """)
    void validatesNameAndEmail(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                               boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(
                name, email, dietaryRequirements, accessibilityNeeds, NEUTRAL_DATE, NEUTRAL_GROUP_SIZE);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Base price is fixed at £100 for every registration. Early-bird cutoff is
        2025-03-01 (exclusive: registrations on or after this date do not qualify);
        group discount applies to groups of 5 or more. When both would apply, the
        higher discount (early-bird, 20%) is used instead of stacking with the
        group discount (15%). Assumes Price? is the amount charged after discount
        and Discount? is the currency amount saved.
        """)
    @TableTest("""
        Scenario                                    | Registration Date | Group Size | Discount? | Price?
        Early-bird discount applies                 | before cutoff     | 1          | 20.00     | 80.00
        Early-bird boundary, on cutoff not eligible  | on cutoff         | 1          | 0.00      | 100.00
        After cutoff, no early-bird discount         | after cutoff      | 1          | 0.00      | 100.00
        Below group threshold, no discount           | on cutoff         | 4          | 0.00      | 100.00
        Group discount applies, at and above threshold| on cutoff        | {5, 10}    | 15.00     | 85.00
        Both apply, higher discount wins, no stacking| before cutoff     | 5          | 20.00     | 80.00
        """)
    void calculatesPriceAndDiscount(LocalDate registrationDate, int groupSize, BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register(
                VALID_NAME, VALID_EMAIL, null, null, registrationDate, groupSize);

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
