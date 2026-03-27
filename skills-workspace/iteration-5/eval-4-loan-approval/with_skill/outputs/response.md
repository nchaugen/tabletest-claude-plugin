Here's a concrete example table for **Loan Approval Decision**, with the known rules pinned down and the open cases surfaced honestly.

---

## Loan Approval Decision

| Scenario | Age | Credit Score | Min Score (Policy) | Income Status | Approved? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Typical standard approval | 35 | 720 | 650 | Stable | yes | |
| Score too low, standard applicant | 35 | 620 | 650 | Stable | no | Credit score below 650 |
| Unstable income, high score | 35 | 720 | 650 | Unstable | no | Income not stable |
| Both conditions failing | 35 | 580 | 650 | Unstable | no | Credit score below 650, income not stable |
| **At the standard threshold** | 35 | **650** | 650 | Stable | **?** | Is 650 itself approved or rejected? |
| Just above standard threshold | 35 | 651 | 650 | Stable | yes | |
| Senior, above lower threshold | 68 | 630 | 600 | Stable | yes | |
| Senior, score between thresholds | 68 | 640 | 600 | Stable | yes | Would be rejected if not senior |
| Senior, below lower threshold | 68 | 580 | 600 | Stable | no | Credit score below 600 |
| **At the senior threshold** | 68 | **600** | 600 | Stable | **?** | Is 600 itself approved or rejected for seniors? |
| Just at senior age boundary | 65 | 630 | 600 | Stable | yes | |
| Just below senior age, score between thresholds | 64 | 630 | 650 | Stable | no | Not senior, score below 650 |
| **Missing income — standard applicant** | 35 | 720 | 650 | | **?** | Reject? Pend? |
| **Missing income — senior applicant** | 68 | 630 | 600 | | **?** | Same rule as above, or different? |
| **Senior, unstable income** | 68 | 630 | 600 | Unstable | **?** | Does senior threshold override income rule? |

---

## Open Questions

Three things need a decision before implementation:

1. **Boundary inclusivity** — "above 650" — does a score of exactly 650 pass or fail? Same question for seniors at exactly 600. Boundary rows are included above to make this visible.

2. **Missing income** — three plausible behaviours:
   - Reject immediately (treat as unstable)
   - Pend for review (human decision needed)
   - Approve if score is sufficiently high (income becomes optional above some threshold)
   Which is it?

3. **Senior + unstable income** — the lower credit threshold is described as a senior concession, but is stable income still required? Or does the senior rule relax both conditions?

---

Once these three are resolved, the table maps directly to a `@TableTest` — the `Min Score (Policy)` column also signals this is a configurable threshold, not a hardcoded constant.