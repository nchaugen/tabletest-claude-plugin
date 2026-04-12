Here's the TableTest. A few design decisions I worked through:

**Non-ISO formats in the `Input` column**: These are `String` values — no conversion needed. The `Result?` column uses ISO format (`2024-01-15`) which JUnit converts to `LocalDate` natively. No `@TypeConverter` required.

**Exception case**: Only one throwing case (empty string), so the quality check "2+ rows; use `@Test` for single cases" applies — a single-row `@TableTest` adds noise without value here.

**Null case**: Kept as a blank-cell row in the main parsing table.

```java
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateParserTest {

    @TableTest("""
        Scenario     | Input       | Result?
        ISO format   | 2024-01-15  | 2024-01-15
        Slash format | 15/01/2024  | 2024-01-15
        Short year   | 24-01-15    | 2024-01-15
        Null input   |             |
        """)
    void shouldParseDate(String input, LocalDate result) {
        assertEquals(result, parseDate(input));
    }

    @Test
    void shouldThrowForEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> parseDate(""));
    }
}
```

The `Result?` column is blank for null input — blank cells map to `null` for reference types, and `LocalDate` is a reference type so the parameter handles it correctly.

If you add more exception cases later (e.g., malformed strings like `"not-a-date"`), convert the `@Test` to a `@TableTest` with a `Throws?` column at that point.