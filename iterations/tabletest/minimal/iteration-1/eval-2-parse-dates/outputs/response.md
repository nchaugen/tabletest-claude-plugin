No project files found, so I'll write the test class directly. Let me work through the design.

**Concern analysis:**
- Successful parsing (ISO, slash, short year, null → null) — uniform `assertEquals` assertion
- Rejection (empty string → exception) — `assertThrows` assertion

These need separate tables since the assertion logic differs.

**Type conversion:**
- `Input` column → `String`: no converter needed
- `Date?` column → `LocalDate`: built-in ISO conversion covers the expected values (I'll write expected dates in ISO format)
- `Throws?` column → `Class<?>`: built-in conversion requires fully-qualified names

Here's the test:

```java
import org.tabletest.junit.TableTest;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class DateParserTest {

    @DisplayName("Parse date strings")
    @TableTest("""
        Scenario     | Input      | Date?
        ISO format   | 2024-01-15 | 2024-01-15
        Slash format | 15/01/2024 | 2024-01-15
        Short year   | 24-01-15   | 2024-01-15
        Null input   |            |
        """)
    void parsesValidInputs(String input, LocalDate date) {
        assertEquals(date, parseDate(input));
    }

    @DisplayName("Reject invalid date strings")
    @TableTest("""
        Scenario     | Input | Throws?
        Empty string | ''    | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidInputs(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parseDate(input));
    }
}
```

**Key design decisions:**

- **Two tables** — split because successful parsing uses `assertEquals` and error cases use `assertThrows`; mixing them would require `if`/`switch` in the method body
- **Null input** — blank cell in both `Input` and `Date?` columns; JUnit converts blank cells to `null` for reference types
- **`Date?` expected values in ISO format** — built-in `LocalDate` conversion handles ISO 8601, so all three formats' expected output is written as `2024-01-15` (they all represent the same date)
- **`java.lang.IllegalArgumentException`** — fully-qualified name avoids needing a `@TypeConverter` for the `Class<?>` column