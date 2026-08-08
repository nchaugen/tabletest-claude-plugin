Since this is non-interactive, I've worked through the rules myself and made the assumptions explicit rather than asking. Here's the spec as three example tables — one per shipping method, since each has a genuinely distinct eligibility/cost rule.

## Assumptions (stated because I can't confirm them)

1. **Express outside the UK at £50+**: the rule only says the free tier "applies in the UK" — I've assumed that means orders ≥£50 outside the UK still pay the normal £9.99, not some other price.
2. **Overnight outside UK/Ireland**: assumed *not offered* (no price returned) rather than falling back to a price — the rule says "only available for UK and Ireland."
3. **Threshold is inclusive at £50.00**: "£50 and over" → `>= 50.00`; "under £50" → `< 50.00`.
4. **Standard and Express have no destination restriction** — they're offered everywhere; only their *price* (Express) or *availability* (Overnight) varies by destination.
5. Currency values are exact pence, GBP. `France` / `USA` are used as representative "rest of world" destinations throughout.

---

### 1. Sets the standard shipping cost regardless of order value or destination

| Scenario                                | Order Value (£)            | Destination                   | Standard Cost (£)? |
|------------------------------------------|-----------------------------|--------------------------------|---------------------|
| Order value varies, destination fixed    | {0.01, 49.99, 50.00, 500.00}| UK                              | 3.99                |
| Destination varies, order value fixed    | 50.00                        | {UK, Ireland, France, USA}     | 3.99                |

*Standard is a flat rate — the two rows each hold one dimension constant to show independently that neither order value nor destination affects it.*

---

### 2. Calculates the express shipping cost from order value and destination

| Scenario                                          | Order Value (£)      | Destination                  | Express Cost (£)? |
|----------------------------------------------------|------------------------|--------------------------------|---------------------|
| Below the free-shipping threshold                  | 49.99                  | {UK, Ireland, France, USA}    | 9.99                |
| At or above the threshold, UK destination          | {50.00, 500.00}        | UK                              | 0.00                |
| At or above the threshold, outside the UK          | {50.00, 500.00}        | {Ireland, France, USA}        | 9.99                |

*Below £50, destination is irrelevant. At/above £50, only the UK gets free shipping — Ireland does **not** get the free tier even though it's eligible for Overnight. The value set `{50.00, 500.00}` shows the free/charged outcome holds regardless of how far above the threshold the order is.*

---

### 3. Determines overnight shipping availability and cost by destination

| Scenario                            | Destination     | Order Value (£)              | Available? | Overnight Cost (£)? |
|---------------------------------------|------------------|--------------------------------|------------|------------------------|
| UK destination                        | UK               | {0.01, 49.99, 50.00, 500.00}  | yes        | 14.99                  |
| Ireland destination                   | Ireland          | {0.01, 49.99, 50.00, 500.00}  | yes        | 14.99                  |
| Destination outside UK and Ireland    | {France, USA}    | {0.01, 49.99, 50.00, 500.00}  | no         |                         |

*Order value never affects Overnight, so it's a value set on every row. `Overnight Cost?` is blank (not £0.00) when unavailable — absent, not free.*

---

## Open questions

- Is there any minimum order value at all, or a maximum shippable value, for any method? Not stated — assumed no floor/ceiling beyond the £50 Express threshold.
- Should an unrecognized/unsupported destination (not UK, Ireland, or "rest of world") behave like "outside UK and Ireland," or be rejected outright? Assumed the former.

If you want these turned into an executable test (JUnit/TableTest, pytest, etc.), I can hand this off via `/tabletest` or the table-driven-testing skill — let me know the language/framework.