# Narration — 17

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~325 thinking tokens (6 deltas)]_

> (thinking) The user is asking for help designing test scenarios for a shopping cart system, and this is a perfect use case for table-driven testing with concrete examples. Since the requirements involve multiple variables like coupons, inventory levels, and price calculations, mapping out the different combinations through example tables will help clarify the expected behavior.

This is exactly the kind of task the spec-by-example skill is built for — working through concrete examples before locking down test scenarios, especially with the ambiguous bits here (coupon interactions, stock shortfalls, discount floor at zero). Let me pull that in.

_[~8,558 thinking tokens (209 deltas)]_

> (thinking) I'm working through the decomposition methodology to identify the distinct business rules and their varying conditions. Looking at the add-to-cart operation, I need to separate the concerns: adding an item with quantity and price lookup, handling whether the item already exists in the cart, and noting that stock verification happens at checkout rather than during the add operation.
> 
> For the remove operation, I'm clarifying whether it deletes the entire line item or supports partial quantity reduction—the prompt suggests full removal, so I'll document that assumption. I'm also considering whether adding the same product twice merges quantities or replaces the entry, which likely merges.
> 
> Now examining the coupon logic as a state transition: the system supports three coupon types with only one active at a time, where applying a new code replaces the previous one and invalid or expired codes trigger errors.
> 
> For coupon validation, I'm thinking about whether to model rejected codes (expired or non-existent) separately or in the same table as successful applications. Since rejecting a code leaves the previous coupon unchanged, that's itself a rule that fits naturally in a before/after table structure—one table captures both accepted applications and rejections.
> 
> The cart total calculation is straightforward arithmetic: sum of quantity times price, minus the coupon discount, floored at zero. This is separate from the coupon classification logic and will need its own table structure.
> 
> For product-specific coupons, I need to handle the case where the discount only applies if that product is in the cart—if it's not there, the discount doesn't apply. I'll need to design that assumption into the table.
> 
> On checkout stock verification, there's the question of whether an empty cart check should be part of the same table as stock validation or separate. Since an empty cart is a precondition that prevents any stock checking from running, I could either model it as a special first row that rejects before stock checks, or keep it separate.
> 
> Actually, treating it as one row in the "Checkout Eligibility" table makes sense—empty cart gets rejected with its own message, and remaining rows handle stock shortfalls per item. This way, rejected rows can be struck and the remaining rows still form a valid stock check rule.
> 
> For price lookup, the key consideration is that the system should use the catalogue price regardless of what price might be submitted—this is more of a security/implementation detail test. The add-to-cart table's inputs would naturally be just Product and Quantity, with price looked up from the catalogue rather than accepted as input.
> 
> There's an interesting open question here: when the catalogue price changes after an item is added to the cart, does the cart use the snapshotted price or perform a live lookup at checkout? That's worth marking as an open question and potentially creating a separate "Price Integrity" test scenario table to explore.
> 
> I'm also thinking through the coupon edge case — if a coupon applies to a specific product and that product is later removed from the cart, does the coupon still apply or error out? Another open question to track.
> 
> Now I'm designing the Add Item to Cart table with columns for the scenario, cart state before, product, requested quantity, catalogue price, resulting cart state, and the system message. The key scenarios are: successfully adding a new item, adding the same item again to increase quantity, rejecting zero or negative quantities, rejecting products not in the catalogue, and implicitly resolving price from the catalogue since customers can't supply it. I'm realizing the catalogue price might belong in the Cart Total table as an intermediate output rather than in the Add table itself.
> 
> Generating markdown table... I need to handle edge cases for the add-to-cart function: rejecting zero or negative quantities with a "Quantity must be at least 1" message, treating both as the same validation failure, and also rejecting products not found in the catalogue with a "Product not found" message while leaving the cart unchanged.
> 
> For stock validation, the requirements specify that stock is only checked at checkout, so I'm noting that add-to-cart doesn't perform stock checks by design.
> 
> Now I'm mapping out the remove-from-cart scenarios: successfully removing an item that exists in the cart, handling attempts to remove items not present (whether the cart is empty or just doesn't contain that item), and collapsing these into a single error case since both produce the same "not in your cart" message. I can collapse the "remove absent item" cases into a single value set since both the empty cart and nonempty cart scenarios produce the same error outcome. That gives me three clean test cases: normal removal, removal that empties the cart (the boundary condition), and attempting to remove an item that isn't there. I'm also noting that the requirements don't specify partial quantity decrements, so I'll assume whole-line removal only and flag that as an assumption if they need something different. Now I'm moving into the coupon code logic, which involves both state transitions and validation rules. or if any remains active), message "Coupon has expired". For nonexistent codes, the cart stays unchanged and shows "Coupon code not found". The discount type itself doesn't affect the apply logic — whether it's percentage, fixed amount, or product-specific is irrelevant to accepting or rejecting the coupon, so type becomes a value set that keeps the application table separate from the discount calculation concerns.
> 
> Now I'm mapping out the scenarios for applying a coupon code to the cart, including cases where no coupon is active, replacing an existing one, and handling expired or invalid codes. I'm deciding whether to split the invalid code cases into separate rows or combine them. The key insight is that testing an invalid code against an existing coupon is the important boundary case—it shows whether a failed attempt accidentally clears the previous coupon. I'll keep them as distinct rows: one for nonexistent code with no active coupon, and another with an existing coupon active, since they demonstrate different persistence scenarios. For expired versus not-found codes with an existing coupon, the outcomes differ only in the message, so those might collapse into a single row.
> 
> Actually, the messages are different enough that they need separate rows—the test expectations would differ. So I'm settling on six rows total: valid code with no coupon, valid code replacing an active coupon, expired code with no coupon, expired code with existing coupon, nonexistent code with no coupon, and nonexistent code with existing coupon. This covers the replacement scenario and thoroughly tests boundary conditions around invalid attempts against different coupon states.
> 
> Now I'm moving to the discount calculation table, which handles a different concern—how the coupon type (percentage, fixed amount, or product-specific) determines the actual discount value applied to the cart. This is separate from the application logic, so it needs its own table with columns for coupon type, the relevant parameters, cart contents, and the resulting discount amount.
> 
> I'm working through a percentage discount example to clarify the mechanics: a 10% coupon on a cart with specific items. There's a rounding question that comes up—when calculating 10% of 19.99, I get 1.999, which needs to round to 2.00. I'm noting this as an assumption and considering whether to use cleaner numbers in the example to avoid rounding ambiguity altogether. I'm clarifying the separation of concerns: Table 4 should output the raw discount amount the coupon implies, without capping it to the subtotal, while Table 5 handles the floor-to-zero logic when subtracting from the cart total. This keeps the discount calculation independent of cart size for fixed and percentage discounts, with the boundary case handled downstream. Now looking at product-specific discounts, where the discount applies only if the cart contains the targeted product. I'll skip testing percentages over 100% since that's not specified in the requirements — I'll just note it as an open question instead. Now I'm moving to Table 5 to test the cart total calculation, which takes the subtotal and discount amount and floors the result at zero. I'm setting up scenarios like a normal discount bringing the total down, a discount that exactly matches the subtotal, and a discount that exceeds it to verify the floor behavior works correctly.
> 
> For Table 6, I'm planning to test checkout eligibility by checking whether items in the cart have sufficient stock available, and I need to capture what happens when there's a shortfall. When one item runs short, I show the specific shortfall in the message. For multiple items with insufficient stock, I list all the shortfalls together. Then I'm testing boundary cases: exactly matching stock should succeed, while requesting one more than available should fail with the appropriate shortfall message.
> 
> Now I'm settling on the table structure with columns for Scenario, Cart Contents, Stock Levels, Checkout Result, Shortfalls, and Message—where Shortfalls captures the structured data and Message provides the human-readable text. I'm also clarifying a couple of assumptions: the remove operation takes out entire line items rather than decrementing quantities, and adding the same product again merges with the existing cart entry.
> 
> There are still some open questions I'm flagging: the exact rounding behavior for percentage discounts, whether a product-specific coupon should warn if the target product isn't in the cart or just apply zero discount, whether cart prices are looked up live at checkout or snapshotted when items are added, and whether negative or over-100% coupon values are even in scope—I'm assuming those are validated upstream and not the cart's concern.
> 
> I'm also noting that coupon expiry could be checked only at apply-time or re-checked at checkout if time passes between the two, and stock race conditions from concurrent orders are outside the scope of unit tests. For the message requirement, I've included Message columns in the user-facing operation tables, but the internal calculation tables don't need them since they're supporting rules rather than direct user operations—though I'm still considering whether displaying the cart total counts as an operation that needs its own message.
> 
> Now I'm refining the table titles to use consistent action-verb phrasing: "Adds an Item to the Cart," "Removes an Item from the Cart," "Applies a Coupon Code to the Cart," "Calculates the Coupon Discount Amount," "Computes the Cart Total," and "Determines Checkout Eligibility." This gives them a uniform grammatical structure that reads like an index.
> 
> Generating markdown tables... ty=1 | Cart After=[Widget: 2 @ 5.00, Gadget: 1 @ 9.99] | Message="Added 1x Gadget to cart"
> 4. "Zero or negative quantity is rejected" | Cart Before=[Widget: 2 @ 5.00] | Product=Gadget | Catalogue Price=9.99 | Qty={0, -1} | Cart After=[Widget: 2 @ 5.00] (unchanged) | Message="Quantity must be at least 1"
> 5. "Product not found in cat
> 
> alogue" | Cart Before=[:] | Product=Unicorn | Qty=1 | Cart After=[:] (unchanged) | Message="Product not found"
> 
> The catalogue price is blank here because the product doesn't exist, which demonstrates the rule about blank meaning absent. I'm also noting that the system resolves prices from the catalogue itself rather than accepting them as input parameters, which clarifies the apparatus for this operation. Now I'm moving on to the second table that covers removing items from the cart, with columns for the scenario, initial state, product being removed, resulting state, and any messages returned.
> 
> Generating test scenarios... Filling in the coupon scenarios with different validity states and coupon types — valid codes that replace existing coupons, expired codes that get rejected, and cases where an expired code leaves an active coupon untouched. I'm reconsidering the Type column approach—instead of leaving it blank for invalid codes, I should populate it with the full set of possible types to show that the rejection reason doesn't depend on which type the coupon is. This makes the test cases more rigorous and demonstrates type-independence. Now I'm moving on to the next table that calculates the actual discount amount based on coupon type and parameters. Generating test cases... Generating test scenarios... Generating test case table... The boundary cases look solid—the zero-floor edge is covered from multiple angles, and the near-boundary values show the contrast between floored and non-floored behavior. The tables properly separate arithmetic from classification logic, use rejection as data through Result and Message columns, and stick to domain terminology throughout. I think this is ready to go, so I should add an explicit assumptions section as the instructions suggest rather than leaving open questions.
> 
> On browsing the product catalogue—that's mentioned in the prompt but there's no real business logic to test there since it's just straightforward listing and retrieval with no branching rules, so it makes sense to note that's been omitted. I should also consider whether removing an item that a product-specific coupon targets is worth testing as a scenario, since the discount would naturally drop to zero in that case.

