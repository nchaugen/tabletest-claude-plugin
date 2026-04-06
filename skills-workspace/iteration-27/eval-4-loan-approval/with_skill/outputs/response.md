## Loan Approval Decision

Let me build this up systematically. Here's a starting table with the cases we can confidently specify, plus the open question surfaced explicitly:

| Scenario | Applicant Age | Credit Score | Income Status | Approved? | Rejection Reason? |
|---|---|---|---|---|---|
| Standard applicant, well above threshold | 35 | 720 | Stable | yes | |
| Standard applicant, just above threshold | 35 | 651 | Stable | yes | |
| Standard applicant, at threshold (boundary) | 35 | 650 | Stable | no | Credit score too low |
| Standard applicant, below threshold | 35 | 580 | Stable | no | Credit score too low |
| Standard applicant, unstable income | 35 | 720 | Unstable | no | Income not stable |
| Senior applicant, above senior threshold | 65 | 630 | Stable | yes | |
| Senior applicant, just above senior threshold | 65 | 601 | Stable | yes | |
| Senior applicant, at senior threshold (boundary) | 65 | 600 | Stable | no | Credit score too low |
| Senior applicant, unstable income | 65 | 650 | Unstable | no | Income not stable |
| Score between thresholds, non-senior | 64 | 630 | Stable | no | Credit score too low |
| Score between thresholds, senior | 65 | 630 | Stable | yes | |
| Below both thresholds | {35, 65} | 580 | Stable | no | Credit score too low |
| Missing income info | {35, 65} | 720 | (missing) | **?** | **Open question** |

---

### Key decisions this table exposes

**"Above 650" — is the boundary inclusive or exclusive?**
The rule says "above 650", which implies strictly greater than. Row 3 tests that 650 itself is *rejected*. Worth confirming — it's a common source of bugs.

**Age 64 vs 65 boundary (rows 10–11)**
These two rows are the most important for the senior rule. A 64-year-old with a score of 630 is rejected; a 65-year-old with the same score is approved. Confirm the boundary is inclusive at 65.

**The `{35, 65}` value set on the last two rows**
The "below both thresholds" row says: age is irrelevant when the score is below 600 — both age groups are rejected. One row states this more clearly than two near-identical rows.

---

### Open question: missing income

Three options to choose from:

| Option | Meaning | Approved? |
|---|---|---|
| **Hard reject** | Missing info = cannot verify stability → reject | no |
| **Treat as unstable** | Absence of proof = unstable income | no |
| **Defer / pending** | Request more info, decision deferred | pending |

All three lead to non-approval, but they differ in *why* and what happens next. If there's a "request more documents" flow, option 3 may matter. Otherwise options 1 and 2 are equivalent in outcome — but they affect the rejection reason shown to the applicant.

**Which of these fits your product's intent?** Once that's resolved, we can fill in the last row and the table is ready to hand off to `/tabletest`.