Here are two tables that specify this behaviour. The rules break naturally into two concerns: which methods are available, and what they cost.

---

## Shipping Method Availability

| Scenario | Shipping Method | Destination | Available? |
|---|---|---|---|
| Standard — worldwide | Standard | {UK, Ireland, Other} | yes |
| Express — worldwide | Express | {UK, Ireland, Other} | yes |
| Overnight — UK | Overnight | UK | yes |
| Overnight — Ireland | Overnight | Ireland | yes |
| Overnight — rest of world | Overnight | Other | no |

---

## Shipping Cost

| Scenario | Shipping Method | Destination | Order Value | Cost? |
|---|---|---|---|---|
| Standard — flat rate regardless | Standard | {UK, Ireland, Other} | {£10, £75} | £3.99 |
| Express — below threshold, any destination | Express | {UK, Ireland, Other} | £49.99 | £9.99 |
| Express — at threshold in UK (free tier applies) | Express | UK | £50.00 | £0.00 |
| Express — above threshold in UK | Express | UK | £75.00 | £0.00 |
| Express — at threshold outside UK (free tier does not apply) | Express | {Ireland, Other} | £50.00 | £9.99 |
| Express — above threshold outside UK | Express | {Ireland, Other} | £75.00 | £9.99 |
| Overnight — UK or Ireland, any order value | Overnight | {UK, Ireland} | {£10, £75} | £14.99 |

---

## Open Questions

| Question | Why it matters |
|---|---|
| When overnight is requested for an unavailable destination, what should the system do — return an error, exclude the option silently, or something else? | Determines whether "availability" is enforced by the calculator or upstream by the UI |
| Is the express free tier threshold inclusive of exactly £50.00, or strictly above £50.00? | The table assumes `£50.00 → free`; confirm if the rule is "£50 and over" or "over £50" |
| What counts as "UK" — Great Britain only, or does it include Northern Ireland? | Affects whether Northern Ireland orders get free express and overnight access |