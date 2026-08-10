Hand-authored reference answer for eval-14, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` § Findings.
Not agent output. Must never be read as a skill measurement.

Four `@TableTest` methods:

- `splitsWeekdayHoursAtTheOvertimeThreshold` — classification only, no pay arithmetic. The overtime
  threshold is a column because it is policy the split does not take as an argument.
- `paysEachBandOfHoursAtItsOwnRate` — pay through the API the requirement names: the calculator
  receives weekday, Sunday and holiday hours plus the rate, and applies the overtime threshold
  itself rather than taking pre-split hours from the caller. Hour columns are `Integer`,
  so a band a row is not about is a **blank cell rather than a 0**, and one hour of each band at a
  rate of 10 prices the band directly. The zero-rate row is a value set over three hour counts that
  all collapse to the same pay.
- `neverPaysLessThanZero` — the floor rule, on the reading that a negative hour count is a timesheet
  correction. Stated as an assumption, with the reason: reject every negative instead and the
  prompt's own floor rule can never be exercised.
- `rejectsANegativeHourlyRate` — the rate boundary, zero accepted against -1 rejected.

The calculator is a stub throwing `UnsupportedOperationException`, per the prompt, so the tests
compile and fail at run time.
