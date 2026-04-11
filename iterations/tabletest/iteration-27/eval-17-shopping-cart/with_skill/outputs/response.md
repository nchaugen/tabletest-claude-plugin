Four distinct concerns emerge from this description. I'll work through them as separate tables — they have different column shapes and different rules to pin down.

---

## Cart Item Operations

Each row is a state transition: given this cart, when this action happens, expect this result.

| Scenario | Cart before | Action | Message? | Cart after? |
|---|---|---|---|---|
| Add to empty cart | [] | Add 2x Widget | Added 2x Widget to cart | [2x Widget] |
| Add different item | [1x Widget] | Add 1x Gadget | Added 1x Gadget to cart | [1x Widget, 1x Gadget] |
| Add more of existing item | [1x Widget] | Add 2x Widget | Added 2x Widget to cart | [3x Widget] |
| Remove item, others remain | [1x Widget, 1x Gadget] | Remove Widget | Removed Widget from cart | [1x Gadget] |
| Remove last item | [1x Widget] | Remove Widget | Removed Widget from cart | [] |
| Remove item not in cart | [1x Widget] | Remove Gadget | Gadget is not in your cart | [1x Widget] |

**Open questions:**
- Row 3 assumes adding an existing item *increments* the quantity (1 + 2 = 3). Does it instead *replace* it (set to 2)? Or is that not a supported operation at all — do customers always set a specific quantity?
- Does Remove always remove the entire line, or can it reduce by a specified quantity (e.g. "remove 1 of 3")?

---

## Coupon Application

The interesting rules here are: what happens to the active coupon when a code is entered, and what errors look like. The discount amount is a separate concern (next table).

| Scenario | Current coupon | Code entered | Code status | Active coupon after? | Message? |
|---|---|---|---|---|---|
| Apply first coupon | none | SAVE10 | valid | SAVE10 | Coupon SAVE10 applied |
| Valid code replaces existing | SAVE10 | FLAT5 | valid | FLAT5 | Coupon FLAT5 applied |
| Expired code, no existing coupon | none | SUMMER21 | expired | none | Coupon SUMMER21 has expired |
| Unknown code, no existing coupon | none | BOGUS | not found | none | Coupon code BOGUS not found |
| Expired code, existing coupon active | SAVE10 | SUMMER21 | expired | SAVE10 | Coupon SUMMER21 has expired |
| Unknown code, existing coupon active | SAVE10 | BOGUS | not found | SAVE10 | Coupon code BOGUS not found |

**Open questions:**
- Rows 5–6 assume an invalid code leaves the existing coupon untouched. Is that right? Or should attempting an invalid code also clear the active coupon?
- Is "expired" a distinct message from "not found", or do both get a generic "invalid code" error? (Rows 3–6 assume they're distinct — a customer who typed their code correctly but it lapsed would want to know it expired rather than wonder if they mis-typed it.)

---

## Coupon Discount Calculation

The interesting rules: which coupon type applies to what, and what happens when the discount would take the total below zero.

| Scenario | Items | Coupon type | Coupon value | Discount applied? | Cart total? |
|---|---|---|---|---|---|
| No coupon | 2x Widget £10 | none | — | £0.00 | £20.00 |
| Percentage off whole cart | 2x Widget £10 | % off cart | 10% | £2.00 | £18.00 |
| Fixed amount off whole cart | 2x Widget £10 | fixed off cart | £5.00 | £5.00 | £15.00 |
| Product discount, item in cart | 2x Widget £10, 1x Gadget £5 | product discount | £3 off Widget | £6.00 | £19.00 |
| Product discount, item not in cart | 2x Widget £10 | product discount | £3 off Gadget | £0.00 | £20.00 |
| Fixed discount exceeds subtotal | 1x Widget £10 | fixed off cart | £15.00 | £10.00 | £0.00 |
| Percentage discount hits floor | 1x Widget £10 | % off cart | 100% | £10.00 | £0.00 |

Row 4 assumes a product discount applies **per unit** (£3 × 2 Widgets = £6 off). Rows 6–7 show the floor in action: the discount applied is capped at the subtotal, not the coupon face value.

**Open questions:**
- Row 4: is the product discount per unit (£3 off each Widget in the cart) or off the entire product line regardless of quantity (always £3 off, however many Widgets are present)?
- Does the floor apply after all discounts are combined, or per-line? (Assumed: total floor only.)

---

## Checkout Validation

| Scenario | Cart | Stock available | Checkout allowed? | Message? |
|---|---|---|---|---|
| Normal checkout | [2x Widget, 1x Gadget] | Widget: 10, Gadget: 5 | yes | Order placed |
| Empty cart | [] | — | no | Your cart is empty |
| Exactly enough stock | [3x Widget] | Widget: 3 | yes | Order placed |
| One item short | [2x Widget] | Widget: 1 | no | Only 1 Widget available |
| Item completely out of stock | [1x Widget] | Widget: 0 | no | Widget is out of stock |
| Multiple items short | [2x Widget, 3x Gadget] | Widget: 1, Gadget: 2 | no | ? |
| One item short, others OK | [2x Widget, 1x Gadget] | Widget: 1, Gadget: 5 | no | Only 1 Widget available |

**Open questions:**
- Row 6 (multiple items short): is there one error message per shortage, shown as a list? Or a single combined message? The spec example 'Only 3 available' is terse — does it identify the item name when there are multiple shortages?
- Rows 4 and 5: "Only 1 available" vs "out of stock" — are these two separate message formats, or should zero stock also say "Only 0 available"? The distinction matters for the implementation.
- After a failed checkout (stock error), does the cart stay as-is so the customer can adjust quantities, or does anything change?

---

The most important open questions before implementation are the **product discount per-unit vs per-line** ambiguity (it changes the discount model significantly) and the **multi-shortage message format** (it shapes how the checkout return type is structured). Worth resolving those two first.