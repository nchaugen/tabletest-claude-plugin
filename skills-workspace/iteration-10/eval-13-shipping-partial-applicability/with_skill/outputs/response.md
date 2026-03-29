Looking at these rules, I can see two distinct concerns worth separating:
1. **Express shipping cost** — depends on order value AND destination (with a £50 threshold that only unlocks free delivery in the UK)
2. **Overnight availability** — destination-gated, with a flat cost when available

Standard is trivial (constant £3.99) and fits naturally alongside Express in the cost table.

---

## Shipping Cost Calculation

| Scenario | Method | Order Value | Destination | Express Free Threshold | Cost? |
|---|---|---|---|---|---|
| Standard — flat rate regardless of context | Standard | {£10, £100} | {UK, Ireland, EU} | | £3.99 |
| Express — below free threshold | Express | £30 | {UK, Ireland, EU} | £50 | £9.99 |
| Express — just below free threshold, UK | Express | £49.99 | UK | £50 | £9.99 |
| Express — at free threshold, UK | Express | £50 | UK | £50 | £0.00 |
| Express — above free threshold, UK | Express | £75 | UK | £50 | £0.00 |
| Express — at free threshold, outside UK (no free tier) | Express | £50 | {Ireland, EU} | £50 | £9.99 |
| Express — above free threshold, outside UK | Express | £75 | {Ireland, EU} | £50 | £9.99 |

**Open question:** Is Express available for all non-UK destinations, or only some? The rules above treat Ireland and EU identically for Express — worth confirming.

---

## Overnight Availability

| Scenario | Destination | Cost? | Reason? |
|---|---|---|---|
| Overnight — UK | UK | £14.99 | |
| Overnight — Ireland | Ireland | £14.99 | |
| Overnight — outside UK/Ireland | {EU, Rest of World} | not available | Only available in UK and Ireland |

**Open question:** Does "not available" mean the option is hidden from the UI, or shown but blocked at checkout? This affects whether the calculator returns an error or simply omits the option.

---

### Notes on the Express free threshold column

The £50 threshold is a policy value — it belongs in configuration, not hardcoded. The column makes the boundary explicit and highlights that the same threshold value produces different outcomes depending on destination (free in UK, no effect elsewhere). The two boundary rows (£49.99 and £50) pin down the `<` vs `≤` question: the free tier kicks in at exactly £50.