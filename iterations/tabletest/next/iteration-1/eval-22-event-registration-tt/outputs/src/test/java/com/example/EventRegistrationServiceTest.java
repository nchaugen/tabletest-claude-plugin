package com.example;

import org.junit.jupiter.api.Assertions;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    // Registration date/group size are irrelevant to validation, so a fixed
    // valid-looking value (after the early-bird cutoff, below the group
    // threshold) is used for every row here; pricing is covered separately below.
    private static final LocalDate ANY_REGISTRATION_DATE = LocalDate.of(2025, 6, 1);
    private static final int ANY_GROUP_SIZE = 1;

    @Description("""
        Name is required (blank/whitespace-only counts as missing). Email must
        be a syntactically valid address. dietaryRequirements and
        accessibilityNeeds are optional and may be null.
        Assumption: when both name and email are invalid, the name error is
        reported first.
        """)
    @TableTest("""
        Scenario                            | Name        | Email             | Dietary Requirements | Accessibility Needs | Accepted? | Error?
        Valid registration                  | Alice Smith | alice@example.com |                      |                     | true      |
        Valid with optional details supplied | Bob Jones   | bob@example.com   | Vegetarian           | Wheelchair access   | true      |
        Missing name                        |             | alice@example.com |                      |                     | false     | Name is required
        Blank name                          | '   '       | alice@example.com |                      |                     | false     | Name is required
        Missing email                       | Alice Smith | ''                |                      |                     | false     | Email is required
        Email missing @ symbol              | Alice Smith | aliceexample.com  |                      |                     | false     | Invalid email format
        Email missing domain                | Alice Smith | alice@            |                      |                     | false     | Invalid email format
        Email missing local part            | Alice Smith | @example.com      |                      |                     | false     | Invalid email format
        Email missing top-level domain      | Alice Smith | alice@example     |                      |                     | false     | Invalid email format
        Both name and email invalid         |             | aliceexample.com  |                      |                     | false     | Name is required
        """)
    void validatesRegistration(String name, String email, String dietaryRequirements,
                               String accessibilityNeeds, boolean accepted, String error) {
        RegistrationResult result = service.register(
                name, email, dietaryRequirements, accessibilityNeeds, ANY_REGISTRATION_DATE, ANY_GROUP_SIZE);

        assertEquals(accepted, result.isAccepted());
        assertEquals(error, result.getErrorMessage());
    }

    @Description("""
        Base price is £100 for all rows. Early-bird: registrations before
        2025-03-01 get 20% off. Group discount: groups of 5 or more get 15%
        off. When both would apply, only the higher discount (early-bird,
        20%) is used - they don't stack. Name/email are fixed valid values;
        validation is covered separately above.
        """)
    @TableTest("""
        Scenario                             | Registration Date | Group Size | Price? | Discount?
        Baseline - no discount applies       | 2025-06-01         | 1          | 100.00 | 0.00
        Well before early-bird cutoff        | 2025-01-01         | 1          | 80.00  | 20.00
        Just before early-bird cutoff        | 2025-02-28         | 1          | 80.00  | 20.00
        On early-bird cutoff date            | 2025-03-01         | 1          | 100.00 | 0.00
        Just after early-bird cutoff         | 2025-03-02         | 1          | 100.00 | 0.00
        Just below group discount threshold  | 2025-06-01         | 4          | 100.00 | 0.00
        At group discount threshold          | 2025-06-01         | 5          | 85.00  | 15.00
        Well above group discount threshold  | 2025-06-01         | 20         | 85.00  | 15.00
        Both apply - early-bird discount wins| 2025-02-15         | 10         | 80.00  | 20.00
        """)
    void appliesPricingAndDiscountRules(LocalDate registrationDate, int groupSize,
                                         BigDecimal expectedPrice, BigDecimal expectedDiscount) {
        RegistrationResult result = service.register(
                "Alice Smith", "alice@example.com", null, null, registrationDate, groupSize);

        assertTrue(result.isAccepted());
        assertMoneyEquals(expectedDiscount, result.getDiscount());
        assertMoneyEquals(expectedPrice, result.getPrice());
    }

    private static void assertMoneyEquals(BigDecimal expected, BigDecimal actual) {
        Assertions.assertEquals(0, expected.compareTo(actual),
                () -> "expected " + expected + " but was " + actual);
    }
}
