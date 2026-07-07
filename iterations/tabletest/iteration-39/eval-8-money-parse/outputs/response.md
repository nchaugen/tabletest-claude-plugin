All 6 rows fail exactly as expected — the stub throws `UnsupportedOperationException` since parsing logic isn't implemented yet. This confirms the table structure and type conversions are wired up correctly; the tests will pass once the parser is implemented.

Summary of what was delivered:

- **`build.gradle`**: added `org.tabletest:tabletest-junit:1.2.1` test dependency (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).
- **`src/test/java/com/example/MoneyParserTest.java`**: two `@TableTest` methods:
  - `parsesInputIntoMoney` — valid parsing plus the null-input case as a blank-cell row (per the skill's guidance to keep null cases in the main table, not a separate `@Test`). Uses a `@TypeConverter` (`parseExpectedMoney`) to turn the expected column into a `Money`, so the test method itself stays a single assertion with no conditionals.
  - `rejectsInvalidInput` — empty string, letters-only, and negative-value cases, with a `Throws?` column (`Class<? extends Exception>`) rather than hardcoding the exception in the method body.
- Left `MoneyParser.parse` as the existing stub (throws `UnsupportedOperationException`) so the tests compile but intentionally still fail, since implementing the parsing logic was out of scope.

Assumption stated for the record: negative-value rejection is tested with `-5.00` as given in the requirements; the parser's actual validation logic (empty/format/sign checks) is still to be implemented next.