All 94 generated test cases compiled and ran, failing only with `UnsupportedOperationException` from the stubs — confirming the table syntax, value sets, and custom type converters (`PastPurchase`, `DiscountPercentage`) all parse and wire correctly.

## Summary

**Design:** Added `SingleTicketDiscountCalculator` (stub, all methods throw `UnsupportedOperationException`) with three methods, decomposed so each table drives one rule:

- `calculateDiscount(travelerCategory, purchaseTime, purchaseHistoryRepository)` — public entry point (wiring only, untested — not a rule)
- `determinePurchaseNumber(history, purchaseTime)` — package-private; counts this traveler's `SINGLE` tickets purchased in the trailing 30 days (inclusive), including the one being bought now
- `singleTicketDiscount(travelerCategory, purchaseNumber)` — package-private; the discount rule itself

**Two `@TableTest` tables** in `SingleTicketDiscountCalculatorTest`:

1. **Discount by category and purchase number** — one row per ladder tier (0% through the 40% cap, in 5% steps), `{ADULT, SENIOR}` value sets proving they're treated identically, and a `CHILD` row proving the flat 20% is indifferent to purchase count.
2. **Purchase number derivation** — covers empty history, the 30-day boundary (30d counts, 31d doesn't), ticket-type filtering (`WEEKLY`/`MONTHLY` excluded), and indifference to zone and to a past purchase's traveler category.

**Assumptions documented in `@Description`** (since the brief was ambiguous): the ladder recalculates fresh from the current rolling count each purchase with no tier "memory," and a past single ticket counts toward the ladder regardless of what traveler category it was bought under.

Also added the `tabletest-junit:1.2.2` dependency to `build.gradle`. Everything compiles and runs; implementing the real logic is the natural next step.