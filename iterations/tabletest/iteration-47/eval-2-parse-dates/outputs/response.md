**What was done:**
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).
- Left `DateParser.parseDate` as its existing stub (throws `UnsupportedOperationException`) — unchanged, per instructions not to implement parsing yet.
- Wrote `src/test/java/com/example/DateParserTest.java` with two tables:
  - **`parsesSupportedDateFormats`** — ISO, slash, and short-year formats plus the null-passes-through case, all sharing a `Parsed?` (`LocalDate`) column.
  - **`rejectsEmptyInput`** — the empty-string case, kept in its own table with a `Throws?` column rather than merged with the value table (mixing a `Result?` and `Throws?` column in one table risks a value-assertion failure being silently swallowed as a mismatched exception type).
- Assumptions recorded in `@Description`: slash format is day/month/year (inferred from `15/01/2024`, since 15 can't be a month), and the two-digit year is interpreted as `2000 + YY`.

**Verified:** `gradle test` compiles cleanly and all 5 generated test cases fail against the stub as expected (proves the table/converter wiring is correct, not a false pass). Once real parsing logic is implemented, these should go green without any test changes.