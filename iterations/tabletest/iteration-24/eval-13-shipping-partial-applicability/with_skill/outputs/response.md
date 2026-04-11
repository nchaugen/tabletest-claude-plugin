Here's a spec table for the shipping cost calculator:

## Shipping Cost

| Scenario | Shipping Method | Destination | Order Value | Cost? |
|---|---|---|---|---|
| Standard — flat fee always | Standard | {UK, Ireland, International} | {£10.00, £100.00} | £3.99 |
| Express — under free threshold | Express | {UK, Ireland, International} | £49.99 | £9.99 |
| Express — at free threshold, UK | Express | UK | £50.00 | £0.00 |
| Express — over free threshold, UK | Express | UK | £100.00 | £0.00 |
| Express — at or over threshold, outside UK | Express | {Ireland, International} | {£50.00, £100.00} | £9.99 |
| Overnight — UK | Overnight | UK | {£10.00, £100.00} | £14.99 |
| Overnight — Ireland | Overnight | Ireland | {£10.00, £100.00} | £14.99 |
| Overnight — not available internationally | Overnight | International | {£10.00, £100.00} | Unavailable |

---

**A few things to confirm:**

1. **Express threshold boundary** — the table shows £49.99 → £9.99 and £50.00 → £0.00 in the UK. Is the threshold strictly "less than £50" (pence-exact), or is there any rounding to consider?

2. **Unavailable meaning** — for Overnight internationally, should the system return an error, silently hide the option from the UI, or return a cost of "Unavailable"? This affects what the method signature looks like.

3. **Express outside UK** — the rules say the free tier "only applies in the UK", implying Express is still available in Ireland and internationally — just never free. The table reflects that. Is that correct?

4. **Order value definition** — the table assumes Order Value is the merchandise subtotal before shipping. Is that right, or is it post-discount, or something else?

Once those are settled, this table is ready to hand off to `/tabletest` to generate the `@TableTest` implementation.