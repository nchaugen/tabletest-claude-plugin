Here's a set of example tables covering the cart's distinct rules. I've split by concern (add, remove, coupon validation, discount math, totals, checkout guards) rather than one giant end-to-end table, since each has its own boundary conditions worth pinning down separately.

**Assumptions** (flagging since I can't ask follow-ups — flip any of these if wrong):
- Stock is checked only at checkout, not when adding to cart — you can add more than is in stock and only find out later.
- "Remove" takes an item and removes the entire line, not a partial quantity decrement.
- An invalid/expired coupon attempt leaves any existing active coupon untouched (it just fails, doesn't clear the cart's current coupon).
- Coupon expiry is inclusive — a coupon is still valid on its expiry date, not just before it.
- Product-specific coupons subtract a flat amount from that product's line subtotal (open question below — could instead be a % off that product).
- Quantities are positive integers; catalogue prices are the sole source of truth.

---

### 1. Adds an item to the cart, looking up its price from the catalogue

| Scenario | Cart Before | Item | Quantity Requested | Catalogue Price | Cart After? | Message? |
|---|---|---|---|---|---|---|
| Adding a new item to an empty cart | [empty] | Widget | 2 | $8.00 | Widget: 2 @ $8.00 | Added 2x Widget to cart |
| Adding more of an item already in the cart | Widget: 2 @ $8.00 | Widget | 3 | $8.00 | Widget: 5 @ $8.00 | Added 3x Widget to cart |
| Adding a different item alongside an existing one | Widget: 2 @ $8.00 | Gadget | 1 | $15.00 | Widget: 2 @ $8.00, Gadget: 1 @ $15.00 | Added 1x Gadget to cart |
| Adding an item not in the catalogue | [empty] | Sprocket | 1 | (not found) | [empty] | Product not found |
| Adding a non-positive quantity | [empty] | Widget | {0, -1} | $8.00 | [empty] | Quantity must be at least 1 |

Note: price always comes from the catalogue column, never a customer-supplied value — there's no "customer price" input because the system doesn't accept one.

---

### 2. Removes an item from the cart

| Scenario | Cart Before | Item To Remove | Cart After? | Message? |
|---|---|---|---|---|
| Removing the only item in the cart | Widget: 2 @ $8.00 | Widget | [empty] | Removed Widget from cart |
| Removing one item while others remain | Widget: 2 @ $8.00, Gadget: 1 @ $15.00 | Widget | Gadget: 1 @ $15.00 | Removed Widget from cart |
| Removing an item not in the cart | Gadget: 1 @ $15.00 | Widget | Gadget: 1 @ $15.00 | Widget is not in your cart |
| Removing from an already-empty cart | [empty] | Widget | [empty] | Widget is not in your cart |

Open question: should removal support decrementing quantity (remove 1 of 2), or is it always whole-line? Assumed whole-line above.

---

### 3. Validates a coupon code and replaces the cart's active coupon

| Scenario | Active Coupon Before | Coupon Code | Code Status | Expiry Date | Today | Coupon Type | Active Coupon After? | Message? |
|---|---|---|---|---|---|---|---|---|
| Applying a valid coupon with none active | (none) | WELCOME10 | exists | 2026-12-31 | 2026-08-03 | {Percentage, Fixed, Product} | WELCOME10 | Coupon WELCOME10 applied |
| Applying a valid coupon that replaces an active one | SAVE5 | WELCOME10 | exists | 2026-12-31 | 2026-08-03 | {Percentage, Fixed, Product} | WELCOME10 | Coupon WELCOME10 applied |
| Applying a coupon on its exact expiry date | (none) | SUMMER22 | exists | 2026-08-03 | 2026-08-03 | Percentage | SUMMER22 | Coupon SUMMER22 applied |
| Applying a coupon that expired yesterday | (none) | SUMMER22 | exists | 2026-08-02 | 2026-08-03 | Percentage | (none) | Coupon has expired |
| Applying a code that doesn't exist | (none) | FAKE99 | not found | — | 2026-08-03 | — | (none) | Coupon code not found |
| Applying an expired code while another coupon is active | SAVE5 | SUMMER22 | exists | 2026-08-02 | 2026-08-03 | Percentage | SAVE5 | Coupon has expired |

The last row is the assumption I flagged: a failed replacement attempt doesn't clear what's already active. Worth confirming — the alternative (clearing the slot on any attempt) is equally plausible.

---

### 4. Computes the discount amount for the active coupon

| Scenario | Coupon Type | Coupon Value | Cart Subtotal | Target Product Line | Discount Amount? |
|---|---|---|---|---|---|
| Percentage off the whole cart | Percentage | 10% | $50.00 | — | $5.00 |
| Fixed amount off the whole cart | Fixed | $10.00 | $50.00 | — | $10.00 |
| Fixed amount exceeding the subtotal | Fixed | $60.00 | $50.00 | — | $60.00 |
| Discount on a specific product present in the cart | Product | $5.00 off Widget | $50.00 | Widget: 2 @ $8.00 ($16.00) | $5.00 |
| Discount on a specific product absent from the cart | Product | $5.00 off Widget | $50.00 | (Widget not in cart) | $0.00 |

Discount is deliberately left uncapped here (row 3 produces $60 against a $50 subtotal) — flooring at zero is the total calculation's job (table 6), not this one's, so this table doesn't duplicate that rule.

Open question: is a product-specific coupon always a flat amount off that product's line, or can it also be a percentage off that product? Spec only says "a discount on a specific product," so I assumed flat amount — flag if percentage-off-product is also needed, it'd be another row in this same family table.

---

### 5. Sums item lines into a cart subtotal

| Scenario | Line Items | Subtotal? |
|---|---|---|
| Single line item | Widget: 2 @ $8.00 | $16.00 |
| Multiple line items | Widget: 2 @ $8.00, Gadget: 3 @ $5.00 | $31.00 |
| Empty cart | (none) | $0.00 |

---

### 6. Applies the discount to the subtotal, floored at zero

| Scenario | Subtotal | Discount Amount | Total? |
|---|---|---|---|
| No coupon applied | $50.00 | $0.00 | $50.00 |
| Discount smaller than subtotal | $50.00 | $10.00 | $40.00 |
| Discount exactly equal to subtotal | $50.00 | $50.00 | $0.00 |
| Discount larger than subtotal (massive coupon, small order) | $50.00 | $60.00 | $0.00 |

Rows 3 and 4 are the straddling pair that actually proves the floor — anything further past ($50 subtotal, $200 discount) wouldn't add a new obligation.

---

### 7. Rejects checkout when the cart is empty

| Scenario | Cart Contents | Checkout Result? | Message? |
|---|---|---|---|
| Checking out with no items | (empty) | Rejected | Your cart is empty |
| Checking out with at least one item | {Widget: 1 @ $8.00, Widget: 1 @ $8.00 + Gadget: 1 @ $15.00} | Proceeds to stock check | (no rejection) |

---

### 8. Verifies stock levels at checkout

| Scenario | Cart Items (Requested / Available) | Checkout Result? | Message? |
|---|---|---|---|
| All items within stock | Widget: 2/5 | Succeeds | Checkout complete |
| Requested quantity exactly matches stock | Widget: 5/5 | Succeeds | Checkout complete |
| One item exceeds available stock | Widget: 6/5 | Rejected | Only 5 Widgets available |
| Multiple items exceed available stock | Widget: 6/5, Gadget: 3/1 | Rejected | Only 5 Widgets and 1 Gadget available |

Rows 2 and 3 are the boundary pair (exact match succeeds, one-over fails).

---

**Open questions to resolve before implementation:**
1. Does "remove" decrement quantity or always drop the whole line?
2. Is a product-specific coupon a flat amount or a percentage off that product?
3. Does an invalid/expired coupon attempt clear the existing active coupon, or leave it alone?
4. Is coupon expiry inclusive of the expiry date itself?

These eight tables map cleanly to `/tabletest` if you want them turned into executable `@TableTest` code once the open questions are settled — happy to do that next.