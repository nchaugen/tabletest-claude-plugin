Here's the spec:

---

## Shipping Cost Calculator Spec

### Inputs
- `order_value: float` — total order value in GBP
- `destination: string` — delivery country (e.g. `"UK"`, `"IE"`, `"US"`)
- `shipping_method: "standard" | "express" | "overnight"`

### Rules

| Method | Cost | Conditions |
|---|---|---|
| Standard | £3.99 | Always |
| Express | £9.99 | Orders under £50, any destination |
| Express | £0.00 | Orders £50+, UK only |
| Express | £9.99 | Orders £50+, non-UK |
| Overnight | £14.99 | UK and Ireland only |
| Overnight | unavailable | All other destinations |

### Outputs
- On success: cost in GBP (float), e.g. `3.99`
- On unavailable method: error/exception — `"Overnight shipping is not available for destination: {destination}"`

### Edge Cases & Clarifications Needed

1. **Express £50 threshold** — is £50.00 exactly "under £50" or "£50 and over"? The spec says "£50 and over" is free, so £50.00 → free. Confirm.
2. **Ireland + Express** — Ireland is not UK, so Express at £50+ would cost £9.99. Is that intended?
3. **What counts as "UK"?** — Does this include Northern Ireland, Channel Islands, Isle of Man? Relevant for overnight and free express eligibility.
4. **Invalid method** — Should requesting a valid method with an invalid destination (e.g. overnight to `"US"`) throw an error, or return a sentinel value like `null`?
5. **Currency/rounding** — Are all inputs and outputs assumed GBP with 2 decimal places?

### Example Outputs

| Method | Order Value | Destination | Cost |
|---|---|---|---|
| Standard | £10 | UK | £3.99 |
| Standard | £100 | US | £3.99 |
| Express | £30 | UK | £9.99 |
| Express | £60 | UK | £0.00 |
| Express | £60 | US | £9.99 |
| Overnight | £20 | UK | £14.99 |
| Overnight | £20 | IE | £14.99 |
| Overnight | £20 | US | error |

---

The main ambiguities are around the UK definition and whether Ireland gets any special treatment for express. Worth resolving those before implementation.