# Shipping Cost Calculator — Example Tables

I can't confirm assumptions with you interactively, so here they are up front. Everything below is built on them — flag any that are wrong and I'll adjust the tables.

**Assumptions**
- All prices are GBP; no multi-currency handling.
- "Under £50" is strictly `<50.00`; "£50 and over" is `>=50.00`.
- Destinations outside UK/Ireland are undifferentiated — grouped as "Other" (e.g. France).
- Overnight's £14.99 doesn't vary with order value (nothing in the rules suggests it does).
- Requesting Overnight for a destination outside UK/Ireland is treated as **unavailable**, not a priced option — this is the one genuinely open call; see below.
- Order value is the pre-shipping order subtotal.

This splits into two tables per different concerns: which methods are **available** for a destination, and what they **cost** once available.

---

## Table 1: Shipping Method Availability

| Scenario                          | Shipping Method | Destination         | Available? | Reason?                                |
|------------------------------------|------------------|----------------------|------------|------------------------------------------|
| Standard available everywhere      | Standard         | {UK, Ireland, Other} | yes        |                                          |
| Express available everywhere       | Express          | {UK, Ireland, Other} | yes        |                                          |
| Overnight in the UK                | Overnight        | UK                   | yes        |                                          |
| Overnight in Ireland               | Overnight        | Ireland              | yes        |                                          |
| Overnight outside UK/Ireland       | Overnight        | Other (e.g. France)  | no         | Overnight only available in UK and Ireland |

---

## Table 2: Shipping Cost Calculation

Assumes the method is already available for the destination (Table 1 governs that).

| Scenario                                         | Shipping Method | Order Value | Destination            | Free Express Threshold (Policy) | Cost?         |
|---------------------------------------------------|------------------|-------------|--------------------------|----------------------------------|---------------|
| Standard, regardless of order value               | Standard         | {£10.00, £500.00} | UK                 |                                  | £3.99         |
| Standard, regardless of destination                | Standard         | £10.00      | {Ireland, Other}          |                                  | £3.99         |
| Express under threshold, any destination           | Express          | £49.99      | {UK, Ireland, Other}      | £50.00                           | £9.99         |
| Express at threshold, UK — free tier applies       | Express          | £50.00      | UK                        | £50.00                           | £0.00 (free)  |
| Express above threshold, UK — free tier applies    | Express          | £500.00     | UK                        | £50.00                           | £0.00 (free)  |
| Express at threshold, Ireland — free tier doesn't apply | Express     | £50.00      | Ireland                   | £50.00                           | £9.99         |
| Express above threshold, Other — free tier doesn't apply | Express    | £500.00     | Other (e.g. France)       | £50.00                           | £9.99         |
| Overnight, UK, regardless of order value           | Overnight        | {£10.00, £500.00} | UK                 |                                  | £14.99        |
| Overnight, Ireland, regardless of order value       | Overnight        | {£10.00, £500.00} | Ireland             |                                  | £14.99        |

Notes on this table:
- `Free Express Threshold (Policy)` is pulled out as its own column (constant £50.00) rather than buried in the logic — it's a policy value, so if it ever varies by market this table is where that would show up.
- The Ireland rows for Express are the "surprise" case worth calling out explicitly: Ireland qualifies for Overnight but **not** the Express free tier — easy to get wrong if you assume "UK and Ireland" applies uniformly across all three methods.
- Boundary rows (£49.99 vs £50.00) pin down that the threshold is inclusive at £50.00.

---

## Open Question

**What happens if a customer requests Overnight to a non-UK/Ireland destination?** I assumed it's simply not offered (Table 1: `Available? = no`) rather than priced at £14.99 anyway or rejected with an error at checkout. If your system needs a specific error/message for this case, that's a small addition to Table 1's `Reason?` column, but the actual message text needs your input.

Ready to hand this to `/tabletest` or `/table-driven-testing` for implementation once you confirm the assumptions.