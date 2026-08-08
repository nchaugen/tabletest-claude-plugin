package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    @DisplayName("Rejects a registration when the name is missing")
    @Description("""
        Email is held at a well-formed address for every row; email validation is
        covered separately. Registration date and group size are held at values that
        attract no pricing discount, since pricing is a separate concern.
        """)
    @TableTest("""
        Scenario                | Name         | Accepted? | Error Message?
        Name provided           | Ada Lovelace | true      |
        Name absent             |              | false     | Name is required
        Name is an empty string | ''           | false     | Name is required
        """)
    void rejectsRegistrationMissingName(String name, boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(
                name, "ada@example.com", null, null, LocalDate.of(2025, 6, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @DisplayName("Rejects a registration with an invalid email address")
    @Description("""
        Name is held at a valid value for every row; name validation is covered
        separately. A missing email is treated the same as a malformed one, since
        neither satisfies "a valid email format". Registration date and group size are
        held at values that attract no pricing discount, since pricing is a separate
        concern.
        """)
    @TableTest("""
        Scenario                   | Email           | Accepted? | Error Message?
        Well-formed email          | ada@example.com | true      |
        Email absent               |                 | false     | Invalid email format
        Malformed email, no @ sign | ada.example.com | false     | Invalid email format
        """)
    void rejectsRegistrationWithInvalidEmail(String email, boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(
                "Ada Lovelace", email, null, null, LocalDate.of(2025, 6, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @DisplayName("Accepts a registration regardless of whether optional details are supplied")
    @Description("""
        Name and email are held at valid values for every row; their validation is
        covered separately. Registration date and group size are held at values that
        attract no pricing discount, since pricing is a separate concern.
        """)
    @TableTest("""
        Scenario                  | Dietary Requirements | Accessibility Needs | Accepted?
        Optional details supplied | vegetarian           | wheelchair access   | true
        Optional details omitted  |                      |                     | true
        """)
    void acceptsRegistrationRegardlessOfOptionalDetails(
            String dietaryRequirements, String accessibilityNeeds, boolean accepted) {
        RegistrationResult result = service.register(
                "Ada Lovelace", "ada@example.com", dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 6, 1), 1);

        assertEquals(accepted, result.accepted());
    }

    @DisplayName("Applies the higher of the early-bird and group discounts, without stacking")
    @Description("""
        Name and email are held at valid values throughout; their validation is covered
        separately. Base price is £100. The early-bird cutoff (2025-03-01) and the group
        discount threshold (5) are fixed policy constants that register(...) does not
        expose as parameters, so they are stated here rather than as columns; the
        cutoff date itself does not qualify for early-bird pricing. Discount is the
        amount deducted, in GBP; Price is the final price after the discount is
        applied. The group rate (15%) never exceeds the early-bird rate (20%), so rows
        where both apply resolve to the early-bird rate; that resolution is what shows
        the discounts don't stack and the higher one wins.
        """)
    @TableTest("""
        Scenario                          | Registration Date | Group Size | Discount? | Price?
        No discount applies               | 2025-03-01        | 4          | 0.00      | 100.00
        Early-bird, day before the cutoff | 2025-02-28        | 4          | 20.00     | 80.00
        Group discount, at the threshold  | 2025-03-01        | 5          | 15.00     | 85.00
        Both apply, early-bird rate wins  | 2025-01-01        | 10         | 20.00     | 80.00
        """)
    void appliesHigherOfEarlyBirdAndGroupDiscount(
            LocalDate registrationDate, int groupSize, BigDecimal discount, BigDecimal price) {
        RegistrationResult result = service.register(
                "Ada Lovelace", "ada@example.com", null, null, registrationDate, groupSize);

        assertEquals(discount, result.discount());
        assertEquals(price, result.price());
    }
}
