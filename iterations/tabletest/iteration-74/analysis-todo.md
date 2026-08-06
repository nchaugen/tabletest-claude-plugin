# Analysis to-do — tabletest, iteration 74

Compared against **iteration 73**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**1 of 2 evals comparable.** The rest were excluded from them:

- `eval-14-weekly-pay` — the baseline never ran this eval; there is nothing to compare against.
  **Compared by hand against `iteration-72` (same digest `8349171b`), which is eval-14's live baseline:
  19/23 → 19/23, one won and one lost.** WON `no-duplicate-rows-within-a-table` (16 {{rows}} → 11).
  LOST `1.11-format-description` — the four tables became three, so the double-time multipliers and the
  40-hour threshold lost their per-table `@DisplayName` home and moved into `@Description`, where the
  grader read them as restating the rows (evidence: *"Assumes a standard 40-hour work week is the
  overtime threshold ... paid at double time regardless of how many hours are worked"*). **This is
  § I's recorded item-6 merge trade recurring, not repair 6** — repair 6's vocabulary appears 0× in
  this narration, and eval-14's hour types are not a multi-class ladder.

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `dimensions-as-list` — eval-25-convert-from-spock

Grader said: _Dimensions given as separate columns 'Length (cm) | Width (cm) | Height (cm)' rather than a single [L,W,H] list column_

- Output: `eval-25-convert-from-spock/outputs/`
- Narration: `eval-25-convert-from-spock/narration.md`
- Raw transcript: `eval-25-convert-from-spock/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): One decision covers all three composite slots (narration:77): *"I'm reconsidering whether to keep region/speed and fragile/insuredValue/handling as separate columns or combine them into composite objects. Since these represent distinct dimensions of ShippingZone and PackageOptions respectively, and **the table focuses on individual fields one at a time rather than all fields together**, it makes sense to leave them as separate typed columns and construct the objects directly in the test method body ... this is standard practice, not hiding table concerns behind converters."* This is the composite-cell gap already recorded as open in § J27 (*varying one field of a composite object in a cell*), now on its fourth host. **Not repair 6.** Repair 6's vocabulary appears 3× in this narration and every hit is on the base-rate ladder; it says nothing about cell shape. Table count is ruled out as the mechanism: it-71 also wrote 8 tables and passed all three slots.

## LOST `options-as-map` — eval-25-convert-from-spock

Grader said: _Options split into separate columns like 'Fragile | Cost?', 'Handling | Cost?', 'Insured Value | Cost?' and no-options rows use blank cells, e.g. 'No special handling | | 7.50'_

- Output: `eval-25-convert-from-spock/outputs/`
- Narration: `eval-25-convert-from-spock/narration.md`
- Raw transcript: `eval-25-convert-from-spock/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Same decision as `dimensions-as-list` above — narration:77, one choice losing three slots. Each surcharge table varies one field of `PackageOptions`, so the agent read a map cell as noise and built the object in the arrange step.

## LOST `options-type-converter` — eval-25-convert-from-spock

Grader said: _No @TypeConverter method exists; PackageOptions is built directly in method bodies, e.g. 'val opts = PackageOptions(handling = handling)'_

- Output: `eval-25-convert-from-spock/outputs/`
- Narration: `eval-25-convert-from-spock/narration.md`
- Raw transcript: `eval-25-convert-from-spock/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Consequence of the same choice — with no map or list cell there is nothing for a `@TypeConverter` to convert. The three slots are one artefact fact, not three.

## WON `scenario-names-describe-conditions` — eval-25-convert-from-spock

Grader said: _Names like 'At the oversize threshold', 'Fragile package with hazmat fee', 'No special handling' describe conditions, not echoing expectation values, and no generic 'Test 1' labels appear_

- Output: `eval-25-convert-from-spock/outputs/`
- Narration: `eval-25-convert-from-spock/narration.md`
- Raw transcript: `eval-25-convert-from-spock/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Not attributable to repair 6 and not claimed for it. This slot has been unstable on this eval — it passed in it-71, failed in it-73, passed here — across three skill states, so it reads as draw variance. The names it rewards (*'At the oversize threshold'*, *'No special handling'*) come from the per-concern tables, which repair 6 did not create.
