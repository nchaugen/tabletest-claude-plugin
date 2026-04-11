Two concerns here: **which transitions are allowed**, and **return eligibility** for delivered orders. I'll build a table for each.

---

## Table 1: Order Status Transition

| Scenario | Current Status | Target Status | Allowed? | Reason? |
|---|---|---|---|---|
| Confirm a new order | PENDING | CONFIRMED | yes | |
| Dispatch a confirmed order | CONFIRMED | SHIPPED | yes | |
| Mark order received | SHIPPED | DELIVERED | yes | |
| Cancel before processing | PENDING | CANCELLED | yes | |
| Cancel after confirmation, before dispatch | CONFIRMED | CANCELLED | yes | |
| Cancel after dispatch | SHIPPED | CANCELLED | no | Already dispatched |
| Cancel a delivered order | DELIVERED | CANCELLED | no | Already received |
| Cancel an already-cancelled order | CANCELLED | {CONFIRMED, SHIPPED, DELIVERED, CANCELLED} | no | Already cancelled |
| Skip confirmation step | PENDING | SHIPPED | no | Must confirm first |
| Revert confirmed order to pending | CONFIRMED | PENDING | no | Cannot move backward |

**Open questions:**
- Can DELIVERED move to any state other than the return flow? (Or is it terminal?)
- Is CANCELLED → PENDING a recovery path, or is cancellation always terminal?
- What happens with PENDING → DELIVERED or other multi-step skips — one blanket rule, or enumerate each?

---

## Table 2: Return Flag Eligibility

| Scenario | Current Status | Days Since Delivery | Return Window (Policy) | Eligible for Return? | Reason? |
|---|---|---|---|---|---|
| Return on delivery day | DELIVERED | 0 | 30 days | yes | |
| Return mid-window | DELIVERED | 15 | 30 days | yes | |
| Return on last day of window | DELIVERED | 30 | 30 days | ? | Is day 30 inclusive? |
| Return one day past window | DELIVERED | 31 | 30 days | no | Past return window |
| Return long after delivery | DELIVERED | 60 | 30 days | no | Past return window |
| Flag return on non-delivered order | {PENDING, CONFIRMED, SHIPPED, CANCELLED} | | 30 days | no | Order not yet delivered |

**Open questions:**
- Is day 30 inclusive or exclusive? The boundary row (`Days Since Delivery = 30`) is marked `?` — pin this down before coding the comparison operator.
- What state does a flagged-for-return order move to? `RETURN_REQUESTED` isn't in the current state list — is it a new state, or a separate flag on DELIVERED?
- Who can trigger a return flag — the customer, an admin, or both? Does the actor affect eligibility?

---

The `Return Window (Policy)` column is intentionally explicit — if this ever varies by product category or customer tier, it becomes a new axis rather than a buried constant. Worth confirming now whether 30 days is universal.

Once the open questions are resolved, both tables map directly to `@TableTest` — hand them to `/tabletest` to start implementation.