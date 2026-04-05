# Eval Review — Iteration 13

**Model:** sonnet · **Date:** 2026-03-29 · **Evals:** 4

## Summary

**with_skill:** 50/66 (75.8%) · 1138646 tokens · 641.3s · $1.5501

## Delta vs Iteration 12

**Regressions (14):**
- ❌ eval-14-weekly-pay: `1.1-traceability-columns`
- ❌ eval-14-weekly-pay: `1.5-depth-error-edge-cases`
- ❌ eval-14-weekly-pay: `1.8-correctness-expected-values`
- ❌ eval-14-weekly-pay: `1.11-format-description`
- ❌ eval-14-weekly-pay: `1.15-format-clean-method`
- ❌ eval-15-reis-discount: `2.4-depth-rolling-window-boundary`
- ❌ eval-15-reis-discount: `2.6-readability-human-readable-values`
- ❌ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ❌ eval-15-reis-discount: `2.16-no-duplicate-tier-mapping`
- ❌ eval-15-reis-discount: `2.12-format-typeconverter`
- ❌ eval-15-reis-discount: `2.19-depth-all-tiers`
- ❌ eval-15-reis-discount: `2.20-readability-one-row-per-tier`
- ❌ eval-15-reis-discount: `2.21-readability-relative-time`
- ❌ eval-16-order-splitting: `3.4-depth-availability-scenarios`

**Improvements (2):**
- ✅ eval-14-weekly-pay: `1.14-depth-zero-rate`
- ✅ eval-15-reis-discount: `2.17-zone-irrelevance-visible`

## Resource Comparison vs Iteration 12

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-14-weekly-pay | 10/15 | 14/15 | 468626 | 303464 | 220.2 | 339.6 |
| eval-15-reis-discount | 9/18 | 16/18 | 530073 | 276981 | 249.0 | 253.0 |
| eval-16-order-splitting | 15/17 | 16/17 | 71164 | 77674 | 103.8 | 210.9 |
| eval-17-shopping-cart | 16/16 | 16/16 | 68783 | 68687 | 68.2 | 71.6 |

## Per-Eval Results

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**10/15** · 468626 tokens · 220209ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > The pay table contains only these expected columns: 'Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?'. There are no intermediate columns such as 'Regular pay?', 'Overtime pay?', 'Premium pay?', 'Sunday pay?', or 'Holiday pay?'.
- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > The tables include no row testing negative hours or flooring them to zero. Only negative rate is tested in the error table. The assertion requires both negative hours and negative/missing rate; only negative rate is present.
- ✅ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ❌ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
  > No @Description annotation is present on the test method. The assertion specifically requires verification 'consistent with the formulas in @Description', but this annotation is missing from the code.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ❌ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotation is present on the pay calculation method (shouldCalculateWeeklyPay) or on any other method in the class.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ❌ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
  > The test method bodies contain ternary operators for null-handling: 'int weekday = weekdayHours != null ? weekdayHours : 0; int sunday = sundayHours != null ? sundayHours : 0; int holiday = holidayHours != null ? holidayHours : 0;' This violates the assertion which explicitly requires null-handling to be external to the test method.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**9/18** · 530073 tokens · 248961ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > The first @TableTest uses 'Tickets Bought in Last 30 Days' column which measures ticket count, not time boundaries. No rows test the boundary between day 30 and day 31; only ticket counts (0, 4, 5, 8, 9, 19, 39, 44, 60) are tested.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > While PassengerType enum (ADULT, SENIOR, CHILD) provides human-readable values, the response does not mention TypeConverter or any conversion mechanism. The "Interface implied by the tests" section lists only method signatures without reference to TypeConverters.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > The 5% tier is split: "First tier kicks in — ticket 5 | 4 | 5" (row 2) and "First tier holds through ticket 9 | {5, 8} | 5" (row 3), violating "not split across multiple rows". Table enumerates boundaries (4, 9, 19, 39) rather than expressing each tier as a single value set.
- ❌ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
  > Table 1 establishes mappings: 0→0%, 4→5%, 9→10%, 19→20%, 39→40%. Table 2 repeats these for ADULT/SENIOR: "Adult, no history | ADULT | 0... | 0", "Adult, first Reis tier | ADULT | 4... | 5", "Adult, mid-ladder | ADULT | 9... | 10". Same input-output pairs appear in both tables.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > Response contains @DisplayName, @Description, and @TableTest annotations, but makes no mention of TypeConverterSources or any TypeConverter implementation. No conversion mechanism is referenced.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > ADULT and SENIOR are in separate rows with identical outcomes: "Adult, no history | ADULT | 0 | {1, 2, 3} | 0" (row 1) and "Senior treated same as adult — no history | SENIOR | 0 | {1, 2, 3} | 0" (row 4). These should be combined as a single row with {ADULT, SENIOR} value set.
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
  > Table shows only 5 tiers: 0% (row 1), 5% (rows 2-3), 10% (row 4), 20% (row 5), and 40% (rows 6-7). Missing tiers: 15%, 25%, 30%, 35%. No rows test ticket counts in ranges 14-18, 24-28, 29-33, 34-38.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > The 5% tier violates this: "First tier kicks in — ticket 5 | 4 | 5" and "First tier holds through ticket 9 | {5, 8} | 5" are two separate rows for the same tier. The assertion explicitly states 'not split across multiple rows' with the exact pattern shown here.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > No rolling window table exists in the response. Only two @TableTest methods are present. The first table uses 'Tickets Bought in Last 30 Days' (a count column) but contains no time-relative expressions like 'days ago' or temporal boundary tests.

### ⚠️ Eval eval-16-order-splitting [with_skill]

