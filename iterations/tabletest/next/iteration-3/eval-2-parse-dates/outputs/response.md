Tests are in place and verified against the stub (all 5 fail with `UnsupportedOperationException`, as expected before implementation).

**Files changed:**
- `build.gradle` — added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'` (JUnit Jupiter was already 6.0.3, which satisfies the ≥5.11 requirement)
- `src/test/java/com/example/DateParserTest.java` — new test class, two `@TableTest` methods split by concern:
  - `parsesSupportedDateFormats` — ISO, slash, and short-year inputs all resolving to `2024-01-15`, plus a null-input row (blank cell → blank/null) — all use the same `assertEquals` assertion.
  - `rejectsEmptyInput` — one row for `''` → `IllegalArgumentException`, kept separate since it uses `assertThrows` instead.

**Assumptions made** (stated since this is non-interactive):
- Short year format `24-01-15` is `YY-MM-DD` (2-digit year, month, day) — chose the same underlying date (`2024-01-15`) as the other rows so all three format rows are directly comparable at a glance.
- `DateParser.parseDate` stayed untouched (still throws `UnsupportedOperationException`) since it already compiles against the tests — no implementation logic was added, per your instruction.

`gradle test` confirms compilation and correct row generation (4 + 1 = 5 test cases); next step is implementing `parseDate` to make them pass.