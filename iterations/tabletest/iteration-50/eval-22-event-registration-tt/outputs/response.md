## Summary

Added `src/test/java/com/example/EventRegistrationServiceTest.java` with two `@TableTest` methods, and registered the `tabletest-junit` dependency in `build.gradle`:

- **`validatesEmailAndName`** — 5 rows covering valid input, missing name, missing email, malformed email, and optional fields (dietary/accessibility) being null. Uses `Accepted?` and `Has Error Message?` boolean columns rather than hardcoded error text, since the spec doesn't define specific messages.
- **`appliesEarlyBirdAndGroupDiscount`** — 5 rows covering early-bird alone, the cutoff-date boundary, the group-size boundary (4 vs 5), and the non-stacking precedence case (both conditions true → early-bird's 20% wins over group's 15%). A `@TypeConverter` maps descriptive timing values (`before/on/after cutoff`) to `LocalDate`, and price/discount are compared via `BigDecimal.compareTo` to avoid scale mismatches.

Both concerns (validation vs. pricing) are separated per the skill's decomposition rules, each holding the other's inputs at one valid value. Ran `gradle test`: all 10 generated cases compiled and executed, failing only with the stub's `UnsupportedOperationException` as expected — confirming the tables and converter are wired correctly ahead of implementing the real logic. Cleaned up the local `build/` output afterward.