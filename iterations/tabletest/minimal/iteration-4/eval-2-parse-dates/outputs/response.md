No project files to check here, so I'll note the dependency assumptions inline.

Since the test cases have different assertion logic (return value vs. exception), they split into two `@TableTest` methods:

```java
import org.tabletest.junit.TableTest;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class DateParserTest {

    private final DateParser parser = new DateParser();

    @TableTest("""
        Scenario     | Input      | Result?
        ISO format   | 2024-01-15 | 2024-01-15
        Slash format | 15/01/2024 | 2024-01-15
        Short year   | 24-01-15   | 2024-01-15
        Null input   |            |
        """)
    void parsesDate(String input, LocalDate expected) {
        assertEquals(expected, parser.parseDate(input));
    }

    @TableTest("""
        Scenario     | Input | Throws?
        Empty string | ''    | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parser.parseDate(input));
    }
}
```

A few things to note:

- **No `@TypeConverter` needed**: `Input` is `String` (no conversion), `Result?` uses ISO format `2024-01-15` so JUnit's built-in `LocalDate` converter handles it, and `Throws?` uses the fully-qualified class name which JUnit's `Class<?>` converter handles.
- **Null input**: blank cells in both `Input` and `Result?` columns — `String` and `LocalDate` are reference types so null works.
- **`Throws?` uses fully-qualified name**: `java.lang.IllegalArgumentException` not just `IllegalArgumentException` — JUnit requires this for `Class<?>` conversion.
- **Dependency**: ensure `org.tabletest:tabletest-junit` is in your `pom.xml`/`build.gradle` with JUnit Jupiter 5.11+.