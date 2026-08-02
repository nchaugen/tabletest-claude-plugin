Since this is a non-interactive run, I'll state my assumptions up front and build the tables directly rather than pausing for clarification.

## Assumptions

1. **Return flagging is not a status transition.** The five states you listed don't include anything like `RETURN_REQUESTED`, so I'm modeling "flagged for return" as a separate boolean attribute on a `DELIVERED` order, not a sixth state. Worth revisiting if you actually want a `RETURNED` state in the enum.
2. **"Within 30 days" is inclusive** — day 30 still qualifies, day 31 does not. Flagged as open below since this is a common off-by-one dispute.
3. **The 30-day clock starts at the delivery date/timestamp.**
4. **Re-flagging an already-flagged order is unresolved** — I don't know if it should be idempotent (silently allowed) or rejected as a duplicate. Left as an open cell.
5. Attempting an action from a state where it's not valid (skipping a step, repeating a terminal action) rejects rather than silently no-ops.

These split into two tables because they're two different rules: how an order *moves between statuses*, and whether a *delivered* order is *eligible for return* — the second only ever looks at one status value plus elapsed time, so folding it into the first table would leave most of its columns blank for every other row.

---

## Table 1: Applies a Status Transition to an Order

| Scenario | Status Before | Action | Status After? | Rejection Reason? |
|---|---|---|---|---|
| Confirm a pending order | PENDING | CONFIRM | CONFIRMED | |
| Ship a confirmed order | CONFIRMED | SHIP | SHIPPED | |
| Deliver a shipped order | SHIPPED | DELIVER | DELIVERED | |
| Cancel a pending order | PENDING | CANCEL | CANCELLED | |
| Cancel a confirmed order | CONFIRMED | CANCEL | CANCELLED | |
| Cancel a shipped order | SHIPPED | CANCEL | SHIPPED | Cannot cancel after shipment |
| Cancel a delivered order | DELIVERED | CANCEL | DELIVERED | Cannot cancel after shipment |
| Cancel an already-cancelled order | CANCELLED | CANCEL | CANCELLED | Order is already cancelled |
| Ship a pending order | PENDING | SHIP | PENDING | Order must be confirmed before shipping |
| Deliver a confirmed order | CONFIRMED | DELIVER | CONFIRMED | Order must be shipped before delivery |
| Confirm a cancelled order | CANCELLED | CONFIRM | CANCELLED | Order is cancelled |

Note: `Status After?` repeats `Status Before` on rejected rows to show the transition didn't happen — nothing changes state on a rejection.

---

## Table 2: Flags a Delivered Order for Return

| Scenario | Status Before | Days Since Delivery | Return Window (Policy) | Already Flagged | Return Flag Allowed? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Return flagged 5 days after delivery | DELIVERED | 5 | 30 | no | yes | |
| Return flagged exactly 30 days after delivery | DELIVERED | 30 | 30 | no | yes | |
| Return flagged 31 days after delivery | DELIVERED | 31 | 30 | no | no | Return window has expired |
| Return flagged before delivery | {PENDING, CONFIRMED, SHIPPED} | | 30 | no | no | Order has not been delivered |
| Return flagged on a cancelled order | CANCELLED | | 30 | no | no | Order was cancelled |
| Return flagged twice | DELIVERED | 5 | 30 | yes | ? | ? |

`Days Since Delivery` is blank for the pre-delivery rows — there's no delivery date to measure from, which is a different thing from "0 days."

---

## Open Questions

- **Boundary at day 30**: confirmed as inclusive per assumption #2 — flag if this should actually be exclusive (window closes *at* day 30, not after it).
- **Repeat flagging**: does a second flag attempt on an already-flagged order succeed silently, get rejected as a duplicate, or reset the clock? This changes both the `Return Flag Allowed?` value and whether `Already Flagged` even needs to exist as a column.
- **Does cancelling a `SHIPPED`/`DELIVERED` order need a *different* rejection message than "Cannot cancel after shipment"?** I used the same reason for both since the rule is identical, but you may want delivered orders to point users toward the return flow instead.

Once you settle those, this maps cleanly onto two functions (`applyTransition(status, action)` and `flagForReturn(status, daysSinceDelivery, alreadyFlagged)`), each testable directly from its table via `/tabletest`.