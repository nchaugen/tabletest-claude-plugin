# Analysis to-do — tabletest, iteration 72

Compared against **iteration 71**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**1 of 2 evals comparable.** The rest were excluded from them:

- `eval-25-convert-from-spock` — generation failed (timeout or crash), so this eval produced no answer to compare — re-run it before reading anything into the gap

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `1.11-format-description` — eval-14-weekly-pay

Grader said: _"This formula backs both Sunday pay and holiday pay, since both are always paid at double time regardless of the category." adds context, not just restating rows_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **repair 3's revert, confirmed.** At 6 tables (it-71) Sunday and holiday pay
  sat in separate thin tables, so neither description could say anything beyond its own rows. At 4
  tables they share `computesDoubleTimePay`, and the description states the shared rule — both are
  double time regardless of category. The slot returned because the fusion gave it something to say.

## WON `concern-not-over-split` — eval-14-weekly-pay

Grader said: _Each table addresses a genuinely distinct concern (weekday pay, double-time pay, rate rejection, total weekly pay) rather than fragmenting one concern across tables._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **repair 3's revert, confirmed, and it lands the bound nothing had held.**
  Table count went 2 (it-69) → 6 (it-71) → **4** (it-72). it-69 failed `concerns-decomposed` at 2
  tables; it-71 failed `concern-not-over-split` at 6. **it-72 is the first eval-14 run to pass both
  ends at once.** The four tables are weekday pay, double-time pay, rate rejection and the weekly
  total — one per concern, no component-per-table fragmentation.

## Still failing, causes recorded (not this run's regressions)

- **`no-duplicate-rows-within-a-table` — NOT repair-3 collateral.** The duplicate is *inside* one
  table: `computesWeekdayPay` carries `Well past the overtime threshold | 48 | 15 | 780`, which the
  grader reads as re-showing the overtime arithmetic already fixed by the 40/41 pair. That is rule
  06's own named false positive — "a value further past the same boundary" — and it is a {{row}}
  count, not a table count. Falsifies § J23's prediction 2 for this slot.
- **`1.14-depth-zero-rate` — NOT repair-3 collateral either, and it points at a live gap.** No
  {{row}} anywhere shows zero-rate pay with non-zero hours; `rejectsNegativeHourlyRate` holds all
  three hour columns at 0, so a zero rate is only ever exercised for rejection. The held-at-zero
  shape survived the revert, so repair 3's text was not its only source.
- **`separates-classification-and-calculation` — failed in it-69, it-71 and it-72 alike.** This is
  the slot repair 3 was written to win, and it never moved in either direction. Reverting cost
  nothing here, which is § J19's "bought nothing measurable anywhere" confirmed from the other side.
- **`1.6-readability-empty-cells` — failed in all three runs.** Blank-vs-0 for irrelevant hour
  columns plus `double` rather than `Integer` params. Untouched by anything in this batch.
