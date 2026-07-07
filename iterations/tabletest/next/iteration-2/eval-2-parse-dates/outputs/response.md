Confirmed. Everything compiles, and the test results are exactly as intended:

- **Null input** and **empty-string throws** rows pass (the stub already handles these two contract points).
- **ISO / slash / short-year** rows fail with `UnsupportedOperationException`, since the actual parsing logic is intentionally not implemented yet.

## Summary

- `build.gradle`: added `testImplementation 'org.tabletest:tabletest-junit:1.2.1'`.
- `src/test/java/com/example/DateParserTest.java`: two `@TableTest` methods:
  - `parsesSupportedDateFormats` — decision table for ISO, slash, and short-year formats plus the null-input case, all sharing one assertion.
  - `rejectsEmptyInput` — single-row exception table (`Throws?` column) for the empty-string case.
- `src/main/java/com/example/DateParser.java`: extended the stub only enough to satisfy null/empty handling; the three real formats still throw `UnsupportedOperationException` as requested.

**Assumption documented in `@Description`**: short year `yy-MM-dd` expands into the 2000s (`24-01-15` → `2024-01-15`); handling of years outside the 2000s is flagged as an open question.