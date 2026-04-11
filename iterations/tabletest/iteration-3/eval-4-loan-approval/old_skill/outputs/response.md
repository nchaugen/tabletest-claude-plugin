# Loan Approval — Worked Examples

Good time to work out these examples before coding. Let me help you build a concrete table that captures what you know and surfaces the gaps.

## What We Know

- Standard approval: credit score > 650 AND stable income
- Senior applicants (65+): lower threshold of 600 (income requirement unclear — same rule?)
- Missing income: unspecified behaviour

## Example Table

| Scenario                                   | Age | Credit Score | Income Status | Approved? |
|--------------------------------------------|-----|--------------|---------------|-----------|
| Standard applicant, well above threshold   | 40  | 720          | Stable        | Yes       |
| Standard applicant, exactly at threshold   | 40  | 651          | Stable        | Yes       |
| Standard applicant, at threshold           | 40  | 650          | Stable        | No        |
| Standard applicant, below threshold        | 40  | 580          | Stable        | No        |
| Standard applicant, good score, no income  | 40  | 720          | Unstable      | No        |
| Senior applicant, above senior threshold   | 65  | 620          | Stable        | Yes       |
| Senior applicant, at senior threshold      | 65  | 600          | Stable        | Yes       |
| Senior applicant, below senior threshold   | 65  | 590          | Stable        | No        |
| Senior applicant, above standard threshold | 65  | 660          | Stable        | Yes       |
| Standard applicant, missing income info    | 40  | 720          | Unknown       | ?         |
| Senior applicant, missing income info      | 65  | 620          | Unknown       | ?         |

## Open Questions

1. **Missing income information** — The table marks these as `?` because we haven't agreed on what to do. Options:
   - Reject automatically (treat unknown income as unstable)
   - Pend the application for manual review
   - Request income verification before deciding
   Which should it be?

2. **Senior threshold boundary** — Is age 65 exactly a "senior", or is it 66+? The table assumes 65+ is the senior category.

3. **Senior income requirement** — Do seniors still need stable income, just at the lower credit score threshold? Or does age 65+ waive the income requirement? All senior rows above assume income is still required.

4. **Score exactly at 650 for standard** — Is the rule "above 650" (strict) or "650 or above" (inclusive)? The table assumes strict (> 650), so 650 itself is rejected.

Once you agree on answers to these questions, we can fill in the `?` cells and tighten the boundary conditions.
