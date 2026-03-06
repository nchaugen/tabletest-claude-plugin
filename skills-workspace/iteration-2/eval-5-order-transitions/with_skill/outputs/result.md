# Order Status Transition Examples

## Table 1: Order Cancellation

| Scenario                                    | Current Status         | Target Status | Cancellation Window (Policy) | Allowed? | Reason?                        |
|---------------------------------------------|------------------------|---------------|------------------------------|----------|--------------------------------|
| Cancel a pending order                      | PENDING                | CANCELLED     | Before shipment              | yes      |                                |
| Cancel a confirmed but unshipped order      | CONFIRMED              | CANCELLED     | Before shipment              | yes      |                                |
| Cancel an order that has already shipped    | SHIPPED                | CANCELLED     | Before shipment              | no       | Order already dispatched       |
| Cancel a delivered order                    | DELIVERED              | CANCELLED     | Before shipment              | no       | Order already delivered        |
| Cancel an already-cancelled order           | CANCELLED              | CANCELLED     | Before shipment              | no       | Order already cancelled        |
| Cancellation blocked regardless of state past shipment | {SHIPPED, DELIVERED} | CANCELLED | Before shipment           | no       | Order already dispatched or delivered |

---

## Table 2: Standard Status Progression

| Scenario                                    | Current Status | Target Status | Allowed? | Reason?                              |
|---------------------------------------------|----------------|---------------|----------|--------------------------------------|
| Confirm a pending order                     | PENDING        | CONFIRMED     | yes      |                                      |
| Ship a confirmed order                      | CONFIRMED      | SHIPPED       | yes      |                                      |
| Deliver a shipped order                     | SHIPPED        | DELIVERED     | yes      |                                      |
| Skip confirmation and ship directly         | PENDING        | SHIPPED       | no       | Must be confirmed before shipping    |
| Redeliver an already-delivered order        | DELIVERED      | DELIVERED     | no       | Order already delivered              |
| Revert a shipped order to confirmed         | SHIPPED        | CONFIRMED     | no       | Cannot move backwards                |
| Revert a confirmed order to pending         | CONFIRMED      | PENDING       | no       | Cannot move backwards                |
| Transition from cancelled to any active state | CANCELLED    | {PENDING, CONFIRMED, SHIPPED, DELIVERED} | no | Order is cancelled      |

---

## Table 3: Return Flag Eligibility

| Scenario                                          | Current Status | Days Since Delivery | Return Window (Policy) | Return Flag Allowed? | Reason?                        |
|---------------------------------------------------|----------------|---------------------|------------------------|----------------------|--------------------------------|
| Return request within window                      | DELIVERED      | 10                  | 30 days                | yes                  |                                |
| Return request on the last day of the window      | DELIVERED      | 30                  | 30 days                | yes                  |                                |
| Return request one day past the window            | DELIVERED      | 31                  | 30 days                | no                   | Return window has expired      |
| Return request well past the window               | DELIVERED      | 60                  | 30 days                | no                   | Return window has expired      |
| Return request on a non-delivered order           | {PENDING, CONFIRMED, SHIPPED} | —    | 30 days                | no                   | Order not yet delivered        |
| Return request on a cancelled order               | CANCELLED      | —                   | 30 days                | no                   | Order is cancelled             |

> **Open question:** Does the 30-day window start from the moment of delivery (timestamp) or the calendar day of delivery? At-boundary cases (day 30) may behave differently depending on the answer.

> **Open question:** Can a return-flagged order be cancelled, or does flagging for return replace the cancellation path?
