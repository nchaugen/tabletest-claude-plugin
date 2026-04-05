Looking at the description, I can see five distinct concerns worth specifying separately: adding items, removing items, applying coupons, calculating the cart total, and checkout validation. Let me work through each.

---

## Add Item to Cart

Each row is: given this cart, when this item is added, what is the new state and what message is shown?

| Scenario | Cart before | Item | Qty | Cart after? | Message? |
|---|---|---|---|---|---|
| First item added | [] | Widget | 2 | [Widget ×2] | Added 2× Widget to cart |
| Second distinct item | [Widget ×2] | Gadget | 1 | [Widget ×2, Gadget ×1] | Added 1× Gadget to cart |
| Same item added again — quantity accumulates | [Widget ×2] | Widget | 3 | [Widget ×5] | Added 3× Widget to cart |

**Open questions:**
- What happens if the item doesn't exist in the catalogue? (not listed above — should it silently fail, or is this prevented upstream?)
- Can customers add quantity 0? If so, what is the response?

---

## Remove Item from Cart

| Scenario | Cart before | Item removed | Cart after? | Message? |
|---|---|---|---|---|
| Remove one of several items | [Widget ×2, Gadget ×1] | Widget | [Gadget ×1] | Removed Widget from cart |
| Remove last item — cart becomes empty | [Widget ×2] | Widget | [] | Removed Widget from cart |
| Remove item not in cart | [Widget ×2] | Gadget | [Widget ×2] | Gadget is not in your cart |

---

## Coupon Application

The most complex behaviour — several types, validity states, and the replacement rule.

| Scenario | Coupon Code | Coupon Type | Coupon Status | Existing Coupon | Active Coupon After? | Message? |
|---|---|---|---|---|---|---|
| Percentage coupon on empty slate | SAVE10 | 10% off cart | valid | none | SAVE10 | Coupon applied: 10% off your order |
| Fixed amount coupon | FLAT5 | £5 off cart | valid | none | FLAT5 | Coupon applied: £5 off your order |
| Product-specific coupon | WIDGET20 | 20% off Widget | valid | none | WIDGET20 | Coupon applied: 20% off Widget |
| Expired coupon | OLDCODE | 10% off cart | expired | none | none | Coupon has expired |
| Unrecognised code | NOTREAL | — | not found | none | none | Coupon code not recognised |
| Valid coupon replaces existing | FLAT5 | £5 off cart | valid | SAVE10 | FLAT5 | Coupon applied: £5 off your order |
| Expired coupon entered when one already active | OLDCODE | 10% off cart | expired | SAVE10 | ??? | Coupon has expired |
| Unrecognised code entered when one already active | NOTREAL | — | not found | SAVE10 | ??? | Coupon code not recognised |

**Open questions:**
- Rows 7–8 are marked `???`: when an invalid/expired code is entered and a coupon is already active, does the old coupon **stay** or get **cleared**? This is a significant product decision — clearing it would be a surprising experience for the customer.
- If a product-specific coupon is applied and that product is later removed from the cart, does the coupon stay active (and do nothing), or does it get cleared automatically?
- What does the message say when a coupon *replaces* an existing one — does it mention the old one (e.g. "SAVE10 replaced")?

---

## Cart Total

The main rule here is the zero floor. The arithmetic itself (qty × price) is straightforward once prices are confirmed; these rows focus on the discount rules and the boundary.

| Scenario | Items | Catalogue Prices | Coupon | Total? |
|---|---|---|---|---|
| No coupon | Widget ×2, Gadget ×1 | Widget £10, Gadget £5 | none | £25 |
| Percentage off whole cart | Widget ×2 | Widget £10 | 10% off cart | £18 |
| Fixed amount off | Widget ×2 | Widget £10 | £5 off cart | £15 |
| Product-specific coupon — applies only to that product | Widget ×2, Gadget ×1 | Widget £10, Gadget £5 | 20% off Widget | £21 |
| Discount larger than order — floors at zero | Gadget ×1 | Gadget £5 | £20 off cart | £0 |
| Product coupon on discounted item, rest unaffected | Widget ×1, Gadget ×1 | Widget £10, Gadget £5 | 50% off Widget | £10 |

**Open question:**
- Does the product-specific coupon apply to the per-unit price, or the line subtotal? (They're equivalent, but worth confirming explicitly — "20% off Widget" at qty 2 should give £4 off, not £2.)

---

## Checkout Validation

Two failure modes: empty cart, and insufficient stock. Stock shortfalls are the interesting rule.

| Scenario | Cart | Stock Available | Checkout Result? | Message? |
|---|---|---|---|---|
| Successful checkout | Widget ×2, Gadget ×1 | Widget: 5, Gadget: 3 | success | Order placed successfully |
| Stock exactly meets demand | Widget ×3 | Widget: 3 | success | Order placed successfully |
| One item under-stocked | Widget ×2, Gadget ×1 | Widget: 1, Gadget: 3 | failure | Only 1 Widget available |
| One item completely out of stock | Widget ×2 | Widget: 0 | failure | Widget is out of stock |
| Multiple items under-stocked | Widget ×2, Gadget ×3 | Widget: 1, Gadget: 1 | failure | ??? |
| Empty cart | [] | — | failure | Your cart is empty |

**Open questions:**
- Row 5: when *multiple* items are short, do you get one combined message or one message per short item? This affects both the UI design and what the `Message?` column should look like — it may need to become a list.
- Is there a distinction in message between "zero stock" and "partial stock"? The examples above treat them differently ("out of stock" vs "only N available") — is that intentional?

---

## Summary of Key Open Questions

These are the decisions that most need a product answer before implementation:

1. **Invalid coupon with existing active coupon** — does the old coupon stay or clear?
2. **Checkout with multiple shortfalls** — one message or one per item?
3. **Product-specific coupon when that product is removed** — auto-clear or stays dormant?
4. **Item not found in catalogue at add time** — error or prevented upstream?

Once those are resolved, these tables are ready to hand off to `/tabletest` to generate the `@TableTest` code.