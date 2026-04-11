# Order Status Transitions — Worked Examples

Let me map out the behaviour before you start coding. There are two distinct rule areas here — cancellation eligibility and return eligibility — which are probably worth separate tables.

## Table 1: Cancellation Rules

| Scenario                              | Current Status | Cancel Allowed? |
|---------------------------------------|----------------|-----------------|
| Order not yet confirmed               | PENDING        | Yes             |
| Order confirmed, not yet shipped      | CONFIRMED      | Yes             |
| Order has been shipped                | SHIPPED        | No              |
| Order has been delivered              | DELIVERED      | No              |
| Order already cancelled               | CANCELLED      | No              |

## Table 2: Return Eligibility (Delivered Orders)

| Scenario                                      | Status    | Days Since Delivery | Return Allowed? |
|-----------------------------------------------|-----------|---------------------|-----------------|
| Delivered, within return window               | DELIVERED | 15                  | Yes             |
| Delivered, on last day of return window       | DELIVERED | 30                  | Yes             |
| Delivered, one day past return window         | DELIVERED | 31                  | No              |
| Delivered, well outside return window         | DELIVERED | 60                  | No              |
| Not delivered (shipped but not yet delivered) | SHIPPED   | —                   | No              |

## Table 3: Invalid / No-Op Transitions

| Scenario                        | From      | To        | Allowed? |
|---------------------------------|-----------|-----------|----------|
| Confirm a pending order         | PENDING   | CONFIRMED | Yes      |
| Ship a confirmed order          | CONFIRMED | SHIPPED   | Yes      |
| Deliver a shipped order         | SHIPPED   | DELIVERED | Yes      |
| Skip confirmation — ship direct | PENDING   | SHIPPED   | No       |
| Reopen a delivered order        | DELIVERED | PENDING   | No       |
| Unship an order                 | SHIPPED   | CONFIRMED | No       |

## Open Questions

1. **Return window boundary** — Is day 30 included (≤ 30 days) or excluded (< 30 days)? Table 2 assumes inclusive (day 30 allowed).

2. **Cancellation after shipping** — Is there any exception? (e.g. carrier intercept before physical handoff?) The table says No for SHIPPED; confirm this is absolute.

3. **Return from CANCELLED** — Can a cancelled order be flagged for return if it was somehow delivered before cancellation was processed? Probably not, but worth confirming.

4. **Who measures "days since delivery"** — Is it calendar days from the delivered-at timestamp, or business days?
