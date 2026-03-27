Here's a spec table for the shipping cost calculator:

## Shipping Cost

| Scenario | Shipping Method | Order Value | Destination | Cost? |
|---|---|---|---|---|
| Standard — flat rate always | Standard | {£10.00, £50.00, £100.00} | {UK, Ireland, International} | £3.99 |
| Express — below free threshold | Express | £49.99 | {UK, Ireland, International} | £9.99 |
| Express — at threshold, UK | Express | £50.00 | UK | £0.00 |
| Express — above threshold, UK | Express | £75.00 | UK | £0.00 |
| Express — at threshold, non-UK (free tier does not apply) | Express | £50.00 | {Ireland, International} | £9.99 |
| Express — above threshold, non-UK | Express | £75.00 | {Ireland, International} | £9.99 |
| Overnight — UK | Overnight | {£10.00, £100.00} | UK | £14.99 |
| Overnight — Ireland | Overnight | {£10.00, £100.00} | Ireland | £14.99 |
| Overnight — unavailable outside UK and Ireland | Overnight | {£10.00, £100.00} | International | unavailable |

---

**Notes on design decisions:**

- The `{...}` value sets on the Standard row assert the rule holds regardless of order value and destination — one row rather than many near-identical ones.
- The Express boundary rows at exactly £49.99 and £50.00 pin down whether the threshold is `>= 50` or `> 50` — the spec says "£50 and over", so £50.00 → free in UK.
- Ireland and International are deliberately grouped together in the Express non-UK rows: both get £9.99 above £50. This makes explicit that the UK free tier is UK-only, not UK+Ireland.

---

**Open questions to resolve:**

| Question | Impact |
|---|---|
| What does "unavailable" mean for Overnight outside UK/Ireland — error, hidden option, or something else? | Shapes the API response and UI |
| Is Ireland in the UK for Express purposes? (Currently: no — Ireland only gets the Overnight discount, not Express free tier.) | One row to add or remove |
| Can Express and Overnight both be selected for the same order, or is it one method per order? | May need an availability table separate from cost |