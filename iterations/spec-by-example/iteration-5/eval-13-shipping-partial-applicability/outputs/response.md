Here's the spec worked through as three example tables, one per independently-varying rule, plus the assumptions I had to lock in since I can't ask you to confirm them.

## Assumptions (stated because I can't ask)

1. **"Order Value" = order subtotal in £, excluding the shipping charge itself** — avoids circularity (shipping cost depending on a total that includes shipping).
2. **"Under £50" is strictly `<` £50; "£50 and over" is `>=` £50** — matches the wording given.
3. **The Express free tier is UK-only, explicitly excluding Ireland** — you said "only applies in the UK," and I've taken that literally even though Overnight groups UK+Ireland together. This is the one genuinely surprising interaction in your rules: Ireland gets Overnight but does *not* get free Express at £50+. Flagging this in case it's a slip rather than intentional — happy to fold Ireland into the free tier if so.
4. **Standard and Express are available for *any* destination worldwide** — nothing in your rules restricts them, so only Overnight has a destination gate.
5. **"UK" is treated as one destination unit** (England/Scotland/Wales/NI undifferentiated) — no indication finer granularity matters here.
6. Currency is GBP throughout; no conversion logic in scope.

---

### Sets the standard shipping cost regardless of order value or destination

| Scenario                          | Order Value        | Destination                | Standard Cost? |
|------------------------------------|---------------------|------------------------------|-----------------|
| Any order, any destination         | {£0.01, £49.99, £50.00, £250.00} | {UK, Ireland, France, USA} | £3.99 |

*One row, fully expressed with value sets — this is the whole rule: nothing about the order changes the price.*

---

### Determines the express shipping cost from order value and destination

| Scenario                                         | Order Value          | Destination                  | Free Threshold (Policy) | Express Cost? |
|---------------------------------------------------|------------------------|---------------------------------|---------------------------|------------------|
| Below the free threshold                          | £49.99                 | {UK, Ireland, France, USA}      | £50                       | £9.99            |
| At or above threshold, UK                          | {£50.00, £250.00}      | UK                               | £50                       | £0.00            |
| At or above threshold, outside UK                  | {£50.00, £250.00}      | {Ireland, France, USA}          | £50                       | £9.99            |

*Threshold made visible as a column. The straddling pair (£49.99 vs £50.00) pins down inclusivity. Row 3 is the one that encodes "free tier is UK-only" — Ireland sits with France/USA here, not with UK, per Assumption 3 above.*

---

### Determines overnight availability and cost by destination

| Scenario                              | Destination                        | Available? | Overnight Cost? |
|-----------------------------------------|---------------------------------------|-------------|-------------------|
| UK or Ireland destination               | {UK, Ireland}                         | yes         | £14.99            |
| Destination outside UK and Ireland      | {France, USA, Germany}                | no          |                   |

*Overnight has no order-value dependency at all — the only variable is destination, and the price is flat once available. Cost is blank (not £0.00) when unavailable, since blank means "no such option," not "free."*

---

## Summary of the resolved rules

- **Standard**: flat £3.99, always, everywhere.
- **Express**: £9.99 if order subtotal < £50; £0.00 if ≥ £50 **and** destination is UK; £9.99 if ≥ £50 but destination is anything else (including Ireland).
- **Overnight**: not offered outside UK/Ireland; flat £14.99 where offered, regardless of order value.

Let me know if Assumption 3 (Ireland excluded from Express's free tier) should instead match Overnight's UK+Ireland grouping — that's the only place these rules could plausibly mean something other than what's written.