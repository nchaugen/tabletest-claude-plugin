Tests are written and verified against the stub.

**What was created:**
- `src/test/java/com/example/DateParserTest.java` — two `@TableTest` methods:
  - `parsesRecognizedDateFormats`: ISO, slash, and short-year formats plus null input, checked against a `Parsed Date?` column (documented assumption in `@Description`: short years resolve to the 2000s).
  - `rejectsEmptyInput`: single-row table asserting `IllegalArgumentException` via a `Throws?` column, kept separate from the parsing table since "what it returns" and "what it rejects" are different concerns.
- `build.gradle` — added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'`.
- `DateParser.java` — stub extended only enough to make the null/empty rows meaningful (returns `null` for `null`, throws `IllegalArgumentException` for empty); the three format cases still throw `UnsupportedOperationException`, confirmed by running `gradle test` (3 expected failures, 2 passes, no compile errors).

Parsing logic for ISO/slash/short-year formats is intentionally left unimplemented, as requested.