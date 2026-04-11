# Loan Approval Decision

| Scenario                                        | Applicant Age | Credit Score | Standard Threshold (Policy) | Senior Threshold (Policy) | Income Status | Approved? | Rejection Reason?           |
|-------------------------------------------------|---------------|--------------|----------------------------|--------------------------|---------------|-----------|-----------------------------|
| Standard applicant, strong profile              | 40            | 720          | 650                        | 600                      | Stable        | yes       |                             |
| Standard applicant, score just above threshold  | 40            | 651          | 650                        | 600                      | Stable        | yes       |                             |
| Standard applicant, score exactly at threshold  | 40            | 650          | 650                        | 600                      | Stable        | no        | Credit score not above 650  |
| Standard applicant, score below threshold       | 40            | 580          | 650                        | 600                      | Stable        | no        | Credit score not above 650  |
| Standard applicant, unstable income             | 40            | 720          | 650                        | 600                      | Unstable      | no        | Income not stable           |
| Standard applicant, both conditions fail        | 40            | 580          | 650                        | 600                      | Unstable      | no        | Credit score not above 650  |
| Senior applicant, meets senior threshold        | 70            | 620          | 650                        | 600                      | Stable        | yes       |                             |
| Senior applicant, score just above senior threshold | 65        | 601          | 650                        | 600                      | Stable        | yes       |                             |
| Senior applicant, score exactly at senior threshold | 65        | 600          | 650                        | 600                      | Stable        | no        | Credit score not above 600  |
| Senior applicant, score above standard threshold | 70           | 700          | 650                        | 600                      | Stable        | yes       |                             |
| Senior applicant, between senior and standard threshold | 70    | 630          | 650                        | 600                      | Stable        | yes       |                             |
| Senior applicant, unstable income               | 70            | 620          | 650                        | 600                      | Unstable      | no        | Income not stable           |
| Applicant, age exactly 65 (senior boundary)     | 65            | 620          | 650                        | 600                      | Stable        | yes       |                             |
| Applicant, age 64 (just below senior boundary)  | 64            | 620          | 650                        | 600                      | Stable        | no        | Credit score not above 650  |
| Standard applicant, income info missing         | 40            | 720          | 650                        | 600                      |               | ?         | Open: treat as unstable, or reject with separate reason, or require income info before deciding? |
| Senior applicant, income info missing           | 70            | 620          | 650                        | 600                      |               | ?         | Open: same question as above — does missing income behave the same for seniors? |

## Open Questions

1. **Missing income info**: The behaviour when income information is absent has not been agreed. Three plausible interpretations exist:
   - Treat missing income as unstable — reject with "Income not stable"
   - Reject with a distinct reason — "Income information required"
   - Block the application before it reaches the approval rule — income becomes a required field upstream

   The last two rows mark this as open (`?`). A decision is needed before implementation.

2. **"Stable income" definition**: The table uses `Stable` / `Unstable` as concrete domain values, but the definition of stable income (e.g. employed full-time vs. self-employed vs. on benefits) may need its own table if the approval logic distinguishes those cases.

3. **Senior boundary**: The table assumes age 65 and above qualifies as senior. The row "age exactly 65" makes this explicit. Confirm this is correct (≥ 65, not > 65).
