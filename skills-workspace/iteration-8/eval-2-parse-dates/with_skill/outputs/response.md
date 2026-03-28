Good — the `Input` column is `String` (no conversion needed), and the `Result?` column will use ISO-format `LocalDate` values that JUnit converts automatically. No custom `@TypeConverter` is needed.

The exception case has a different assertion shape than the return cases, so I'll split into two focused tables — one for normal parsing (including the null case) and one for rejection. Here's the result:

```java
@TableTest("""
    Scenario     | Input      | Result?
    ISO format   | 2024-01-15 | 2024-01-15
    Slash format | 15/01/2024 | 2024-01-15
    Short year   | 24-01-15   | 2024-01-15
    Null input   |            |
    """)
void parsesDate(String input, LocalDate result) {
    assertEquals(result, parseDate(input));
}

@TableTest("""
    Scenario     | Input | Throws?
    Empty string | ''    | java.lang.IllegalArgumentException
    """)
void rejectsInvalidInput(String input, Class<? extends Exception> throws_) {
    assertThrows(throws_, () -> parseDate(input));
}
```

A few design notes:

- **Two tables** — the exception case requires `assertThrows` while the return cases use `assertEquals`. Mixing them in one table would force an `if` in the method body, which the quality checks prohibit. Splitting keeps each method a clean arrange-act-assert with no branching.
- **Null input as blank cell** — the null row sits naturally in the main table with a blank `Input` and blank `Result?`, keeping it visible alongside the other parsing cases rather than in a separate `@Test`.
- **`Result?` values are ISO** — even for the slash and short-year rows, the *expected* `LocalDate` is written as `2024-01-15` (ISO), which JUnit converts automatically. The non-ISO strings only appear in the `Input` column which is typed `String` — no converter needed.
- **`Throws?` uses fully-qualified name** — JUnit's built-in `Class<?>` conversion requires the full class name (`java.lang.IllegalArgumentException`), avoiding the need for a custom `@TypeConverter`.
- **Single-row exception table** — one row is fine here since this is part of a focused set of parsing tables; more exception inputs (blank, malformed) can be added as rows later.