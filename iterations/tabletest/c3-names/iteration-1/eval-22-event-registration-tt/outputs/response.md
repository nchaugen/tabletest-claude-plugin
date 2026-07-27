All 13 rows parsed correctly and generated the expected test cases, failing only with the intentional `UnsupportedOperationException` from the stub — confirming the table structure, value sets, and type conversion all work.

Summary of what was written to `src/test/java/com/example/EventRegistrationServiceTest.java`, decomposed by concern:

1. **Registration validation** (7 rows) — name/email required, dietary/accessibility optional-nullable, distinct error messages for missing name vs. invalid email, with documented assumptions about blank-name and null-email handling.
2. **Early-bird pricing** (2 rows) — boundary pair at the 2025-03-01 cutoff, using a descriptive `@TypeConverter` (`before cutoff`/`on cutoff`/`after cutoff`) instead of raw dates.
3. **Group pricing** (2 rows) — boundary pair at the 5-person threshold.
4. **Discount precedence** (1 row) — proves non-stacking: both discounts apply but the result is the early-bird rate (20.00/80.00), not the group rate or their sum.

Also added the `org.tabletest:tabletest-junit:1.2.2` test dependency to `build.gradle`. The service/`RegistrationResult` stub was left untouched (still throwing `UnsupportedOperationException`), so the tests compile and run but fail until the actual logic is implemented.