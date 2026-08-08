Done. Summary of what I added, all under `src/main/java/com/example/orders` and a new `src/test/java/com/example/orders/OrderSplitterTest.java`:

**Test file** — 4 `@TableTest` tables, 16 rows total, all verified to compile, parse, and hit converters correctly (currently red because the stub throws `UnsupportedOperationException`, as expected pre-implementation):
1. `groupsItemsByFulfillmentAndAddress` — partitions items by fulfillment type + delivery address.
2. `splitsItemsByStockAvailability` — separates in-stock items from backordered/pre-ordered ones (documented assumption: the two non-in-stock statuses aren't distinguished from each other).
3. `selectsWarehousesMinimisingShipmentsWithCompanionPreference` — set-cover minimization with companion tie-breaking (documented assumption: pure ties with no companion signal are left unspecified, since the four given rules don't determine an answer there).
4. `keepsCompanionsApartAcrossHigherPrecedenceSplits` — a small integration table on the real `splitOrder` entry point proving fulfillment-type/address grouping and availability splitting take precedence over shipping companions together, even when nothing else would prevent it.

**Stub adjustments** (compilation-only, no logic implemented):
- `OrderSplitter` gained three narrower stub methods (`groupByFulfillment`, `groupByAvailability`, `selectWarehouses`) alongside the existing `splitOrder`, each still throwing `UnsupportedOperationException`.
- New `AvailabilityGroups` record (immediate/pending lists) as a return type.
- `build.gradle`: added the `tabletest-junit` test dependency.

**Notable assumption, called out in the Table 4 `@Description`**: `WarehouseInventory.companionGroups` is treated as the source of truth for companion pairings; `Order`'s own (duplicate) `companionGroups` field exists in the stub but is left unused by these tests, since a single source is simpler and inventory is the more natural owner of a product-level fact.

I verified all this empirically by running `gradle test` — every row parses and every converter fires correctly, with failures limited to the intentional stub exception.