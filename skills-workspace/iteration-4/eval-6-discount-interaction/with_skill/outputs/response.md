Here's an example table designed to force the key decisions into the open. The `?` cells are intentionally unresolved — those are the questions your team needs to answer.

---

## Discount Calculation

| Scenario | Order Value | Bulk Threshold | Bulk Discount | Loyalty Tier | Loyalty Discount | Discount Cap | Final Discount? | Final Price? |
|---|---|---|---|---|---|---|---|---|
| No discounts apply | £80 | £100 | 10% | none | 0% | — | 0% | £80 |
| Bulk only, no loyalty | £150 | £100 | 10% | none | 0% | — | 10% | £135 |
| Loyalty only, under bulk threshold | £80 | £100 | 10% | silver | 5% | — | 5% | £76 |
| Both apply — stacked? | £150 | £100 | 10% | silver | 5% | — | **?** | **?** |
| Both apply — higher only? | £150 | £100 | 10% | silver | 5% | — | **?** | **?** |
| Both apply, loyalty wins | £150 | £100 | 5% | gold | 15% | — | **?** | **?** |
| Both apply, cap reached | £150 | £100 | 10% | gold | 15% | 20% | **?** | **?** |
| Both apply, no cap | £150 | £100 | 10% | gold | 15% | — | **?** | **?** |

---

**The open questions this table surfaces:**

1. **Stacking vs. higher-only** — rows 4 and 5 are the same inputs; the `?` cells force a decision. If stacked: `15%` → `£127.50`. If higher-only: `10%` → `£135`.

2. **Stacking method** — if stacked, is it additive (`10% + 5% = 15%`) or multiplicative (`1 - 0.9 × 0.95 = 14.5%`)?

3. **Cap behaviour** — row 7 vs row 8: does a cap exist, and if so, what is it? At `20%` cap with stacking (`10% + 15% = 25%`), the cap would give `£120` instead of `£127.50`.

4. **When loyalty wins** — row 6 asks whether "higher-only" means bulk is simply ignored when loyalty is larger.

---

Bring this table to the team and ask them to fill in the `?` cells with actual values. Where they disagree on a cell, that's the decision that needs escalating. Once every `?` is resolved, hand the table to `/tabletest` to turn it into a `@TableTest`.