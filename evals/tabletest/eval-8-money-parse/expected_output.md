Two `@TableTest` methods over `MoneyParser.parse(String)`: a **valid-parsing** table returning
`Money` and an **error** table asserting the exception per row. `null` is a blank input cell in
the valid table (mapping to a `null` result); the empty string is a quoted `""` in the error
table. `Money` is a `record Money(BigDecimal amount)`, and that representation should shape the
rows — the interesting questions here are about *scale and format*, not about the arithmetic.

## The two concerns

| Table | Ranges over | Expected column |
|---|---|---|
| Valid parsing | well-formed amounts + `null` | `Money?` (or `Result?`) |
| Errors | inputs that must be rejected | `Throws?` — exception type per row (`exception-has-expected-column`) |

Kept separate (`separates-valid-and-invalid`), so no good row carries an empty `Throws?` cell.

## Let `BigDecimal` drive the valid rows

The prompt's examples (`10.00`, `0.01`) are only a starting point. Because the value is a
`BigDecimal`, a reader needs to know what the parser does with *scale* — and the prompt does not
say. Good valid rows go past the two given examples to state those answers:

- `10.00 → Money(10.00)`, `0.01 → Money(0.01)` — the given cases.
- Rows that pin down scale/format assumptions the prompt leaves open (see below).

Note the `BigDecimal` footgun: `new BigDecimal("10.00")` does **not** `.equals` `new
BigDecimal("10.0")`. The expected `Money` in each row therefore also asserts the *scale* the
parser produces — which is itself one of the underspecified points a row should make explicit.

## Underspecified — make the assumption a row, not a silent choice

The spec is a first cut and cannot be clarified interactively, so the expected move is to pick an
interpretation and **show it in a row a reviewer can challenge**, rather than deciding it
silently in the parser stub:

- **Decimal places / scale.** Is `5` (no fraction) valid? `10.5` (one place)? `10.005` (three)?
  What scale does `10` parse to — `10` or `10.00`? Each is a row.
- **Zero.** Negative is rejected and positive is fine, but `0.00` sits on the boundary and the
  prompt never places it. A zero row states whether zero is a valid amount.
- **Alternative formats.** Comma fraction separator (`10,00`), leading `+`, currency symbol
  (`$10.00`), thousands separators, surrounding whitespace — all plausible, none specified. A
  solution that accepts or rejects any of these should have a row saying so.

Omitting a foreseeable case is not penalised; silently baking a choice into the stub with no row
to expose it is the miss.

## Mechanics

- Null is a blank cell in the **valid** table (`null-as-blank-cell`) — not a separate `@Test`,
  not the string `"null"`. Empty string is quoted `""` in the error table. The two must not be
  conflated.
- The parser ships as a stub; tests are expected not to pass. Only `compiles` is asserted — do
  not implement the parsing logic.

## Judging

Two separated tables; an exception column per error row; the null-vs-empty distinction; and
credit for using `BigDecimal`'s scale/format questions to generate explicit boundary rows rather
than copying the two example values and stopping.
