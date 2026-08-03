# Narration — 29

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~131 thinking tokens (3 deltas)]_

> (thinking) I should use the TableTest library to write tests for this Java feature.

_[~83 thinking tokens (3 deltas)]_

> (thinking) I'll start by examining the existing project structure to understand how the API is currently stubbed out.

I'll start by exploring the existing cart domain types and stubbed service API.

_[~78 thinking tokens (4 deltas)]_

> (thinking) I need to examine the existing Java files to get a sense of how the API is structured.

_[~21,795 thinking tokens (469 deltas)]_

> (thinking) I'm seeing the full API structure now—the Cart holds items and coupon codes, CartResult wraps the outcome with a success flag and message, and CartService acts as a static facade over ShoppingCart. CheckoutResult is similar but without returning the cart itself, while Coupon is a record type with factory methods for different discount types like percentage-based, fixed amounts, and product-specific offers.
> 
> CouponStore uses a functional interface where the factory method checks an expired set first—if the code is there, it returns an expired Coupon regardless of what's in the available map. CouponType has three variants: PERCENT, FIXED, and PRODUCT. InventoryService and ProductCatalogue are also functional interfaces, with ProductCatalogue creating Products where the id and name both map to the productId. ShoppingCart is currently stubbed out.
> 
> For the build configuration, I need to add the TableTest dependency since JUnit is already at 6.0.3, which exceeds the 5.11+ requirement. Now I'm thinking through the test table design, starting with the add item scenario. When adding the same product again, quantities should accumulate rather than replace—that's standard shopping cart behavior. I need to clarify that the cart only stores product ID to quantity mappings, not prices; the catalogue parameter is used to validate the product exists and retrieve its name for the result message, while price calculations happen later in calculateTotal.
> 
> For the addItem test scenarios, I should cover successful additions of new items, increasing quantities of existing items, and rejecting unknown product IDs. I'm uncertain whether to test for invalid quantities like zero or negative values since the rules don't explicitly mention this, so I'll stick to what's specified rather than inventing constraints.
> 
> I'll structure this as a state transition table showing the cart before and after each operation, along with the product ID, quantity, and resulting message. The catalog will be used for validation and generating product names in messages, though since the stub factory creates names from IDs, the messages will reference products by their identifiers.
> 
> I'm considering whether to use simple product IDs like "widget" so the name matches the ID, or create a helper to map product IDs to more realistic display names like "Widget". Since the instructions allow adding small helper types, I could create a test catalog converter that transforms a map of product IDs to names, making the test messages more realistic without overcomplicating things. Generating test scenarios... Since the activeCouponCode isn't part of these test cases, I'll keep it constant across all rows (defaulting to null) and rely on Cart's equals method to compare both items and coupon code. I need a single type converter for Map<String,Integer> to Cart that uses Cart.withItems, which I can reuse consistently throughout the test table.
> 
> For tables that do need coupon variations, mixing integers and strings in the same map gets messy—I'd need a more complex structure like nested maps or lists to represent both items and coupon code in one cell, which adds unnecessary complexity when I can just apply coupons only where they're actually being tested. I'm wondering whether to split the cart representation across two separate columns—one for the items map and another for the coupon code—then combine them in a converter method. But that creates a tension: the skill requires domain objects to be built from table values via converters, not constructed in the test body itself, yet building from multiple columns seems to blur that boundary. So the question is whether TableTest supports nested maps as composite cells—like `[items: [widget: 2], coupon: SAVE10]`—that could be passed to a converter, since a single converter can't directly handle two separate table columns. The docs mention that converters can receive Map arguments, so this might be the way to assemble Cart from its two constituent fields without putting construction logic in the test method itself. Rather than trying to parse a heterogeneous map structure, I could define a `@TypeConverter` that reads from a `Map<String, String>` and constructs a `Cart` object directly, though that feels like a workaround. A cleaner approach would be to use separate input columns for the cart items (as `Map<String, Integer>`) and the coupon code (as a `String`), then combine them via a converter — but converters only take a single parameter. The real question is whether the "apply coupon" table actually needs to capture the cart's existing coupon state, and the answer is yes, because the rule that a new code replaces the old one means the initial cart must already have an active coupon to test that behavior.
> 
> So I'm settling on a single `@TypeConverter` that accepts a `Map<String, String>` where item quantities are stored as string values (like `"2"` for two widgets) and a reserved key like `"$coupon"` holds the active coupon code — something like `[widget: 2, $coupon: SAVE10]`. The converter iterates through the map entries and separates them accordingly. it's really a map key convention rather than a delimiter, which feels acceptable since it uses native map syntax. But I'm wondering if there's a simpler approach: split the "cart before" into two separate columns—one for items as a map and another for the active coupon code—then call a builder method directly in the test that chains the two together. The question is whether that counts as "construction in the test body" that the guidance warns against, or if using a builder method sidesteps that concern. The guidance suggests using a Map column with a converter when an object has several optional fields, and our Cart parameter fits this pattern perfectly — it has two fields where the coupon code is sparse (blank in most test rows, present in only a few). So I should collapse the Cart into a single map column with a converter that transforms the map data into the actual Cart object. Automatic recursive conversion gets tricky because the generic type information isn't specific enough for the converter to know how to handle nested maps as typed objects—it would just see Object values and either return raw representations or fail. Given this complexity and uncertainty, the cleaner approach is to represent the cart state with two separate columns: one for the items map and one for the coupon code, treating them as independent observable facts rather than trying to nest them into a single sparse object. Since the @TypeConverter constraint only accepts a single parameter, I'll construct the Cart inline in the test using a fluent builder pattern—chaining `withItems()` and `withActiveCouponCode()` to assemble the before-state from the two independent columns. This keeps the arrange step clean and focused on object construction rather than branching logic. Both the items and coupon come directly from table columns with no hidden logic, so it's similar to combining two separate inputs like a date and time. I could make the test method accept Cart as a parameter with a @TypeConverter, but that gets complicated with encoding the coupon in a map — it's simpler to just pass the two columns separately and let the method body combine them in one line.
> 
> I'll go with a straightforward approach: accept the items map and coupon code as separate test method parameters, then use a small helper method to construct the Cart object for both the arrange step and the expected result, keeping the pattern consistent throughout without over-engineering it.
> 
> For the CouponStore, I need to build a mapping of coupon codes to their details, including some expired ones, and represent the available coupons in the test data as a list of maps with code, type, and amount fields. Writing the CouponStore lambda... I'll keep the cart items constant across test cases since they don't affect coupon application logic, and focus the table columns on what actually matters: the active coupon code before the operation, the code being applied, and what the coupon lookup returns (whether the code exists, is valid, or expired). The Coupon Code column serves double duty—it's the input passed to CartService and potentially appears in error messages—while Coupon Definition determines what the store actually returns. I could simplify by encoding the coupon meaning directly into the code name itself using a naming convention, then mapping those patterns to Coupon objects, but that risks hiding important test data.
> 
> It's cleaner to keep them separate and explicit: the Coupon Code column stays arbitrary (like "SAVE10"), and a distinct "Coupon In Store" column describes the definition clearly—whether it's a percentage discount, fixed amount, product-specific, expired, or absent. This keeps the domain logic visible and readable without any hidden algorithm.
> 
> Now I'm defining a TypeConverter that takes a String and produces a nullable Coupon object...
> 
> Writing converter logic... The converter logic handles all four coupon formats through a switch-like pattern, which is fine for parsing. For the Coupon column values, I'm mapping "10% off" to percentage type, "$5 off" to fixed amount, and "widget: $3 off" to product-specific—the colon doesn't require quoting since it's not a problematic character within the cell. Now I need to design the actual Apply Coupon test table.
> 
> I'm thinking through the rule test cases: applying valid percentage, fixed-amount, and product-specific coupons to an empty cart; replacing an existing active coupon with a new valid one; and handling expired coupon codes where the cart's active coupon should remain unchanged and an error message appears.
> 
> I should also add a test case for nonexistent coupon codes that produces an error and leaves the active coupon unchanged. I'm structuring this as a state transition table with columns for the scenario, active coupon before, coupon code being applied, whether it exists in the store, the resulting active coupon, and the message returned. This covers the boundary between expired/invalid versus valid coupons, and distinguishes between replacing an existing coupon versus applying one for the first time. Since the behavior isn't implemented yet, I need to document my assumptions about the exact message strings that should be returned on success. Removed widget" - I'm now mapping out the removeItem operation with test scenarios covering both successful removal when an item exists and error cases when it's absent or the cart is empty.
> 
> I'm questioning whether removing an item from an empty cart and removing a non-existent item from a non-empty cart should be separate test rows or consolidated as a single obligation, since both result in the same error regardless of the cart's other contents. So I can't use a value set here since the expected cart state after the operation differs depending on what was in the cart before—one scenario leaves an empty cart empty, the other leaves a cart with other items unchanged. These are actually two distinct test cases worth keeping separate: one demonstrates the boundary condition of an empty cart, and the other shows that non-matching items don't get cleared. Both scenarios validate that the cart remains unchanged when the item isn't found, so they each deserve their own row. Generating test table... Generating test cases... The catalogue's role in addItem is really just to validate that the product exists—it doesn't actually get stored in the cart since Cart.items only tracks quantities, not prices. Price calculations happen elsewhere with calculateTotal, so I only need to confirm the catalogue lookup works and the product is found.
> 
> Now looking at the calculateTotal logic: it takes the cart, an already-resolved coupon, and the catalogue to compute total = sum(quantity × price) minus the coupon discount, floored at zero. The coupon classification and discount amount are already determined by the time calculateTotal is called, so I'm really just testing the arithmetic—summing up the line items and subtracting the discount. I'll reuse the same Coupon converter across both tables for consistency, and structure the test table with columns for Scenario, Cart Items, Catalogue Prices, Active Coupon, and Total—including a blank/null case to represent when no coupon is applied.
> 
> Now I'm mapping out the test rows to cover the key discount scenarios: no coupon applied, percentage-based discounts, fixed-amount deductions, edge cases where the discount exceeds the subtotal, and product-specific discounts that apply only to certain items in the cart. ount only applies when that product is in the cart; if it's absent, the coupon has no effect on the total. I'll add test cases showing both scenarios—one where the product-specific coupon applies because the target product is present, and another where it doesn't apply because the product is missing—to make this behavior explicit. I'm also planning to test a percentage coupon at the 100% boundary to verify the floor behavior works correctly.
> 
> Actually, testing over-100% percentages isn't realistic since coupons shouldn't exceed that in normal business rules, so I'll stick with demonstrating the floor through a fixed-amount discount that exceeds the subtotal instead—that's cleaner and shows the boundary clearly. I should also include test cases with multiple items in the cart to verify the subtotal calculation works across different products. So I'm settling on six test rows for the Final Total table: a multi-item baseline with no discount to verify the sum-across-products logic, then one row each for percentage coupons, fixed-amount coupons below and exceeding the subtotal (to catch the floor-at-zero boundary), product-specific coupons when the target product is present, and when it's absent. That covers all the coupon types and edge cases in a single focused concern.
> 
> For the Coupon column, I'll reuse the same string format as the apply-coupon table—"10% off", "$5 off", "widget: $3 off"—but I won't include "expired" since calculateTotal doesn't care about expiry; that's already handled upstream when the coupon object is passed in. A null value represents the no-coupon case.
> 
> Now I'm designing the Checkout table for CartService.checkout(Cart, InventoryService) -> CheckoutResult. The rules are: stock levels must be verified before checkout succeeds, insufficient inventory fails with details of what's short, and empty carts cannot checkout. I'm checking whether these are really two separate concerns or one—stock verification and empty-cart guard—by applying the strike test: removing the empty-cart row still leaves a valid rule about stock checking for nonempty carts, so they're distinct axes worth separate rows.
> 
> However, since both conditions gate the same operation and produce the same output shape (CheckoutResult with success/message fields), combining them into one Checkout table makes sense rather than splitting into two tables. They're both validation guards on the same operation, similar to how a single validation table handles multiple failure modes. rows about stock-sufficiency are different axes (cart having zero total items vs specific product shortage), so by "One Rule, One Axis" strictly they might be two separate concerns/tables. But the natural grouping here is around checkout validation as a whole, so I'm leaning toward keeping them together in one table and using the strike test to verify each row independently contributes a distinct rule.
> 
> Actually, let me apply the "and" test to the overall behavior: "Checkout succeeds when the cart is non-empty and stock is sufficient" — that "and" suggests I should split this into two separate tables. One table focuses on the cart-emptiness rule with stock held constant at a generous level, and another table focuses on stock-sufficiency with a non-empty cart held constant.
> 
> For the first table, I'm setting up scenarios where the cart varies between empty and non-empty while the inventory service always provides ample stock, so stock never becomes a limiting factor. For the second table, I'm varying the available stock levels while keeping the cart populated, isolating the stock-verification logic.
> 
> I'm identifying key boundaries: exact matches between requested and available quantities, and the case where available falls one unit short. These boundary pairs let me verify both the success path and the failure path with minimal test rows. I'll define the shortage message format as "Insufficient stock for [product]: need X, have Y" and document that assumption. For the InventoryService functional interface, I'm using a @TypeConverter to build it from a Map of product IDs to stock quantities, so the test can easily provide mock inventory data via a simple map.
> 
> For ProductCatalogue, I need a similar @TypeConverter that wraps the existing ProductCatalogue.from factory method, since JUnit's TableTest won't know how to construct the interface directly from a map of prices.
> 
> On the Cart side, I'm avoiding a dedicated @TypeConverter altogether — instead I'll pass the items map and coupon code as separate raw columns and use a private helper method to construct the Cart, which sidesteps the limitation of converters only accepting a single parameter.
> 
> For the apply-coupon table, I'll add an "Active Coupon Before" column to capture the existing coupon state, then build the Cart inline with both items and coupon code. When comparing expected results, I'll check just the items map directly rather than the whole Cart object to keep the assertions focused.
> 
> I don't need a Cart @TypeConverter at all — this approach is cleaner. Now for the Coupon @TypeConverter, I'm defining a consistent string format that handles percentage coupons like "10% off", fixed-amount coupons like "$5 off", and product-specific ones like "widget: $3 off", each mapping to the appropriate Coupon factory method.
> 
> For expired coupons, I'll represent them as a special marker string that creates a Coupon with the expired flag set, and blank cells will map to null through the built-in null handling. I don't need a separate converter for CouponStore since I can just inline a simple lambda in the test that wraps the resolved Coupon in an Optional.
> 
> For the success message in the apply-coupon table, I want to keep it simple and traceable—rather than including type-specific details like "10% off" or "$5 off", I'll just reference the coupon code itself, like "Applied coupon SAVE10". That way the message directly ties back to the Coupon Code column value and avoids exposing type-specific logic in the test.
> 
> For the activeCouponCode state, after success it should equal the newly applied code, and after failure it should remain whatever it was before (matching the "Active Coupon Before" column), which keeps everything traceable to concrete values.
> 
> Now I need to write out the description assumptions for each table since the messages are something I'm defining rather than given by the spec, and then plan out the checkout test split between empty-cart and insufficient-stock scenarios—since the requirement is "Checkout succeeds when the cart is non-empty AND stock is sufficient", that's two conditions joined by AND, so I'll need separate tables for each skill's explanation.
> 
> I'll create two focused tables: one that checks the non-empty cart requirement and another that verifies stock validation at checkout. Combined with the add-item, remove-item, apply-coupon, and total tables, that gives me six @TableTest methods covering the five API operations plus the checkout split—a solid, well-scoped set.
> 
> The "every operation returns a user-facing message" requirement is already handled structurally by including a Message column in each table, so that's satisfied across all scenarios including success cases.
> 
> For helper types, I already have CouponStore.of() provided, but for the apply-coupon table I can build custom per-row stores manually rather than relying on the static factory—it's simpler and works just as well. CheckoutResult doesn't need to include the cart state, and the Coupon record fields align perfectly with what the converter expects. JUnit's parameter conversion handles BigDecimal natively, so I can use it directly in test assertions for total values.
> 
> I need to be careful with BigDecimal comparisons though — scale matters when using assertEquals, so 9.99 and 9.990 would be treated as different values.  Instead, I'll use compareTo to check if they're equal regardless of scale, which is more reliable for these tests. The actual implementation will fail for now since it's not done yet, but at least the tests will compile. Now I'm setting up the build.gradle file...
> 
> For the test structure, I'm thinking one test class makes sense since everything goes through CartService as the public interface. I'll put it in src/test/java/com/example/cart/ShoppingCartTest.java with all six table-driven test methods, plus the necessary type converters and helper methods. The question is whether the tests should call CartService directly or test through a different layer...
> 
> I'll go with CartService since that's the public API described in the task. Now I need to write the converters: one to transform a map of prices into a ProductCatalogue, another to convert stock levels into an InventoryService, and a third to parse coupon strings into Coupon objects. Since Coupon appears in multiple column contexts but uses the same string format, I can use a single converter without conflicts.
> 
> I'm skipping the @Scenario annotation and DisplayName since they're not needed for assertion messages. Now I'll draft the test tables and use the format-table script to verify they parse correctly—the script will auto-align the pipes, so I can just run it normally to fix spacing and catch any parse errors.
> 
> Let me start mapping out the first table for addItem, which tests adding items to the cart using catalogue prices. I'm defining the columns: scenario description, cart state before, catalogue prices, product ID, quantity, expected cart state after, and any message returned. The first couple of rows cover adding a new item to an empty cart and increasing quantity for an existing item.
> 
> Continuing with the test cases...
> 
> I'm defining a test method that takes the cart state before, the product catalogue, the product ID and quantity to add, the expected cart state after, and the expected message. The method creates a cart from the initial items, calls the add item service, then asserts that the resulting cart and message match expectations.
> 
> Now I'm checking that the parameter order aligns with the test data columns—scenario name, items before, catalogue prices, product ID, quantity, items after, and message. The mapping looks correct. I need to make sure the catalogue parameter type matches what the converter expects, which should be ProductCatalogue with a Map<String, ...> structure.
> 
> Looking at the cell formats now—the widget price is formatted as a simple decimal value without quotes, and the empty cart items use the bracket syntax for an empty map. I should add a description documenting the assumptions about message formats: the "added item" message follows the pattern of quantity and product ID, unknown products show an error message, and the product display name matches its catalogue ID since there's no separate display name field.
> 
> Moving on to the next test table for removing items from the cart.
> 
> I'm writing a test method that removes an item and verifies both the resulting cart contents and the response message. The key question is whether the cart returned in error cases should be unchanged—and based on the design, CartResult always returns the cart (potentially unchanged), so error rows should assert that the items after match the items before.
> 
> Now I'm moving on to the coupon application tests. I need to define the columns for this scenario: the coupon code being applied, whether it exists in the store, what the active coupon is after the operation, and the resulting message. I'm reconsidering whether to include a cart items column—since the coupon logic doesn't depend on items and I'm holding them constant through the test fixture, I don't need it as a separate column. Coupon Code is the code being submitted, Coupon In Store represents whether that code exists in the store, and Active Coupon After is what gets set if the application succeeds. I'm laying out test cases covering percentage discounts, fixed amounts, and product-specific coupons, all starting from no active coupon and expecting the new code to be applied with an appropriate success message.
> 
> Then I'm testing replacement scenarios where an existing coupon gets swapped for a new one, rejection cases where expired or unknown codes are submitted but the previous coupon stays active with an error message, and I'm considering whether there's a distinct test case for rejecting an unknown code when nothing was active to begin with. Each coupon type (percentage, fixed, product-specific) needs its own row to prove that type works independently, and I'm keeping the replace scenario separate to isolate that obligation. The rejection cases cover both when there's an active coupon to preserve and when there's none, completing the test matrix at seven rows total. ") — that's a legitimate boundary case per the remove-item precedent.
> 
> Now I'm setting up the test method signature, which creates a cart with a fixed widget item and an optional active coupon, mocks the coupon store lookup, then calls the apply coupon service and asserts the resulting active coupon code and message match expectations.
> 
> For the parameters, blank cells map to null for both the coupon code and coupon object, which aligns with the "not found in store" scenario. The cart builder uses method chaining to set items and the initial coupon code in one fluent line.
> 
> Moving on to the next test table for the checkout validation — checking that the service requires a non-empty cart before allowing checkout.
> 
> Setting up two scenarios: an empty cart that should fail with a message about being empty, and a non-empty cart that proceeds successfully. I'm configuring the inventory service to return a very large stock value so inventory isn't a limiting factor here, since stock shortage behavior gets its own dedicated test table.
> 
> Writing the test method...
> 
> Now I'm moving on to the next table for stock verification at checkout, which will examine whether the system properly validates that sufficient inventory exists for each item in the cart before completing the transaction. Writing test method...
> 
> I'm setting up a converter to transform the available stock map into an InventoryService instance for the test table, then moving on to define the next test scenario for calculating cart totals with coupon discounts applied. Continuing with the remaining test cases where product-specific discounts apply only when the target item is in the cart, and then verifying the calculations for each scenario to ensure the logic is working correctly.
> 
> Writing test method... Writing TypeConverter methods... Finishing the amount parsing logic...
> 
> Now I'm checking whether the test cases need quoting in the table format. "widget: $3 off" contains a colon but doesn't start with brackets or braces, so it's safe as a whole-cell string without quotes. And I'm verifying that "10% off" also doesn't require quoting. Checking the fixed coupon parsing path - when there's no colon, it falls through to the fixed amount branch and correctly extracts the numeric value. The converter logic handles all our coupon text formats properly, and since this parsing lives in the @TypeConverter annotation, it's appropriately placed for this kind of data transformation.
> 
> For the stock shortage message, I'm relying on Cart's LinkedHashMap to preserve insertion order so the product names appear in the expected sequence when building the error message. The table parsing should maintain that order too, which is a reasonable assumption for these libraries. Now I'll write out the complete test file. Adding test imports and refining method names... I'm verifying that the Map equality checks will work correctly regardless of whether the implementation uses LinkedHashMap or immutable maps, since Map.equals compares entries structurally rather than by type. Empty map comparisons should also work fine. Let me draft the complete test file now, keeping in mind that Cart.items() returns the live internal map that gets mutated by putItem.
> 
> For the second item test case, the expected result has widget and gadget in a specific order, but since Map equality ignores insertion order, the test will pass regardless. I'll write out the file and then run the formatting script to align everything properly.
> 
> I'm also adding a private static helper method `parseAmount` that's used only within the `toCoupon` converter, placing it at the bottom with the other test helpers.
> 
> Now checking the `bigDecimal` total assertion — the parameter order in the method signature matches the table columns: Cart Items, Catalogue Prices, Active Coupon, and Total. Same verification for the checkout stock table where Cart Items maps to Map, Available Stock to InventoryService, Success to boolean, and Message to String. The empty-cart-checkout table columns align with their corresponding parameter types as well.
> 
> Time to finalize the test file and update the build.gradle with the tabletest dependency, then compile to verify everything works.

