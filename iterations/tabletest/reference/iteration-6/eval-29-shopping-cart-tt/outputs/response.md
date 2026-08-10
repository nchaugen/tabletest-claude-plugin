Hand-authored reference answer for eval-29, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

Five `@TableTest` methods, one per cart operation, against the supplied `CartService` API. Cart,
catalogue, stock and coupon store are map columns converted by `@TypeConverter`; the coupon is a
single column in readable shorthand.

- `addsItemsAtCataloguePrices` — six rows: the three accepting shapes, plus unknown product, zero
  quantity and negative quantity.
- `removesItemsAlreadyInTheCart` — four rows, including removal from an empty cart.
- `keepsOneCouponActiveAtATime` — validity and replacement only, never discount size. `Active Coupon
  Before` / `Active Coupon After?` with blank meaning none active, so the two invalid-code rows prove
  the previous coupon survives.
- `totalsTheCartNetOfTheActiveCoupon` — the money, including the floor, in **one** table. The
  fixed-amount rows walk below / equal to / above the subtotal, and the empty-cart row carries a
  value set over all three coupon types so its "whatever the coupon" claim is discharged by rows.
- `verifiesStockBeforeCheckingOut` — success plus a `Message Mentions?` list checked by containment,
  because the requirement fixes what a failure must name and not how it reads.

Scenario names state the condition the row exercises, never the outcome: an operation's verb belongs
in the `@DisplayName`, and repeating it per row ("Rejects…", "Applies…") is the answer restated.

The cart service stays the supplied stub, per the prompt, so the tests compile and fail at run time.
