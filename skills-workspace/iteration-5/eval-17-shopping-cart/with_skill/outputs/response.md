The cart has several distinct behaviours here — let me separate them into focused tables rather than one large one. I'll work through them and flag the decisions that need resolving.

---

## Table 1: Cart Item Mutations

| Scenario | Cart before | Action | Message? | Cart after? |
|---|---|---|---|---|
| Add first item to empty cart | [] | add Widget x1 | Added 1x Widget to cart | [Widget x1] |
| Add distinct second item | [Widget x1] | add Gadget x2 | Added 2x Gadget to cart | [Widget x1, Gadget x2] |
| Remove one of several items | [Widget x1, Gadget x2] | remove Widget | Removed Widget from cart | [Gadget x2] |
| Remove last item in cart | [Widget x1] | remove Widget | Removed Widget from cart | [] |
| Remove item not in cart | [Widget x1] | remove Gadget | Gadget is not in your cart | [Widget x1] |
| Add item already in cart | [Widget x1] | add Widget x2 | ? | ? |

> **Open question — row 6:** Does adding a product already in the cart accumulate quantity (→ Widget x3) or replace it (→ Widget x2)? The message will differ too: "Updated Widget quantity to 3" vs "Added 2x Widget to cart".

---

## Table 2: Coupon Application

| Scenario | Coupon type | Cart subtotal | Coupon value | Applies to | Discount? | Message? |
|---|---|---|---|---|---|---|
| Percentage off whole cart | % off cart | £100 | 10% | | £10.00 | Coupon applied: 10% off your order |
| Fixed amount off whole cart | fixed off cart | £80 | £15 | | £15.00 | Coupon applied: £15 off your order |
| Product discount, product in cart | fixed off product | £100 | £20 | Widget | £20.00 | Coupon applied: £20 off Widget |
| Product discount, product not in cart | fixed off product | £100 | £20 | Gadget | ? | ? |
| Expired coupon code | {any type} | {any} | | | £0 | Coupon has expired |
| Non-existent coupon code | doesn't exist | {any} | | | £0 | Invalid coupon code |
| New coupon replaces existing one | % off cart | £100 | 5% (replacing 10%) | | £5.00 | ? |

> **Open question — row 4:** When a product-specific coupon is entered but that product isn't in the cart, do you apply no discount silently, or show an error? "This coupon applies to Gadget, which isn't in your cart" would be user-friendly but needs a decision.

> **Open question — row 7:** What does the replacement message say? Options: just "Coupon applied: 5% off your order" (same as applying fresh), or "Coupon updated: 5% off your order (replaced previous code)". Affects frontend copy.

---

## Table 3: Cart Total (floor rule)

The interesting rule here is that the total can't go below zero. The arithmetic is trivial; the boundary is the decision worth nailing down.

| Scenario | Cart subtotal | Discount | Total? |
|---|---|---|---|
| Discount smaller than subtotal | £100 | £30 | £70 |
| Discount equals subtotal exactly | £50 | £50 | £0 |
| Discount exceeds subtotal | £30 | £50 | £0 |
| No coupon active | £75 | £0 | £75 |

---

## Table 4: Checkout Validation

| Scenario | Cart | Stock levels | Allowed? | Message? |
|---|---|---|---|---|
| Empty cart | [] | | no | Your cart is empty |
| All items within stock | [Widget x2] | Widget: 5 | yes | Order placed |
| Quantity exactly matches stock | [Widget x3] | Widget: 3 | yes | Order placed |
| One item short | [Widget x5] | Widget: 3 | no | Only 3x Widget available |
| Item with zero stock | [Widget x2] | Widget: 0 | no | Widget is out of stock |
| Multiple items short | [Widget x5, Gadget x2] | Widget: 3, Gadget: 1 | no | ? |
| One item short, one fine | [Widget x5, Gadget x2] | Widget: 3, Gadget: 10 | no | Only 3x Widget available |

> **Open question — row 6:** When several items are short, does the message list all of them or only the first? "Only 3x Widget available; only 1x Gadget available" is more helpful but adds complexity. Worth deciding before implementation.

---

## Summary of Open Questions

| # | Question | Why it matters |
|---|---|---|
| 1 | Add existing item: accumulate or replace quantity? | Message copy and cart-after state differ |
| 2 | Product coupon when product not in cart: silent no-op or user-facing error? | Determines whether you need a validation step at coupon entry |
| 3 | Coupon replacement message: same as fresh apply, or mention the replacement? | Frontend copy and UX expectation |
| 4 | Multiple stock failures: list all or first only? | Implementation complexity and message format |

Once these are resolved, each table maps directly to a `@TableTest`. Shall I draft the Java/Kotlin test code for any of these once the open questions are settled?