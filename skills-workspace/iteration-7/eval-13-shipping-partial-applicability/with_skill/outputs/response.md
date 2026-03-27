Here's a spec table for the shipping cost calculator.

## Shipping Cost

| Scenario | Shipping Method | Destination | Order Value | Cost? |
|---|---|---|---|---|
| Standard — always flat rate | Standard | {UK, Ireland, France} | {£25, £75} | £3.99 |
| Express under threshold | Express | {UK, Ireland, France} | £35 | £9.99 |
| Express just under threshold | Express | UK | £49.99 | £9.99 |
| Express at threshold — UK | Express | UK | £50.00 | £0.00 |
| Express over threshold — UK | Express | UK | £75 | £0.00 |
| Express at or over threshold — non-UK | Express | {Ireland, France} | {£50, £75} | £9.99 |
| Overnight — UK | Overnight | UK | {£25, £75} | £14.99 |
| Overnight — Ireland | Overnight | Ireland | {£25, £75} | £14.99 |
| Overnight — outside UK and Ireland | Overnight | France | {£25, £75} | not available |

---

A few things worth confirming:

1. **Express free tier boundary**: the table assumes "£50 and over" means ≥ £50, so £50.00 exactly is free in the UK. Is that right, or is the threshold strictly > £50?

2. **"Not available" for Overnight**: the table uses `not available` as the cost for unsupported destinations. Is this a hard error (the UI should never offer it), or does the calculator need to return a sentinel value? That affects whether this belongs in a separate "Availability" table.

3. **Standard value sets**: Standard rows use `{UK, Ireland, France}` and `{£25, £75}` to show destination and order value are irrelevant — is France a representative stand-in for all non-UK/non-Ireland destinations, or should a different example be used?

4. **Express outside UK under the threshold**: the first Express row uses `{UK, Ireland, France}` for the under-threshold case, implying all destinations cost £9.99 below £50. That follows from the rules, but worth a quick confirm.