## Shipping Cost — Spec by Example

The rules split naturally into two concerns: **which methods are available** (depends on destination) and **what they cost** (depends on method and order value). Here are both tables.

---

### Table 1: Method Availability

| Scenario | Shipping Method | Destination | Available? |
|---|---|---|---|
| Standard available everywhere | Standard | {UK, International} | yes |
| Express available everywhere | Express | {UK, International} | yes |
| Overnight available for UK | Overnight | UK | yes |
| Overnight unavailable internationally | Overnight | International | no |

---

### Table 2: Shipping Cost

| Scenario | Shipping Method | Order Value | Free Threshold (Express) | Cost? |
|---|---|---|---|---|
| Standard — low order value | Standard | £20.00 | | £3.99 |
| Standard — high order value | Standard | £100.00 | | £3.99 |
| Express — just below threshold | Express | £49.99 | £50.00 | £9.99 |
| Express — at threshold | Express | £50.00 | £50.00 | Free |
| Express — above threshold | Express | £75.00 | £50.00 | Free |
| Overnight — low order value | Overnight | £20.00 | | £14.99 |
| Overnight — high order value | Overnight | £100.00 | | £14.99 |

The `Free Threshold` column is blank for Standard and Overnight because order value doesn't affect their price. The boundary rows at £49.99 and £50.00 pin down exactly where Express tips to Free.

---

### Open Questions

A few things worth confirming before implementation:

| Question | Why it matters |
|---|---|
| When Overnight is unavailable (international), is it hidden from the UI or shown as disabled/greyed out? | Affects how the calculator surfaces unavailable options |
| Is "International" one zone, or are there multiple (e.g. EU vs. Rest of World)? | Could split into more rows if zones have different rules |
| Are there any order value minimums (e.g. minimum £5 order to ship)? | Would add new rows to the availability or cost table |
| Is the Express free threshold (£50) a configurable policy value, or hardcoded? | If configurable, it belongs in a policy table rather than the cost table |