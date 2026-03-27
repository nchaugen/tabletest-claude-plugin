Three distinct behaviors here — success, null, and exceptions — so I'll split into focused tables. The exception cases all share the same assertion, valid parses share another, and null stands alone as a single `@Test`.

```java
@Description("Valid decimal strings are parsed into Money objects with the correct amount")
@TableTest("""
    Scenario      | Input | Amount?
    Typical value | 10.00 | 10.00
    Minimum value | 0.01  | 0.01
    """)
void parsesValidInput(String input, BigDecimal expectedAmount) {
    assertThat(parse(input).getAmount()).isEqualByComparingTo(expectedAmount);
}

@Test
void returnsNullForNullInput() {
    assertNull(parse(null));
}

@Description("Malformed or negative inputs are rejected immediately")
@TableTest("""
    Scenario       | Input
    Empty string   | ''
    Letters only   | abc
    Negative value | -5.00
    """)
void rejectsInvalidInput(String input) {
    assertThrows(IllegalArgumentException.class, () -> parse(input));
}
```

A few design notes:

- **`parse(null)` is a `@Test`**: it's a single case with different assertion logic from the valid-parse rows (`assertNull` vs `assertThat(...).isEqualByComparingTo(...)`). Folding it into the valid table would require an `if` in the test method.
- **No `Exception?` column** in the rejection table: all three cases throw the same type, so hardcoding `IllegalArgumentException.class` in the assertion is cleaner than a redundant column.
- **`BigDecimal` for `Amount?`**: adjust `getAmount()` to match your actual `Money` API. If `Money.toString()` returns the decimal string directly, `assertThat(parse(input).toString()).isEqualTo(expectedAmount.toPlainString())` also works.