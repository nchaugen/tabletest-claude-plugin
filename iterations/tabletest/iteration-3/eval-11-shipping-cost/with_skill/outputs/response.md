# Shipping Cost Calculator — Spec by Example

The shipping rules vary by shipping type. Standard and express shipping costs do not depend on destination — so that column is left blank for those rows (genuinely inapplicable, not "any"). Overnight shipping is only available for UK destinations, so the destination is a meaningful input for those rows.

| Scenario                               | Shipping Type | Order Value | Destination | Cost?  |
|----------------------------------------|---------------|-------------|-------------|--------|
| Standard shipping, low order           | Standard      | £10.00      |             | £3.99  |
| Standard shipping, high order          | Standard      | £60.00      |             | £3.99  |
| Express shipping, under threshold      | Express       | £49.99      |             | £9.99  |
| Express shipping, at threshold         | Express       | £50.00      |             | £0.00  |
| Express shipping, above threshold      | Express       | £75.00      |             | £0.00  |
| Overnight shipping, UK destination     | Overnight     | £30.00      | UK          | £14.99 |
| Overnight shipping, UK, high order     | Overnight     | £60.00      | UK          | £14.99 |

---

## Notes on open questions

- The destination column is blank for Standard and Express rows because it genuinely does not affect the outcome — these shipping types are available regardless of destination. It is not filled with "ANY" or "N/A".
- For Overnight, "UK" is the only valid destination. Whether a non-UK destination for Overnight returns an error, is unavailable, or is silently ignored is unspecified — worth clarifying (e.g., should an `Available?` column be added?).
- The Express threshold is stated as "£50 and over = free". The row at exactly £50 confirms the boundary is inclusive.
- Are there other destinations (e.g., international) that affect Express or Standard costs? The current spec suggests not — but worth confirming.
