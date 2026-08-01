All 26 generated test cases compile and fail with `UnsupportedOperationException` as expected — confirming the tables, converters, and stub API all wire up correctly ahead of any real implementation.

## What I built

**Production stubs** (`src/main/java/com/example`):
- `DiscountCalculator` (interface) + `ReisDiscountCalculator` — top-level entry point: `calculateDiscount(TravelerCategory, ZoneValidity, LocalDateTime purchaseTime, List<PastPurchase> history)`
- `RecentPurchaseCounter` — counts single-ticket purchases in the trailing 30-day window
- `DiscountLadder` — pure arithmetic: prior-ticket count → discount %

All three throw `UnsupportedOperationException` for now, per your instruction to write tests first.

**Three test tables**, decomposed by rule (not by output):
1. `RecentPurchaseCounterTest` — the 30-day window + single-ticket filter (boundary at exactly 30 days, non-single tickets excluded)
2. `DiscountLadderTest` — the arithmetic ladder (0% below ticket 5, +5% per 5 tickets, cap at 40%, tested at and past the cap)
3. `ReisDiscountCalculatorTest` — the dispatch rule (child flat 20% regardless of zone/history; adult/senior routed to the ladder regardless of zone)

A shared `PastPurchaseConverters` (test-only) gives history a compact notation like `5d;15d;29d` (days before a fixed reference purchase time) instead of verbose ISO datetimes, plus optional ticket-type/category overrides like `10d WEEKLY`.

## Assumptions documented in `@Description` (worth your review)
- The 30-day window is **inclusive** of a purchase made exactly 30 days before the purchase time.
- The ladder's "prior tickets" count **includes the ticket currently being purchased** (so 4 prior single tickets = this is ticket #5 = first 5% discount) — this is how I resolved "the first discount applies to ticket number five."
- `PurchaseHistoryRepository`'s list is assumed already scoped to the purchasing traveler.
- Added `build.gradle:15` dependency on `tabletest-junit:1.2.2`.

Since the actual discount logic wasn't implemented (as requested), running `gradle test` will show all 26 cases failing — that's expected until the stubs are filled in.