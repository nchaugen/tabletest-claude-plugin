Summary of what I did:

- Added `tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter is already 6.0.3, which satisfies the ≥5.11 requirement).
- Wrote `src/test/java/com/example/MoneyParserTest.java` with two `@TableTest` methods, split by concern per the skill's guidance (a value-returning table shouldn't mix with a throwing table):
  - **`parsesAmountString`**: valid amounts (`10.00`, `0.01`) plus the null-input → null-output case, asserting equality against a `Money` built by a `@TypeConverter` (`parseMoney`).
  - **`rejectsInvalidAmountString`**: empty string, letters-only, and negative value, each expecting `IllegalArgumentException` via a `Throws?` column and `assertThrows`.
- Left `MoneyParser.parse` as the existing unconditional-throw stub (already compiles) and `Money` untouched, since implementing the logic wasn't in scope.
- Verified with `gradle compileTestJava` (clean compile) and `gradle test` — all 6 generated cases run and fail only due to the stub's `UnsupportedOperationException`, the expected red state before implementation.

One assumption worth flagging: I grouped the null-input case into the same table as valid parsing (both are "produces a value, doesn't throw"), rather than a third table, since it shares the same assertion and blank cells naturally express null-in/null-out.