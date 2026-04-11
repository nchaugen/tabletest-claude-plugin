Great case for Spec by Example — there's a senior-applicant rule, two thresholds, and an open question around missing income. Let me build the table from what you've described.

---

## Loan Approval Decision

| Scenario | Credit Score | Applicant Age | Income Status | Min Score (Threshold) | Approved? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Standard approval | 700 | 40 | stable | 650 | yes | |
| Score just above standard threshold | 651 | 40 | stable | 650 | yes | |
| Score at standard threshold | 650 | 40 | stable | 650 | yes | |
| Score just below standard threshold | 649 | 40 | stable | 650 | no | Credit score too low |
| Senior approval above senior threshold | 680 | 65 | stable | 600 | yes | |
| Senior score just above senior threshold | 601 | 65 | stable | 600 | yes | |
| Senior score at senior threshold | 600 | 65 | stable | 600 | yes | |
| Senior score just below senior threshold | 599 | 65 | stable | 600 | no | Credit score too low |
| Senior with score above standard threshold | 700 | 65 | stable | 600 | yes | |
| Senior age boundary — just under 65 | 620 | 64 | stable | 650 | no | Credit score too low |
| Senior age boundary — exactly 65 | 620 | 65 | stable | 600 | yes | |
| Unstable income, good credit | 720 | 40 | unstable | 650 | no | Unstable income |
| Unstable income, senior | 650 | 65 | unstable | 600 | no | Unstable income |
| **Missing income, good credit** | 720 | 40 | **?** | 650 | **?** | **?** |
| **Missing income, senior** | 650 | 65 | **?** | 600 | **?** | **?** |

---

**Open questions to resolve:**

1. **Missing income** — three realistic options:
   - Reject outright ("income verification required")
   - Treat as unstable/rejected
   - Allow manual review (a third outcome beyond approved/rejected)
   
   What's the business intent here? Can an applicant even submit without income info, or is that a validation error before approval logic runs?

2. **Senior threshold boundary** — is age 65 checked as `age >= 65` (so 65 qualifies) or `age > 65`? The table assumes `>= 65` — confirm this is right.

3. **Both conditions must hold** — the table shows unstable income always rejects regardless of credit score. Is that correct, or does a very high score (say 800) ever override income instability?

Once you resolve the missing income rows, this table is ready to hand off to `/tabletest` for implementation.