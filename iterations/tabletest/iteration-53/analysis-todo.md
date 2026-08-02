# Analysis to-do — tabletest, iteration 53

Compared against **iteration 52**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**6 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `2.17-zone-irrelevance-visible` — eval-15-reis-discount

Grader said: _No Zone column appears in discountForPurchaseNumber or calculatesDiscount tables where the percentage is decided_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.18-adult-senior-value-set` — eval-15-reis-discount

Grader said: _'Senior reaches the first discount on their fifth ticket' is a separate row from the Adult row, not a value set_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.9-correctness-value-set-tier-semantics` — eval-15-reis-discount

Grader said: _Ladder table uses discrete boundary rows (4,5,9,10,39,40,45) rather than value sets, so no cross-tier contamination occurs_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `held-constants-declared` — eval-15-reis-discount

Grader said: _'Purchase time is fixed at 2026-08-02T10:00:00 for every row' declared in @Description; defaults 'category and zone keys (default ADULT / ZONE_1)' also declared._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `window-boundary-uses-purchase-time` — eval-15-reis-discount

Grader said: _All rows use integer 'daysAgo' (e.g. 30, 31); no sub-day/hour distinction like 29d23h vs 30d1h_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `zone-independent-counting` — eval-15-reis-discount

Grader said: _'Purchase in zone 1 counts' | 1 and 'Purchase in zone 3 counts the same' | 1 rows in the frequency table_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
