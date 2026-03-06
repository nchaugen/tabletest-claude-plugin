# Discount Calculation — Bulk and Loyalty Interaction

This table pins down the open questions surfaced mid-implementation:
1. When both a bulk discount and a loyalty discount apply, are they **stacked** (combined) or does only the **higher one** apply?
2. Is there a **cap** on the total discount a customer can receive?

Rows where the outcome depends on those decisions are marked `?` — these are the cells the team needs to agree on before implementation can proceed.

---

## Table 1 — Individual Discounts (Baselines, No Interaction)

These rows establish what each discount does in isolation, so the interaction rows below are traceable.

| Scenario                              | Order Value (£) | Years as Customer | Bulk Threshold (Policy) | Loyalty Threshold (Policy) | Bulk Discount Rate (Policy) | Loyalty Discount Rate (Policy) | Bulk Discount Applied? | Loyalty Discount Applied? | Final Price (£)? |
|---------------------------------------|-----------------|-------------------|-------------------------|----------------------------|-----------------------------|--------------------------------|------------------------|---------------------------|-----------------|
| Small order, new customer             | 50              | 0                 | 100                     | 2                          | 10%                         | 15%                            | no                     | no                        | 50.00           |
| Bulk order only, new customer         | 150             | 0                 | 100                     | 2                          | 10%                         | 15%                            | yes                    | no                        | 135.00          |
| Small order, loyal customer           | 50              | 3                 | 100                     | 2                          | 10%                         | 15%                            | no                     | yes                       | 42.50           |
| Exactly at bulk threshold             | 100             | 0                 | 100                     | 2                          | 10%                         | 15%                            | yes                    | no                        | 90.00           |
| Just below bulk threshold             | 99              | 0                 | 100                     | 2                          | 10%                         | 15%                            | no                     | no                        | 99.00           |
| Exactly at loyalty threshold (years)  | 50              | 2                 | 100                     | 2                          | 10%                         | 15%                            | no                     | yes                       | 42.50           |
| Just below loyalty threshold (years)  | 50              | 1                 | 100                     | 2                          | 10%                         | 15%                            | no                     | no                        | 50.00           |

---

## Table 2 — Both Discounts Apply (Open Questions)

These rows require a team decision. The `Final Price (£)?` column is marked `?` where the answer depends on the stacking/cap policy. Two alternative outcomes are shown to make the choice concrete and explicit.

| Scenario                                               | Order Value (£) | Years as Customer | Bulk Threshold (Policy) | Loyalty Threshold (Policy) | Bulk Discount Rate (Policy) | Loyalty Discount Rate (Policy) | Discount Cap (Policy) | Bulk Discount Applied? | Loyalty Discount Applied? | Final Price (£)? | Open Question |
|--------------------------------------------------------|-----------------|-------------------|-------------------------|----------------------------|-----------------------------|--------------------------------|-----------------------|------------------------|---------------------------|-----------------|---------------|
| Both apply — if discounts are stacked                  | 150             | 3                 | 100                     | 2                          | 10%                         | 15%                            | none                  | yes                    | yes                       | 114.75          | OPEN: do we stack? (10% then 15% on remainder = 150 × 0.90 × 0.85) |
| Both apply — if only the higher discount applies       | 150             | 3                 | 100                     | 2                          | 10%                         | 15%                            | none                  | yes                    | yes                       | 127.50          | OPEN: or higher-only? (15% on 150 = 127.50) |
| Both apply, equal rates — stacked vs higher is visible | 150             | 3                 | 100                     | 2                          | 15%                         | 15%                            | none                  | yes                    | yes                       | ?               | OPEN: stacked gives 107.81 (150 × 0.85 × 0.85); higher-only gives 127.50 — which? |
| Both apply, cap in effect — if cap exists              | 150             | 3                 | 100                     | 2                          | 10%                         | 15%                            | 20%                   | yes                    | yes                       | 120.00          | OPEN: is there a cap? If stacked = 23.6% > 20% cap → floor at 80% of 150 = 120.00 |
| Both apply, cap not reached                            | 150             | 3                 | 100                     | 2                          | 10%                         | 15%                            | 25%                   | yes                    | yes                       | ?               | OPEN: stacked = 23.6% < 25% cap, so cap doesn't bite — outcome same as stacked row |
| Very loyal, large order — cap matters most             | 1000            | 10                | 100                     | 2                          | 20%                         | 25%                            | 30%                   | yes                    | yes                       | 700.00          | OPEN: stacked = 40% > 30% cap → 30% cap applies → 1000 × 0.70 = 700.00 |
| Very loyal, large order — no cap                       | 1000            | 10                | 100                     | 2                          | 20%                         | 25%                            | none                  | yes                    | yes                       | ?               | OPEN: stacked = 40% → 600.00; higher-only = 25% → 750.00 — which? |

---

## Decisions Needed

| # | Question | Option A | Option B | Notes |
|---|----------|----------|----------|-------|
| 1 | When both discounts qualify, how are they combined? | **Stack them** (apply one after the other) | **Higher only** (take the larger of the two) | Stacking is more generous to the customer; higher-only is simpler to explain |
| 2 | Is there a cap on total discount? | **Yes — specify the cap %** | **No cap** | A cap protects margin on large orders from very loyal customers |
| 3 | If stacking: in which order are discounts applied? | **Bulk first, then loyalty** | **Loyalty first, then bulk** | Order affects the absolute £ amount when rates differ (though final % is the same either way) |

---

## Notes for the Team

- The baseline rows in Table 1 are not in dispute — they pin down each discount rate and threshold individually.
- The `(Policy)` suffix on threshold and rate columns signals that these are configuration values, not per-transaction inputs. They may need to be loaded from config rather than hardcoded.
- Once decisions 1–3 above are agreed, mark the `?` cells in Table 2 with the correct values and this table becomes the handoff spec for `/tabletest`.
