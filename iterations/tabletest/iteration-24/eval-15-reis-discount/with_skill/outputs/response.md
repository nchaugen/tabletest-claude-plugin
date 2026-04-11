Here is the complete `ReisDiscountTest.java`:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountTest {

    private static final LocalDateTime PURCHASE_TIME = LocalDateTime.of(2024, 2, 1, 12, 0);

    @DisplayName("Reis discount percentage from trip count")
    @Description("""
            Trip count is the number of single tickets purchased in the rolling
            30-day window (see rollingTripCount). First discount (5%) applies when
            the count reaches 5. Each additional 5 trips adds 5%. Maximum is 40%.
            Open: does the current purchase count toward the window total, or only
            prior purchases? ("the first discount applies to ticket number five"
            suggests the current ticket is included in the count.)
            """)
    @TableTest("""
            Scenario           | Trips in last 30 days | Discount percentage?
            0% tier            | {1, 4}                | 0
            5% tier            | {5, 9}                | 5
            10% tier           | {10, 14}              | 10
            15% tier           | {15, 19}              | 15
            20% tier           | {20, 24}              | 20
            25% tier           | {25, 29}              | 25
            30% tier           | {30, 34}              | 30
            35% tier           | {35, 39}              | 35
            40% tier (max)     | {40, 50}              | 40
            """)
    void reisDiscountPercentage(int trips, int discountPercent) {
        assertEquals(discountPercent, ReisDiscount.discountPercentageForTripCount(trips));
    }

    @DisplayName("Discount by passenger type")
    @Description("""
            Adults and seniors receive the Reis discount based on their rolling
            30-day trip count. Children always receive a flat 20% discount,
            regardless of trip count or travel zone.
            """)
    @TableTest("""
            Scenario                        | Passenger type  | Trips in last 30 days | Discount percentage?
            No discount yet (adult/senior)  | {ADULT, SENIOR} | {1, 4}                | 0
            First discount tier             | {ADULT, SENIOR} | 5                     | 5
            Mid-ladder                      | {ADULT, SENIOR} | 25                    | 25
            Maximum discount                | {ADULT, SENIOR} | 40                    | 40
            Child flat discount             | CHILD           | {1, 5, 40}            | 20
            """)
    void discountByPassengerType(PassengerType passengerType, int trips, int discountPercent) {
        assertEquals(discountPercent, ReisDiscount.discountPercentage(passengerType, trips));
    }

    @DisplayName("Rolling 30-day trip count")
    @Description("""
            Counts prior single-ticket purchases within 30 days of the current
            purchase, measured backwards from the purchase timestamp.
            Open: is the 30-day boundary inclusive? A trip exactly 30 days before
            purchase may or may not count — confirm with the product team before
            implementing the boundary check.
            """)
    @TableTest("""
            Scenario                         | Prior trips (days before purchase) | Count?
            No prior trips                   | []                                 | 0
            All trips within window          | [1, 15, 29]                        | 3
            Single trip within window        | [1]                                | 1
            Trip just outside window         | [31]                               | 0
            Mix of inside and outside window | [1, 29, 31, 45]                    | 2
            Only trips outside window        | [31, 45, 60]                       | 0
            """)
    void rollingTripCount(List<LocalDateTime> priorTrips, int count) {
        assertEquals(count, ReisDiscount.countTripsInWindow(priorTrips, PURCHASE_TIME));
    }

    @TypeConverter
    public static List<LocalDateTime> parsePriorTrips(String value) {
        if (value == null || value.isBlank() || "[]".equals(value.strip())) {
            return List.of();
        }
        String inner = value.strip().replaceAll("^\\[|]$", "");
        return Arrays.stream(inner.split(","))
                .map(String::strip)
                .map(s -> PURCHASE_TIME.minusDays(Long.parseLong(s)))
                .toList();
    }
}
```

---

**Three tables, three concerns:**

**`reisDiscountPercentage`** — the ladder. Value sets express each tier (first and last value in range), making the tier boundaries the primary concept. Covers all 9 tiers including the 40% cap. An open question is flagged in `@Description` about whether the current purchase counts toward the total.

**`discountByPassengerType`** — the routing. `{ADULT, SENIOR}` as a value set asserts they behave identically (not just that they happen to produce the same result for a given input). `{1, 5, 40}` on the child row asserts the flat 20% holds regardless of trip count.

**`rollingTripCount`** — the derived input for the ladder. Expressed as days-before-purchase for readability; the `@TypeConverter` converts to actual `LocalDateTime` values relative to `PURCHASE_TIME`. The 30-day boundary row is intentionally excluded — the `@Description` flags it as an open question to resolve with the product team before implementing.