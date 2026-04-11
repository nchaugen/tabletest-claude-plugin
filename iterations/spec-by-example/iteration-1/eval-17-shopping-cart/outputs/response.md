Here are example tables covering the distinct concerns in your cart. I've split them by behaviour — each table is one decision or rule.

---

## Add Item to Cart

| Scenario | Cart before | Product | Catalogue Price | Quantity | Cart after? | Message? |
|---|---|---|---|---|---|---|
| Add to empty cart | [] | Widget | £10.00 | 2 | [2× Widget @ £10.00] | Added 2× Widget to cart |
| Add new product to non-empty cart | [1× Widget] | Gadget | £5.00 | 1 | [1× Widget, 1× Gadget] | Added 1× Gadget to cart |
| Add already-in-cart product | [1× Widget] | Widget | £10.00 | 2 | ? | ? |
| Add quantity of zero | [] | Widget | £10.00 | 0 | ? | ? |

**Open questions:**
- What happens when you add a product already in the cart — does the quantity accumulate (becomes 3×) or replace (becomes 2×)?
- Is zero or negative quantity rejected? What's the message?

---

## Remove Item from Cart

| Scenario | Cart before | Product to remove | Cart after? | Message? |
|---|---|---|---|---|
| Remove the only item | [2× Widget] | Widget | [] | Removed Widget from cart |
| Remove one product, others remain | [2× Widget, 1× Gadget] | Widget | [1× Gadget] | Removed Widget from cart |
| Remove product not in cart | [1× Gadget] | Widget | [1× Gadget] | Widget is not in your cart |
| Remove from empty cart | [] | Widget | [] | Widget is not in your cart |

**Open question:** Does remove always remove the whole line, or can customers remove a specific quantity?

---

## Coupon Application

| Scenario | Current Coupon | Code Entered | Code Status | Coupon Type | Discount | Active Coupon after? | Message? |
|---|---|---|---|---|---|---|---|
| Apply percentage coupon, none active | none | SAVE10 | valid | % off cart | 10% | SAVE10 | Coupon SAVE10 applied: 10% off your order |
| Apply fixed-amount coupon, none active | none | FIVEOFF | valid | fixed off cart | £5.00 | FIVEOFF | Coupon FIVEOFF applied: £5.00 off your order |
| Apply product-specific coupon, none active | none | WIDGET5 | valid | off specific product | £5.00 off Widget | WIDGET5 | Coupon WIDGET5 applied: £5.00 off Widget |
| Replace existing coupon with new valid one | SAVE10 | SUMMER20 | valid | % off cart | 20% | SUMMER20 | Coupon SUMMER20 applied, replaced SAVE10 |
| Enter expired code, none active | none | OLDCODE | expired | {any} | {any} | none | Coupon has expired |
| Enter nonexistent code | none | FAKE123 | not found | — | — | none | Coupon code not found |
| Enter expired code when one already active | SAVE10 | OLDCODE | expired | {any} | {any} | SAVE10 | Coupon has expired |

---

## Cart Total Calculation

| Scenario | Items in cart | Coupon Type | Coupon Value | Target Product | Total? |
|---|---|---|---|---|---|
| No coupon | Widget ×2 @ £10.00 | none | — | — | £20.00 |
| Percentage off whole cart | Widget ×2 @ £10.00 | % off cart | 10% | — | £18.00 |
| Fixed amount off whole cart | Widget ×2 @ £10.00 | fixed off cart | £5.00 | — | £15.00 |
| Product-specific discount | Widget ×2 @ £10.00, Gadget ×1 @ £8.00 | off specific product | £5.00 | Widget | £23.00 |
| Multiple items, no coupon | Widget ×2 @ £10.00, Gadget ×3 @ £4.00 | none | — | — | £32.00 |
| Fixed discount exceeds cart total | Widget ×1 @ £5.00 | fixed off cart | £20.00 | — | £0.00 |
| Percentage discount, result rounds | Widget ×1 @ £9.99 | % off cart | 10% | — | ? |

**Open questions:**
- For a product-specific coupon on Widget ×2: does the £5.00 come off the Widget line total (£20 → £15) or off each Widget unit (£20 → £10)? The example above assumes line total.
- How does rounding work on percentage discounts?

---

## Checkout

| Scenario | Cart | Stock: Widget | Stock: Gadget | Success? | Message? |
|---|---|---|---|---|---|
| Empty cart | [] | — | — | no | Your cart is empty |
| All items in stock | [2× Widget, 1× Gadget] | 5 | 3 | yes | Order placed |
| Exact stock match | [2× Widget] | 2 | — | yes | Order placed |
| One item understocked | [2× Widget, 1× Gadget] | 1 | 3 | no | Only 1 Widget available (2 needed) |
| One item completely out of stock | [1× Widget] | 0 | — | no | Only 0 Widget available (1 needed) |
| Multiple items understocked | [2× Widget, 3× Gadget] | 1 | 2 | no | ? |

**Open question:** When multiple items are short, is there one combined message or a separate message per short item? The examples in your spec ("Only 3 available") suggest per-item messages — but the frontend may need to know how to display a list of failures.

---

These tables cover the main rules. The **open questions** are the most important things to resolve before implementation — particularly the add-duplicate behaviour and the product coupon scoping, since those affect the total calculation directly.