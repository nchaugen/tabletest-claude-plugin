# TableTest for `parseDate` — Design Explanation

## Why two tables?

The five scenarios split into two distinct assertion shapes:

| Scenario         | Assertion shape                |
|------------------|-------------------------------|
| ISO format       | `assertEquals(expected, result)` |
| Slash format     | `assertEquals(expected, result)` |
| Short year       | `assertEquals(expected, result)` |
| Null input       | `assertEquals(null, result)`     |
| Empty string     | `assertThrows(...)`              |

A `@TableTest` method must use the same assertion logic for every row. Because the empty-string case throws and the others return a value, they cannot share one table. Two tables give each scenario the right assertion without any `if`/`switch` in the test body.

## Table 1 — Valid inputs (including null)

```
Scenario        | Input      | Parsed Date?
ISO format      | 2024-01-15 | 2024-01-15
Slash format    | 15/01/2024 | 2024-01-15
Short year      | 24-01-15   | 2024-01-15
Null input      |            |
```

- **Input column** (`String`): A blank cell maps to `null` (the null-input scenario). The other values are raw strings passed directly to `parseDate`.
- **Parsed Date? column** (`LocalDate`): All three valid inputs produce `2024-01-15`. JUnit's built-in converter handles ISO-format `LocalDate` values (`2024-01-15`) in the expectation column automatically. A blank cell maps to `null` for the null-input row.
- No `@TypeConverter` is needed: the input column is `String` (no conversion), and the expectation column uses ISO format which JUnit resolves natively.

## Table 2 — Exception-throwing inputs

```
Scenario      | Input | Exception?
Empty string  | ''    | java.lang.IllegalArgumentException
```

- `''` is the TableTest syntax for an empty string (blank cell would mean null).
- The `Exception?` column holds a fully-qualified class name; JUnit's built-in `Class<?>` converter resolves it automatically.
- `assertThrows` verifies both that an exception is thrown and that it is of the declared type.
- The table has only one row. A single-row `@TableTest` is appropriate here because it is structurally consistent with Table 1 and makes it easy to add more invalid-input cases later (e.g., whitespace-only, garbage strings).

## Why no `@TypeConverter` is needed

The SKILL.md notes that a `@TypeConverter` is required when non-ISO date strings appear **as table column values that TableTest must convert to a typed parameter**. In this test:

- The non-ISO strings (`15/01/2024`, `24-01-15`) appear in the **Input** column, which has type `String` — no conversion needed.
- The **Parsed Date?** expectation column only ever holds ISO-format dates (`2024-01-15`) or blank (null), both of which JUnit handles natively.

If the expectation column needed to express expected dates in a non-ISO format, a `@TypeConverter` returning `LocalDate` would be required.

## Quality checklist

- [x] Multiple rows: Table 1 has 4 rows; Table 2 has 1 row (justified by structural consistency)
- [x] Black-box design: columns are observable inputs and outputs
- [x] Clear communication: scenario names describe input conditions, not outcomes
- [x] Uniform assertions within each table
- [x] Straightforward methods: no `if`/`switch`, no parsing in test body
- [x] Parameter alignment: `(String input, LocalDate expectedDate)` matches column order
- [x] Valid syntax: `''` for empty string, blank cells for null
- [x] Expectation columns use `?` suffix
- [x] Concrete values: `2024-01-15` is directly traceable across all three valid-input rows
