The shopping cart has several distinct concerns worth separating. Let me work through each one — I'll draft the tables and flag the open questions as I go.

---

## Table 1: Adding Items to Cart

| Scenario | Cart Before | Product | Quantity | Cart After? | Message? |
|---|---|---|---|---|---|
| Add item to empty cart | [] | Widget | 2 | [Widget ×2] | Added 2× Widget to cart |
| Add a second product | [Widget ×2] | Gadget | 1 | [Widget ×2, Gadget ×1] | Added 1× Gadget to cart |
| Add more of existing product | [Widget ×2] | Widget | 3 | [Widget ×5] | Added 3× Widget to cart |
| Product not in catalogue | [] | Unknown | 1 | [] | Product not found |

Prices come from the catalogue, so no price column here — that's a concern for the total calculation table.

---

## Table 2: Removing Items from Cart

| Scenario | Cart Before | Product | Quantity to Remove | Cart After? | Message? |
|---|---|---|---|---|---|
| Remove some of an item | [Widget ×5] | Widget | 2 | [Widget ×3] | Removed 2× Widget from cart |
| Remove all of an item | [Widget ×2] | Widget | 2 | [] | Removed 2× Widget from cart |
| Remove item not in cart | [Gadget ×1] | Widget | 1 | [Gadget ×1] | Widget is not in your cart |
| Remove more than in cart | [Widget ×1] | Widget | 5 | ? | ? |

**Open question:** when removing more units than are in the cart, do you remove what's there (and succeed), or return an error? The last row needs a decision.

---

## Table 3: Applying Coupons

| Scenario | Current Coupon | Code | Code Status | Coupon Type | Active Coupon After? | Message? |
|---|---|---|---|---|---|---|
| Apply percentage coupon to empty slot | none | SAVE10 | valid | 10% off cart | SAVE10 | Coupon SAVE10 applied |
| Apply fixed-amount coupon | none | FLAT5 | valid | £5.00 off cart | FLAT5 | Coupon FLAT5 applied |
| Apply product-specific coupon | none | WIDGET2 | valid | £2.00 off Widget | WIDGET2 | Coupon WIDGET2 applied |
| Replace existing coupon with valid one | SAVE10 | FLAT5 | valid | £5.00 off cart | FLAT5 | Coupon FLAT5 applied |
| Enter expired code | {none, SAVE10} | OLD20 | expired | — | unchanged | Coupon has expired |
| Enter unknown code | {none, SAVE10} | NOTREAL | not found | — | unchanged | Invalid coupon code |

The value set on the last two rows makes the replacement rule explicit in both directions: invalid codes leave whatever coupon was there untouched.

---

## Table 4: Cart Total Calculation

| Scenario | Items | Coupon Type | Coupon Value | Applies To | Total? |
|---|---|---|---|---|---|
| No items, no coupon | [] | none | | | £0.00 |
| Single item, no coupon | [Widget ×2 @ £10] | none | | | £20.00 |
| Multiple items, no coupon | [Widget ×2 @ £10, Gadget ×1 @ £5] | none | | | £25.00 |
| Percentage off whole cart | [Widget ×2 @ £10] | % off cart | 10% | whole cart | £18.00 |
| Fixed amount off | [Widget ×2 @ £10, Gadget ×1 @ £5] | fixed off | £5.00 | whole cart | £20.00 |
| Fixed amount exceeds total (floor at zero) | [Widget ×1 @ £3] | fixed off | £10.00 | whole cart | £0.00 |
| Product coupon, product in cart | [Widget ×2 @ £10, Gadget ×1 @ £5] | product | £3.00 | Widget | ? |
| Product coupon, product not in cart | [Widget ×2 @ £10] | product | £3.00 | Gadget | £20.00 |

**Open question on the product coupon row:** does the £3.00 apply once to the line total (£25 − £3 = £22), or once per unit of that product (£25 − £6 = £19)? This is the most common source of bugs in discount engines — worth pinning down with a concrete example before coding.

---

## Table 5: Checkout

| Scenario | Cart | Stock Levels | Success? | Message? |
|---|---|---|---|---|
| Empty cart | [] | — | no | Cannot check out with an empty cart |
| All items in stock | [Widget ×2, Gadget ×1] | Widget: 5, Gadget: 3 | yes | Order placed |
| Exact stock match | [Widget ×3] | Widget: 3 | yes | Order placed |
| One item insufficient | [Widget ×5] | Widget: 3 | no | Only 3 Widget available |
| Multiple items insufficient | [Widget ×5, Gadget ×2] | Widget: 3, Gadget: 0 | no | ? |
| One item short among several | [Widget ×2, Gadget ×1] | Widget: 1, Gadget: 5 | no | Only 1 Widget available |

**Open question on multiple shortfalls:** does checkout report all shortfalls at once, or just the first one it finds? The message format differs significantly — "Only 3 Widget available" vs "Only 3 Widget available; Gadget out of stock" — so this affects both the domain logic and the frontend contract.

---

## The open questions worth resolving now

1. **Remove more than in cart** — error, or silently remove what's there?
2. **Product-specific coupon** — applies once per line, or once per unit?
3. **Multiple checkout shortfalls** — report all at once or first only?

Any of those you can answer off the top of your head? Once those are settled we can hand the tables straight to `/tabletest` to generate the implementation.