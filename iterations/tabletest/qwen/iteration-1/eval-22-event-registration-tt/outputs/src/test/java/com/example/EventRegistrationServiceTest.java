package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventRegistrationServiceTest {

    // ---- Validation: rejection scenarios ---------------------------------

    @DisplayName("rejects when name is empty")
    @TableTest("""
        Scenario               | Email                   | Expected error?
        Empty string           | ''                     | "Name is required"
        Blank string           | '     '                | "Name is required"
        """)
    void rejectsWhenNameIsEmpty(String email, String expectedError) {
        EventRegistrationService service = new EventRegistrationService();
        RegistrationResult result = service.register("", email, null, null, LocalDate.of(2025, 1, 15), 3);
        assertFalse(result.isAccepted());
        assertEquals(expectedError, result.getErrorMessage());
    }

    @DisplayName("rejects when name is missing")
    @TableTest("""
        Scenario               | Email                   | Expected error?
        Null name              | alice@example.com      | "Name is required"
        """)
    void rejectsWhenNameIsNull(String email, String expectedError) {
        EventRegistrationService service = new EventRegistrationService();
        RegistrationResult result = service.register(null, email, null, null, LocalDate.of(2025, 1, 15), 3);
        assertFalse(result.isAccepted());
        assertEquals(expectedError, result.getErrorMessage());
    }

    @DisplayName("rejects when email format is invalid")
    @TableTest("""
        Scenario                   | Name              | Expected error?
        Missing at sign            | 'Alice'           | "Valid email required"
        No domain                  | 'alice@'          | "Valid email required"
        No tld                     | 'alice@x'         | "Valid email required"
        Space in local part        | 'a li ce@e.c'     | "Valid email required"
        """)
    void rejectsWhenEmailFormatIsInvalid(String name, String expectedError) {
        EventRegistrationService service = new EventRegistrationService();
        RegistrationResult result = service.register(name, "not-an-email", null, null, LocalDate.of(2025, 1, 15), 3);
        assertFalse(result.isAccepted());
        assertEquals(expectedError, result.getErrorMessage());
    }

    // ---- Valid registration (baseline) ---------------------------------

    @DisplayName("accepts valid registration with all options")
    @TableTest("""
        Scenario                       | Name              | Email                 | Dietary | AccessNeed | RegistrationDate | GroupSize?
        Minimal valid                  | 'Alice'           | 'alice@example.com'   |         |             | 2025-01-15        | 3
        """)
    void acceptsValidRegistration(String name, String email, String dietary, String accessNeed,
                                  LocalDate date, Integer groupSize) {
        EventRegistrationService service = new EventRegistrationService();
        RegistrationResult result = service.register(name, email, dietary, accessNeed, date, groupSize);
        assertTrue(result.isAccepted());
        assertNull(result.getErrorMessage());
    }

    // ---- Early-bird pricing --------------------------------------------

    @DisplayName("applies 20%% early-bird discount when before cutoff")
    @TableTest("""
        Scenario                   | Name           | Email                | RegistrationDate? | Price?  | Discount?
        Before cutoff (Jan)        | 'Alice'        | 'alice@ex.com'       | 2025-01-15        | 80.00   | 20.00
        On the day before          | 'Bob'          | 'bob@ex.com'         | 2025-02-28        | 80.00   | 20.00
        """)
    void earlyBirdGetsTwentyPercentOff(String name, String email, LocalDate date, BigDecimal expectedPrice, BigDecimal expectedDiscount) {
        EventRegistrationService service = new EventRegistrationService();
        RegistrationResult result = service.register(name, email, null, null, date, 1);
        assertTrue(result.isAccepted());
        assertEquals(0, expectedPrice.compareTo(result.getPrice()));
        assertEquals(0, expectedDiscount.compareTo(result.getDiscount()));
    }

    @DisplayName("no early-bird discount on or after cutoff")
    @TableTest("""
        Scenario                   | Name           | Email                | RegistrationDate? | Price?   | Discount?
        On cutoff date             | 'Charlie'      | 'charlie@ex.com'     | 2025-03-01        | 100.00   | 0.00
        After cutoff (Mar)         | 'Dana'         | 'dana@ex.com'        | 2025-03-15        | 100.00   | 0.00
        """)
    void noEarlyBirdOnOrAfterCutoff(String name, String email, LocalDate date, BigDecimal expectedPrice, BigDecimal expectedDiscount) {
        EventRegistrationService service = new EventRegistrationService();
        RegistrationResult result = service.register(name, email, null, null, date, 1);
        assertTrue(result.isAccepted());
        assertEquals(0, expectedPrice.compareTo(result.getPrice()));
        assertEquals(0, expectedDiscount.compareTo(result.getDiscount()));
    }

    // ---- Group discount ------------------------------------------------

    @DisplayName("applies 15%% group discount for five or more")
    @TableTest("""
        Scenario                       | Name           | Email                | RegistrationDate? | GroupSize? | Price?   | Discount?
        Exactly at threshold           | 'Eve'          | 'eve@ex.com'         | 2025-01-15        | 5          | 85.00    | 15.00
        Just below threshold           | 'Frank'        | 'frank@ex.com'       | 2025-01-15        | 4          | 100.00   | 0.00
        Well above threshold           | 'Grace'        | 'grace@ex.com'       | 2025-06-01        | 10         | 85.00    | 15.00
        """)
    void groupOfFiveGetsFifteenPercentOff(String name, String email, LocalDate date, Integer groupSize, BigDecimal expectedPrice, BigDecimal expectedDiscount) {
        EventRegistrationService service = new EventRegistrationService();
        RegistrationResult result = service.register(name, email, null, null, date, groupSize);
        assertTrue(result.isAccepted());
        assertEquals(0, expectedPrice.compareTo(result.getPrice()));
        assertEquals(0, expectedDiscount.compareTo(result.getDiscount()));
    }

    @DisplayName("no group discount at zero or one attendee")
    @TableTest("""
        Scenario                       | Name           | Email                | GroupSize? | Price?   | Discount?
        Single attendee (one)          | 'Hank'         | 'hank@ex.com'        | 1          | 100.00   | 0.00
        No group specified             | 'Ivy'          | 'ivy@ex.com'         |            | 100.00   | 0.00
        """)
    void noGroupDiscountForSmallerGroups(String name, String email, Integer groupSize, BigDecimal expectedPrice, BigDecimal expectedDiscount) {
        EventRegistrationService service = new EventRegistrationService();
        RegistrationResult result = service.register(name, email, null, null, LocalDate.of(2025, 6, 1), groupSize);
        assertTrue(result.isAccepted());
        assertEquals(0, expectedPrice.compareTo(result.getPrice()));
        assertEquals(0, expectedDiscount.compareTo(result.getDiscount()));
    }

    // ---- Discount precedence (both apply) ------------------------------

    @DisplayName("uses higher discount when both early-bird and group apply")
    @TableTest("""
        Scenario                               | Name            | Email                  | RegistrationDate? | GroupSize? | Price?   | Discount?
        Both discounts applied                 | 'Jack'          | 'jack@ex.com'          | 2025-02-15        | 5          | 80.00    | 20.00
        Large group, early-bird wins           | 'Karen'         | 'karen@ex.com'         | 2025-01-30        | 20         | 80.00    | 20.00
        """)
    void higherDiscountWinsWhenBothApply(String name, String email, LocalDate date, Integer groupSize, BigDecimal expectedPrice, BigDecimal expectedDiscount) {
        EventRegistrationService service = new EventRegistrationService();
        RegistrationResult result = service.register(name, email, null, null, date, groupSize);
        assertTrue(result.isAccepted());
        assertEquals(0, expectedPrice.compareTo(result.getPrice()));
        assertEquals(0, expectedDiscount.compareTo(result.getDiscount()));
    }

    // TODO: implement the actual registration logic
}
