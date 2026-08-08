All 32 test cases compile and run, failing only with `UnsupportedOperationException` from the stub methods — confirming the tests are correctly wired and just waiting on implementation.

**What I built**, following spec-by-example decomposition into one rule per table:

1. **`ReisTicketNumberResolver.determineTicketNumber(history, purchaseTime, windowDays)`** — derives which "ticket number" (1st, 2nd, 5th…) this purchase is, from single tickets in the trailing window. Table covers: no history, same-day purchase, the 30-day boundary from both sides, non-SINGLE tickets excluded, zone irrelevance, and multiple tickets accumulating. (`ReisTicketNumberResolverTest.java`)

2. **`ReisLadder.discountForTicketNumber(ticketNumber, maxDiscountPercent)`** — the 5%-per-5-tickets ladder, all 9 tiers with both-sided boundaries via value sets, and the 40% cap kicking in past tier 8. (`ReisLadderTest.java`)

3. **`ReisDiscountCalculator.resolveDiscount(category, ticketNumberInWindow)`** — the category branch: adult/senior follow the ladder, child gets a flat 20% regardless of ticket number (proven via value set `{1, 5, 40}`). Plus one standalone `@Test` proving `calculateDiscount(...)` wires history → ticket number → ladder end-to-end. (`ReisDiscountCalculatorTest.java`)

**Assumptions I made (documented in `@Description`, worth confirming with you):**
- The 30-day window boundary is **inclusive** — a purchase exactly 30 days before is still counted.
- Only `TicketType.SINGLE` past purchases count toward the ladder; weekly/monthly history is ignored.
- Zone never affects counting or the discount, for both past and current purchases (per "regardless of zones").
- The traveler category recorded on *past* purchases doesn't affect counting — the resolver counts all qualifying singles regardless of who they were booked under.
- `windowDays` (30) and `maxDiscountPercent` (40) are passed as explicit parameters rather than hardcoded, so the tests can pin the boundary/cap directly as visible thresholds.

Stub implementations (`ReisTicketNumberResolver`, `ReisLadder`, `ReisDiscountCalculator`) just throw `UnsupportedOperationException` for now — ready for you to implement against.