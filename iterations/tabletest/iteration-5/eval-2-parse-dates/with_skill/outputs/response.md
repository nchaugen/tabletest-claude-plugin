Since the table's **input** column is `String` (passed through as-is) and the **expected** column only needs ISO format `LocalDate` values (built-in conversion handles those), no `@TypeConverter` is needed here.

The exception case (`empty string → IllegalArgumentException`) uses different assertion logic, so it belongs in a separate `@Test`.

Here's the TableTest:

```java
public class DateParserTest {

    @TableTest("""
        Scenario     | Input      | Parsed date?
        ISO format   | 2024-01-15 | 2024-01-15
        Slash format | 15/01/2024 | 2024-01-15
        Short year   | 24-01-15   | 2024-01-15
        Null input   |            |
        """)
    void parsesDate(String input, LocalDate expected) {
        assertEquals(expected, parseDate(input));
    }

    @Test
    void throwsForEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> parseDate(""));
    }
}
```

A few design notes:

- **Input column is `String`** — JUnit passes it through with no conversion, including `null` for the blank cell.
- **Expected column uses ISO format** — all three valid inputs happen to represent `2024-01-15`, and JUnit's built-in converter handles ISO `LocalDate` values. The blank cell in the null row → `null LocalDate`.
- **Exception case is a plain `@Test`** — `assertThrows` is structurally different from `assertEquals`, so mixing it into the table would require an `if` in the test method body, which the quality check flags as a sign to split.
- If you add more exception-triggering inputs later (e.g., malformed strings), convert `throwsForEmptyString` to its own `@TableTest` at that point.