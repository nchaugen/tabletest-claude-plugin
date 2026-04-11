Here are the example tables I'd propose. I've split the shopping cart into five distinct concerns — each is a separate decision with its own rules.

---

## 1. Add Item to Cart

| Scenario | Cart Before | Product | Quantity | Cart After? | Message? |
|---|---|---|---|---|---|
| Add to empty cart | [] | Widget | 1 | [Widget ×1] | Added 1× Widget to cart |
| Add new product to non-empty cart | [Gadget ×2] | Widget | 3 | [Gadget ×2, Widget ×3] | Added 3× Widget to cart |
| Add same product again | [Widget ×1] | Widget | 2 | ? | ? |

> **Open question:** When a product already in the cart is added again, does the quantity increment (Widget ×1 + 2 = Widget ×3), or does it replace (Widget ×2)? This affects the message too.

---

## 2. Remove Item from Cart

| Scenario | Cart Before | Product | Cart After? | Message? |
|---|---|---|---|---|
| Remove only item | [Widget ×2] | Widget | [] | Removed Widget from cart |
| Remove one of several items | [Widget ×2, Gadget ×1] | Widget | [Gadget ×1] | Removed Widget from cart |
| Remove item not in cart | [Gadget ×1] | Widget | [Gadget ×1] | Widget is not in your cart |

> **Open question:** Does remove always clear the entire line regardless of quantity, or does it decrement by one? The spec says "remove them again" which implies the whole line.

---

## 3. Coupon Application

| Scenario | Coupon Code | Coupon Type | Status | Discount | Target Product | Active Coupon Before | Active Coupon After? | Message? |
|---|---|---|---|---|---|---|---|---|
| Apply percentage coupon | SAVE10 | Percentage | Valid | 10% | | none | SAVE10 | Coupon applied: 10% off your order |
| Apply fixed-amount coupon | FIXED5 | Fixed amount | Valid | £5.00 | | none | FIXED5 | Coupon applied: £5.00 off your order |
| Apply product-specific coupon | WIDGET20 | Product | Valid | 20% | Widget | none | WIDGET20 | Coupon applied: 20% off Widget |
| Replace existing coupon | SAVE10 | Percentage | Valid | 10% | | FIXED5 | SAVE10 | Coupon applied: 10% off your order |
| Expired coupon | OLDCODE | {Percentage, Fixed, Product} | Expired | | | {none, SAVE10} | unchanged | Coupon has expired |
| Unrecognised coupon code | NOTREAL | — | Does not exist | — | | {none, SAVE10} | unchanged | Coupon code not recognised |

> **Open questions:**
> - Does replacing a coupon mention the old code in the message (e.g., "FIXED5 replaced")?
> - What happens if a product-specific coupon is applied but that product isn't in the cart? Applied silently (£0 discount) or shown as a warning?

---

## 4. Cart Total

The interesting rule here is the floor at zero. The arithmetic is straightforward once the discount type is known, so this table focuses on the rules — one or two rows per coupon type, plus the floor.

| Scenario | Items | Coupon Type | Coupon Value | Target Product | Total? |
|---|---|---|---|---|---|
| No coupon | Widget ×2 @ £10 | none | | | £20.00 |
| Percentage off whole cart | Widget ×2 @ £10, Gadget ×1 @ £5 | Percentage | 10% | | £22.50 |
| Fixed amount off | Widget ×2 @ £10 | Fixed | £5.00 | | £15.00 |
| Product discount | Widget ×2 @ £10, Gadget ×1 @ £5 | Product | 20% | Widget | £21.00 |
| Product coupon, product not in cart | Gadget ×1 @ £5 | Product | 20% | Widget | ? |
| Fixed discount exceeds total | Widget ×1 @ £5 | Fixed | £20.00 | | £0.00 |
| Percentage discount would reach zero | Widget ×1 @ £5 | Percentage | 100% | | £0.00 |

> **Open question:** Row 5 — product not in cart. Does the £0 discount apply silently, or does this surface somewhere? This interacts with the unanswered question from Table 3.

---

## 5. Checkout

| Scenario | Cart | Stock Levels | Checkout Allowed? | Message? |
|---|---|---|---|---|
| Empty cart | [] | | no | Your cart is empty |
| All items sufficiently stocked | Widget ×2, Gadget ×1 | Widget: 5, Gadget: 3 | yes | |
| Exactly enough stock | Widget ×3 | Widget: 3 | yes | |
| One item one short | Widget ×3 | Widget: 2 | no | Only 2 Widget available (3 requested) |
| Multiple items short | Widget ×2, Gadget ×3 | Widget: 1, Gadget: 1 | no | Only 1 Widget available; Only 1 Gadget available |
| One item short, one fine | Widget ×2, Gadget ×1 | Widget: 1, Gadget: 5 | no | Only 1 Widget available (2 requested) |

> **Open question:** When multiple items are short, does the message list all shortfalls together, or only the first one? The frontend behaviour depends on this.

---

## Summary of open questions

| # | Question | Impact |
|---|---|---|
| 1 | Add same product: increment or replace quantity? | Table 1 row 3, message format |
| 2 | Remove: whole line or decrement by one? | Table 2 |
| 3 | Replace coupon message: mention the old code? | Table 3 message format |
| 4 | Product coupon applied when product not in cart | Tables 3 and 4 |
| 5 | Multiple stock shortfalls: all listed or just first? | Table 5 message format |

Once these are resolved, all five tables map directly to `@TableTest` implementations — the column structure carries over as-is.