Now I have the full picture: `ShoppingCart` is stubbed (throws `UnsupportedOperationException`), and `CartService` is a thin facade. Let me add the TableTest dependency and write the tests.

**Edit ./build.gradle (edit)**

_[~67 thinking tokens (3 deltas)]_

> (thinking) Now I'll write out the test file to verify the implementation works correctly.

Now writing the test class, one `@TableTest` at a time.

**Write ./src/test/java/com/example/cart/ShoppingCartTest.java (171 lines)**

A formatter hook reformatted the file. Let me read it back to see the current state before doing anything else.

The formatter cleanly re-aligned all the tables. This is exactly what I intended. Now let's compile to verify everything type-checks.

_[~77 thinking tokens (4 deltas)]_

> (thinking) Checking if gradle is installed on the system globally since there's no local wrapper.

Gradle is installed globally, no wrapper needed.

Compiles cleanly. Now let's run the tests to confirm they execute (they're expected to fail/error, since the cart logic isn't implemented yet — this confirms the table data is being parsed and converters are working).

_[~201 thinking tokens (6 deltas)]_

> (thinking) All 27 test rows parsed successfully and hit the system under test with the expected UnsupportedOperationException, confirming the table structure and parameter bindings are solid. Now I'm running the format-table validation script and doing a final quality review of the table design.

