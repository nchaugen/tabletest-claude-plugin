Here's a worked example table for **Loan Approval Decision**, covering the rules you've described and surfacing the open question about missing income:

---

**Loan Approval Decision**

| Scenario                                | Age | Credit Score | Min Score (Policy) | Income Status | Approved? | Rejection Reason?    |
|-----------------------------------------|-----|--------------|--------------------|---------------|-----------|----------------------|
| Standard approval                       | 35  | 720          | 650                | Stable        | yes       |                      |
| Just above standard threshold           | 40  | 651          | 650                | Stable        | yes       |                      |
| At standard threshold                   | 40  | 650          | 650                | Stable        | no        | Credit score too low |
| Just below standard threshold           | 40  | 649          | 650                | Stable        | no        | Credit score too low |
| Unstable income, good score             | 35  | 720          | 650                | Unstable      | no        | Income not stable    |
| Senior approval above lower threshold   | 67  | 630          | 600                | Stable        | yes       |                      |
| Just above senior threshold             | 65  | 601          | 600                | Stable        | yes       |                      |
| At senior threshold                     | 65  | 600          | 600                | Stable        | no        | Credit score too low |
| Just below senior threshold             | 65  | 599          | 600                | Stable        | no        | Credit score too low |
| One year below senior age               | 64  | 630          | 650                | Stable        | no        | Credit score too low |
| Exactly at senior age                   | 65  | 630          | 600                | Stable        | yes       |                      |
| Missing income, good score              | 35  | 720          | 650                |               | ?         |                      |
| Senior, missing income, above threshold | 67  | 630          | 600                |               | ?         |                      |

---

**A few things to confirm or challenge:**

1. **Threshold is strictly greater than, not ≥** — I've shown 650 as a rejection for standard applicants, and 600 as a rejection for seniors. Is that correct, or should those scores pass?

2. **"Senior" starts at exactly 65** — the boundary rows at 64 and 65 test this directly. Does age 65 qualify, or is it 66+?

3. **Missing income is the key open question** — two rows left with `?`. Possible resolutions:
   - Reject outright (treat missing as unstable)
   - Pend the application (new status: `Pending — income verification`)
   - Approve if score is well above threshold (risky, probably not)

   Which of these fits your policy? Or should the application just be rejected with reason "Income information missing"?

4. **"Stable income" definition** — is this a binary field from the application form, or is it derived from something (employment type, payslips)? If it's derived, there may be more edge cases lurking there.

Once you've resolved the missing income question I can add those rows and the table is ready to hand off to `/tabletest`.