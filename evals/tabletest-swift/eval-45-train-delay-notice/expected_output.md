A `@TableTest` inside `DelayNoticeTests` for the notice a departure gets, and one plain test that
shows that the text is the rendering of the decided notice. The tests pass.

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

A person reads the text, so the wording is for a reader to review, not a rule to test. A test that
compares a constant sentence with a copy of it checks only that the two copies agree. One plain
`@Test` through `text(for:)` is the wiring check: it shows that the text renders the notice the
departure gets. It may assert one sentence in full, split over source lines, or compare with the
rendering of the decided notice. No table cell holds a sentence. A fragment compared with
`contains` is a rule the doc comment must state.

## Not acceptable here

A table over `text(for:)` with the full sentence as the expectation. The code has the seam, so the
boundaries belong in the table of `notice(for:)`, and one long cell makes a table wide.

A plain test for each of the four sentences. Each restates a constant.

## Equally good

The destination, the times and the delays chosen, the titles and the column names. The form of the
wiring check: one sentence in full, or a comparison with the rendering of the decided notice. A
remark in the answer that the texts are in the code, and that keys would separate them.
