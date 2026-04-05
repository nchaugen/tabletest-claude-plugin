Good domain for this — let me work through the behaviors systematically and build out example tables.

The requirements describe five distinct concerns: adding items, removing items, applying coupons, calculating the total, and checking out. I'll give each its own table.

---

## Adding Items

Each row is: given this cart state, when the customer adds this item, expect this result.

| Scenario | Cart before | Product | Qty to add | Cart after? | Message? |
|---|---|---|---|---|---|
| Add to empty cart | [] | Widget | 2 | [Widget ×2] | Added 2x Widget to cart |
| Add new item alongside existing | [Widget ×2] | Gadget | 1 | [Widget ×2, Gadget ×1] | Added 1x Gadget to cart |
| Add more of an item already in cart | [Widget ×2] | Widget | 3 | [Widget ×5] | Added 3x Widget to cart |
| Product not in catalogue | [] | Unknown | 1 | [] | Product not found |

**Open questions:**
- What happens when quantity is 0 or negative — reject with an error, or silently ignore?
- Do we check stock at add-to-cart time, or only at checkout? (Affects whether "only 3 available" can appear here.)

---

## Removing Items

| Scenario | Cart before | Product to remove | Cart after? | Message? |
|---|---|---|---|---|
| Remove one item, others remain | [Widget ×2, Gadget ×1] | Widget | [Gadget ×1] | Removed Widget from cart |
| Remove the last item in cart | [Widget ×2] | Widget | [] | Removed Widget from cart |
| Remove item not in cart | [Widget ×2] | Gadget | [Widget ×2] | Gadget is not in your cart |
| Remove from empty cart | [] | Widget | [] | Your cart is empty |

**Open question:** Does "remove" always remove all units of an item, or can the customer specify a quantity (e.g., remove 1 of 5 Widgets)? This affects whether the cart-after column needs a quantity-remaining value and whether partial removal needs its own message.

---

## Coupon Application

| Scenario | Coupon code | Coupon type | Coupon status | Existing coupon | Active coupon after? | Message? |
|---|---|---|---|---|---|---|
| Apply percentage coupon to fresh cart | SAVE10 | 10% off cart | Valid | none | SAVE10 | Coupon applied: 10% off your order |
| Apply fixed amount coupon | FLAT5 | £5 off cart | Valid | none | FLAT5 | Coupon applied: £5 off your order |
| Apply product-specific coupon | WIDGETDEAL | £2 off Widget | Valid | none | WIDGETDEAL | Coupon applied: £2 off Widget |
| Replace an active coupon with a new one | NEWCODE | 20% off cart | Valid | SAVE10 | NEWCODE | Coupon applied: 20% off your order |
| Expired code, no existing coupon | OLD10 | {any} | Expired | none | none | Coupon has expired |
| Expired code leaves existing coupon intact | OLD10 | {any} | Expired | SAVE10 | SAVE10 | Coupon has expired |
| Code does not exist | FAKE | — | Does not exist | {none, SAVE10} | unchanged | Coupon code not recognised |

The last two rows make a claim worth confirming: **a failed coupon attempt never disturbs the existing active coupon.** If that is intended, it should hold for any existing-coupon state — hence the value set in the last row.

---

## Cart Total

The interesting rules here are which discount type applies, and that the total floors at zero. Once those rules are clear, the arithmetic is straightforward.

| Scenario | Items | Coupon type | Discount | Total? |
|---|---|---|---|---|
| No coupon | Widget ×2 @ £10 | none | — | £20.00 |
| Percentage off cart | Widget ×2 @ £10 | % off cart | 10% | £18.00 |
| Fixed amount off cart | Widget ×2 @ £10 | fixed off cart | £5 | £15.00 |
| Product coupon, item is in cart | Widget ×2 @ £10, Gadget ×1 @ £5 | fixed off Widget | £3 | £22.00 |
| Product coupon, item is not in cart | Gadget ×1 @ £5 | fixed off Widget | £0 | £5.00 |
| Fixed coupon larger than cart total | Widget ×1 @ £5 | fixed off cart | £20 | £0.00 |
| Percentage coupon, multiple items | Widget ×2 @ £10, Gadget ×3 @ £5 | % off cart | 50% | £17.50 |

**Open question:** For a product-specific coupon when that item has qty > 1 — does the discount apply once (£3 off the line) or per unit (£3 × qty)? The table above assumes once; confirm this is intended.

---

## Checkout

| Scenario | Cart | Stock available | Success? | Message? |
|---|---|---|---|---|
| Empty cart | [] | — | no | Your cart is empty |
| Single item, sufficient stock | Widget ×2 | Widget: 5 | yes | Order placed |
| Single item, exactly at stock limit | Widget ×3 | Widget: 3 | yes | Order placed |
| Single item, stock falls one short | Widget ×4 | Widget: 3 | no | Only 3 Widget available |
| Multiple items, all have sufficient stock | Widget ×2, Gadget ×1 | Widget: 5, Gadget: 4 | yes | Order placed |
| Multiple items, one falls short | Widget ×2, Gadget ×3 | Widget: 5, Gadget: 2 | no | Only 2 Gadget available |
| Multiple items, both fall short | Widget ×5, Gadget ×3 | Widget: 3, Gadget: 2 | no | Only 3 Widget available; Only 2 Gadget available |

The "both short" row pins down an important behavior: does checkout report all shortfalls at once, or stop at the first? The row above assumes all-at-once; adjust if the intent is fail-fast.

---

## Summary of open questions to resolve before implementation

| # | Question | Why it matters |
|---|---|---|
| 1 | Can you add qty 0 or negative? | Affects add-item validation |
| 2 | Is stock checked at add time or only at checkout? | Affects which table "Only N available" appears in |
| 3 | Does "remove" always remove all units, or can you remove a partial quantity? | Changes remove-item signature and message format |
| 4 | Does a product coupon apply once per line or once per unit? | Changes total calculation for qty > 1 |
| 5 | Does checkout report all stock shortfalls or stop at the first? | Changes checkout failure message format |

Once those are answered, these tables are ready to hand off to `/tabletest` for implementation — the column structure maps directly.