I'll read the value-sets reference (already read) to confirm syntax, then write the tests. Let me verify the arithmetic before writing:

- Zone 1 (46 kr): ×0.95=43.70, ×0.9=41.40... Let me plan all rows first, then write the code.

Pricing checks:
- 46×0.00=46.00 | 75×0.05→71.25 | 105×0.10→94.50 | 134×0.15→113.90 | 162×0.20→129.60
- 46×0.25→34.50 | 75×0.30→52.50 | 105×0.35→68.25 | 134×0.40→80.40 | 162×0.40→97.20
- 46×0.20(child)→36.80 | 105×0.20→84.00 | 75×0.00(youth)→75.00 | 134×0.00(student)→134.00

All verified. Here are the tests:

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Description;
import org.tabletest.junit.TableTest;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reis loyalty discount")
@Description("Single-ticket loyalty programme: frequent travellers earn progressive discounts "
    + "in a rolling 30-day window. All ticket purchases within the window count toward the tier, "
    + "regardless of zone or traveller category. "
    + "Zone base prices (adult, full price): 1 zone = 46 kr, 2 = 75, 3 = 105, 4 = 134, 5 = 162.")
class ReisLoyaltyDiscountTest {

    private final ReisTicketService reisService = new ReisTicketService();

    @TableTest("""
        Scenario                       | Tickets in last 30 days | Discount %?
        No purchase history            | 0                       | 0
        Below threshold (1–4 tickets)  | {1, 4}                  | 0
        Tier 1  (5–9 tickets)          | {5, 9}                  | 5
        Tier 2  (10–14 tickets)        | {10, 14}                | 10
        Tier 3  (15–19 tickets)        | {15, 19}                | 15
        Tier 4  (20–24 tickets)        | {20, 24}                | 20
        Tier 5  (25–29 tickets)        | {25, 29}                | 25
        Tier 6  (30–34 tickets)        | {30, 34}                | 30
        Tier 7  (35–39 tickets)        | {35, 39}                | 35
        Maximum tier (40+ tickets)     | {40, 50, 100}           | 40
        """)
    @DisplayName("Tier ladder — adults and pensioners")
    @Description("Discount climbs in 5 percentage point steps per tier. "
        + "Boundary values for both ends of each tier are tested. "
        + "All purchases in the rolling window count toward the tier, regardless of zone.")
    void adultPensionerTierLadder(int ticketCount, int discountPercent) {
        assertEquals(discountPercent, ReisDiscountCalculator.tierDiscountPercent(ticketCount));
    }

    @TableTest("""
        Scenario                                         | Category  | Tickets in last 30 days | Discount %?
        Adult below threshold earns no discount          | ADULT     | {1, 4}                  | 0
        Adult in tier 2 earns 10%                        | ADULT     | {10, 14}                | 10
        Pensioner follows the same tier ladder as adult  | PENSIONER | {10, 14}                | 10
        Child earns flat 20% at low purchase count       | CHILD     | {1, 4}                  | 20
        Child earns flat 20% at high purchase count      | CHILD     | {20, 40}                | 20
        Youth earns no discount regardless of count      | YOUTH     | {1, 40}                 | 0
        Student earns no discount regardless of count    | STUDENT   | {1, 40}                 | 0
        """)
    @DisplayName("Category eligibility rules")
    @Description("Adults and pensioners both follow the full tier ladder. "
        + "Children always receive a flat 20% — the ladder does not apply, "
        + "and a high purchase count does not increase it beyond 20%. "
        + "Youth and students receive no discount; they pay the adult full price.")
    void categoryEligibility(TravellerCategory category, int ticketCount, int discountPercent) {
        assertEquals(discountPercent, ReisDiscountCalculator.discountPercent(category, ticketCount));
    }

