Working through the requirements analysis before writing code:

**Concern 1: Parse valid and null input** — returns `Money` or `null`  
**Concern 2: Reject invalid input** — throws `IllegalArgumentException`

These need separate tables because the assertion logic differs (`assertEquals` vs `assertThrows`).

The `Money` expected column needs a `@TypeConverter` since JUnit can't construct it automatically. The `Throws?` column uses fully-qualified class names, which JUnit converts to `Class<?>` natively — no converter needed.

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyParseTest {

    @DisplayName("Parse valid and null input")
    @Description("""
        Open: behaviour for '0.00' (zero amount) is unspecified — assumed valid.
        Money.equals() must be implemented for assertEquals to work correctly.
        """)
    @TableTest("""
        Scenario        | Input | Result?
        Typical amount  | 10.00 | 10.00
        Minimal amount  | 0.01  | 0.01
        Null input      |       |
        """)
    void shouldParseValidInput(String input, Money expected) {
        assertEquals(expected, parse(input));
    }

    @DisplayName("Reject invalid input")
    @TableTest("""
        Scenario        | Input  | Throws?
        Empty string    | ''     | java.lang.IllegalArgumentException
        Letters only    | abc    | java.lang.IllegalArgumentException
        Negative value  | -5.00  | java.lang.IllegalArgumentException
        """)
    void shouldRejectInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        if (value == null) return null;
        return new Money(new BigDecimal(value)); // adjust to actual Money constructor
    }
}
```

**Key decisions:**

- **Two tables** because the assertion logic differs — `assertEquals` for valid/null, `assertThrows` for invalid inputs.
- **Null input** sits in the first table as a blank cell row — no need for a separate `@Test`.
- **`Throws?` column** uses fully-qualified class names (`java.lang.IllegalArgumentException`) so JUnit's built-in `Class<?>` converter handles it with no custom code.
- **`@TypeConverter` for `Money`** — adjust the constructor call to match your actual `Money` API (e.g. `Money.of(...)`, `new Money(BigDecimal)`, etc.).
- **`@Description`** notes the open question about `0.00` and the `equals()` assumption, which aren't visible from the table alone.