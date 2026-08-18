# Analysis to-do — tabletest, iteration 89

Compared against **iteration 88**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**1 of 4 evals comparable.** The rest were excluded from them:

- `eval-14-weekly-pay` — definition changed since the baseline; the two verdicts are not commensurable
- `eval-25-convert-from-spock` — the baseline never ran this eval; there is nothing to compare against
- `eval-30-order-splitting-tt` — the baseline never ran this eval; there is nothing to compare against

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `black-box-columns` — eval-18-convert-from-code

Grader said: _Columns: 'Scenario | Applicant Type | Age | Claim Count | Decision? | Premium?' — all public I/O, no internal fields._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `description-no-internals` — eval-18-convert-from-code

Grader said: _@Description text like 'isolating the risk-based rejection boundary from that rule' names the rule without exposing 'age/10 + claims*15' or numeric thresholds._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `observable-io-only` — eval-18-convert-from-code

Grader said: _No column named hasActivePolicy, internalRiskScore, or riskScore appears in any table header._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `premium-charge-is-per-claim` — eval-18-convert-from-code

Grader said: _Premium rows found: (RENEWAL,30,0,0),(RENEWAL,30,1,136),(NEW,30,0,106),(NEW,0,5,250),(NEW,10,5,0),(NEW,64,2,172),(NEW,65,2,326) - no age has three consecutive claim counts 0,1,2._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
