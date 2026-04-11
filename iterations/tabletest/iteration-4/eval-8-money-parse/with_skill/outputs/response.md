No build file found, so I'll assume TableTest is already on the classpath. Here's the design:

- **Two `@TableTest` methods**: valid inputs (returns Money), invalid inputs (throws)  
- **One `@Test`**: null → returns null (single scenario, different assertion shape)

```java
@Description("Money.parse converts a decimal string to a Money object; "
    + "returns null for null input; throws IllegalArgumentException for "
    + "empty string, non-numeric input, and negative values")
class MoneyParseTest {

    @TableTest("""
        Scenario       | Input | Amount?
        Whole dollars  | 10.00 | 10.00
        Minimum value  | 0.01  | 0.01
        Zero           | 0.00  | 0.00
        """)
    @Description("Valid decimal strings produce a Money object with the corresponding amount")
    void validInput_returnsMoneyWithAmount(String input, BigDecimal amount) {
        Money result = Money.parse(input);
        assertThat(result.getAmount()).isEqualByComparingTo(amount);
    }

    @Test
    void nullInput_returnsNull() {
        assertThat(Money.parse(null)).isNull();
    }

    @TableTest("""
        Scenario       | Input
        Empty string   | ''
        Letters only   | abc
        Negative value | -5.00
        """)
    @Description("Malformed or negative strings are rejected with IllegalArgumentException")
    void invalidInput_throwsIllegalArgumentException(String input) {
        assertThatThrownBy(() -> Money.parse(input))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
```

A few notes:
- `BigDecimal` is auto-converted by JUnit from the string column values — no `@TypeConverter` needed.
- `null` → `@Test` rather than a single-row table, since the assertion logic (`isNull()`) differs from the valid-input table.
- The invalid-input table has no expectation column (`?`) because the assertion is uniform — every row must throw — and that's captured in `@Description`.
- Adjust `Money.parse(input)` and `result.getAmount()` to match your actual API if the method is on a parser instance or the accessor differs.