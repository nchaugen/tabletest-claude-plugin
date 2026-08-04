# Analysis to-do — spec-by-example, iteration 10

Compared against **iteration 3**, grading claude-sonnet-5/default.

**2 of 2 evals comparable.**

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `scenario-names-describe-conditions` — eval-21-event-registration-sbe

Grader said: _'Neither discount applies' row has Discount Applied? = None, echoing the result_

- Output: `eval-21-event-registration-sbe/outputs/`
- Narration: `eval-21-event-registration-sbe/narration.md`
- Raw transcript: `eval-21-event-registration-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **This one is the flip, and the baseline proves it.** it-3 wrote `Neither applies` and **passed**; it-10 wrote `Neither discount applies` and **failed**. The names are the same shape — the added noun does not change whether the name echoes the expectation — so the same input got both verdicts.

  `docs/assertion-triage.md` records this slot at 3 flips across three identical re-grades, the worst in the suite. **It also moved in the opposite direction on eval-24 in this very run** (see below), which is the signature of a flip rather than a change. **Not attributed; no repair indicated.**

  **Contrast with `iteration-70`'s eval-22, where the same slot lost and it was *not* a flip:** there the name genuinely changed, from it-52's condition-style `On the early-bird cutoff, small group` to `Neither discount applies`. Same assertion, same phrase, two different situations — which is why this slot has to be judged against the baseline text every time. 

## WON `scenario-names-describe-conditions` — eval-24-weekly-pay-sbe

Grader said: _Names like 'Below the threshold', 'Mixed week combining all categories', 'Correction hours drive the raw total negative' describe situations, not echoing expectation values._

- Output: `eval-24-weekly-pay-sbe/outputs/`
- Narration: `eval-24-weekly-pay-sbe/narration.md`
- Raw transcript: `eval-24-weekly-pay-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **The other half of the flip above — same assertion, same run, opposite direction, net zero.** The names the grader quoted (*Below the threshold*, *Mixed week combining all categories*, *Correction hours drive the raw total negative*) are good condition names, but the baseline's were not materially worse.

  **Two movements of one slot in one run, in opposite directions, is the instrument talking, not the skill.** Do not bank this win; do not repair the loss. 
