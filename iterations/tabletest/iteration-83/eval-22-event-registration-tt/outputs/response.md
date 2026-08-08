All formatted correctly and parses cleanly.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/EventRegistrationServiceTest.java` with four `@TableTest` tables against `EventRegistrationService.register(...)`, following the decomposition rules:

- **`rejectsAnInvalidEmailFormat`** — email-format validation, name held valid (5 rows: well-formed + 4 malformed shapes)
- **`requiresAName`** — name-required validation, email held valid (3 rows: provided, null, empty — distinct absent states)
- **`acceptsRegistrationsRegardlessOfOptionalFields`** — dietary/accessibility fields vary absent vs. present, both accepted (states the "regardless of" claim in data)
- **`computesPriceAndDiscountForAValidRegistration`** — early-bird/group discount pricing, with the cutoff date and group-size threshold as visible policy columns, straddling both boundaries and covering the no-stacking precedence case (early-bird's 20% beats group's 15% when both apply)

Documented assumptions in `@Description` blocks: assumed rejection messages ("Invalid email format", "Name is required"), that the early-bird cutoff is exclusive, and the £100 base price with the two discount rates.

`EventRegistrationService` remains the stub (`throw new UnsupportedOperationException`) as requested. Verified the whole suite compiles and all 15 generated test cases fail only with `UnsupportedOperationException` — confirming the tables, type conversions, and BigDecimal comparisons are structurally correct and ready for the real implementation.