Good. The non-ISO date strings (`15/01/2024`, `24-01-15`) are in a `String` input column — JUnit won't try to auto-convert them, so no `@TypeConverter` is needed. The `Result?` column can express all expected `LocalDate` values in ISO format (`2024-01-15`), which JUnit converts automatically.

The exception case needs a separate table since mixing return-value rows and exception rows would require `if`/`switch` logic in the method body, violating the "uniform assertions" rule.

The single-row exception table is fine here — it's a focused, single-responsibility table alongside `parsesValidFormats`, and easy to extend with more invalid inputs later.

```java
import org.tabletest.junit.TableTest;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class ParseDateTest {

    @TableTest("""
        Scenario     | Input      | Result?
        ISO format   | 2024-01-15 | 2024-01-15
        Slash format | 15/01/2024 | 2024-01-15
        Short year   | 24-01-15   | 2024-01-15
        Null input   |            |
        """)
    void parsesValidFormats(String input, LocalDate result) {
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

**Dependency** (if not already present):

Maven:
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Gradle:
```groovy
testImplementation "org.tabletest:tabletest-junit:VERSION"
```

**Key design notes:**
- `Result?` column uses ISO format for all expected `LocalDate` values — JUnit auto-converts, no `@TypeConverter` needed
- Blank cells in both input and result columns map to `null` (covering the null-in / null-out case)
- `Throws?` uses fully-qualified class name (`java.lang.IllegalArgumentException`) for JUnit's built-in `Class<?>` conversion
- Two tables keeps assertion logic uniform within each method — no `if`/`switch`