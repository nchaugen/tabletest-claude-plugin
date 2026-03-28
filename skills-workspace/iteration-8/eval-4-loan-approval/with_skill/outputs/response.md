Let me work through this systematically, building up the examples from the core rules outward to boundaries and the uncertain cases.

---

## Loan Approval Decision

| Scenario | Applicant Age | Credit Score | Min Score (Policy) | Stable Income | Approved? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Standard applicant, clear approval | 40 | 720 | 650 | yes | yes | |
| Standard applicant, score just above threshold | 40 | 651 | 650 | yes | yes | |
| Standard applicant, score at threshold (not above) | 40 | 650 | 650 | yes | no | Credit score not above 650 |
| Standard applicant, score below threshold | 40 | 580 | 650 | yes | no | Credit score too low |
| Standard applicant, no stable income | 40 | 720 | 650 | no | no | No stable income |
| Senior applicant, benefits from lower threshold | 65 | 630 | 600 | yes | yes | |
| Senior applicant, score just above senior threshold | 65 | 601 | 600 | yes | yes | |
| Senior applicant, score at senior threshold (not above) | 65 | 600 | 600 | yes | no | Credit score not above 600 |
| Senior applicant, score below senior threshold | 65 | 580 | 600 | yes | no | Credit score too low |
| Senior applicant, no stable income | 65 | 630 | 600 | no | no | No stable income |
| Senior applicant, high score regardless of threshold | 70 | 720 | 600 | yes | yes | |
| Standard applicant, missing income info | 40 | 720 | 650 | | **?** | |
| Senior applicant, missing income info | 65 | 630 | 600 | | **?** | |

---

## Open Questions

**1. Missing income info (rows 12–13):** This is the key unresolved case. Three reasonable policies exist — you need to pick one:

| Option | Outcome | Rationale |
|---|---|---|
| Reject (treat as unstable) | no | Conservative: absence of proof = proof of absence |
| Pend for review | pending / referred | Kick to manual underwriting |
| Reject with specific reason | no | "Income information required" — distinct from "unstable income" |

**Recommendation:** If this feeds into a manual review queue, "referred" is a real outcome worth modelling. If there's no review queue, rejection with a clear reason is the safest default. *Which path does your process support?*

**2. Boundary wording — "above 650":** The table currently treats 650 as a rejection (strictly greater than). If the real rule is "650 or above", rows 3 and 8 flip to `yes`. Worth confirming with the business.

**3. Age boundary — "senior is 65+":** Does an applicant who is exactly 65 qualify for the senior threshold? The table assumes yes (65+). Confirming this prevents an off-by-one in implementation.

**4. Both conditions fail simultaneously:** E.g., a senior with score 580 and no income — the table currently only covers one failing condition at a time. Rejection reason would need to list both, or prioritise one. Usually low-risk to decide late, but worth noting.

---

Once you've resolved the income question especially, this table is ready to hand off to `/tabletest` for implementation — the column structure maps directly.