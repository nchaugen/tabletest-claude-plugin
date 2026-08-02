Here's the test scenario breakdown, organized as one table per rule (each table's axis is a single concern — this avoids the trap of one giant "cart behavior" table that mixes unrelated rules).

## 1. Adds an Item to the Cart

| Scenario | Cart Before | Product | Catalogue Price | Quantity Requested | Cart After? | Message? |
|---|---|---|---|---|---|---|
| Adds a new product to an empty cart | [] | Widget | $10.00 | 1 | [Widget x1 @ $10.00] | "Added 1x Widget to cart" |
| Adds more of a product already in the cart | [Widget x2 @ $10.00] | Widget | $10.00 | 3 | [Widget x5 @ $10.00] | "Added 3x Widget to cart" |
| Rejects a non-positive quantity | [] | Widget | $10.00 | {0, -1} | [] unchanged | "Quantity must be at least 1" |
| Rejects a product not in the catalogue | [] | Unicorn Statue | (not in catalogue) | 1 | [] unchanged | "Product not found" |

*Assumption: adding an already-present product increases its quantity rather than erroring or overwriting.*

## 2. Removes an Item from the Cart

| Scenario | Cart Before | Product | Cart After? | Message? |
|---|---|---|---|---|
| Removes a product present in the cart | [Widget x2, Gadget x1] | Widget | [Gadget x1] | "Removed Widget from cart" |
| Rejects removing a product not in the cart | [Gadget x1] | Widget | [Gadget x1] unchanged | "Widget is not in your cart" |

*Open question: does remove take a quantity (decrement) or always remove the whole line item? Table above assumes whole-line removal — flag if partial removal is needed, that'd be a third row/rule.*

## 3. Applies a Coupon Code to the Cart

State-transition table — coupon before, code entered, coupon after.

| Scenario | Active Coupon Before | Code Entered | Code Validity | Active Coupon After? | Message? |
|---|---|---|---|---|---|
| Applies the first coupon when none is active | (none) | SAVE10 (10% off cart) | valid | 10% off cart (SAVE10) | "Coupon applied: SAVE10 (10% off)" |
| Replaces an active coupon with a different type | $10 off cart (FIXED10) | SAVE10 (10% off cart) | valid | 10% off cart (SAVE10) | "Coupon applied: SAVE10 (10% off)" |
| Rejects an expired code, keeping the previous coupon | 10% off cart (SAVE10) | EXPIRED5 | expired | 10% off cart (SAVE10) unchanged | "Coupon has expired" |
| Rejects an unknown code, keeping the previous coupon | 10% off cart (SAVE10) | BOGUS | unknown | 10% off cart (SAVE10) unchanged | "Coupon code not found" |
| Rejects an expired code when none was active | (none) | EXPIRED5 | expired | (none) unchanged | "Coupon has expired" |

The second row exists specifically to prove replacement doesn't care about coupon *type* — a fixed coupon replaced by a percentage coupon.

## 4. Calculates the Discount from an Active Coupon

One table, "Coupon Type" as the family column — percentage/fixed/product-specific are members of one rule ("computes a discount"), not three separate tables.

| Scenario | Cart Contents (Subtotal) | Coupon Type | Coupon Detail | Discount? |
|---|---|---|---|---|
| Percentage off the whole cart | Widget x2 @ $10.00 ($20.00) | Percentage | 10% | $2.00 |
| Fixed amount off the whole cart | Widget x2 @ $10.00 ($20.00) | Fixed | $5.00 off | $5.00 |
| Fixed amount exceeding the subtotal | Widget x2 @ $10.00 ($20.00) | Fixed | $50.00 off | $50.00 |
| Discount on a specific product in the cart | Widget x2 @ $10.00 + Gadget x1 @ $15.00 ($35.00) | Product | Widget: $3.00 off/unit | $6.00 |
| Discount on a specific product not in the cart | Gadget x1 @ $15.00 ($15.00) | Product | Widget: $3.00 off/unit | $0.00 |

*Note: row 3's raw $50.00 discount on a $20.00 subtotal is intentional — clamping happens downstream (table 5), so this table just proves the discount calculation itself is correct before clamping.*
*Open question: should a product coupon that matches nothing in the cart also emit its own message (e.g. "Your coupon doesn't apply to any items in your cart"), or silently produce $0 discount?*

## 5. Calculates the Cart Total from Subtotal and Discount

Pure arithmetic, separated from the discount *decision* above — this table just proves the clamp.

| Scenario | Subtotal | Discount | Total? |
|---|---|---|---|
| No discount applied | $20.00 | $0.00 | $20.00 |
| Discount less than the subtotal | $20.00 | $5.00 | $15.00 |
| Discount equal to the subtotal | $20.00 | $20.00 | $0.00 |
| Discount exceeding the subtotal is clamped at zero | $20.00 | $50.00 | $0.00 |

Rows 3 and 4 are the boundary pair for the "never below zero" rule.

## 6. Rejects Checkout on an Empty Cart

| Scenario | Cart Contents | Checkout Result? | Message? |
|---|---|---|---|
| Empty cart is rejected | [] | rejected | "Your cart is empty" |
| Non-empty cart proceeds past this check | [Widget x2] | proceeds to stock check | (none) |

## 7. Rejects Checkout When Stock Is Insufficient

Kept separate from table 6 — "cart not empty" and "stock sufficient" are independent conditions (an "and"), so each gets its own table, holding the other satisfied.

| Scenario | Cart Contents (Qty Requested) | Available Stock | Checkout Result? | Shortages? |
|---|---|---|---|---|
| All items sufficiently stocked | Widget x2 | Widget: 5 | accepted | [] |
| Requested quantity exactly matches stock | Widget x3 | Widget: 3 | accepted | [] |
| Requested quantity exceeds stock by one | Widget x4 | Widget: 3 | rejected | [Widget: only 3 available] |
| Multiple items simultaneously short | Widget x4, Gadget x2 | Widget: 3, Gadget: 1 | rejected | [Widget: only 3 available, Gadget: only 1 available] |

`Shortages?` is a real list, not a flattened string — that's what lets the frontend render "Only 3 available" per line item rather than parsing a sentence.

---

### Assumptions made (flag any that are wrong)
- Re-adding a product already in the cart increments its quantity rather than replacing it or erroring.
- Remove takes no quantity — it removes the whole line item. If partial decrement is a real requirement, that needs its own table.
- Product-specific coupons discount a fixed amount *per unit* of the matching product (not a flat one-time amount) — worth confirming, since the spec just says "a discount on a specific product."
- A coupon replacement is silent (no "previous coupon removed" message) — only the new coupon's applied/error message is shown.
- Checkout success has its own message too (not specified in your examples) — worth deciding wording now so it's covered in tests.

Want me to turn these into executable tests? Let me know the language/framework (JVM → `@TableTest`, otherwise pytest/Jest/Go table tests etc.) and I'll convert directly.