The return-value cases and exception cases need separate tables — they use different assertion logic. The `Money` expected column needs a `@TypeConverter` to avoid conditionals in the test body.

Here is the TableTest:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyParserTest {

    @TableTest("""
        Scenario     | Input | Result?
        Valid amount | 10.00 | 10.00
        Minimal cent | 0.01  | 0.01
        Null input   |       |
        """)
    void shouldParseOrReturnNull(String input, Money result) {
        assertEquals(result, parse(input));
    }

    @TableTest("""
        Scenario       | Input  | Throws?
        Empty string   | ''     | java.lang.IllegalArgumentException
        Letters only   | abc    | java.lang.IllegalArgumentException
        Negative value | -5.00  | java.lang.IllegalArgumentException
        """)
    void shouldThrowForInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        if (value == null) return null;
        return new Money(new BigDecimal(value));
    }
}
```

A few notes on the design choices:

**Two tables, not one.** The return-value cases (`assertEquals`) and exception cases (`assertThrows`) require different assertion logic, so they must be separate. Mixing them would require an `if`/`switch` in the test body.

**`@TypeConverter` for `Money`.** The expected `Result?` column uses `Money` as its parameter type, not `String` or `BigDecimal`. The converter handles both the non-null parsing and the null case (blank cell → `null`), keeping the test method body to a single `assertEquals` with no conditionals. This assumes `Money` implements `equals` — a reasonable expectation for a value type.

**`Throws?` with fully-qualified class names.** JUnit's built-in `Class<?>` conversion handles `java.lang.IllegalArgumentException` directly — no custom converter needed.

**Null via blank cell, empty string via `''`.** The blank `Input` cell for "Null input" passes `null` to `parse()`; `''` passes an empty string.