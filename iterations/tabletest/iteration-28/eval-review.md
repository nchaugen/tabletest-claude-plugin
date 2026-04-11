# Eval Review — Iteration 28

**Model:** sonnet · **Date:** 2026-04-06 · **Evals:** 2

## Summary

**with_skill:** 30/35 (85.7%) · 724249 tokens · 1750.4s · $2.5293

## Delta vs Iteration 27

No changes.

## Resource Comparison vs Iteration 27

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-29-shopping-cart-tt | 17/17 | — | 323952 | — | 853.1 | — |
| eval-30-order-splitting-tt | 13/18 | — | 400297 | — | 897.3 | — |

## Per-Eval Results

### ✅ Eval eval-29-shopping-cart-tt [with_skill]

**17/17** · 323952 tokens · 853127ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing item operations, coupon logic, total calculation, and checkout.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-item-coupon-total-checkout**: Item operations (add/remove), coupon application (validity and replacement), cart total calculation (coupon type effects on price), and checkout (stock verification) are in separate @TableTest methods.
- ✅ **rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script. Each row specifies its own preconditions (e.g. 'Cart with items' as an input column, not implied from a previous row).
- ✅ **coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **coupon-validity-not-coupon-types**: Coupon application table tests validity and replacement (valid code, expired code, nonexistent code, replacing active coupon) — not coupon type effects on price. Coupon type effects belong in the cart total table.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Message?', 'Total?', 'Active coupon after?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Add to empty cart', 'Replace active coupon with expired') — not outcomes ('Error', 'Success').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Product', 'Quantity', 'Message?', 'Cart total?') — not code identifiers like 'productId', 'result'.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario.
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.

### ⚠️ Eval eval-30-order-splitting-tt [with_skill]

**13/18** · 400297 tokens · 897303ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing all splitting rules.
  > Table 1 combines fulfillment type and address into one method: '@DisplayName("Group items by fulfillment type and delivery address")' tests both concerns together, not separately
- ❌ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > Table 1 has 7 rows mixing fulfillment and address. Rows like 'Same address stays together' test address logic only with DELIVERY type, creating unnecessary permutations from combining separate concerns
- ❌ **concern-fulfillment-method**: Fulfillment method splitting is represented as its own @TableTest — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
  > Fulfillment method is tested in Table 1 combined with address: 'shouldGroupItemsByFulfillmentTypeAndAddress'. Expected output specifies five separate concerns; model has four
- ❌ **concern-delivery-address**: Delivery address splitting is represented as its own @TableTest — items going to different addresses must be in separate shipments.
  > Address splitting is tested in Table 1 combined with fulfillment type: 'shouldGroupItemsByFulfillmentTypeAndAddress'. Expected output requires 'five concerns' as separate tables
- ✅ **concern-availability**: Availability splitting is represented as its own @TableTest — in-stock items ship immediately, backordered/pre-ordered items ship when available.
- ✅ **concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own @TableTest — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **concern-companion-products**: Companion product grouping is represented as its own @TableTest — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **all-outputs-same-table**: Each @TableTest method includes all output columns for its concern in the same table — e.g. warehouse allocation includes both the assignment and shipment count, not split across methods. All outputs of the same concern belong together.
- ❌ **scalar-quantity-for-warehouse**: Warehouse allocation table uses scalar quantity columns (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
  > Table 3 Items column uses '[P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]' format; expected output states 'Warehouse allocation uses scalar quantity'
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Shipments?', 'Groups?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **business-language-columns**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

