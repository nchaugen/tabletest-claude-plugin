Here are the example tables, built the way the skill lays out: one table per independent condition, thresholds made visible, and the missing-income gap surfaced explicitly rather than assumed away.

## Assumptions (stated since I can't ask)
- "Above 650" / "lower threshold of 600" are **strict** thresholds — a score *equal to* the threshold does **not** qualify. Flag this if your business rule actually means "≥".
- "Senior" means age **≥ 65**.
- Income Stability has three real states: `stable`, `unstable`, and **absent** (no data at all) — these are different things, not the same case.

---

### Table 1 — Sets the credit score threshold from applicant age

| Scenario                      | Applicant Age | Credit Score Threshold? |
|--------------------------------|----------------|--------------------------|
| Just below the senior age      | 64             | 650                      |
| At the senior age boundary     | 65             | 600                      |

Age only ever affects *which threshold applies* — it plays no other role, so the decision table below takes the threshold as a direct input rather than restating age.

### Table 2 — Approves a loan from credit score against its threshold
*(income held at `stable` throughout — that's a different rule's concern)*

| Scenario                              | Credit Score | Credit Score Threshold (Policy) | Income Stability | Approved? | Reason?                          |
|----------------------------------------|--------------|----------------------------------|-------------------|-----------|-----------------------------------|
| At the standard threshold              | 650          | 650                               | stable            | no        | Credit score not above threshold |
| Just above the standard threshold      | 651          | 650                               | stable            | yes       |                                   |
| At the senior threshold                | 600          | 600                               | stable            | no        | Credit score not above threshold |
| Just above the senior threshold        | 601          | 600                               | stable            | yes       |                                   |

### Table 3 — Approves a loan from income stability
*(credit score held safely above any threshold — 750 vs. 650)*

| Scenario                        | Credit Score | Credit Score Threshold (Policy) | Income Stability | Approved? | Reason?                                   |
|-----------------------------------|--------------|-----------------------------------|-------------------|-----------|---------------------------------------------|
| Stable income                     | 750          | 650                                | stable            | yes       |                                             |
| Unstable income                   | 750          | 650                                | unstable          | no        | Income not stable                          |
| **Income info missing**           | 750          | 650                                | *(blank)*         | **?**     | **Open — behaviour not yet defined**       |

---

## The open question

The last row is the one you flagged: **what does "missing income info" resolve to?** There are three defensible policies, and the code needs to pick one on purpose rather than by accident of implementation:

1. **Fail closed (reject)** — missing income is treated like unstable income. Safest default for a lending/compliance context; false negatives (rejecting a good applicant) are cheaper than false positives.
2. **Fail open (approve if credit score qualifies)** — missing income is ignored, decision rests on credit score alone. Risky — silently drops a stated requirement of the rule.
3. **Neither — a third outcome** (`PENDING` / `MANUAL_REVIEW`) — most accurate to "we don't know," but means `Approved?` isn't just yes/no anymore, which ripples into the API/UI contract.

**My recommendation:** option 1 (fail closed) as the default, since the stated rule requires stable income as a precondition and absence of evidence shouldn't satisfy it. But this is a business/compliance call, not a technical one — confirm it with whoever owns the lending policy before implementing.

Once you confirm the policy, that row's `Approved?`/`Reason?` cells get filled in for real, and this table becomes the seed for a `@TableTest` if you want to hand it to `/tabletest` next.