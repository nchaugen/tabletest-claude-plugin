# Analysis to-do — tabletest variant=next, iteration 4

Compared against **official (iterations 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 80, 79, 78, 77, 76, 75, 74, 73, 72, 71, 70, 69, 68, 67, 66, 65, 64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40 merged)**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**5 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `black-box-columns` — eval-18-convert-from-code

Grader said: _Columns are 'Scenario | Applicant Type | Age | Claim Count | Decision? | Premium?' in both tables - no internal state columns._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concern-not-over-split` — eval-18-convert-from-code

Grader said: _bypassesEvaluationForZeroClaimRenewals and decidesOutcomeAndPremiumFromRiskAndAge address genuinely distinct concerns (renewal bypass vs risk/age path), not the same fixture split by sub-rule_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `observable-io-only` — eval-18-convert-from-code

Grader said: _No column named riskScore, internalRiskScore, or hasActivePolicy appears in either @TableTest header._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `rule-falsifiable-by-a-row` — eval-18-convert-from-code

Grader said: _bypassesEvaluationForZeroClaimRenewals: Decision? varies AUTO_APPROVED/APPROVED PASS, Premium? varies 0/138.0/108.0 PASS. decidesOutcomeAndPremiumFromRiskAndAge: Decision? varies APPROVED/REJECTED PASS, Premium? varies 166.0/250.0/0/112.0/221.0/382.0 PASS. No column constant, no verbatim copy of input, no fixed-value implementation would satisfy all rows._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-18-convert-from-code

Grader said: _Names like 'Just past the risk-score rejection limit' and 'At the senior age threshold' describe conditions, not outcomes._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
