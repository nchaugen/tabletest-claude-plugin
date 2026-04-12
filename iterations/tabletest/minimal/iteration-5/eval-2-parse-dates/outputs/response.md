No existing project. I'll write the test class directly.

**Design decisions:**
- Two tables: one for valid/null inputs (returns `LocalDate` or null), one for invalid inputs (throws)
- Input column is `String` — no type converter needed
- Expected `LocalDate` values in ISO format — JUnit converts those natively
- Slash format (`15/01/2024`) and short year (`24-01-15`) inputs both parse to a `LocalDate` expressible in ISO format, so the expected column stays converter-free

```java
public class DateParserTest {

    @TableTest("""
        Scenario           | Input      | Result?
        ISO format         | 2024-01-15 | 2024-01-15
        Slash format       | 15/01/2024 | 2024-01-15
        Short year         | 24-01-15   | 2024-01-15
        Null input         |            |
        """)
    void parsesInput(String input, LocalDate result) {
        assertEquals(result, parseDate(input));
    }

    @TableTest("""
        Scenario      | Input | Throws?
        Empty string  | ''    | IllegalArgumentException
        """)
    void rejectsInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parseDate(input));
    }
}
```

**Key points:**

- `Result?` column uses ISO-format dates — JUnit converts them to `LocalDate` natively. No `@TypeConverter` needed even though the *inputs* are non-ISO, because the *expected values* are always ISO.
- Null input uses a blank cell; `LocalDate` is a reference type so JUnit passes `null`.
- Empty string uses `''` to distinguish from a blank/null cell.
- The two tables are split by concern: what the method returns vs. what it throws. This keeps assertion logic uniform within each method.
- The `throws_` parameter is `Class<? extends Exception>` — the `Throws?` column holds the exception type name so each row's expected outcome is visible in the table.