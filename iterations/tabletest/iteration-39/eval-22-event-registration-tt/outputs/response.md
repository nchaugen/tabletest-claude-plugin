All 19 test cases (11 validation + 8 pricing, including value-set expansion) parse and run correctly, failing only because the stub throws `UnsupportedOperationException` as expected — the table structure, scenario decomposition, and conversions all work. The tests are ready for the implementation phase.

Summary of what was written to `src/test/java/com/example/EventRegistrationServiceTest.java`:

- **`validatesRegistration`** — one table covering name/email validation (null, empty, blank name; null/empty email; four distinct malformed-email structures) plus two valid rows confirming optional fields don't block acceptance.
- **`calculatesPricingAndDiscount`** — one table covering the early-bird cutoff (before/on/after boundary), the group-size threshold (4 vs. {5, 20} as a tier value set), and the no-stacking rule (both discounts eligible → early-bird's 20% wins over group's 15%).
- A `@TypeConverter` maps descriptive `before cutoff`/`on cutoff`/`after cutoff` values to concrete `LocalDate`s relative to the 2025-03-01 cutoff, so the table reads without needing the reader to compare raw dates.
- Added `org.tabletest:tabletest-junit:1.2.1` to `build.gradle` (was missing).

Assumptions documented via `@Description` (since the spec didn't cover them): blank/whitespace-only names count as missing; null/empty email yields "Email is required" while malformed-but-present email yields "Invalid email format"; discount/price are modeled as `BigDecimal` currency amounts (not percentages).

No changes were made to `EventRegistrationService`'s stub — it still throws, which is why all 19 generated cases currently fail.