All done. Here's a summary of what was written:

**Test file**: `src/test/java/com/example/MoneyParserTest.java` with 2 `@TableTest` methods (6 total test rows):

| Table | Purpose | Rows |
|---|---|---|
| `parse_returnsMoneyForValidInput` | Valid inputs return Money; null returns null | 3 (`10.00`, `0.01`, null) |
| `parse_throwsForInvalidInput` | Invalid inputs throw `IllegalArgumentException` | 3 (empty, letters, negative) |

**Changes**:
- Added `org.tabletest:tabletest-junit:1.2.1` to `build.gradle` dependencies
- A `@TypeConverter` handles `String → BigDecimal` conversion for the `Expected?` column
- First table uses a boolean `Expected?` column (`true`/`false`) to distinguish Money-return from null-return
- Second table uses concrete input values and `assertThrows` against `IllegalArgumentException`

All 6 tests fail as expected — the stub throws `UnsupportedOperationException`, which is correct behavior for an unimplemented parser. The test suite is ready to green once `MoneyParser.parse(...)` is implemented.