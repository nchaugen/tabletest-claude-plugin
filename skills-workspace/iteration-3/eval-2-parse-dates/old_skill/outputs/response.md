# TableTest for a Date Parsing Method

Here is a TableTest covering the cases you described.

## Basic Cases (successful parsing)

```java
@TableTest("""
    Scenario              | Input      | Result?
    ISO format            | 2024-01-15 | 2024-01-15
    Slash format          | 15/01/2024 | 2024-01-15
    Short year            | 24-01-15   | 2024-01-15
    Null input            | null       | null
    """)
void parsesDate(String scenario, String input, LocalDate expected) {
    LocalDate actual = parseDate("null".equals(input) ? null : input);
    assertEquals(expected, actual);
}
```

## Exception Case (empty string)

The empty string case throws an exception, which doesn't fit cleanly into the same table as the success cases. Handle it with a separate test:

```java
@Test
void throwsForEmptyString() {
    assertThrows(IllegalArgumentException.class, () -> parseDate(""));
}
```

Alternatively, if your TableTest library supports a `Throws?` column, you could express the exception case in the table. Check your library's documentation for that feature.

## Type Conversion Notes

For non-standard formats like `15/01/2024` and `24-01-15`, you will need the `parseDate` implementation to handle multiple format patterns — for example using `DateTimeFormatter` with `DateTimeFormatterBuilder` or a list of candidate formatters tried in order.

If your TableTest library supports custom type converters (sometimes via a `@TypeConverter` annotation or a converter registration mechanism), you can register a converter that parses `LocalDate` from these string representations. This avoids repeating conversion logic in every test method.

The `LocalDate` column (`Result?`) will typically need either:
- A `@TypeConverter` that knows how to parse ISO-format date strings into `LocalDate`, or
- A string representation that the library can map directly (ISO format `yyyy-MM-dd` is usually supported by default)

Without a registered converter, you may need to compare against string values or parse the expected value manually inside the test method.
