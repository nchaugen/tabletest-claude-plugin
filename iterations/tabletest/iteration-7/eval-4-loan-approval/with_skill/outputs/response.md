## Loan Approval Decision

Here's a first draft of the example table, along with the open questions it surfaces.

---

### Loan Approval Decision

| Scenario | Applicant Age | Credit Score | Income Status | Min Score (Policy) | Approved? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Standard approval | 40 | 700 | Stable | 650 | yes | |
| Credit score just above standard threshold | 40 | 651 | Stable | 650 | yes | |
| Credit score at standard threshold | 40 | 650 | Stable | 650 | **?** | Is 650 "above" or must it be 651+? |
| Credit score below standard threshold | 40 | 640 | Stable | 650 | no | Credit score below minimum |
| Unstable income, credit otherwise fine | 40 | 700 | Unstable | 650 | no | Income not stable |
| Senior approval using lower threshold | 70 | 625 | Stable | 600 | yes | |
| Senior: score that would fail standard rule | 65 | 620 | Stable | 600 | yes | |
| Senior: score just above senior threshold | 65 | 601 | Stable | 600 | yes | |
| Senior: score at senior threshold | 65 | 600 | Stable | 600 | **?** | Same ambiguity — ≥600 or >600? |
| Senior: score below senior threshold | 65 | 590 | Stable | 600 | no | Credit score below minimum |
| Just below senior age, mid-range score | 64 | 620 | Stable | 650 | no | Credit score below minimum |
| Just at senior age, same score | 65 | 620 | Stable | 600 | yes | |
| Very low score regardless of age | {30, 70} | 400 | Stable | {650, 600} | no | Credit score below minimum |
| Missing income information | 40 | 700 | Unknown | 650 | **?** | Open — see below |

---

### Open Questions

**1. Are the thresholds inclusive or exclusive?**
The spec says "above 650" — does a score of exactly 650 pass or fail? Same for the senior threshold of 600. This is a boundary that must be pinned down before implementation. Rows at 650 and 600 are marked `?`.

**2. What happens when income information is missing?**
Three plausible behaviours — you need to decide:
- **Reject**: treat unknown as "not stable" (safer/more conservative)
- **Pend**: flag for manual review, no immediate decision
- **Approve conditionally**: pass if credit score is strong enough, pending income verification

This is the most consequential open question. Would a domain expert or product owner be able to answer this?

**3. Does the senior threshold apply to income too, or only to credit score?**
The spec mentions seniors get a lower *credit score* threshold — but is income stability still required, or is that also relaxed for seniors? The current table assumes income stability is still required for seniors. Is that correct?

---

Once these are resolved, this table maps directly to a `@TableTest`. Want to work through any of the open questions, or add more edge cases?