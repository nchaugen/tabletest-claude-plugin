Good candidate for spec-by-example. There are five distinct concerns here — let me work through each with a table, highlighting the edge cases and open questions as we go.

---

## 1. Adding Items to Cart

| Scenario | Cart Before | Product | Price (Catalogue) | Quantity | Cart After? | Message? |
|---|---|---|---|---|---|---|
| Add to empty cart | [] | Widget | £10 | 1 | [1× Widget] | Added 1× Widget to cart |
| Add with quantity > 1 | [] | Widget | £10 | 3 | [3× Widget] | Added 3× Widget to cart |
| Add second distinct product | [1× Widget] | Gadget | £5 | 2 | [1× Widget, 2× Gadget] | Added 2× Gadget to cart |
| Add same product again | [2× Widget] | Widget | £10 | 1 | ? | ? |

**Open question — row 4:** Does adding the same product a second time *accumulate* (→ 3× Widget) or *replace* (→ 1× Widget)? The message and post-state depend on this decision.

---

## 2. Removing Items from Cart

| Scenario | Cart Before | Product Removed | Cart After? | Message? |
|---|---|---|---|---|
| Remove only item | [1× Widget] | Widget | [] | Removed Widget from cart |
| Remove one of multiple products | [1× Widget, 2× Gadget] | Widget | [2× Gadget] | Removed Widget from cart |
| Remove product not in cart | [1× Widget] | Gadget | [1× Widget] | Gadget is not in your cart |
| Remove from empty cart | [] | Widget | [] | Your cart is empty |

---

## 3. Coupon Application

| Scenario | Current Coupon | New Code | Code Status | Coupon Type | Active Coupon After? | Message? |
|---|---|---|---|---|---|---|
| Apply % coupon to empty cart | none | SAVE10 | valid | 10% off cart | SAVE10 | 10% discount applied |
| Apply fixed coupon | none | FLAT5 | valid | £5 off cart | FLAT5 | £5 off applied |
| Apply product coupon | none | WIDGETDEAL | valid | 20% off Widget | WIDGETDEAL | 20% off Widget applied |
| Replace existing coupon | SAVE10 | NEW20 | valid | 20% off cart | NEW20 | 20% discount applied |
| Apply expired code | none | OLDCODE | expired | — | none | Coupon has expired |
| Apply nonexistent code | none | FAKE | not found | — | none | Invalid coupon code |
| Expired code does not clear active coupon | SAVE10 | OLDCODE | expired | — | SAVE10 | Coupon has expired |

**Open question — row 7 (and expired replacement generally):** When an invalid code is entered, should any existing active coupon stay active? The table above assumes yes — entering a bad code doesn't blow away your current discount. Worth confirming.

---

## 4. Cart Total Calculation

| Scenario | Cart Contents | Coupon Type | Coupon Value | Total? |
|---|---|---|---|---|
| No coupon | 2× Widget £10 | none | — | £20 |
| Percentage off cart | 2× Widget £10 | % off cart | 10% | £18 |
| Fixed amount off | 2× Widget £10 | fixed off | £5 | £15 |
| Product coupon — product in cart | 2× Widget £10, 1× Gadget £5 | % off Widget | 50% | £15 |
| Product coupon — product not in cart | 1× Gadget £5 | % off Widget | 50% | £5 |
| Fixed coupon larger than total | 1× Widget £10 | fixed off | £15 | £0 |
| Percentage coupon, very small cart | 1× Widget £1 | % off cart | 100% | £0 |

**Open question — row 4 (product coupon scope):** Does the product coupon apply to *all units* of that product in the cart (so 2× Widget at 50% off → both units discounted), or only *one unit*? The table above assumes all units — £10 total for widgets instead of £15.

---

## 5. Checkout

| Scenario | Cart | Stock Available | Checkout Result? | Message? |
|---|---|---|---|---|
| Empty cart | [] | — | fail | Your cart is empty |
| All items in stock | [2× Widget] | Widget: 5 | success | Order placed |
| Exactly enough stock | [3× Widget] | Widget: 3 | success | Order placed |
| One item insufficient | [2× Widget] | Widget: 1 | fail | Only 1 Widget available |
| Item completely out of stock | [1× Widget] | Widget: 0 | fail | Widget is out of stock |
| Multiple items, one short | [2× Widget, 1× Gadget] | Widget: 5, Gadget: 0 | fail | Gadget is out of stock |
| Multiple items, multiple short | [2× Widget, 3× Gadget] | Widget: 1, Gadget: 2 | fail | Only 1 Widget available; Only 2 Gadget available |

**Open question — multiple shortfalls:** Row 7 assumes the error lists *all* shortfalls at once. Confirm this is preferable to failing on the first one found.

---

## Summary of Open Questions

| # | Question | Rows affected |
|---|---|---|
| 1 | Adding the same product: accumulate qty or replace? | Add to Cart row 4 |
| 2 | Invalid/expired code: does it preserve the active coupon? | Coupon Application row 7 |
| 3 | Product coupon: applies to all units or just one? | Cart Total row 4 |
| 4 | Multiple stock failures: report all at once or first only? | Checkout row 7 |

Once these are resolved, this maps cleanly to `/tabletest` to produce the `@TableTest` code. Which question do you want to tackle first?