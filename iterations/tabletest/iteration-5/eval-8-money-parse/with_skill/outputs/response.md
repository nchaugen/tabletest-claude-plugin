The `parse` method has two distinct assertion shapes — checking a return value (valid + null) and asserting an exception (invalid inputs) — so two tables are the right structure.

For the `Money` return type, a `@TypeConverter` is needed since JUnit can't convert it automatically.

```java
public class MoneyParseTest {

    @Description("Valid string inputs produce a Money with the same value; null input returns null")
    @TableTest("""
        Scenario       | Input | Money?
        Typical value  | 10.00 | 10.00
        Minimum value  | 0.01  | 0.01
        Null input     |       |
        """)
    void parse_valid_and_null(String input, Money expected) {
        assertEquals(expected, parse(input));
    }

    @Description("Empty string, non-numeric input, and negative values are rejected")
    @TableTest("""
        Scenario        | Input  | Exception?
        Empty string    | ''     | java.lang.IllegalArgumentException
        Letters only    | abc    | java.lang.IllegalArgumentException
        Negative value  | -5.00  | java.lang.IllegalArgumentException
        """)
    void parse_invalid_inputs_throw(String input, Class<? extends Throwable> expectedException) {
        assertThrows(expectedException, () -> parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        return value == null ? null : new Money(value);
    }
}
```

A few notes:

- **Null input** goes in the valid table with a blank `Input` cell and a blank `Money?` cell — both map to `null`, and `assertEquals(null, null)` passes cleanly.
- **Exception class** uses the fully-qualified name (`java.lang.IllegalArgumentException`) so JUnit's built-in `Class<?>` converter handles it — no custom converter needed for that column.
- **Two tables** rather than one: the assertion logic genuinely differs (`assertEquals` vs `assertThrows`), so combining them would require `if`/`switch` in the test method, which is a quality check violation.
- **`@TypeConverter`** assumes `Money` has a string-based constructor. Adjust `new Money(value)` to match your actual `Money` API (e.g., `Money.of(value)` or `new Money(new BigDecimal(value))`).