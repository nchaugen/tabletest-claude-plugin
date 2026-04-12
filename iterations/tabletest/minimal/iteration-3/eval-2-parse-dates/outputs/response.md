Two tables: one for parsing results (including the null case as a blank row), one for the exception case. The input is a `String` so no converter is needed; the `Result?` column uses ISO format which JUnit converts natively.

```java
import org.tabletest.junit.TableTest;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class DateParserTest {

    @TableTest("""
        Scenario     | Input       | Result?
        ISO format   | 2024-01-15  | 2024-01-15
        Slash format | 15/01/2024  | 2024-01-15
        Short year   | 24-01-15    | 2024-01-15
        Null input   |             |
        """)
    void parsesDateStrings(String input, LocalDate result) {
        assertEquals(result, parseDate(input));
    }

    @TableTest("""
        Scenario     | Input | Throws?
        Empty string | ''    | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parseDate(input));
    }
}
```

A few notes:

- **Two tables** because success rows and exception rows need different assertion logic (`assertEquals` vs `assertThrows`). Mixing them in one table would require an `if`/`switch` in the method body, which the skill flags as a quality violation.
- **Null input** stays in the success table as a blank cell — the `LocalDate` parameter receives `null`, and `assertEquals(null, null)` passes.
- **`Throws?` column** uses the fully-qualified class name (`java.lang.IllegalArgumentException`) because JUnit's built-in `Class<?>` converter requires it.
- **Single-row exception table** is fine here since it's part of a focused set of parsing tests, and additional invalid inputs can be added as rows later.
- **No `@TypeConverter` needed** — the input is already a `String`, and the expected `LocalDate` values are expressed in ISO format, which JUnit converts natively.