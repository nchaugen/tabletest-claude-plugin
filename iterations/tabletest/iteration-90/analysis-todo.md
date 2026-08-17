# Analysis to-do — tabletest, iteration 90

Compared against **iteration 89**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**6 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `black-box-columns` — eval-18-convert-from-code

Grader said: _Column header 'Rejection Threshold (Risk Score)' and 'Senior Age Threshold' expose internal risk-score/threshold as columns_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concern-not-over-split` — eval-18-convert-from-code

Grader said: _Each of the four @TableTest methods uses a distinct fixture/focus (renewal auto-approval, auto-approval-vs-rejection interaction, risk threshold boundary, premium formula by age) rather than splitting one same-fixture concern across tables._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `description-no-internals` — eval-18-convert-from-code

Grader said: _"Age 760 alone drives the internal risk score past the rejection threshold." explicitly names internal risk score_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `observable-io-only` — eval-18-convert-from-code

Grader said: _'Rejection Threshold (Risk Score)' column names the internal risk score directly_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `rule-statable-from-table` — eval-18-convert-from-code

Grader said: _Method2 uses 'Age 760' engineered from the hidden risk formula with no formula published; Method3's 'Rejection Threshold (Risk Score) | 75' column gives only the threshold, not the age/claims->score operation, so the decision flip between age 5 and 10 can't be named from headers/values alone._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `scenario-names-describe-conditions` — eval-18-convert-from-code

Grader said: _'Same high-risk profile with a claim is rejected' echoes the Decision? cell value REJECTED_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
