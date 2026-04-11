# Subscription Billing — Specification by Example

## Plan Pricing

| Plan    | Billing cycle | Price?   |
|---------|---------------|----------|
| Monthly | Monthly       | £9.99    |
| Annual  | Annual        | £95.88   |

## Free Trial

| Scenario                        | New subscriber? | Trial duration? | Auto-converts to? |
|---------------------------------|-----------------|-----------------|-------------------|
| New subscriber gets free trial  | Yes             | 30 days         | Monthly           |
| Existing subscriber             | No              | —               | —                 |

## Cancellation and Refunds

| Scenario                                    | Time since renewal charge | Refund given?        |
|---------------------------------------------|--------------------------|----------------------|
| Cancel mid-cycle (more than 24h after renewal) | > 24 hours            | Prorated for unused days |
| Cancel within 24h of renewal charge         | ≤ 24 hours              | No refund            |
| Cancel during free trial                    | N/A (trial)              | No charge to refund  |

### Notes

- **Prorated refund**: calculated as `(unused days / total days in cycle) × amount charged`.
- The 24-hour no-refund window applies specifically after a **renewal charge** (not the initial subscription).
- Annual plan subscribers are subject to the same cancellation rules as monthly plan subscribers.
