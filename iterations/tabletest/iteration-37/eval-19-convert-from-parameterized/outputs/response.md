All 8 scenarios ran and passed. Summary:

- Added the missing `org.tabletest:tabletest-junit:1.2.1` test dependency to `build.gradle`.
- Converted `EmailValidatorTest` (in `src/test/java/com/example/EvailValidatorTest.java`) from `@ParameterizedTest`/`@CsvSource` to `@TableTest`, with a `Scenario` column and descriptive names (e.g. "Missing local part", "Missing TLD") kept as separate rows rather than grouped, since each tests a distinct structural rule.
- Verified with `gradle test` — build succeeded, all 8 rows passed.