Now I have everything I need. Working through the requirements analysis:

**Concerns identified:**
1. **Validate registration inputs** — name required, email format valid, optionals nullable
2. **Calculate registration price** — early-bird (20%), group (15%), higher wins when both apply

These are genuinely separate concerns with different inputs driving their logic.

**Boundary conditions:**
- Cutoff is "before 2025-03-01" → 2025-02-28 qualifies, 2025-03-01 does not
- Group threshold: exactly 4 → no discount, exactly 5 → discount applies
- Both conditions met: early-bird (20%) > group (15%) → early-bird wins

**Open questions:**
- Does blank (whitespace-only) name count as missing?
- Is "higher discount wins" evaluated dynamically, or is early-bird always the winner by definition?

**Arithmetic check:**
- Early-bird: £100 × 0.80 = £80.00 ✓
- Group: £100 × 0.85 = £85.00 ✓
- Both apply, early-bird wins: £80.00 ✓

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventRegistrationTest {

    private RegistrationService service;

    @BeforeEach
    void setUp() {
        service = new RegistrationService();
    }

    @DisplayName("Registration input validation")
    @Description("""
            Registration date and group size are fixed to 2025-04-01 and 1
            (after the early-bird cutoff, below the group threshold) so that
            pricing rules do not affect validation outcomes.
            Open: should blank (whitespace-only) names be rejected as missing,
            or is only null treated as absent?
            """)
    @TableTest("""
            Scenario                       | Name        | Email             | Dietary Requirements | Accessibility Needs | Registration Date | Group Size | Accepted? | Error?
            Valid registration             | Alice Smith | alice@example.com |                      |                     | 2025-04-01        | 1          | true      |
            With dietary requirements      | Alice Smith | alice@example.com | Vegetarian           |                     | 2025-04-01        | 1          | true      |
            With accessibility needs       | Alice Smith | alice@example.com |                      | Wheelchair access   | 2025-04-01        | 1          | true      |
            Both optionals provided        | Alice Smith | alice@example.com | Vegan                | Hearing loop        | 2025-04-01        | 1          | true      |
            Null name                      |             | alice@example.com |                      |                     | 2025-04-01        | 1          | false     | Name is required
            Blank name                     | '   '       | alice@example.com |                      |                     | 2025-04-01        | 1          | false     | Name is required
            Email missing at-sign          | Alice Smith | notanemail        |                      |                     | 2025-04-01        | 1          | false     | Invalid email format
            Email missing domain           | Alice Smith | alice@            |                      |                     | 2025-04-01        | 1          | false     | Invalid email format
            Null email                     | Alice Smith |                   |                      |                     | 2025-04-01        | 1          | false     | Invalid email format
            """)
    void shouldValidateRegistrationInputs(String name, String email,
                                          String dietaryRequirements, String accessibilityNeeds,
                                          LocalDate registrationDate, int groupSize,
                                          boolean accepted, String error) {
        RegistrationResult result = service.register(
                name, email, dietaryRequirements, accessibilityNeeds, registrationDate, groupSize);
        assertEquals(accepted, result.isAccepted());
        assertEquals(error, result.getErrorMessage());
    }

    @DisplayName("Registration pricing")
    @Description("""
            Base price is £100. Two discounts are available:
              - Early-bird: 20% off for registrations strictly before 2025-03-01 → £80.00.
              - Group: 15% off for groups of 5 or more → £85.00.
            When both conditions are met, only the higher discount is applied (no stacking).
            Under the current rates early-bird (20%) always beats group (15%).
            Name and email are fixed to valid values; validation is tested separately.
            Open: is "only the higher discount" evaluated dynamically at runtime, or is
            early-bird always the winner by definition when both conditions apply?
            """)
    @TableTest("""
            Scenario                               | Registration Date | Group Size | Price?
            No discount                            | 2025-04-01        | 1          | 100.00
            Early-bird: last day before cutoff     | 2025-02-28        | 1          | 80.00
            No early-bird: on the cutoff date      | 2025-03-01        | 1          | 100.00
            Group discount: exactly 5 attendees    | 2025-04-01        | 5          | 85.00
            No group discount: exactly 4 attendees | 2025-04-01        | 4          | 100.00
            Both apply: early-bird wins over group | 2025-02-28        | 5          | 80.00
            """)
    void shouldCalculateRegistrationPrice(LocalDate registrationDate, int groupSize,
                                          BigDecimal price) {
        RegistrationResult result = service.register(
                "Alice Smith", "alice@example.com", null, null, registrationDate, groupSize);
        assertTrue(result.isAccepted());
        assertEquals(0, price.compareTo(result.getPrice()));
    }

    @TypeConverter
    static BigDecimal toBigDecimal(String value) {
        return new BigDecimal(value);
    }
}
```

A few design notes:

**Validation table** — the assertion is uniform for all rows. For accepted rows the `Error?` cell is blank (null), and `result.getErrorMessage()` should also be null — both sides match. No `if`-branching needed in the test method.

**Pricing table** — `price.compareTo(result.getPrice()) == 0` is used instead of `assertEquals` to avoid `BigDecimal` scale sensitivity (`100.00` vs `100` would fail with `.equals()` but pass with `.compareTo()`).

**`@TypeConverter`** — `BigDecimal` is not in JUnit's built-in conversion list, so the converter is needed to parse the price strings from the table.

**Two open questions flagged in `@Description`** rather than silently resolved — both the blank-name behaviour and whether "higher discount wins" is dynamic or definitional are genuinely ambiguous in the spec.