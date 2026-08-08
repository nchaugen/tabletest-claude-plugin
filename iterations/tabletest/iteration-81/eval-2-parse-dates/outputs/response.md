## Summary

Wrote `src/test/java/com/example/DateParserTest.java` with two `@TableTest` tables:

1. **`parsesSupportedDateFormats`** — one row per format (ISO, slash, short year), all producing `2024-01-15`.
2. **`handlesAbsentInput`** — null input (blank cell → returns `null`) and empty string (throws `IllegalArgumentException`), using a `ParseResult` helper record so no row branches on assertion style.

Added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'` to `build.gradle` (JUnit Jupiter was already 6.0.3, satisfying the ≥5.11 requirement).

**Assumptions** (no `@Description` needed since not implementation-revealing, but noting here): slash format is day/month/year (`15/01/2024` → 2024-01-15), and short year `24-01-15` is `yy-MM-dd` interpreted in the 2000s.

Verified with `gradle test`: all 5 generated cases compile and run, failing against the existing `UnsupportedOperationException` stub — exactly the red state expected before implementation. Left `DateParser.parseDate` untouched, as instructed.