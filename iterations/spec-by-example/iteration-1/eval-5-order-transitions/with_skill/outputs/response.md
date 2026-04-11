Two concerns are worth separating here: **which transitions are allowed** and **return eligibility** (the 30-day rule). Here are example tables for both.

---

## Order Status Transition

| Scenario | Current Status | Requested Status | Allowed? | Reason? |
|---|---|---|---|---|
| Confirm pending order | PENDING | CONFIRMED | yes | |
| Cancel before confirmation | PENDING | CANCELLED | yes | |
| Dispatch confirmed order | CONFIRMED | SHIPPED | yes | |
| Cancel confirmed order | CONFIRMED | CANCELLED | yes | |
| Mark order as delivered | SHIPPED | DELIVERED | yes | |
| Cancel after dispatch | SHIPPED | CANCELLED | no | Already dispatched |
| Cancel after delivery | DELIVERED | CANCELLED | no | Already delivered — use return process |
| Any transition from terminal state | CANCELLED | {PENDING, CONFIRMED, SHIPPED, DELIVERED} | no | Order is cancelled |
| Skip confirmation step | PENDING | SHIPPED | no | Must be confirmed first |
| Backward transition | {CONFIRMED, SHIPPED, DELIVERED} | PENDING | no | Cannot reverse status |

---

## Return Eligibility

| Scenario | Order Status | Days Since Delivery | Return Window (Policy) | Eligible? | Reason? |
|---|---|---|---|---|---|
| Early return request | DELIVERED | 5 | 30 days | yes | |
| Return on final day of window | DELIVERED | 30 | 30 days | yes | |
| Return one day past window | DELIVERED | 31 | 30 days | no | Return window expired |
| Return well past window | DELIVERED | 60 | 30 days | no | Return window expired |
| Order not yet delivered | {PENDING, CONFIRMED, SHIPPED} | — | 30 days | no | Not yet delivered |
| Cancelled order | CANCELLED | — | 30 days | no | Order cancelled |

---

A few things worth resolving before coding:

1. **What does "flagged for return" produce?** Is `RETURN_REQUESTED` a new status, or is it a flag alongside `DELIVERED`? The transition table currently treats these as separate concerns — if it becomes a state, it needs rows of its own.
2. **Boundary on day 30**: The table above treats day 30 as *within* the window (eligible). Confirm this is the intended behaviour — "within 30 days" can mean `< 30` or `<= 30`.
3. **Skipping steps**: Is `PENDING → SHIPPED` (skipping CONFIRMED) truly disallowed, or does it happen for certain order types? If some order types skip confirmation, that needs an input column.
4. **Same-status transition**: What happens if `PENDING → PENDING` is requested? Currently not specified.

Once you've settled those, this maps directly to a `@TableTest` via `/tabletest`.