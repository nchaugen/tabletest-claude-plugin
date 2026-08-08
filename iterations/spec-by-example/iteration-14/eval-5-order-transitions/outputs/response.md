Here are two example tables — one for the core state machine, one for the return-eligibility rule. I split them because they're genuinely different concerns: the transition table asks "is this action allowed from this status," while return eligibility asks "how long has this order been delivered." Combining them would have blurred both.

## Table 1 — Determines the Result of an Order Status Transition

| Scenario | Order Status (Before) | Action | Transition Allowed? | New Status? | Rejection Reason? |
|---|---|---|---|---|---|
| Confirm a pending order | PENDING | confirm | yes | CONFIRMED | |
| Confirm from any non-pending status | {CONFIRMED, SHIPPED, DELIVERED, CANCELLED} | confirm | no | | Order must be PENDING to confirm |
| Ship a confirmed order | CONFIRMED | ship | yes | SHIPPED | |
| Ship from any non-confirmed status | {PENDING, SHIPPED, DELIVERED, CANCELLED} | ship | no | | Order must be CONFIRMED to ship |
| Deliver a shipped order | SHIPPED | deliver | yes | DELIVERED | |
| Deliver from any non-shipped status | {PENDING, CONFIRMED, DELIVERED, CANCELLED} | deliver | no | | Order must be SHIPPED to deliver |
| Cancel a pending order | PENDING | cancel | yes | CANCELLED | |
| Cancel a confirmed order | CONFIRMED | cancel | yes | CANCELLED | |
| Cancel an order that has already shipped | {SHIPPED, DELIVERED} | cancel | no | | Cannot cancel once the order has shipped |
| Cancel an already-cancelled order | CANCELLED | cancel | no | | Order is already cancelled |

## Table 2 — Determines Return Eligibility for a Delivered Order

| Scenario | Order Status | Days Since Delivery | Return Window (Policy, days) | Return Already Requested | Eligible for Return? | Reason? |
|---|---|---|---|---|---|---|
| Just delivered | DELIVERED | 0 | 30 | no | yes | |
| At the return window boundary | DELIVERED | 30 | 30 | no | yes | |
| Just past the return window | DELIVERED | 31 | 30 | no | no | Return window has expired |
| Order not yet delivered | {PENDING, CONFIRMED, SHIPPED, CANCELLED} | | 30 | no | no | Order has not been delivered yet |
| Return already requested for this order | DELIVERED | 10 | 30 | yes | no | Return already requested for this order |

## Assumptions made (non-interactive run, please confirm)

1. **"Flagged for return" is a boolean flag on a DELIVERED order**, not a 6th status — your state list only has 5 values, so I didn't invent `RETURN_REQUESTED` as a status. Open question below.
2. **Day 30 is still inside the window** (inclusive); day 31 is not. If your intent is "30 days" meaning strictly less than 30, flip the boundary rows.
3. **Rejection messages are generic per action** ("must be in status X"), not state-specific. E.g. confirming a SHIPPED order and confirming a CANCELLED order give the same message. If you want distinct messages per invalid state, that value set needs to be split back into individual rows.
4. **Re-cancelling an already-cancelled order is rejected**, not a silent no-op — treated as its own case since the reason differs from the "shipped" rejection.
5. **Days Since Delivery** is measured from the delivery timestamp in whole days, matching the rule's day-level granularity.

## Open question to resolve before coding

- When a return is successfully flagged, what happens to the order afterward — does it stay `DELIVERED` with a separate `returnRequested` flag (as modeled above), or should there be an explicit new status? This affects whether Table 2's "Eligible for Return?" column is the whole story or needs a companion transition table once the flag fires.

Once you confirm the boundary direction (#2) and the flag-vs-status question, this is ready to hand to `/tabletest` for the executable version.