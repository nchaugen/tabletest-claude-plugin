Hand-authored reference answer for eval-14, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

Three `@TableTest` methods against the API the requirement names —
`calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)`:

- `paysEachBandOfHoursAtItsOwnRate` — every rate band, with the 40/41 overtime boundary in the same
  Weekday Hours column. Hour parameters are `Integer` on the calculator itself, so a blank cell
  reaches it as null with no translation in the test at all. One hour of a band at a rate of 10
  prices the band directly. The zero-rate row is a value set over three hour counts that all
  collapse to the same pay.
- `neverPaysLessThanZero` — the floor rule, on the reading that a negative hour count is a timesheet
  correction. Stated as an assumption, with the reason: reject every negative instead and the
  prompt's own floor rule can never be exercised by any row.
- `rejectsANegativeHourlyRate` — the rate boundary, zero accepted against -1 rejected.

There is no classification table and no `splitWeekdayHours`. Where weekday hours stop being regular
is internal to the calculator once it receives raw hours, so a test that reached for it would be
testing an implementation detail the requirement never exposes.

The calculator is a stub throwing `UnsupportedOperationException`, per the prompt, so the tests
compile and fail at run time.
