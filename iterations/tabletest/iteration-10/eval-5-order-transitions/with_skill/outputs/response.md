Good candidate for two tables — the core state machine and the return window have different input shapes.

---

## Order Status Transition

| Scenario | Current Status | Target Status | Allowed? | Reason? |
|---|---|---|---|---|
| Confirm a pending order | PENDING | CONFIRMED | yes | |
| Cancel a pending order | PENDING | CANCELLED | yes | |
| Ship a confirmed order | CONFIRMED | SHIPPED | yes | |
| Cancel a confirmed order | CONFIRMED | CANCELLED | yes | |
| Deliver a shipped order | SHIPPED | DELIVERED | yes | |
| Cancel after dispatch | {SHIPPED, DELIVERED} | CANCELLED | no | Order already dispatched |
| Re-cancel an order | CANCELLED | {PENDING, CONFIRMED, SHIPPED, DELIVERED} | no | Order already cancelled |

The `{SHIPPED, DELIVERED}` value set on row 6 states the rule directly: cancellation is blocked from any post-dispatch state, not just SHIPPED. If their reasons differ (e.g. "already delivered" vs "already dispatched"), split that row.

---

## Return Request Eligibility

This has its own time dimension, so it gets its own table. The return window is a policy constant — making it an explicit column rather than burying it in the reason keeps the boundary row legible and signals this value is configurable.

| Scenario | Days Since Delivery | Return Window (Policy) | Eligible? | Reason? |
|---|---|---|---|---|
| Return well within window | 15 | 30 days | yes | |
| Return on the final day | 30 | 30 days | yes | |
| Return one day after window | 31 | 30 days | no | Return window expired |
| Return long after window | 90 | 30 days | no | Return window expired |

---

## Open Questions

A few things the table exposes that aren't yet specified:

1. **Day 30 boundary** — the rows above treat it as inclusive (≤ 30 days). Is that right, or is it strictly less than?
2. **Clock start** — does the 30-day window begin at delivery timestamp, or midnight on the delivery date?
3. **Return state name** — what state does the order enter when flagged? (`RETURN_REQUESTED`? `RETURNING`?) This becomes a column in the return table once decided.
4. **State skipping** — is `PENDING → SHIPPED` or `PENDING → DELIVERED` possible (e.g. via admin override), or is every intermediate state required?
5. **Already-cancelled reinstatement** — can a CANCELLED order be reinstated to PENDING? The table currently blocks it; confirm that's intentional.

The boundary rows (days 30 and 31) are where implementation bugs tend to appear — worth making sure those are confirmed before coding the eligibility check.