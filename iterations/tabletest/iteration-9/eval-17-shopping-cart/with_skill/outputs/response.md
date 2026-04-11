Let me work through the distinct concerns here and build a table for each. The cart has five separable behaviours: adding items, removing items, applying coupons, calculating the total (with discount floor), and validating checkout. I'll take them one at a time.

---

## Add Item to Cart

| Scenario | Cart before | Product | Unit Price | Quantity | Cart after? | Message? |
|---|---|---|---|---|---|---|
| Add to empty cart | [] | Widget | £10 | 1 | [Widget ×1] | Added 1× Widget to cart |
| Add new item to non-empty cart | [Widget ×1] | Gadget | £20 | 1 | [Widget ×1, Gadget ×1] | Added 1× Gadget to cart |
| Add multiple of one item | [] | Widget | £10 | 3 | [Widget ×3] | Added 3× Widget to cart |
| Increase quantity of existing item | [Widget ×1] | Widget | £10 | 2 | [Widget ×3] | Added 2× Widget to cart |
| Add zero quantity | [] | Widget | £10 | 0 | [] | ? |
| Add negative quantity | [] | Widget | £10 | -1 | [] | ? |

**Open questions:**
- What happens with quantity ≤ 0? Error message, silent no-op, or something else?
- Does adding an existing item always add on top of the current quantity, or does it set the quantity?

---

## Remove Item from Cart

| Scenario | Cart before | Product to remove | Cart after? | Message? |
|---|---|---|---|---|
| Remove one of several items | [Widget ×1, Gadget ×1] | Widget | [Gadget ×1] | Removed Widget from cart |
| Remove the last item | [Widget ×1] | Widget | [] | Removed Widget from cart |
| Remove item with multiple quantity | [Widget ×3] | Widget | [] | Removed Widget from cart |
| Remove item not in cart | [Widget ×1] | Gadget | [Widget ×1] | Gadget is not in your cart |

**Open question:** Does remove always clear all quantity of that item, or should there be a "remove N" operation that reduces quantity?

---

## Apply Coupon

Coupon validity and replacement are the interesting rules here — keep the discount arithmetic for the next table.

| Scenario | Coupon code | Coupon type | Coupon status | Existing coupon | Applied? | Message? |
|---|---|---|---|---|---|---|
| Apply percentage-off coupon | SAVE10 | 10% off cart | valid | none | yes | Coupon applied: 10% off your order |
| Apply fixed-amount coupon | FLAT5 | £5 off cart | valid | none | yes | Coupon applied: £5 off your order |
| Apply product-specific coupon | WIDG20 | 20% off Widget | valid | none | yes | Coupon applied: 20% off Widget |
| Expired coupon | OLD10 | 10% off cart | expired | none | no | Coupon has expired |
| Non-existent coupon | FAKECODE | — | does not exist | none | no | Invalid coupon code |
| Replace existing coupon with valid one | SAVE20 | 20% off cart | valid | SAVE10 | yes | Coupon applied: 20% off your order |
| Attempt to replace with expired coupon | OLD10 | 10% off cart | expired | SAVE10 | ? | Coupon has expired |

**Open question (last row):** When the replacement coupon is invalid, does the existing coupon stay active, or is it cleared? The answer matters for what the cart state is after this operation.

---

## Discount Calculation

Separate from coupon validity — this table specifies how each coupon type affects the total and tests the zero floor.

| Scenario | Cart items | Coupon type | Discount | Cart total? |
|---|---|---|---|---|
| No coupon | Widget ×2 (£10 each) | none | — | £20.00 |
| Percentage off entire cart | Widget ×2 (£10 each) | % off cart | 10% | £18.00 |
| Fixed amount off | Widget ×2 (£10 each) | fixed off cart | £5 | £15.00 |
| Product discount, item in cart | Widget ×1 (£10), Gadget ×1 (£20) | % off Widget | 50% | £25.00 |
| Product discount, item not in cart | Gadget ×1 (£20) | % off Widget | 50% | £20.00 |
| Fixed discount exceeds cart total | Widget ×1 (£5) | fixed off cart | £10 | £0.00 |
| Multiple quantity with product discount | Widget ×3 (£10 each) | % off Widget | 20% | £24.00 |

**Open question (product coupon, item not in cart):** Does the coupon apply silently with no effect (£20 total), or should the system warn the customer that the coupon doesn't apply to anything in their cart?

---

## Checkout Validation

| Scenario | Cart | Stock: Widget | Stock: Gadget | Checkout succeeds? | Message? |
|---|---|---|---|---|---|
| Empty cart | [] | — | — | no | Your cart is empty |
| All items well within stock | [Widget ×2, Gadget ×1] | 10 | 5 | yes | Order placed successfully |
| Quantity exactly equals stock | [Widget ×5] | 5 | — | yes | Order placed successfully |
| Quantity one over stock | [Widget ×6] | 5 | — | no | Only 5 Widget available |
| One item short of stock | [Widget ×5, Gadget ×1] | 3 | 5 | no | Only 3 Widget available |
| Multiple items short of stock | [Widget ×5, Gadget ×3] | 3 | 1 | no | Only 3 Widget available; Only 1 Gadget available |
| Out of stock entirely | [Widget ×1] | 0 | — | no | Only 0 Widget available |

**Open question (multiple shortfalls):** When several items fail the stock check, are all failures reported together, or just the first one? The row above assumes all — worth confirming with the frontend team since it affects the message format.

---

## Summary of open questions to resolve before coding

| # | Question | Why it matters |
|---|---|---|
| 1 | Quantity ≤ 0 on add — error or no-op? | Determines whether add needs validation |
| 2 | Add existing item — accumulate or set? | Affects what the cart state is after the call |
| 3 | Remove — all quantity or N at a time? | Changes the remove API shape |
| 4 | Replacement with invalid coupon — keep old or clear? | Cart state differs; affects what coupon is active after the call |
| 5 | Product coupon, item not in cart — silent or warn? | Changes the apply-coupon message |
| 6 | Multiple checkout failures — all reported or just first? | Affects the checkout message format |

These six decisions cover the ambiguous edges. Everything else in the tables is specific enough to implement against directly.