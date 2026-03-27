Here are the TableTests for the Reis discount feature:

```java
package com.transit.reis;

import org.junit.jupiter.api.Description;
import org.tabletest.junit5.TableTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ReisDiscountTest {

    private final ReisTicketPricing pricing = new ReisTicketPricing();

    @TableTest("""
            Scenario                       | Passenger type  | Tickets in last 30 days | Discount %?
            No travel history              | {ADULT, SENIOR} | 0                       | 0
            Just below first threshold     | {ADULT, SENIOR} | 4                       | 0
            First discount kicks in        | {ADULT, SENIOR} | 5                       | 5
            Top of first tier              | {ADULT, SENIOR} | 9                       | 5
            Second tier                    | {ADULT, SENIOR} | 10                      | 10
            Third tier                     | {ADULT, SENIOR} | 15                      | 15
            Fourth tier                    | {ADULT, SENIOR} | 20                      | 20
            Fifth tier                     | {ADULT, SENIOR} | 25                      | 25
            Sixth tier                     | {ADULT, SENIOR} | 30                      | 30
            Seventh tier                   | {ADULT, SENIOR} | 35                      | 35
            Maximum discount reached       | {ADULT, SENIOR} | 40                      | 40
            Above maximum, still capped    | {ADULT, SENIOR} | 55                      | 40
            """)
    @Description("""
            Adults and seniors accumulate a personal Reis discount based on how many single \
            tickets they have purchased in the last 30 days, measured at the time of purchase. \
            The discount starts at 5% from the fifth ticket onwards, increases by 5% per \
            additional five tickets, and is capped at a maximum of 40%.\
            """)
    void reisDiscountLadderForAdultsAndSeniors(PassengerType passengerType,
            int ticketsInLast30Days, int expectedDiscountPercent) {
        assertThat(pricing.discountPercent(passengerType, ticketsInLast30Days))
                .isEqualTo(expectedDiscountPercent);
    }

    @TableTest("""
            Scenario                     | Tickets in last 30 days | Base price | Discount %? | Final price?
            No discount yet              | 0                       | 100.00     | 0           | 100.00
            5% discount at fifth ticket  | 5                       | 100.00     | 5           | 95.00
            10% discount                 | 10                      | 200.00     | 10          | 180.00
            20% discount                 | 20                      | 50.00      | 20          | 40.00
            Maximum 40% discount         | 40                      | 100.00     | 40          | 60.00
            Cap applies above maximum    | 55                      | 100.00     | 40          | 60.00
            """)
    @Description("""
            The Reis discount is applied to the standard single ticket price for adults. \
            The discount tier and final price are both shown so the tier-to-price derivation \
            can be traced step by step.\
            """)
    void adultTicketPriceReflectsReisDiscount(int ticketsInLast30Days, BigDecimal basePrice,
            int discountPercent, BigDecimal finalPrice) {
        TicketPrice result = pricing.calculate(PassengerType.ADULT, ticketsInLast30Days, basePrice);
        assertThat(result.discountPercent()).isEqualTo(discountPercent);
        assertThat(result.finalPrice()).isEqualByComparingTo(finalPrice);
    }

    @TableTest("""
            Scenario              | Tickets in last 30 days | Base price | Discount %? | Final price?
            Occasional traveller  | {0, 4, 10, 40}          | 100.00     | 20          | 80.00
            Higher-priced ticket  | {0, 4, 10, 40}          | 250.00     | 20          | 200.00
            """)
    @Description("""
            Children always receive a flat 20% discount on single tickets, regardless of \
            how many tickets have been purchased in the last 30 days.\
            """)
    void childrenAlwaysReceiveFlatTwentyPercentDiscount(int ticketsInLast30Days,
            BigDecimal basePrice, int discountPercent, BigDecimal finalPrice) {
        TicketPrice result = pricing.calculate(PassengerType.CHILD, ticketsInLast30Days, basePrice);
        assertThat(result.discountPercent()).isEqualTo(discountPercent);
        assertThat(result.finalPrice()).isEqualByComparingTo(finalPrice);
    }
}
```

**Design decisions:**

- **`reisDiscountLadderForAdultsAndSeniors`**: Value set `{ADULT, SENIOR}` on the passenger type column expresses that both use the same ladder — one row covers both types without duplication. All 8 tier boundaries are included to pin down the exact step function.

- **`adultTicketPriceReflectsReisDiscount`**: `Discount %?` is a traceability column — not strictly needed, but it lets you see at a glance which tier is active for each row and diagnose failures at the tier level vs. the arithmetic level.

- **`childrenAlwaysReceiveFlatTwentyPercentDiscount`**: Value set `{0, 4, 10, 40}` on the tickets column expresses the "regardless" relationship directly — the rule says the count doesn't matter for children, and the table structure makes that visible. Two base prices confirm the 20% is applied correctly to different amounts.