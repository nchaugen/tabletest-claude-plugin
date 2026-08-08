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
        Registration Date, Group Size and the base price/group threshold are not this
        table's concern; pricing behaviour is covered separately. Assumes "required"
        rejects both a null name and a blank/whitespace-only name, and that an invalid
        email format is rejected with the same message whether it is malformed, blank
        or missing. Error message wording is an assumption, not a specified contract.
        """)
    @TableTest("""
        Scenario                                 | Name         | Email                                                                    | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration without optional info | Ada Lovelace | ada@example.com                                                          |                      |                     | true      |
        Valid registration with optional info    | Ada Lovelace | ada@example.com                                                          | Vegetarian           | Wheelchair access   | true      |
        Blank or whitespace-only name            | {'', '   '}  | ada@example.com                                                          |                      |                     | false     | Name is required
        Missing name                             |              | ada@example.com                                                          |                      |                     | false     | Name is required
        Malformed email address                  | Ada Lovelace | {not-an-email, missing-domain@, '@missing-local.com', 'ada example.com'} |                      |                     | false     | A valid email address is required
        Missing email                            | Ada Lovelace |                                                                          |                      |                     | false     | A valid email address is required
        """)
    void registerValidatesEmailFormatAndRequiresName(String name, String email, String dietaryRequirements,
                                                       String accessibilityNeeds, boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 6, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Description("""
        Name, email and optional fields are fixed to valid values; validation is
        covered by registerValidatesEmailFormatAndRequiresName. Group Size is held at 1,
        below the group discount threshold, so group pricing does not interact with
        this table. Discount is expressed as a currency amount, not a percentage.
        """)
    @TableTest("""
        Scenario                         | Registration Date | Base Price (£) | Early-bird Cutoff | Price (£)? | Discount (£)?
        Day before the early-bird cutoff | before cutoff     | 100            | 2025-03-01        | 80.00      | 20.00
        On the early-bird cutoff         | on cutoff         | 100            | 2025-03-01        | 100.00     | 0.00
        """)
    void registerAppliesEarlyBirdDiscountBeforeCutoff(LocalDate registrationDate, BigDecimal basePrice,
                                                        LocalDate earlyBirdCutoff, BigDecimal price, BigDecimal discount) {
        RegistrationResult result = service.register("Ada Lovelace", "ada@example.com", null, null,
                registrationDate, 1);

        assertEquals(price, result.price());
        assertEquals(discount, result.discount());
    }

    @Description("""
        Name, email and optional fields are fixed to valid values; validation is
        covered by registerValidatesEmailFormatAndRequiresName. Registration Date is
        held after the early-bird cutoff, so early-bird pricing does not interact with
        this table. Discount is expressed as a currency amount, not a percentage.
        """)
    @TableTest("""
        Scenario                  | Group Size | Group Threshold | Base Price (£) | Price (£)? | Discount (£)?
        Below the group threshold | {1, 4}     | 5               | 100            | 100.00     | 0.00
        At the group threshold    | 5          | 5               | 100            | 85.00      | 15.00
        """)
    void registerAppliesGroupDiscountAtThreshold(int groupSize, int groupThreshold, BigDecimal basePrice,
                                                  BigDecimal price, BigDecimal discount) {
        RegistrationResult result = service.register("Ada Lovelace", "ada@example.com", null, null,
                LocalDate.of(2025, 6, 1), groupSize);

        assertEquals(price, result.price());
        assertEquals(discount, result.discount());
    }

    @Description("""
        Neither the early-bird table nor the group discount table shows what happens
        when both conditions are met at once. Group Size is fixed at the group
        threshold so both rows qualify for the group discount; Registration Date is
        the only thing that varies, showing that once early-bird no longer applies
        the group discount is used on its own, and that when both apply the higher
        discount wins rather than the two stacking (20% + 15% would be 35%).
        """)
    @TableTest("""
        Scenario                                | Registration Date | Group Size | Base Price (£) | Early-bird Cutoff | Group Threshold | Price (£)? | Discount (£)?
        Early-bird date with a qualifying group | before cutoff     | 5          | 100            | 2025-03-01        | 5               | 80.00      | 20.00
        Cutoff reached with a qualifying group  | on cutoff         | 5          | 100            | 2025-03-01        | 5               | 85.00      | 15.00
        """)
    void registerUsesHigherOfEarlyBirdOrGroupDiscountWithoutStacking(LocalDate registrationDate, int groupSize,
                                                                       BigDecimal basePrice, LocalDate earlyBirdCutoff,
                                                                       int groupThreshold, BigDecimal price, BigDecimal discount) {
        RegistrationResult result = service.register("Ada Lovelace", "ada@example.com", null, null,
                registrationDate, groupSize);

        assertEquals(price, result.price());
        assertEquals(discount, result.discount());
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String value) {
        return switch (value) {
            case "before cutoff" -> LocalDate.of(2025, 2, 28);
            case "on cutoff" -> LocalDate.of(2025, 3, 1);
            case "after cutoff" -> LocalDate.of(2025, 6, 1);
            default -> LocalDate.parse(value);
        };
    }
}
