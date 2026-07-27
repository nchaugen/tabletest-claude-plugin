All 49 generated test cases compile, parse, and run — failing only with `UnsupportedOperationException` from the stub methods, confirming the table design and type converters are correct (27 ladder tests, 6 derivation tests, 15 category/zone tests, 1 wiring test).

## Summary

Added to `/private/var/folders/.../eval-15-work`:

- **`build.gradle`**: added the `org.tabletest:tabletest-junit:1.2.2` dependency (was missing).
- **`src/main/java/com/example/ReisDiscountCalculator.java`** (new stub): four methods, each throwing `UnsupportedOperationException` for now:
  - `recentSingleTicketCount(history, purchaseTime)` — derives the trailing-30-day single-ticket count
  - `ladderDiscount(count)` — the accumulation ladder (adult/senior only)
  - `discountFor(category, zone, count)` — branches child (flat) vs adult/senior (ladder), zone always ignored
  - `discountFor(category, zone, history, purchaseTime)` — orchestrates the two above for real callers
- **`src/test/java/com/example/ReisDiscountCalculatorTest.java`**: three `@TableTest` methods, each isolating one rule, plus one plain `@Test` for wiring:
  1. **`computesLadderDiscount`** — pure tier ladder (0/5/10.../40%), one row per tier as a value set spanning both boundaries, capped tier sampled well past 40.
  2. **`countsRecentSingleTickets`** — derivation from raw history: 30-day window boundary (30 days in, 31 days out) and ticket-type filtering (only `SINGLE` counts), with a `@TypeConverter` for `PastPurchase` from compact tokens like `2024-05-31T12:00:00@SINGLE`.
  3. **`selectsDiscountRuleByCategory`** — child flat 20% (ticket count and zone both value sets, showing irrelevance) vs adult/senior ladder delegation (merged into one row via `{ADULT, SENIOR}` since they behave identically), zone always a value set to prove it never matters.
  4. **`combinesRecentTicketCountAndCategoryIntoAFinalDiscount`** — one plain `@Test` proving the two pieces wire together correctly (pure delegation, not worth a full table).

Assumptions documented in the tables' `@Description`: the 30-day window's lower boundary is inclusive; the recent-ticket count includes the ticket currently being purchased (so the 5th ticket is the first to get a discount); "levels off / drops with less travel" falls out automatically from recomputing the count on each purchase, so it isn't a separate rule needing its own table.

Logic itself is intentionally unimplemented — next step is making these 49 cases pass.