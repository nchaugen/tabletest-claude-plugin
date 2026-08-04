All 32 test cases parse and run correctly, failing only because the implementation is intentionally stubbed with `UnsupportedOperationException` — the expected state for a test-first workflow.

## Summary

I designed the Reis discount tests as **6 tables across 3 concerns**, decomposed so each table proves one rule:

**New production code** (stubs only, no logic):
- `DiscountCalculator` — public interface with `calculateDiscount(TravelerCategory, LocalDateTime, List<PastPurchase>)`
- `ReisDiscountCalculator` — implements it, plus two package-private helpers: `countRecentSingleTicketPurchases(...)` and `discountForRecentPurchaseCount(int)`

**Tests** in `ReisDiscountCalculatorTest`:
1. `routesTheDiscountByTravelerCategory` — children get a flat 20% regardless of history; adults/seniors route to the ladder
2. `computesTheDiscountFromTheRecentPurchaseCount` — the ladder itself (17 rows, every 5%-step boundary from both sides, plus the 40% cap enforced past the natural ladder)
3. `countsOnlySingleTicketPurchases` — weekly/monthly purchases excluded from the count
4. `countsOnlyPurchasesByTheSameTravelerCategory` — only purchases matching the new ticket's category count
5. `countsOnlyPurchasesWithinTheLast30Days` — the rolling window boundary (exactly 30 days still counts, 30 days + 1 second doesn't)
6. `countsPurchasesRegardlessOfZone` — zone never affects the count

**Assumptions I made explicit in `@Description`** (the spec didn't cover these, and the given interfaces have no traveler/account ID to disambiguate):
- The 30-day count only includes past purchases by the *same* traveler category (adult history doesn't count toward a senior's ladder, or vice versa)
- "Last 30 days" is a closed window — a purchase exactly 30 days before the purchase time still counts
- Zone isn't a parameter of `calculateDiscount` at all, since it never affects the outcome

Also added `tabletest-junit:1.2.2` to `build.gradle`. One hiccup along the way: nested datetime values inside the composite `PastPurchase` map cells (e.g. `2026-03-25T10:00:00`) needed quoting, since the colon in the time portion collides with the map's `key: value` syntax — fixed and reformatted with the skill's `format-table.sh`.