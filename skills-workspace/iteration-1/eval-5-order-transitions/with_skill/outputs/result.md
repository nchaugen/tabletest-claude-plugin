# Order Status Transition

## Table 1: Order Cancellation

| Scenario | Current Status | Transition To | Return Window (Policy) | Allowed? | Rejection Reason? |
|---|---|---|---|---|---|
| Standard cancellation of pending order | PENDING | CANCELLED | 30 days | yes | |
| Cancellation of confirmed order | CONFIRMED | CANCELLED | 30 days | yes | |
| Cancellation attempt after shipment | SHIPPED | CANCELLED | 30 days | no | Cannot cancel after shipment |
| Cancellation attempt on delivered order | DELIVERED | CANCELLED | 30 days | no | Cannot cancel after shipment |
| Cancellation of already cancelled order | CANCELLED | CANCELLED | 30 days | no | Order already cancelled |
| Cancellation regardless of return window | PENDING | CANCELLED | {30 days, 60 days} | yes | |
| Confirmation of pending order | PENDING | CONFIRMED | 30 days | yes | |
| Shipping a confirmed order | CONFIRMED | SHIPPED | 30 days | yes | |
| Delivering a shipped order | SHIPPED | DELIVERED | 30 days | yes | |
| Skipping confirmation — pending to shipped | PENDING | SHIPPED | 30 days | no | Must be CONFIRMED before SHIPPED |
| Reactivating a cancelled order | CANCELLED | CONFIRMED | 30 days | no | Cancelled orders cannot be reactivated |
| Delivering an order that was not shipped | CONFIRMED | DELIVERED | 30 days | no | Must be SHIPPED before DELIVERED |


## Table 2: Return Eligibility for Delivered Orders

| Scenario | Current Status | Days Since Delivery | Return Window (Policy) | Return Allowed? | Rejection Reason? |
|---|---|---|---|---|---|
| Return request well within window | DELIVERED | 5 days | 30 days | yes | |
| Return request on final day of window | DELIVERED | 30 days | 30 days | yes | |
| Return request one day after window closes | DELIVERED | 31 days | 30 days | no | Outside 30-day return window |
| Return request long after delivery | DELIVERED | 90 days | 30 days | no | Outside 30-day return window |
| Return request on day of delivery | DELIVERED | 0 days | 30 days | yes | |
| Return request for non-delivered order | SHIPPED | 0 days | 30 days | no | Order not yet delivered |
| Return request for pending order | PENDING | 0 days | 30 days | no | Order not yet delivered |
| Return request for cancelled order | CANCELLED | 0 days | 30 days | no | Order was cancelled, not delivered |
| Extended return window in effect | DELIVERED | 45 days | 60 days | yes | |
| Extended window — just expired | DELIVERED | 61 days | 60 days | no | Outside 60-day return window |
