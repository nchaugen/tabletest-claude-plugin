Here's the complete `EventRegistrationTest.java`:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EventRegistrationTest {

    // Early-bird cutoff: registrations before 2025-03-01 qualify
    @TypeConverter
    public static LocalDate parseRegistrationDate(String input) {
        return switch (input) {
            case "before cutoff" -> LocalDate.of(2025, 2, 15);
            case "on cutoff"     -> LocalDate.of(2025, 3, 1);
            case "after cutoff"  -> LocalDate.of(2025, 4, 1);
            default              -> LocalDate.parse(input);
        };
    }

    @Description("""
            Registration date is 2025-04-01 (after early-bird cutoff) and group size is 1,
            so pricing rules do not apply. Dietary requirements and accessibility needs are null.
            """)
    @TableTest("""
            Scenario                  | Name        | Email             | Accepted? | Error?
            Valid registration        | Alice Smith | alice@example.com | true      |
            Missing name              |             | alice@example.com | false     | Name is required
            Empty name                | ''          | alice@example.com | false     | Name is required
            No @ in email             | Bob Jones   | bobjones.com      | false     | Invalid email format
            No domain after @         | Bob Jones   | bob@              | false     | Invalid email format
            No local part before @    | Bob Jones   | @example.com      | false     | Invalid email format
            """)
    void shouldValidateRequiredFields(String name, String email, boolean accepted, String error) {
        RegistrationResult result = register(name, email, null, null, LocalDate.of(2025, 4, 1), 1);
        assertEquals(accepted, result.isAccepted());
        assertEquals(error, result.getError());
    }

    @Description("""
            Base price is £100. Name and email are always valid; dietary requirements and
            accessibility needs are null. Early-bird cutoff is 2025-03-01 (exclusive —
            registrations on that date do not qualify). Group discount requires 5 or more.
            When both discounts apply, only the higher one (early-bird at 20%) is used.
            """)
    @TableTest("""
            Scenario                            | Registration Date | Group Size   | Price?
            No discount                         | after cutoff      | {1, 2, 3, 4} | 100.00
            Early-bird discount (20% off)       | before cutoff     | {1, 2, 3, 4} | 80.00
            Group discount (15% off)            | after cutoff      | {5, 10}      | 85.00
            Early-bird wins over group discount | before cutoff     | {5, 10}      | 80.00
            On cutoff is not early-bird         | on cutoff         | 1            | 100.00
            """)
    void shouldApplyDiscount(LocalDate registrationDate, int groupSize, BigDecimal expectedPrice) {
        RegistrationResult result = register("Alice Smith", "alice@example.com", null, null, registrationDate, groupSize);
        assertTrue(result.isAccepted());
        assertEquals(0, expectedPrice.compareTo(result.getPrice()));
    }
}
```

---

**Design decisions:**

**Two tables, two concerns:**
- `shouldValidateRequiredFields` — isolates name/email validation. Date and group size are fixed in the body (no effect on validation) and explained in `@Description`.
- `shouldApplyDiscount` — isolates pricing. Name/email are always valid, isolating the discount logic.

**`@TypeConverter` for dates** — `before cutoff`/`on cutoff`/`after cutoff` are more readable than raw ISO dates, and the cutoff date (`2025-03-01`) is stated in `@Description`. The reader can verify correctness without mentally comparing numbers.

**Value sets for group size** — `{1, 2, 3, 4}` and `{5, 10}` express "any size in this range produces the same result," which directly communicates the threshold rule. The "on cutoff" boundary row uses group size `1` to isolate only the date boundary.

**`compareTo` for BigDecimal** — avoids false failures from scale differences (e.g., `100` vs `100.00`).

**No separate arithmetic table** — the arithmetic is `£100 × (1 − rate)`. With only three possible discount rates (0%, 15%, 20%), the rule and the arithmetic are verified together in one row each. A separate arithmetic table would just restate the multiplication.