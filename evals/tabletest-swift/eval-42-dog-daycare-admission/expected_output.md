The admission table in `AdmissionTests` gains rows for the new rule. No new test function, and the
play-group and daily-fee tables do not change. The tests pass.

## What the change is

`Admission.decision(for:)` already holds the rule: from 12 weeks a dog needs one vaccination, and
under 16 weeks it needs two. The admission table has columns for the age and the count of
vaccinations, so the new rule needs rows only, not a column and not a table.

## The rows

- **The row the rule breaks.** "At the youngest age" (12 weeks, 1 vaccination, `admitted`) fails
  now. It changes: at 12 weeks one vaccination gives `needsVaccination`, and two give `admitted`.
- **The 16-week line, both sides, with one vaccination:** 15 weeks gives `needsVaccination`, 16
  weeks gives `admitted`. A value set such as `{12, 15}` with `needsVaccination` covers the puppy
  weeks, and its end members are the edges.
- **Two vaccinations under 16 weeks** give `admitted`, for example `{12, 15}`.
- The rows under 12 weeks and for no vaccination stay as they are.

## Scenario names

The scaffold's names say where a row stands against the rule: "At the youngest age", "A week under
the youngest age". A new name does the same: "Puppy weeks, before the second dose", "First week
past the puppy weeks". A name that only repeats its row's cells ("15 weeks, one vaccination") adds
nothing to the row. A name that states the decision ("Puppy needs a second dose") states its
row's answer.

## Equally good

The order of the rows, the exact names, and a value set or single rows at the edges. A row for an
adult with two vaccinations is redundant but not wrong.
