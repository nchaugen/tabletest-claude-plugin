## Summary

Wrote three focused `@TableTest` classes for the Reis discount, each covering one rule, plus compilable stub implementations under `src/main/java/com/example`:

1. **`SingleTicketTripCounterTest`** (6 rows) — derives the trip's ordinal position from raw purchase history: 30-day window boundary (inclusive), SINGLE-only filtering, and combined filtering. Assumes history is already scoped to the purchasing traveler.
2. **`ReisDiscountLadderTest`** (8 rows) — pure arithmetic: 0% below tier 5, +5% per 5 trips, capped at 40% (boundary at 40 vs. 45 to prove clipping).
3. **`ReisDiscountCalculatorTest`** (4 rows, expanded to 12 via value sets) — the dispatch/combining rule: CHILD gets a flat 20% regardless of zone or travel frequency; ADULT/SENIOR route through the ladder; zone never matters.

Stub classes (`SingleTicketTripCounter`, `ReisDiscountLadder`, `ReisDiscountCalculator` implementing new `DiscountCalculator` interface) throw `UnsupportedOperationException` — no logic implemented yet, per your request. Added `tabletest-junit:1.2.2` to `build.gradle`.

Key assumptions (documented in each table's `@Description`):
- Purchase history passed in is already scoped to the purchasing traveler (no cross-traveler filtering).
- Only `SINGLE`-ticket past purchases count toward the trailing-window trip number.
- The 30-day window is inclusive of the instant exactly 30 days before purchase time.
- "Trip number" includes the ticket currently being purchased (so the 5th ticket = first discount, matching the spec).
- The "levels off" / "decreases if you travel less" behavior falls out naturally from recomputing the trailing count fresh each purchase — no extra state or table needed.

Ran `gradle test`: all 26 generated test cases compile and fail with `UnsupportedOperationException` as expected (red state, ready for implementation). One gotcha hit and fixed along the way: raw `LocalDateTime` values inside map cells (e.g. `[purchasedAt: 2026-07-05T08:00:00, ...]`) need quoting because the embedded colons collide with map `key: value` syntax.