All 27 rows across the 6 tables parsed, converted, and reached the stubbed `ShoppingCart` methods — every failure is `UnsupportedOperationException`, confirming the tables and converters are wired correctly (no conversion or parse errors). This is exactly the expected state before implementation.

All tables are well-formed and already aligned (matching the auto-format from the write hook).

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/cart/ShoppingCartTest.java` with 6 `@TableTest` methods (27 rows total) covering the five operations, no `ShoppingCart` logic touched:

- **`addsItemsPricedFromTheCatalogue`** — new item, quantity increment on existing item, adding alongside other items, unknown product rejection (price/name always sourced from `ProductCatalogue`, never the caller)
- **`removesItemsFromTheCart`** — removal from single/multi-item carts, absent-item error from empty and non-empty carts
- **`appliesACouponCodeToTheCart`** — all three coupon types applied fresh, replacing an active coupon, expired/unknown codes rejected without clearing the existing (or absent) active coupon
- **`requiresANonEmptyCartToCheckOut`** — empty-cart guard, split out as its own concern from stock checking (an "and" rule split into two tables)
- **`verifiesStockLevelsAtCheckout`** — sufficient/exact-boundary/one-short/multi-item-short stock, with aggregated shortage detail in the message
- **`calculatesTheCartTotalAfterAnyCoupon`** — subtotal sum, each coupon type, zero-floor boundary, and the product-specific-coupon-with-absent-target edge case

Since message wording and a couple of coupon-discount rules (e.g. product-specific coupon when its target isn't in the cart) aren't specified, I documented those as assumptions/open questions in each table's `@Description`. Small additions to make it compile: three `@TypeConverter` methods (`ProductCatalogue`, `InventoryService`, `Coupon` from domain-readable strings like `"10% off"`, `"$5 off"`, `"widget: $3 off"`, `"expired"`) — no production code was changed.

Ran `gradle test`: all 27 rows compile and execute, each failing with `UnsupportedOperationException` from the stub — confirming the tables, conversions, and parameter wiring are correct and ready to drive the implementation.

