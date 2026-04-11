```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class MoneyParserTest {

    private MoneyParser parser;

    @BeforeEach
    void setUp() {
        parser = new MoneyParser();
    }

    @ParameterizedTest(name = "parse(\"{0}\") returns Money({1})")
    @CsvSource({
        "10.00, 10.00",
        "0.01,  0.01",
    })
    void parse_validInput_returnsMoney(String input, String expectedAmount) {
        Money result = parser.parse(input);
        assertNotNull(result);
        assertEquals(expectedAmount, result.getAmount().toString());
    }

    @ParameterizedTest(name = "parse(null) returns null")
    @ValueSource(strings = {})  // placeholder — see nullTest below
    void parse_nullInput_returnsNull() {
        assertNull(parser.parse(null));
    }

    @ParameterizedTest(name = "parse(\"{0}\") throws IllegalArgumentException")
    @ValueSource(strings = {
        "",        // empty string
        "abc",     // letters only
        "-5.00",   // negative value
    })
    void parse_invalidInput_throwsIllegalArgumentException(String input) {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(input));
    }
}
```

A few notes:

- The null case is a single assertion, not a table — the `parse_nullInput_returnsNull` test is kept separate since `@ValueSource` can't supply `null`. If you prefer everything in one place, use `@MethodSource` with a `Stream<Arguments>` that includes a `null` row.
- `result.getAmount().toString()` assumes `Money` exposes its amount; adjust to match your actual API (e.g., `result.toString()`, `result.getValue()`, etc.).
- The `@ParameterizedTest(name = ...)` labels make the test report readable — each row shows its input in the test name.