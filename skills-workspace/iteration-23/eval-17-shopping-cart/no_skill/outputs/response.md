Here's a thorough breakdown of test scenarios, organized by feature area.

---

## Adding items to cart

**Happy path**
- Add a new item → appears in cart with correct quantity
- Add the same item twice → quantities accumulate (not two separate line items)
- Add multiple distinct items → all appear independently

**Edge cases**
- Add quantity zero → should this be rejected? Define the behavior.
- Add a negative quantity → reject with error
- Add an item not in the catalogue → error message
- Add an item that has zero stock → should it be blocked at add-time, or only at checkout?
- Price is pulled from catalogue, not supplied by caller → verify the cart always uses catalogue price, even if caller passes one

---

## Removing items from cart

- Remove an item that exists → gone from cart
- Remove an item not in cart → error message (or silent no-op — define which)
- Remove one item when multiple exist → others unaffected
- Remove the last item → cart is now empty

---

## Coupon codes

**Percentage off**
- 10% off a $100 cart → $90
- 100% off → total is $0 (not negative)
- Applied before or after fixed discounts? Define the order if mixing isn't possible.

**Fixed amount off**
- $20 off a $100 cart → $80
- $20 off a $15 cart → total floors at $0, not -$5
- $0 coupon → probably invalid, define behavior

**Product-specific discount**
- Discount applies only to matching line item
- Discount on a product not in cart → does it apply ($0 effect), error, or silently wait?
- Discount exceeds the price of that product → that line item floors at $0, rest of cart unaffected

**Coupon replacement**
- Apply coupon A, then coupon B → only B is active
- Confirm the old coupon is fully gone (no partial stacking)

**Invalid / expired coupons**
- Code doesn't exist → error message, cart unchanged
- Code exists but is expired → error message, cart unchanged
- Previously valid coupon already applied, then it expires mid-session → define whether this is re-validated at checkout

**Coupon removal / clearing**
- Can a customer remove a coupon without replacing it? Define and test.

---

## Cart total calculation

- Empty cart → total is $0
- Single item: `quantity × price`
- Multiple items: sum of all line totals
- With percentage coupon applied
- With fixed coupon applied
- With product coupon applied
- Total never goes below $0 regardless of coupon type or size
- Floating point / rounding: e.g. 3 items at $0.10 each with 10% off — define rounding behavior

---

## Checkout

**Stock validation**
- All items in stock → checkout succeeds
- One item has insufficient stock → checkout fails, error names that item and shortfall (e.g. "Only 3 available")
- Multiple items have insufficient stock → error reports all of them, not just the first
- Item has exactly enough stock (quantity == available) → succeeds

**Empty cart**
- Attempt checkout with empty cart → rejected with message

**Coupon state at checkout**
- Expired coupon that was applied earlier — does checkout re-validate it?

---

## User-facing messages

Every operation should return a message. Verify the message content (or at least the shape) for:

| Operation | Expected message shape |
|---|---|
| Add item | "Added 2x Widget to cart" |
| Add duplicate item | "Updated Widget quantity to 5" (or similar) |
| Remove item | "Removed Widget from cart" |
| Apply valid coupon | "Coupon SAVE10 applied — 10% off" |
| Apply expired coupon | "Coupon has expired" |
| Apply unknown coupon | "Invalid coupon code" |
| Replace coupon | "Coupon updated to SAVE20" |
| Checkout success | "Order placed" (or similar) |
| Checkout — out of stock | "Only 3 available" (per item) |
| Checkout — empty cart | "Your cart is empty" |

---

## Cross-cutting concerns

- **Catalogue price changes mid-session**: if the price in the catalogue changes after an item is in the cart, what does the cart show? Define and test.
- **Order of operations**: add item → apply coupon → remove item → does coupon still apply correctly to the updated total?
- **Cart with coupon, then all items removed**: what is the total? What happens to the coupon?

---

The two areas most likely to have subtle bugs are the **total floor at zero** (test it for all three coupon types independently) and **multi-item stock failure at checkout** (make sure all failures are reported, not just the first). I'd prioritize those.