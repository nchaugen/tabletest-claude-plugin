Here's a starting example table that surfaces the open questions concretely — something your team can sit around and fill in together.

---

## Discount Calculation

| Scenario | Order Value | Bulk Threshold (Policy) | Bulk Discount (Policy) | Loyalty Tier | Loyalty Discount (Policy) | Discount Applied? | Final Price? | Open Questions |
|---|---|---|---|---|---|---|---|---|
| No discounts apply | £80 | £100 | 10% | none | 0% | none | £80 | |
| Bulk only — just at threshold | £100 | £100 | 10% | none | 0% | 10% bulk | £90 | |
| Bulk only — above threshold | £200 | £100 | 10% | none | 0% | 10% bulk | £180 | |
| Loyalty only — below bulk threshold | £80 | £100 | 10% | silver | 5% | 5% loyalty | £76 | |
| Both apply — which wins? | £200 | £100 | 10% | silver | 5% | ? | ? | Stack (15%) → £170, or higher-only (10%) → £180? |
| Both apply — loyalty higher | £200 | £100 | 10% | gold | 15% | ? | ? | Stack (25%) → £150, or higher-only (15%) → £170? |
| Both apply — equal discounts | £200 | £100 | 10% | silver | 10% | ? | ? | Stack (20%) → £160, or higher-only (10%) → £180? |
| Cap scenario — large stacked discount | £500 | £100 | 10% | gold | 15% | ? | ? | Is 25% uncapped, or is there a max (e.g. 20%)? |

---

The `?` cells are the decisions your team needs to make. A few pointed questions to drive the conversation:

**On stacking vs. higher-only:**
- Is stacking ever the intended behaviour, even for small amounts?
- Is "higher-only" simpler to explain to customers on a receipt?

**On a cap:**
- Is there a business reason to limit total discount (e.g. margin floor)?
- Does the cap apply before or after tax?

**On thresholds — worth confirming:**
- Is the bulk threshold inclusive (`≥ £100`) or exclusive (`> £100`)?
- Does loyalty tier affect the bulk threshold at all, or are they truly independent inputs?

Once your team fills in the `?` cells, the table becomes the spec — hand it to `/tabletest` when you're ready to implement.