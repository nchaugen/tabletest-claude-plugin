# Loan Approval — Spec by Example

Let's work out the concrete examples before you write a line of code. The domain looks like it has two overlapping rules (standard threshold vs. senior threshold), plus an open question about missing income. Let's get all of that visible in a table.

## Identifying the Concern

The behaviour to specify is: **Loan Approval Decision** — given a customer's credit score, age, and income status, should they be approved?

## The Example Table

| Scenario                                  | Credit Score | Customer Age | Income Status      | Credit Threshold (Policy) | Approved? | Reason?                          |
|-------------------------------------------|--------------|--------------|--------------------|---------------------------|-----------|----------------------------------|
| Standard customer, above threshold        | 700          | 40           | Stable             | 650                       | yes       |                                  |
| Standard customer, below threshold        | 600          | 40           | Stable             | 650                       | no        | Credit score below 650           |
| Standard customer, at threshold exactly   | 650          | 40           | Stable             | 650                       | ?         | Is 650 approved or rejected?     |
| Standard customer, unstable income        | 700          | 40           | Unstable           | 650                       | no        | Income not stable                |
| Senior applicant, above senior threshold  | 630          | 65           | Stable             | 600                       | yes       |                                  |
| Senior applicant, at senior threshold     | 600          | 65           | Stable             | 600                       | ?         | Is 600 approved or rejected?     |
| Senior applicant, below senior threshold  | 580          | 65           | Stable             | 600                       | no        | Credit score below 600           |
| Senior applicant, just below senior age   | 630          | 64           | Stable             | 650                       | no        | Age 64 uses standard threshold   |
| Senior with unstable income               | 630          | 65           | Unstable           | 600                       | no        | Income not stable                |
| Missing income information                | 700          | 40           |                    | 650                       | ?         | Open question — see below        |
| Missing income, senior applicant          | 630          | 65           |                    | 600                       | ?         | Open question — see below        |

## Notes on the Table

**Senior threshold boundary** — the table includes age 64 vs. 65 to make clear exactly where the lower threshold kicks in. Is 65 the boundary, or is it "over 65"? The row at age 64 forces that decision.

**At-threshold rows** — both 650 (standard) and 600 (senior) are marked `?` because "above 650" is ambiguous: does the rule trigger at exactly 650, or only above it? Confirm which before implementing.

**Income stability** — the current rule refers to "stable income" but we haven't defined what that means yet. Is it a boolean flag from an upstream system, a category with more than two values, or something the applicant self-declares? That definition will determine whether `Unstable` is the right term or whether there are more categories (e.g. `Self-employed`, `Retired`, `Student`).

## Open Questions

1. **Missing income information** — when income status is not provided, what happens?
   - Option A: Automatic rejection (safest default)
   - Option B: Treated as unstable income (same as rejection, but a different reason)
   - Option C: Requires manual review (introduces a third outcome: `Pending`)
   - This needs a product/risk decision before implementation.

2. **Boundary inclusivity** — is the rule "credit score > 650" or "credit score ≥ 650"? Same question applies to the senior threshold of 600 and the senior age of 65.

3. **What counts as "stable income"?** — is this a binary (stable / unstable), or are there more categories?

4. **Senior age definition** — is a "senior applicant" someone who is 65 or older, or strictly older than 65?

Once these are resolved, this table maps directly to a `@TableTest`.
