# Shipping Cost Calculator — Specification

| Scenario                             | Shipping type | Order value | Destination | Shipping cost? |
|--------------------------------------|---------------|-------------|-------------|----------------|
| Standard shipping                    | Standard      | £10.00      |             | £3.99          |
| Standard shipping (high order value) | Standard      | £100.00     |             | £3.99          |
| Express shipping (under £50)         | Express       | £30.00      |             | £9.99          |
| Express shipping (£50 and over)      | Express       | £50.00      |             | £0.00          |
| Express shipping (high value)        | Express       | £99.99      |             | £0.00          |
| Overnight shipping (UK)              | Overnight     | £20.00      | UK          | £14.99         |

### Notes

- Standard shipping costs £3.99 regardless of order value or destination, so the Destination column is left blank for Standard rows.
- Express shipping does not depend on destination, so Destination is also left blank for Express rows.
- Overnight shipping is only available for UK destinations; the UK destination is explicitly shown.
- The `Shipping cost?` column ends with `?` to indicate it is the result/output.
