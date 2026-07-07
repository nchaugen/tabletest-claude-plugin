Wrote `src/test/java/com/example/MoneyParserTest.java` with two `@TableTest` methods covering the two concerns:

- **`shouldParseAmount`** — valid parsing plus the null-input edge case, using a `Money` `@TypeConverter` so the expected column reads as plain decimal values (blank cell → null `Money`, matching "return null for null input").
- **`shouldRejectInvalidInput`** — the three exception cases (empty string, letters-only, negative) with a `Throws?` column instead of hardcoding the exception class in the method body.

Also added `org.tabletest:tabletest-junit:1.2.1` to `build.gradle`. Ran `gradle test`: all 6 rows execute and fail with `UnsupportedOperationException` from the stub, confirming the tests are wired correctly and ready for the parsing logic to be implemented next. `MoneyParser` and `Money` were left untouched, as requested.