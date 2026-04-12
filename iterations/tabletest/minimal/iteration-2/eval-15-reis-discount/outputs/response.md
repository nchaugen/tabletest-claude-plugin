```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountTest {

    @DisplayName("Discount percentage by passenger type and recent trips")
    @Description("""
            "Prior trips" means tickets purchased in the last 30 days before the current purchase.
            Adults and seniors share the same discount ladder: every 5th trip adds 5%, starting at
            the 5th trip (4 prior trips). Maximum is 40%. Zone has no effect on the discount.
            Open: the requirement states the count is "measured at the time of purchase" — it is
            unclear whether it includes the ticket currently being purchased. These tests model
            the count as excluding the current purchase.
            """)
    @TableTest("""
            Scenario                      | Passenger type  | Prior trips in last 30 days | Discount %?
            No prior trips                | {ADULT, SENIOR} | 0                           | 0
            Below first tier              | {ADULT, SENIOR} | 3                           | 0
            At first tier (5th ticket)    | {ADULT, SENIOR} | 4                           | 5
            Within first tier             | {ADULT, SENIOR} | 8                           | 5
            At second tier (10th ticket)  | {ADULT, SENIOR} | 9                           | 10
            Mid-ladder (20th ticket)      | {ADULT, SENIOR} | 19                          | 20
            At maximum (40th ticket)      | {ADULT, SENIOR} | 39                          | 40
            Above maximum, capped at 40%  | {ADULT, SENIOR} | 100                         | 40
            Child, flat 20% regardless    | CHILD           | {0, 4, 39}                  | 20
            """)
    void discountPercentByPassengerTypeAndTrips(PassengerType passengerType, int priorTrips, int expectedDiscountPercent) {
        assertEquals(expectedDiscountPercent, ReisDiscount.discountPercent(passengerType, priorTrips));
    }

    @DisplayName("Ticket price with discount applied")
    @TableTest("""
            Scenario             | Standard price | Discount % | Final price?
            No discount          | 50.00          | 0          | 50.00
            5% adult discount    | 50.00          | 5          | 47.50
            20% child discount   | 50.00          | 20         | 40.00
            40% maximum discount | 50.00          | 40         | 30.00
            """)
    void ticketPriceWithDiscount(BigDecimal standardPrice, int discountPercent, BigDecimal expectedPrice) {
        assertEquals(expectedPrice, ReisDiscount.applyDiscount(standardPrice, discountPercent));
    }
}
```

---

Two tables covering two separate concerns:

**`discountPercentByPassengerTypeAndTrips`** — the decision rule:
- `{ADULT, SENIOR}` value sets assert both types follow the identical ladder (no need to duplicate rows)
- Boundaries tested at both sides of each threshold (3 vs 4, 8 vs 9, 39 vs 100)
- Child row uses `{0, 4, 39}` to assert the flat 20% holds even at values that are tier boundaries for adults
- Open question flagged in `@Description`: whether "trips in last 30 days" includes or excludes the current purchase (the requirement says "measured at the time of purchase" but doesn't clarify)

**`ticketPriceWithDiscount`** — the arithmetic, kept separate from the rule:
- Uses a fixed standard price of 50.00 so expected prices are traceable directly to inputs