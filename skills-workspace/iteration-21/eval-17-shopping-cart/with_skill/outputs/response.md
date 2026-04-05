Good domain for example tables — there are several independent concerns here, and the coupon + total rules have enough combinations to benefit from making them concrete before coding starts.

I'll work through four tables: cart mutations, coupon application, total calculation, and checkout. Each covers one rule. Let me lay them out and flag the open questions I spotted in the spec.

---

## Cart Item Operations

| Scenario | Cart before | Action | Item | Quantity | Cart after? | Message? |
|---|---|---|---|---|---|---|
| Add item to empty cart | [] | add | Widget | 2 | [Widget ×2] | Added 2x Widget to cart |
| Add new item to non-empty cart | [Widget ×2] | add | Gadget | 1 | [Widget ×2, Gadget ×1] | Added 1x Gadget to cart |
| Add to existing item increases quantity | [Widget ×2] | add | Widget | 3 | [Widget ×5] | Added 3x Widget to cart |
| Remove item from cart | [Widget ×2, Gadget ×1] | remove | Widget | — | [Gadget ×1] | Removed Widget from cart |
| Remove the only item in cart | [Widget ×2] | remove | Widget | — | [] | Removed Widget from cart |
| Remove item not in cart | [Gadget ×1] | remove | Widget | — | [Gadget ×1] | Widget is not in your cart |
| Add item not in catalogue | [Widget ×2] | add | Flibbertigibbet | 1 | [Widget ×2] | ? | ← open |

**Open questions:**
- Does "remove" always remove the whole item, or can customers reduce quantity (e.g. remove 1 of 3)?
- What happens when adding an item that doesn't exist in the catalogue?

---

## Coupon Application

| Scenario | Coupon code | Coupon type | Discount | Applies to | Coupon status | Existing coupon | Coupon active after? | Message? |
|---|---|---|---|---|---|---|---|---|
| Apply percentage coupon, slot empty | SAVE10 | Percentage | 10% | Whole cart | Valid | none | SAVE10 | Coupon SAVE10 applied |
| Apply fixed-amount coupon, slot empty | FLAT5 | Fixed amount | £5.00 | Whole cart | Valid | none | FLAT5 | Coupon FLAT5 applied |
| Apply product-specific coupon, slot empty | WIDGET20 | Product | 20% | Widget | Valid | none | WIDGET20 | Coupon WIDGET20 applied |
| New coupon replaces existing one | NEWCODE | Percentage | 15% | Whole cart | Valid | SAVE10 | NEWCODE | Coupon NEWCODE applied, SAVE10 removed |
| Expired coupon, slot empty | OLDCODE | {Percentage, Fixed amount, Product} | any | any | Expired | none | none | Coupon has expired |
| Non-existent code | FAKE99 | — | — | — | Not found | none | none | Coupon code not recognised |
| Expired coupon, valid coupon already active | OLDCODE | Percentage | 10% | Whole cart | Expired | SAVE10 | ? | ? | ← open |

**Open questions:**
- When an invalid code is entered with a coupon already active, does the existing coupon stay? (Assume yes, but confirm.)
- What's the exact message wording for the replacement case — does it name the old coupon being removed?

---

## Cart Total Calculation

The interesting rule is the zero floor. These rows focus on discount types and the boundary — not on verifying that multiplication works.

| Scenario | Items | Coupon type | Discount | Total? |
|---|---|---|---|---|
| No coupon | Widget ×2 @ £10.00, Gadget ×1 @ £5.00 | none | — | £25.00 |
| Percentage off whole cart | Widget ×2 @ £10.00, Gadget ×1 @ £5.00 | Percentage | 10% | £22.50 |
| Fixed amount off | Widget ×2 @ £10.00, Gadget ×1 @ £5.00 | Fixed amount | £5.00 | £20.00 |
| Product-specific discount | Widget ×2 @ £10.00, Gadget ×1 @ £5.00 | Product | 20% off Widget | £21.00 |
| Fixed discount exactly equals total | Widget ×1 @ £5.00 | Fixed amount | £5.00 | £0.00 |
| Fixed discount exceeds total — floored at zero | Widget ×1 @ £5.00 | Fixed amount | £20.00 | £0.00 |
| Product coupon for item not in cart | Widget ×2 @ £10.00 | Product | 20% off Gadget | £20.00 | ← confirm |

**Open questions:**
- For product-specific coupons: is the discount a percentage off that line's total, a fixed amount off that line, or something else?
- If a product-specific coupon was applied for a product the customer later removed, what discount applies? (Covered by the last row above — confirm £0.00 discount is the intended behaviour.)

---

## Checkout Validation

| Scenario | Cart | Stock levels | Result? | Message? |
|---|---|---|---|---|
| All items in stock | Widget ×2, Gadget ×1 | Widget: 5, Gadget: 3 | Success | ? | ← open |
| Exactly enough stock | Widget ×3 | Widget: 3 | Success | ? | ← open |
| One item short | Widget ×2, Gadget ×1 | Widget: 1, Gadget: 3 | Fail | Only 1 Widget available |
| Item completely out of stock | Widget ×2 | Widget: 0 | Fail | Only 0 Widgets available |
| Multiple items short | Widget ×2, Gadget ×3 | Widget: 1, Gadget: 1 | Fail | Only 1 Widget available, only 1 Gadget available |
| Empty cart | [] | — | Fail | Your cart is empty |

**Open questions:**
- Is there a success message at checkout, or does the frontend handle that transition itself?
- When multiple items are short, is the error one combined message or a list? Does checkout surface all shortfalls at once, or just the first?

---

## Things to resolve before implementation

A few decisions that will shape the implementation significantly:

1. **Remove semantics** — whole-item removal vs. quantity adjustment. If you want quantity adjustment, the "remove" table needs a quantity column and rows like "reduce Widget from 3 to 1".
2. **Product coupon discount mechanism** — percentage off line total vs. fixed amount off. This changes both the total calculation and how the coupon is stored.
3. **Coupon persistence on invalid entry** — what's the behaviour when a bad code is entered and a valid one is already active.
4. **Checkout success message** — small thing, but the frontend team will need it.

Want to work through any of these open cells, or move any of these tables straight into `@TableTest` code?