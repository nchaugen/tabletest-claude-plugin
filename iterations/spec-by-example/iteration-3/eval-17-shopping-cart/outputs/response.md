Here's a decomposed set of example tables covering the cart. I split concerns per table (add vs. remove, coupon acceptance vs. discount arithmetic, subtotal/total vs. checkout eligibility) rather than one giant end-to-end table, since each has its own boundaries and would otherwise mix rules.

## Assumptions (stated explicitly — no follow-ups possible)

1. **Remove takes a quantity**, symmetric to add. Removing more than the cart holds clamps to removing everything (reports the actual quantity removed), rather than being rejected outright.
2. **Coupon "Code Status" (Valid/Expired/Unknown) is treated as a resolved input** from the coupon system — the underlying expiry-timestamp math isn't modeled here since no time representation was given.
3. **Product-specific coupons are a percentage off that product's line subtotal** (equally could be a flat amount — same table shape, different `Coupon Value` semantics).
4. **Checkout success message** is assumed generic ("Order placed successfully"); shortage messages are a **collection**, one per under-stocked product, since multiple items can be short at once.
5. Add-to-cart never checks stock — stock is only verified at checkout, per the prompt.

---

### Adds an item to the cart

| Scenario | Cart Before | Product | Catalogue Price | Requested Quantity | Cart After? | Message? |
|---|---|---|---|---|---|---|
| New product into an empty cart | [] | Widget | $10 | 2 | [Widget: 2 @ $10] | Added 2x Widget to cart |
| New product into a non-empty cart | [Widget: 2 @ $10] | Gadget | $25 | 1 | [Widget: 2 @ $10, Gadget: 1 @ $25] | Added 1x Gadget to cart |
| Existing product accumulates quantity | [Widget: 2 @ $10] | Widget | $10 | 3 | [Widget: 5 @ $10] | Added 3x Widget to cart |
| Catalogue price changed since it was added | [Widget: 2 @ $10] | Widget | $12 | 1 | [Widget: 3 @ $12] | Added 1x Widget to cart |
| Product not in the catalogue | [] | Unknown SKU |  | 1 | [] | Product not found |
| Non-positive quantity requested | [Widget: 2 @ $10] | Widget | $10 | {0, -3} | [Widget: 2 @ $10] | Quantity must be greater than zero |

The 4th row is the important one for "prices come from the catalogue, not the customer" — it shows the line updates to the *current* catalogue price, not a stale or client-supplied one.

### Removes an item from the cart

| Scenario | Cart Before | Product | Requested Quantity To Remove | Cart After? | Message? |
|---|---|---|---|---|---|
| Partial removal leaves a remainder | [Widget: 5 @ $10] | Widget | 2 | [Widget: 3 @ $10] | Removed 2x Widget from cart |
| Removal empties the line exactly | [Widget: 3 @ $10] | Widget | 3 | [] | Removed 3x Widget from cart |
| Removal requested past what's in the cart | [Widget: 3 @ $10] | Widget | 4 | [] | Removed 3x Widget from cart |
| Product not in the cart | [Gadget: 1 @ $25] | Widget | 1 | [Gadget: 1 @ $25] | Widget is not in your cart |
| Non-positive quantity requested | [Widget: 3 @ $10] | Widget | {0, -1} | [Widget: 3 @ $10] | Quantity must be greater than zero |

Row 3 is the boundary just past "exact removal" — message reports what was *actually* removed (3), not the requested 4.

### Applies a coupon code to the cart

| Scenario | Active Coupon Before | Entered Code | Code Status | Active Coupon After? | Message? |
|---|---|---|---|---|---|
| First coupon applied to a cart with none active | none | SAVE10 (10% off cart) | Valid | SAVE10 | Coupon applied: 10% off |
| New valid code replaces the active one | FLAT15 ($15 off cart) | SAVE10 (10% off cart) | Valid | SAVE10 | Coupon applied: 10% off |
| Expired code is rejected | none | SUMMER22 | Expired | none | Coupon has expired |
| Unknown code is rejected | none | BOGUS1 | Unknown | none | Coupon code not found |
| Expired code doesn't disturb an existing active coupon | FLAT15 ($15 off cart) | SUMMER22 | Expired | FLAT15 ($15 off cart) | Coupon has expired |

Row 5 is the one easy to miss: rejection must not clear whatever coupon was already active.

### Computes the coupon discount amount

One table, `Coupon Type` as the family column — each type computes differently, but that's exactly what the rows are for.

| Scenario | Coupon Type | Coupon Value | Cart Subtotal | Target Product Line Subtotal | Discount Amount? |
|---|---|---|---|---|---|
| Percentage off the whole cart | Percentage | 10% | $200 |  | $20 |
| Fixed amount off, regardless of subtotal size | Fixed | $15 | {$20, $200} |  | $15 |
| Product discount, target product in cart | Product | 20% |  | $20 | $4 |
| Product discount, target product not in cart | Product | 20% |  |  | $0 |

Note: `Cart Subtotal` blank = not used by this coupon type. `Target Product Line Subtotal` blank on the last row = the product isn't currently in the cart (a genuinely different "absent" than the row above it). This table deliberately doesn't cap the discount against the subtotal — that's the total calculation's job, next.

### Computes the cart total

Combining table: subtotal is a traceability column, and the floor-at-zero interaction is the thing this table proves that no other table shows.

| Scenario | Cart Lines | Discount Amount | Subtotal? | Total? |
|---|---|---|---|---|
| Empty cart, no coupon | [] | $0 | $0 | $0 |
| Single line, no coupon | [Widget: 3 @ $10] | $0 | $30 | $30 |
| Multiple lines, no coupon | [Widget: 2 @ $10, Gadget: 1 @ $25] | $0 | $45 | $45 |
| Discount smaller than subtotal | [Widget: 2 @ $10, Gadget: 1 @ $25] | $20 | $45 | $25 |
| Discount exactly equals subtotal | [Widget: 3 @ $10] | $30 | $30 | $0 |
| Discount exceeds subtotal | [Widget: 3 @ $10] | $50 | $30 | $0 |

Rows 5 and 6 straddle the floor boundary — "massive coupon on a small order" lands here.

### Determines checkout eligibility

Rejection modeled as data (a message), with stock shortage as a collection since several items can be short at once.

| Scenario | Cart Contents | Available Stock | Checkout Succeeds? | Message? |
|---|---|---|---|---|
| Empty cart | [] | — | no | Your cart is empty |
| All items sufficiently stocked | [Widget: 2, Gadget: 1] | Widget: 5, Gadget: 3 | yes | Order placed successfully |
| Requested quantity exactly matches stock | [Widget: 5] | Widget: 5 | yes | Order placed successfully |
| Requested one more than available | [Widget: 5] | Widget: 4 | no | [Only 4 available for Widget] |
| Multiple items short at once | [Widget: 5, Gadget: 3] | Widget: 4, Gadget: 1 | no | [Only 4 available for Widget, Only 1 available for Gadget] |

Row 3 vs. row 4 is the boundary: exact stock match succeeds, one unit short fails.

---

## Open questions to confirm before implementation

- Does over-removal clamp (assumed) or should it be rejected with a "you only have 3" message instead?
- Is the product-specific coupon a percentage or a flat amount off that product?
- What's the exact success message text at checkout?
- Can a coupon be entered against an empty cart (assumed yes, takes effect once items are added)?

These are marked as `?` open decisions rather than silently baked into the assumptions above — worth a quick confirm from whoever owns the coupon/checkout copy before these become executable tests.