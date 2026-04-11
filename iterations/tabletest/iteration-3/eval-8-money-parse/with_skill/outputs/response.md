# TableTest for `Money parse(String input)`

The method has two distinct behaviours: returning a `Money` result (including `null` for null input), and throwing `IllegalArgumentException` for invalid inputs. The skill recommends a `Throws?` column to handle exception cases within the same table when the assertion logic differs per row — but that would require an `if`/`switch` in the test body, which violates the uniform-assertion rule.

The clean approach is **two focused `@TableTest` methods**: one for valid inputs (including the null case), and one for the exception cases.

## Valid inputs (including null)

```java
@TableTest("""
    Scenario           | Input | Money?
    Standard amount    | 10.00 | 10.00
    Minimum value      | 0.01  | 0.01
    Null input         |       |
    """)
void parsesValidInput(String input, Money expected) {
    assertEquals(expected, Money.parse(input));
}
```

Notes:
- The blank cell in `Input` represents `null` (not the string `"null"`).
- The blank cell in `Money?` represents `null` return value — consistent with the method contract.
- `Money?` suffix signals this is the expectation column.

## Exception cases

```java
@TableTest("""
    Scenario          | Input
    Empty string      | ''
    Letters only      | abc
    Negative value    | -5.00
    """)
void rejectsInvalidInput(String input) {
    assertThrows(IllegalArgumentException.class, () -> Money.parse(input));
}
```

Notes:
- `''` is the TableTest syntax for an empty string (blank cell would mean `null`).
- All three cases throw `IllegalArgumentException` — uniform assertion logic, no `if`/`switch` needed.
- A single-column table is valid here: `input` is the only varying data, and the expectation (always throws) is expressed in the assertion.

## Why two tables?

The skill's quality check requires "uniform assertions: all rows use the same assertion logic; split into separate TableTests if logic differs per row." A `Throws?` column that is sometimes populated and sometimes not would require conditional logic in the test body. Two focused tables keep both clean.
