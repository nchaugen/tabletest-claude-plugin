I'll work through the distinct concerns here and produce example tables for each, then flag the open questions that surface.

---

## Cart Operations (Add / Remove)

| Scenario | Cart Before | Action | Message? | Cart After? |
|---|---|---|---|---|
| Add item to empty cart | [] | add 2x Widget | Added 2x Widget to cart | [2x Widget] |
| Add same item again | [1x Widget] | add 1x Widget | Added 1x Widget to cart | [2x Widget] |
| Add different item | [1x Widget] | add 1x Gadget | Added 1x Gadget to cart | [1x Widget, 1x Gadget] |
| Remove only item in cart | [2x Widget] | remove Widget | Removed Widget from cart | [] |
| Remove one of several items | [1x Widget, 1x Gadget] | remove Widget | Removed Widget from cart | [1x Gadget] |
| Remove item not in cart | [1x Widget] | remove Gadget | Gadget is not in your cart | [1x Widget] |
| Unknown product code | [] | add 1x XYZZY | Product not found | [] |

**Open questions:**
- Does removing a line item remove the entire quantity, or decrements by 1? The table above assumes the whole line is removed.
- Can a customer add quantity 0 or a negative quantity? If so, what should happen?

---

## Coupon Application

| Scenario | Coupon Code | Coupon Type | Active Coupon Before | Code Status | Applied Coupon? | Message? |
|---|---|---|---|---|---|---|
| Apply percentage coupon to fresh cart | SAVE10 | percentage off cart | none | valid | SAVE10 | Coupon SAVE10 applied: 10% off |
| Apply fixed-amount coupon | FLAT5 | fixed amount off | none | valid | FLAT5 | Coupon FLAT5 applied: £5 off |
| Apply product-specific coupon | WIDGET20 | product discount | none | valid | WIDGET20 | Coupon WIDGET20 applied |
| New coupon replaces existing | NEWDEAL | percentage off cart | SAVE10 | valid | NEWDEAL | Coupon NEWDEAL applied: 15% off |
| Expired coupon — no existing coupon | OLDCODE | {percentage, fixed, product discount} | none | expired | none | Coupon has expired |
| Expired coupon — had existing coupon | OLDCODE | {percentage, fixed, product discount} | SAVE10 | expired | SAVE10 | Coupon has expired |
| Unrecognised coupon code | BADCODE | — | {none, SAVE10} | not found | unchanged | Invalid coupon code |

**Open questions:**
- When a product-specific coupon is applied but that product isn't currently in the cart — is the coupon accepted (discount just doesn't apply yet) or rejected with an error?
- When an expired/invalid code is entered and a coupon was already active, does the previous coupon remain active? (Table assumes yes.)
- Should the confirmation message repeat the discount value, or just confirm the code?

---

## Cart Total

The interesting rules here are how each coupon type is applied and the zero floor — not the arithmetic itself.

| Scenario | Subtotal (before discount) | Coupon Type | Discount Value | Target Product | Total? |
|---|---|---|---|---|---|
| No coupon | £35.00 | none | | | £35.00 |
| Percentage off whole cart | £35.00 | percentage off cart | 10% | | £31.50 |
| Fixed amount off | £35.00 | fixed amount off | £5.00 | | £30.00 |
| Product discount — product is in cart | £35.00 | product discount | 20% | Widget (£20 of subtotal) | £31.00 |
| Product discount — product not in cart | £15.00 | product discount | 20% | Widget | £15.00 |
| Fixed discount exceeds cart value | £8.00 | fixed amount off | £50.00 | | £0.00 |
| 100% percentage coupon | £8.00 | percentage off cart | 100% | | £0.00 |

**Open questions:**
- For a product-specific coupon, is the discount percentage off the unit price, or off the line total (qty × unit price)? The table assumes line total.
- Does a product-specific coupon apply per unit (discount multiplied by quantity) or as a flat amount regardless of quantity?

---

## Checkout Validation

| Scenario | Cart | Stock Available | Checkout Succeeds? | Message? |
|---|---|---|---|---|
| Empty cart | [] | — | no | Your cart is empty |
| All items fully in stock | [2x Widget, 1x Gadget] | Widget: 5, Gadget: 3 | yes | |
| One item understocked | [3x Widget, 1x Gadget] | Widget: 1, Gadget: 3 | no | Only 1 Widget available |
| Exact stock match | [2x Widget] | Widget: 2 | yes | |
| Item with zero stock | [1x Widget] | Widget: 0 | no | Only 0 Widget available |
| Multiple items short | [3x Widget, 2x Gadget] | Widget: 1, Gadget: 1 | no | Only 1 Widget available; Only 1 Gadget available |

**Open questions:**
- When multiple items are short, is the customer shown all shortfalls at once (so they can update the whole cart in one go), or only the first one found? The table assumes all shortfalls are reported together.
- After a failed checkout, does the cart stay as-is (so the customer can adjust quantities), or is any partial state possible?
- Is stock checked at add-to-cart time as well as at checkout? If so, "add item to empty cart" may also need an insufficient-stock path.

---

These four tables cover the main decision points. The most important open questions to resolve before coding are the **remove semantics** (whole line vs. decrement), the **product-specific coupon behaviour when the product isn't in the cart**, and the **multi-shortfall reporting** at checkout — those will affect your API contract and the frontend integration.