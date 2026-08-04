# Analysis to-do — spec-by-example, iteration 9

Compared against **iteration 4**, grading claude-sonnet-5/default.

**5 of 5 evals comparable.**

**5 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `question-mark-only-on-outputs` — eval-10-subscription-billing

Grader said: _Table columns include 'Cancelled?' as an input scenario descriptor, not a derived output_

- Output: `eval-10-subscription-billing/outputs/`
- Narration: `eval-10-subscription-billing/narration.md`
- Raw transcript: `eval-10-subscription-billing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **A naming regression, not a structural one, and no repair in the batch targets it.** it-4 named the input `Cancelled During Trial`; it-9 named it `Cancelled?` and kept it an input. The skill is unambiguous — *"The `?` suffix is reserved for output columns"* (`SKILL.md:98`) — and it did not fire.

  **Note the interaction with the loss above:** both movements are on the same eval and both are column-naming/selection decisions in tables the agent restructured. Nothing in the batch touches `?` suffix rules, so record as unattributed drift and watch whether it recurs. 

## LOST `refund-table-shows-proportion` — eval-10-subscription-billing

Grader said: _Refund table columns: Plan Price | Cycle Length (Policy, Days) | Days Remaining in Cycle | Refund Amount? — no proportion/rate column shown._

- Output: `eval-10-subscription-billing/outputs/`
- Narration: `eval-10-subscription-billing/narration.md`
- Raw transcript: `eval-10-subscription-billing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **A standing conflict between the skill and this assertion, not a batch effect.** it-4's prorate table carried `Daily Rate?` as an intermediate expectation column and passed; it-9 dropped it — `Plan Price | Cycle Length (Policy, Days) | Days Remaining in Cycle | Refund Amount?`.

  **The skill instructs the drop, in as many words:** *"A column that is constant down every row, or that changes only as a side effect of another column, is not being tested … Move it to the table whose axis varies it, and drop it here rather than keeping it 'for completeness'."* `Daily Rate?` moves only as a side effect of plan price and cycle length, so the rule catches it exactly.

  **That passage predates this batch** (`07b7d01`), so this is salience drift on standing guidance, not a repair's cost. **The finding is the conflict itself: the skill's drop-the-side-effect-column rule and `refund-table-shows-proportion` cannot both be satisfied.** Resolve on the assertion side or carve out derived-rate columns; do not repair blind. 

## LOST `refund-table-includes-loyalty-dimension` — eval-12-subscription-loyalty-trial

Grader said: _Table 3b columns: 'Amount Paid For Cycle | Unused Days | Cycle Length (Days) | Exception Applied? | Refund Amount?' - no explicit loyalty column_

- Output: `eval-12-subscription-loyalty-trial/outputs/`
- Narration: `eval-12-subscription-loyalty-trial/narration.md`
- Raw transcript: `eval-12-subscription-loyalty-trial/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **The cost side of the win recorded below — one decision, two verdicts.** Table 3b is `Amount Paid For Cycle | Unused Days | Cycle Length (Days) | Exception Applied? | Refund Amount?` with no loyalty column, because loyalty was given its own table (Table 2).

  **That is the same "move it to the table whose axis varies it" rule that cost eval-10 its `Daily Rate?` column** — applied here to the loyalty dimension. The assertion wants loyalty visible *in the refund table*; the skill says a dimension another table owns does not belong here. **Count this and `separates-pricing-trial-loyalty-refund` as one event with opposite signs, not as a net-zero pair of independent findings.** 

## WON `separates-pricing-trial-loyalty-refund` — eval-12-subscription-loyalty-trial

Grader said: _Table 1 — Grants Free Trial Only on the Monthly Plan; Table 2 — Applies the Loyalty Discount Only to Annual Plans; Table 3a — Decides Whether the 24-Hour No-Refund Exception Applies; Table 3b — Prorates the Refund by Unused Days_

- Output: `eval-12-subscription-loyalty-trial/outputs/`
- Narration: `eval-12-subscription-loyalty-trial/narration.md`
- Raw transcript: `eval-12-subscription-loyalty-trial/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Won by the decomposition that lost `refund-table-includes-loyalty-dimension` above.** Four tables, each on one concern: *Grants Free Trial Only on the Monthly Plan*, *Applies the Loyalty Discount Only to Annual Plans*, *Decides Whether the 24-Hour No-Refund Exception Applies*, *Prorates the Refund by Unused Days*.

  **Worth setting beside eval-4's loss in this same run.** Here decomposition is rewarded and the agent decomposed; there decomposition is rewarded and the agent collapsed. **The difference is that eval-4's shared value was a threshold, which rule 10 names as always-a-column, and eval-12's was a discount dimension, which it does not.** That is the sharpest available statement of where the collapse pressure comes from. 

## LOST `concerns-decomposed` — eval-4-loan-approval

Grader said: _Only one table 'Determines Loan Approval Eligibility' mixing age, credit score, and income rules_

- Output: `eval-4-loan-approval/outputs/`
- Narration: `eval-4-loan-approval/narration.md`
- Raw transcript: `eval-4-loan-approval/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **The same event as `iteration-69`'s eval-14, on a second skill — and rule 10's threshold-as-column guidance is the mechanism.** it-4 wrote **two** tables: *Determine Minimum Credit Score Threshold by Age* (classification) and *Approve Loan Application* (decision). it-9 wrote **one**, with the derived threshold as a column — `Min Credit Score (Policy)` — carried down all eight {{rows}}.

  **This is rule 10 working exactly as written**: *a constant the outcome depends on is a column wherever it can be one — and a value the rule turns on, such as a threshold or a limit, always can be*. Applying it to a threshold that another table **derived** dissolves that table into a column. **The rule never says what to do when the constant is itself a derived output**, and that is the gap.

  `011c959`'s floor is visible in the same output — the response carries a *"Why these rows and no others"* section discharging each {{row}}'s obligation by name, which is the commit's instruction followed literally.

  **Third host of this trade in the closing run** (eval-14 `iteration-69`, eval-25 `iteration-70` vs it-50, eval-4 here). Not variance. 
