Worked through this using the spec-by-example approach — splitting it into one table per rule since "Express" combines an order-value tier with a UK-only condition (one combined rule) while "Overnight" is a separate destination-eligibility rule, and validation is a third concern entirely.

## Assumptions (stated because I can't ask)

1. **Express ≥£50 outside the UK**: the rule only says the *free* tier is UK-only — it doesn't say Express itself is UK-only. Assumption: Express remains available everywhere; outside the UK it just never drops to free, so it stays at £9.99 regardless of order value.
2. **Overnight outside UK/Ireland**: not offered at all (the option doesn't appear / is rejected if selected), rather than falling back to another price.
3. **£0.00 order value**: valid (e.g., a fully-discounted order still needs shipping). Only *negative* values are invalid.
4. **Missing/unknown destination**: rejected upfront as invalid input, because destination determines Express's free tier and Overnight's availability — the calculator can't produce a correct answer without it. (This is the one assumption I'd most want a human to confirm — the alternative is silently treating unknown as "non-UK.")
5. **Currency**: all order values and costs are GBP; no conversion logic in scope.
6. **£50 threshold is inclusive** ("£50 and over" → free), consistent with the wording given.

---

### Table 1 — Charges a flat rate for Standard shipping regardless of order value or destination

| Scenario | Order Value | Destination | Cost? |
|---|---|---|---|
| Standard rate applies unconditionally | {£0.00, £49.99, £50.00, £500.00} | {UK, Ireland, Other} | £3.99 |

### Table 2 — Prices Express shipping from order value and UK free-tier eligibility

*Free Shipping Threshold (Policy) = £50.00 throughout.*

| Scenario | Order Value | Destination | Cost? |
|---|---|---|---|
| Below threshold, any destination | £49.99 | {UK, Ireland, Other} | £9.99 |
| At threshold, UK — free tier applies | £50.00 | UK | £0.00 |
| At threshold, non-UK — free tier does not apply | £50.00 | {Ireland, Other} | £9.99 |

### Table 3 — Determines Overnight shipping availability and cost from destination

| Scenario | Destination | Available? | Cost? | Unavailable Reason? |
|---|---|---|---|---|
| UK destination | UK | yes | £14.99 | |
| Ireland destination | Ireland | yes | £14.99 | |
| Destination outside UK/Ireland | {France, USA} | no | | Overnight is only available for UK and Ireland |

### Table 4 — Rejects shipping requests with invalid order value or missing destination

| Scenario | Order Value | Destination | Valid? | Rejection Reason? |
|---|---|---|---|---|
| Zero-value order, valid destination | £0.00 | UK | yes | |
| Negative order value | -£0.01 | UK | no | Order value cannot be negative |
| Missing destination | £49.99 | (blank) | no | Destination is required |

---

## Open questions (not blocking, worth a human sign-off)

- Should Express really stay at £9.99 for non-UK orders ≥£50, or should it be free everywhere and "UK only" was meant to restrict something else (e.g., Express availability itself)? Assumption 1 above.
- Should an unrecognized/unsupported destination string (e.g., a typo) be treated the same as "missing" (rejected), or default to the "Other" behavior?
- Is there a maximum order value or currency other than GBP to consider? Assumed out of scope.

This is structured to hand straight to `/tabletest` if you want executable table-driven tests generated from it — let me know if you'd like that next, or if you want to resolve any of the open questions first.