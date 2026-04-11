Let me work through the examples systematically, covering the main cases and surfacing the open questions.

---

## Loan Approval Decision

| Scenario | Applicant Age | Credit Score | Income Status | Approved? | Rejection Reason? |
|---|---|---|---|---|---|
| Standard applicant, approved | 40 | 720 | stable | yes | |
| Standard applicant, score below threshold | 40 | 600 | stable | no | Credit score below 650 |
| Standard applicant, score at threshold (not above) | 40 | 650 | stable | no | Credit score below 650 |
| Standard applicant, score just above threshold | 40 | 651 | stable | yes | |
| Standard applicant, unstable income | 40 | 720 | unstable | no | No stable income |
| Standard applicant, missing income | 40 | 720 | missing | **?** | |
| Both conditions fail | 40 | 600 | unstable | no | Credit score below 650 |
| Senior applicant, approved with mid credit | 65 | 630 | stable | yes | |
| Senior applicant, score at senior threshold (not above) | 65 | 600 | stable | no | Credit score below 600 |
| Senior applicant, score just above senior threshold | 65 | 601 | stable | yes | |
| Senior applicant, score below even senior threshold | 65 | 550 | stable | no | Credit score below 600 |
| Senior applicant, unstable income | 65 | 630 | unstable | no | No stable income |
| Senior applicant, missing income | 65 | 630 | missing | **?** | |
| Age boundary — just below senior (credit in 600–650 range) | 64 | 630 | stable | no | Credit score below 650 |
| Age boundary — at senior threshold (credit in 600–650 range) | 65 | 630 | stable | yes | |
| Senior with high credit, regardless of income | 70 | 720 | {stable, unstable} | **?** | Does a high score ever override income? |

---

## Open Questions to Resolve

**1. Missing income — what does it mean?**
The two most common policies:
- **Reject** — missing income counts the same as unstable. Simplest to implement, most conservative.
- **Defer / pend** — return a third state (`PENDING`), requesting more information. More complex but more realistic for a real loan product.

_Rows marked `?` above should be filled in once this is decided._

**2. Does a high credit score (> 650) ever override a missing/unstable income?**
The rules as stated require *both* conditions, but it's worth confirming explicitly, particularly for seniors.

**3. Is the age threshold inclusive?** The table assumes 65+ = senior (i.e., age 65 is included). The boundary rows make this explicit — confirm this is right.

**4. Dual failure — which reason is reported?**
When both credit and income fail, the `Both conditions fail` row shows rejection but leaves the reason open. Does it report the first failing rule, all failing rules, or a generic reason?

---

Once you've settled the missing-income question, fill in the `?` cells and you'll have a complete spec ready to hand off to `/tabletest`.