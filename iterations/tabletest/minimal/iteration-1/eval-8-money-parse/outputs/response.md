Working through the design:

**Concerns:**
1. Valid inputs + null → returns `Money` or `null` (assert equality)
2. Invalid inputs → throws `IllegalArgumentException` (assert throws)

These need separate `@TableTest` methods since the assertion logic differs.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParseTest {

    @DisplayName("Parse valid and null input")
    @TableTest("""
        Scenario      | Input | Result?
        Valid amount  | 10.00 | 10.00
        Minimum value | 0.01  | 0.01
        Null input    |       |
        """)
    void shouldParseInput(String input, Money result) {
        assertEquals(result, parse(input));
    }

    @DisplayName("Reject invalid input")
    @TableTest("""
        Scenario       | Input  | Throws?
        Empty string   | ''     | IllegalArgumentException
        Letters only   | abc    | IllegalArgumentException
        Negative value | -5.00  | IllegalArgumentException
        """)
    void shouldRejectInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        if (value == null) return null;
        return new Money(new BigDecimal(value)); // adjust to match your Money API
    }

    @TypeConverter
    public static Class<? extends Exception> parseExceptionClass(String name) {
        return switch (name) {
            case "IllegalArgumentException" -> IllegalArgumentException.class;
            default -> throw new IllegalArgumentException("Unknown exception class: " + name);
        };
    }
}
```

A few notes:

- **`parseMoney`** — adjust `new Money(new BigDecimal(value))` to match your actual `Money` constructor or factory method. The null guard handles the blank cell in the `Null input` row.
- **`parseExceptionClass`** — avoids needing the fully qualified `java.lang.IllegalArgumentException` in the table. Extend the `switch` if you add other exception types later.
- **`-5.00`** — no quoting needed; `-` is not a special character in the table syntax.