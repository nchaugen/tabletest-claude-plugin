Here's a spec table for the shipping cost calculator:

**Shipping Cost**

| Scenario | Method | Order Value | Destination | Cost? |
|---|---|---|---|---|
| Standard: flat rate always applies | Standard | {£10, £50, £100} | {UK, France, Ireland} | £3.99 |
| Express: below threshold | Express | £49.99 | {UK, France, Ireland} | £9.99 |
| Express: at threshold in UK (free tier) | Express | £50.00 | UK | £0.00 |
| Express: above threshold in UK (free tier) | Express | £75.00 | UK | £0.00 |
| Express: at threshold outside UK (no free tier) | Express | £50.00 | {France, Ireland} | £9.99 |
| Express: above threshold outside UK (no free tier) | Express | £100.00 | {France, Ireland} | £9.99 |
| Overnight: available in UK | Overnight | {£20, £80} | UK | £14.99 |
| Overnight: available in Ireland | Overnight | {£20, £80} | Ireland | £14.99 |
| Overnight: not available outside UK/Ireland | Overnight | {£20, £80} | France | Not available |

A few things worth confirming before implementation:

1. **Ireland + Express free tier** — the table assumes Ireland does *not* qualify for Express free tier (only UK does). Rows 5–6 make this explicit. Is that correct?

2. **"Not available" behaviour** — when Overnight is selected for a non-UK/Ireland destination, what does the system do? Return an error? Hide the option? The table marks it `Not available` but the handling isn't specified.

3. **UK definition** — does "UK" include Northern Ireland? This matters for the Express free tier boundary.

4. **Other destinations** — the rules mention UK, Ireland, and implicitly "everywhere else". Are there destinations where no shipping is offered at all?