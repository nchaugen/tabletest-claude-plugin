Two concerns here with different assertion logic: successful parsing (including null) and exception throwing. I'll split them into separate `@TableTest` methods.

Key observations:
- Expected `LocalDate` values can all be written in ISO format (`2024-01-15`) — JUnit's built-in conversion handles this, no `@TypeConverter` needed
- Inputs are plain `String`, also no conversion needed
- Null input (blank cell) works because `LocalDate` is a reference type
- The single-row "rejects" table is fine here as it's part of a focused set and easy to extend

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseDateTest {

    DateParser parser = new DateParser();

    @DisplayName("Parse date strings")
    @TableTest("""
        Scenario      | Input      | Result?
        ISO format    | 2024-01-15 | 2024-01-15
        Slash format  | 15/01/2024 | 2024-01-15
        Short year    | 24-01-15   | 2024-01-15
        Null input    |            |
        """)
    void parsesDateStrings(String input, LocalDate result) {
        assertEquals(result, parser.parseDate(input));
    }

    @DisplayName("Reject invalid inputs")
    @TableTest("""
        Scenario      | Input | Throws?
        Empty string  | ''    | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidInputs(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parser.parseDate(input));
    }
}
```

A few things to adjust for your project:
- Replace `DateParser` and `parser.parseDate(input)` with your actual class/method reference
- Add whatever imports your project uses for the class under test
- Verify the dependency in your `pom.xml`/`build.gradle` (see the Pre-Check section in the skill for the coordinates)