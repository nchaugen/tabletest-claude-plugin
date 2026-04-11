Here is the example table for **Shipping Cost**:

| Scenario | Method | Destination | Order Value | Free Threshold | Cost? |
|---|---|---|---|---|---|
| Standard — always flat rate | Standard | {UK, Ireland, International} | {£10.00, £100.00} | | £3.99 |
| Express, UK, below threshold | Express | UK | £49.99 | £50.00 | £9.99 |
| Express, UK, at threshold — free tier applies | Express | UK | £50.00 | £50.00 | £0.00 |
| Express, UK, above threshold | Express | UK | £75.00 | £50.00 | £0.00 |
| Express, non-UK, below threshold | Express | {Ireland, International} | £49.99 | £50.00 | £9.99 |
| Express, non-UK, at or above threshold — free tier does not apply | Express | {Ireland, International} | {£50.00, £75.00} | £50.00 | £9.99 |
| Overnight, UK or Ireland — always £14.99 | Overnight | {UK, Ireland} | {£10.00, £100.00} | | £14.99 |
| Overnight, International — not available | Overnight | International | {£10.00, £100.00} | | Not available |

**A few notes on the design:**

- **Free Threshold** is an explicit column to make the £50.00 boundary legible and to clarify the `≥` direction (rows at £49.99 and £50.00 both appear). It is blank for Standard and Overnight, which have no threshold rule.
- The rows for Express non-UK at/above threshold are the most important to confirm: the free tier does not apply, so £50.00+ in Ireland still costs £9.99. The value set `{£50.00, £75.00}` says this holds regardless of being at or above the threshold.
- **Standard** uses value sets on both Destination and Order Value to state the rule clearly: nothing changes it.

**Open questions worth confirming before implementation:**

1. **"Not available" behaviour** — when Overnight is selected for an International destination, should the calculator return an error, return a sentinel value, or should the option be hidden from the UI entirely?
2. **"International" definition** — does this mean all non-UK, non-Ireland destinations, or is there a specific list of supported countries? If there is a list, a separate availability table may be needed.