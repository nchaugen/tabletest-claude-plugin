No build file found, so I'll skip the dependency check. The assertion logic differs across the three behaviors (returns Money, returns null, throws), so I'll use two `@TableTest` methods and one `@Test`:

```java
@TableTest("""
    Scenario      | Input | Amount?
    Standard      | 10.00 | 10.00
    Minimum value | 0.01  | 0.01
    """)
@Description("Valid decimal strings are parsed into Money with the exact amount")
void parse_returns_money_for_valid_input(String input, BigDecimal amount) {
    Money result = parse(input);
    assertNotNull(result);
    assertEquals(amount, result.amount());
}

@Test
void parse_returns_null_for_null_input() {
    assertNull(parse(null));
}

@TableTest("""
    Scenario       | Input
    Empty string   | ''
    Letters only   | abc
    Negative value | -5.00
    """)
@Description("Inputs that are empty, non-numeric, or negative are rejected with IllegalArgumentException")
void parse_throws_for_invalid_input(String input) {
    assertThrows(IllegalArgumentException.class, () -> parse(input));
}
```

A few notes:
- The three behaviors have different assertion logic, so they're split into separate methods — two `@TableTest`s (multiple rows each) and one `@Test` (single scenario).
- `''` in the table is the TableTest syntax for an empty string; a blank cell would mean `null`.
- `Amount?` assumes `Money` has an `amount()` accessor returning `BigDecimal` — adjust to match your actual API.
- If `Money` doesn't have a convenient equality-comparable field, you could assert `assertNotNull(result)` only, or use `toString()` comparison.