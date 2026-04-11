Here's a spec table that covers the key scenarios and decision boundaries:

## Shipping Cost

| Scenario | Shipping Method | Destination | Order Value | Cost? |
|---|---|---|---|---|
| Standard, any destination and order value | Standard | {UK, Ireland, EU} | {£10.00, £100.00} | £3.99 |
| Express, below threshold | Express | {UK, Ireland, EU} | £49.99 | £9.99 |
| Express, at threshold — UK | Express | UK | £50.00 | £0.00 |
| Express, above threshold — UK | Express | UK | £75.00 | £0.00 |
| Express, at threshold — non-UK | Express | {Ireland, EU} | £50.00 | £9.99 |
| Express, above threshold — non-UK | Express | {Ireland, EU} | £75.00 | £9.99 |
| Overnight, UK or Ireland | Overnight | {UK, Ireland} | {£10.00, £100.00} | £14.99 |
| Overnight, other destination | Overnight | EU | {£10.00, £100.00} | Unavailable |

---

A few things worth confirming before implementation:

**Open questions:**

1. **Express below threshold, non-UK** — the rules state the free tier requires UK, but don't explicitly address £49.99 to a non-UK destination. The table assumes it's £9.99 (same as UK below threshold). Is that correct?

2. **"Unavailable" for overnight** — what should the system actually do? Return a specific error, omit the option from a list, throw an exception? The behaviour on the boundary matters for the caller.

3. **Exact threshold boundary** — the table treats £50.00 as "at or over" (free for UK express). Worth confirming this is `>= 50` not `> 50`.

4. **"EU" as a stand-in** — the table uses `EU` to represent any destination that is neither UK nor Ireland. Does the system have a richer destination model (country codes, zones) that might affect this?