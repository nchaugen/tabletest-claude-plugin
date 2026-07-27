package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventRegistrationServiceTest {

    private final EventRegistrationService service = new EventRegistrationService();

    @DisplayName("Requires a name and a valid email to accept a registration")
    @Description("""
        Registration date is 2025-06-01 and group size is 1 for every row here \
        (values that attract no pricing discount), since pricing is not the concern \
        of this table. Email format is assumed to require an "@" and a domain with \
        a dot, e.g. "name@example.com".
        """)
    @TableTest("""
        Scenario                | Name         | Email                                                    | Dietary Requirements | Accessibility Needs | Accepted? | Error Message?
        Valid registration      | Ada Lovelace | ada@example.com                                          | Vegetarian           | Wheelchair access   | true      |
        Optional fields omitted | Ada Lovelace | ada@example.com                                          |                      |                      | true      |
        Name missing            |              | ada@example.com                                          |                      |                      | false     | Name is required
        Name blank              | {'', '   '}  | ada@example.com                                          |                      |                      | false     | Name is required
        Email missing           | Ada Lovelace |                                                           |                      |                      | false     | Email must be a valid email address
        Email malformed         | Ada Lovelace | {not-an-email, missing-domain@, '@no-local-part.com', '   '} |                  |                      | false     | Email must be a valid email address
        """)
    void validatesRequiredFields(String name, String email, String dietaryRequirements, String accessibilityNeeds,
                                  boolean accepted, String errorMessage) {
        RegistrationResult result = service.register(name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 6, 1), 1);

        assertEquals(accepted, result.accepted());
        assertEquals(errorMessage, result.errorMessage());
    }

    @DisplayName("Applies the higher of the early-bird or group discount")
    @Description("""
        Name and email are valid for every row here, so each registration is accepted. \
        Base price is fixed at £100. Early-bird applies strictly before the 2025-03-01 \
        cutoff; the group discount applies at a group size of 5 or more.
        """)
    @TableTest("""
        Scenario                        | Registration Timing | Group Size | Price? | Discount?
        Neither discount applies        | on cutoff            | 4          | 100.00 | 0.00
        Early-bird before the cutoff    | before cutoff        | 4          | 80.00  | 20.00
        Group of 5 or more              | on cutoff            | {5, 10}    | 85.00  | 15.00
        Early-bird and group both apply | before cutoff        | {5, 10}    | 80.00  | 20.00
        """)
    void appliesEarlyBirdAndGroupDiscounts(LocalDate registrationDate, int groupSize,
                                            BigDecimal price, BigDecimal discount) {
        RegistrationResult result = service.register("Ada Lovelace", "ada@example.com", null, null,
                registrationDate, groupSize);

        assertTrue(result.accepted());
        assertEquals(price, result.price());
        assertEquals(discount, result.discount());
    }

    @TypeConverter
    public static LocalDate parseRegistrationTiming(String input) {
        return switch (input) {
            case "before cutoff" -> LocalDate.of(2025, 2, 28);
            case "on cutoff" -> LocalDate.of(2025, 3, 1);
            default -> LocalDate.parse(input);
        };
    }
}
