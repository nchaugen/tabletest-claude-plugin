All expected values in the table match `min(40, 5 * floor(ticketNumber/5))`. The test suite is complete and compiles/runs correctly against stub implementations.

## Summary

I found the existing domain model (`PastPurchase`, `PurchaseHistoryRepository`, `TicketType`, `TravelerCategory`, `ZoneValidity`, `DiscountPercentage`) and added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`.

**Decomposed the Reis rule into 4 concerns**, each its own `@TableTest` in `ReisDiscountCalculatorTest.java`:

1. **`discountLadderByTicketNumber`** — the tier ladder itself: `min(40, 5 × ⌊ticketNumber/5⌋)`. One row per tier (10 rows), with a boundary row proving the 40% cap kicks in beyond the natural formula (ticket 45 would be 45% uncapped).
2. **`countsRecentSingleTicketsWithinThirtyDayWindow`** — filters purchase history to SINGLE tickets in the trailing 30 days, with boundary rows at exactly 30 days (counts) vs. 31 days (excluded), and rows proving WEEKLY/MONTHLY tickets never count.
3. **`dispatchesDiscountByTravelerCategory`** — CHILD always gets flat 20% regardless of history (value set); ADULT and SENIOR delegate identically to the ladder (value set), with one row proving the prior-count → ticket-number (+1) offset.
4. One plain `@Test` (`calculatesDiscountFromInjectedPurchaseHistory`) — a single wiring check that the repository-backed entry point composes the above correctly; not a table, since it's pure composition with no new rule.

**Key assumptions** (documented in each table's `@Description`):
- "Ticket number" = ordinal position among single tickets in the trailing 30-day window, *including* the ticket being purchased now — so ticket #5 is the first to get 5%.
- The 30-day window is inclusive of exactly 30 days ago, exclusive beyond that.
- Traveler category/zone don't affect history counting (spec says discount applies "regardless of zones"; the repository is already scoped to one customer).
- The main entry point takes no `TicketType` param since it's only invoked for single-ticket purchases.

Added `ReisDiscountCalculator` with stub methods (`UnsupportedOperationException`) so everything compiles. Ran `gradle test`: all 60 generated test cases execute and fail only on the stub exception — confirming table syntax, converters, and value sets are all correct. Logic implementation is intentionally left for a follow-up step.