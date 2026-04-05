This domain has four distinct behaviours worth specifying separately. Let me work through each one.

---

## 1. Cart Item Operations

Each row is independent: given this cart state, when this action fires, expect this result.

| Scenario | Cart before | Action | Cart after? | Message? |
|---|---|---|---|---|
| Add item to empty cart | [] | Add 2× Widget | [2× Widget] | Added 2× Widget to cart |
| Add new item alongside existing | [1× Widget] | Add 1× Gadget | [1× Widget, 1× Gadget] | Added 1× Gadget to cart |
| Add more of an item already in cart | [1× Widget] | Add 2× Widget | [3× Widget] | Added 2× Widget to cart |
| Remove one item, others remain | [2× Widget, 1× Gadget] | Remove Widget | [1× Gadget] | Removed Widget from cart |
| Remove the last item | [1× Widget] | Remove Widget | [] | Removed Widget from cart |
| Remove item not in cart | [1× Widget] | Remove Gadget | [1× Widget] | Gadget is not in your cart |

---

## 2. Coupon Application

This table covers whether a coupon is accepted or rejected, and what happens to any previously active coupon. The discount *amount* is covered separately in Table 3.

| Scenario | Active coupon | New code | Coupon status | Active coupon after? | Message? |
|---|---|---|---|---|---|
| Apply valid coupon, none active | none | SAVE10 | valid | SAVE10 | Coupon SAVE10 applied |
| Replace active coupon with valid one | SAVE10 | SUMMER20 | valid | SUMMER20 | Coupon SUMMER20 applied |
| Apply expired coupon, none active | none | OLDCODE | expired | none | Coupon has expired |
| Apply nonexistent code | none | FAKE | does not exist | none | Coupon code not found |
| Expired code does not remove active coupon | SAVE10 | OLDCODE | expired | SAVE10 | Coupon has expired |
| Nonexistent code does not remove active coupon | SAVE10 | FAKE | does not exist | SAVE10 | Coupon code not found |

---

## 3. Cart Total

The interesting rules here are what the three coupon types apply to and the floor-at-zero guarantee. The table is split into the classification rule (which lines are discounted) and the floor rule rather than testing multiplication row-by-row.

**3a. What the discount applies to**

| Scenario | Cart items | Coupon type | Discount | Discounted amount? |
|---|---|---|---|---|
| No coupon | 2× Widget (£5), 1× Gadget (£10) | none | — | £0.00 |
| Percentage off whole cart | 2× Widget (£5), 1× Gadget (£10) | percentage, whole cart | 10% | £2.00 |
| Fixed amount off whole cart | 2× Widget (£5), 1× Gadget (£10) | fixed, whole cart | £5.00 | £5.00 |
| Product coupon, product in cart | 2× Widget (£5), 1× Gadget (£10) | product: Widget | £3.00 | £3.00 |
| Product coupon, product not in cart | 1× Gadget (£10) | product: Widget | £3.00 | £0.00 |

**3b. Floor-at-zero rule**

| Scenario | Cart subtotal | Coupon type | Discount | Total? |
|---|---|---|---|---|
| Discount smaller than subtotal | £20.00 | fixed, whole cart | £5.00 | £15.00 |
| Discount exactly equals subtotal | £20.00 | fixed, whole cart | £20.00 | £0.00 |
| Discount larger than subtotal | £5.00 | fixed, whole cart | £20.00 | £0.00 |
| 100% percentage coupon | £20.00 | percentage, whole cart | 100% | £0.00 |

---

## 4. Checkout

| Scenario | Cart | Stock levels | Checkout result? | Message? |
|---|---|---|---|---|
| Empty cart | [] | — | fails | Cannot check out with an empty cart |
| All items sufficiently stocked | [2× Widget, 1× Gadget] | Widget: 5, Gadget: 3 | succeeds | |
| Requested quantity exactly matches stock | [3× Widget] | Widget: 3 | succeeds | |
| One item short | [5× Widget] | Widget: 3 | fails | Only 3 Widget available |
| Multiple items short | [5× Widget, 4× Gadget] | Widget: 3, Gadget: 1 | fails | Only 3 Widget available; Only 1 Gadget available |
| One item short, one fine | [5× Widget, 1× Gadget] | Widget: 3, Gadget: 5 | fails | Only 3 Widget available |
| One item out of stock entirely | [2× Widget] | Widget: 0 | fails | Widget is out of stock |

---

## Open questions to resolve before coding

A few things the spec leaves ambiguous that will affect implementation:

1. **Product coupon: amount type** — Is the £3.00 discount per unit of that product, or a flat amount off the product line total? (2× Widget at £3 off each = £6 off vs £3 total.) This changes the calculation meaningfully.

2. **Product coupon: product not in cart** — Does applying a product coupon succeed with a £0 discount (coupon is "active" but has no effect), or do we reject it at apply-time with an error?

3. **Checkout success message** — Table 4 shows a blank message for success. Does the frontend receive an explicit "Order placed" confirmation, or just a success status?

4. **Stock check timing** — If a price or stock level changes between "add to cart" and "checkout", which value wins? (This surfaces at the data-access layer but the spec should declare intent.)

5. **Quantity validation** — Can a customer add 0 or a negative quantity? The operations table assumes valid positive integers; worth confirming whether input validation is in scope here.

Once the open questions are resolved, these tables map directly to `@TableTest` — the column structure carries over as-is. The stateful rows (Tables 1 and 2) will each need a small setup helper to build the "before" state; Tables 3 and 4 are pure calculations and will be simpler.