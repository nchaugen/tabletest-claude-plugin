Here is the complete test class:

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReisDiscountCalculatorTest {

    private ReisDiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ReisDiscountCalculator();
    }

    @DisplayName("Reis discount tier")
    @Description("""
            Discount is based on the number of single tickets bought in the rolling
            30-day window ending at the moment of purchase — not counting the ticket
            currently being purchased.
            Every fifth trip moves the discount 5% higher on the ladder. Maximum is 40%.
            Applies to adults and seniors only; children receive a flat discount instead.
            """)
    @TableTest("""
            Scenario                              | Tickets Bought in Last 30 Days | Discount?
            No discount — first four tickets      | {0, 1, 2, 3}                   | 0
            First tier kicks in — ticket 5        | 4                              | 5
            First tier holds through ticket 9     | {5, 8}                         | 5
            Second tier kicks in — ticket 10      | 9                              | 10
            Intermediate tier — ticket 20         | 19                             | 20
            Maximum tier reached — ticket 40      | 39                             | 40
            Cap holds beyond maximum              | {44, 60}                       | 40
            """)
    void reisDiscountTier(int ticketsBoughtInLast30Days, int discount) {
        assertEquals(discount, calculator.reisDiscountPercent(ticketsBoughtInLast30Days));
    }

    @DisplayName("Single ticket discount by passenger type")
    @Description("""
            Adults and seniors: Reis discount based on purchase history (see reisDiscountTier).
            Children: flat 20% regardless of Reis history.
            Zone never affects the discount for any passenger type.
            """)
    @TableTest("""
            Scenario                                    | Passenger Type | Tickets Bought in Last 30 Days | Zone      | Discount?
            Adult, no history                           | ADULT          | 0                              | {1, 2, 3} | 0
            Adult, first Reis tier                      | ADULT          | 4                              | {1, 2, 3} | 5
            Adult, mid-ladder                           | ADULT          | 9                              | {1, 2, 3} | 10
            Senior treated same as adult — no history   | SENIOR         | 0                              | {1, 2, 3} | 0
            Senior, first Reis tier                     | SENIOR         | 4                              | {1, 2, 3} | 5
            Child, flat 20% — no history                | CHILD          | 0                              | {1, 2, 3} | 20
            Child, flat 20% — high Reis history ignored | CHILD          | 40                             | {1, 2, 3} | 20
            """)
    void singleTicketDiscount(PassengerType passengerType, int ticketsBoughtInLast30Days,
                              int zone, int discount) {
        assertEquals(discount, calculator.singleTicketDiscountPercent(
                passengerType, ticketsBoughtInLast30Days, zone));
    }
}
```

---

Two methods, two concerns:

**`reisDiscountTier`** — the ladder rule in isolation. The `{0,1,2,3}` value set documents that all four no-discount tickets behave identically; the explicit `4` / `9` rows pin down the exact tier boundaries; `{44,60}` shows the cap is enforced beyond ticket 40 (where without the cap, ticket 45 would yield 45%).

**`singleTicketDiscount`** — routes by passenger type. The `{1,2,3}` value set on Zone declares in the table itself that zone is irrelevant, generating three test cases per row without listing each zone separately. The two `CHILD` rows (history=0 and history=40) together prove the flat 20% rule ignores Reis state.

The interface implied by the tests:
- `ReisDiscountCalculator.reisDiscountPercent(int ticketsBoughtInLast30Days) → int`
- `ReisDiscountCalculator.singleTicketDiscountPercent(PassengerType, int ticketsBoughtInLast30Days, int zone) → int`
- `PassengerType` enum: `ADULT`, `SENIOR`, `CHILD`