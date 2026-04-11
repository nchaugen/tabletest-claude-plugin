# Eval Review — Iteration 20

**Model:** sonnet · **Date:** 2026-03-31 · **Evals:** 3

## Summary

**with_skill:** 39/48 (81.3%) · 262233 tokens · 440.2s · $0.8164

## Delta vs Iteration 19

**Regressions (3):**
- ❌ eval-14-weekly-pay: `1.1-traceability-columns`
- ❌ eval-14-weekly-pay: `1.2-error-has-expected-column`
- ❌ eval-14-weekly-pay: `1.14-depth-zero-rate`

## Resource Comparison vs Iteration 19

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-14-weekly-pay | 10/15 | 13/15 | 120557 | 279218 | 232.8 | 286.6 |
| eval-16-order-splitting | 16/17 | — | 72714 | — | 133.0 | — |
| eval-17-shopping-cart | 13/16 | — | 68962 | — | 74.3 | — |

## Per-Eval Results

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**10/15** · 120557 tokens · 232837ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > All three pay calculation tables only show a single 'Pay?' column. The weekdayPayWithOvertimeThreshold table shows only 'Scenario | Weekday hrs | Rate | Pay?', premiumHoursPay shows 'Scenario | Sunday hrs | Holiday hrs | Rate | Pay?', and totalWeeklyPay shows 'Scenario | Weekday hrs | Sunday hrs | Holiday hrs | Rate | Pay?' — no intermediate columns like Regular pay?, Overtime pay?, or Premium pay?.
- ❌ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
  > The negativeRateIsRejected table contains only 'Scenario | Rate' columns, with no exception type column. The exception type is hardcoded in the assertion: 'assertThrows(IllegalArgumentException.class, () -> WeeklyPayCalculator.calculate(0, 0, 0, rate));'
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > The negativeRateIsRejected table tests negative rates (-0.01 and -50.00), but there is no test case with negative hour values. All hour parameters are typed as 'int' (not 'Integer'), and all test scenarios use non-negative hour values.
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > In the premiumHoursPay table, 'Sunday hours only | 5 | 0 | 20.00 | 200.00' shows '0' for Holiday hrs instead of an empty cell. The method signature shows 'int sundayHours, int holidayHours' instead of 'Integer' types, preventing null values from blank cells.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
  > No test row has rate = 0.00. The rates appearing across all tables are: 20.00, 15.00, 10.00, 25.00 (positive rates) and -0.01, -50.00 (negative rates in the rejection test), but zero rate is not tested.
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.

### ⚠️ Eval eval-16-order-splitting [with_skill]

**16/17** · 72714 tokens · 133008ms

- ✅ **3.1a-concern-fulfillment-method**: Fulfillment method splitting is represented as its own table — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
- ✅ **3.1b-concern-delivery-address**: Delivery address splitting is represented as its own table — items going to different addresses must be in separate shipments.
- ✅ **3.1c-concern-availability**: Availability splitting is represented as its own table — in-stock items ship immediately, backordered/pre-ordered items ship when available.
- ✅ **3.1d-concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own table — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **3.1e-concern-companion-products**: Companion product grouping is represented as its own table — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **3.2-depth-fulfillment-scenarios**: Fulfillment table covers at least: all items for home delivery, all items for store pickup, and a mix of delivery and pickup items.
- ✅ **3.3-depth-delivery-address-scenarios**: Delivery address table covers at least: all items to the same address, and items to different addresses.
- ✅ **3.4-depth-availability-scenarios**: Availability table covers at least: all items in stock, a mix of in-stock and pre-order/backorder, and two backordered/pre-ordered items with different dates. Pre-order and backorder are recognised as equivalent for splitting semantics — either by comment, note, or by including both with the same rules.
- ✅ **3.5-depth-warehouse-scenarios**: Warehouse allocation table covers at least: single warehouse has full quantity, multi-warehouse split needed, fewer-warehouse split preferred over more warehouses, and cannot fulfill (insufficient total stock).
- ❌ **3.6-depth-companion-scenarios**: Companion table covers at least: single warehouse has both companions, unavoidable split (no warehouse has both but both are available), and fulfillment not possible (one companion not available anywhere).
  > Table 5 covers the first two required scenario types: 'One warehouse has both companions' (NYC: {EOS R5: 1, RF 50mm: 1}) and 'No warehouse stocks both companions' (NYC: {EOS R5: 1}, Chicago: {RF 50mm: 1}, both items available). However, no scenario covers fulfillment not possible where one companion is completely unavailable anywhere—all scenarios show both companion items available somewhere in the warehouse system
