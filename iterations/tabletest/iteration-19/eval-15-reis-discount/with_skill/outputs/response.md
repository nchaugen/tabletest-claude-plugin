Here is the test class (`src/test/java/ReisDiscountTest.java`):

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReisDiscountTest {

    @DisplayName("Reis discount tier")
    @Description("""
            Applies to adult and senior passengers buying single tickets.
            "Prior tickets" = single tickets purchased in the 30 days before
            this purchase, not counting the current ticket.
            Every 5 prior trips moves one rung up the discount ladder.
            The first discount (5%) kicks in at 4 prior tickets (the 5th purchase).
            Maximum discount is 40%, reached at 39 prior tickets.
            """)
    @TableTest("""
            Scenario                         | Prior tickets (last 30 days) | Discount?
            No travel history                | 0                            | 0
            Below first threshold            | {1, 2, 3}                    | 0
            First threshold — fifth trip     | 4                            | 5
            First tier upper boundary        | 8                            | 5
            Second tier lower boundary       | 9                            | 10
            Second tier upper boundary       | 13                           | 10
            Third tier                       | {14, 18}                     | 15
            Fourth tier                      | {19, 23}                     | 20
            Fifth tier                       | {24, 28}                     | 25
            Sixth tier                       | {29, 33}                     | 30
            Seventh tier                     | {34, 38}                     | 35
            Maximum tier lower boundary      | 39                           | 40
            Well above maximum — cap applies | 50                           | 40
            """)
    void reisDiscountTier(int priorTickets, int expectedDiscountPercent) {
        assertEquals(expectedDiscountPercent,
                ReisDiscount.tierPercentForAdultOrSenior(priorTickets));
    }

    @DisplayName("Discounted ticket price by passenger type")
    @Description("""
            Adults and seniors receive the Reis tier discount (based on prior trips).
            Children always receive a flat 20% discount, regardless of travel history.
            Zones have no effect on the discount amount.
            "Discount?" is a traceability column showing which percentage was applied.
            Open: are there other passenger types beyond Adult, Senior, and Child?
            """)
    @TableTest("""
            Scenario                                       | Passenger type | Prior tickets | Base price | Discount? | Discounted price?
            Adult, no prior trips                          | ADULT          | 0             | 100.00     | 0         | 100.00
            Adult, first Reis tier                         | ADULT          | 4             | 100.00     | 5         | 95.00
            Senior follows same Reis tiers as adult        | SENIOR         | 4             | 100.00     | 5         | 95.00
            Adult, maximum Reis discount                   | ADULT          | 39            | 100.00     | 40        | 60.00
            Child — flat 20% regardless of travel history  | CHILD          | {0, 4, 39}    | 100.00     | 20        | 80.00
            Child — flat 20% on different base price       | CHILD          | 10            | 50.00      | 20        | 40.00
            """)
    void discountedTicketPrice(PassengerType passengerType, int priorTickets,
                               BigDecimal basePrice, int discountPercent,
                               BigDecimal discountedPrice) {
        TicketPricer pricer = new TicketPricer(passengerType, priorTickets);
        assertAll(
                () -> assertEquals(discountPercent, pricer.discountPercent()),
                () -> assertEquals(discountedPrice, pricer.discountedPrice(basePrice))
        );
    }
}
```

---

**Key design decisions:**

**Two tables, two concerns.** `reisDiscountTier` isolates the tier ladder rule; `discountedTicketPrice` covers how passenger type selects the scheme plus the resulting price. Arithmetic is light (2–3 rows would suffice to verify the formula), so the second table earns its keep through the passenger-type rule coverage.

**Value sets at every tier boundary.** Each tier row shows the lower and upper boundary explicitly (e.g. `4` and `8` for 5%), with mid-tier values grouped as `{14, 18}`. This expresses the tier structure as a claim — "all values in this range produce 15%" — rather than just spot-checking a few.

**`{0, 4, 39}` for the child row** asserts that the 20% flat discount holds regardless of travel history, across no trips, a Reis-tier-triggering count, and the maximum Reis count. A blank would wrongly imply the field is absent.

**`Discount?` as a traceability column** in the second table lets you see the intermediate result (which percentage was applied) before verifying the price — useful when a row fails.

**One assumption to confirm:** "Prior tickets" counts tickets purchased *before* the current one (past tense: "tickets you have purchased"). If the spec means *including* the current ticket, shift all prior-ticket thresholds down by 1 (e.g. first threshold at `5` instead of `4`).