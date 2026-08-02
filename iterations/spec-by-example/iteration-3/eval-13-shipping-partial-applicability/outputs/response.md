Since Overnight has a rejection path (unavailable outside UK/Ireland) and the other two methods don't, splitting into two tables keeps each table's rows honest — removing the rejected row from a combined "all methods" table would still leave a full standalone cost rule, which is the signal to split (per the skill's "remove the rejected rows" test). Standard and Express share one table because neither ever rejects; they're one family ("Shipping Method") with different cost formulas.

## Assumptions (stated up front — this is a non-interactive run)

- **UK** = United Kingdom as a single destination (not distinguishing GB/NI/Channel Islands).
- **Ireland** = Republic of Ireland, distinct from the UK.
- Order Value is in GBP, to the penny; "under £50" is strictly `< 50.00`, "£50 and over" is `>= 50.00`.
- Order Value means the order subtotal, exclusive of shipping cost itself.
- Express's UK-only free tier does **not** extend to Ireland — Ireland gets Overnight eligibility but not the Express free tier. This is the standout "surprising case," so it gets its own row rather than being assumed.
- Any destination other than UK/Ireland is represented by a stand-in ("France") — the rule doesn't care which non-UK/Ireland country it is.

---

### Table 1: Calculates Shipping Cost By Method (Standard, Express)

Overnight is excluded from this table — its cost is gated by destination eligibility, handled in Table 2.

| Scenario | Shipping Method | Order Value (£) | Destination | Free-Tier Threshold (£) | Cost (£)? |
|---|---|---|---|---|---|
| Standard, regardless of value or destination | Standard | {10, 500} | {UK, France} | | 3.99 |
| Express, below the free-tier threshold | Express | 49.99 | {UK, Ireland, France} | 50 | 9.99 |
| Express, at the threshold, UK | Express | 50.00 | UK | 50 | 0.00 |
| Express, at the threshold, outside the UK | Express | 50.00 | {Ireland, France} | 50 | 9.99 |

Row 2 shows destination is irrelevant below the threshold. Rows 3 vs 4 hold Order Value constant at exactly £50 and vary only destination, isolating "UK-only" as the deciding factor — and explicitly proves Ireland doesn't get the Express free tier.

### Table 2: Calculates Overnight Shipping Cost And Eligibility

| Scenario | Destination | Order Value (£) | Cost (£)? | Rejection Reason? |
|---|---|---|---|---|
| Eligible destination, any order value | {UK, Ireland} | {10, 500} | 14.99 | |
| Ineligible destination, any order value | France | {10, 500} | | Overnight delivery is not available for this destination |

Order Value is irrelevant to Overnight in both directions (available or not), shown via value sets.

---

## Open questions

- Should the rejection reason for ineligible Overnight destinations be a user-facing message or an internal error code? Left as prose above — resolve during implementation.
- Not addressed: order value of exactly £0 or negative values, multiple items shipped to different addresses, VAT treatment. Flag if any of these are in scope.

**Next step:** hand these two tables to `/tabletest` or `/table-driven-testing` (depending on target language) once ready to implement — the columns map directly to parameters and the `?` columns to assertions.