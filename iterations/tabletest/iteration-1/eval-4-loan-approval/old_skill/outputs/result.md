# Loan Approval Decision

| Scenario                                        | Credit Score | Credit Score Threshold (Policy) | Applicant Age | Income Status  | Approved? | Rejection Reason?               |
|-------------------------------------------------|--------------|---------------------------------|---------------|----------------|-----------|---------------------------------|
| Standard applicant, above threshold             | 700          | 650                             | 40            | Stable         | yes       |                                 |
| Standard applicant, below threshold             | 600          | 650                             | 40            | Stable         | no        | Credit score below 650          |
| Standard applicant, at threshold                | 650          | 650                             | 40            | Stable         | no        | Credit score below 650          |
| Standard applicant, just above threshold        | 651          | 650                             | 40            | Stable         | yes       |                                 |
| Senior applicant, above senior threshold        | 620          | 600                             | 65            | Stable         | yes       |                                 |
| Senior applicant, below senior threshold        | 580          | 600                             | 65            | Stable         | no        | Credit score below 600          |
| Senior applicant, at senior threshold           | 600          | 600                             | 65            | Stable         | no        | Credit score below 600          |
| Senior applicant, just above senior threshold   | 601          | 600                             | 65            | Stable         | yes       |                                 |
| Just below senior age, standard threshold used  | 620          | 650                             | 64            | Stable         | no        | Credit score below 650          |
| Standard applicant, unstable income             | 700          | 650                             | 40            | Unstable       | no        | Income not stable               |
| Senior applicant, unstable income               | 620          | 600                             | 65            | Unstable       | no        | Income not stable               |
| Standard applicant, income info missing         | 700          | 650                             | 40            |                | ?         | Open: reject or hold pending?   |
| Senior applicant, income info missing           | 620          | 600                             | 65            |                | ?         | Open: reject or hold pending?   |
| Standard applicant, score and income both absent| 500          | 650                             | 40            | Unstable       | no        | Credit score below 650          |

## Open Questions

- **Missing income information** (rows 12–13): It is not yet decided whether a missing income field causes an automatic rejection ("Rejected: income not verified") or places the application on hold pending further information. This needs a policy decision before implementation.
- **Senior age threshold**: Is 65 the threshold (applicants aged 65 and over qualify for the lower score threshold), or must the applicant be older than 65? Rows above treat 65 as inclusive — confirm with the domain owner.
- **Score thresholds are exclusive**: The table treats "above 650" as strictly greater than 650 (i.e. a score of exactly 650 does not qualify). Confirm whether the rule is `> 650` or `>= 650`.
