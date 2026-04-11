## Loan Approval Decision

Here's a starting table based on the rules you've described, with the open question about missing income flagged explicitly.

| Scenario | Age | Credit Score | Income Status | Approved? | Rejection Reason? |
|---|---|---|---|---|---|
| Standard approval | 40 | 700 | Stable | yes | |
| Credit score not above threshold | 40 | 650 | Stable | no | Credit score below 651 |
| Credit score just above threshold | 40 | 651 | Stable | yes | |
| Unstable income, good credit | 40 | 700 | Unstable | no | Unstable income |
| Both credit and income failing | 40 | 600 | Unstable | no | Credit score below 651 |
| Non-senior with credit between thresholds | 64 | 625 | Stable | no | Credit score below 651 |
| Senior approval with lower threshold | 70 | 625 | Stable | yes | |
| Senior credit score not above senior threshold | 70 | 600 | Stable | no | Credit score below 601 |
| Senior credit score just above senior threshold | 70 | 601 | Stable | yes | |
| Senior with unstable income | 70 | 625 | Unstable | no | Unstable income |
| Age boundary — qualifies as senior | 65 | 625 | Stable | yes | |
| Age boundary — just below senior | 64 | 625 | Stable | no | Credit score below 651 |
| Missing income info | 40 | 700 | (missing) | ? | **Open: reject or treat as unstable?** |
| Senior, missing income info | 70 | 625 | (missing) | ? | **Open: same rule as non-senior?** |

---

A few things I want to flag for discussion:

**The boundary rows (650 vs 651, 600 vs 601):** The rule says "above 650" and "above 600" — I've taken that to mean strictly greater than. Rows at exactly 650 and 600 are rejected. Worth confirming this is intentional, not "at least 650".

**The age boundary (65):** Same question — does 65 qualify as senior, or does it start at 66? I've assumed 65 counts.

**Missing income:** Two open questions here. The simplest approaches are:
- Treat missing as unstable → reject (safe/conservative)
- Reject with a distinct reason ("Income information required") so callers know to prompt for it
- Allow a separate "pending" state while income is verified

Which direction feels right for your use case?

**"Both failing" row:** When both credit score and income are bad, I've shown credit score as the rejection reason — but is there a priority, or should both be reported?