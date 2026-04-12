No build file found, so I'll write the test and note the dependency. Two tables cover the two distinct assertion shapes — `assertEquals` for valid/null returns, and `assertThrows` for rejections.

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParseTest {

    @TableTest("""
        Scenario       | Input | Result?
        Valid decimal  | 10.00 | 10.00
        Minimum value  | 0.01  | 0.01
        Null input     |       |
        """)
    void parsesValidInput(String input, Money result) {
        assertEquals(result, parse(input));
    }

    @TableTest("""
        Scenario       | Input  | Throws?
        Empty string   | ''     | IllegalArgumentException
        Letters only   | abc    | IllegalArgumentException
        Negative value | -5.00  | IllegalArgumentException
        """)
    void rejectsInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        return new Money(new BigDecimal(value));  // adjust to match your Money constructor
    }
}
```

**Two tables, two reasons:**
- `parsesValidInput` — `assertEquals` on the return value; the null case fits naturally as a blank cell row
- `rejectsInvalidInput` — `assertThrows` with the exception type as a column, so each row's expected outcome is visible in the table

**Notes:**
- The class must be `public` for `@TypeConverter` discovery
- Adjust `new Money(new BigDecimal(value))` to match your actual `Money` construction API
- If not already present, add to your build file:
  ```xml
  <dependency>
      <groupId>org.tabletest</groupId>
      <artifactId>tabletest-junit</artifactId>
      <version>VERSION</version>
      <scope>test</scope>
  </dependency>
  ```