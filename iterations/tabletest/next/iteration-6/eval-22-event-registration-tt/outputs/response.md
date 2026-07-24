All 20 scenarios generated and parsed correctly (value sets, `LocalDate` converter, blank/empty/whitespace name variants) — every failure is the stub's `UnsupportedOperationException`, confirming the tests are correctly red and waiting on the real implementation.

Summary of what was added:
- `build.gradle`: added `org.tabletest:tabletest-junit:1.2.2` test dependency.
- `src/test/java/com/example/EventRegistrationServiceTest.java` with two `@TableTest` methods:
  - `shouldValidateRegistration` — name required (null/empty/whitespace), email format (missing `@`, missing local part, missing domain, no TLD, missing entirely), and optional dietary/accessibility fields (null and populated), asserting `accepted` + `errorMessage`.
  - `shouldCalculateDiscountAndPrice` — early-bird cutoff (2025-03-01, boundary tested at "just before"/"on"), group discount tier (`{1,2,3,4}` vs `{5,6,20}` via value sets), and the no-stacking precedence rule (both conditions met → 20% wins, not 35%). Uses a `@TypeConverter` for descriptive `"just before cutoff"/"on cutoff"/"after cutoff"` dates.

Assumptions documented in `@Description` blocks (since the spec didn't pin these down): `discount`/`price` are absolute currency amounts, not percentages; missing email vs. malformed email get distinct error messages ("Email is required" vs "Invalid email format"); name blank/whitespace also counts as missing.