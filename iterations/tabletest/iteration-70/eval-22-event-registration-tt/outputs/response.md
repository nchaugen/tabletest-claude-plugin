Both tables are correctly formatted (already-aligned, exit 0 for a well-formed file with no changes needed — confirmed separately by the earlier successful compile/run). Everything is in place.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`, then wrote `src/test/java/com/example/EventRegistrationServiceTest.java` with two `@TableTest` methods against the existing `EventRegistrationService` stub:

- **`validatesRegistration`** — 6 rows covering: valid registration with/without optional fields, missing name, blank name (value set for `''`/`'   '`), missing email, and malformed email (value set of 4 formats). Registration date and group size are held fixed since validation doesn't depend on them.
- **`computesPriceAndDiscount`** — 6 rows covering: no discount, early-bird boundary (day before/on cutoff), group-size boundary (4 vs 5), and the no-stacking case where both apply and only the higher 20% wins. Cutoff date and group threshold are included as columns even though constant, per the "make thresholds visible" pattern.

Assumptions documented in the `@Description` blocks:
- `discount` is the monetary amount off the £100 base; `price` is what remains after it.
- Name is "required" means non-null and non-blank (whitespace-only rejected too).
- Both null and malformed-format email produce the same `"Email is invalid"` error; missing name produces `"Name is required"` — these exact strings are asserted, so the real implementation should match or the tests will need a one-line update.

Verified: `compileTestJava` succeeds, and `test` runs all 16 generated cases, each failing with the stub's `UnsupportedOperationException` — confirming the tables parse, convert types correctly, and wire up to the real method signature without implementing any logic.