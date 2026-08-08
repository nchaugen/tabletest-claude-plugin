## Summary

Added `src/test/java/com/example/MoneyParserTest.java` with two `@TableTest` tables (plus the `tabletest-junit` dependency in `build.gradle`):

1. **`parsesAmountsIntoMoney`** — valid inputs (`10.00`, `0.01`) parse to the matching `Money`, and a null input parses to `null` (blank cell = absent, consistent for both input and expected output).
2. **`rejectsInvalidAmounts`** — empty string, letters-only (`abc`), and negative (`-5.00`) all expect `IllegalArgumentException` via a `Throws?` column.

Split into two tables because striking the rejection rows from the first table still leaves a coherent rule ("parses valid input, including null"), per the skill's test for when accepted/rejected cases belong together vs. apart.

A `@TypeConverter` (`parseMoney`) builds `Money` from the cell text (`new Money(new BigDecimal(value))`), keeping construction out of the test body. All 6 generated cases currently fail against the untouched stub (`UnsupportedOperationException`) — confirming they compile, wire up correctly, and are red as expected before the parser is implemented.

**Assumption**: kept `MoneyParser.parse` exactly as the pre-existing stub (throws `UnsupportedOperationException`) since it already compiles against the tests — no implementation changes were made, per your instruction not to implement the parsing logic yet.