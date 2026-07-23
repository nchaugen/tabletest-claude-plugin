Two `@TableTest` methods for one underspecified parser: a **valid-parsing** table and an
**error** table. The valid table varies the *input format* while holding the *parsed date*
constant, so format-equivalence is visible at a glance; the error table asserts the exception
per row. `null` is a blank input cell mapping to a `null` result; the empty string is a quoted
`""` cell in the error table. The two are never confused.

## The two concerns

| Table | Ranges over | Result column |
|---|---|---|
| Valid parsing | supported input formats + `null` | `LocalDate` (ISO string maps to it via built-in conversion) |
| Errors | inputs that must be rejected | `Throws?` / exception type per row |

Keeping them separate is the point (`separates-valid-and-invalid`): mixing a `Throws?` column
into the valid table forces every good row to carry an empty exception cell.

## Vary the axis the rule is about

The rule is "*these formats* all parse to the same date". So the valid rows should hold one
target date and vary only the format:

- ISO `2024-01-15`, slash `15/01/2024`, short-year `24-01-15` — all → `2024-01-15`.

Three rows landing on the *same* `LocalDate` state format-equivalence in a way three unrelated
dates cannot. `null` → `null` is the fourth valid row (blank input cell, blank result cell).

## Underspecified — make the assumption a row, not a silent choice

The prompt is a first-cut spec and is deliberately vague in places. A solution cannot ask
clarifying questions here, so the expected move is to **pick an interpretation and make it
visible as an example row** that a reviewer can challenge — not to bury it in the parser stub
or omit it.

- **The two-digit-year century window.** `24 → 2024` is given, but `99 → ?` (1999 or 2099) is
  not. A row such as `99-01-15 → …` makes the chosen window explicit.
- **Anything outside the three formats.** The prompt names only the empty string as an error.
  Whether unsupported junk (`"not a date"`, `2024/13/45`) is rejected or is out of scope is a
  choice; if the solution rejects it, a row should show it.
- **Invalid calendar values** (`2024-13-01`, `2024-02-30`) — foreseeable, out of scope. Omitting
  them is fine; a row inventing behaviour that contradicts the prompt is not.

## Mechanics

- The **input column is a plain `String`** — the parser under test does the parsing, not
  TableTest — so no custom converter is needed for input. The `LocalDate` result column relies
  on built-in ISO conversion; that counts as addressing type conversion.
- Empty string uses quoted `""`; `null` is a blank cell. Preserving that distinction is a
  correctness requirement, not a style point.
- The parser ships as a stub, so the tests are expected **not** to pass yet — only `compiles`
  is asserted. Do not implement the parsing logic to make them green.

## Judging

Two separated tables; format-equivalence shown by holding the date constant; the null/empty
distinction preserved; and credit for surfacing the century-window (and any other) assumption
as an explicit row rather than a silent default.
