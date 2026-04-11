Working through the requirements:

**Step 1 — Name concerns:**
1. Parse valid input → returns Money (or null)
2. Reject invalid input → throws

These can't share assertion logic (one uses `assertEquals`, the other `assertThrows`), so two tables.

**Step 2–3 — Examples and variations:**
- Valid: `10.00`, `0.01` → Money objects
- Null → null
- Rejections: `''`, `abc`, `-5.00` → all `IllegalArgumentException`

**Step 4 — Irrelevant inputs:** none apply here.

**Type conversion:** `Money` is a custom type, needs a `@TypeConverter`. `Class<?>` for the Throws? column uses JUnit's built-in fully-qualified class name conversion.

---

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParseTest {

    @DisplayName("Parse valid input")
    @TableTest("""
            Scenario      | Input | Result?
            Standard      | 10.00 | 10.00
            Minimum value | 0.01  | 0.01
            Null input    |       |
            """)
    void shouldParseValidInput(String input, Money result) {
        assertEquals(result, parse(input));
    }

    @DisplayName("Reject invalid input")
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
        // Adjust to match your Money factory method (e.g. Money.of(...))
        return value == null ? null : new Money(new BigDecimal(value));
    }
}
```

Two tables because the assertion logic differs — valid/null cases assert a return value, rejection cases assert an exception. Key design notes:

- **Blank `Input` cell** → `null` String → `parse(null)` → `null` result
- **Blank `Result?` cell** → TypeConverter receives `null` → returns `null` Money → `assertEquals(null, result)`
- **`Throws?` column** uses JUnit's built-in `Class<?>` conversion from fully-qualified name — no custom converter needed
- `Money.equals()` must compare by amount for the first table's assertions to work — adjust `parseMoney` if your `Money` class uses a different factory method