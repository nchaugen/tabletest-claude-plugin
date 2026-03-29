Let's call this **"Loan Approval Decision"** and build out the examples.

Here's an initial table covering the rules you've described, with the open question on missing income flagged explicitly:

| Scenario | Applicant Age | Credit Score | Income Status | Min Score (Policy) | Approved? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Standard adult, good score, stable income | 35 | 700 | stable | 650 | yes | |
| Standard adult, score below threshold | 35 | 600 | stable | 650 | no | Credit score below 650 |
| Standard adult, at threshold — not above | 35 | 650 | stable | 650 | no | Credit score below 650 |
| Standard adult, just above threshold | 35 | 651 | stable | 650 | yes | |
| Standard adult, unstable income | 35 | 700 | unstable | 650 | no | Unstable income |
| Just below senior age | 64 | 620 | stable | 650 | no | Credit score below 650 |
| Just at senior age | 65 | 620 | stable | 600 | yes | |
| Senior, score meets senior threshold | 70 | 620 | stable | 600 | yes | |
| Senior, at senior threshold — not above | 70 | 600 | stable | 600 | no | Credit score below 600 |
| Senior, just above senior threshold | 70 | 601 | stable | 600 | yes | |
| Senior, score below senior threshold | 70 | 580 | stable | 600 | no | Credit score below 600 |
| Senior, unstable income | 70 | 620 | unstable | 600 | no | Unstable income |
| Standard adult, income not provided | 35 | 700 | not provided | 650 | **?** | Open: reject (missing = unstable) or block (require info)? |
| Senior, income not provided | 70 | 620 | not provided | 600 | **?** | Open: same question |

---

A few things I want to flag:

**The "above 650" boundary** — I've included rows at 650, 651, 600, and 601. "Above 650" means strictly greater than, so 650 itself is rejected. Please confirm this matches your intent — it's a common source of off-by-one bugs.

**Age 64 vs 65** — The boundary row at age 64 applies the standard threshold, while 65 gets the senior threshold. Worth confirming: is the senior rule `age >= 65`, or `age > 65`?

**Missing income — two paths to decide:**

1. **Treat as unstable** — absence of income info is a rejection, same reason as unstable income. Simpler, slightly harsh.
2. **Block with a different error** — "Income information required" — so the applicant can reapply with the info. More user-friendly but needs a third income state.

Which way does the business want to handle it? Once you decide, I'll fill in those two `?` rows.