Here's the worked-out example table, split into two tables per the standard "classification, then decision" pattern — the age→threshold lookup is a separate rule from the actual approve/reject decision, so mixing them into one table would hide the derivation.

## Table 1 — Determine Minimum Credit Score Threshold by Age

| Scenario                          | Applicant Age | Credit Score Threshold? |
|------------------------------------|---------------|--------------------------|
| Standard applicant                | 40            | 650                      |
| Just below senior age              | 64            | 650                      |
| Exactly senior age                | 65            | 600                      |

## Table 2 — Approve Loan Application

| Scenario                                     | Credit Score | Credit Score Threshold | Stable Income | Approved? | Reason?                          |
|-----------------------------------------------|--------------|--------------------------|----------------|-----------|-----------------------------------|
| Comfortably above standard threshold          | 700          | 650                      | yes            | yes       |                                   |
| Exactly at standard threshold                 | 650          | 650                      | yes            | no        | Credit score not above threshold |
| One point above standard threshold            | 651          | 650                      | yes            | yes       |                                   |
| Well below standard threshold                 | 600          | 650                      | yes            | no        | Credit score not above threshold |
| Exactly at senior threshold                   | 600          | 600                      | yes            | no        | Credit score not above threshold |
| One point above senior threshold              | 601          | 600                      | yes            | yes       |                                   |
| High score, unstable income                   | 700          | 650                      | no             | no        | Income not stable                 |
| High score, income info missing               | 700          | 650                      |                | **?**     | **?**                             |

Note on the last row: "Stable Income" is genuinely absent, not "no" — those are different claims and shouldn't be conflated.

## Open question: missing income

This is the one your team needs to actually decide — I can't infer it from the rule as stated. Two live options:

1. **Treat missing as not-stable → reject.** Rationale: the rule requires *demonstrated* stable income; absence of evidence isn't evidence of stability. This is the common default in credit-decision systems (fail closed) and is what I'd recommend absent other guidance.
2. **Treat missing as a separate error state**, e.g. reject with a distinct reason like "Income information required" rather than silently reusing "Income not stable" — useful if the UI needs to prompt the applicant to supply it rather than presenting a flat denial.

What I'd avoid: silently defaulting missing income to "stable" and approving — that's the one option that would let an unverified applicant slip through, which seems to conflict with the intent of the rule.

**Assumption I've made pending your answer:** I left the last row's `Approved?`/`Reason?` blank rather than guessing, since this determines a real branch in the implementation and a wrong guess here is exactly the kind of thing this table is meant to catch before code is written.

A few other assumptions baked into the tables above, flagged for confirmation:
- "Above 650" / "lower threshold of 600" are **strict** inequalities (650 itself does not qualify) — this is the more common reading of "above X" but worth confirming since off-by-one here is a classic bug.
- Senior threshold applies at exactly age 65 (i.e., "65+" is inclusive).

Let me know your call on the missing-income question and I can fill in the row and move this into an executable `@TableTest` (or whatever test format you're using).