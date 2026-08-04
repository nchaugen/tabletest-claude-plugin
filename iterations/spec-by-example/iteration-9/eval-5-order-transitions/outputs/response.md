Two separate concerns emerged here: the state machine itself (which actions are valid from which status) and the return-eligibility window (a time-boundary rule that only applies to `DELIVERED` orders). I've kept them as two tables rather than one, since neither the states nor the actions in table 1 have anything to do with elapsed time, and vice versa.

## Table 1: Transitions an order between statuses

The order exposes four actions — `CONFIRM`, `SHIP`, `DELIVER`, `CANCEL` — no action reverts a `CANCELLED` order.

| Scenario | Status Before | Action | Allowed? | Status After? | Rejection Reason? |
|---|---|---|---|---|---|
| Pending order confirmed | PENDING | CONFIRM | yes | CONFIRMED | |
| Order confirmed from a non-pending, non-cancelled status | {CONFIRMED, SHIPPED, DELIVERED} | CONFIRM | no | unchanged | Only a PENDING order can be confirmed |
| Confirmed order shipped | CONFIRMED | SHIP | yes | SHIPPED | |
| Order shipped from a non-confirmed, non-cancelled status | {PENDING, SHIPPED, DELIVERED} | SHIP | no | unchanged | Only a CONFIRMED order can be shipped |
| Shipped order delivered | SHIPPED | DELIVER | yes | DELIVERED | |
| Order delivered from a non-shipped, non-cancelled status | {PENDING, CONFIRMED, DELIVERED} | DELIVER | no | unchanged | Only a SHIPPED order can be delivered |
| Pending order cancelled | PENDING | CANCEL | yes | CANCELLED | |
| Confirmed order cancelled | CONFIRMED | CANCEL | yes | CANCELLED | |
| Shipped or delivered order cancelled | {SHIPPED, DELIVERED} | CANCEL | no | unchanged | Cannot cancel after the order has shipped |
| Any action attempted on a cancelled order | CANCELLED | {CONFIRM, SHIP, DELIVER, CANCEL} | no | unchanged | Order is cancelled and cannot be modified |

## Table 2: Determines return eligibility for a delivered order

| Scenario | Order Status | Days Since Delivery | Return Window (Policy, days) | Return Allowed? | Rejection Reason? |
|---|---|---|---|---|---|
| Return flagged on an order that hasn't been delivered | {PENDING, CONFIRMED, SHIPPED, CANCELLED} | | 30 | no | Only delivered orders can be flagged for return |
| Return flagged immediately after delivery | DELIVERED | 0 | 30 | yes | |
| Return flagged on the last eligible day | DELIVERED | 30 | 30 | yes | |
| Return flagged after the window has closed | DELIVERED | 31 | 30 | no | Return window has expired |

`Days Since Delivery` is blank (not 0) for undelivered orders — there's no delivery date to measure from.

## Assumptions made (non-interactive, so I committed to these rather than leaving them open)

1. Transitions are strictly sequential, one step at a time — no skipping stages (`PENDING`→`SHIPPED` directly) and no going backward.
2. A cancelled order gives one consistent "order is cancelled" message for any action, rather than each action reporting its own precondition failure separately.
3. "Not after SHIPPED" means cancellation is blocked from `SHIPPED` onward — `SHIPPED` itself is not cancellable, only `PENDING` and `CONFIRMED` are.
4. The 30-day return window is **inclusive** (day 30 eligible, day 31 not).
5. Return-flagging is a separate action/flag, not a 6th status — you only named 5 states, so I didn't invent a `RETURN_REQUESTED` status.

## Open questions worth confirming before/while coding

- Should the cancelled-order rejection message differ per attempted action, or is the single generic message (assumption 2) actually what you want?
- Is the 30-day window inclusive or exclusive at the boundary (assumption 4)?
- Can a `DELIVERED` order be flagged for return more than once — is that idempotent or an error?
- Is there any override path (support/admin) that bypasses the sequential-only rule or the post-shipment cancellation block?

Once these are confirmed, this maps directly onto a `@TableTest` per table if you want to hand it to `/tabletest` for the executable version.