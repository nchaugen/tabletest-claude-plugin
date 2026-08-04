Here's a full set of example tables covering the cart behaviour. I split checkout into two separate tables (empty-cart vs. stock) since they're independent conditions — each is testable on its own and combining them would hide which one actually failed. Same for coupon discount calculation (classification by coupon type) vs. cart total (pure arithmetic with flooring).

Assumptions are called out at the end since I can't check them with you interactively — flag any that are wrong and I'll adjust the tables.

## 1. Adds a Product to the Cart

| Scenario | Cart Before | Product | Catalogue Price | Quantity | Cart After? | Added? | Message? |
|---|---|---|---|---|---|---|---|
| Add a new product to an empty cart | [:] | Widget | 10 | 2 | [Widget: 2] | yes | Added 2x Widget to cart |
| Add more of a product already in the cart | [Widget: 2] | Widget | 10 | 3 | [Widget: 5] | yes | Added 3x Widget to cart |
| Add a second, different product | [Widget: 2] | Gadget | 25 | 1 | [Widget: 2, Gadget: 1] | yes | Added 1x Gadget to cart |
| Quantity is zero | [:] | Widget | 10 | 0 | [:] | no | Quantity must be greater than zero |
| Quantity is negative | [:] | Widget | 10 | -1 | [:] | no | Quantity must be greater than zero |
| Product does not exist in the catalogue | [:] | Unknown Item | | 1 | [:] | no | Product not found |

## 2. Removes a Product from the Cart

| Scenario | Cart Before | Product | Cart After? | Removed? | Message? |
|---|---|---|---|---|---|
| Remove one of several products | [Widget: 2, Gadget: 1] | Widget | [Gadget: 1] | yes | Removed Widget from cart |
| Remove the only product, leaving the cart empty | [Widget: 2] | Widget | [:] | yes | Removed Widget from cart |
| Remove a product that isn't in the cart | [Gadget: 1] | Widget | [Gadget: 1] | no | Widget is not in your cart |
| Remove from an empty cart | [:] | Widget | [:] | no | Widget is not in your cart |

## 3. Applies a Coupon Code to the Cart

*Coupon lookup (valid / expired / unknown) is treated as an external given, not derived in the table.*

| Scenario | Active Coupon Before | Code Entered | Code Status | Coupon Details | Active Coupon After? | Message? |
|---|---|---|---|---|---|---|
| Apply a valid code to a cart with no coupon | [:] | SAVE10 | Valid | 10% off cart | 10% off cart (SAVE10) | Coupon SAVE10 applied |
| Apply a valid code that replaces an existing coupon | 10% off cart (SAVE10) | FLAT5 | Valid | $5 off cart | $5 off cart (FLAT5) | Coupon FLAT5 applied |
| Expired code leaves the existing coupon in place | 10% off cart (SAVE10) | OLDCODE | Expired | | 10% off cart (SAVE10) | Coupon has expired |
| Unknown code leaves the cart without a coupon | [:] | FAKE99 | Unknown | | [:] | Coupon code not found |

## 4. Computes the Discount for the Active Coupon

*Coupon type is a family — one rule, one column, computed differently per member.*

| Scenario | Cart Items | Subtotal | Active Coupon | Discount Amount? |
|---|---|---|---|---|
| No coupon active | [Widget: 2 @ 10] | 20 | [:] | 0 |
| Percentage off the whole cart | [Widget: 2 @ 10] | 20 | 10% off cart | 2 |
| Fixed amount off the whole cart | [Widget: 2 @ 10] | 20 | $5 off cart | 5 |
| Product discount, product is in the cart | [Widget: 2 @ 10, Gadget: 1 @ 25] | 45 | $3 off Widget | 3 |
| Product discount, product is not in the cart | [Gadget: 1 @ 25] | 25 | $3 off Widget | 0 |

## 5. Computes the Cart Total from Subtotal and Discount

*Kept separate from Table 4 — this is pure arithmetic, not a classification, and every cell should be predictable in one step.*

| Scenario | Subtotal | Discount Amount | Total? |
|---|---|---|---|
| No discount | 20 | 0 | 20 |
| Discount less than subtotal | 20 | 5 | 15 |
| Discount exactly equal to subtotal | 20 | 20 | 0 |
| Discount exceeds subtotal (floored at zero) | 10 | 50 | 0 |

## 6. Rejects Checkout of an Empty Cart

| Scenario | Cart Items | Checkout Result? | Message? |
|---|---|---|---|
| Empty cart | [:] | Rejected | Your cart is empty |
| Non-empty cart passes this check | [Widget: 2] | Accepted | Order placed |

## 7. Validates Stock Availability at Checkout

*Cart is non-empty throughout — that condition belongs to Table 6.*

| Scenario | Cart Items (Qty Requested) | Stock Available | Checkout Result? | Message? |
|---|---|---|---|---|
| All items have sufficient stock | [Widget: 2] | {Widget: 5} | Accepted | Order placed |
| Requested quantity exactly matches stock (boundary) | [Widget: 3] | {Widget: 3} | Accepted | Order placed |
| Requested quantity one over stock (boundary) | [Widget: 4] | {Widget: 3} | Rejected | Only 3 available for Widget |
| Multiple items short at once | [Widget: 5, Gadget: 2] | {Widget: 3, Gadget: 1} | Rejected | Only 3 available for Widget, only 1 available for Gadget |
| Product is completely out of stock | [Widget: 1] | {Widget: 0} | Rejected | Widget is out of stock |

---

## Assumptions made (flag any that are wrong)

- **Re-adding a product increases its quantity** rather than being rejected or creating a duplicate line.
- **Remove takes only a product**, not a quantity — it removes the whole line item. (If you actually want "remove N units," that's a different table with its own boundary: removing the last unit vs. removing partway.)
- **An invalid coupon code (expired or unknown) never clears an existing valid coupon** — it's rejected and the cart keeps whatever it had before.
- **A product-specific coupon that no longer matches anything in the cart just contributes zero discount** rather than being cleared automatically — worth confirming, since the alternative (auto-removing it, or erroring) is a real product decision.
- **Zero available stock gets its own wording** ("out of stock") rather than "Only 0 available for X" — worth a product-copy sanity check.
- **Stock shortfall messages list every short item in one message**, not just the first one hit.
- Quantity is only validated at add-time (`> 0`); stock is only checked at checkout, never at add-time, per your description.

Once these are confirmed, these tables map directly to `@TableTest` methods (one per table) if you want to hand this to `/tabletest` for implementation.