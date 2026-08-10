# Analysis to-do — tabletest, iteration 87

Compared against **iteration 86**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**6 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `2.1-decomposition-concern-separation` — eval-15-reis-discount

Grader said: _looksUpTheDiscountForTicketCount takes 'int ticketCount' directly (no history derivation), while appliesTheFlatDiscountForChildren/calculatesTheDiscountForASinglePurchase decide scheme by category separately._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.15-ticket-count-uses-value-sets` — eval-15-reis-discount

Grader said: _'Tickets 1 to 4 | {1, 4} | 40 | 0' and similar rows use min/max value sets rather than enumerating every boundary._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.17-zone-irrelevance-visible` — eval-15-reis-discount

Grader said: _Zone | {ZONE_1, ZONE_2, ZONE_3} used in calculatesTheDiscountForASinglePurchase for both child and adult rows_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.18-adult-senior-value-set` — eval-15-reis-discount

Grader said: _Rows 'Adult reaches...' and 'Senior also accumulates...' are separate rows, not a {ADULT, SENIOR} value set_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.20-readability-one-row-per-tier` — eval-15-reis-discount

Grader said: _'Tickets 1 to 4 | {1, 4} | 40 | 0' — one row per tier with a value set for ticket counts_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `scheme-derived-once` — eval-15-reis-discount

Grader said: _calculatesTheDiscountForASinglePurchase again uses rows 'Child ignores the zone...' / 'Adult reaches the ladder's first discount tier' / 'Senior also accumulates...' re-deriving the same category-based routing already tested in appliesTheFlatDiscountForChildren._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