**15/17** · 71164 tokens · 103845ms

- ✅ **3.1a-concern-fulfillment-method**: Fulfillment method splitting is represented as its own table — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
- ✅ **3.1b-concern-delivery-address**: Delivery address splitting is represented as its own table — items going to different addresses must be in separate shipments.
- ✅ **3.1c-concern-availability**: Availability splitting is represented as its own table — in-stock items ship immediately, backordered/pre-ordered items ship when available.
- ✅ **3.1d-concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own table — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **3.1e-concern-companion-products**: Companion product grouping is represented as its own table — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **3.2-depth-fulfillment-scenarios**: Fulfillment table covers at least: all items for home delivery, all items for store pickup, and a mix of delivery and pickup items.
- ✅ **3.3-depth-delivery-address-scenarios**: Delivery address table covers at least: all items to the same address, and items to different addresses.
- ❌ **3.4-depth-availability-scenarios**: Availability table covers at least: all items in stock, a mix of in-stock and pre-order/backorder, and two backordered/pre-ordered items with different dates. Pre-order and backorder are recognised as equivalent for splitting semantics — either by comment, note, or by including both with the same rules.
  > Table 2 covers in-stock, mixed, and different dates scenarios. However, the equivalence requirement is not met: the table only includes 'Backordered items' column with no pre-order examples shown, no explicit comment noting equivalence, and no examples of both pre-order AND backorder items with the same rules as required by the assertion's three-option specification
- ✅ **3.5-depth-warehouse-scenarios**: Warehouse allocation table covers at least: single warehouse has full quantity, multi-warehouse split needed, fewer-warehouse split preferred over more warehouses, and cannot fulfill (insufficient total stock).
- ❌ **3.6-depth-companion-scenarios**: Companion table covers at least: single warehouse has both companions, unavoidable split (no warehouse has both but both are available), and fulfillment not possible (one companion not available anywhere).
  > Table 5 covers 'Only one warehouse stocks both' and 'No warehouse stocks both (unavoidable split)' scenarios. However, it lacks the required 'fulfillment not possible' scenario where one companion item is completely unavailable (e.g., Camera body in WH East but Camera lens in no warehouse at all)
- ✅ **3.7-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **3.8-readability-item-property-mapping**: Trigger tables represent multi-item orders where each item has its own fulfillment type, address, and availability. The notation makes the item-to-property mapping unambiguous — not value sets like {delivery, pickup} which lose which item has which property.
- ✅ **3.9-readability-scalar-column**: Warehouse allocation table uses a scalar count column (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.10-readability-business-language**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **3.11-readability-output-column-question-mark**: Output/expected columns end with '?' (e.g. 'Shipments?', 'Groups?') to distinguish them from input columns.
- ✅ **3.12-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ✅ **3.13-depth-open-questions**: Multiple open questions are surfaced across the tables — ambiguities, edge cases, or product decisions that the requirements don't fully specify.

### ✅ Eval eval-17-shopping-cart [with_skill]

**16/16** · 68783 tokens · 68240ms

- ✅ **4.1a-concern-item-operations**: Item operations (add/remove) are represented as their own table or tables — separate from coupon and cart total logic.
- ✅ **4.1b-concern-coupon-application**: Coupon application (entering, replacing, rejecting codes) is represented as its own table — separate from item operations and cart total calculation.
- ✅ **4.1c-concern-cart-total**: Cart total calculation (how different coupon types affect the price) is represented as its own table — separate from coupon application and item operations.
- ✅ **4.1d-concern-checkout**: Checkout (stock verification) is represented as its own table — separate from the other concerns.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ✅ **4.4-depth-item-operations**: Item operations cover at least: add to empty cart, add to non-empty cart, add more of an item already in cart, remove item from cart, remove last item in cart, remove item not in cart.
- ✅ **4.5-depth-coupon-application**: Coupon application table covers validity and replacement — not coupon types. Scenarios include at least: add valid coupon (none active), add valid coupon (another active), add expired coupon (none active), add expired coupon (another active), add nonexistent code (none active), add nonexistent code (another active).
- ✅ **4.6-depth-cart-total-scenarios**: Cart total table covers how different coupon types affect the price. Scenarios include at least: no coupon, percentage off whole cart, fixed amount off, fixed amount exceeding cart total (floors at zero), product-specific discount (product in cart), product-specific discount (product not in cart).
- ✅ **4.7-depth-checkout-scenarios**: Checkout table covers at least: empty cart, all items in stock, and not all items in stock (insufficient inventory).
- ✅ **4.8-readability-business-language**: Column names use business/user-perspective language (e.g. 'Message?' not 'Result?', 'Coupon code' not 'input'). The tables read as a specification a product person could review.
- ✅ **4.9-readability-test-data-visible**: Test data that affects the outcome — product prices, coupon rules (type, amount, expiry) — is visible in the table via columns, notes, or explicit setup, not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **4.10-depth-open-questions**: Multiple open questions are surfaced — ambiguities or product decisions the requirements don't specify (e.g. what happens to a product-specific coupon when that product is removed, or how percentage and product-specific discounts interact).
- ✅ **4.11-correctness-cart-totals**: Cart totals are arithmetically correct: sum(quantity × price) − coupon = total, for every row in the total calculation table.
- ✅ **4.12-coupon-expiry-column**: Coupon application table uses a column to signal expiry status — either an 'Expired?' yes/no column, a 'Status' column, or equivalent. Expiry is visible per row, not just implied by the scenario name.
- ✅ **4.14-coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **4.15-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Add more of same product', 'Replace active coupon') — not 'Test case 1' or generic labels.

