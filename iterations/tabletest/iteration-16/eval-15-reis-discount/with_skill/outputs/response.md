Here's the test class I've designed (`ReisDiscountTest.java`):

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.Description;
import org.tabletest.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReisDiscountTest {

    @DisplayName("Reis discount tier")
    @Description("""
            Applies to adult and senior passengers only.
            Discount is based on tickets purchased in the last 30 days, measured at time of purchase.
            Every 5 tickets advances one tier (+5%). The first discount applies from ticket 5.
            Maximum discount is 40%.
            """)
    @TableTest("""
            Scenario         | Trips in last 30 days | Discount?
            Below first tier | {0, 1, 2, 3, 4}       | 0
            First tier       | {5, 6, 7, 8, 9}       | 5
            Second tier      | {10, 11, 12, 13, 14}  | 10
            Third tier       | {15, 16, 17, 18, 19}  | 15
            Fourth tier      | {20, 21, 22, 23, 24}  | 20
            Fifth tier       | {25, 26, 27, 28, 29}  | 25
            Sixth tier       | {30, 31, 32, 33, 34}  | 30
            Seventh tier     | {35, 36, 37, 38, 39}  | 35
            Maximum tier     | {40, 45, 100}         | 40
            """)
    void reisDiscountTier(int tripsInLast30Days, int discountPercent) {
        assertEquals(discountPercent, ReisPricing.reisDiscount(tripsInLast30Days));
    }

    @DisplayName("Discount by passenger type")
    @Description("""
            Adults and seniors receive the Reis ladder discount based on travel frequency.
            Children always receive a flat 20% discount regardless of trip count.
            Open: are there other passenger types (e.g., youth, infant)?
            """)
    @TableTest("""
            Scenario                 | Passenger type | Trips in last 30 days | Discount?
            Child, infrequent        | CHILD          | {0, 3, 4}             | 20
            Child, frequent          | CHILD          | {5, 20, 40}           | 20
            Adult, below first tier  | ADULT          | {0, 1, 4}             | 0
            Adult, first tier        | ADULT          | 5                     | 5
            Adult, maximum tier      | ADULT          | 40                    | 40
            Senior, below first tier | SENIOR         | {0, 1, 4}             | 0
            Senior, maximum tier     | SENIOR         | 40                    | 40
            """)
    void discountByPassengerType(PassengerType type, int tripsInLast30Days, int discountPercent) {
        assertEquals(discountPercent, ReisPricing.discount(type, tripsInLast30Days));
    }

    @DisplayName("Discounted ticket price")
    @Description("""
            Final price = standard price × (1 − discount% / 100).
            Open: is the result rounded to 2 decimal places (half-up), or truncated?
            """)
    @TableTest("""
            Scenario              | Standard price | Passenger type | Trips in last 30 days | Final price?
            No discount           | 50.00          | ADULT          | 3                     | 50.00
            5% Reis discount      | 50.00          | ADULT          | 5                     | 47.50
            40% max discount      | 100.00         | ADULT          | 40                    | 60.00
            Child flat 20%        | 50.00          | CHILD          | 0                     | 40.00
            Child ignores trips   | 50.00          | CHILD          | 40                    | 40.00
            """)
    void discountedTicketPrice(BigDecimal standardPrice, PassengerType type,
                               int tripsInLast30Days, BigDecimal finalPrice) {
        assertEquals(0, finalPrice.compareTo(ReisPricing.price(standardPrice, type, tripsInLast30Days)));
    }
}
```

**Three concerns, three tables:**

- **`reisDiscountTier`** — documents the full ladder using value sets per tier (e.g. `{5, 6, 7, 8, 9} → 5%`), including the cap at `{40, 45, 100} → 40%`. This is the cleanest way to show tier structure without enumerating every possible trip count.
- **`discountByPassengerType`** — tests the branching rule: children get 20% flat (value sets confirm it's *regardless of* trip count), adults and seniors follow the Reis ladder.
- **`discountedTicketPrice`** — tests the arithmetic separately, keeping the ladder logic tests free of multiplication noise.

**Assumed API** (adjust to match your actual class/method names):
- `ReisPricing.reisDiscount(int tripsInLast30Days)` → `int`
- `ReisPricing.discount(PassengerType type, int tripsInLast30Days)` → `int`
- `ReisPricing.price(BigDecimal standardPrice, PassengerType type, int tripsInLast30Days)` → `BigDecimal`

Two open questions are flagged in `@Description`: other passenger types (youth? infant?), and rounding behaviour for the final price.