Great set of requirements. There are two distinct concerns here — **registration validation** and **discount calculation** — so I'll propose a table for each and flag the open question on stacking explicitly.

---

## Table 1: Registration Validation

| Scenario | Name | Email | Valid? | Validation Error? |
|---|---|---|---|---|
| Standard registration | Alice Smith | alice@example.com | yes | |
| Missing name | | alice@example.com | no | Name is required |
| Missing email | Alice Smith | | no | Email is required |
| Invalid email — no @ symbol | Alice Smith | aliceexample.com | no | Invalid email format |
| Invalid email — no domain | Alice Smith | alice@ | no | Invalid email format |
| Both name and email missing | | | no | ? |
| With dietary requirements | Alice Smith | alice@example.com | yes | |
| With accessibility needs | Alice Smith | alice@example.com | yes | |
| With both optional fields | Alice Smith | alice@example.com | yes | |

**Open question:** When both name and email are missing, which error do you return — the first one found, or all of them?

---

## Table 2: Discount Calculation

| Scenario | Before Cutoff | Group Size | Early-Bird Cutoff (Policy) | Min Group Size (Policy) | Base Price | Discount? | Final Price? |
|---|---|---|---|---|---|---|---|
| No discounts apply | no | 1 | 2026-05-01 | 5 | 100.00 | none | 100.00 |
| Early-bird only | yes | 1 | 2026-05-01 | 5 | 100.00 | 20% | 80.00 |
| Group discount only — exactly at threshold | no | 5 | 2026-05-01 | 5 | 100.00 | 15% | 85.00 |
| Group discount only — above threshold | no | 8 | 2026-05-01 | 5 | 100.00 | 15% | 85.00 |
| Just below group threshold | no | 4 | 2026-05-01 | 5 | 100.00 | none | 100.00 |
| Early-bird and group — both apply | yes | 5 | 2026-05-01 | 5 | 100.00 | ? | ? |
| Registered on the cutoff date exactly | yes/no? | 1 | 2026-05-01 | 5 | 100.00 | ? | ? |

---

## Open Questions to Resolve

**1. Do early-bird and group discounts stack?**
Three common options — which fits your intent?

| Option | Both apply (100.00 base) | Description |
|---|---|---|
| A: Additive | 65.00 | 20% + 15% = 35% off |
| B: Multiplicative | 68.00 | 100 × 0.80 × 0.85 |
| C: Better discount only | 80.00 | Only the larger (early-bird) applies |

**2. Is the cutoff date inclusive?**
If the cutoff is May 1st, does a registration *on* May 1st qualify for early-bird pricing?

Once you answer these two questions, I can fill in the open rows and the tables will be ready to hand off to `/tabletest`.