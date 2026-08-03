## Summary

Added `src/test/java/com/example/ReisDiscountTest.java` with three `@TableTest` tables driving the Reis discount design, plus the minimal stub classes they need to compile:

- **`ReisTicketCounter.countSingleTicketsInLast30Days`** (5 rows) — the trailing-30-day derivation: empty history, the 29-vs-30-day boundary, WEEKLY/MONTHLY excluded, a mixed case.
- **`ReisDiscountLadder.discountForTicketCount`** (17 rows) — the pure count→discount ladder, both sides of every tier boundary (0% through 40%) plus a row past N=40 proving the 40% cap is enforced rather than emergent.
- **`ReisSingleTicketDiscountCalculator.calculateDiscount`** (4 rows × 3 zones) — the routing table: child flat 20% regardless of history, adult/senior wired through the ladder via history, zone varied as a value set on every row to prove it never affects the outcome.

New main-source files (all stubs, `throw new UnsupportedOperationException`, no logic): `SingleTicketDiscountCalculator` (interface), `ReisTicketCounter`, `ReisDiscountLadder`, `ReisSingleTicketDiscountCalculator`. Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`.

Documented assumptions live in each table's `@Description`:
- N (ticket count) includes the ticket currently being purchased.
- "Last 30 days" is exclusive at exactly 30 days ago.
- A past purchase's own traveler category/zone don't affect the count (only ticket type and timing do).

`gradle test` confirms all 34 generated cases compile and run, failing red with `UnsupportedOperationException` — ready for implementation to turn green.