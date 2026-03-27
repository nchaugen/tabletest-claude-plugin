Here's a worked-out example table for **Loan Approval Decision**:

| Scenario | Age | Credit Score | Income Status | Approved? | Rejection Reason? |
|---|---|---|---|---|---|
| Standard approved customer | 40 | 700 | stable | yes | |
| Credit score below standard threshold | 40 | 620 | stable | no | Credit score below 650 |
| Unstable income | 40 | 700 | unstable | no | Unstable income |
| Both credit low and unstable | 40 | 600 | unstable | no | Credit score below 650; unstable income |
| Credit score just above standard threshold | 40 | 651 | stable | yes | |
| Credit score at standard threshold | 40 | 650 | stable | **?** | Is 650 approved or rejected? (boundary) |
| Senior approved at lower threshold | 70 | 620 | stable | yes | |
| Senior credit below lower threshold | 70 | 580 | stable | no | Credit score below 600 |
| Senior at lower threshold exactly | 70 | 600 | stable | **?** | Is 600 approved or rejected? (boundary) |
| Senior with unstable income | 70 | 620 | unstable | no | Unstable income |
| Age boundary — exactly 65 | 65 | 620 | stable | **?** | Does 65 count as senior? |
| Just below senior age | 64 | 620 | stable | no | Credit score below 650 |
| Missing income — standard applicant | 40 | 700 | missing | **?** | Treat as unstable? Reject? Require field? |
| Missing income — senior applicant | 70 | 620 | missing | **?** | Same open question |

---

**Four open questions to resolve:**

1. **Boundary inclusivity** — are the thresholds `> 650` / `> 600` (strict) or `>= 650` / `>= 600` (inclusive)? The rows at exactly 650 and 600 hinge on this.

2. **Senior age boundary** — does 65 qualify as senior, or is the rule `> 65` (i.e., 66+)?

3. **Missing income — single rule or two?** When income info is absent, does the system:
   - Reject (treat absence as unstable),
   - Require the field (validation error before approval logic runs), or
   - Is the behaviour different for seniors vs. standard applicants?

4. **Multiple rejection reasons** — when both credit and income fail, does the system return both reasons or just the first? The row "both credit low and unstable" surfaces this.

---

Once you've resolved the open questions, this table maps directly to a `@TableTest`. Would you like to walk through any of these cases with the domain expert, or shall we fill in the blanks and move to implementation?