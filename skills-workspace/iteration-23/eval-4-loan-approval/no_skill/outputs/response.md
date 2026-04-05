Here are the cases worked out based on your rules:

## Clear cases

| Age | Credit Score | Stable Income | Decision | Reason |
|-----|-------------|---------------|----------|--------|
| 40 | 700 | Yes | **Approved** | Score > 650, stable income |
| 40 | 620 | Yes | **Rejected** | Score ≤ 650, not senior |
| 70 | 630 | Yes | **Approved** | Senior, score > 600 |
| 70 | 590 | Yes | **Rejected** | Senior, but score ≤ 600 |
| 40 | 700 | No | **Rejected** | Unstable income (any age) |
| 70 | 700 | No | **Rejected** | Unstable income (any age) |

## The missing income question

This is genuinely ambiguous — you need a policy decision. The two reasonable options:

**Option A — Reject (conservative, common):** Missing income = cannot confirm stability → reject. Lowest risk for the lender.

**Option B — Pend/escalate:** Treat as incomplete application, request more info rather than outright rejecting.

Most lending systems go with **Option A** and tell the applicant to reapply with income documentation.

## Edge cases worth pinning down

1. **Age exactly 65** — is the senior threshold `>= 65` or `> 65`? The word "senior" usually means 65+, so `>= 65`.
2. **Credit score exactly 650 / 600** — is it `> 650` (strict) or `>= 650`? Same for the 600 threshold. Your spec says "above", so strict `>`.
3. **Missing credit score** — same question as missing income: reject or pend?

Want me to write this up as code (with a specific language in mind) or formalize it as test cases?