Tests compile and run successfully. As expected for the pre-implementation stub state, 41 of 49 test cases fail (the stub logic returns default `false`/`0` values) while a handful pass trivially where the default happens to match the expected outcome. This is exactly the "red" state the tests-first workflow expects — the design is confirmed compile-correct and ready for implementation.

## Summary

Added to `com.example`:
- **`ReisDiscountCalculator`** (stub only, no logic implemented yet) with:
  - `countsTowardReisFrequency(TicketType, LocalDateTime purchasedAt, LocalDateTime purchaseTime)` — per-purchase eligibility (single tickets only, within trailing 30 days)
  - `countQualifyingTickets(List<PastPurchase>, LocalDateTime purchaseTime)` — aggregates eligible purchases from history
  - `discountForTicketCount(TravelerCategory, int ticketCount)` — the discount ladder (flat 20% for children; 5%-per-5-tickets ladder capped at 40% for adults/seniors)
  - `calculateDiscount(TravelerCategory, LocalDateTime purchaseTime)` — orchestrator wired to `PurchaseHistoryRepository` (thin glue, no dedicated table — its contract is fully covered by the three tables above)
- **`ReisDiscountCalculatorTest`** with three `@TableTest` methods covering those three concerns as separate tables (eligibility predicate, history aggregation, discount ladder), plus a `@TypeConverter` for building `PastPurchase` history entries from compact strings like `SINGLE 2026-01-20`.
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`.

**Assumptions documented in the tables' `@Description`s** (since the spec left them ambiguous):
- The 30-day window is inclusive of exactly 30 days before the purchase; 31 days before is excluded.
- The ticket count driving the ladder includes the ticket currently being purchased (so the 5th ticket itself gets the first 5% step).
- Zone doesn't factor into the discount at all — the calculator API has no zone parameter, so it structurally can't vary by zone.
- Traveler category and zone on historical purchases don't affect frequency counting, only ticket type and date do.

Next step is implementing the three stubbed methods to turn the red tests green — let me know if you'd like me to proceed with that now.