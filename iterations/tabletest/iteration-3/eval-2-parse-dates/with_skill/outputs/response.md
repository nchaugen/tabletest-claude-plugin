# TableTest for `parseDate`

## Design considerations

Your five scenarios fall into two distinct groups with different assertion logic:

1. **Valid inputs** — expect a `LocalDate` result
2. **Exception input** — expect an `IllegalArgumentException` to be thrown

These need separate tables because the assertion logic differs per group. The null case also needs care: a blank cell in the table maps to `null` input, keeping the table clean.

### Type conversion

JUnit 5 has built-in conversion for `LocalDate` from ISO 8601 format (`2024-01-15`) — no converter needed for that. However, the slash format (`15/01/2024`) and the short-year format (`24-01-15`) are non-standard and JUnit cannot convert them to `LocalDate` automatically.

For those, you need a `@TypeConverter` method that handles all three input formats and produces a `LocalDate`, then uses that `LocalDate` for the `Result?` column.

Since the result column contains `LocalDate` values in ISO format (which JUnit can convert natively), you can use `LocalDate` as the parameter type for `Result?` directly.

---

## Table 1 — Valid inputs (including null)

```java
@TableTest("""
    Scenario            | Input        | Result?
    ISO format          | 2024-01-15   | 2024-01-15
    Slash format        | 15/01/2024   | 2024-01-15
    Short year          | 24-01-15     | 2024-01-15
    Null input          |              |
    """)
void parsesDateSuccessfully(String input, LocalDate result) {
    assertEquals(result, dateParser.parseDate(input));
}
```

The blank cell in the `Input` column passes `null` to the method. The blank cell in `Result?` asserts that the return value is `null`.

**Note on type conversion**: The `Input` column stays as `String` (no conversion needed — you pass it straight to `parseDate`). The `Result?` column uses `LocalDate` as the parameter type, and JUnit's built-in converter handles the ISO 8601 values (`2024-01-15`). The blank `Result?` cell becomes `null`, which is fine for the boxed `LocalDate` type.

The non-standard input formats (`15/01/2024`, `24-01-15`) are handled by `parseDate` itself — the table just passes them as strings. If you wanted to test the converter in isolation or had a `LocalDate` input column, you would need a `@TypeConverter`.

---

## Table 2 — Exception cases

Since the empty string should throw `IllegalArgumentException`, and TableTest doesn't have a `Throws?` column built in, the cleanest approach is a separate `@TableTest` using `assertThrows` in the method body:

```java
@TableTest("""
    Scenario            | Input
    Empty string        | ''
    """)
void throwsForInvalidInput(String input) {
    assertThrows(IllegalArgumentException.class, () -> dateParser.parseDate(input));
}
```

The `''` syntax represents an empty string (a blank cell would mean `null`, not empty string).

If you later add more invalid inputs that also throw `IllegalArgumentException`, just add rows:

```java
@TableTest("""
    Scenario            | Input
    Empty string        | ''
    Nonsense value      | not-a-date
    """)
void throwsForInvalidInput(String input) {
    assertThrows(IllegalArgumentException.class, () -> dateParser.parseDate(input));
}
```

---

## Imports needed

```java
import org.tabletest.junit.TableTest;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions.assertThrows;
```
