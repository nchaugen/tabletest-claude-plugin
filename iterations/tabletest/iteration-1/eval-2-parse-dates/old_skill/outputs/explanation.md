# TableTest Design Explanation

## Why two tables instead of one

The five test scenarios split into two tables because they require different assertion logic:

- Scenarios 1-4 (ISO format, slash format, short year, null input) all call `parseDate` and assert the return value with `assertEquals`. A blank cell in the `Input` column represents null, and a blank cell in `Parsed Date?` represents an expected null return.
- Scenario 5 (empty string) expects an exception, so it uses `assertThrows`. Mixing this assertion into the first table would require an `if`/`switch` in the test method, which the skill explicitly prohibits under quality checks ("Uniform assertions: all rows use the same assertion logic; split into separate TableTests if logic differs per row").

## Why a single-row exception table is acceptable

The skill permits a single-scenario `@TableTest` when it is part of a set of focused, single-responsibility tables and the benefit is structural consistency and the ease of adding rows later. The exception table satisfies this: it represents a distinct concern (error handling), has a clear structure, and makes it trivial to add further invalid-input cases.

## Type conversion

- `LocalDate` in `Parsed Date?` is handled by JUnit's built-in converter, which understands ISO-8601 format (`yyyy-MM-dd`). No custom `@TypeConverter` is needed.
- `Class<? extends Throwable>` in `Exception?` is handled by JUnit's built-in class converter when given a fully-qualified class name (`java.lang.IllegalArgumentException`).
- The `''` syntax represents an empty string in table syntax (as opposed to a blank cell, which represents null).

## Null handling

The null-input row uses a blank `Input` cell (null passed to the method) and a blank `Parsed Date?` cell (null expected return). Because `LocalDate` is a reference type, null is a valid parameter and return value here.
