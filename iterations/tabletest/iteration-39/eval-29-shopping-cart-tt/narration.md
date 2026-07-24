# Narration — eval-29-shopping-cart-tt

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

Now let me check the build file and add the TableTest dependency, then write the test class.

**Edit ./build.gradle (edit)**

**Write ./src/test/java/com/example/cart/ShoppingCartTest.java (174 lines)**

Compiles cleanly. Now let's run the tests to confirm they execute (and fail only due to the stubbed `UnsupportedOperationException`, not compile/parsing errors).

All 35 rows parse, convert, and run correctly — every failure is the expected `UnsupportedOperationException` from the stubbed `ShoppingCart` class, confirming the tables and converters are wired correctly against the real API. No implementation has been added.

I've written TableTests for the shopping cart feature and verified they compile and run correctly (all 35 cases currently fail only with the stub's `UnsupportedOperationException`, confirming the harness is wired correctly against the real API).

**Files:**
- `build.gradle` — added `org.tabletest:tabletest-junit:1.2.1` test dependency
- `src/test/java/com/example/cart/ShoppingCartTest.java` — new test class, 5 `@TableTest` methods (one per concern):
  - `addsItemsFromTheCatalogue` — new item, stacking quantity on existing item, unknown product, zero/negative quantity
  - `removesItemsFromTheCart` — remove alone, remove among others, remove missing item, remove from empty cart
  - `appliesCouponsToTheCart` — apply with none active, replace across all 3 coupon types, expired/unknown codes leave the active coupon untouched
  - `checksOutTheCart` — empty cart, sufficient/exact/insufficient stock (single & multiple items), product missing from inventory entirely
  - `calculatesTheCartTotal` — no coupon, each coupon type, floor-at-zero boundary (exact and over), product coupon present/absent

Key assumptions documented via `@Description` on each table (since the rules didn't fully specify these):
- Adding an already-present product increases its quantity; quantity must be positive.
- Remove operates on the whole line item (no partial-quantity removal).
- Product-specific coupons subtract a flat amount from the total only when that product is in the cart.
- Checkout's message-content assertions use "must contain" substrings rather than exact wording, since the message format itself isn't specified.

No changes were made to `ShoppingCart.java` — it still throws `UnsupportedOperationException` everywhere, ready for implementation against this test suite.

