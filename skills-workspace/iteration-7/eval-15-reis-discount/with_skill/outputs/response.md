No `pom.xml` found — this is a greenfield context, so I'll note the dependency. Good value-sets syntax confirmed. Now let me write the tests.

The feature has two distinct discount mechanisms, which map cleanly to two focused tables:
- **Child**: flat 20%, regardless of trip count → value set on `recentTickets`
- **Adult/Senior**: step function based on trips in last 30 days → boundary rows to pin the step

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import io.github.tabletest.annotation.Description;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Requires: org.tabletest:tabletest-junit (check pom.xml/build.gradle for exact coordinates)
@DisplayName("Reis single-ticket discount")
class ReisSingleTicketDiscountTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Description(
        "Children always receive a flat 20% discount on single tickets, "
        + "regardless of how many tickets they have purchased recently.")
    @TableTest("""
        Scenario                         | Recent tickets | Discount?
        Child, flat rate regardless      | {0, 5, 20, 40} | 20
        """)
    void childDiscountIsAlwaysFlat20Percent(int recentTickets, int discountPercent) {
        assertEquals(discountPercent,
            calculator.calculateDiscount(CustomerType.CHILD, recentTickets));
    }

    @Description(
        "Adults and seniors accumulate a Reis discount based on single tickets purchased "
        + "in the last 30 days, measured at time of purchase. Every 5 trips moves one step "
        + "up the discount ladder (+5%). The first discount applies at the 5th trip; "
        + "the maximum of 40% is reached at 40 trips and is never exceeded.")
    @TableTest("""
        Scenario                     | Customer type   | Recent tickets | Discount?
        No prior trips               | {ADULT, SENIOR} | 0              | 0
        Just below first threshold   | {ADULT, SENIOR} | 4              | 0
        First tier — 5th trip        | {ADULT, SENIOR} | 5              | 5
        Still in first tier          | {ADULT, SENIOR} | 9              | 5
        Second tier — 10th trip      | {ADULT, SENIOR} | 10             | 10
        Third tier — 15th trip       | {ADULT, SENIOR} | 15             | 15
        Midway up ladder             | {ADULT, SENIOR} | 20             | 20
        Near maximum — 39th trip     | {ADULT, SENIOR} | 39             | 35
        Maximum reached — 40th trip  | {ADULT, SENIOR} | 40             | 40
        Above maximum, capped at 40% | {ADULT, SENIOR} | 50             | 40
        """)
    void adultAndSeniorAccumulateReisDiscountByTripFrequency(
            CustomerType customerType, int recentTickets, int discountPercent) {
        assertEquals(discountPercent,
            calculator.calculateDiscount(customerType, recentTickets));
    }
}
```

**Coverage rationale:**

- `{ADULT, SENIOR}` value set asserts both types share the same discount logic — if they diverge, all rows fail for one type
- `{0, 5, 20, 40}` value set for children documents that recent trip count is irrelevant to the child rate
- Boundary rows at 4/5 and 9/10 pin down the exact step-function threshold (is it `>= 5` or `> 5`?) — both rows are needed to catch an off-by-one
- Row at 50 trips confirms the 40% cap is enforced, not just that 40 happens to return 40%

**One spec ambiguity worth resolving with the team**: does `recentTickets` include the current ticket being purchased, or only prior ones? The spec says "purchased in the last 30 days, measured at the time of purchase" which could mean either. The boundary row `4 → 0%, 5 → 5%` will expose whichever interpretation the implementation uses — if the threshold is actually at 4 prior tickets (5th purchase), those two rows will fail and signal the mismatch.