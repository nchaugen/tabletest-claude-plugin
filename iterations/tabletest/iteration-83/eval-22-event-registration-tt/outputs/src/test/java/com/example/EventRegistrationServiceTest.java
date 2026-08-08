package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    // A normally-priced arrangement used by the validation tables below, where date and
    // group size are not the concern under test.
    private static final LocalDate ANY_VALID_DATE = LocalDate.of(2025, 6, 1);
    private static final int ANY_VALID_GROUP_SIZE = 1;

    @Description("""
        Name is held valid throughout; the name-required rule has its own table below.
        Assumes an invalid email format is rejected with the message "Invalid email format".
        """)
    @TableTest("""
        Scenario                 | Email           | Accepted? | Error?
        Well-formed email        | ada@example.com | true      |
        Missing @ symbol         | ada.example.com | false     | Invalid email format
        Missing local part       | @example.com    | false     | Invalid email format
        Missing domain           | ada@            | false     | Invalid email format
        Missing top-level domain | ada@example     | false     | Invalid email format
        """)
    void rejectsAnInvalidEmailFormat(String email, boolean accepted, String error) {
        RegistrationResult result = service.register(
                "Ada Lovelace", email, null, null, ANY_VALID_DATE, ANY_VALID_GROUP_SIZE);

        assertEquals(accepted, result.accepted());
        assertEquals(error, result.errorMessage());
    }

    @Description("""
        Email is held valid throughout; the email-format rule has its own table above.
        Assumes a missing name is rejected with the message "Name is required".
        """)
    @TableTest("""
        Scenario      | Name         | Accepted? | Error?
        Name provided | Ada Lovelace | true      |
        Name is null  |              | false     | Name is required
        Name is empty | ''           | false     | Name is required
        """)
    void requiresAName(String name, boolean accepted, String error) {
        RegistrationResult result = service.register(
                name, "ada@example.com", null, null, ANY_VALID_DATE, ANY_VALID_GROUP_SIZE);

        assertEquals(accepted, result.accepted());
        assertEquals(error, result.errorMessage());
    }

    @TableTest("""
        Scenario                 | Dietary Requirements | Accessibility Needs | Accepted?
        Optional fields absent   |                      |                     | true
        Optional fields provided | Vegetarian           | Wheelchair access   | true
        """)
    void acceptsRegistrationsRegardlessOfOptionalFields(
            String dietaryRequirements, String accessibilityNeeds, boolean accepted) {
        RegistrationResult result = service.register(
                "Ada Lovelace", "ada@example.com", dietaryRequirements, accessibilityNeeds,
                ANY_VALID_DATE, ANY_VALID_GROUP_SIZE);

        assertEquals(accepted, result.accepted());
    }

    @Description("""
        Base price is £100. Early-bird takes 20% off; the group discount takes 15% off.
        A registration dated on the cutoff itself is not early-bird (the cutoff is exclusive).
        When both discounts would apply, only the higher one (early-bird) is used - they do
        not stack.
        """)
    @TableTest("""
        Scenario                                 | Registration Date | Group Size | Early-Bird Cutoff (Policy) | Group Discount Threshold (Policy) | Price (£)? | Discount (£)?
        Day before the early-bird cutoff         | 2025-02-28        | 1          | 2025-03-01                 | 5                                 | 80.00      | 20.00
        On the early-bird cutoff                 | 2025-03-01        | 1          | 2025-03-01                 | 5                                 | 100.00     | 0.00
        Below the group discount threshold       | 2025-06-01        | 4          | 2025-03-01                 | 5                                 | 100.00     | 0.00
        At the group discount threshold          | 2025-06-01        | 5          | 2025-03-01                 | 5                                 | 85.00      | 15.00
        Both early-bird and group discount apply | 2025-01-15        | 5          | 2025-03-01                 | 5                                 | 80.00      | 20.00
        """)
    void computesPriceAndDiscountForAValidRegistration(
            LocalDate registrationDate, int groupSize, LocalDate earlyBirdCutoff,
            int groupDiscountThreshold, BigDecimal price, BigDecimal discount) {
        RegistrationResult result = service.register(
                "Ada Lovelace", "ada@example.com", null, null, registrationDate, groupSize);

        assertMoneyEquals(price, result.price());
        assertMoneyEquals(discount, result.discount());
    }

    private static void assertMoneyEquals(BigDecimal expected, BigDecimal actual) {
        assertEquals(0, expected.compareTo(actual),
                () -> "expected " + expected + " but was " + actual);
    }
}
