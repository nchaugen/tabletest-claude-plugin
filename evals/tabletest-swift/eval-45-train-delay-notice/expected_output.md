`@TableTest` tables inside `DelayNoticeTests` for the notice a departure gets and for the text of
each notice. The tests pass.

## Two rules in one function

`DelayNotice.text(for:)` joins two rules, and the code has the seam between them:

1. **Which notice a departure gets, with what data** — `DelayNotice.notice(for:)`. A delay under
   5 minutes is on time, a delay of 60 minutes or more gives a refund, and a cancelled departure
   names the next one.
2. **How a notice reads as text** — one sentence of 150–210 characters for each of the four
   notices.

## The decision table

The inputs are the delay and whether the departure is cancelled; the expectation is the notice,
one cell as Swift spells it (`delayed(minutes: 25)`, `cancelled(next: 15:05)`), with `Codable` on
`Notice` in the app. Both sides of each boundary: 4 and 5 minutes, 59 and 60 minutes. A cancelled
departure gives `cancelled` whatever its delay, which a value set shows.

## The wording

Each of the four notices is asserted once in full, through `text(for:)` or the text of the notice.
The full sentence comes from the code; a fragment compared with `contains` is a rule the doc comment
must state.

## Also acceptable

A single table over `text(for:)` with the full sentence as the expectation, when the doc comment
says why the seam is not used. The rows are then long, and each still asserts the full text.

## Equally good

The destination, the times and the delays chosen, the titles and the column names.
