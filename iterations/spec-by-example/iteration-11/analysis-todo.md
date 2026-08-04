# Analysis to-do — spec-by-example, iteration 11

Compared against **iteration 5**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `no-duplicate-rows-within-a-table` — eval-13-shipping-partial-applicability

Grader said: _Table 1: PASS (2 rows, distinct value bounds). Table 2: PASS (3 rows, distinct branches). Table 3: FAIL — 'UK destination | UK | yes' and 'Irish destination | Ireland | yes' both re-show the same 'available' branch instead of using a value set. Table 4: PASS (2 rows, distinct combos)._

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **This is `iteration-70`'s eval-7 regression, on a second skill — the value sets are gone again.** Table 3 writes `UK destination | UK | yes` and `Irish destination | Ireland | yes` as two {{rows}} where `{UK, Ireland}` is one; Table 4 repeats the split.

  **Same cause as eval-7:** `011c959`'s floor (*"When you cut a row, say which surviving row discharges its obligation. If none does, keep it"*) fires, and `4c348e1`'s collapse guard cannot, because its test asks *which neighbouring table's rule names the branch* — and here the branch (destination country) is named by **this** table's own rule, so the test returns nothing and the {{rows}} stay.

  **Two hosts on two skills makes this the closing run's one confirmed regression.** The repair is the same one: state the collapse test so it works inside a single table — does the differing input change *this* table's expectation? If not, value set. 

## LOST `overnight-grouped` — eval-13-shipping-partial-applicability

Grader said: _Table 3 lists 'UK destination | UK | yes' and 'Irish destination | Ireland | yes' as separate rows instead of {UK, Ireland}; Table 4 likewise separates 'UK, low order value | £0.01 | UK | £14.99' and 'Ireland, high order value | £500.00 | Ireland | £14.99'_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Same event as `no-duplicate-rows-within-a-table` above — count once.** This assertion is eval-13's mechanical statement of the same obligation: it asks specifically for `{UK, Ireland}` in one cell, which is precisely the collapse that did not happen.

  **The pairing is the same as eval-7's** (`fewer-than-nine-rows` + `no-duplicate-role-output`): a generic value-set assertion and an eval-specific one, moving together because one guidance failure drives both. **Four slots across two evals and two skills now rest on this single repair.** 

## WON `separates-availability-and-cost` — eval-13-shipping-partial-applicability

Grader said: _'Table 3 — Limits Overnight Shipping To UK And Irish Destinations' (Available?) vs 'Table 4 — Fixes Overnight Shipping At £14.99 For Available Destinations' (Overnight Cost?)_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Won, and it is the decomposition side of the batch's other pattern.** *Table 3 — Limits Overnight Shipping To UK And Irish Destinations* (`Available?`) against *Table 4 — Fixes Overnight Shipping At £14.99 For Available Destinations* (`Overnight Cost?`).

  **Note this eval decomposed correctly while eval-4 in `iteration-9` collapsed** — and the distinguishing feature is the same one: eval-13's shared value is a destination list, eval-4's was a derived **threshold**, which rule 10 names as always-a-column. **Consistent with the reading that the collapse pressure is specifically rule 10's threshold clause**, not a general anti-decomposition drift. 
