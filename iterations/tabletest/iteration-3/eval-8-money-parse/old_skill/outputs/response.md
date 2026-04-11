# TableTest for `parse(String input)`

The method has two distinct behaviours: returning a `Money` value (or null) for valid/null input, and throwing for invalid input. I'll handle these in two separate `@TableTest` methods to keep the structure clean.

## Valid and null inputs

```java
@TableTest("""
    Scenario              | Input  | Money?
    Standard amount       | 10.00  | Money(10.00)
    Minimum valid amount  | 0.01   | Money(0.01)
    Null input            |        | null
    """)
void testParseValidInputs(String scenario, String input, Money expected) {
    assertThat(Money.parse(input)).isEqualTo(expected);
}
```

> Note: The `Null input` row has a blank `Input` cell, which the framework will pass as `null` to the method. The `Money?` column ends with `?` to indicate it is the output/result column.

## Invalid inputs that throw

```java
@TableTest("""
    Scenario           | Input  | Throws?
    Empty string       |        | IllegalArgumentException
    Letters only       | abc    | IllegalArgumentException
    Negative value     | -5.00  | IllegalArgumentException
    """)
void testParseInvalidInputs(String scenario, String input, Class<? extends Exception> expectedEx) {
    assertThrows(expectedEx, () -> Money.parse(input));
}
```

> The `Throws?` column captures the expected exception type for each invalid case. Empty string is represented as a blank cell.

### Design notes

- Separating valid-return cases from throwing cases avoids awkward conditional logic in the test method body.
- The `Money?` column name (with `?`) follows the convention of marking output/result columns.
- Null input is represented as a blank cell, not the literal string `"null"`.
