Five distinct concerns here, each worth its own table. Let me work through them and flag where I need your input to pin down the rules.

---

## Add Item to Cart

| Scenario | Cart before | Product | Quantity | Message? | Cart after? |
|---|---|---|---|---|---|
| Add to empty cart | [] | Widget | 2 | Added 2x Widget to cart | [Widget ×2] |
| Add a different item | [Widget ×2] | Gadget | 1 | Added 1x Gadget to cart | [Widget ×2, Gadget ×1] |
| Add more of an item already in cart | [Widget ×2] | Widget | 3 | Added 3x Widget to cart | [Widget ×5] |
| Add single unit | [] | Gadget | 1 | Added 1x Gadget to cart | [Gadget ×1] |

> **Open question:** Does re-adding an existing product accumulate (×2 + ×3 = ×5), or does it replace? The "accumulate" interpretation seems right from the description but worth confirming.

> **Open question:** What happens if someone adds quantity 0 or a negative number — validation error, or is that not a valid input at all?

---

## Remove Item from Cart

| Scenario | Cart before | Product to remove | Message? | Cart after? |
|---|---|---|---|---|
| Remove one of several items | [Widget ×2, Gadget ×1] | Widget | Removed Widget from cart | [Gadget ×1] |
| Remove the only item | [Widget ×2] | Widget | Removed Widget from cart | [] |
| Remove item not in cart | [Widget ×2] | Gadget | ? | [Widget ×2] |

> **Open question:** What's the message when removing something that isn't in the cart? Error ("Widget is not in your cart") or silent no-op?

> **Open question:** Does remove always clear the full quantity, or can customers remove a specific quantity (e.g. "remove 1 of the 3 Widgets")?

---

## Coupon Application

| Scenario | Current coupon | Code entered | Code status | Message? | Active coupon after? |
|---|---|---|---|---|---|
| Apply % coupon to empty slot | none | SAVE10 | valid, 10% off cart | Coupon SAVE10 applied | SAVE10 |
| Apply fixed-amount coupon | none | TENOFF | valid, £10 off cart | Coupon TENOFF applied | TENOFF |
| Apply product-specific coupon | none | WIDGET5 | valid, £5 off Widget | Coupon WIDGET5 applied | WIDGET5 |
| Replace an existing coupon | SAVE10 | SAVE20 | valid, 20% off cart | Coupon SAVE20 applied | SAVE20 |
| Expired code, no existing coupon | none | OLDCODE | expired | Coupon has expired | none |
| Non-existent code | none | BADCODE | not found | Invalid coupon code | none |
| Expired code, existing coupon active | SAVE10 | OLDCODE | expired | Coupon has expired | SAVE10 |
| Non-existent code, existing coupon active | SAVE10 | BADCODE | not found | Invalid coupon code | SAVE10 |

The last two rows capture an important rule: an invalid/expired entry should leave the existing coupon untouched. Worth confirming this is intentional.

---

## Cart Total

The interesting rules here are the coupon type behaviours and the zero floor — not the arithmetic itself.

| Scenario | Items | Coupon type | Coupon value | Total? |
|---|---|---|---|---|
| No coupon | Widget ×2 (£10 each), Gadget ×1 (£25) | none | — | £45.00 |
| Percentage off whole cart | Widget ×2 (£10 each), Gadget ×1 (£25) | % off cart | 10% | £40.50 |
| Fixed amount off whole cart | Widget ×2 (£10 each), Gadget ×1 (£25) | £ off cart | £10 | £35.00 |
| Product-specific discount | Widget ×2 (£10 each), Gadget ×1 (£25) | £ off Widget | £5 | £40.00 |
| Fixed coupon larger than order total | Widget ×1 (£10) | £ off cart | £50 | £0.00 |
| Percentage coupon, no items subtracted | Widget ×1 (£10) | % off cart | 0% | £10.00 |

> **Open question:** For the product-specific coupon, is the £5 off the total line (all units combined) or £5 per unit? "£5 off Widget" in row 4 above assumes it's the line total — Widget ×2 goes from £20 to £15. Does that match your intent?

> **Open question (boundary):** Does the 100% percentage case ever need handling, or is the zero-floor rule sufficient to cover it regardless of percentage value?

---

## Checkout

| Scenario | Cart | Stock available | Checkout allowed? | Message? |
|---|---|---|---|---|
| Empty cart | [] | — | no | Cannot check out with an empty cart |
| All items fully stocked | [Widget ×2, Gadget ×1] | Widget: 5, Gadget: 3 | yes | |
| Exact stock match | [Widget ×3] | Widget: 3 | yes | |
| One item one unit short | [Widget ×3] | Widget: 2 | no | Only 2 available (Widget) |
| One item completely out of stock | [Widget ×2] | Widget: 0 | no | Only 0 available (Widget) |
| Multiple items short | [Widget ×3, Gadget ×2] | Widget: 2, Gadget: 1 | no | ? |

> **Open question:** For the multiple-shortfalls case, does the message list all short items, or just the first one found? "Tell them what's short" suggests all — but does it? And if so, what's the format?

> **Open question:** What message does a successful checkout return? The spec says every operation returns a message, but doesn't give an example for the happy path here.

---

Once you answer the open questions I'll tighten up the tables and we can hand them to `/tabletest` to generate the executable `@TableTest` code.