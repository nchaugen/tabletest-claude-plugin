All 16 rows parsed correctly (8 validation + 8 pricing) and fail only due to the intentionally unimplemented stub (`UnsupportedOperationException`) — exactly the expected state.

Summary:
- Added `org.tabletest:tabletest-junit:1.2.1` test dependency in `build.gradle`.
- Created `src/test/java/com/example/EventRegistrationServiceTest.java` with two `@TableTest` methods:
  - `shouldValidateRegistration` — 8 rows covering valid input (with/without optional fields), missing/blank name, and four structurally distinct invalid-email cases, asserting `accepted()`/`errorMessage()`.
  - `shouldApplyPricingAndDiscount` — 8 rows covering early-bird boundary (before/on/after cutoff), group-size boundary (4/5/10), and both-discounts-apply cases, asserting `discount()`/`price()`. Uses a `parseRegistrationTiming` `@TypeConverter` for readable `before/on/after cutoff` values, and a `@Description` documenting the fixed £100 base price, cutoff date, and group threshold since those aren't method parameters.
- `EventRegistrationService.register(...)` stays an unimplemented stub as instructed — tests compile and run, failing only on `UnsupportedOperationException`, ready for the real implementation next.