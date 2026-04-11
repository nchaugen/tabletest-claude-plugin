Here's the TableTest:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyParseTest {

    @TableTest("""
            Scenario        | Input  | Expected?
            Standard value  | 10.00  | 10.00
            Minimum value   | 0.01   | 0.01
            Null input      |        |
            """)
    void shouldParseInput(String input, Money expected) {
        assertEquals(expected, parse(input));
    }

    @TableTest("""
            Scenario       | Input  | Throws?
            Empty string   | ''     | java.lang.IllegalArgumentException
            Letters only   | abc    | java.lang.IllegalArgumentException
            Negative value | -5.00  | java.lang.IllegalArgumentException
            """)
    void shouldRejectInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        if (value == null) return null;
        return new Money(new BigDecimal(value)); // adjust to your Money constructor/factory
    }
}
```

**Key design decisions:**

- **Two tables** because the assertion logic differs: `assertEquals` for return-value cases, `assertThrows` for exception cases. The skill requires uniform assertions within a table.
- **Null input in the success table** (blank cell → `null` parameter). `assertEquals(null, null)` passes when `parse(null)` returns null — no need for a separate `@Test`.
- **`Throws?` column** uses fully-qualified class names so JUnit's built-in `Class<?>` conversion handles it — no custom converter needed.
- **`@TypeConverter` for `Money`** — since JUnit can't construct `Money` from a string automatically. The converter returns `null` for blank cells (the null-input row's expected column), keeping the assertion uniform.

**Two things to adjust for your project:**
1. The `parseMoney` converter — replace `new Money(new BigDecimal(value))` with however your `Money` class is constructed.
2. The `parse(input)` call — replace with your actual method reference (e.g., `MoneyParser.parse(input)`).

**Dependency** (if not already present):
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```