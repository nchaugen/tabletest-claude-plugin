This is complete. Summary of what was done:

**Design (test-first, per the tabletest skill's decomposition workflow):**
- Decomposed the Reis discount logic into 4 concerns, each its own `@TableTest`:
  1. `discountForTripCount` — the pure ladder rule (5% per 5 trips, capped at 40%), with explicit tier boundaries
  2. `countSingleTicketsInWindow` — the rolling 30-day counting rule (only `SINGLE` tickets, zone-independent, boundary at exactly 30 days)
  3. `discountForCategoryAndTripCount` — the category branch (flat 20% for `CHILD` vs. the ladder for `ADULT`/`SENIOR`, treated identically)
  4. `calculatesDiscountEndToEnd` — thin wiring smoke test through the real public API + a fake `PurchaseHistoryRepository`, also demonstrating the "no ratchet" behavior (discount rises/falls with recent frequency)

**New code added** (stubs only, no logic implemented — TDD red phase):
- `SingleTicketDiscountCalculator` interface
- `ReisDiscountCalculator` implementing it, all methods throwing `UnsupportedOperationException`

**Assumptions documented in `@Description` blocks** (since this is non-interactive and the spec had gaps):
- Discount = `floor(N/5)*5%` capped at 40%, where N = single tickets in the last 30 days **including** the ticket currently being purchased
- 30-day window is inclusive of the exact boundary instant
- Only `SINGLE` ticket type counts; `WEEKLY`/`MONTHLY` history is ignored; zone never affects the result

Verified all 45 generated test cases compile and fail only with the expected `UnsupportedOperationException` (no table-syntax or type-conversion errors) via `gradle test`. Also added the `org.tabletest:tabletest-junit:1.2.1` dependency to `build.gradle`.