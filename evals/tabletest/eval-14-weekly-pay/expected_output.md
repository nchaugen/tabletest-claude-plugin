Weekly pay for hourly employees, test-first against a stub. **The calculator receives the hours the
prompt says it receives** — weekday, Sunday and holiday — plus the rate, and applies the 40-hour
overtime threshold itself. Where weekday hours stop being regular is internal to it, so a solution
that makes the calculator take *pre-split* regular and overtime hours has moved the policy onto its
caller and no longer implements the feature the prompt describes. A pay table and a **rejection**
table are the two the feature needs; a further table is welcome where it carries a rule of its own
(the pay floor, say), and a separate classification table is optional rather than expected — see
below. The substance the tables must communicate is the *rate structure* — the multiplication itself
is uninteresting and should be kept transparent.

## What the tables are really about

The interesting content is **which band an hour falls into and what multiplier it carries** — the
40-hour overtime boundary, and the 1.5× / 2× premiums. The `hours × rate` arithmetic is not where
the risk is; a table should show it happening but never make the reader do sums to see the rule.
Two devices keep it transparent:

- **Choose a wage that makes the multiplier legible** — a rate of 1 or 10, so `1 Sunday hour @ 10
  → 20` reads as "2×" directly. State a plain "1 normal hour @ rate" either as a `@Description`
  example or as the rate column itself.
- **Show each band's premium in rows a reader can compare**, not in prose. One weekday hour at 10
  pays 10, one hour past the threshold adds 15, one Sunday hour pays 20 — the multipliers are then
  readable off the cells with no sums to do. A separate classification table is one way to make the
  band logic visible and is perfectly acceptable, but it is **not** required, and a solution without
  one must not be marked down for it: with the calculator taking raw hours, classification has no
  observable surface of its own.

## The rate structure, in rows not prose

The multipliers are the rule, so they live in the **rows**, not in the `@Description`. A reader
should be able to read `normal = 1×, overtime = 1.5×, Sunday = 2×, holiday = 2×` straight off the
example rows without a prose restatement. Reference valid rows (a Sunday/Holiday cell is *blank*,
not 0, when that band is not exercised — `1.6-readability-empty-cells`, which needs `Integer`
parameters, not `int`):

| Scenario | Weekday | Sunday | Holiday | Rate | … |
|---|---|---|---|---|---|
| No hours worked | 0 |  |  | 10 | pay 0 |
| One normal hour | 1 |  |  | 10 | shows 1× |
| Full regular week | 40 |  |  | 10 | overtime boundary (at) |
| One hour of overtime | 41 |  |  | 10 | overtime boundary (just over) |
| Heavy overtime | 100 |  |  | 10 | 40 regular + 60 overtime |
| Single Sunday hour |  | 1 |  | 10 | shows 2× |
| Single holiday hour |  |  | 1 | 10 | shows 2× |
| All bands at once | 41 | 1 | 1 | 10 | composition of every rate |

The 40/41 pair (`1.3-depth-overtime-boundary`) shows where overtime *starts*; the all-bands row
(`1.4-depth-combined-scenario`) shows the bands compose rather than shadow each other.

## Rate edge cases — both belong in examples

The prompt's "total pay cannot go below zero" and "a negative rate is rejected" are really about
the **rate**, and both boundaries should be explicit rows:

- **Zero rate is allowed** and yields zero pay for any hours — make it a row
  (`1.14-depth-zero-rate`), ideally a value set over several hour shapes at rate 0, since they all
  collapse to 0 (`1.9-correctness-value-set-semantics`).
- **Negative rate is rejected** — a rejection row with an expected-exception column
  (`1.2-error-has-expected-column`).

## Underspecified — negative hours must be shown, not silently chosen

The prompt does **not** say what happens when an hour count is negative. Several readings are
defensible: reject the whole hour sheet as invalid; floor pay at zero; or let a negative slot
offset a positive one. **None is "the" answer.** What the eval rewards is that the solution
*commits to one and makes it visible as an example row a reviewer can challenge*
(`1.5-depth-error-edge-cases`) — not that it picks a particular semantics, and not that it buries
the decision in a helper with no row to expose it. The reference solution treats negative hours in
any slot as invalid input and puts them in the rejection table with an expected exception; a
solution that instead floors pay at zero and *shows a row proving it* is equally acceptable.

## Mechanics and format

- The method body stays arrange/act/assert: null-to-zero for blank Sunday/holiday cells goes
  through a `@TypeConverter` or private helper, never an inline `if`/ternary
  (`1.15-format-clean-method`).
- Columns use business language — `Weekday hours`, `Sunday hours`, `Hourly rate`, `Weekly pay?`
  — not `weekdayHrs` or `param1`.
- Scenario names describe the work pattern ("Five hours overtime", "Full Sunday shift"), not
  outcomes or "Test case 1".
- Annotation order `@DisplayName`, `@Description`, `@TableTest`; a multi-line `@Description` uses
  a text block. `@Description` adds only context the rows cannot show (what a "week" is, currency,
  rounding) and must **not** restate the multipliers the rows already demonstrate.
- No implementation — the calculation is a stub.

## Judging

Score the visibility of the rate structure first: the 40/41 boundary, the 2× premiums shown by a
legible wage, the all-bands composition, and zero-vs-negative rate as distinct rows. **Do not score
the number of tables** — how the solution divides its concerns is `concerns-decomposed`'s and
`concern-not-over-split`'s business, and a calculator taking the raw hours the prompt names has no
classification step to put in a table of its own. Then the underspecified negative-hours choice being made *explicit in a row*,
whatever choice it is. Arithmetic must be internally consistent with the rule the rows state
(`1.8-correctness-expected-values`).
