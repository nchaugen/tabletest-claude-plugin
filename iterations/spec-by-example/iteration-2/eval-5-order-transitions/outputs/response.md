Here are two example tables — status transitions are one concern, return eligibility is a separate one (different lifecycle, different trigger).

## Table 1: Order Status Transition

| Scenario | Current Status | Target Status | Allowed? | Reason? |
|---|---|---|---|---|
| Confirm a pending order | PENDING | CONFIRMED | yes | |
| Ship a confirmed order | CONFIRMED | SHIPPED | yes | |
| Deliver a shipped order | SHIPPED | DELIVERED | yes | |
| Cancel before shipment | {PENDING, CONFIRMED} | CANCELLED | yes | |
| Cancel after shipment | SHIPPED | CANCELLED | no | Already dispatched |
| Cancel a delivered order | DELIVERED | CANCELLED | no | Already delivered |
| Cancel an already-cancelled order | CANCELLED | CANCELLED | no | Already cancelled |
| Skip confirmation | PENDING | SHIPPED | no | Must be confirmed first |
| Skip straight to delivered | {PENDING, CONFIRMED} | DELIVERED | no | Must be shipped first |
| Move backwards in the flow | {CONFIRMED, SHIPPED, DELIVERED} | PENDING | no | Cannot move backwards |
| Change status from a terminal state | {CANCELLED, DELIVERED} | {PENDING, CONFIRMED, SHIPPED} | no | Order reached a terminal state |

## Table 2: Return Flag Eligibility

| Scenario | Order Status | Days Since Delivery | Return Window (Policy) | Return Flag Allowed? | Reason? |
|---|---|---|---|---|---|
| Well within return window | DELIVERED | 5 days | 30 days | yes | |
| Exactly at the boundary | DELIVERED | 30 days | 30 days | yes | |
| Just past the boundary | DELIVERED | 31 days | 30 days | no | Return window closed |
| Long past the window | DELIVERED | 90 days | 30 days | no | Return window closed |
| Order not yet delivered | {PENDING, CONFIRMED, SHIPPED} | | 30 days | no | Not yet delivered |
| Order was cancelled | CANCELLED | | 30 days | no | Order was cancelled |

`Days Since Delivery` is blank for non-delivered/cancelled orders — there's no delivery date to count from, not zero.

## Assumptions made (no follow-up possible, so stated explicitly)

1. **Linear happy path** — an order must pass through every prior state (PENDING → CONFIRMED → SHIPPED → DELIVERED); no skipping or moving backwards.
2. **CANCELLED and DELIVERED are terminal** for status transitions — nothing moves *out* of them via the normal transition rule (return flagging is modeled separately, not as a status change).
3. **30-day window is inclusive** — day 30 itself is still eligible; day 31 is not. This is the one genuinely ambiguous point in your description ("within 30 days") — worth confirming with a domain owner before coding.
4. **Return flag is not a status** — I treated it as a separate boolean/flag on a DELIVERED order rather than a sixth state, since you only listed five statuses. If a return actually needs its own status (e.g., `RETURN_REQUESTED`), Table 2 would need to become a transition table like Table 1 instead.

## Open questions

- Can a DELIVERED order be flagged for return more than once (idempotency)?
- Does requesting a return change the order's status at all, or purely set a flag/timestamp alongside DELIVERED?
- Is there any path back from CANCELLED (e.g., reopening), or is it strictly terminal?

Once these are resolved, this maps directly onto `/tabletest` — one `@TableTest` per table.