Here are six example tables covering the cart's distinct rules, each isolating one concern per the "one rule, one axis" principle. I skipped catalogue *browsing* itself — no branching logic was described for it, just retrieval.

## Assumptions (stated since this is non-interactive)

- **Remove** deletes a line item entirely; there's no separate "decrease quantity" operation (not mentioned in the brief).
- **Adding an already-present product** merges into the existing line (adds to quantity) rather than replacing it; the success message reports the quantity just added, not the new total.
- **Add-to-cart does not check stock** — per the brief, stock is only verified at checkout.
- A **product-specific coupon whose target isn't in the cart** silently contributes $0 discount rather than erroring (flagged as open below — could reasonably be a rejection instead).
- **Percentage discounts round to 2 decimal places**; rounding *mode* (half-up vs. banker's) is left open.
- Money in cells is `major.minor` (e.g. `5.00`); cart/stock contents use `[Product: qty]` or `[Product: qty @ price]`.

---

### Table 1 — Adds an Item to the Cart

| Scenario | Cart Before | Product | Catalogue Price | Requested Quantity | Cart After? | Message? |
|---|---|---|---|---|---|---|
| First item added to an empty cart | [:] | Widget | 5.00 | 2 | [Widget: 2 @ 5.00] | Added 2x Widget to cart |
| Adding more of an item already in the cart | [Widget: 2 @ 5.00] | Widget | 5.00 | 3 | [Widget: 5 @ 5.00] | Added 3x Widget to cart |
| Adding a second, different product | [Widget: 2 @ 5.00] | Gadget | 9.99 | 1 | [Widget: 2 @ 5.00, Gadget: 1 @ 9.99] | Added 1x Gadget to cart |
| Zero or negative quantity is rejected | [Widget: 2 @ 5.00] | Gadget | 9.99 | {0, -1} | [Widget: 2 @ 5.00] (unchanged) | Quantity must be at least 1 |
| Product not found in catalogue | [:] | Unicorn |  | 1 | [:] (unchanged) | Product not found |

*Catalogue Price is always resolved by the system from the catalogue — the operation takes no price input from the caller, so a spoofed price can't reach the cart at all.*

### Table 2 — Removes an Item from the Cart

| Scenario | Cart Before | Product | Cart After? | Message? |
|---|---|---|---|---|
| Removing one of several items | [Widget: 2 @ 5.00, Gadget: 1 @ 9.99] | Widget | [Gadget: 1 @ 9.99] | Removed Widget from cart |
| Removing the last item empties the cart | [Widget: 2 @ 5.00] | Widget | [:] | Removed Widget from cart |
| Removing a product that isn't in the cart | {[:], [Gadget: 1 @ 9.99]} | Widget | same as Before | Widget is not in your cart |

### Table 3 — Applies a Coupon Code to the Cart

| Scenario | Active Coupon Before | Entered Code | Code Validity | Coupon Type | Active Coupon After? | Message? |
|---|---|---|---|---|---|---|
| Applying a valid code when no coupon is active | (none) | SAVE10 | valid | {Percentage, Fixed Amount, Product Discount} | SAVE10 | Coupon SAVE10 applied |
| A new valid code replaces the active coupon | WELCOME5 | SAVE10 | valid | {Percentage, Fixed Amount, Product Discount} | SAVE10 | Coupon SAVE10 applied |
| An expired code is rejected, no coupon was active | (none) | OLDCODE | expired | {Percentage, Fixed Amount, Product Discount} | (none) | Coupon has expired |
| An expired code does not disturb the active coupon | WELCOME5 | OLDCODE | expired | {Percentage, Fixed Amount, Product Discount} | WELCOME5 | Coupon has expired |
| An unknown code is rejected, no coupon was active | (none) | BOGUS | not found | {Percentage, Fixed Amount, Product Discount} | (none) | Coupon code not found |
| An unknown code does not disturb the active coupon | WELCOME5 | BOGUS | not found | {Percentage, Fixed Amount, Product Discount} | WELCOME5 | Coupon code not found |

*Rows 3–6 use a value set for Coupon Type to state explicitly: rejection doesn't depend on what kind of coupon it would have been.*

### Table 4 — Calculates the Coupon Discount Amount

| Scenario | Coupon Type | Coupon Parameters | Cart Contents | Discount Amount? |
|---|---|---|---|---|
| No coupon applied | None |  | [Widget: 2 @ 5.00] | 0.00 |
| Percentage off the whole cart | Percentage | [percent: 10%] | [Widget: 2 @ 5.00] (subtotal 10.00) | 1.00 |
| Fixed amount off the whole cart | Fixed Amount | [amount: 5.00] | [Widget: 2 @ 5.00] (subtotal 10.00) | 5.00 |
| Product discount when the target is in the cart | Product Discount | [product: Widget, amount: 3.00] | [Widget: 2 @ 5.00, Gadget: 1 @ 9.99] | 3.00 |
| Product discount when the target is not in the cart | Product Discount | [product: Widget, amount: 3.00] | [Gadget: 1 @ 9.99] | 0.00 |

*Discount here is not yet capped against the subtotal — that's Table 5's job, kept separate so the classification and the arithmetic aren't fused.*

### Table 5 — Computes the Cart Total

| Scenario | Subtotal | Discount Amount | Total? |
|---|---|---|---|
| Discount smaller than the subtotal | 19.99 | 1.00 | 18.99 |
| No discount applies | 10.00 | 0.00 | 10.00 |
| Discount exactly equal to the subtotal | 10.00 | 10.00 | 0.00 |
| Discount one cent less than the subtotal | 10.00 | 9.99 | 0.01 |
| Discount larger than the subtotal is floored at zero | 10.00 | 15.00 | 0.00 |

### Table 6 — Determines Checkout Eligibility

| Scenario | Cart Contents | Stock Levels | Checkout Result? | Shortfalls? | Message? |
|---|---|---|---|---|---|
| An empty cart cannot check out | [:] | [:] | Rejected | [:] | Your cart is empty |
| Every item has enough stock | [Widget: 2, Gadget: 1] | [Widget: 5, Gadget: 10] | Accepted | [:] | Order placed |
| Requested quantity exactly matches stock | [Widget: 3] | [Widget: 3] | Accepted | [:] | Order placed |
| Requested quantity is one more than available | [Widget: 4] | [Widget: 3] | Rejected | [Widget: 3 available] | Only 3 available for Widget |
| One of several items is short | [Widget: 5, Gadget: 1] | [Widget: 3, Gadget: 10] | Rejected | [Widget: 3 available] | Only 3 available for Widget |
| Multiple items are short at once | [Widget: 5, Gadget: 5] | [Widget: 3, Gadget: 2] | Rejected | [Widget: 3 available, Gadget: 2 available] | Only 3 available for Widget, only 2 available for Gadget |

---

## Open questions (marked, not guessed away)

1. Should a product-discount coupon whose target isn't in the cart be a silent no-op ($0 discount, as assumed) or should applying it be rejected/warned up front?
2. Is catalogue price snapshotted onto the cart line at add-time, or re-looked-up live at checkout if the catalogue price changes in between? This affects whether a Table 1-style "price changed after add" scenario is even meaningful.
3. Rounding mode for percentage discounts (half-up, half-even, truncate)?
4. Is coupon expiry checked only at apply-time, or re-validated again at checkout if time has passed since it was applied?
5. Any upper bound on percentage value (e.g., can a coupon be >100% off), or is that validated elsewhere and out of the cart's concern?

These six tables are ready to hand to `/tabletest` once you're coding — each column set maps directly to `@TableTest` parameters.