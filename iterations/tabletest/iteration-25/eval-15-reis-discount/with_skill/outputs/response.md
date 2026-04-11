There's no existing Java project here, so I'll present the complete test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountTest {

    @DisplayName("Reis discount percentage")
    @Description("""
            Adults and seniors accumulate a personal discount based on the number of
            single tickets purchased in the last 30 days, including the current ticket.
            Formula: min(floor(count / 5) * 5, 40)%.
            The discount applies regardless of which zones are traveled.
            Open: does "the number of tickets you have purchased" include the ticket
            being purchased right now, or only prior tickets? These tests assume the
            current ticket IS included (ticket #5 triggers the first 5% discount).
            """)
    @TableTest("""
            Scenario                         | Tickets in window (incl. current) | Discount %?
            Building up, no discount yet     | {1, 2, 3, 4}                      | 0
            First discount: ticket 5         | 5                                 | 5
            Still on first tier              | {6, 7, 8, 9}                      | 5
            Second tier: ticket 10           | 10                                | 10
            Third tier: ticket 15            | 15                                | 15
            Fourth tier: ticket 20           | 20                                | 20
            Fifth tier: ticket 25            | 25                                | 25
            Sixth tier: ticket 30            | 30                                | 30
            Seventh tier: ticket 35          | 35                                | 35
            Maximum tier: ticket 40          | {40, 41, 42, 43, 44}             | 40
            Above maximum tier, capped at 40 | {45, 50, 100}                    | 40
            """)
    void reisDiscountTier(int ticketsInWindow, int expectedDiscountPercent) {
        assertEquals(expectedDiscountPercent, ReisDiscountCalculator.discountPercent(ticketsInWindow));
    }

    @DisplayName("Tickets counted in rolling 30-day window")
    @Description("""
            At the moment of purchase, the count includes all single tickets purchased
            in the 30 days up to and including today (the purchase date).
            Open: is the boundary day (exactly 30 days ago) included or excluded?
            These tests assume the boundary day IS included.
            """)
    @TableTest("""
            Scenario                      | Purchase date | Purchase history              | Count?
            No history                    | 2024-04-04    | []                            | 0
            All tickets within window     | 2024-04-04    | [2024-04-01, 2024-03-20]      | 2
            Ticket exactly at boundary    | 2024-04-04    | [2024-03-05]                  | 1
            Ticket just outside boundary  | 2024-04-04    | [2024-03-04]                  | 0
            Mix inside and outside        | 2024-04-04    | [2024-04-01, 2024-03-04]      | 1
            All tickets outside window    | 2024-04-04    | [2024-03-01, 2024-02-01]      | 0
            Ticket purchased today        | 2024-04-04    | [2024-04-04]                  | 1
            """)
    void ticketsInRollingWindow(LocalDate purchaseDate, List<LocalDate> purchaseHistory, int expectedCount) {
        assertEquals(expectedCount, ReisDiscountCalculator.countInWindow(purchaseDate, purchaseHistory));
    }

    @DisplayName("Discounted ticket price")
    @Description("""
            Final price = base price × (1 - discount / 100), rounded to 2 decimal places (HALF_UP).
            Applies to both the Reis accumulating discount and the child flat 20% discount.
            """)
    @TableTest("""
            Scenario        | Base price | Discount % | Final price?
            No discount     | 100.00     | 0          | 100.00
            5% off          | 100.00     | 5          | 95.00
            20% off         | 100.00     | 20         | 80.00
            40% off (max)   | 100.00     | 40         | 60.00
            Non-round price | 73.50      | 10         | 66.15
            """)
    void discountedTicketPrice(BigDecimal basePrice, int discountPercent, BigDecimal expectedPrice) {
        assertEquals(expectedPrice, ReisDiscountCalculator.applyDiscount(basePrice, discountPercent));
    }

    @DisplayName("Discount type by passenger type")
    @Description("""
            Adults and seniors receive the Reis accumulating discount (0–40% based on travel history).
            Children receive a flat 20% discount regardless of travel history or zones.
            Open: are there other passenger categories (youth, infant, student)?
            If so, which discount rule applies to each?
            """)
    @TableTest("""
            Scenario                | Passenger type  | Discount type?
            Adult and senior: Reis  | {adult, senior} | REIS
            Child: flat 20%         | child           | CHILD_FLAT
            """)
    void discountTypeByPassengerType(String passengerType, DiscountType expectedDiscountType) {
        assertEquals(expectedDiscountType, ReisDiscountCalculator.discountType(passengerType));
    }

    @TypeConverter
    public static BigDecimal parseBigDecimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }
}
```

**Four tables, each covering a distinct concern:**

1. **`reisDiscountTier`** — the core ladder rule. Value sets express tier membership (`{1,2,3,4}` → 0%, `{6–9}` → 5%) and both the natural max (`{40–44}` → 40%) and the cap boundary (`{45+}` → 40% capped). Key boundary row: ticket 5 is explicit to pin the exact threshold.

2. **`ticketsInRollingWindow`** — separated because deriving the count has its own edge cases: the exact boundary day (March 5 = 30 days before April 4), the day just outside (March 4), and same-day tickets. An open question flags whether the boundary is inclusive.

3. **`discountedTicketPrice`** — the arithmetic is simple but worth pinning with a non-round input (73.50 × 90% = 66.15) to force the rounding behavior to be specified.

4. **`discountTypeByPassengerType`** — the rule classification. Value set `{adult, senior}` → `REIS` asserts both produce the same rule, not just similar ones.

**Two open questions flagged in `@Description`:**
- Does the ticket count include the ticket being purchased now? (affects where the threshold sits)
- Is the 30-day boundary inclusive or exclusive?