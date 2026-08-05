All 29 test cases (17 ladder tiers, 8 trip-count scenarios, 4 routing cases from the `{ADULT, SENIOR}` value-set expansion) compile and run, failing only with `UnsupportedOperationException` from the stub — confirming the tests are correctly wired to the not-yet-implemented logic.

## Summary

Added `build.gradle` dependency on `tabletest-junit`, then designed the Reis discount rule as three separate concerns, each with its own table in `src/test/java/com/example/ReisDiscountCalculatorTest.java`:

1. **`appliesTheReisLadderFromTripsInTheWindow`** — pure ladder lookup (trip count → discount %), 17 rows covering all 9 tiers and both sides of every boundary, plus the 40% cap.
2. **`countsTripsTowardTheRollingReisTotal`** — derives the trip count from raw purchase history: the 30-day window boundary (inclusive), ticket-type filter (SINGLE only), traveler-category filter (CHILD excluded, ADULT/SENIOR pooled together), and multi-entry accumulation.
3. **`resolvesWhichDiscountRuleAppliesByTravelerCategory`** — the routing rule: CHILD always gets a flat 20% regardless of history, ADULT/SENIOR use the ladder — proven with the *same* history producing different results per category.

Supporting production code (stubs only, no logic yet, per your instruction):
- `SingleTicketDiscountCalculator` interface
- `ReisDiscountCalculator` — implements it, plus package-private `countTripsInWindow` and `discountForTripCount` seams the tables target directly (avoids fabricating large purchase-history lists just to hit a target trip count)

**Assumptions documented in each table's `@Description`** (since the spec left them open):
- The 30-day window is inclusive of exactly 30 days ago.
- Trip count pools ADULT and SENIOR purchases together (so aging from adult to senior doesn't reset your pattern).
- ZoneValidity plays no role and is held constant.
- Ladder step (5%/5 trips) and cap (40%) are treated as fixed policy, not configurable parameters — so they're shown as tier values in the table rather than extra columns.

Next step would be implementing the three stub methods against these tables — let me know if you'd like me to proceed.