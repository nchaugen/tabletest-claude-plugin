# TableTest Design for `parseDate`

## Two Tables, Not One

The five test cases are split into two `@TableTest` methods because the skill requires uniform assertion logic per table. The null-return and normal-return cases share the same `assertEquals` assertion, while the exception case requires `assertThrows`. Mixing these in one table would force an `if`/`switch` in the test method body, which the skill explicitly forbids.

## Table 1 — `parsesDateFromVariousFormats`

Covers ISO format, slash format, short year, and null input together, since all four are handled by a single `assertEquals(expectedDate, parseDate(input))` call.

- **Null input**: a blank cell in the `Input` column maps to `null` (the built-in blank-cell convention), and a blank cell in the `Parsed Date?` column maps to a `null` `LocalDate` — so the assertion naturally verifies the null-returns-null contract.
- **Expected date column**: all three valid inputs produce `2024-01-15`, and JUnit's built-in `LocalDate` converter handles ISO-format values in this column automatically — no `@TypeConverter` is needed.
- **Input column**: inputs are plain `String` values; no converter is required since the column type is `String`.

## Table 2 — `throwsOnInvalidInput`

Contains only the empty-string case. The exception type is written as a fully-qualified class name (`java.lang.IllegalArgumentException`), which JUnit converts to `Class<? extends Throwable>` via its built-in `Class<?>` converter — no custom converter needed.

Even though this table has a single row, keeping it as a `@TableTest` rather than a plain `@Test` is consistent with the skill's guidance: single-row tables are fine when they are part of a set of focused, single-responsibility tables, and they make adding future exception cases (e.g., malformed format) straightforward.
