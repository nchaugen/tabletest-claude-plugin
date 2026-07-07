Summary of what I did:

**Cleaned up stray stubs**: `EventRegistration.java` and `RegistrationService.java` were empty, unreferenced classes that just extended `EventRegistrationService` — removed them since they're not part of the described API.

**Added the TableTest dependency** to `build.gradle` (`org.tabletest:tabletest-junit:1.2.1`).

**Wrote `src/test/java/com/example/EventRegistrationServiceTest.java`** with two `@TableTest` methods, decomposed by concern:

1. `validatesRegistration` — 10 rows covering: valid registration (with and without optional fields), missing/blank name, missing email, and four malformed-email shapes (no `@`, no domain, no local part, no TLD), plus one case where both name and email are invalid.
2. `appliesPricingAndDiscountRules` — 9 rows covering the early-bird cutoff boundary (well before / just before / on / just after 2025-03-01), the group-discount threshold boundary (4 / 5 / 20), and the no-stacking rule (both conditions met → the higher 20% early-bird discount wins, not 15%+20%).

**Assumptions documented in `@Description`** (since the spec didn't pin these down):
- Discount is a monetary amount (not a percentage) and price is the final charged amount; base price £100.
- Distinct error messages: "Name is required", "Email is required", "Invalid email format".
- When both name and email are invalid, the name error is reported first.
- Early-bird cutoff and group threshold are compared exactly at the boundary (cutoff date itself does *not* get early-bird; group size exactly 5 *does* get the group discount).

Verified the whole thing compiles (`gradle compileTestJava` → BUILD SUCCESSFUL) and ran the suite — all 19 generated test cases parse/convert correctly and fail only with the stub's `UnsupportedOperationException`, confirming the tests are correctly wired ahead of implementation.