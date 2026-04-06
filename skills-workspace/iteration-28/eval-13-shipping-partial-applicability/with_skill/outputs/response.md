Let me work through the rules to build out a spec table with concrete examples.

**Shipping Cost**

| Scenario | Shipping Method | Destination | Order Value | Express Free Threshold | Cost? |
|---|---|---|---|---|---|
| Standard — destination and order value irrelevant | Standard | {UK, Ireland, International} | | | £3.99 |
| Express, UK, just below free threshold | Express | UK | £49.99 | £50.00 | £9.99 |
| Express, UK, at free threshold | Express | UK | £50.00 | £50.00 | £0.00 |
| Express, UK, above free threshold | Express | UK | £75.00 | £50.00 | £0.00 |
| Express, outside UK, below threshold | Express | {Ireland, International} | £30.00 | £50.00 | £9.99 |
| Express, outside UK, at threshold — free tier doesn't apply | Express | {Ireland, International} | £50.00 | £50.00 | £9.99 |
| Overnight, UK | Overnight | UK | | | £14.99 |
| Overnight, Ireland | Overnight | Ireland | | | £14.99 |
| Overnight, international — not available | Overnight | International | | | ? |

---

A few notes:

**Boundary rows for Express (UK):** I've included £49.99 and £50.00 as the key boundary pair — that's where misunderstandings tend to live. The rule appears to be `order value >= £50` for free; is that correct, or is it strictly `> £50`?

**Express outside UK:** The two non-UK rows (below threshold, at threshold) are intentionally paired to document that even at £50.00, non-UK customers still pay £9.99. This directly contradicts the UK behaviour at the same value, so it's worth making explicit.

**Overnight + International:** This is an open question — what should the system do when someone requests overnight shipping to an international destination? Options include:
- Return an error / throw an exception
- Return a sentinel value (e.g., `null`, `unavailable`)
- Filter the option out before the user can select it (making this a UI concern, not a cost concern)

What should the cost calculator itself return in this case?