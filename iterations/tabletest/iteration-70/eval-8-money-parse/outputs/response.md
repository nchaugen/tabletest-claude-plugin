This looks good. Summary of what was delivered:

**`build.gradle`** — added `org.tabletest:tabletest-junit:1.2.2` test dependency (JUnit Jupiter is already at 6.0.3, well above the 5.11 minimum).

**`src/test/java/com/example/MoneyParserTest.java`** — two `@TableTest` tables:
- `parsesMoneyFromValidInput` — covers valid parsing (`10.00`, `0.01`) plus null-passthrough (`null` in → `null` out), all as one concern since striking any row still leaves a coherent "successful/non-throwing" rule. A `@TypeConverter` builds `Money` directly so both cases share one assertion with no branching in the test body.
- `rejectsInvalidMoneyInput` — covers the three distinct invalid-input obligations (empty string, letters-only, negative) as separate rows with a `Throws?` column, since each represents a different invalid-input category, not a redundant restatement of the same boundary.

**`MoneyParser.java`** — left as the original stub (`throw new UnsupportedOperationException(...)`) so it compiles but stays unimplemented, per your instructions.

I compiled and ran the suite: all 6 test cases execute correctly and fail red with `UnsupportedOperationException`/mismatched-exception errors, confirming the tables, converter, and assertions are wired correctly and are just waiting on the real parsing implementation.

One assumption worth flagging: I treated null-input handling as part of the "parses valid input" table rather than a standalone case, since the tabletest skill's guidance says a lone null case belongs as a row in an existing related table rather than its own `@Test`.