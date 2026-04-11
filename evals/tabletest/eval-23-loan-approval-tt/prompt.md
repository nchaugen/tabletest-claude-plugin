Write TableTests for a loan approval feature. The method signature is:

```java
ApprovalResult evaluateLoan(int customerAge, int creditScore, boolean hasStableIncome)
```

The rules:
- Customers are approved if their credit score is above 650 and they have stable income
- Senior applicants (65+) get approved with a lower threshold of 600
- Missing income information (null) should result in a 'PENDING_REVIEW' status
- Below-threshold scores are rejected regardless of income

Write comprehensive TableTests covering these rules.
