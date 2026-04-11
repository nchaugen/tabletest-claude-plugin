Here's what I recommend.

**Table design:** The `input` column is typed as `String` — no `@TypeConverter` needed for it. The expected `LocalDate` column uses ISO format in the table, which JUnit converts automatically. The exception case has different assertion logic (it must `assertThrows`), so it belongs in a separate `@Test`.

```java
public class ParseDateTest {

    @TableTest("""
        Scenario      | Input      | Parsed date?
        ISO format    | 2024-01-15 | 2024-01-15
        Slash format  | 15/01/2024 | 2024-01-15
        Short year    | 24-01-15   | 2024-01-15
        Null input    |            |
        """)
    void shouldParseDate(String input, LocalDate expected) {
        assertEquals(expected, parser.parseDate(input));
    }

    @Test
    void shouldThrowForEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> parser.parseDate(""));
    }
}
```

A few things worth noting:

- **No `@TypeConverter` needed.** The slash and short-year strings are just raw `String` inputs passed to your method — the table doesn't need to parse them. Only the expected `LocalDate` column uses conversion, and that's ISO format (`2024-01-15`) which JUnit handles built-in.
- **Null input row:** blank `Input` cell → `null` String, blank `Parsed date?` cell → `null` LocalDate. The assertion `assertEquals(null, null)` passes.
- **Exception case split out:** the empty-string row would require `assertThrows` logic, which can't be expressed in the same assertion as `assertEquals`. One `@Test` is cleaner here. If you later add more "throws" scenarios (e.g. blank string, malformed input), you can promote it to its own `@TableTest`.
- The class is declared `public` — required if you ever add a `@TypeConverter` method later.