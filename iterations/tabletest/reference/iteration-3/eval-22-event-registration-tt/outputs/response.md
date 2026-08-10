Hand-authored reference answer for eval-22, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` § Findings.
Not agent output. Must never be read as a skill measurement.

Two `@TableTest` methods, one per concern:

- `acceptsRegistrationWhenNamePresentAndEmailWellFormed` — name, email and both optional fields in
  columns; registration date and group size held in the method body. A blank cell means the field
  was not supplied, and the table turns on the same notation meaning two different things: an absent
  optional is accepted, an absent name is not. `Error Message?` is blank on every accepted row and
  carries the reason on the rejected ones, so it distinguishes the two rejection rules rather than
  restating `Accepted?`. The wording is this table's assumption — the prompt fixes none — and the
  `@Description` says so.
- `appliesLargerOfEarlyBirdAndGroupDiscount` — registration timing and group size in columns,
  `Discount?` as the rule's direct output with `Price per attendee?` alongside it. Timing is written
  relative to the cutoff through a `@TypeConverter`, so no row carries a literal date.

The service stays the supplied stub, per the prompt, so the tests compile and fail at run time.
