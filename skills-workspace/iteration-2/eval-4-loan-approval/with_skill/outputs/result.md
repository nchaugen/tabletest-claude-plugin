# Loan Approval Decision

| Scenario                                        | Customer Age | Credit Score Threshold (Policy) | Senior Threshold (Policy) | Senior Age (Policy) | Credit Score | Income Status | Approved? | Rejection Reason?            |
|-------------------------------------------------|--------------|---------------------------------|---------------------------|---------------------|--------------|---------------|-----------|------------------------------|
| Standard approval — all criteria met            | 40           | 650                             | 600                       | 65                  | 700          | Stable        | yes       |                              |
| Credit score exactly at standard threshold      | 40           | 650                             | 600                       | 65                  | 650          | Stable        | no        | Credit score too low         |
| Credit score one above standard threshold       | 40           | 650                             | 600                       | 65                  | 651          | Stable        | yes       |                              |
| Credit score below standard threshold           | 40           | 650                             | 600                       | 65                  | 600          | Stable        | no        | Credit score too low         |
| Unstable income regardless of credit score      | 40           | 650                             | 600                       | 65                  | {700, 800}   | Unstable      | no        | Income not stable            |
| Senior applicant — approved under lower threshold | 68         | 650                             | 600                       | 65                  | 620          | Stable        | yes       |                              |
| Senior applicant — credit score exactly at senior threshold | 68 | 650                          | 600                       | 65                  | 600          | Stable        | no        | Credit score too low         |
| Senior applicant — credit score one above senior threshold | 68 | 650                           | 600                       | 65                  | 601          | Stable        | yes       |                              |
| Senior applicant — unstable income              | 68           | 650                             | 600                       | 65                  | 620          | Unstable      | no        | Income not stable            |
| Applicant exactly at senior age boundary        | 65           | 650                             | 600                       | 65                  | 620          | Stable        | yes       |                              |
| Applicant just below senior age boundary        | 64           | 650                             | 600                       | 65                  | 620          | Stable        | no        | Credit score too low         |
| Missing income information — standard applicant | 40           | 650                             | 600                       | 65                  | 700          |               | ?         | Open question — see notes    |
| Missing income information — senior applicant   | 68           | 650                             | 600                       | 65                  | 620          |               | ?         | Open question — see notes    |

## Open Questions

**Missing income information (rows 12–13):** The rules state that stable income is required for approval, but what happens when income data is absent entirely is not specified. Two interpretations are possible:

- **Conservative (reject):** If income cannot be verified, treat it as not stable and reject with a reason such as "Income information missing". This is the safer default for a lending context.
- **Permissive (refer):** Route the application to manual review rather than giving an automatic decision.

A decision is needed before implementation. Until resolved, these rows are marked `?`.

## Notes on Design

- The three policy thresholds (standard credit score threshold 650, senior credit score threshold 600, senior age 65) are shown as explicit columns so that the rules are readable from the table alone and the boundary rows are naturally visible. In implementation these would likely be configuration values, not hardcoded constants.
- The row "Unstable income regardless of credit score" uses a value set `{700, 800}` to express that the credit score does not affect the outcome when income is unstable — two rows that differ only in an irrelevant column are avoided.
- The boundary rows for credit score (at threshold = rejected, one above = approved) pin down that the rule is strictly greater than, not greater than or equal to.
- The boundary row for senior age (exactly 65 = senior, 64 = standard threshold applies) pins down that the rule is age >= 65.
