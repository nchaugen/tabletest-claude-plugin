# Order Status Transition

## Table 1: Order Cancellation

| Scenario | Current Status | Cancellation Window (Policy) | Transition Allowed? | Reason? |
|---|---|---|---|---|
| Standard pending order cancelled | PENDING | before SHIPPED | yes | |
| Confirmed order cancelled before shipment | CONFIRMED | before SHIPPED | yes | |
| Shipped order cancellation attempted | SHIPPED | before SHIPPED | no | Cancellation not allowed after shipment |
| Delivered order cancellation attempted | DELIVERED | before SHIPPED | no | Cancellation not allowed after shipment |
| Already cancelled order cancelled again | CANCELLED | before SHIPPED | no | Order already cancelled |
| Cancellation regardless of pending or confirmed | {PENDING, CONFIRMED} | before SHIPPED | yes | |

## Table 2: Return Flag Eligibility (Delivered Orders)

| Scenario | Current Status | Days Since Delivery | Return Window (Policy) | Return Flag Allowed? | Reason? |
|---|---|---|---|---|---|
| Delivered order flagged within window | DELIVERED | 1 | 30 days | yes | |
| Delivered order flagged on last day of window | DELIVERED | 30 | 30 days | yes | |
| Delivered order flagged one day after window | DELIVERED | 31 | 30 days | no | Outside 30-day return window |
| Delivered order flagged well after window | DELIVERED | 60 | 30 days | no | Outside 30-day return window |
| Pending order return flag attempted | PENDING | | 30 days | no | Order not yet delivered |
| Confirmed order return flag attempted | CONFIRMED | | 30 days | no | Order not yet delivered |
| Shipped order return flag attempted | SHIPPED | | 30 days | no | Order not yet delivered |
| Cancelled order return flag attempted | CANCELLED | | 30 days | no | Order was cancelled |
| Delivered order flagged on day zero (same day) | DELIVERED | 0 | 30 days | yes | |

## Open Questions

| Scenario | Current Status | Days Since Delivery | Return Window (Policy) | Return Flag Allowed? | Open Questions |
|---|---|---|---|---|---|
| Cancelled order that was previously delivered? | CANCELLED | 5 | 30 days | ? | Can an order be delivered and then cancelled? Is CANCELLED reachable from DELIVERED? |
| Already-flagged order flagged again | DELIVERED | 5 | 30 days | ? | Can a return flag be applied more than once? Is RETURN_REQUESTED a state? |
