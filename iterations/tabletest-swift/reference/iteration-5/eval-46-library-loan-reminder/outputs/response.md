Hand-authored reference answer for eval-46, from `products/claude-plugin/plans/slice-13-swift-skill-field-feedback.md` step 5. Not agent output. Must never be read
as a skill measurement.

`text(for:)` decided the reminder and rendered it in one `switch`, so a table could reach the
boundaries only through the text. I added the key to the app: a `Reminder` enum and
`LoanReminder.reminder(for:)`, which holds the decision. `text(for:)` now calls it and switches
over the result. No text and no boundary changed.

One table for `reminder(for:)`, with both sides of the 3-day, the due-date and the 30-day line,
and a value set where the reserved flag does not change the reminder. One plain test shows that
`text(for:)` renders the decided reminder. The wording itself is for a reader to review.
