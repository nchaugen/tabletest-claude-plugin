Looking at the rules you've described, I can see two distinct concerns worth separating: **which state transitions are allowed**, and **return eligibility** for delivered orders. Let me draft both.

---

## Table 1: Order Status Transition

| Scenario | Current Status | Requested Status | Allowed? | Reason? |
|---|---|---|---|---|
| Standard order confirmation | PENDING | CONFIRMED | yes | |
| Order dispatched | CONFIRMED | SHIPPED | yes | |
| Order received | SHIPPED | DELIVERED | yes | |
| Customer cancels before confirmation | PENDING | CANCELLED | yes | |
| Customer cancels after confirmation | CONFIRMED | CANCELLED | yes | |
| Cancellation attempt after dispatch | SHIPPED | CANCELLED | no | Already dispatched |
| Cancellation attempt on delivered order | DELIVERED | CANCELLED | ? | Open: is this the return flow, or a separate error? |
| Reactivating a cancelled order | CANCELLED | {PENDING, CONFIRMED} | ? | Open: can cancelled orders be reopened? |
| Skipping confirmation stage | PENDING | SHIPPED | ? | Open: are stage skips allowed? |
| Backwards transition | SHIPPED | CONFIRMED | no | Cannot reverse |

---

## Table 2: Return Request Eligibility

| Scenario | Current Status | Days Since Delivery | Return Window (Policy) | Return Eligible? | Reason? |
|---|---|---|---|---|---|
| Early return request | DELIVERED | 5 | 30 days | yes | |
| Return on last day | DELIVERED | 30 | 30 days | yes | |
| Return one day late | DELIVERED | 31 | 30 days | no | Return window expired |
| Same-day return | DELIVERED | 0 | 30 days | yes | |
| Return attempted on non-delivered order | {PENDING, CONFIRMED, SHIPPED} | | 30 days | no | Order not yet delivered |
| Return attempted on cancelled order | CANCELLED | | 30 days | no | Order was cancelled |

---

## Open Questions to Resolve

1. **DELIVERED → CANCELLED**: Is this a separate cancellation path (likely an error), or does requesting a return effectively supersede it? The two tables may need a third one for the return lifecycle if a return goes through its own states (e.g., `RETURN_REQUESTED`, `RETURN_APPROVED`).

2. **Stage skipping**: Can PENDING jump straight to SHIPPED, or must every stage be visited in order? The answer changes how many "invalid transition" rows you need.

3. **CANCELLED reactivation**: Can a cancelled order be reopened, or is CANCELLED a terminal state?

4. **Return window boundary**: I've assumed day 30 is still within the window (≤ 30 days). Confirm whether the rule is `< 30` or `≤ 30` — this is exactly the kind of thing that becomes a bug if left ambiguous.

5. **"Days Since Delivery" calculation**: Calendar days or business days? Calculated from midnight or exact timestamp?

Once you've resolved these, the tables map directly to a `@TableTest` — want me to help convert them once you're ready to implement?