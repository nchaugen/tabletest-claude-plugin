package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    @DisplayName("Accepts a registration only when a name is given and the email is well formed")
    @Description("""
        Registration date and group size are held at 2025-01-01 and a group of one, because pricing
        is a separate concern. A blank cell means the field was not supplied, rather than supplied
        empty. The prompt fixes no error-message text, so a rejection is scored by the fact that a
        message is reported and never by its wording.
        """)
    @TableTest("""
        Scenario                  | Name      | Email            | Dietary Requirements | Accessibility Needs | Accepted? | Error Reported?
        Both optionals given      | Ann Blake | ann@example.com  | Vegan                | Step-free access    | true      | false
        Neither optional given    | Ben Cole  | ben@example.com  |                      |                     | true      | false
        Only one optional given   | Cara Dunn | cara@example.com | Halal                |                     | true      | false
        Email without an at sign  | Dan Ellis | dan.example.com  |                      |                     | false     | true
        Email without a domain    | Eve Frost | eve@             |                      |                     | false     | true
        No name given             |           | fay@example.com  |                      |                     | false     | true
        """)
    void acceptsRegistrationWhenNamePresentAndEmailWellFormed(
            String name,
            String email,
            String dietaryRequirements,
            String accessibilityNeeds,
            boolean accepted,
            boolean errorReported) {
        RegistrationResult result = service.register(
                name, email, dietaryRequirements, accessibilityNeeds, LocalDate.of(2025, 1, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(errorReported, result.errorMessage() != null);
    }

    @DisplayName("Applies the larger of the early-bird and group discounts to the base price")
    @Description("""
        The base price is £100 per attendee. Discount? is the fraction taken off it and
        Price per attendee? is the pounds that remain. Registration timing is written relative to the
        early-bird cutoff the prompt fixes, so a reader needs no literal date; a registration made on
        the cutoff day is read as too late, which is this table's deliberate assumption. Name and
        email are held at one valid value because acceptance is a separate concern.
        """)
    @TableTest("""
        Scenario                                | Registration Timing | Group Size | Discount? | Price per attendee?
        Before the cutoff, registering alone    | before cutoff       | 1          | 0.20      | 80.00
        On the cutoff, registering alone        | on cutoff           | 1          | 0.00      | 100.00
        After the cutoff, one short of a group  | after cutoff        | 4          | 0.00      | 100.00
        After the cutoff, in a group            | after cutoff        | 5          | 0.15      | 85.00
        Before the cutoff, in a group           | before cutoff       | 5          | 0.20      | 80.00
        """)
    void appliesLargerOfEarlyBirdAndGroupDiscount(
            LocalDate registrationTiming,
            int groupSize,
            BigDecimal discount,
            BigDecimal pricePerAttendee) {
        RegistrationResult result = service.register(
                "Jo Ellis", "jo@example.com", null, null, registrationTiming, groupSize);

        assertEquals(0, discount.compareTo(result.discount()));
        assertEquals(0, pricePerAttendee.compareTo(result.price()));
    }

    @TypeConverter
    public static LocalDate parseRegistrationTiming(String timing) {
        LocalDate earlyBirdCutoff = LocalDate.of(2025, 3, 1);
        return switch (timing) {
            case "before cutoff" -> earlyBirdCutoff.minusDays(1);
            case "on cutoff" -> earlyBirdCutoff;
            case "after cutoff" -> earlyBirdCutoff.plusDays(1);
            default -> throw new IllegalArgumentException("Unknown registration timing: " + timing);
        };
    }
}
