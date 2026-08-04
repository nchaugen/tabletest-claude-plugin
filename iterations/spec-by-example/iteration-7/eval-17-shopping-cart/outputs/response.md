Since this is a non-interactive session, I worked through this myself rather than asking you round-by-round — I've flagged every place I had to assume a business rule so you can correct me. Six tables below, each covering one behavior (I split coupon *type* into a single family table rather than three, and split coupon *acceptance* from *total calculation* since those are a classification and an arithmetic step, not one rule).

## 1. Adds an Item to the Cart

| Scenario | Cart Before | Product | Quantity | Catalogue Price | Cart After? | Message? |
|---|---|---|---|---|---|---|
| Empty cart, first item | [:] | Widget | 2 | $5.00 | [Widget: 2] | Added 2x Widget to cart |
| Product already in cart | [Widget: 1] | Widget | 2 | $5.00 | [Widget: 3] | Added 2x Widget to cart |
| Second distinct product | [Widget: 2] | Gadget | 1 | $3.00 | [Widget: 2, Gadget: 1] | Added 1x Gadget to cart |
| Non-positive quantity | [Widget: 2] | Widget | {0, -1} | $5.00 | [Widget: 2] | Quantity must be at least 1 |
| Product not in catalogue | [Widget: 2] | Sprocket | 1 |  | [Widget: 2] | Sprocket is not available |

**Assumption:** adding a product already in the cart accumulates quantity rather than replacing it.

## 2. Removes an Item from the Cart

| Scenario | Cart Before | Product | Quantity To Remove | Cart After? | Message? |
|---|---|---|---|---|---|
| Partial removal | [Widget: 3] | Widget | 1 | [Widget: 2] | Removed 1x Widget from cart |
| Removes exactly what's present | [Widget: 2] | Widget | 2 | [:] | Removed 2x Widget from cart |
| Removes more than present | [Widget: 2] | Widget | 5 | [Widget: 2] | Only 2x Widget in cart |
| Product not in cart | [Widget: 2] | Gadget | 1 | [Widget: 2] | Gadget is not in your cart |

**Assumption:** removing more than the held quantity is rejected outright (cart unchanged) rather than clamped to "remove all." If you'd rather it clamp and remove everything, that's a one-row change.

## 3. Applies a Coupon Code to the Cart

One table for all three coupon kinds — the rule ("a new valid code replaces whatever was active") doesn't change by kind, so kind is data, not a separate table per type.

| Scenario | Active Coupon Before | Code Entered | Coupon Record | Active Coupon After? | Message? |
|---|---|---|---|---|---|
| No coupon, percentage code | (none) | SAVE10 | 10% off cart | 10% off cart | Coupon SAVE10 applied |
| Replaces with a fixed-amount code | 10% off cart | FLAT5 | $5 off cart | $5 off cart | Coupon FLAT5 applied |
| Replaces with a product-specific code | $5 off cart | WIDGET20 | 20% off Widget | 20% off Widget | Coupon WIDGET20 applied |
| Expired code, existing coupon kept | 10% off cart | OLD2024 | Expired | 10% off cart | Coupon has expired |
| Unknown code, none active | (none) | FAKE99 | Does not exist | (none) | Coupon code not found |

## 4. Calculates the Cart Total

Takes the *resolved* coupon as input (output of Table 3) — this is the arithmetic step, kept separate from the acceptance decision above.

| Scenario | Cart Items | Active Coupon | Total? |
|---|---|---|---|
| No coupon | [Widget: 2 @ $5.00] | (none) | $10.00 |
| Percentage off cart | [Widget: 2 @ $5.00] | 10% off cart | $9.00 |
| Fixed amount under subtotal | [Widget: 2 @ $5.00] | $3 off cart | $7.00 |
| Fixed amount exactly equals subtotal | [Widget: 1 @ $5.00] | $5 off cart | $0.00 |
| Fixed amount exceeds subtotal (floored) | [Widget: 1 @ $5.00] | $6 off cart | $0.00 |
| Product discount, product in cart | [Widget: 2 @ $5.00, Gadget: 1 @ $3.00] | 20% off Widget | $11.00 |
| Product discount, product not in cart | [Gadget: 1 @ $3.00] | 20% off Widget | $3.00 |

**Open question:** last row assumes a product-specific coupon simply has no effect if that product isn't in the cart (not an error). Worth confirming — some carts instead show a hint like "Add a Widget to use this coupon."

## 5. Blocks Checkout When the Cart Is Empty

Kept separate from stock verification — this precondition doesn't reference stock at all.

| Scenario | Cart Items | Blocked? | Message? |
|---|---|---|---|
| Empty cart | [:] | yes | Your cart is empty |
| Cart has items | [Widget: 1] | no |  |

## 6. Verifies Stock Levels at Checkout

| Scenario | Cart Items | Stock Levels | Checkout Result? | Message? |
|---|---|---|---|---|
| Ample stock | [Widget: 2] | {Widget: 5} | Success |  |
| Stock exactly matches demand | [Widget: 2] | {Widget: 2} | Success |  |
| One item short | [Widget: 3] | {Widget: 2} | Failed | Only 2x Widget available |
| One sufficient, one short | [Widget: 2, Gadget: 3] | {Widget: 2, Gadget: 1} | Failed | Only 1x Gadget available |
| Multiple items short | [Widget: 3, Gadget: 3] | {Widget: 1, Gadget: 1} | Failed | Only 1x Widget available, Only 1x Gadget available |

**Assumption:** shortfall messages name the product ("Only 2x Widget available") since checkout can fail on several items at once and the frontend needs to know which.

---

Not tabled: browsing the catalogue is a plain read with no branching logic, so it doesn't need a decision table.

Once you've confirmed or corrected the three flagged assumptions/open questions, these six tables translate directly into `@TableTest` methods — happy to run `/tabletest` against them if this is a JVM project, or point me at the test framework if it's something else.