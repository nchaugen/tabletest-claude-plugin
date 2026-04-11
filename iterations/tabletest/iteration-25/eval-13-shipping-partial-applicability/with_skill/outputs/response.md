The rules split naturally into two concerns: **which methods are available** (only overnight has restrictions), and **what they cost** (only express has variation). Here are two tables — one per concern.

---

## Shipping Method Availability

| Scenario | Shipping Method | Destination | Available? |
|---|---|---|---|
| Standard – available everywhere | Standard | {UK, Ireland, EU, International} | yes |
| Express – available everywhere | Express | {UK, Ireland, EU, International} | yes |
| Overnight – UK | Overnight | UK | yes |
| Overnight – Ireland | Overnight | Ireland | yes |
| Overnight – outside UK and Ireland | Overnight | {EU, International} | no |

---

## Shipping Cost Calculation

| Scenario | Shipping Method | Destination | Order Value | Cost? |
|---|---|---|---|---|
| Standard – fixed rate, any destination or value | Standard | {UK, Ireland, EU, International} | {£20, £75} | £3.99 |
| Express – below threshold | Express | {UK, Ireland, EU, International} | £30 | £9.99 |
| Express – just below threshold | Express | {UK, Ireland, EU, International} | £49.99 | £9.99 |
| Express – exactly at threshold, UK (free tier) | Express | UK | £50 | £0.00 |
| Express – above threshold, UK | Express | UK | £75 | £0.00 |
| Express – at or above threshold, non-UK (no free tier) | Express | {Ireland, EU, International} | {£50, £75} | £9.99 |
| Overnight – UK or Ireland, any value | Overnight | {UK, Ireland} | {£20, £75} | £14.99 |

---

## Open Questions

A few things the rules leave unspecified:

1. **What counts as "UK" for the express free tier?** Does it include Northern Ireland, Channel Islands, Isle of Man? These are edge cases for the free tier and the overnight cutoff.

2. **Overnight for unavailable destinations** — when a customer picks an EU destination, is overnight simply hidden/not offered, or can they attempt to select it and get an error? The availability table says `no`, but the UX response isn't specified.

3. **Express at £49.99 vs £50 — rounding** — if order values can have fractional pence (e.g. £49.999 after a discount), which side of the boundary do they fall on? Worth confirming whether order values are always rounded to 2dp before the threshold check.

---

The express threshold boundary rows (£49.99 / £50 / £75) cover the cases where misunderstandings most often hide. If those look right, the table is ready to hand to `/tabletest` to produce the executable `@TableTest`.