- ✅ **3.7-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **3.8-readability-item-property-mapping**: Trigger tables represent multi-item orders where each item has its own fulfillment type, address, and availability. The notation makes the item-to-property mapping unambiguous — not value sets like {delivery, pickup} which lose which item has which property.
- ✅ **3.9-readability-scalar-column**: Warehouse allocation table uses a scalar count column (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.10-readability-business-language**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **3.11-readability-output-column-question-mark**: Output/expected columns end with '?' (e.g. 'Shipments?', 'Groups?') to distinguish them from input columns.
- ✅ **3.12-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ✅ **3.13-depth-open-questions**: Multiple open questions are surfaced across the tables — ambiguities, edge cases, or product decisions that the requirements don't fully specify.

### ⚠️ Eval eval-17-shopping-cart [with_skill]

**13/16** · 68962 tokens · 74322ms

- ✅ **4.1a-concern-item-operations**: Item operations (add/remove) are represented as their own table or tables — separate from coupon and cart total logic.
- ✅ **4.1b-concern-coupon-application**: Coupon application (entering, replacing, rejecting codes) is represented as its own table — separate from item operations and cart total calculation.
- ✅ **4.1c-concern-cart-total**: Cart total calculation (how different coupon types affect the price) is represented as its own table — separate from coupon application and item operations.
- ✅ **4.1d-concern-checkout**: Checkout (stock verification) is represented as its own table — separate from the other concerns.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ❌ **4.4-depth-item-operations**: Item operations cover at least: add to empty cart, add to non-empty cart, add more of an item already in cart, remove item from cart, remove last item in cart, remove item not in cart.
  > Cart Item Operations table covers: 'Add item to empty cart', 'Add different item' (non-empty), 'Add more of same item', 'Remove item from cart' (removes Widget from [Widget x2, Gadget x1] leaving [Gadget x1]), and 'Remove item not in cart'. Missing explicit scenario for 'remove last item in cart' (removing the only item, resulting in an empty cart).
- ❌ **4.5-depth-coupon-application**: Coupon application table covers validity and replacement — not coupon types. Scenarios include at least: add valid coupon (none active), add valid coupon (another active), add expired coupon (none active), add expired coupon (another active), add nonexistent code (none active), add nonexistent code (another active).
  > Table covers: valid coupon with none active (3 rows), valid coupon replacing existing ('New valid coupon replaces existing'), expired coupon with none active, and nonexistent code with none active. Missing: 'add expired coupon (another active)' — the Expired coupon row shows 'Existing Coupon' = 'none', not another active coupon. Missing: 'add nonexistent code (another active)' — the Unknown coupon code row shows 'Existing Coupon' = 'none', not another active coupon.
- ✅ **4.6-depth-cart-total-scenarios**: Cart total table covers how different coupon types affect the price. Scenarios include at least: no coupon, percentage off whole cart, fixed amount off, fixed amount exceeding cart total (floors at zero), product-specific discount (product in cart), product-specific discount (product not in cart).
- ✅ **4.7-depth-checkout-scenarios**: Checkout table covers at least: empty cart, all items in stock, and not all items in stock (insufficient inventory).
- ✅ **4.8-readability-business-language**: Column names use business/user-perspective language (e.g. 'Message?' not 'Result?', 'Coupon code' not 'input'). The tables read as a specification a product person could review.
- ✅ **4.9-readability-test-data-visible**: Test data that affects the outcome — product prices, coupon rules (type, amount, expiry) — is visible in the table via columns, notes, or explicit setup, not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **4.10-depth-open-questions**: Multiple open questions are surfaced — ambiguities or product decisions the requirements don't specify (e.g. what happens to a product-specific coupon when that product is removed, or how percentage and product-specific discounts interact).
- ✅ **4.11-correctness-cart-totals**: Cart totals are arithmetically correct: sum(quantity × price) − coupon = total, for every row in the total calculation table.
- ✅ **4.12-coupon-expiry-column**: Coupon application table uses a column to signal expiry status — either an 'Expired?' yes/no column, a 'Status' column, or equivalent. Expiry is visible per row, not just implied by the scenario name.
- ❌ **4.14-coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
  > Coupon Application table has 'Existing Coupon' column (before state), but lacks an explicit 'Active coupon after?' or similar after-state column. For 'New valid coupon replaces existing' row, the table shows SAVE10 in the before column and that SAVE20 is applied, but does not explicitly show a column confirming SAVE20 is the new active coupon after the operation.
- ✅ **4.15-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Add more of same product', 'Replace active coupon') — not 'Test case 1' or generic labels.

