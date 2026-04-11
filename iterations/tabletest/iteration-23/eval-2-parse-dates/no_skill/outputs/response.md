```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DateParserTest {

    static Stream<Object[]> parseDateCases() {
        return Stream.of(
            // input,          expected result,              expectedException
            new Object[]{ "2024-01-15",  LocalDate.of(2024, 1, 15), null },
            new Object[]{ "15/01/2024",  LocalDate.of(2024, 1, 15), null },
            new Object[]{ "24-01-15",    LocalDate.of(2024, 1, 15), null },
            new Object[]{ null,          null,                       null },
            new Object[]{ "",            null,                       IllegalArgumentException.class }
        );
    }

    @ParameterizedTest(name = "[{index}] input={0}")
    @MethodSource("parseDateCases")
    void parseDate(String input, LocalDate expected, Class<? extends Exception> expectedException) {
        if (expectedException != null) {
            assertThrows(expectedException, () -> parser.parseDate(input));
        } else {
            assertEquals(expected, parser.parseDate(input));
        }
    }
}
```

A few notes:
- Add `private final DateParser parser = new DateParser();` (or whatever your class is named) as a field.
- The `[{index}] input={0}` name pattern makes failure messages show the exact input that failed.
- If you're on JUnit 5.8+ you can use `@MethodSource` with a static method in the same class without fully qualifying it; older versions need the class name prefix like `"DateParserTest#parseDateCases"`.
- For the `null` input case, `assertEquals(null, result)` works fine — but you could also use `assertNull` if you prefer clarity.