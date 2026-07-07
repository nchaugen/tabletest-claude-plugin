I've written the TableTests for order splitting. Summary:

**`build.gradle`** — added the `tabletest-junit:1.2.1` test dependency (JUnit Jupiter 6.0.3 already present, satisfies the ≥5.11 requirement).

**`src/test/java/com/example/orders/OrderSplitterTest.java`** — 4 `@TableTest` methods, each isolating one splitting concern (per the "cannot name it without 'and'" decomposition rule):

1. `groupsItemsByFulfillmentTypeAndDeliveryAddress` — fulfillment-type and address grouping (single warehouse, all in stock, so these two rules can be verified in isolation).
2. `shipsInStockItemsImmediatelyWithoutHoldingForBackorderedItems` — in-stock vs. backordered/pre-ordered availability grouping, including the "don't hold in-stock items back" case.
3. `minimisesShipmentCountAcrossWarehouses` — warehouse-combination selection, including a "greedy trap" row where picking the warehouse with more coverage of one item leads to a worse total count than picking the warehouse covering everything.
4. `prefersShippingCompanionProductsFromTheSameWarehouseWhenPossible` — companion grouping as a tie-breaker among equally-minimal warehouse combinations, the fallback when no single warehouse holds both companions, and independent handling of multiple companion groups.

Each row's expected result is a `Set<String>` of `warehouseId/availability=sortedProductIds` shipment descriptors, compared against the actual `List<Shipment>` via a small `describeShipment` helper (pure field extraction, not reimplemented logic). Custom `@TypeConverter`s parse compact `productId@type@address`, `warehouse@productId@status`, and `productA+productB` specs into `OrderItem`, `StockEntry`, and `CompanionGroup` values.

Verified: `gradle compileTestJava` succeeds, and `gradle test` runs all 17 parameterized cases — each fails only with `UnsupportedOperationException` from the stub `OrderSplitter`, confirming the tables parse and wire up correctly ahead of the real implementation.