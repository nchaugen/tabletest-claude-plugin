This is complete and correctly red (TDD state).

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/MoneyParserTest.java` with two `@TableTest` tables, split per the "remove rejected rows" rule since valid-parsing/null-passthrough and rejection are separate concerns:

- **`parsesAValidAmountString`** — valid inputs (`10.00`, `0.01`) parse to the matching `Money`; the null-input case is expressed as a blank input row mapping to a blank (null) expected `Money`, since blank cells naturally mean "absent" for both a `String` and a domain-object parameter.
- **`rejectsAnInvalidAmountString`** — empty string, letters-only, and negative value all expect `java.lang.IllegalArgumentException` in a `Throws?` column (fully-qualified name, since `Class<?>` conversion requires it), using a `thrownBy` helper rather than branching per row.

A `@TypeConverter` (`parseMoney`) builds `Money` from the decimal string so no construction logic lives in the test bodies.

Ran `gradle test`: it compiles and all 6 cases fail against the existing stub (`UnsupportedOperationException` instead of returning `Money`/`null` or throwing `IllegalArgumentException`) — the expected red state before implementing `MoneyParser.parse`. No parsing logic was implemented, per the request.