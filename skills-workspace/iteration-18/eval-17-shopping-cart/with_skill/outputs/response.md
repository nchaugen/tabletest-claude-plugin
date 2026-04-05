There are four distinct concerns here worth specifying separately. Let me work through each one with concrete examples, noting the open questions as I go.

---

## Adding Items to Cart

| Scenario | Cart before | Product | Unit price | Qty to add | Cart after? | Message? |
|---|---|---|---|---|---|---|
| Add item to empty cart | [] | Widget | £10 | 1 | [Widget×1] | Added 1x Widget to cart |
| Add multiple units at once | [] | Widget | £10 | 3 | [Widget×3] | Added 3x Widget to cart |
| Add a second distinct product | [Widget×2] | Gadget | £25 | 1 | [Widget×2, Gadget×1] | Added 1x Gadget to cart |
| Add more of an existing product | [Widget×1] | Widget | £10 | 2 | [Widget×3] | Added 2x Widget to cart |
| Add zero quantity | {[], [Widget×1]} | Widget | £10 | 0 | ? | ? |

**Open:** Does adding zero quantity succeed silently, reject with an error, or is it simply not a valid input? And does the product have to exist in the catalogue to be added, or is that enforced elsewhere?

---

## Removing Items from Cart

| Scenario | Cart before | Product to remove | Cart after? | Message? |
|---|---|---|---|---|
| Remove the only item | [Widget×1] | Widget | [] | Removed Widget from cart |
| Remove one product among several | [Widget×2, Gadget×1] | Widget | [Gadget×1] | Removed Widget from cart |
| Remove item not in cart | [Gadget×1] | Widget | [Gadget×1] | Widget is not in your cart |
| Remove from empty cart | [] | Widget | [] | Widget is not in your cart |

**Open:** Does remove clear the entire line (all units), or reduce by one? "Remove 2 Widgets" vs "remove Widgets from cart" are two different operations — which is intended?

---

## Applying a Coupon Code

| Scenario | Coupon code | Code status | Coupon type | Previously active coupon | Active coupon after? | Message? |
|---|---|---|---|---|---|---|
| Apply percentage coupon, no prior coupon | SAVE10 | valid | 10% off cart | none | SAVE10 | Coupon applied: 10% off your order |
| Apply fixed-amount coupon | FLAT5 | valid | £5 off cart | none | FLAT5 | Coupon applied: £5 off your order |
| Apply product-specific coupon | WIDGET2 | valid | £2 off Widget | none | WIDGET2 | Coupon applied: £2 off Widget |
| New coupon replaces active one | SAVE20 | valid | 20% off cart | SAVE10 | SAVE20 | Coupon applied: 20% off your order |
| Expired code | OLD50 | expired | 50% off cart | none | none | Coupon has expired |
| Non-existent code | FAKE | does not exist | — | none | none | Coupon code not found |
| Expired code entered while another is active | OLD50 | expired | 50% off cart | SAVE10 | ? | ? |

**Open:** When an invalid code is submitted and a different coupon is already active, does the existing coupon stay or get cleared? The last row needs a decision.

---

## Discount Calculation

| Scenario | Cart items | Coupon type | Coupon value | Cart subtotal | Discount? | Total? |
|---|---|---|---|---|---|---|
| No coupon | [Widget×2 @£10, Gadget×1 @£25] | none | — | £45 | £0 | £45 |
| Percentage off entire cart | [Widget×2 @£10, Gadget×1 @£25] | % off cart | 10% | £45 | £4.50 | £40.50 |
| Fixed amount off entire cart | [Widget×2 @£10, Gadget×1 @£25] | fixed off cart | £15 | £45 | £15 | £30 |
| Product coupon, product is in cart | [Widget×2 @£10, Gadget×1 @£25] | fixed off product | £3 off Widget | £45 | £3 | £42 |
| Product coupon, product not in cart | [Gadget×1 @£25] | fixed off product | £3 off Widget | £25 | £0 | £25 |
| Fixed coupon larger than cart total | [Widget×1 @£5] | fixed off cart | £20 | £5 | £5 | £0 |
| 100% percentage coupon | [Widget×2 @£10] | % off cart | 100% | £20 | £20 | £0 |

**Open:** For the product coupon — is the discount applied once per line (flat £3 off), or per unit (£3 × 2 Widgets = £6 off)? The "product coupon, product in cart" row needs that answered before the discount and total values can be filled in confidently.

---

## Checkout

| Scenario | Cart | Stock levels | Checkout result? | Message? |
|---|---|---|---|---|
| Empty cart | [] | — | rejected | Cannot check out with an empty cart |
| All items sufficiently stocked | [Widget×2, Gadget×1] | Widget: 5, Gadget: 3 | approved | |
| Exactly enough stock | [Widget×3] | Widget: 3 | approved | |
| One unit short | [Widget×4] | Widget: 3 | rejected | Only 3 Widget available |
| Item completely out of stock | [Widget×1] | Widget: 0 | rejected | Widget is out of stock |
| Multiple items short | [Widget×5, Gadget×2] | Widget: 3, Gadget: 0 | rejected | Only 3 Widget available; Gadget is out of stock |
| One of two items short, other is fine | [Widget×5, Gadget×1] | Widget: 3, Gadget: 5 | rejected | Only 3 Widget available |

**Open:** When multiple items are short, does checkout report all shortfalls at once or stop at the first? The "multiple items short" row assumes all-at-once — confirm this is right.

---

The four open questions that need answers before implementation:
1. Is adding zero quantity valid?
2. Does remove clear the whole line or decrement by one?
3. Does an invalid coupon entry leave an existing coupon untouched?
4. Is a product coupon a flat discount per line, or multiplied by quantity?

Once those are resolved, these tables are ready to hand to `/tabletest` for implementation.