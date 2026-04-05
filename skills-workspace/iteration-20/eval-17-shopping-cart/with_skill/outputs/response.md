Good set of requirements. Let me work through the distinct behavioral concerns and build out example tables for each one. I'll flag open questions where the spec is ambiguous.

---

## Cart Item Operations

| Scenario | Cart before | Action | Item | Qty | In Catalogue | Message? | Cart after? |
|---|---|---|---|---|---|---|---|
| Add item to empty cart | [] | add | Widget | 2 | yes | Added 2x Widget to cart | [Widget x2] |
| Add different item | [Widget x2] | add | Gadget | 1 | yes | Added 1x Gadget to cart | [Widget x2, Gadget x1] |
| Add more of same item | [Widget x2] | add | Widget | 3 | yes | Added 3x Widget to cart | [Widget x5] |
| Add item not in catalogue | [] | add | Gizmo | 1 | no | Product not found | [] |
| Remove item from cart | [Widget x2, Gadget x1] | remove | Widget | — | yes | Removed Widget from cart | [Gadget x1] |
| Remove item not in cart | [Gadget x1] | remove | Widget | — | yes | Widget is not in your cart | [Gadget x1] |

**Open question:** Does remove always clear the whole line item, or can a customer reduce quantity (e.g. remove 1 of 3)?

---

## Coupon Application

| Scenario | Coupon Code | Type | Status | Existing Coupon | Applied? | Message? |
|---|---|---|---|---|---|---|
| Valid percentage coupon, no existing | SAVE10 | Percentage | Active | none | yes | Coupon SAVE10 applied: 10% off |
| Valid fixed coupon, no existing | FLAT5 | Fixed amount | Active | none | yes | Coupon FLAT5 applied: £5.00 off |
| Valid product coupon, no existing | WIDGET20 | Product (Widget) | Active | none | yes | Coupon WIDGET20 applied: 20% off Widget |
| Expired coupon | OLD10 | {Percentage, Fixed, Product} | Expired | none | no | Coupon has expired |
| Unknown coupon code | FAKE | — | Does not exist | none | no | Invalid coupon code |
| New valid coupon replaces existing | SAVE20 | Percentage | Active | SAVE10 | yes | Coupon SAVE20 applied: 20% off |

**Open questions:**
- Does the replacement message mention the old code (e.g. "SAVE10 replaced by SAVE20") or just confirm the new one?
- For product coupons: is the discount always a percentage, or can it also be a fixed amount off that product?
- What happens if a product coupon is applied but that product isn't in the cart? Is it accepted silently (zero discount) or rejected?

---

## Cart Total

| Scenario | Cart Contents | Subtotal | Coupon Type | Coupon Value | Discount? | Total? |
|---|---|---|---|---|---|---|
| No coupon | Widget x2 @ £15 | £30.00 | none | — | £0.00 | £30.00 |
| Percentage off whole cart | Widget x2 @ £15 | £30.00 | Percentage | 10% | £3.00 | £27.00 |
| Fixed amount off | Widget x2 @ £15 | £30.00 | Fixed | £5.00 | £5.00 | £25.00 |
| Product coupon, item in cart | Widget x2 @ £15, Gadget x1 @ £20 | £50.00 | Product (Widget) | 20% | £6.00 | £44.00 |
| Product coupon, item not in cart | Gadget x1 @ £20 | £20.00 | Product (Widget) | 20% | £0.00 | £20.00 |
| Fixed coupon exceeds subtotal | Gadget x1 @ £8 | £8.00 | Fixed | £20.00 | £8.00 | £0.00 |
| Percentage coupon at 100% | Widget x1 @ £15 | £15.00 | Percentage | 100% | £15.00 | £0.00 |

The "product coupon, item not in cart" row depends on resolving the open question above — mark it `?` if the answer isn't settled.

---

## Checkout Validation

| Scenario | Cart | Stock Available | Checkout Allowed? | Message? |
|---|---|---|---|---|
| Successful checkout | [Widget x2, Gadget x1] | Widget: 5, Gadget: 3 | yes | Order placed |
| Empty cart | [] | — | no | Your cart is empty |
| Insufficient stock, single item | [Widget x5] | Widget: 3 | no | Only 3 Widget available |
| Out of stock entirely | [Widget x1] | Widget: 0 | no | Widget is out of stock |
| Exactly enough stock | [Widget x3] | Widget: 3 | yes | Order placed |
| Multiple items short | [Widget x5, Gadget x3] | Widget: 3, Gadget: 1 | no | Only 3 Widget available; Only 1 Gadget available |
| One item short, one OK | [Widget x5, Gadget x1] | Widget: 3, Gadget: 5 | no | Only 3 Widget available |

**Open question:** When multiple items are short, do you report all shortfalls in one response, or fail on the first one found? The "multiple items short" row above assumes all-at-once — confirm this is what the frontend expects.

---

## What's still open

Collecting the open questions in one place:

1. **Remove quantity vs remove line** — remove 1-of-3 Widgets, or always remove the whole line?
2. **Product coupon discount form** — percentage off that product, fixed amount off, or both?
3. **Product coupon, product absent** — silent £0 discount, or reject the coupon?
4. **Coupon replacement message** — mention the old code or just confirm the new one?
5. **Multiple stock failures** — report all shortfalls or stop at the first?

Resolving these will fill in the `?` cells and may surface a couple of new rows. Once they're settled, these tables map directly to `@TableTest` via `/tabletest`.