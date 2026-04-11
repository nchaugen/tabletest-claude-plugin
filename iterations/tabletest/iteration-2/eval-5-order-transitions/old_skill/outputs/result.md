# Order Status Transition — Example Tables

---

## Table 1: Order Status Transition

| Scenario                                          | Current Status | Requested Transition | Allowed? | Rejection Reason?                        |
|---------------------------------------------------|----------------|----------------------|----------|------------------------------------------|
| Confirm a pending order                           | PENDING        | CONFIRMED            | yes      |                                          |
| Cancel a pending order                            | PENDING        | CANCELLED            | yes      |                                          |
| Ship a confirmed order                            | CONFIRMED      | SHIPPED              | yes      |                                          |
| Cancel a confirmed order                          | CONFIRMED      | CANCELLED            | yes      |                                          |
| Mark a shipped order as delivered                 | SHIPPED        | DELIVERED            | yes      |                                          |
| Cancel a shipped order                            | SHIPPED        | CANCELLED            | no       | Cannot cancel after order has shipped    |
| Cancel a delivered order                          | DELIVERED      | CANCELLED            | no       | Cannot cancel after order has shipped    |
| Re-confirm an already confirmed order             | CONFIRMED      | CONFIRMED            | no       | Order is already in CONFIRMED state      |
| Ship an order that has not been confirmed         | PENDING        | SHIPPED              | no       | Order must be CONFIRMED before shipping  |
| Move a delivered order back to shipped            | DELIVERED      | SHIPPED              | no       | Cannot reverse a completed transition    |
| Cancel an already cancelled order                 | CANCELLED      | CANCELLED            | no       | Order is already CANCELLED               |
| Attempt any transition on a cancelled order       | CANCELLED      | {PENDING, CONFIRMED, SHIPPED, DELIVERED} | no | Order is CANCELLED and cannot be transitioned |

---

## Table 2: Return Flag Eligibility

This table covers whether a DELIVERED order may be flagged for return. The 30-day window is an explicit policy input so the boundary is visible from the table alone.

| Scenario                                      | Current Status | Days Since Delivery | Return Window (Policy) | Return Flag Allowed? | Rejection Reason?                        |
|-----------------------------------------------|----------------|---------------------|------------------------|----------------------|------------------------------------------|
| Return requested well within window           | DELIVERED      | 5                   | 30 days                | yes                  |                                          |
| Return requested on the last eligible day     | DELIVERED      | 30                  | 30 days                | yes                  |                                          |
| Return requested one day after window closes  | DELIVERED      | 31                  | 30 days                | no                   | Return window of 30 days has expired     |
| Return requested long after delivery          | DELIVERED      | 90                  | 30 days                | no                   | Return window of 30 days has expired     |
| Return flag attempted on non-delivered order  | {PENDING, CONFIRMED, SHIPPED, CANCELLED} | — | 30 days     | no                   | Order must be DELIVERED to request return |

---

## Open Questions

| Question                                                                 | Relevant Table              |
|--------------------------------------------------------------------------|-----------------------------|
| Can a DELIVERED order transition to any state other than return flagging? Is there an explicit RETURNED state? | Table 1 / Table 2 |
| Does the 30-day window start from the delivery timestamp or the delivery date (end of day)? | Table 2         |
| If an order is flagged for return, does it move to a new RETURN_REQUESTED state, or is that a separate concern? | Table 2 |
| Can a PENDING order skip directly to SHIPPED (e.g., same-day fulfilment)? Current table says no — confirm. | Table 1 |
