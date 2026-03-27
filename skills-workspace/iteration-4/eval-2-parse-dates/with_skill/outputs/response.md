The non-ISO values (`15/01/2024`, `24-01-15`) are in the **input** column, which is typed `String` — JUnit passes them as-is. No `@TypeConverter` needed here. The expected `Date?` column uses ISO format, which JUnit converts automatically.

One design decision: the exception case has different assertion logic from the return-value cases, so it belongs in a separate method. Since there's only one exception scenario, a plain `@Test` is cleaner than a single-row `@TableTest`.

Here's the result:

```java
public class ParseDateTest {

    @TableTest("""
        Scenario     | Input       | Date?
        ISO format   | 2024-01-15  | 2024-01-15
        Slash format | 15/01/2024  | 2024-01-15
        Short year   | 24-01-15    | 2024-01-15
        Null input   |             |
        """)
    void parsesDateStrings(String input, LocalDate expectedDate) {
        assertThat(parser.parseDate(input)).isEqualTo(expectedDate);
    }

    @Test
    void throwsOnEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> parser.parseDate(""));
    }
}
```

A few things to note:

- **`Date?` column** — ISO values (`2024-01-15`) are auto-converted to `LocalDate` by JUnit. The blank cell for `Null input` becomes `null`, which maps to the `null` return you expect.
- **`Input` column** — typed `String`, so JUnit passes `15/01/2024` and `24-01-15` verbatim. No converter needed.
- **Exception case** — kept as `@Test` since there's a single scenario and the assertion logic (`assertThrows`) differs from the table rows. If you add more invalid-input cases later, promote it to a `@TableTest`.
- **Class must be `public`** — required for TableTest to discover converter methods (relevant if you add any later).