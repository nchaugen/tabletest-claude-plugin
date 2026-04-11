Working through the requirements step by step before writing any code.

**Concerns identified:**
1. Discount percentage by passenger type and travel history (adult/senior tier-based; child flat 20%)
2. Count of tickets in the 30-day rolling window (derived value — fed into concern 1)
3. Apply the discount to the base ticket price (arithmetic)

**Key design decisions:**
- Adult and senior get identical tiers → value set `{Adult, Senior}` on every tier row makes the assertion explicit
- "Regardless of zones" → zone isn't an input to the discount% method; documented in `@Description` noting zone affects base price, not discount %
- Rolling window boundary (is 30-days-ago inclusive?) is ambiguous → `@Description` open question; boundary row assumes inclusive
- Each 5-ticket step = +5%, starting at ticket 5, capped at 40% → 9 tiers, expressed with value sets for tier membership

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReisDiscountTest {

    @DisplayName("Discount percentage by passenger type")
    @Description("""
            Adult and senior passengers receive a tier-based Reis discount
            determined by the number of single tickets purchased in the last
            30 days at the time of purchase. The same tiers apply to adult
            and senior equally. Zone affects the base ticket price but not
            the discount percentage — the same tier applies across all zone
            combinations. Maximum discount is 40%.
            Children receive a flat 20% discount regardless of travel history.
            """)
    @TableTest("""
            Scenario                   | Passenger type  | Tickets in last 30 days | Discount %?
            Adult/senior, no discount  | {Adult, Senior} | {0, 1, 2, 3, 4}        | 0
            Adult/senior, tier 1       | {Adult, Senior} | {5, 9}                  | 5
            Adult/senior, tier 2       | {Adult, Senior} | {10, 14}                | 10
            Adult/senior, tier 3       | {Adult, Senior} | {15, 19}                | 15
            Adult/senior, tier 4       | {Adult, Senior} | {20, 24}                | 20
            Adult/senior, tier 5       | {Adult, Senior} | {25, 29}                | 25
            Adult/senior, tier 6       | {Adult, Senior} | {30, 34}                | 30
            Adult/senior, tier 7       | {Adult, Senior} | {35, 39}                | 35
            Adult/senior, max discount | {Adult, Senior} | {40, 50}                | 40
            Child, regardless of count | Child           | {0, 1, 5, 40}           | 20
            """)
    void discountPercentByPassengerType(String passengerType, int ticketCount, int discountPercent) {
        assertEquals(discountPercent, ReisDiscount.discountPercent(passengerType, ticketCount));
    }

    @DisplayName("Count of single tickets in 30-day rolling window")
    @Description("""
            Counts single tickets purchased within the last 30 days before the
            current purchase, measured at the time of purchase. The current
            ticket being purchased does not count toward its own discount.
            All rows use purchase date 2026-04-05; the 30-day window spans back
            to 2026-03-06.
            Open: is a ticket purchased exactly 30 days ago (2026-03-06) included
            in the count? The 'At boundary' row assumes inclusive.
            """)
    @TableTest("""
            Scenario                    | Previous purchase dates                 | Count?
            No previous purchases       | []                                      | 0
            All within window           | [2026-03-07, 2026-03-15, 2026-04-04]   | 3
            All outside window          | [2026-01-01, 2026-03-05]               | 0
            Mixed inside and outside    | [2026-03-07, 2026-03-05, 2026-04-01]   | 2
            Just inside (29 days ago)   | [2026-03-07]                           | 1
            At boundary (30 days ago)   | [2026-03-06]                           | 1
            Just outside (31 days ago)  | [2026-03-05]                           | 0
            """)
    void countTicketsInRollingWindow(List<LocalDate> previousPurchases, int count) {
        LocalDate purchaseDate = LocalDate.of(2026, 4, 5);
        assertEquals(count, ReisDiscount.countTicketsInWindow(previousPurchases, purchaseDate));
    }

    @DisplayName("Discounted single ticket price")
    @Description("""
            Final price = base price × (1 − discount% / 100).
            Base price is set by the ticket zone; discount% is determined by
            passenger type and travel history (see discountPercentByPassengerType).
            """)
    @TableTest("""
            Scenario              | Base price | Discount % | Final price?
            No discount           | 100.00     | 0          | 100.00
            5% Reis discount      | 100.00     | 5          | 95.00
            20% child discount    | 100.00     | 20         | 80.00
            40% maximum discount  | 100.00     | 40         | 60.00
            Non-round base price  | 37.50      | 20         | 30.00
            """)
    void discountedSingleTicketPrice(BigDecimal basePrice, int discountPercent, BigDecimal finalPrice) {
        assertEquals(finalPrice, ReisDiscount.applyDiscount(basePrice, discountPercent));
    }
}
```

A few notes on what's captured here:

- **Tier boundaries**: The value sets `{0,1,2,3,4}` / `{5,9}` / `{10,14}` etc. cover both interior values and tier boundaries in each row, making the step-at-5 rule explicit across all 9 levels.
- **Adult = Senior**: The `{Adult, Senior}` value set on every tier row actively asserts they produce identical results — not just that they happen to.
- **Child irrelevance**: The `{0,1,5,40}` value set on the child row asserts the flat 20% holds regardless of how many tickets they've bought.
- **Open question**: The 30-day boundary row is flagged in `@Description` with its assumption stated — the implementation team should confirm before shipping.
- **Dependency**: Add `org.tabletest:tabletest-junit` to your `pom.xml` / `build.gradle` if not already present.