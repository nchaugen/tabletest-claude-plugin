All 5 tests compile and fail against the stub as expected (each fails because `parseDate` currently throws `UnsupportedOperationException` for every input) — that's the correct TDD starting state.

## Summary

- **`build.gradle`**: added `tabletest-junit:1.2.2` test dependency (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).
- **`src/test/java/com/example/DateParserTest.java`**: one `@TableTest` covering all five specified behaviors as rows of a single table (`Scenario | Input | Parsed? | Throws?`) — ISO, slash, and short-year formats; null input (blank → blank, returns null); empty string (rejects with `IllegalArgumentException`). A `thrownBy` helper wraps the parse-and-assert call so the method has no branching: it captures either "no exception" (value checked via inner `assertEquals`) or the thrown exception's class, and compares that uniformly against the `Throws?` column.
- **`DateParser.java`**: left untouched — its existing `UnsupportedOperationException` stub already lets the tests compile without implementing parsing logic.

**Assumption documented in `@Description`**: short-year input (`24-01-15`) resolves into the 2000s century (`24` → `2024`), since the spec didn't state a pivot year.

Compiled and ran via `gradle test`: build succeeds, all 5 cases execute and fail for the expected reason (unimplemented stub), not a compile or table-syntax error.