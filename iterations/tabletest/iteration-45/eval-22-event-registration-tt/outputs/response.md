All 16 rows parsed and executed correctly, failing only with `UnsupportedOperationException` from the stub — exactly as expected since the registration logic isn't implemented yet. Table parsing, value sets, quoting, and the custom `@TypeConverter` all worked correctly.

Summary of what was delivered:

**`src/test/java/com/example/EventRegistrationServiceTest.java`** — two `@TableTest` methods:

1. **`validatesRequiredFields`** (10 cases) — name required (null/blank), email required and valid format (null/malformed), and demonstrates `dietaryRequirements`/`accessibilityNeeds` are nullable/optional. Registration date and group size are held constant (documented in `@Description`) since pricing isn't this table's concern.
2. **`appliesEarlyBirdAndGroupDiscounts`** (6 cases) — covers no discount, early-bird only, group-only, and the non-stacking precedence rule (higher discount wins when both apply), with boundary rows at the 2025-03-01 cutoff and the 5-person group threshold. A `@TypeConverter` maps descriptive timing values (`before cutoff`, `on cutoff`) to `LocalDate` per the skill's guidance on calendar dates.

**Assumptions documented in `@Description`:**
- Base price is £100, discount amounts are currency (e.g. 20.00), not percentages.
- Email validity requires an `@` and a domain with a dot.
- Rejected registrations aren't asserted on price/discount here — that's a separate concern.

Also added the `org.tabletest:tabletest-junit:1.2.2` test dependency to `build.gradle` (it was missing). Confirmed with `gradle compileTestJava` (succeeds) and `gradle test` (all 16 generated cases run and fail only on the stub's `UnsupportedOperationException`, as intended — the service itself was left unimplemented per your instructions).