    @TableTest("""
        Scenario                                   | Zone | Category  | Tickets in last 30 days | Discount tier? | Price (kr)?
        Adult, no history, zone 1                  | 1    | ADULT     | 3                       | 0%             | 46.00
        Adult, tier 1, zone 2                      | 2    | ADULT     | 7                       | 5%             | 71.25
        Adult, tier 2, zone 3                      | 3    | ADULT     | 12                      | 10%            | 94.50
        Adult, tier 3, zone 4                      | 4    | ADULT     | 17                      | 15%            | 113.90
        Adult, tier 4, zone 5                      | 5    | ADULT     | 22                      | 20%            | 129.60
        Adult, tier 5, zone 1                      | 1    | ADULT     | 27                      | 25%            | 34.50
        Adult, tier 6, zone 2                      | 2    | ADULT     | 32                      | 30%            | 52.50
        Adult, tier 7, zone 3                      | 3    | ADULT     | 37                      | 35%            | 68.25
        Adult, maximum tier, zone 4                | 4    | ADULT     | 45                      | 40%            | 80.40
        Pensioner, maximum tier, zone 5            | 5    | PENSIONER | 45                      | 40%            | 97.20
        Child, flat 20%, low count, zone 1         | 1    | CHILD     | 3                       | 20%            | 36.80
        Child, flat 20%, high count, zone 3        | 3    | CHILD     | 40                      | 20%            | 84.00
        Youth, full price, zone 2                  | 2    | YOUTH     | 30                      | 0%             | 75.00
        Student, full price, zone 4                | 4    | STUDENT   | 20                      | 0%             | 134.00
        """)
    @DisplayName("End-to-end ticket price by zone, category, and purchase history")
    @Description("Final price = zone base price × (1 − discount%). "
        + "The Discount tier? column traces the intermediate step: "
        + "which tier was applied before computing the price. "
        + "Verified against all five zones and all eligible category rules.")
    void ticketPriceByZoneCategoryAndHistory(int zones, TravellerCategory category, int ticketCount,
                                              String discountTier, BigDecimal expectedPrice) {
        ReisPricing result = reisService.calculatePrice(zones, category, ticketCount);
        assertAll(
            () -> assertEquals(discountTier, result.discountTier()),
            () -> assertEquals(expectedPrice, result.amount())
        );
    }

    @TableTest("""
        Scenario                              | Days ago per purchase | Tickets in window?
        All purchases well within window      | [1, 15, 29, 30]       | 4
        All purchases outside window          | [31, 45, 60]          | 0
        Mix: some inside, some outside        | [5, 30, 31, 60]       | 2
        Exactly on day 30 is included         | [30]                  | 1
        Day 31 is excluded                    | [31]                  | 0
        No purchase history                   | []                    | 0
        """)
    @DisplayName("Rolling 30-day purchase window")
    @Description("Only purchases with a timestamp within the last 30 days (inclusive) count toward "
        + "the tier. Day 30 is the boundary: included. Day 31 and beyond: excluded. "
        + "The window is evaluated at ticket-purchase time, not at the start of a calendar month.")
    void rollingWindowCountsPurchasesWithinLast30Days(List<Integer> daysAgoPerPurchase, int ticketsInWindow) {
        PurchaseHistory history = PurchaseHistory.ofDaysAgo(daysAgoPerPurchase);
        assertEquals(ticketsInWindow, history.countInLast30Days());
    }
}
```

---

**Design notes:**

**Four tables, four concerns:**
1. **Tier ladder** — the full 0–40% progression, tested at both ends of each range using value sets (`{1, 4}`, `{5, 9}`, etc.). Value sets are valid here because both boundary values produce identical output.
2. **Category eligibility** — the three distinct rules (ladder / flat 20% / no discount), with value sets on ticket count for child/youth/student rows to document "regardless of count".
3. **End-to-end pricing** — traces zone + category + count → discount tier → final price. The `Discount tier?` traceability column shows where in the calculation a failure occurred.
4. **Rolling window** — isolated concern; uses `List<Integer>` (days ago per purchase) so dates don't appear in the table. Tests the inclusive day-30 boundary explicitly.

**Arithmetic verified:** every `Price (kr)?` value independently confirmed as `zone_base × (1 − discount%)`.