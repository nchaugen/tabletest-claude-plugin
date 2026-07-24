## Summary

I decomposed the Reis discount rule into three concerns, each with its own `@TableTest`, and added stub implementations (just `UnsupportedOperationException`, no logic) so everything compiles and runs:

1. **`SingleTicketFrequencyCounterTest`** — derives the trailing-30-day single-ticket count from raw purchase history (6 rows: empty history, non-single types excluded, multiple singles, the 30/31-day boundary, and a mixed case).
2. **`ReisDiscountLadderTest`** — the ladder rule itself: given a prior single-ticket count, what discount applies (12 rows, one per tier from 0% up to the 40% cap, including the boundary where the 5th ticket first gets a discount and the boundary where the cap kicks in).
3. **`ReisDiscountCalculatorTest`** — the combining/precedence table: child gets a flat 20% regardless of history or zone (proven against a 9-ticket history that would otherwise earn an adult a 10% ladder discount), adult/senior defer to the ladder regardless of zone.

Key assumptions, recorded in each table's `@Description`:
- The 30-day window is inclusive of the boundary day (a ticket bought exactly 30 days ago counts; 31 days ago doesn't).
- The trailing count is of *all* single tickets by the traveler, regardless of what category they were purchased under.
- Discount is recomputed fresh from the current trailing count on every purchase (not carried over from a prior discount) — this is what makes "levels off," "decreases if you travel less," and "increases if you travel more" fall out for free rather than needing separate logic.
- Ticket number = prior count + 1, so the discount first appears on the 5th ticket, matching the spec.

I verified the design against the real `tabletest-junit` library (added it to `build.gradle`) by running `gradle test`: all 30 generated cases parse correctly and fail with the stub's `UnsupportedOperationException`, confirming the table/type-converter design is sound before any real logic is written. One thing worth noting: I discovered mid-way that `:` inside a list element gets parsed as map syntax by TableTest, so the purchase-history fixture format uses `daysAgo-TicketType` (dash) instead of a colon.

Not done (by design, per test-first workflow): the actual discount logic in `ReisDiscountLadder`, `SingleTicketFrequencyCounter`, and `ReisDiscountCalculator` — those still throw `UnsupportedOperationException` pending your review of the test design.