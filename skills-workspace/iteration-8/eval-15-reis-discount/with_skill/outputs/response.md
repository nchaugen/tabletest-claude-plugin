Here are the tests — I'll display them directly since file write requires approval:

```java
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Requires: org.tabletest:tabletest-junit (see Maven Central for version)
//           org.junit.jupiter:junit-jupiter 5.11+
class ReisSingleTicketDiscountTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Description("""
            Children receive a flat 20% discount on single tickets regardless of how
            often they travel. The recentTickets count has no effect on this rate.
            """)
    @TableTest("""
            Scenario                           | Customer type | Recent tickets | Discount?
            Child, no prior trips              | CHILD         | 0              | 20
            Child, below adult threshold       | CHILD         | 4              | 20
            Child, at adult first threshold    | CHILD         | 5              | 20
            Child, at adult max threshold      | CHILD         | 40             | 20
            Child, above adult max             | CHILD         | 45             | 20
            """)
    void childAlwaysReceivesFlatTwentyPercentDiscount(CustomerType customerType,
            int recentTickets, int discountPercent) {
        assertEquals(discountPercent,
                calculator.calculateDiscount(customerType, recentTickets));
    }

    @Description("""
            Adults and seniors accumulate a Reis discount based on single tickets purchased
            in the last 30 days, measured at the time of purchase. Every 5 tickets moves
            one step up the discount ladder (+5%), starting at ticket 5. The maximum
            discount of 40% is reached at 40 tickets and is capped there.

            Open question: does recentTickets count the ticket being purchased now, or
            only prior purchases? The boundary rows at 4/5 will expose whichever
            interpretation the implementation uses.
            """)
    @TableTest("""
            Scenario                         | Customer type   | Recent tickets | Discount?
            No prior trips                   | {ADULT, SENIOR} | 0              | 0
            Just below first threshold       | {ADULT, SENIOR} | 4              | 0
            First tier — ticket 5            | {ADULT, SENIOR} | 5              | 5
            Just below second threshold      | {ADULT, SENIOR} | 9              | 5
            Second tier — ticket 10          | {ADULT, SENIOR} | 10             | 10
            Just below third threshold       | {ADULT, SENIOR} | 14             | 10
            Third tier — ticket 15           | {ADULT, SENIOR} | 15             | 15
            Just below fourth threshold      | {ADULT, SENIOR} | 19             | 15
            Fourth tier — ticket 20          | {ADULT, SENIOR} | 20             | 20
            Just below fifth threshold       | {ADULT, SENIOR} | 24             | 20
            Fifth tier — ticket 25           | {ADULT, SENIOR} | 25             | 25
            Just below sixth threshold       | {ADULT, SENIOR} | 29             | 25
            Sixth tier — ticket 30           | {ADULT, SENIOR} | 30             | 30
            Just below seventh threshold     | {ADULT, SENIOR} | 34             | 30
            Seventh tier — ticket 35         | {ADULT, SENIOR} | 35             | 35
            Just below maximum               | {ADULT, SENIOR} | 39             | 35
            Maximum reached — ticket 40      | {ADULT, SENIOR} | 40             | 40
            Above maximum, capped at 40%     | {ADULT, SENIOR} | 50             | 40
            """)
    void adultAndSeniorAccumulateDiscountByRecentTicketCount(CustomerType customerType,
            int recentTickets, int discountPercent) {
        assertEquals(discountPercent,
                calculator.calculateDiscount(customerType, recentTickets));
    }
}
```

---

**Design decisions:**

**Two tables, not one.** Child and adult/senior discount rules have different logic (flat vs. step function) and different relevant inputs — combining them would require a column that's irrelevant for one group and meaningful for the other.

**Child table uses 5 explicit rows instead of a value set on `recentTickets`.** The rows are chosen to span the full range of adult tier boundaries (0, 4, 5, 40, 45), making it unambiguous that child discount is truly independent of trip count at every meaningful point — not just a coincidence at one value.

**Adult/Senior table pins every boundary pair.** Each tier transition is covered by one row just below (still the lower rate) and one row at (the new rate), e.g. 4→0% and 5→5%. Without both rows, an off-by-one (`> 5` instead of `>= 5`) passes all tests.

**`{ADULT, SENIOR}` value set** asserts that both types share identical discount logic. If they ever diverge, all rows fail for one type and the error is immediate.

**`recentTickets` ambiguity flagged in `@Description`.** The spec says "measured at the time of purchase" which is ambiguous about whether the current ticket is included. The 4/5 boundary row will detect whichever interpretation the implementation uses.

**Dependency note:** add `org.tabletest:tabletest-junit` to your `pom.xml`/`build.gradle` test scope — check Maven Central for the current version.