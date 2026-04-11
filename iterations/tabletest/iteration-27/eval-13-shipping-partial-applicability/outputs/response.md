Let me work through this systematically. There are two distinct concerns here: what shipping options are **available**, and what they **cost**. I'll start with cost since it's the core question, and let the availability concern emerge naturally.

---

## Shipping Cost Calculation

| Scenario | Shipping Method | Order Value | Destination | Free Express Threshold (Policy) | Cost? |
|---|---|---|---|---|---|
| Standard: flat rate regardless of order or destination | Standard | {£10.00, £100.00} | {UK, Ireland, International} | | £3.99 |
| Express below threshold | Express | £49.99 | {UK, Ireland, International} | £50.00 | £9.99 |
| Express exactly at threshold in UK | Express | £50.00 | UK | £50.00 | £0.00 |
| Express above threshold in UK | Express | £75.00 | UK | £50.00 | £0.00 |
| Express at or above threshold, outside UK | Express | {£50.00, £75.00} | {Ireland, International} | £50.00 | £9.99 |
| Overnight to UK | Overnight | {£10.00, £100.00} | UK | | £14.99 |
| Overnight to Ireland | Overnight | {£10.00, £100.00} | Ireland | | £14.99 |
| Overnight not available internationally | Overnight | {£10.00, £100.00} | International | | Not available |

A few notes on the design choices:

- **Standard row** uses `{£10.00, £100.00}` — two values straddling the Express threshold — to make it explicit that Standard ignores order value entirely, not just for some values.
- **Free Express Threshold** is left blank for Standard and Overnight rows, where the concept doesn't apply. It's a constant (£50.00) for Express rows — but making it explicit shows it's a policy setting that could change, and clarifies the exact boundary.
- **"At or above threshold, outside UK"** uses a value set `{£50.00, £75.00}` to state: the free-shipping rule doesn't activate outside UK at *any* value ≥ £50.
- **Overnight outside UK** produces `Not available` rather than a price — worth confirming whether this means it's hidden from checkout, shown as greyed-out, or triggers an error.

---

## Open Questions

A few things this table doesn't resolve:

1. **"International" definition** — is this "Rest of World excluding UK and Ireland", or does it include EU specifically? If EU is a distinct tier with different rules, it needs its own destination value.

2. **Ireland — which Ireland?** Northern Ireland is technically UK. Does "Ireland" mean Republic of Ireland only? Does an NI postcode qualify for the Overnight restriction or the UK free-Express rule?

3. **Overnight + International: user experience** — `Not available` covers the business rule, but does the system hide the option, show it disabled, or show an error at checkout? That may need a separate table (or a note in the scenario).

4. **Express, order value exactly £49.99 vs £50.00** — confirmed above, but worth a domain expert sign-off that the threshold is *at least* £50 (i.e., £50.00 is free), not *over* £50 (i.e., only £50.01+ is free).

---

Once these are resolved, this table maps cleanly to a `/tabletest` — the column structure carries over directly.