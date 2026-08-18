# Analysis to-do — tabletest variant=next, iteration 2

Compared against **official (iterations 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 80, 79, 78, 77, 76, 75, 74, 73, 72, 71, 70, 69, 68, 67, 66, 65, 64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40 merged)**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**6 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `black-box-columns` — eval-18-convert-from-code

Grader said: _Scenario | Applicant Type | Age | Claim Count | Decision? | Premium?_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concern-not-over-split` — eval-18-convert-from-code

Grader said: _Only a single @TableTest method (decidesTheOutcomeAndPremiumForAnApplication) exists; no duplicate same-fixture tables to collapse._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `no-duplicate-rows-within-a-table` — eval-18-convert-from-code

Grader said: _decidesTheOutcomeAndPremiumForAnApplication FAIL: 'High-risk senior applicant' (New,70,5→REJECTED) restates the direction already fixed by the age 9/10 boundary pair (rows 6-7), adding no new obligation_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `observable-io-only` — eval-18-convert-from-code

Grader said: _Columns are Applicant Type, Age, Claim Count, Decision?, Premium? — no riskScore or hasActivePolicy column_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `rule-falsifiable-by-a-row` — eval-18-convert-from-code

Grader said: _Method decidesTheOutcomeAndPremiumForAnApplication: Decision? column PASS (values vary: AUTO_APPROVED, APPROVED, REJECTED...); Premium? column PASS (values vary: 0.0,138.0,108.0,250.0,112.0,221.0...); no cell is a verbatim copy of an input cell, and no fixed value would satisfy all rows._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-18-convert-from-code

Grader said: _Names like 'Renewal with no claims', 'Just below the senior pricing threshold' describe conditions, not outcomes_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
