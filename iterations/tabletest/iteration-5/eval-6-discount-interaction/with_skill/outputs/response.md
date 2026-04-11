Here's a spec table to drive that team conversation. It separates the agreed cases from the open ones, and makes the stakes concrete so the team can decide — not just discuss in the abstract.

---

**Discount Calculation**

| Scenario | Order Total | Bulk Discount | Loyalty Discount | Discount Applied? | Final Price? |
|---|---|---|---|---|---|
| No discounts | £200 | — | — | 0% | £200.00 |
| Bulk only | £200 | 10% | — | 10% | £180.00 |
| Loyalty only | £200 | — | 15% | 15% | £170.00 |
| Both apply — lower rates | £200 | 10% | 15% | ? | ? |
| Both apply — higher rates | £500 | 20% | 15% | ? | ? |
| Both apply — rates would exceed any likely cap | £200 | 30% | 25% | ? | ? |

**Open questions to resolve:**

1. **Stacking rule** — when both apply, which of these?
   - *Additive*: 10% + 15% = 25% → £150.00
   - *Sequential*: 15% then 10% on remainder = 23.5% → £153.00
   - *Higher only*: max(10%, 15%) = 15% → £170.00

2. **Cap** — is there a maximum combined discount regardless of how they combine?
   - If yes: what is it, and whose discount is reduced to hit it?

3. **Order of application** — if stacking: does bulk apply first, then loyalty, or vice versa? (Only matters for sequential stacking, not additive.)

---

A few notes on the table design:

- The "Both apply — rates would exceed any likely cap" row is there deliberately: if the team hasn't thought about a cap, seeing 30% + 25% = 55% off makes the question unavoidable.
- The `—` in single-discount rows means that discount genuinely doesn't apply (not zero — absent), which matters for the implementation.
- Once the team fills in the `?` cells, hand this table to `/tabletest` — the column structure maps directly.