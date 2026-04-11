Let me build out the example table with you, surfacing the decisions that need resolving — including the missing income question.

---

## Loan Approval Decision

| Scenario | Applicant Age | Credit Score | Income Status | Min Score (Policy) | Approved? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Standard applicant, comfortably above threshold | 40 | 700 | Stable | 650 | yes | |
| Standard applicant, below threshold | 40 | 600 | Stable | 650 | no | Credit score below 650 |
| Standard applicant, just above threshold | 40 | 651 | Stable | 650 | yes | |
| Standard applicant, at the threshold exactly | 40 | 650 | Stable | 650 | ? | Is 650 approved or rejected? |
| Standard applicant, unstable income | 40 | 700 | Unstable | 650 | no | Unstable income |
| Senior applicant, above senior threshold | 68 | 620 | Stable | 600 | yes | |
| Senior applicant, below senior threshold | 68 | 590 | Stable | 600 | no | Credit score below 600 |
| Senior applicant, at senior threshold exactly | 68 | 600 | Stable | 600 | ? | Is 600 approved or rejected for seniors? |
| Senior applicant, above standard threshold | 68 | 700 | Stable | 600 | yes | |
| Senior applicant, unstable income | 68 | 620 | Unstable | 600 | no | Unstable income |
| Applicant exactly age 65 | 65 | 620 | Stable | ? | ? | Is 65 senior or standard? |
| Standard applicant, income not provided | 40 | 700 | Not provided | 650 | ? | Open question |
| Senior applicant, income not provided | 68 | 620 | Not provided | 600 | ? | Open question |
| Below threshold regardless of age | {30, 68} | 500 | Stable | {650, 600} | no | Credit score below threshold |

---

## Open Questions to Resolve

**1. Credit score thresholds — strictly above, or at-or-above?**
The rule says "above 650" which reads as strictly greater than (651 passes, 650 fails). Is that the intent, or should 650 itself pass?

**2. Senior age boundary — is 65 included?**
"65+" normally means >= 65, so someone aged exactly 65 gets the 600 threshold. Worth confirming explicitly.

**3. Missing income — three plausible decisions:**
- **Reject** — income stability cannot be confirmed, so the answer is no (safest for lenders)
- **Treat as unstable** — same outcome as unstable income; rejection reason differs
- **Refer for manual review** — approval decision is deferred, not a binary yes/no

This is the most significant open question because it affects what the system returns. If you lean toward "reject", does it say "income information required" or "unstable income"? That distinction matters for the applicant.

---

Once you've resolved those three points, this table is ready to hand to `/tabletest` to produce the `@TableTest` implementation.