Here are the tests:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReisDiscountTest {

    @DisplayName("Reis discount ladder")
    @Description("""
            Applies to adult and senior single-ticket purchases in the app.
            Ticket count includes the current purchase ("first discount applies to ticket number five").
            Every 5 tickets in the last 30 days increases the discount by 5 percentage points.
            Maximum discount is 40%.
            Open: is the count based on calendar days or 24-hour rolling periods?
            """)
    @TableTest("""
            Scenario         | Tickets in last 30 days | Discount?
            Below threshold  | {1, 2, 3, 4}            | 0
            First tier       | {5, 6, 7, 8, 9}         | 5
            Second tier      | {10, 11, 12, 13, 14}    | 10
            Third tier       | {15, 16, 17, 18, 19}    | 15
            Fourth tier      | {20, 21, 22, 23, 24}    | 20
            Fifth tier       | {25, 26, 27, 28, 29}    | 25
            Sixth tier       | {30, 31, 32, 33, 34}    | 30
            Seventh tier     | {35, 36, 37, 38, 39}    | 35
            Maximum tier     | {40, 41, 100}           | 40
            """)
    void reisDiscountLadder(int ticketsInLast30Days, int discountPercent) {
        assertEquals(discountPercent, ReisDiscount.tierDiscount(ticketsInLast30Days));
    }

    @DisplayName("Discount by passenger type")
    @Description("""
            Adults and seniors are eligible for the Reis ladder discount based on travel frequency.
            Children always receive a flat 20% discount, regardless of how many trips they take.
            """)
    @TableTest("""
            Scenario                    | Passenger type | Tickets in last 30 days | Discount?
            Adult, below threshold      | ADULT          | 3                       | 0
            Adult, first discount       | ADULT          | 5                       | 5
            Adult, maximum discount     | ADULT          | 40                      | 40
            Senior, below threshold     | SENIOR         | 3                       | 0
            Senior, first discount      | SENIOR         | 5                       | 5
            Child, infrequent traveller | CHILD          | 1                       | 20
            Child, frequent traveller   | CHILD          | 40                      | 20
            """)
    void discountByPassengerType(PassengerType passengerType, int ticketsInLast30Days, int discountPercent) {
        assertEquals(discountPercent, ReisDiscount.calculate(passengerType, ticketsInLast30Days));
    }

    @DisplayName("30-day rolling window")
    @Description("""
            Single tickets count toward the discount only if they were purchased within the last 30 days,
            measured at the time of the current purchase.
            Open: is day 30 inclusive (purchased exactly 30 days ago counts) or exclusive?
            The row "At 30-day boundary" marks this as an open question — verify with the domain.
            """)
    @TableTest("""
            Scenario                  | Days ago | In window?
            Same day                  | 0        | yes
            Two weeks ago             | 14       | yes
            At 30-day boundary        | 30       | yes
            One day outside window    | 31       | no
            Well outside window       | 60       | no
            """)
    void rollingWindowBoundary(int daysAgo, boolean inWindow) {
        LocalDate purchaseDate = LocalDate.of(2026, 3, 29);
        LocalDate ticketDate = purchaseDate.minusDays(daysAgo);
        assertEquals(inWindow, ReisDiscount.isInWindow(ticketDate, purchaseDate));
    }

    @TypeConverter
    boolean parseYesNo(String value) {
        return "yes".equals(value);
    }
}
```

**Three separate tables, one concern each:**

1. **`reisDiscountLadder`** — the tier calculation in isolation. Value sets group each tier (e.g. `{5,6,7,8,9} → 5%`) so the tier structure is explicit, not a list of boundary-only rows. The `{40, 41, 100}` row documents that the cap holds for arbitrarily high counts.

2. **`discountByPassengerType`** — tests the routing: adults/seniors follow the ladder, children get 20% flat regardless. Representative rows from the ladder (0%, 5%, 40%) confirm the ladder is wired up correctly for ADULT and SENIOR; two child rows confirm the flat rate is invariant to trip count.

3. **`rollingWindowBoundary`** — tests which past purchases count toward the window. Day 30 is marked as an open question in `@Description` since the requirement says "last 30 days" without specifying whether the boundary is inclusive or exclusive — a common source of bugs worth surfacing before implementation.

The `@TypeConverter` on `parseYesNo` handles the `yes`/`no` cells in the rolling window table; JUnit's built-in conversion handles `int` and `PassengerType` (enum) in the other tables.