Hand-authored reference answer for eval-23, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

One `@TableTest`, seven rows, against `LoanEvaluator.evaluateLoan(age, creditScore, stableIncome)`.
`Stable Income` is `Boolean` as the prompt requires, so a blank cell is genuinely unknown income.

Every row is a one-input delta against another row, so each rule is the difference between a pair
rather than something the reader has to infer:

- rows 1/2 and 3/4 bracket the two thresholds (650/651 and 600/601), fixing each cut strictly above,
  and the two cuts sitting at different scores is what states the age rule;
- rows 2/5/6 hold age and score at 64/651 and vary only income: true, false, unknown;
- row 7 differs from row 6 in **credit score alone**, which is where the precedence is settled: the
  same unknown income that gives pending at a qualifying score gives rejection below the threshold.

A row isolating age alone (a senior at 650, approved, against row 1's rejection) was drafted and
**cut**: the only reading it kills — that 650 is a floor seniors must clear too — is already dead by
row 4 approving a senior at 601. A delta is worth having where a rule needs establishing, never as a
presentation of a rule other rows have already established.

No invalid-input table. The requirement says nothing about negative ages or out-of-range scores, and
inventing validation rules is the defect the traceability assertions exist to discourage; the
`@Description` records the one interpretive choice that *is* forced.

The evaluator stays the supplied stub, per the prompt, so the tests compile and fail at run time.
