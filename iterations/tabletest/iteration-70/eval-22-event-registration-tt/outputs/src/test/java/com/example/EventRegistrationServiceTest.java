package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    @Description("""
        Registration date and group size do not affect validation, so both are held fixed
        at an obviously-valid value (2025-06-01, group size 1); pricing behaviour driven by
        those two inputs is covered separately below.
        """)
    @TableTest("""
        Scenario                                   | Name         | Email                                                         | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration without optional fields | Ada Lovelace | ada@example.com                                               |                      |                     | true      |
        Valid registration with optional fields    | Ada Lovelace | ada@example.com                                               | Vegetarian           | Wheelchair access   | true      |
        Missing name                               |              | ada@example.com                                               |                      |                     | false     | Name is required
        Blank name                                 | {'', '   '}  | ada@example.com                                               |                      |                     | false     | Name is required
        Missing email                              | Ada Lovelace |                                                               |                      |                     | false     | Email is invalid
        Malformed email                            | Ada Lovelace | {not-an-email, missing-at.com, '@missing-local.com', 'user@'} |                      |                     | false     | Email is invalid
        """)
    void validatesRegistration(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                                boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
            LocalDate.of(2025, 6, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Base price is fixed at £100 for every registration; this table does not vary it.
        Discount is the monetary amount taken off the base price, and Price is what remains
        after the discount. Early-bird and group discounts do not stack: when both apply,
        only the higher of the two (20% early-bird vs 15% group) is used.
        """)
    @TableTest("""
        Scenario                                         | Registration Date | Group Size | Early-Bird Cutoff (Policy) | Min Group Size (Policy) | Price? | Discount?
        Neither discount applies                         | 2025-06-01        | 1          | 2025-03-01                 | 5                       | 100.00 | 0.00
        Early-bird registration, day before cutoff       | 2025-02-28        | 1          | 2025-03-01                 | 5                       | 80.00  | 20.00
        On the cutoff date, early-bird no longer applies | 2025-03-01        | 1          | 2025-03-01                 | 5                       | 100.00 | 0.00
        Group just below the threshold                   | 2025-06-01        | 4          | 2025-03-01                 | 5                       | 100.00 | 0.00
        Group at the threshold                           | 2025-06-01        | 5          | 2025-03-01                 | 5                       | 85.00  | 15.00
        Both apply: higher discount wins, no stacking    | 2025-02-28        | 5          | 2025-03-01                 | 5                       | 80.00  | 20.00
        """)
    void computesPriceAndDiscount(LocalDate registrationDate, int groupSize, LocalDate earlyBirdCutoff,
                                   int minGroupSize, BigDecimal price, BigDecimal discount) {
        RegistrationResult result = service.register("Ada Lovelace", "ada@example.com", null, null,
            registrationDate, groupSize);

        assertTrue(result.accepted());
        assertNull(result.errorMessage());
        assertEquals(0, price.compareTo(result.price()));
        assertEquals(0, discount.compareTo(result.discount()));
    }
}
