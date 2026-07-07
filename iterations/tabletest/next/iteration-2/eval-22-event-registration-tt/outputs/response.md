Delivered. Summary:

**Added** `build.gradle`: `testImplementation 'org.tabletest:tabletest-junit:1.2.1'`

**Created** `src/test/java/com/example/EventRegistrationServiceTest.java` with two `@TableTest` methods (decomposed by concern):

1. **`validatesRegistration`** — 10 rows covering: valid registration (with/without optional fields), missing/blank name, and 5 distinct invalid-email shapes (missing `@`, missing local part, missing domain, missing TLD, missing entirely) plus a combined name+email failure. Asserts `accepted()` and `errorMessage()`.
2. **`calculatesPriceAndDiscount`** — 9 rows covering the discount rule table: no discount, group-threshold boundary (4 vs 5), early-bird boundary (day-before/on/after the 2025-03-01 cutoff), and two rows proving no-stacking (early-bird wins over group when both qualify). Uses a `@TypeConverter` mapping descriptive timing labels (`"well before cutoff"`, `"day before cutoff"`, `"cutoff date"`, etc.) to `LocalDate`, per skill guidance to avoid raw ISO dates for cutoff comparisons.

**Assumptions documented in `@Description`:** name is validated before email (drives the error message when both are invalid); discount is treated as a currency amount off the £100 base, not a rate.

Verified with `gradle test`: all 19 generated cases compile and run, currently failing only with `UnsupportedOperationException` from the untouched stub — confirming the tables are structurally correct and ready to drive the real implementation.