All 5 tests are compiled, collected, and executing correctly. Here's a summary:

**Test class**: `DateParserTest.java` — two `@TableTest` methods following TableTest principles:

| Method | Scenario | Row |
|--------|----------|-----|
| `parsDate()` | ISO format (`2024-01-15`) | ✓ |
| `parsDate()` | Slash format (`15/01/2024`) | ✓ |
| `parsDate()` | Short year (`24-01-15`) | ✓ |
| `parsDate()` | Null input → null | ✓ |
| `rejectsEmptyString()` | Empty string → `IllegalArgumentException` | ✓ |

**Design decisions:**
- **Split concerns**: one table for parsing rules (valid dates), one for error rejection — per the "separate rules" guideline
- **`Throws?` column** for exception type rather than branching in the test body
- **Blank cell** for null input (per TableTest convention)
- **Single `@TypeConverter`** consolidating all date-format parsing into one method (`parseTableDate`) to avoid multiple converters sharing the same `(String)` signature
- **Stub unchanged**: `DateParser.parseDate()` still throws `UnsupportedOperationException`, so all tests fail with the expected error

Once you implement `DateParser.parseDate(...)`, these 5 tests will exercise each format and boundary condition.