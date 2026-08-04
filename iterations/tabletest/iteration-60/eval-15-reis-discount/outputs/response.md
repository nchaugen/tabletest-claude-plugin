Everything compiles and expands correctly: all 30 rows (6 counter cases + 24 policy cases from the value-set expansions) run and fail with `UnsupportedOperationException` exactly as expected from unimplemented stubs — confirming the table wiring, converters, and cartesian-product expansion all work.

## Summary

Added, against the existing `PastPurchase`/`TravelerCategory`/`TicketType`/`ZoneValidity` model:

**Main (stub interfaces + implementations, no logic yet):**
- `RecentSingleTicketCounter` — counts single tickets in the trailing 30 days, inclusive of the ticket being purchased
- `ReisDiscountPolicy` — pure `(category, count) → discount%` lookup: flat 20% for children, tiered ladder capped at 40% for adults/seniors
- `SingleTicketDiscountCalculator` — the real entry point wiring the two together (no dedicated test — it's pure composition, not a new rule)

**Tests (`@TableTest`, 30 cases total):**
- `RecentSingleTicketCounterTest` — the 30-day window boundary (30 days ago counts, 31 doesn't), excludes non-SINGLE ticket types, and proves category/zone of past purchases don't affect the count
- `ReisDiscountPolicyTest` — two tables: flat 20% for children regardless of count, and the adult/senior ladder (0%→5%→10%→...→40% cap, both from the formula landing there naturally and from clamping)

Key assumptions are recorded in each table's `@Description` — most notably that the count is inclusive of the current purchase (so ticket #5 gets the first bump) and that the trailing-30-day window alone explains the ramp-up/leveling-off/decrease behavior without needing separate "first 30 days" logic.

Run `gradle test` to see the stub failures; implementing the three classes is the next step whenever you're ready.