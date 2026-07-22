Compilation and table generation are confirmed working. Summary of what was done:

- **`build.gradle`**: added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'` (JUnit Jupiter is already 6.0.3, well above the 5.11 minimum).
- **`src/test/java/com/example/DateParserTest.java`** (new): one `@TableTest` covering the full `parseDate` contract — ISO, slash, and short-year formats all resolving to the same date (`2024-01-15`) for easy cross-row comparison, plus the null-input and empty-string edge cases as rows in the same table (per the exception-column convention) rather than separate `@Test` methods. A `Throws?` column drives an `assertThrows` vs `assertEquals` helper.
- **`DateParser.java`**: left untouched — its existing `UnsupportedOperationException` stub still compiles against the tests and produces the expected red failures.

Assumptions documented in the table's `@Description` (since the formats are ambiguous): slash format is `day/month/year`, and short-year format is `yy-MM-dd` with `20yy` as the century.

Ran `gradle test`: it compiles cleanly and all 5 generated cases fail with the stub's `UnsupportedOperationException`/resulting `AssertionFailedError`, confirming red-phase TDD is correctly wired before any parsing logic is written.