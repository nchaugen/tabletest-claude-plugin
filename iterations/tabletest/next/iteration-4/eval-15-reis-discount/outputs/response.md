Good — everything parsed and ran correctly despite the minor column-alignment whitespace inconsistencies (TableTest trims cell values). Tests all compile and fail for the right reason (unimplemented logic), confirming the harness is wired correctly.

## Summary

Wrote three decomposed `@TableTest` classes covering the Reis discount feature, plus compiling stubs (all throwing `UnsupportedOperationException`) so the build is currently red, as intended for TDD:

**New production interfaces/classes** (`src/main/java/com/example/`):
- `SingleTicketHistory.countSingleTicketsInLast30Days(history, purchaseTime)` — derives the recent-travel count from raw purchase history
- `ReisDiscountLadder.discountForTravelCount(travelCount)` — maps that count to a discount tier
- `DiscountCalculator` interface + `ReisDiscountCalculator` impl — the top-level entry point combining traveler category, zone, history, and purchase time into a `DiscountPercentage`

**Tests** (`src/test/java/com/example/`): `ReisTravelCountTest`, `ReisDiscountLadderTest`, `DiscountCalculatorTest`, plus a shared `ReisTestConverters` for `PastPurchase`/`DiscountPercentage` parsing.

Key assumptions (documented in each table's `@Description`):
- The ticket being purchased counts toward its own tier, so the ladder is keyed on `pastCount + 1` — this is why "ticket #5" gets the first discount.
- The 30-day window is inclusive at exactly 30 days before purchase, exclusive just past it.
- Only `SINGLE` tickets count toward the Reis travel count; ticket-holder category/zone on past purchases doesn't matter.
- Discount is capped at 40% (tested with a row at count 45, where the uncapped ladder value would be 45%).

Also added the `org.tabletest:tabletest-junit:1.2.2` dependency to `build.gradle`. Ran `gradle test`: all 27 generated cases fail with `UnsupportedOperationException`, confirming the tables, converters, and value sets (zone irrelevance) all parse and wire correctly — ready for the actual implementation to turn them green.