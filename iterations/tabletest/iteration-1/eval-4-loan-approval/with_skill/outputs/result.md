# Loan Approval Decision

| Scenario                                        | Credit Score | Minimum Score (Policy) | Applicant Age | Stable Income? | Approved? | Rejection Reason?               |
|-------------------------------------------------|--------------|------------------------|---------------|----------------|-----------|---------------------------------|
| Standard applicant, strong credit               | 700          | 650                    | 40            | yes            | yes       |                                 |
| Standard applicant, credit just above threshold | 651          | 650                    | 40            | yes            | yes       |                                 |
| Standard applicant, credit at threshold         | 650          | 650                    | 40            | yes            | yes       |                                 |
| Standard applicant, credit just below threshold | 649          | 650                    | 40            | yes            | no        | Credit score below 650          |
| Standard applicant, low credit score            | 580          | 650                    | 40            | yes            | no        | Credit score below 650          |
| Standard applicant, no stable income            | 700          | 650                    | 40            | no             | no        | No stable income                |
| Standard applicant, both conditions fail        | 600          | 650                    | 40            | no             | no        | Credit score below 650          |
| Senior applicant, credit above senior threshold | 620          | 600                    | 65            | yes            | yes       |                                 |
| Senior applicant, credit just above threshold   | 601          | 600                    | 65            | yes            | yes       |                                 |
| Senior applicant, credit at senior threshold    | 600          | 600                    | 65            | yes            | yes       |                                 |
| Senior applicant, credit just below threshold   | 599          | 600                    | 65            | yes            | no        | Credit score below 600          |
| Senior applicant, no stable income              | 620          | 600                    | 65            | no             | no        | No stable income                |
| Just below senior age, uses standard threshold  | 649          | 650                    | 64            | yes            | no        | Credit score below 650          |
| Just at senior age, uses lower threshold        | 649          | 600                    | 65            | yes            | yes       |                                 |
| Senior, score between thresholds, no income     | 630          | 600                    | 70            | no             | no        | No stable income                |
| Standard applicant, income info missing         | 700          | 650                    | 40            |                | ?         | Open: is missing income treated as unstable, or does it cause an error? |
| Senior applicant, income info missing           | 620          | 600                    | 67            |                | ?         | Open: same question for senior applicants |
