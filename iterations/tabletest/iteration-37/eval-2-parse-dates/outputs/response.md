All 5 test cases (4 table rows + 1 exception test) compile and run correctly, failing with `UnsupportedOperationException` from the stub as expected — confirming the tests are correctly wired for TDD.

Wrote `src/test/java/com/example/DateParserTest.java`:
- `parsesSupportedDateFormats` — a `@TableTest` with 4 rows (ISO, slash, short-year, null input) since they share the same equality assertion
- `rejectsEmptyInput` — a plain `@Test` since it's a single scenario with different assertion logic (`assertThrows`)

Also added `testImplementation 'org.tabletest:tabletest-junit:1.2.1'` to `build.gradle`. `DateParser` stub is untouched — parsing logic is still not implemented, as requested.