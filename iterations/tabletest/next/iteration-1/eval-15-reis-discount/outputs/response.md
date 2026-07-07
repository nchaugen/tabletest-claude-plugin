Here's a summary of what I built for the Reis discount feature.

**Design decisions & assumptions (also documented in each table's `@Description`):**
- Decomposed into 3 concerns/classes since the requirements mix a category rule, a numeric ladder, and history-derived counting:
  - `ReisDiscountRule.calculateDiscount(TravelerCategory, ZoneValidity, int recentSingleTicketCount)` — children get a flat 20% (zone/count irrelevant); adults/seniors follow the ladder (5% per 5 tickets starting at ticket 5, capped at 40%). Zone is accepted but proven irrelevant via value sets.
  - `SingleTicketFrequency.countRecentSingleTickets(List<PastPurchase>, LocalDateTime, TravelerCategory)` — counts only `SINGLE` tickets in the trailing 30 days (inclusive of exactly 30 days ago). **Assumption flagged as open**: only tickets bought under the *same* traveler category count, since Reis is described as a personal discount.
  - `ReisDiscountCalculator.calculateDiscount(...)` — the top-level entry point wiring the two together; the ticket being purchased counts as +1 over the historical count (this is how "ticket number five" is reached).
- 3 `@TableTest` classes (44 test cases total): `ReisDiscountRuleTest` (category selection + full ladder), `SingleTicketFrequencyTest` (30-day window boundary, ticket-type filter, category filter), `ReisDiscountCalculatorTest` (thin end-to-end integration, 5 rows).
- Added a shared `PastPurchaseConverters` (`@TypeConverterSources`) with a compact domain notation for history rows, e.g. `10d SINGLE ADULT` (days before purchase, ticket type, category), anchored to a fixed purchase time so tests are deterministic.
- Added `tabletest-junit:1.2.1` to `build.gradle`.

**Verified:** ran `gradle test` (using the JDK 26 toolchain already on this machine, since the project targets Java 25) — everything compiles, all 44 generated cases fail identically with `UnsupportedOperationException` from the stub methods, confirming the tests are correctly wired and waiting on the real implementation.

Not done (by design, per the task): the actual discount logic in `ReisDiscountRule`, `SingleTicketFrequency`, and `ReisDiscountCalculator` — only stubs were added so the tests compile and run red.