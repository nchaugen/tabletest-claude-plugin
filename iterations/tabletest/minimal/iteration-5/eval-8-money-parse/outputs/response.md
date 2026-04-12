Here's the TableTest:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParserTest {

    @TableTest("""
            Scenario      | Input | Money?
            Valid amount  | 10.00 | 10.00
            Minimum value | 0.01  | 0.01
            Null input    |       |
            """)
    void parsesInput(String input, Money expected) {
        assertEquals(expected, parse(input));
    }

    @TableTest("""
            Scenario       | Input  | Throws?
            Empty string   | ''     | java.lang.IllegalArgumentException
            Letters only   | abc    | java.lang.IllegalArgumentException
            Negative value | -5.00  | java.lang.IllegalArgumentException
            """)
    void rejectsInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        return new Money(new BigDecimal(value));
    }
}
```

**Design decisions:**

- **Two tables** — `parsesInput` and `rejectsInvalidInput` use different assertion logic (`assertEquals` vs `assertThrows`), so they must be separate `@TableTest` methods.
- **Null as blank cell** — the null input row uses blank cells for both `Input` and `Money?`, which TableTest maps to `null` for reference types.
- **`@TypeConverter`** — `Money` is a custom type, so a converter method builds the expected `Money` from the string in the `Money?` column. It won't interfere with `input` (typed `String`) or `throws_` (typed `Class<? extends Exception>`).
- **`Throws?` column** — exception type is explicit per row rather than hardcoded in the method body, keeping each row's full expected outcome visible in the table. Fully-qualified class names are used so JUnit's built-in `Class<?>` conversion handles them without a custom converter.