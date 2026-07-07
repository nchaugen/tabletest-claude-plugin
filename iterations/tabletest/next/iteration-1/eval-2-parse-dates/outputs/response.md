All 5 tests generated correctly and fail against the stub (as expected pre-implementation), confirming table wiring, parameter conversion, and discovery all work.

## Summary

Added the TableTest dependency and wrote `src/test/java/com/example/DateParserTest.java`:

- **`parsesDate`** — one `@TableTest` covering ISO, slash, and short-year formats plus null input, all asserted via `assertEquals` (null-input row uses a blank cell/blank result, consistent with the same assertion logic as the other rows).
- **`rejectsEmptyString`** — a plain `@Test` (not a table) since it's a single scenario with different assertion logic (`assertThrows` vs `assertEquals`).

**Assumption documented in `@Description`**: two-digit years (`24-01-15`) are interpreted as 2000s (→ 2024); behavior outside 2000–2099 is left open.

`DateParser` stub is untouched — still throws `UnsupportedOperationException`, so all 5 generated test cases correctly fail for now (verified via `gradle test`), confirming the harness compiles and wires up before implementation begins.