## Loan Approval Decision

**Assumptions** (since I can't confirm live, stating them so they're easy to challenge):
- "Above" means strictly greater than — a score exactly at the threshold does **not** qualify.
- "Senior (65+)" is inclusive: age 65 already gets the lower threshold.
- Both rules apply with AND: income must be stable regardless of age tier; only the credit-score threshold changes for seniors.
- Missing income information is genuinely undecided — I've left it as an open row rather than guessing a behavior.

| Scenario | Applicant Age | Credit Score | Income Stability | Required Credit Score (Policy) | Approved? | Reason? |
|---|---|---|---|---|---|---|
| Standard applicant, meets both criteria | 40 | 700 | stable | 650 | yes | |
| Standard applicant, score at threshold | 40 | 650 | stable | 650 | no | Credit score not above 650 |
| Standard applicant, just above threshold | 40 | 651 | stable | 650 | yes | |
| Unstable income overrides credit score | 40 | {600, 750} | not stable | 650 | no | Income not stable |
| Senior applicant, score at reduced threshold | 65 | 600 | stable | 600 | no | Credit score not above 600 |
| Senior applicant, just above reduced threshold | 65 | 601 | stable | 600 | yes | |
| Senior applicant, mid-range score only qualifies due to age | 65 | 620 | stable | 600 | yes | |
| Just below senior age, standard threshold still applies | 64 | 620 | stable | 650 | no | Credit score not above 650 (standard threshold applies under 65) |
| Senior applicant with unstable income | 70 | 700 | not stable | 600 | no | Income not stable |
| **Missing income information** | 40 | 700 | *(missing)* | 650 | **?** | **Open — see below** |

**Open questions:**
- **Missing income info**: does the system (a) reject outright, (b) treat as "not stable" and reject, or (c) request the info before deciding (i.e., can't reach a decision at all)? This changes both the `Approved?` value and whether a distinct "pending" outcome needs its own output value.
- Does the same missing-income question apply to senior applicants too, or could seniors get a pass on income info entirely? (I'd assume not, but worth confirming — the rule as stated ties income stability to *all* applicants, not just standard-tier ones.)

**Notes on design choices:**
- `Required Credit Score (Policy)` is included even though it only takes two values (650/600) — it makes the age-tier rule legible directly from the table instead of requiring the reader to infer it from `Reason?`.
- The unstable-income row uses a value set `{600, 750}` for Credit Score to show the rule holds *regardless* of score once income is unstable — one row instead of two near-duplicates.
- Age 64 vs. 65 and the two credit-score boundary pairs (650/651, 600/601) pin down the exact thresholds rather than just showing "high" and "low" examples.

Once the missing-income behavior is settled, I can fill in that row and this is ready to hand to `/tabletest` if you want it turned into an executable test.