I have added the required TableTests for `DateParser.parseDate(...)`.

### Changes Made:
1.  **Updated `build.gradle`**: Added the `org.tabletest:tabletest-junit:1.2.1` dependency to enable table-driven testing.
2.  **Created `src/test/java/com/example/DateParserTest.java`**:
    *   Implemented `shouldParseDates` using `@TableTest` to verify ISO, slash, and short-year formats, as well as handling of `null` inputs (represented by a blank cell).
    *   Implemented `shouldRejectEmptyInput` using `@TableTest` to verify that empty strings throw an `IllegalArgumentException`.
    *   Used standard ISO 8601 dates in the expectation columns to leverage TableTest's built-in `LocalDate` conversion.

The tests are designed according to the TableTest idiom: scenarios are named by condition, and expected outcomes are explicitly stated in the table rows.