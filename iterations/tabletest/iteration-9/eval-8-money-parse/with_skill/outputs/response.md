No existing project files to reference. I'll design the tables first, then write the code.

**Design:**
- Two distinct assertion patterns: return-value rows (valid + null) and exception rows can't share the same assertion logic → two tables.
- Valid/null table: `Input` → `Amount?` (BigDecimal, null-safe).
- Exception table: `Input` → `Throws?` (exception class column, per skill guidance).

```java
@TableTest("""
    Scenario      | Input | Amount?
    Valid amount  | 10.00 | 10.00
    Smallest unit | 0.01  | 0.01
    Null input    |       |
    """)
void shouldParseInput(String input, BigDecimal expectedAmount) {
    Money result = parse(input);
    BigDecimal actual = result != null ? result.getAmount() : null;
    assertEquals(expectedAmount, actual);
}

@TableTest("""
    Scenario        | Input  | Throws?
    Empty string    | ''     | IllegalArgumentException
    Letters only    | abc    | IllegalArgumentException
    Negative value  | -5.00  | IllegalArgumentException
    """)
void shouldRejectInvalidInput(String input, Class<? extends Exception> throws_) {
    assertThrows(throws_, () -> parse(input));
}
```

Two notes to verify against your codebase:

1. **`result.getAmount()`** — adjust to match your actual `Money` API (e.g. `getValue()`, `toBigDecimal()`).
2. **`BigDecimal` conversion** — JUnit 5 converts `String → BigDecimal` implicitly, but if you see a conversion error at runtime, add a `@TypeConverter`:
   ```java
   @TypeConverter
   static BigDecimal toBigDecimal(String value) {
       return new BigDecimal(value);
   }
   ```