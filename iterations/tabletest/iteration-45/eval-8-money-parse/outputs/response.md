All 6 rows are correctly discovered and fail as expected (red phase — parser throws `UnsupportedOperationException` since it's unimplemented). The table structure, scenario names, and both success/failure paths are exercised correctly.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/MoneyParserTest.java` with two `@TableTest` methods:

- **`parsesValidAmounts`** — one concern (successful parse / null passthrough), `Input | Money?` columns, using a `@TypeConverter` (`parseMoney`) so blank cells map to `null` Money and filled cells build a real `Money`.
- **`rejectsInvalidAmounts`** — the rejection concern, `Input | Throws?` columns for empty string, letters-only, and negative amount, each expecting `IllegalArgumentException`.

`MoneyParser.parse` stays as the existing stub (throws `UnsupportedOperationException`), so `compileTestJava` succeeds but `test` correctly fails all 6 generated cases — confirming the tests are wired up and ready for the real implementation.