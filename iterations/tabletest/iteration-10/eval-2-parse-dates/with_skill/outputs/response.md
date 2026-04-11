The test cases split into two groups with different assertion logic — happy path + null returns use `assertEquals`, while the exception case uses `assertThrows`. These belong in separate methods.

For the single exception case, the skill requires `@Test` for single-row scenarios.

The non-ISO formats (`15/01/2024`, `24-01-15`) are String inputs to the method — no type converter needed. The Result? column uses ISO format, which JUnit converts to `LocalDate` automatically.

```java
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateParserTest {

    @TableTest("""
        Scenario     | Input      | Result?
        ISO format   | 2024-01-15 | 2024-01-15
        Slash format | 15/01/2024 | 2024-01-15
        Short year   | 24-01-15   | 2024-01-15
        Null input   |            |
        """)
    void shouldParseDate(String input, LocalDate result) {
        assertEquals(result, parseDate(input));
    }

    @Test
    void shouldThrowForEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> parseDate(""));
    }
}
```

A few things to note:

- **Blank input cell → `null` String**: JUnit passes `null` to `parseDate`, which your method returns `null` for — matched by the blank Result? cell.
- **Result? column is ISO only**: `2024-01-15` is ISO 8601 and converts to `LocalDate` automatically. The slash and short-year rows both expect the same canonical date.
- **No `@TypeConverter` needed**: The non-ISO strings (`15/01/2024`, `24-01-15`) live in the `String input` column — they're just raw strings handed to your method, not values JUnit needs to parse as `LocalDate`.
- **Dependency** (if not already present): `org.tabletest:tabletest-junit` (check Maven Central for the current version).