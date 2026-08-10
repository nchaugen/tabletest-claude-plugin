# Analysis to-do — tabletest, iteration 86

Compared against **iteration 80**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**6 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `2.15-ticket-count-uses-value-sets` — eval-15-reis-discount

Grader said: _Most ladder rows enumerate single boundary values like '5|5', '9|5', '10|10' instead of grouping tiers via value sets._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.18-adult-senior-value-set` — eval-15-reis-discount

Grader said: _'Adult or senior reaching the first Reis tier | {ADULT, SENIOR} | 5 | 5'_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.20-readability-one-row-per-tier` — eval-15-reis-discount

Grader said: _'Reaches the first tier | 5 | 5' and 'Stays at the first tier | 9 | 5' split the same tier into two rows._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.21-readability-relative-time` — eval-15-reis-discount

Grader said: _'[[daysAgo: 0, ticketType: SINGLE]]' uses relative days-ago rather than absolute dates._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `window-boundary-uses-purchase-time` — eval-15-reis-discount

Grader said: _Rows only distinguish 'daysAgo: 30' vs 'daysAgo: 31' — whole-day granularity, no sub-day pair._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `zone-independent-counting` — eval-15-reis-discount

Grader said: _'the conversion fixes them to ADULT and ZONE_1' in SingleTicketTravelCountTest's @Description/@TypeConverter._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
