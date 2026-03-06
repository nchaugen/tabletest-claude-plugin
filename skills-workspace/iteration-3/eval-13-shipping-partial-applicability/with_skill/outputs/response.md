# Shipping Cost Specification

Express shipping has a mixed destination dependency that requires careful representation. For orders under £50, Express costs £9.99 regardless of destination — so destination is irrelevant and should be blank. For orders £50 and over, Express is free *only in the UK*; for non-UK destinations the £9.99 charge still applies. This asymmetry means Express must appear as more than one case: destination is irrelevant for the paid tier, but determines the outcome for the high-value tier.

Standard shipping destination is genuinely irrelevant — blank. Overnight availability depends entirely on destination (UK and Ireland only) — so destination must be specified and cannot be blank.

## Shipping Cost Calculator

| Scenario | Shipping Type | Order Value | Destination | Cost? |
|---|---|---|---|---|
| Standard shipping, any order | Standard | any | | £3.99 |
| Express, order under £50 | Express | under £50 | | £9.99 |
| Express, order £50+, UK destination | Express | £50 or over | UK | £0 |
| Express, order £50+, non-UK destination | Express | £50 or over | non-UK | £9.99 |
| Overnight shipping, UK | Overnight | any | UK | £14.99 |
| Overnight shipping, Ireland | Overnight | any | Ireland | £14.99 |
| Overnight, unavailable outside UK/Ireland | Overnight | any | Other | not available |
