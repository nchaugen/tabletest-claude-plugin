Here is the complete test class:

```java
package com.example.travel;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.annotation.Description;
import org.tabletest.annotation.TableTest;
import org.tabletest.annotation.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReisDiscountTest {

    @DisplayName("Discount tier by ticket count")
    @Description("""
            Applies to adult and senior travellers only. The ladder starts at
            ticket 5 — purchases 1–4 receive no discount. Every five tickets
            advances the tier by 5 percentage points. Discount is capped at 40%.
            """)
    @TableTest("""
            Scenario      | Tickets in last 30 days | Discount%?
            No discount   | {0, 1, 2, 3, 4}         | 0
            Tier 1        | {5, 6, 7, 8, 9}         | 5
            Tier 2        | {10, 11, 12, 13, 14}    | 10
            Tier 3        | {15, 16, 17, 18, 19}    | 15
            Tier 4        | {20, 21, 22, 23, 24}    | 20
            Tier 5        | {25, 26, 27, 28, 29}    | 25
            Tier 6        | {30, 31, 32, 33, 34}    | 30
            Tier 7        | {35, 36, 37, 38, 39}    | 35
            Maximum tier  | {40, 50, 100}            | 40
            """)
    void discountTierByTicketCount(int ticketCount, int expectedDiscountPercent) {
        assertEquals(expectedDiscountPercent, ReisDiscount.discountPercentageFor(ticketCount));
    }

    @DisplayName("Discount type by traveller type")
    @Description("""
            Adult and senior travellers receive the Reis ladder discount based on
            their 30-day ticket count. Children always receive a flat 20% discount
            regardless of ticket count. Zone is irrelevant to the discount type —
            all zone combinations produce the same result.
            """)
    @TableTest("""
            Scenario           | Traveller type  | Zone                        | Discount type?
            Adults and seniors | {ADULT, SENIOR} | {ZONE_A, ZONE_AB, ZONE_ABC} | REIS_LADDER
            Child              | CHILD           | {ZONE_A, ZONE_AB, ZONE_ABC} | FLAT_20_PERCENT
            """)
    void discountTypeByTravellerType(TravellerType travellerType, Zone zone, DiscountType expectedType) {
        assertEquals(expectedType, ReisDiscount.discountTypeFor(travellerType, zone));
    }

    @DisplayName("Ticket count in 30-day rolling window")
    @Description("""
            Counts single tickets purchased within the 30-day window measured at
            the time of purchase. Purchase dates below are expressed relative to today.
            Open question: is a ticket from exactly 30 days ago included or excluded?
            The table assumes included — adjust the boundary row if the rule differs.
            """)
    @TableTest("""
            Scenario                | Purchase dates              | Count?
            No purchase history     | []                          | 0
            Single ticket today     | [0 days ago]                | 1
            At the 30-day boundary  | [30 days ago]               | 1
            Just outside the window | [31 days ago]               | 0
            Mix inside and outside  | [14 days ago, 31 days ago]  | 1
            Multiple in window      | [1 days ago, 15 days ago]   | 2
            """)
    void countTicketsInRollingWindow(List<LocalDate> purchaseDates, int expectedCount) {
        LocalDate today = LocalDate.now();
        assertEquals(expectedCount, ReisDiscount.countInWindow(purchaseDates, today));
    }

    @DisplayName("Discounted ticket price")
    @Description("""
            Applies the discount percentage as a reduction on the standard single
            ticket price. The same calculation applies whether the discount came
            from the Reis ladder (adults/seniors) or the flat rate (children).
            """)
    @TableTest("""
            Scenario     | Base price | Discount% | Price?
            No discount  | 40.00      | 0         | 40.00
            Tier 1       | 40.00      | 5         | 38.00
            Children     | 40.00      | 20        | 32.00
            Maximum      | 40.00      | 40        | 24.00
            """)
    void discountedTicketPrice(BigDecimal basePrice, int discountPercent, BigDecimal expectedPrice) {
        assertEquals(0, expectedPrice.compareTo(ReisDiscount.applyDiscount(basePrice, discountPercent)));
    }

    @TypeConverter
    static LocalDate toRelativeDate(String value) {
        int daysAgo = Integer.parseInt(value.replace(" days ago", "").trim());
        return LocalDate.now().minusDays(daysAgo);
    }
}
```

**Four tables, three concerns + arithmetic:**

- **Discount tier** — all 9 tiers (0%–40%), one row each with value sets for the ticket count ranges so tier structure is immediately obvious
- **Discount type** — `{ADULT, SENIOR}` value set (same rules, no separate rows), `{ZONE_A, ZONE_AB, ZONE_ABC}` value set making zone irrelevance explicit and visible
- **Rolling window** — relative dates ("14 days ago") via `@TypeConverter`; the 30-day boundary is ambiguous in the requirements and flagged as an open question in `@Description`
- **Discounted price** — small arithmetic table (4 rows); uses `compareTo` to avoid BigDecimal scale sensitivity

The referenced classes (`ReisDiscount`, `TravellerType`, `Zone`, `DiscountType`) are placeholders — rename them to match your production code.