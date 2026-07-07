25 tests parsed and ran correctly, all failing with `UnsupportedOperationException` as expected (red, since the stub isn't implemented yet). Table parsing, type conversion, and value-set expansion all worked correctly.

## Summary

Added `org.tabletest:tabletest-junit:1.2.1` to `build.gradle` and wrote `src/test/java/com/example/EventRegistrationServiceTest.java` with 4 `@TableTest` methods (25 scenarios total), decomposed by concern:

1. **`shouldValidateRegistration`** — name/email validation, optional dietary/accessibility fields (11 rows: valid cases, missing/blank name, missing/malformed email variants)
2. **`shouldApplyEarlyBirdDiscount`** — cutoff boundary behavior (5 rows, exclusive cutoff confirmed)
3. **`shouldApplyGroupDiscount`** — group-size threshold behavior (4 rows, boundary at 5)
4. **`shouldResolveDiscountWhenBothApply`** — precedence when both discounts qualify, early-bird (20%) wins over group (15%), using a value set for group size since the result doesn't depend on it once early-bird applies

Assumptions documented via `@Description` on each table:
- Rejected registrations use error messages `"Name is required"` and `"Invalid email address"` (not specified in the prompt)
- `discount`/`price` on `RegistrationResult` are currency amounts (£), not percentages
- Early-bird cutoff (2025-03-01) is exclusive — same-day registrations don't qualify

The service stub was left untouched (still throws `UnsupportedOperationException`); all 25 tests compile and currently fail against it, confirming red status ahead of implementation.