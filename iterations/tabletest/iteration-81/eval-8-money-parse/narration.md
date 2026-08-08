# Narration — 8

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

⚠️ _API retry 1/10 after 579ms (unknown)_

_[~1,245 thinking tokens (21 deltas)]_

> (thinking) I've got JUnit Jupiter 5.11 or higher, so I need to add the tabletest-junit dependency. Let me verify the test directory exists first. For the parser tests, I'm setting up a table with three cases: valid input returning a Money object, null input returning null, and invalid inputs like empty strings, letters, or negative numbers throwing IllegalArgumentException.
> 
> The key question is whether null should be grouped with valid parsing or treated separately. Since null is a distinct behavior—returning null rather than throwing—it makes sense to split this into two tables: one for successful parsing of valid amounts, and another for rejection cases where invalid input throws an exception.
> 
> For the parsing table, I can use a blank cell in the "Parsed?" column to represent null output, which naturally aligns with how blank cells represent absent values in both input and output columns. This keeps the valid parsing scenarios together cleanly.
> 
> Now I'm setting up the rejection table with scenarios for empty strings, non-numeric input, and negative values that should all throw IllegalArgumentException. I'll need a custom type converter to transform string inputs into Money objects for the parameterized tests, though null inputs will bypass the converter since blank cells cascade to null automatically. The first test method validates successful parsing by comparing the parser output to the expected Money value, including the null case. The second test method checks that invalid inputs throw the appropriate exception.

**Edit ./build.gradle (edit)**

Now creating the test class with two tables: valid parsing (including null) and rejection.

**Write ./src/test/java/com/example/MoneyParserTest.java (49 lines)**

Good, the formatter already aligned the table. Now let's verify the tables parse correctly and check compilation.

Good — the formatter correctly detects misalignment (exit 1) and confirmed the actual table is valid (exit 0, not a silent parse failure). Now let's compile the project.

No wrapper, but system gradle is available.

Compiles cleanly. Now let's run the tests to confirm they fail correctly (red phase — stub throws `UnsupportedOperationException`, not yet the expected behavior).

6 tests found and correctly failing (red phase) since `MoneyParser.parse` is still an unimplemented stub. This confirms the tables compile, expand into the right number of cases (3 + 3 = 6), and exercise the parser.

## Summary

Added `src/test/java/com/example/MoneyParserTest.java` with two `@TableTest` tables (plus the `tabletest-junit` dependency in `build.gradle`):

1. **`parsesAmountsIntoMoney`** — valid inputs (`10.00`, `0.01`) parse to the matching `Money`, and a null input parses to `null` (blank cell = absent, consistent for both input and expected output).
2. **`rejectsInvalidAmounts`** — empty string, letters-only (`abc`), and negative (`-5.00`) all expect `IllegalArgumentException` via a `Throws?` column.

Split into two tables because striking the rejection rows from the first table still leaves a coherent rule ("parses valid input, including null"), per the skill's test for when accepted/rejected cases belong together vs. apart.

A `@TypeConverter` (`parseMoney`) builds `Money` from the cell text (`new Money(new BigDecimal(value))`), keeping construction out of the test body. All 6 generated cases currently fail against the untouched stub (`UnsupportedOperationException`) — confirming they compile, wire up correctly, and are red as expected before the parser is implemented.

**Assumption**: kept `MoneyParser.parse` exactly as the pre-existing stub (throws `UnsupportedOperationException`) since it already compiles against the tests — no implementation changes were made, per your instruction not to implement the parsing logic yet.

