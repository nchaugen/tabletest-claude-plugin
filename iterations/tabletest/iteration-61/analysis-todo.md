# Analysis to-do — tabletest, iteration 61

Compared against **iteration 52**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `premium-claim-boundary` — eval-18-convert-from-code

Grader said: _No row in the table has Claim Count = 1 at all; rows only use claims 0 or 5, so no 0-vs-1 claim pair at a shared age exists (only rows: RENEWAL/40/0, RENEWAL/760/0, NEW-RENEWAL/9/5, NEW-RENEWAL/19/5, NEW/64/0, NEW/65/0)._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Row economy overshooting again — the third eval this batch where consolidating dropped a
  coverage row.** iteration-52 wrote 8 rows including the pair `RENEWAL | 30 | 0 | AUTO_APPROVED` and
  `RENEWAL | 30 | 1 | APPROVED`, which *is* this assertion: a 0-vs-1 claim pair at a shared age.
  iteration-61 writes 6 rows and that pair is gone, along with the plain `NEW | 30 | 0` row; claim
  count now appears only as 0 or 5. What it gained instead is `{NEW, RENEWAL}` value sets on the two
  risk-threshold rows — a correct application of rule 08's per-table judgement (§ G item 4) that
  saved no rows, because iteration-52 had only `NEW` there.
  **The generalisable finding, and it is one step past what landed today:** rule 07's new bullet says
  a formula behind the tiers does not reduce the *tiers*. This is the same agent behaviour one level
  down — claim count is an input to the risk-score formula, so the agent sampled it (0, 5) instead of
  straddling it (0, 1). **A formula does not reduce the boundaries either.** Not landed; the run was
  bought as regression evidence and the repair wants its own decision.

## LOST `rule-statable-from-table` — eval-18-convert-from-code

Grader said: _evaluatesApplication: FAIL — rows 'age 9/claims 5 -> APPROVED' vs 'age 19/claims 5 -> REJECTED' mix age and claims in a way whose operation (risk-score threshold) cannot be named from columns/cells alone, since no risk-score value or formula is published anywhere._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Caused by removing the internals from `@Description`, and the two assertions are in direct
  tension on this eval.** iteration-52 published the whole formula — *"Risk score = (age / 10,
  integer division) + (claim count * 15) … rejected above 75 … senior applicants pay 200 + risk score
  * 3.5"* — which is what let a reader state the rule from the table, and it is also why
  `description-no-internals` failed there. iteration-61 drops the formula, and the grader can no
  longer name the operation behind `age 9/claims 5 → APPROVED` against `age 19/claims 5 → REJECTED`.
  **Publishing the formula satisfies one assertion and violates the other**, so this eval cannot pass
  both as currently written. That is an eval-design conflict, not a skill defect — and it is the
  underlying seam problem B3 named (the risk score is not observable, so no column can carry it).
  Note also that `docs/assertion-triage.md:194` records `rule-statable-from-table`/18 as a slot the
  `high` grader got wrong and whose key entry had to be authored from artefacts. **Treat this loss as
  unattributable to the batch.**
