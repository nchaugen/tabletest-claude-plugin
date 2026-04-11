Two distinct concerns here: registration validity and pricing. I'll draft a table for each, then surface the open questions.

---

## Table 1: Registration Validation

| Scenario | Name | Email | Valid? | Error? |
|---|---|---|---|---|
| Typical valid registration | Alice Smith | alice@example.com | yes | |
| All fields provided | Alice Smith | alice@example.com | yes | |
| Name is missing | | alice@example.com | no | Name is required |
| Email is missing | Alice Smith | | no | Email is required |
| Email has no @ symbol | Alice Smith | aliceexample.com | no | Invalid email format |
| Email has no domain | Alice Smith | alice@ | no | Invalid email format |

A few questions to resolve:
- Dietary requirements and accessibility needs don't appear in the output columns — the table implies they're always accepted as-is. Is there any validation on those fields (length limits, allowed values)?
- Should both name and email errors be reported together, or does the system stop at the first error?

---

## Table 2: Discount Rules

The stacking question makes the cutoff date and group size threshold worth making explicit as policy columns — they're the rules the system enforces.

| Scenario | Registration Date | Group Size | Cutoff Date (Policy) | Min Group Size (Policy) | Discount? |
|---|---|---|---|---|---|
| No discounts apply | 2026-06-20 | 3 | 2026-06-15 | 5 | 0% |
| Early-bird only | 2026-06-01 | 3 | 2026-06-15 | 5 | 20% |
| Group only | 2026-06-20 | 5 | 2026-06-15 | 5 | 15% |
| Group just below threshold | 2026-06-20 | 4 | 2026-06-15 | 5 | 0% |
| Group at exact threshold | 2026-06-20 | 5 | 2026-06-15 | 5 | 15% |
| On the cutoff date | 2026-06-15 | 3 | 2026-06-15 | 5 | ? |
| Both early-bird and group | 2026-06-01 | 5 | 2026-06-15 | 5 | ? |

---

## Open Questions

**1. Do discounts stack?** (Row: "Both early-bird and group")
This is the one you flagged as uncertain. Three options — which matches the business intent?

| Option | Calculation | Result on $100 |
|---|---|---|
| Higher discount wins | max(20%, 15%) | $80 |
| Additive | 20% + 15% = 35% | $65 |
| Multiplicative | $100 × 0.80 × 0.85 | $68 |

**2. Is the cutoff date inclusive?** (Row: "On the cutoff date")
A registration submitted on 2026-06-15 — does it qualify as early-bird or not? The boundary row is marked `?` until this is decided.

Once these two are resolved, the `?` cells can be filled in and the table is ready to hand off to `/tabletest`.