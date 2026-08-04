No warning on stderr, so the formatter genuinely ran and confirmed the table is already well-formed — this matches the successful gradle run above, so the table parses correctly (not a silent no-op).

Summary of what's in place:

**`build.gradle`** — added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'`.

**`src/test/java/com/example/DateParserTest.java`** — one `@TableTest` covering all five required behaviors as rows (ISO, slash `DD/MM/YYYY`, short-year `YY-MM-DD`, null→null, empty string→`IllegalArgumentException`). Rejection and null-handling are folded into the same table (per TableTest guidance: a lone null/empty-input case joins an existing table as a row rather than becoming a separate single-row table), using a non-branching `thrownBy(() -> assertEquals(...))` pattern so the method stays a single arrange-act-assert line with no per-row branching. Two assumptions are documented in the `@Description`: slash dates are `DD/MM/YYYY`, and two-digit years default to the 2000s century (with the pivot-year question flagged as open).

**`DateParser.java`** — left as the existing stub (`throws UnsupportedOperationException`), unchanged, since implementation wasn't requested.

Ran `gradle test`: it compiles and executes all 5 table rows; all fail as expected against the stub (red state), confirming the tests are wired correctly and ready to drive the implementation.