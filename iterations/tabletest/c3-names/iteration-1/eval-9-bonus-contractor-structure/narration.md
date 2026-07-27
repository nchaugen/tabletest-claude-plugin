# Narration — 9

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

I'll use the `tabletest` skill since this is a JUnit/Java TableTest task.

**Edit ./build.gradle (edit)**

**Write ./src/test/java/com/example/BonusCalculatorTest.java (30 lines)**

Now update the stub calculator so tests compile (without implementing bonus logic), and run a compile-only check.

Compiles cleanly against the stub. Let's confirm tests actually run (and fail as expected, since the stub throws `UnsupportedOperationException`).

All 6 test cases generated correctly (the value set expanded row 5 into 2 rows), and they fail as expected against the unimplemented stub — confirming the tests are wired correctly and ready for the bonus logic to be implemented against.

**Summary:**
- Added `tabletest-junit:1.2.2` test dependency to `build.gradle`.
- Wrote `src/test/java/com/example/BonusCalculatorTest.java` with one `@TableTest` covering all 5 rules (4 level×department combos + a `{SALES, ENGINEERING}` value set for the contractor "regardless of department" rule), documenting the assumption that the percentage is returned as a whole number (e.g. `15` not `0.15`) via `@Description`.
- Left `BonusCalculator` as the unimplemented stub — compiles fine, tests fail with `UnsupportedOperationException` as expected since logic isn't implemented yet.

