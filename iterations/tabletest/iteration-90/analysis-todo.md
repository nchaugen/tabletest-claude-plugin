# Analysis to-do — tabletest, iteration 90

Compared against **iteration 89**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `black-box-columns` — eval-18-convert-from-code

Grader said: _Column header 'Rejection Threshold (Risk Score) | 75' exposes internal risk-score threshold as a table column._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `description-no-internals` — eval-18-convert-from-code

Grader said: _'The risk score is computed internally from age and claim count... lands exactly at, then one point past, the rejection threshold.' exposes internal formula details._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `observable-io-only` — eval-18-convert-from-code

Grader said: _'Rejection Threshold (Risk Score)' column directly names the internal risk score concept._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `scenario-names-describe-conditions` — eval-18-convert-from-code

Grader said: _'High-risk renewal with no claims still auto-approves' and 'Same high-risk profile with a claim is rejected' state the outcome directly._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
