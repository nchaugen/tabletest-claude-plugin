The non-ISO formats (`15/01/2024`, `24-01-15`) appear in the `Input` column, but since that column is `String`, JUnit won't try to convert them — no `@TypeConverter` needed. The expected `Date?` column uses ISO format (`2024-01-15`), which JUnit auto-converts to `LocalDate`.

The scenarios have two distinct assertion logics: `assertEquals` for the success/null cases, and `assertThrows` for the empty string case. Since the exception case is just one scenario, I'll use a regular `@Test` for it (the quality check requires 2+ rows for a `@TableTest`).

```java
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ParseDateTest {

    @TableTest("""
        Scenario      | Input      | Date?
        ISO format    | 2024-01-15 | 2024-01-15
        Slash format  | 15/01/2024 | 2024-01-15
        Short year    | 24-01-15   | 2024-01-15
        Null input    |            |
        """)
    void shouldParseDate(String input, LocalDate expected) {
        assertEquals(expected, parseDate(input));
    }

    @Test
    void shouldThrowForEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> parseDate(""));
    }
}
```

A few design notes:

- **Null input** — blank cell in both `Input` and `Date?` columns. `String` and `LocalDate` are both reference types, so both accept null cleanly.
- **Expected column** — uses ISO format (`2024-01-15`) for all three valid cases. Even though the inputs differ, they all parse to the same date, so the column is traceable.
- **Empty string** — kept as a `@Test` rather than a single-row `@TableTest`. If you add more invalid-input cases (e.g., letters-only, wrong separator), convert it to a `@TableTest` with a `Throws?` column at that point.