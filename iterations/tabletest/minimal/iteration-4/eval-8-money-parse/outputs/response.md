Here's the TableTest:

```java
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyParseTest {

    @TableTest("""
            Scenario     | Input | Amount?
            Valid amount | 10.00 | 10.00
            Minimal cent | 0.01  | 0.01
            """)
    void parsesValidInput(String input, BigDecimal amount) {
        assertEquals(amount, parse(input).getAmount());
    }

    @Test
    void parsesNullAsNull() {
        assertNull(parse(null));
    }

    @TableTest("""
            Scenario       | Input   | Throws?
            Empty string   | ''      | IllegalArgumentException
            Letters only   | abc     | IllegalArgumentException
            Negative value | -5.00   | IllegalArgumentException
            """)
    void rejectsInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parse(input));
    }
}
```

**Design notes:**

- **Three methods, not one** — `parsesValidInput` and `rejectsInvalidInput` have different assertion logic, so they must be separate tables. A single combined table would require an `if`-statement in the method body.
- **`@Test` for null** — null-returns-null is a single case with no variations, so a `@Test` is the right fit rather than a one-row table.
- **`Throws?` column** — even though all three rows throw the same exception, the column keeps each row's expectation explicit and makes it easy to add rows with different exception types later.
- **`Amount?` uses `BigDecimal`** — JUnit converts `"10.00"` to `BigDecimal` natively; no `@TypeConverter` needed. Adjust `getAmount()` to match your actual `Money` accessor.