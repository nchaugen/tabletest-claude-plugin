All 18 rows are discovered correctly and fail with `UnsupportedOperationException` as expected — confirming the tests are wired up correctly against the stub, which is exactly right for a test-first workflow (implementation isn't written yet).

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and created `src/test/java/com/example/EventRegistrationServiceTest.java` with two decomposed `@TableTest` methods:

1. **`validatesRegistration`** — 10 rows covering: valid registration, optional fields (dietary/accessibility) omitted, missing/empty name, missing/empty email, and four malformed-email shapes (no `@`, no domain, no local part, no TLD). Registration date/group size are fixed defaults since they don't affect validation.
2. **`appliesPricingAndDiscount`** — 8 rows (6 scenarios, 2 use value sets) covering: no discount, early-bird only, the cutoff-date boundary (on cutoff ≠ early-bird), the group-size boundary (4 vs 5+), and the non-stacking rule where early-bird (20%) wins over group (15%) when both apply.

Documented assumptions in `@Description`: error message text (`"Name is required"`, `"Email is required"`, `"Invalid email format"`) and that discount/price are monetary amounts, not percentages — these weren't specified and may need adjustment once you settle on the actual API/error contract.

Verified with `gradle compileTestJava` (compiles clean) and `gradle test` (all 18 generated cases run and fail on the stub's `UnsupportedOperationException`, confirming correct wiring). The stub `EventRegistrationService.register(...)` was left unimplemented as requested.