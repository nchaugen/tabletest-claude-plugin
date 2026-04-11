Two concerns are visible here: **which transitions are allowed** and **when a return can be requested**. I'd suggest two tables — they have different inputs and the 30-day window doesn't apply to the transition rules.

---

## Table 1: Order Status Transition

| Scenario | Current Status | Target Status | Allowed? | Reason? |
|---|---|---|---|---|
| Confirm a pending order | PENDING | CONFIRMED | yes | |
| Cancel before confirmation | PENDING | CANCELLED | yes | |
| Ship a confirmed order | CONFIRMED | SHIPPED | yes | |
| Cancel after confirmation | CONFIRMED | CANCELLED | yes | |
| Deliver a shipped order | SHIPPED | DELIVERED | yes | |
| Cannot cancel after dispatch | {SHIPPED, DELIVERED} | CANCELLED | no | Already dispatched |
| Cannot reactivate a cancelled order | CANCELLED | {PENDING, CONFIRMED, SHIPPED} | no | Order is cancelled |
| Cannot skip confirmation | PENDING | SHIPPED | no | Must confirm first |
| Cannot reverse to prior state | CONFIRMED | PENDING | no | Cannot reverse status |
| Cannot reverse to prior state | SHIPPED | CONFIRMED | no | Cannot reverse status |

**Open questions:**
- Can `PENDING → DELIVERED` or `CONFIRMED → DELIVERED` happen (skipping states), or only the sequential path?
- Can a `DELIVERED` order transition to anything other than a return flag — e.g., can it be cancelled for an admin correction?
- Are there transitions not listed that should be explicitly rejected vs. simply "not a valid move"?

---

## Table 2: Return Request Eligibility

| Scenario | Order Status | Days Since Delivery | Return Window (Policy) | Return Allowed? | Reason? |
|---|---|---|---|---|---|
| Early return request | DELIVERED | 15 | 30 days | yes | |
| Return on last eligible day | DELIVERED | 30 | 30 days | yes | |
| Return one day after window | DELIVERED | 31 | 30 days | no | Return window expired |
| Long after delivery | DELIVERED | 90 | 30 days | no | Return window expired |
| Order not yet delivered | {PENDING, CONFIRMED, SHIPPED} | | 30 days | no | Not yet delivered |
| Cancelled order | CANCELLED | | 30 days | no | Order was cancelled |

The `Return Window (Policy)` column is constant here, but making it explicit signals it's a configurable threshold — and it becomes natural to add rows if the window ever varies (e.g., by order type or loyalty tier).

**Open questions:**
- Is "flagged for return" a new state (e.g., `RETURN_REQUESTED`) or a boolean flag on a `DELIVERED` order? This affects whether it belongs in the transition table or stays separate.
- Is day 30 "within 30 days" (≤ 30) or "before 30 days" (< 30)? The boundary row makes this a decision to confirm rather than assume.
- Are there further states after a return is flagged (`RETURN_PROCESSING`, `RETURNED`, `REFUNDED`)? Those would extend Table 1.

---

Once these are confirmed, `/tabletest` can turn either table directly into `@TableTest` code — the column structure carries over as-is.