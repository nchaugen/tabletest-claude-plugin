package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventRegistrationServiceTest {

    private static final LocalDate PRICING_UNAFFECTED_DATE = LocalDate.of(2025, 6, 1);
    private static final int PRICING_UNAFFECTED_GROUP_SIZE = 1;
    private static final String VALID_NAME = "Alice Smith";
    private static final String VALID_EMAIL = "alice@example.com";

    private final EventRegistrationService service = new EventRegistrationService();

    @DisplayName("Validates registration details before accepting a registration")
    @Description("""
        Registration date and group size are held at values that neither the
        early-bird nor group pricing rule applies to (2025-06-01, group size 1),
        since pricing is covered by a separate table. A blank/whitespace-only
        name is treated the same as a missing name. "Invalid email" covers any
        value that does not have a local part, an "@", and a domain.
        """)
    @TableTest("""
        Scenario                     | Name        | Email                  | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        All fields provided          | Alice Smith | alice@example.com      | vegetarian            | wheelchair access    | true      |
        Optional fields omitted      | Bob Jones   | bob@example.com        |                       |                      | true      |
        Name missing                 |             | carol@example.com      |                       |                      | false     | Name is required
        Name blank                   | '   '       | dave@example.com       |                       |                      | false     | Name is required
        Email missing                | Eve Adams   |                        |                       |                      | false     | Invalid email
        Email missing @ symbol       | Frank Lee   | frank.lee.example.com  |                       |                      | false     | Invalid email
        Email missing domain         | Grace Kim   | grace@                 |                       |                      | false     | Invalid email
        Email missing local part     | Henry Ford  | @example.com           |                       |                      | false     | Invalid email
        """)
    void validatesRegistrationDetails(
            String name,
            String email,
            String dietaryRequirements,
            String accessibilityNeeds,
            boolean accepted,
            String errorMessage
    ) {
        RegistrationResult result = service.register(
                name, email, dietaryRequirements, accessibilityNeeds,
                PRICING_UNAFFECTED_DATE, PRICING_UNAFFECTED_GROUP_SIZE);

        assertEquals(accepted, result.isAccepted());
        assertEquals(errorMessage, result.getErrorMessage());
    }

    @DisplayName("Applies the higher of the early-bird or group discount to the base price")
    @Description("""
        Name and email are held at an already-valid value since validation has
        its own table. Base price is £100 for every row. Early-bird gives 20%
        off for registrations strictly before 2025-03-01; a group of 5 or more
        gives 15% off; when both qualify they do not stack, only the higher
        discount is used. Discount is the amount removed from the base price;
        price is the base price after that discount.
        """)
    @TableTest("""
        Scenario                            | Registration Date | Group Size | Price? | Discount?
        On the early-bird cutoff, small group | 2025-03-01        | 1          | 100.00 | 0.00
        Day before the early-bird cutoff      | 2025-02-28        | 1          | 80.00  | 20.00
        Group just below the threshold        | 2025-03-01        | 4          | 100.00 | 0.00
        Group at the threshold                | 2025-03-01        | 5          | 85.00  | 15.00
        Both early-bird and group qualify     | 2025-02-28        | 5          | 80.00  | 20.00
        """)
    void appliesTheHigherDiscount(
            LocalDate registrationDate,
            int groupSize,
            BigDecimal price,
            BigDecimal discount
    ) {
        RegistrationResult result = service.register(
                VALID_NAME, VALID_EMAIL, null, null, registrationDate, groupSize);

        assertEquals(0, price.compareTo(result.getPrice()));
        assertEquals(0, discount.compareTo(result.getDiscount()));
    }
}
