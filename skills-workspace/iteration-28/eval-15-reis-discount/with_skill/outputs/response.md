Here is the complete `ReisDiscountTest.java`:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReisDiscountTest {

    private final ReisDiscount reisDiscount = new ReisDiscount();

    @DisplayName("Tier discount for adults and seniors")
    @Description("""
            Adults and seniors receive the same tier-based Reis discount.
            The zone of travel is not an input — the discount applies regardless of which zones the ticket covers.
            Every fifth ticket steps up 5% on the ladder (tickets 5, 10, 15 … 40), capped at 40%.
            Open: does "previous tickets in window" count only completed purchases before this one,
            or does it include the current purchase? Rows assume previous purchases only.
            """)
    @TableTest("""
            Scenario                  | Passenger type  | Previous tickets in window | Discount %?
            0% tier: tickets 1–4      | {ADULT, SENIOR} | {0, 1, 2, 3}               | 0
            5% tier: tickets 5–9      | {ADULT, SENIOR} | {4, 5, 6, 7, 8}            | 5
            10% tier: tickets 10–14   | {ADULT, SENIOR} | {9, 10, 11, 12, 13}        | 10
            15% tier: tickets 15–19   | {ADULT, SENIOR} | {14, 15, 16, 17, 18}       | 15
            20% tier: tickets 20–24   | {ADULT, SENIOR} | {19, 20, 21, 22, 23}       | 20
            25% tier: tickets 25–29   | {ADULT, SENIOR} | {24, 25, 26, 27, 28}       | 25
            30% tier: tickets 30–34   | {ADULT, SENIOR} | {29, 30, 31, 32, 33}       | 30
            35% tier: tickets 35–39   | {ADULT, SENIOR} | {34, 35, 36, 37, 38}       | 35
            40% tier: tickets 40–44   | {ADULT, SENIOR} | {39, 40, 41, 42, 43}       | 40
            Above maximum (capped)    | {ADULT, SENIOR} | {44, 50, 100}              | 40
            """)
    void shouldApplyTierDiscountForAdultsAndSeniors(PassengerType passengerType,
                                                    int previousTicketsInWindow,
                                                    int expectedDiscountPercent) {
        assertEquals(expectedDiscountPercent,
                reisDiscount.discountPercent(passengerType, previousTicketsInWindow));
    }

    @DisplayName("Flat 20% discount for children")
    @Description("""
            Children always receive a 20% discount on single tickets, regardless of how
            often they travel. The Reis tier ladder does not apply to children.
            Open: what age threshold distinguishes a child from an adult?
            """)
    @TableTest("""
            Scenario          | Previous tickets in window | Discount %?
            Child: always 20% | {0, 4, 9, 39, 44}         | 20
            """)
    void shouldApplyFlatDiscountForChildren(int previousTicketsInWindow, int expectedDiscountPercent) {
        assertEquals(expectedDiscountPercent,
                reisDiscount.discountPercent(PassengerType.CHILD, previousTicketsInWindow));
    }

    @DisplayName("Count single tickets in the 30-day window")
    @Description("""
            The window spans 30 days (720 hours) ending at the moment of the current purchase.
            Current purchase time is fixed at 2024-06-15T12:00:00 for all rows.
            Open: is a ticket purchased exactly 30 days ago (to the second) inside or outside the window?
            The "just inside" and "just outside" rows bracket this boundary without resolving it.
            """)
    @TableTest("""
            Scenario                             | Previous purchase times                          | Count?
            No previous tickets                  | []                                               | 0
            One ticket well inside window        | [2024-06-05T10:00:00]                           | 1
            Multiple tickets inside window       | [2024-06-05T10:00:00, 2024-06-01T08:00:00]     | 2
            Mix of inside and outside window     | [2024-06-05T10:00:00, 2024-05-10T10:00:00]     | 1
            All tickets outside window           | [2024-04-01T10:00:00, 2024-03-01T10:00:00]     | 0
            Just inside boundary (29d 23h ago)   | [2024-05-16T13:00:00]                           | 1
            Just outside boundary (30d 1h ago)   | [2024-05-16T11:00:00]                           | 0
            """)
    void shouldCountTicketsInThirtyDayWindow(List<LocalDateTime> previousPurchases, int expectedCount) {
        LocalDateTime currentTime = LocalDateTime.of(2024, 6, 15, 12, 0, 0);
        assertEquals(expectedCount, reisDiscount.countInWindow(previousPurchases, currentTime));
    }

    @DisplayName("Calculate discounted ticket price")
    @Description("""
            Final price = base price × (1 − discount / 100), rounded to 2 decimal places.
            Base price is zone-dependent and determined upstream; discount is the percentage
            from the passenger's Reis tier or flat rate.
            """)
    @TableTest("""
            Scenario           | Base price | Discount % | Final price?
            No discount        | 100.00     | 0          | 100.00
            5% adult tier      | 100.00     | 5          | 95.00
            Child flat 20%     | 50.00      | 20         | 40.00
            Maximum 40% tier   | 85.50      | 40         | 51.30
            """)
    void shouldCalculateDiscountedTicketPrice(BigDecimal basePrice,
                                              int discountPercent,
                                              BigDecimal expectedFinalPrice) {
        assertEquals(expectedFinalPrice, reisDiscount.applyDiscount(basePrice, discountPercent));
    }
}
```

---

**Four tables, each covering a distinct concern:**

**1. Tier discount for adults & seniors** — 10 rows, one per tier on the discount ladder. Each row is a value-set pair: `{ADULT, SENIOR}` × the tier's range of previous-ticket counts. The Cartesian product (2 types × each count in the set) asserts that adults and seniors are treated identically at every tier. The last row covers the 40% cap.

**2. Flat discount for children** — a single row with a value set spanning representative counts from each tier. The value set explicitly documents that the ticket count is irrelevant for children — "regardless of" is visible in the table, not just asserted silently.

**3. Count tickets in the 30-day window** — the rolling-window logic. The "just inside" (29d 23h) and "just outside" (30d 1h) rows bracket the exact 30-day boundary without resolving the inclusivity question, which is flagged as open in `@Description`. All times are computed relative to a fixed reference time noted in the description.

**4. Discounted ticket price** — verifies the arithmetic for representative discount percentages. Zones are deliberately absent from the table (they affect base price, which is an upstream concern) — noted in `@Description`.

**Two open questions flagged for your team:**
- Is the window count inclusive of the current purchase or only completed prior purchases?
- What age defines a "child"?