# Analysis to-do — tabletest, iteration 69

Compared against **iteration 57**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `1.11-format-description` — eval-14-weekly-pay

Grader said: _"Weekday hours are assumed to be able to go negative (e.g. a correction to a prior week)..." adds rationale beyond rows_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 10's held-constants surface fired, and the narration says so
  verbatim.** it-57's `combinesHoursIntoWeeklyPay` description restated the rate multipliers —
  *"Regular hours are paid at the base hourly rate; overtime is 1.5x and Sunday and holiday hours are
  2x"* — which is exactly what 1.11 forbids. it-69's two descriptions carry apparatus instead: the
  40-hour threshold held fixed rather than parameterised, and the negative-hours assumption. The
  narration reaches the second one by rule 10's route: *"the rate-validation rule doesn't actually
  make claims about hours … I'll hold them at a fixed valid value like zero and note that in the test
  description"*. **Coupled to the collapse below, not independent of it** — the descriptions only had
  apparatus to carry because the single 4-arg entry point forced hour columns into the rate table.

## WON `1.14-depth-zero-rate` — eval-14-weekly-pay

Grader said: _Zero hourly rate | 45 | 8 | 8 | 0 | 0_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **A second-order effect of the collapse, and the clearest one.** it-57 had a
  zero-rate row, but it sat in `rejectsNegativeHourlyRate` (`Zero hourly rate | 0 |`) with the hours
  pinned at `40, 0, 0, 0` inside the method body — so the table showed that zero rate does not throw,
  never that pay is zero. 1.14 demands pay be shown. it-69's single pay table carries the rate as a
  column, so `Zero hourly rate | 45 | 8 | 8 | 0 | 0` states the outcome. **The body-pinned `40, 0, 0,
  0` it-57 wrote is precisely what rule 10 forbids**, so the same reshape that wins 1.11 wins this.

## LOST `concerns-decomposed` — eval-14-weekly-pay

Grader said: _calculatesWeeklyPayFromHoursAndRate mixes band classification (30/40/41 boundary rows) with pay calculation (multiplication results) in one monolithic table instead of splitting concerns._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Four tables became two, by two separate decisions — and only the second
  has a batch fingerprint.** (1) it-57 invented a production seam, `PayCalculator.splitWeekdayHours`
  returning `HoursSplit`, and gave it its own table; it-69 refused to, and the narration is explicit:
  *"the overtime classification is an internal implementation detail that shouldn't leak into the
  test table structure"*, *"acceptable for a single integrated formula rather than a multi-stage
  process with a distinct intermediate result"*. **That is a reading of the prompt's API contract**
  (*"will receive employee hours (weekday, Sunday, holiday) and their hourly rate"*), not of any
  rule — nothing in the batch names the invent-a-seam case either way. (2) it-57's third table,
  `floorsWeeklyPayAtZero`, was folded in: *"Since flooring is part of the same computation rule
  rather than a separate operation, I should fold it directly into Table A"* — **that is rule 14's
  shape**, and it is the one decision here the batch plausibly drove. **Not attributed to a specific
  repair**: rule 14 predates this batch, and `concern-not-over-split` passes in both runs, so the
  fold bought nothing measurable while contributing to this loss.

## LOST `separates-classification-and-calculation` — eval-14-weekly-pay

Grader said: _calculatesWeeklyPayFromHoursAndRate table shows both '40-hour overtime threshold' classification rows (30/40/41) and pay totals like '415' in the same table/method._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Same event as `concerns-decomposed`, decision (1): no classification
  seam, so no classification table.** Count this once, not twice.

  **The assertion is reachable without inventing a seam, so this is not an eval conflict** — and the
  agent found the route and dropped it: *"testing through the top-level function with rate=1 actually
  does expose this classification implicitly"*. A `Weekday Hours | Weekly Pay?` table at rate 1
  (30/40/41) plus a combination table would satisfy both slots through the four-arg entry point. It
  weighed that against inventing `splitWeekdayHours` and chose neither. **Nothing in the skill states
  the rate-1 route**, which is the gap worth recording: the guidance says separate the concerns and
  says do not invent seams the spec did not ask for, and never says how to do both. 
