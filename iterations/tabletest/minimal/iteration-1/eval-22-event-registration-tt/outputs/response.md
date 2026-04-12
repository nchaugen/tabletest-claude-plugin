No Java project found here — this is the eval framework repo. I'll write the test class directly as output.

Working through the design:

**Step 1 — Concerns:**
1. Input validation (name required, email format; optional fields are always OK)
2. Discount selection + total price (early-bird 20%, group 15%, higher-wins)

**Step 2–4 — Boundaries and value sets:**
- Date boundary: day before cutoff (early-bird ✓) vs. cutoff day itself (✗)
- Group threshold: size 4 (no discount) vs. size 5 (discount ✓)
- "Both apply" row proves early-bird wins; note in `@Description` that 20% > 15% always, so early-bird always beats group at these fixed rates

**Step 5 — Separate rules from arithmetic:**
Discount % is an intermediate result worth asserting (traceability); total price is the final observable. Both come from one `register()` call, so they stay in one table.

**Step 6 — Error messages:** The exact strings depend on the implementation, so noted as an open question in `@Description`.

---

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EventRegistrationTest {

    private final EventRegistrar registrar = new EventRegistrar();

    @DisplayName("Input validation")
    @Description("""
        Date and group size are fixed (2025-06-01, 1 attendee) to isolate
        validation from pricing. Dietary requirements and accessiblity needs
        are optional — null is always accepted for otherwise-valid registrations.
        Open: exact error message strings are not yet specified; update the
        Error? column once the implementation defines them.
        """)
    @TableTest("""
        Scenario                  | Name        | Email             | Dietary Requirements | Accessibility Needs | Accepted? | Error?
        All fields provided       | Alice Smith | alice@example.com | Vegan                | Wheelchair access   | true      |
        Optional fields null      | Alice Smith | alice@example.com |                      |                     | true      |
        Name is null              |             | alice@example.com |                      |                     | false     | Name is required
        Name is blank             | '   '       | alice@example.com |                      |                     | false     | Name is required
        Email missing at-sign     | Alice Smith | notanemail        |                      |                     | false     | Invalid email format
        Email missing domain      | Alice Smith | alice@            |                      |                     | false     | Invalid email format
        Email missing local part  | Alice Smith | @example.com      |                      |                     | false     | Invalid email format
        """)
    void validatesRegistrationInput(String name, String email, String dietaryRequirements,
                                    String accessibilityNeeds, boolean accepted, String error) {
        RegistrationResult result = registrar.register(
                name, email, dietaryRequirements, accessibilityNeeds,
                LocalDate.of(2025, 6, 1), 1);
        assertAll(
                () -> assertEquals(accepted, result.isAccepted()),
                () -> assertEquals(error, result.getErrorMessage()));
    }

    @DisplayName("Discount selection and total price")
    @Description("""
        Base price is £100 per person. Early-bird cutoff is 2025-03-01: registrations
        strictly before that date qualify for 20% off. Groups of 5 or more qualify for
        15% off. When both discounts apply, only the higher is used. Because early-bird
        (20%) always exceeds group (15%) at these fixed rates, early-bird always wins
        when both conditions are met.
        Inputs use a fixed valid registrant (Alice Smith, alice@example.com).
        """)
    @TableTest("""
        Scenario                            | Registration Date | Group Size | Discount %? | Total Price?
        No discount, single attendee        | after cutoff      | 1          | 0           | 100.00
        No discount, just below group min   | after cutoff      | 4          | 0           | 400.00
        Early-bird only, small group        | before cutoff     | 1          | 20          | 80.00
        Early-bird only, just below group min | before cutoff   | 4          | 20          | 320.00
        Group discount, at threshold        | on cutoff         | 5          | 15          | 425.00
        Group discount, large group         | after cutoff      | 10         | 15          | 850.00
        Both apply, early-bird wins         | before cutoff     | 5          | 20          | 400.00
        """)
    void calculatesDiscountedPrice(LocalDate registrationDate, int groupSize,
                                   int appliedDiscount, BigDecimal totalPrice) {
        RegistrationResult result = registrar.register(
                "Alice Smith", "alice@example.com", null, null, registrationDate, groupSize);
        assertAll(
                () -> assertTrue(result.isAccepted()),
                () -> assertEquals(appliedDiscount, result.getAppliedDiscount()),
                () -> assertEquals(totalPrice, result.getTotalPrice()));
    }

    @TypeConverter
    public static LocalDate parseRegistrationDate(String input) {
        return switch (input) {
            case "before cutoff" -> LocalDate.of(2025, 2, 28);
            case "on cutoff"     -> LocalDate.of(2025, 3, 1);
            case "after cutoff"  -> LocalDate.of(2025, 3, 2);
            default              -> LocalDate.parse(input);
        };
    }
}
```

---

**Key design decisions:**

- **Two tables, two concerns.** Validation uses fixed date/group to avoid interference; pricing uses a fixed valid name/email.
- **`Discount %?` as traceability column.** It's an intermediate result that explains the price and pinpoints where a failure occurred (wrong discount selection vs. wrong arithmetic). Assumes `result.getAppliedDiscount()` is exposed — adapt if not.
- **Boundary coverage on both axes.** Date: day before cutoff (2025-02-28) and cutoff itself (2025-03-01). Group size: 4 (no discount) and 5 (discount).
- **`@TypeConverter` for descriptive dates.** `"before cutoff"` is more readable than `2025-02-28` and keeps the policy boundary in one place (the converter and `@Description`).
- **Error message open question flagged in `@Description`.** The exact strings aren't specified — the table has placeholder text that should be updated once the implementation defines them.