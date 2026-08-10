All 38 generated test cases compiled and ran, failing only with the expected `UnsupportedOperationException` from the stub — confirming the tables parse correctly and every row is reachable. This is the correct TDD "red" state.

## Summary

Added to the existing project (`com.example` package):

**`src/main/java/com/example/ReisDiscountCalculator.java`** (stub, unimplemented):
- `calculateDiscount(TravelerCategory, ZoneValidity, LocalDateTime, List<PastPurchase>)` — the public entry point
- `ticketCountIncludingThisPurchase(List<PastPurchase>, LocalDateTime)` — counts eligible single tickets in the trailing 30-day window, including the ticket being bought now
- `discountForTicketCount(int)` — the tier/cap lookup

**`src/test/java/com/example/ReisDiscountCalculatorTest.java`** — four `@TableTest` tables, each isolating one rule:
1. **`countsTicketsPurchasedInTheTrailing30DayWindow`** — derives the eligible ticket count from raw history: filters out non-SINGLE tickets and CHILD tickets, treats ADULT/SENIOR as one personal ladder, and straddles the 30-day boundary (day 30 in, day 31 out).
2. **`looksUpTheDiscountForTicketCount`** — the 5%-per-5-tickets ladder, one row per tier (value sets spanning each tier's edges) plus the 40% cap, including a row showing the cap actually engages beyond the natural formula value.
3. **`appliesTheFlatDiscountForChildren`** — flat 20%, proven independent of travel history (even a history that would push an adult to a higher tier).
4. **`calculatesTheDiscountForASinglePurchase`** — thin wiring table proving category-based routing (child vs. adult/senior) and confirming the ticket's zone never affects the result.

`build.gradle` now pulls in `tabletest-junit:1.2.2`.

**Assumptions documented in `@Description`** (flagged as open questions, not silently baked in):
- ADULT and SENIOR purchases share one combined personal ladder rather than being tracked separately.
- The 30-day window is inclusive of the boundary day.
- Zone never affects eligibility counting or the discount — tested explicitly at the top level.

Next step is implementing the three methods to turn these red tests green — say the word when you want that done.