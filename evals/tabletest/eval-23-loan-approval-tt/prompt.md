A Gradle Java project has been created for this feature.

Under `src/main/java/com/example/loan`, the project already contains a stub `LoanEvaluator` and `ApprovalResult`. The evaluator is not implemented yet.

We want to write tests before implementing the decision logic.

Write TableTests for `LoanEvaluator.evaluateLoan(...)`.

Use the existing API where income information can be unknown, so `hasStableIncome` is `Boolean`.

The rules are:
- Customers are approved if their credit score is above 650 and they have stable income
- Senior applicants (65+) get approved with a lower threshold of 600
- Missing income information (null) should result in a 'PENDING_REVIEW' status
- Below-threshold scores are rejected regardless of income

It is OK to keep a stub implementation for the evaluator so the tests compile, but do not implement the approval logic yet.
