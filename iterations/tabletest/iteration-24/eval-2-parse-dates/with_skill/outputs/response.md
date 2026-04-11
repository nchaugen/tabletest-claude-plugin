Now I have everything I need. Let me design the table structure.

**Key observations:**
- `Result?` column is `LocalDate` — expected values can all be ISO format (`2024-01-15`), so built-in conversion works for that column. The input column is `String`, so no converter needed there.
- The scenarios have **two different assertion logics** (return value vs. throw), so they must be two separate `@TableTest` methods.
- The exception table has one row — that's fine as part of a focused pair.

Here's the complete test class:

```java
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ParseDateTest {

    @TableTest("""
        Scenario      | Input       | Result?
        ISO format    | 2024-01-15  | 2024-01-15
        Slash format  | 15/01/2024  | 2024-01-15
        Short year    | 24-01-15    | 2024-01-15
        Null input    |             |
        """)
    void shouldParseDate(String input, LocalDate result) {
        assertEquals(result, parseDate(input));
    }

    @TableTest("""
        Scenario      | Input | Throws?
        Empty string  | ''    | java.lang.IllegalArgumentException
        """)
    void shouldRejectInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parseDate(input));
    }
}
```

A few things to note:

**Why two tables**: `shouldParseDate` uses `assertEquals`; `shouldRejectInvalidInput` uses `assertThrows`. Mixing them in one table would require `if`/`switch` logic in the method body, which is a red flag.

**Null in `shouldParseDate`**: Blank cell in the `Input` column → `null` passed to `parseDate`. Blank `Result?` cell → `null` expected. No special handling needed since `LocalDate` is a reference type.

**`Throws?` column**: Uses `java.lang.IllegalArgumentException` (fully-qualified name) so JUnit's built-in `Class<?>` conversion works without a custom `@TypeConverter`.

**Dependency** — if not already in your build file:

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