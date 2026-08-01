Here's a set of example tables covering the shopping cart's decision points. Since this is non-interactive, I've made some assumptions where your spec left things open — flagged below and in an "Open Questions" section so you can correct anything before building from these.

## Assumptions made

- **Catalogue price is snapshotted** onto the cart line when an item is added; it doesn't retroactively change if the catalogue price changes later. *(Flagged as open question — matters for correctness if untrue.)*
- **Removing an item removes the whole line**, not a partial quantity. If you need "reduce quantity by N," that's a second action worth its own rows.
- **A rejected coupon code (expired/not found) leaves the previously active coupon untouched** rather than clearing it.
- **The product-specific coupon is a fixed amount off each unit of the named product** (not a percentage, not a flat amount regardless of quantity). This is the biggest guess — your spec just says "a discount on a specific product," so confirm the mechanics.
- Catalogue: `Widget` = $5.00, `Gadget` = $12.00 (used consistently across tables for traceability).

---

## Table 1: Cart Item Management

| Scenario | Cart Before | Action | Message? | Cart After? |
|---|---|---|---|---|
| Add new item to empty cart | (empty) | Add 2x Widget | Added 2x Widget to cart | Widget x2 @ $5.00 |
| Add more of an item already in cart | Widget x2 @ $5.00 | Add 3x Widget | Added 3x Widget to cart | Widget x5 @ $5.00 |
| Add a different item | Widget x2 @ $5.00 | Add 1x Gadget | Added 1x Gadget to cart | Widget x2 @ $5.00, Gadget x1 @ $12.00 |
| Add item not in catalogue | (empty) | Add 1x "Doohickey" | Product not found | (empty) |
| Add with zero quantity | Widget x2 @ $5.00 | Add 0x Widget | Quantity must be at least 1 | Widget x2 @ $5.00 |
| Remove item present in cart | Widget x2, Gadget x1 | Remove Widget | Removed Widget from cart | Gadget x1 |
| Remove item not in cart | Gadget x1 | Remove Widget | Widget is not in your cart | Gadget x1 |
| Remove last item empties the cart | Widget x2 | Remove Widget | Removed Widget from cart | (empty) |

---

## Table 2: Coupon Code Entry

| Scenario | Active Coupon Before | Code Entered | Message? | Active Coupon After? |
|---|---|---|---|---|
| Apply first coupon, cart-wide percentage | none | SAVE10 (10% off cart) | Coupon SAVE10 applied: 10% off cart | SAVE10 |
| New valid code replaces an active one | SAVE10 | FLAT5 ($5.00 off cart) | Coupon FLAT5 applied: $5.00 off cart | FLAT5 |
| Apply a product-specific coupon | none | WIDGET2OFF ($2.00 off Widget) | Coupon WIDGET2OFF applied: $2.00 off Widget | WIDGET2OFF |
| Expired code leaves existing coupon in place, regardless of its type | {SAVE10, FLAT5, WIDGET2OFF} | EXPIRED20 | Coupon has expired | (unchanged) |
| Unknown code leaves existing coupon in place | FLAT5 | BOGUS123 | Coupon code not found | FLAT5 |

---

## Table 3: Cart Total Calculation

| Scenario | Cart Subtotal | Coupon | Discount Applied? | Total? |
|---|---|---|---|---|
| No coupon active | Widget x2 @ $5.00 → 10.00 | none | 0.00 | 10.00 |
| Percentage-off-cart coupon | Widget x2 @ $5.00, Gadget x1 @ $12.00 → 22.00 | 10% off cart | 2.20 | 19.80 |
| Fixed-amount-off-cart coupon | 22.00 | $5.00 off cart | 5.00 | 17.00 |
| Fixed discount exceeds subtotal — clamped at zero | Widget x1 @ $5.00 → 5.00 | $20.00 off cart | 5.00 | 0.00 |
| Product-specific coupon, product present twice | Widget x2 @ $5.00, Gadget x1 @ $12.00 → 22.00 | $2.00 off Widget | 4.00 | 18.00 |
| Product-specific coupon, product not in cart | Gadget x1 @ $12.00 → 12.00 | $2.00 off Widget | 0.00 | 12.00 |
| Empty cart with a coupon active | (empty) → 0.00 | 10% off cart | 0.00 | 0.00 |

---

## Table 4: Checkout

| Scenario | Cart (Requested) | Stock Available | Result? | Message? |
|---|---|---|---|---|
| Sufficient stock for everything | Widget x2, Gadget x1 | Widget: 5, Gadget: 3 | Success | Order placed |
| Requested quantity exactly matches stock | Widget x3 | Widget: 3 | Success | Order placed |
| Insufficient stock for one item | Widget x5 | Widget: 3 | Failure | Only 3 Widget available |
| Item completely out of stock | Widget x1 | Widget: 0 | Failure | Widget is out of stock |
| Insufficient stock for multiple items | Widget x5, Gadget x2 | Widget: 3, Gadget: 0 | Failure | Only 3 Widget available. Gadget is out of stock. |
| Empty cart | (empty) | — | Failure | Your cart is empty |

---

## Open questions to resolve before implementation

1. **Price snapshotting** — if the catalogue price changes after an item is added, does the cart line update, or stay at the price when it was added?
2. **Partial removal** — does "remove" always drop the whole line, or can a customer reduce quantity by less than the full amount?
3. **Product-specific coupon mechanics** — fixed amount per unit (assumed above), fixed amount off the line regardless of quantity, or a percentage off that product?
4. **Rejected coupon behavior** — confirmed assumption is that an expired/unknown code leaves the current coupon alone. Worth confirming that's actually the desired UX (vs. clearing it).
5. **Removing a coupon** — is there an explicit "remove coupon" action, or is the only way to change it entering a new code?

Once these are confirmed, this maps directly onto a table-driven test in whatever framework you're using — happy to convert any of these into executable tests next.