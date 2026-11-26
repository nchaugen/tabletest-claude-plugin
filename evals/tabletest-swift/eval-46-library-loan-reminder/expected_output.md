A function in the app that decides the reminder a loan gets, which `text(for:)` then calls. A
`@TableTest` inside `LoanReminderTests` for that function, and one plain test that shows that the
text is the rendering of the decided reminder. `text(for:)` gives the same text as before. The
tests pass.

## Two rules in one function, and no seam between them

`LoanReminder.text(for:)` joins two rules in one `switch`:

1. **Which reminder a loan gets** — not due yet (4 days or more), due within 3 days (0 to 3),
   overdue (-1 to -29) and blocked (-30 or less). Within 3 days, a reserved item cannot be
   renewed. The reserved flag changes nothing in the other tiers.
2. **How a reminder reads as text** — one sentence for each reminder, with the title in it.

The code has no function or type for the first rule, so a table can reach the boundaries only
through the text. That is the missing seam.

## The change to the app

Add the key, and keep what `text(for:)` does. A `Reminder` enum (or another name) with one case
for each reminder, and a function or computed property that decides it, such as
`LoanReminder.reminder(for:)`. `text(for:)` calls it and switches over the result. No text
changes, and no localisation layer: language files or a String Catalog are a change the user did
not ask for. The answer says what it changed in the app and why.

## The decision table

The inputs are the days until the due date and whether the item is reserved; the expectation is
the reminder, one cell (`Codable` or `TableTextDecodable` on the enum, in the app or as a test
conformance). Both sides of each line: 4 and 3, 0 and -1, -29 and -30. A value set shows that
the reserved flag does not change the reminder outside the 3-day tier.

## The wording

A person reads the text, so a reader reviews the wording. One plain `@Test` through `text(for:)`
is the wiring check: it shows that the text renders the reminder the loan gets. No table cell
holds a sentence, and no test restates each sentence.

## Not acceptable here

- A table over `text(for:)` with the full sentence as the expectation.
- A new deciding function that `text(for:)` does not call. The decision is then in two places,
  and the tests do not test the code that runs.
- A change to the text or to a boundary, also to correct wording that looks wrong.
- A plain test for each sentence.

## Equally good

The names of the enum, its cases and the deciding function; a static function or a computed
property on `Loan`; the titles and the column names; the form of the wiring check (one sentence in
full, or a comparison with the rendering of the decided reminder).
