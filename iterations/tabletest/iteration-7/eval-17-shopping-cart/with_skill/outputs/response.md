Let me work through this systematically. The shopping cart has four distinct concerns worth separate tables: cart mutations, coupon application, total calculation, and checkout. I'll flag open questions where the spec is ambiguous.

---

## Cart Item Operations

| Scenario | Cart before | Action | Message? | Cart after? |
|---|---|---|---|---|
| Add item to empty cart | [] | add 2× Widget (£5.00) | Added 2× Widget to cart | [2× Widget] |
| Add different item | [1× Gadget] | add 1× Widget (£5.00) | Added 1× Widget to cart | [1× Gadget, 1× Widget] |
| Add more of existing item | [2× Widget] | add 3× Widget (£5.00) | Added 3× Widget to cart | [5× Widget] |
| Remove one item from multi-item cart | [2× Widget, 1× Gadget] | remove Widget | Removed Widget from cart | [1× Gadget] |
| Remove last item | [1× Widget] | remove Widget | Removed Widget from cart | [] |
| Remove item not in cart | [1× Gadget] | remove Widget | Widget is not in your cart | [1× Gadget] |

**Open question:** When adding an item already in the cart, does the quantity *accumulate* (row 3 above) or *replace*? The table assumes accumulation — confirm this.

**Open question:** Does "remove" always remove the entire line item, or can customers remove a partial quantity (e.g. "remove 1 of 3")?

---

## Coupon Application

| Scenario | Existing coupon | Code entered | Code status | Coupon type | Message? | Active coupon after? |
|---|---|---|---|---|---|---|
| Apply first coupon — percentage | none | SAVE10 | valid | 10% off cart | Coupon applied: 10% off | SAVE10 |
| Apply first coupon — fixed amount | none | FIVE_OFF | valid | £5.00 off cart | Coupon applied: £5.00 off | FIVE_OFF |
| Apply first coupon — product-specific | none | WIDGET2 | valid | £2.00 off Widget | Coupon applied: £2.00 off Widget | WIDGET2 |
| Replace existing coupon with new valid code | SAVE10 | SAVE15 | valid | 15% off cart | Coupon applied: 15% off | SAVE15 |
| Enter expired code — no existing coupon | none | OLD20 | expired | — | Coupon has expired | none |
| Enter unknown code | none | NOPE | not found | — | Invalid coupon code | none |
| Enter expired code — existing coupon active | SAVE10 | OLD20 | expired | — | Coupon has expired | SAVE10 |
| Replace existing with unknown code | SAVE10 | NOPE | not found | — | Invalid coupon code | SAVE10 |

**Open question:** Rows 7 and 8 assume a failed application *leaves the existing coupon in place*. Confirm this is the intended behaviour — it's the most user-friendly choice but worth an explicit decision.

---

## Cart Total

| Scenario | Items in cart | Coupon type | Coupon value | Total? |
|---|---|---|---|---|
| No coupon | 2× Widget £5.00, 1× Gadget £10.00 | none | | £20.00 |
| Percentage off cart | 2× Widget £5.00, 1× Gadget £10.00 | % off cart | 10% | £18.00 |
| Fixed amount off cart | 2× Widget £5.00, 1× Gadget £10.00 | fixed off cart | £5.00 | £15.00 |
| Product discount — item in cart | 2× Widget £5.00, 1× Gadget £10.00 | off Widget | £2.00 | £16.00 |
| Product discount — item not in cart | 1× Gadget £10.00 | off Widget | £2.00 | £10.00 |
| Massive coupon — total floors at zero | 1× Widget £5.00 | fixed off cart | £50.00 | £0.00 |
| Large percentage — total floors at zero | 1× Widget £5.00 | % off cart | 100% | £0.00 |

**Open question:** Row 4 assumes the £2.00 product discount is *per unit* (2× Widget = £4.00 off). Is that correct, or is it a flat £2.00 off the whole product line regardless of quantity?

**Open question:** Row 5 — when a product-specific coupon is active but that product isn't in the cart, should the UI surface any message (e.g. "Coupon WIDGET2 doesn't apply to items in your cart"), or is it silent?

---

## Checkout

| Scenario | Cart | Stock: Widget | Stock: Gadget | Outcome? | Message? |
|---|---|---|---|---|---|
| Empty cart | [] | — | — | Fail | Cannot check out with an empty cart |
| All items in stock | [2× Widget, 1× Gadget] | 5 | 3 | Success | Order placed |
| Exactly enough stock | [3× Widget] | 3 | — | Success | Order placed |
| One item short | [3× Widget] | 2 | — | Fail | Only 2× Widget available |
| Item fully out of stock | [1× Gadget] | — | 0 | Fail | Gadget is out of stock |
| Multiple items short | [3× Widget, 2× Gadget] | 2 | 0 | Fail | Only 2× Widget available; Gadget is out of stock |

**Open question:** When multiple items are short (last row), are *all* shortfalls reported in one message, or does checkout report the first failure and stop? The table assumes all-at-once, which is more helpful, but confirm this.

---

## Summary of Open Questions

| # | Question | Impact |
|---|---|---|
| 1 | Does adding an existing item accumulate or replace quantity? | Cart Operations row 3 |
| 2 | Can customers remove a partial quantity, or whole line items only? | Remove behaviour |
| 3 | Does a failed coupon leave the existing coupon in place? | Coupon rows 7–8 |
| 4 | Is a product coupon discount per unit or flat per line? | Cart Total row 4 |
| 5 | Should a non-applicable product coupon surface a warning? | Cart Total row 5 |
| 6 | Are all stock shortfalls reported together, or just the first? | Checkout row 6 |

Resolving these before implementation will prevent the messiest mid-coding surprises. Questions 1 and 4 in particular will affect data model decisions.