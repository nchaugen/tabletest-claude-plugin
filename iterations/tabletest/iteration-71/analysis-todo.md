# Analysis to-do — tabletest, iteration 71

Compared against **iteration 64**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**1 of 4 evals comparable.** The rest were excluded from them:

- `eval-7-permission-check` — the baseline never ran this eval; there is nothing to compare against
- `eval-14-weekly-pay` — the baseline never ran this eval; there is nothing to compare against
- `eval-25-convert-from-spock` — the baseline never ran this eval; there is nothing to compare against

**5 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `2.18-adult-senior-value-set` — eval-15-reis-discount

Grader said: _Traveler Category | ... {ADULT, SENIOR} | 2026-08-05T12:00:00 | ... | 5_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.9-correctness-value-set-tier-semantics` — eval-15-reis-discount

Grader said: _Ladder table (appliesTheReisLadderFromTripsInTheWindow) uses individual numeric rows like '4', '5', '9', '10', not value sets, so tier value-set semantics are not demonstrated_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `count-derived-from-raw-history` — eval-15-reis-discount

Grader said: _Every history row in countsTripsTowardTheRollingReisTotal is homogeneous (single entry or multiple identical-type entries); none mix counting and non-counting purchases in one history_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `quantifier-covered-by-rows` — eval-15-reis-discount

Grader said: _countsTripsTowardTheRollingReisTotal claims 'Reis applies regardless of zone' (zone domain) but every row uses zoneValidity: ZONE_1 only — FAIL. resolvesWhichDiscountRuleAppliesByTravelerCategory's implicit 'CHILD always gets flat 20% regardless of history' claim is discharged by empty vs full history rows — PASS. Ladder table has no explicit quantifying claim._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `zone-independent-counting` — eval-15-reis-discount

Grader said: _All history rows use zoneValidity: ZONE_1, fixed; independence only asserted in @Description text_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
