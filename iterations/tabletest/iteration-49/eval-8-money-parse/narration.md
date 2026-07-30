# Narration — 8

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

No existing test source. I'll add the TableTest dependency and write the test class.

**Edit ./build.gradle (edit)**

**Write ./src/test/java/com/example/MoneyParserTest.java (43 lines)**

Compiles cleanly. Let's confirm the tests run (they should fail against the stub, since parsing logic isn't implemented yet, but not error out on structure/conversion issues).

**Edit ./src/test/java/com/example/MoneyParserTest.java (edit)**

All 6 rows now wire up correctly and fail only because the stub throws `UnsupportedOperationException` — exactly the expected pre-implementation state. No structural, conversion, or discovery errors remain.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and created `src/test/java/com/example/MoneyParserTest.java` with two `@TableTest` methods:

- **`parsesAmount`** — valid inputs (`10.00`, `0.01`) and a null input, asserting the returned `Money` (or `null`). A `@TypeConverter` builds `Money` from the cell text; the blank cell for null input bypasses the converter as intended.
- **`rejectsInvalidInput`** — empty string, letters-only, and negative value, all expecting `java.lang.IllegalArgumentException` via a `Throws?` column (fully-qualified name required for JUnit's built-in `Class<?>` conversion).

`MoneyParser.parse` is left as the existing stub (throws `UnsupportedOperationException`), so it compiles but all 6 test cases correctly fail — parsing logic is intentionally not implemented yet.

