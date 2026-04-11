# TableTest Design for `parseDate`

## Table Structure

The five test scenarios are split into two TableTest methods because they require **different assertion logic**:

- `parsesDateFormats` — covers normal parsing and null input, all verified with `assertEquals`
- `throwsOnInvalidInput` — covers exception-throwing inputs, verified with `assertThrows`

Mixing these into one table would require `if`/`switch` logic in the test method body, which violates the quality rule that TableTest methods must be straightforward (no branching).

## Design Decisions

### Type Conversion for Expected Date

`LocalDate` is a JUnit built-in conversion type. The expected date values in the `Parsed Date?` column are written in ISO-8601 format (`2024-01-15`), which JUnit converts directly to `LocalDate` — no `@TypeConverter` method is needed.

The `input` column is typed `String`, so no conversion is required there either.

### Null Input Row

A blank cell in the `Input` column maps to `null` (String reference type). A blank cell in the `Parsed Date?` column maps to `null` (LocalDate reference type). The `assertEquals(null, null)` assertion correctly verifies that `parseDate(null)` returns `null`.

### Exception Table

The exception case uses the built-in JUnit conversion for `Class<? extends Throwable>`: writing the fully-qualified class name `java.lang.IllegalArgumentException` is enough — no custom converter needed. This pattern is shown in the example-patterns reference.

The `throwsOnInvalidInput` table has only one row. This is acceptable because it represents a structurally distinct assertion pattern (exception testing), and additional exception-triggering inputs (e.g., malformed strings) could easily be added as rows in the future.

### No Custom Type Converter Needed

Although the `parseDate` method accepts slash format (`15/01/2024`) and short year (`24-01-15`) as inputs, the *expected output* for all rows is the same `LocalDate` value (`2024-01-15`), expressible in standard ISO-8601 that JUnit handles natively. The non-standard formats are inputs (strings), not outputs, so no converter is required.
