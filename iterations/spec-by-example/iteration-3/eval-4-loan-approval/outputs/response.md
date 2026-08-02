Here's the worked-out example set for loan approval. I decomposed it into two tables since threshold selection (driven by age) and the approval decision (driven by score/threshold/income) are separate rules — the age cutoff has its own boundary that shouldn't be tangled up with the approval logic.

## Table 1 — Selects the Credit Score Threshold From Applicant Age

| Scenario                       | Age | Threshold? |
|---------------------------------|-----|------------|
| Just under the senior cutoff    | 64  | 650        |
| Senior applicant at the cutoff  | 65  | 600        |

## Table 2 — Decides Loan Approval From Credit Score, Threshold And Income Stability

| Scenario                                                        | Credit Score | Threshold | Stable Income | Approved? | Reason?                                   |
|-------------------------------------------------------------------|--------------|-----------|----------------|-----------|--------------------------------------------|
| Score exactly at the threshold                                   | 650          | 650       | yes            | no        | Credit score does not exceed the threshold |
| Score one point above the threshold                               | 651          | 650       | yes            | yes       |                                            |
| Above the threshold but income not stable                        | 700          | 650       | no             | no        | Income not stable                          |
| Score would fail the standard threshold but clears a lower one    | 610          | 600       | yes            | yes       |                                            |
| Income information missing                                       | 700          | 650       | *(blank)*      | **?**     | **open question**                          |

## Assumptions (stated since I can't ask follow-ups)

- **Strict inequality both ways**: "above 650" and "lower threshold of 600" both use `>`, not `>=`. A score exactly equal to the threshold is rejected in both bands. If your policy intends `>=`, swap rows 1 and 4's expected outcomes.
- **Age cutoff is inclusive at 65**: "senior applicants (65+)" means age ≥ 65 gets the 600 threshold; 64 stays on the standard 650 threshold.
- **Stable income is a pre-computed boolean** feeding into this decision — how "stable" is determined is out of scope for this table.
- **Age is always present** — I haven't modeled a missing-age case since nothing in your description suggested that's possible.

## Open question: missing income

I left this genuinely open rather than guessing, since you said you're unsure. Three plausible policies, with a recommendation:

1. **Treat missing income as not stable → reject.** Conservative default: never approve without confirming income. Simplest to implement (same code path as `stable income = no`).
2. **Treat as a distinct "pending / needs more info" outcome**, separate from approve/reject — useful if the workflow can request the missing info before a final decision.
3. **Treat as stable by default → approve if score qualifies.** Not recommended — approving based on absent data is a risk most credit policies avoid.

**My recommendation: option 1** (fail-closed) unless there's a workflow reason to support a "pending" state. It's the safest default and requires no new outcome type. Once you confirm, replace the `?` cell with `no` / `Income missing` (or add a `PENDING` value to `Approved?` if you go with option 2) and the table is ready to hand to `/tabletest`.