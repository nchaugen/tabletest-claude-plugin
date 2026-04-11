Now I have everything I need. The requirements split into two distinct concerns: parsing (success + null) and rejection (exceptions). `Money` is a custom type needing a `@TypeConverter`, and `IllegalArgumentException` can use JUnit's built-in FQN class conversion.

Here's the TableTest:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyParserTest {

    private final MoneyParser parser = new MoneyParser();

    @TableTest("""
        Scenario      | Input | Money?
        Valid amount  | 10.00 | 10.00
        Minimal value | 0.01  | 0.01
        Null input    |       |
        """)
    void shouldParseMoney(String input, Money expected) {
        assertEquals(expected, parser.parse(input));
    }

    @TableTest("""
        Scenario       | Input  | Throws?
        Empty string   | ''     | java.lang.IllegalArgumentException
        Letters only   | abc    | java.lang.IllegalArgumentException
        Negative value | -5.00  | java.lang.IllegalArgumentException
        """)
    void shouldRejectInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parser.parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        if (value == null) return null;
        return new Money(new BigDecimal(value));
    }
}
```

Key design decisions:

- **Two tables, two concerns**: parsing behaviour (success path + null) vs. rejection behaviour (exception path). They have different assertion logic so they belong in separate methods.
- **Null in the main table**: the blank cell in `Money?` becomes `null`, and `assertEquals(null, null)` passes — no separate `@Test` needed.
- **`Throws?` column** uses FQN `java.lang.IllegalArgumentException` — JUnit's built-in `Class<?>` converter handles it, no custom converter needed.
- **`@TypeConverter parseMoney`** constructs expected `Money` values from their string representation without calling the method under test. Adjust the constructor call (`new Money(...)`) to match your actual `Money` API.