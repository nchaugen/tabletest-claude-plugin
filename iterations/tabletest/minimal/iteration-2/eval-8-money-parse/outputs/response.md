**Step 1 — Concerns:** Two distinct behaviors, requiring different assertion logic:
1. Returns a value (or null) → `assertEquals`
2. Throws an exception → `assertThrows`

These need separate `@TableTest` methods.

**Step 2–5 — Scenarios mapped:**
- Valid: `10.00`, `0.01` → Money value
- Null → null
- Invalid: `''`, `abc`, `-5.00` → `IllegalArgumentException`

**Step 9 — Writing the code:**

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParseTest {

    @DisplayName("Parse valid and null inputs")
    @TableTest("""
        Scenario       | Input | Result?
        Valid amount   | 10.00 | 10.00
        Minimum cents  | 0.01  | 0.01
        Null input     |       |
        """)
    void parsesValidAndNullInputs(String input, Money result) {
        assertEquals(result, parse(input));
    }

    @DisplayName("Reject invalid inputs")
    @TableTest("""
        Scenario        | Input   | Throws?
        Empty string    | ''      | java.lang.IllegalArgumentException
        Letters only    | abc     | java.lang.IllegalArgumentException
        Negative value  | -5.00   | java.lang.IllegalArgumentException
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

Two notes:
- The `parseMoney` converter assumes `Money` can be constructed from a `BigDecimal`. Adjust if your `Money` API differs.
- `java.lang.IllegalArgumentException` uses the fully-qualified name so JUnit's built-in `Class<?>` conversion handles it without a custom converter.