Here are six example tables covering the cart's distinct rules, each isolating one concern per the "one rule, one axis" principle. I skipped catalogue *browsing* itself — no branching logic was described for it, just retrieval.

## Assumptions (stated since this is non-interactive)

- **Remove** deletes a line item entirely; there's no separate "decrease quantity" operation (not mentioned in the brief).
- **Adding an already-present product** merges into the existing line (adds to quantity) rather than replacing it; the success message reports the quantity just added, not the new total.
- **Add-to-cart does not check stock** — per the brief, stock is only verified at checkout.
- A **product-specific coupon whose target isn't in the cart** silently contributes $0 discount rather than erroring (flagged as open below — could reasonably be a rejection instead).
- **Percentage discounts round to 2 decimal places**; rounding *mode* (half-up vs. banker's) is left open.
- Money in cells is `major.minor` (e.g. `5.00`); cart/stock contents use `[Product: qty]` or `[Product: qty @ price]`.

---

### Table 1 — Adds an Item to the Cart

| Scenario | Cart Before | Product | Catalogue Price | Requested Quantity | Cart After? | Message? |
|---|---|---|---|---|---|---|
| First item added to an empty cart | [:] | Widget | 5.00 | 2 | [Widget: 2 @ 5.00] | Added 2x Widget to cart |
| Adding more of an item already in the cart | [Widget: 2 @ 5.00] | Widget | 5.00 | 3 | [Widget: 5 @ 5.00] | Added 3x Widget to cart |
| Adding a second, different product | [Widget: 2 @ 5.00] | Gadget | 9.99 | 1 | [Widget: 2 @ 5.00, Gadget: 1 @ 9.99] | Added 1x Gadget to cart |
| Zero or negative quantity is rejected | [Widget: 2 @ 5.00] | Gadget | 9.99 | {0, -1} | [Widget: 2 @ 5.00] (unchanged) | Quantity must be at least 1 |
| Product not found in catalogue | [:] | Unicorn |  | 1 | [:] (unchanged) | Product not found |

*Catalogue Price is always resolved by the system from the catalogue — the operation takes no price input from the caller, so a spoofed price can't reach the cart at all.*

### Table 2 — Removes an Item from the Cart

| Scenario | Cart Before | Product | Cart After? | Message? |
|---|---|---|---|---|
| Removing one of several items | [Widget: 2 @ 5.00, Gadget: 1 @ 9.99] | Widget | [Gadget: 1 @ 9.99] | Removed Widget from cart |
| Removing the last item empties the cart | [Widget: 2 @ 5.00] | Widget | [:] | Removed Widget from cart |
| Removing a product that isn't in the cart | {[:], [Gadget: 1 @ 9.99]} | Widget | same as Before | Widget is not in your cart |

### Table 3 — Applies a Coupon Code to the Cart

| Scenario | Active Coupon Before | Entered Code | Code Validity | Coupon Type | Active Coupon After? | Message? |
|---|---|---|---|---|---|---|
| Applying a valid code when no coupon is active | (none) | SAVE10 | valid | {Percentage, Fixed Amount, Product Discount} | SAVE10 | Coupon SAVE10 applied |
| A new valid code replaces the active coupon | WELCOME5 | SAVE10 | valid | {Percentage, Fixed Amount, Product Discount} | SAVE10 | Coupon SAVE10 applied |
| An expired code is rejected, no coupon was active | (none) | OLDCODE | expired | {Percentage, Fixed Amount, Product Discount} | (none) | Coupon has expired |
| An expired code does not disturb the active coupon | WELCOME5 | OLDCODE | expired | {Percentage, Fixed Amount, Product Discount} | WELCOME5 | Coupon has expired |
| An unknown code is rejected, no coupon was active | (none) | BOGUS | not found | {Percentage, Fixed Amount, Product Discount} | (none) | Coupon code not found |
| An unknown code does not disturb the active coupon | WELCOME5 | BOGUS | not found | {Percentage, Fixed Amount, Product Discount} | WELCOME5 | Coupon code not found |

*Rows 3–6 use a value set for Coupon Type to state explicitly: rejection doesn't depend on what kind of coupon it would have been.*

### Table 4 — Calculates the Coupon Discount Amount

| Scenario | Coupon Type | Coupon Parameters | Cart Contents | Discount Amount? |
|---|---|---|---|---|
| No coupon applied | None |  | [Widget: 2 @ 5.00] | 0.00 |
| Percentage off the whole cart | Percentage | [percent: 10%] | [Widget: 2 @ 5.00] (subtotal 10.00) | 1.00 |
| Fixed amount off the whole cart | Fixed Amount | [amount: 5.00] | [Widget: 2 @ 5.00] (subtotal 10.00) | 5.00 |
| Product discount when the target is in the cart | Product Discount | [product: Widget, amount: 3.00] | [Widget: 2 @ 5.00, Gadget: 1 @ 9.99] | 3.00 |
| Product discount when the target is not in the cart | Product Discount | [product: Widget, amount: 3.00] | [Gadget: 1 @ 9.99] | 0.00 |

*Discount here is not yet capped against the subtotal — that's Table 5's job, kept separate so the classification and the arithmetic aren't fused.*

### Table 5 — Computes the Cart Total

| Scenario | Subtotal | Discount Amount | Total? |
|---|---|---|---|
| Discount smaller than the subtotal | 19.99 | 1.00 | 18.99 |
| No discount applies | 10.00 | 0.00 | 10.00 |
| Discount exactly equal to the subtotal | 10.00 | 10.00 | 0.00 |
| Discount one cent less than the subtotal | 10.00 | 9.99 | 0.01 |
| Discount larger than the subtotal is floored at zero | 10.00 | 15.00 | 0.00 |

### Table 6 — Determines Checkout Eligibility

| Scenario | Cart Contents | Stock Levels | Checkout Result? | Shortfalls? | Message? |
|---|---|---|---|---|---|
| An empty cart cannot check out | [:] | [:] | Rejected | [:] | Your cart is empty |
| Every item has enough stock | [Widget: 2, Gadget: 1] | [Widget: 5, Gadget: 10] | Accepted | [:] | Order placed |
| Requested quantity exactly matches stock | [Widget: 3] | [Widget: 3] | Accepted | [:] | Order placed |
| Requested quantity is one more than available | [Widget: 4] | [Widget: 3] | Rejected | [Widget: 3 available] | Only 3 available for Widget |
| One of several items is short | [Widget: 5, Gadget: 1] | [Widget: 3, Gadget: 10] | Rejected | [Widget: 3 available] | Only 3 available for Widget |
| Multiple items are short at once | [Widget: 5, Gadget: 5] | [Widget: 3, Gadget: 2] | Rejected | [Widget: 3 available, Gadget: 2 available] | Only 3 available for Widget, only 2 available for Gadget |

---

## Open questions (marked, not guessed away)

1. Should a product-discount coupon whose target isn't in the cart be a silent no-op ($0 discount, as assumed) or should applying it be rejected/warned up front?
2. Is catalogue price snapshotted onto the cart line at add-time, or re-looked-up live at checkout if the catalogue price changes in between? This affects whether a Table 1-style "price changed after add" scenario is even meaningful.
3. Rounding mode for percentage discounts (half-up, half-even, truncate)?
4. Is coupon expiry checked only at apply-time, or re-validated again at checkout if time has passed since it was applied?
5. Any upper bound on percentage value (e.g., can a coupon be >100% off), or is that validated elsewhere and out of the cart's concern?

These six tables are ready to hand to `/tabletest` once you're coding — each column set maps directly to `@TableTest` parameters.

