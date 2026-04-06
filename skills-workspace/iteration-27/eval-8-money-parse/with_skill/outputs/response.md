Working through the requirements analysis before writing code:

**Concerns:** Two distinct concerns with different assertion logic:
1. *Parse input* — returns `Money` (or null for null)
2. *Reject invalid input* — throws `IllegalArgumentException`

**Key design decisions:**
- `Money` is a custom type → needs a `@TypeConverter`
- Exception column uses FQN strings — JUnit converts them to `Class<?>` natively, no converter needed
- One open question: is `"0.00"` valid or should it throw? Not specified — worth noting in `@Description`
- The three invalid cases all throw the same exception type, but are distinct input variations (not value-set candidates — different inputs, same exception is just a coincidence of this method)

Here's the complete test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParseTest {

    @DisplayName("Parsing valid and null input")
    @Description("""
            Open: is '0.00' a valid input or should it throw?
            Currently untested.
            """)
    @TableTest("""
            Scenario        | Input  | Money?
            Standard amount | 10.00  | 10.00
            Minimum value   | 0.01   | 0.01
            Null input      |        |
            """)
    void shouldParseInput(String input, Money expected) {
        assertEquals(expected, parse(input));
    }

    @DisplayName("Rejecting invalid input")
    @TableTest("""
            Scenario        | Input   | Throws?
            Empty string    | ''      | java.lang.IllegalArgumentException
            Letters only    | abc     | java.lang.IllegalArgumentException
            Negative value  | -5.00   | java.lang.IllegalArgumentException
            """)
    void shouldRejectInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        if (value == null) return null;
        return new Money(new BigDecimal(value)); // adapt to actual Money constructor/factory
    }
}
```

A few notes on the design:

- **Two tables** because the assertion logic differs — `assertEquals` for valid/null cases, `assertThrows` for invalid ones. Mixing them in one table would require an `if`/`switch` in the method body.
- **`Throws?` column** uses fully-qualified class names (`java.lang.IllegalArgumentException`) so JUnit's built-in `Class<?>` conversion handles them — no extra converter needed.
- **`@TypeConverter parseMoney`** handles the `Money?` column. The null check covers the blank `Null input` row. Adapt the constructor call to your actual `Money` API (e.g. `Money.of(...)` or `Money.parse(...)`).
- **Zero is an open question** — the requirements define valid and invalid cases but don't say whether `"0.00"` should succeed or throw. The `@Description` flags this so it doesn't get silently resolved.