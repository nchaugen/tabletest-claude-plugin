The file write was denied, so here is the complete test class:

```java
package com.example.fulfillment;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.*;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

// Dependency (add to pom.xml):
//   <dependency>
//     <groupId>org.tabletest</groupId>
//     <artifactId>tabletest-junit</artifactId>
//     <version>VERSION</version>
//     <scope>test</scope>
//   </dependency>
//   Requires JUnit Jupiter 5.11+.

public class SplitOrderTest {

    private final OrderSplitter splitter = new OrderSplitter();

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Partition by fulfillment type
    // ─────────────────────────────────────────────────────────────────────────

    @DisplayName("Partition by fulfillment type")
    @Description("""
            Items with different fulfillment types always go in separate shipments,
            regardless of delivery address or warehouse inventory.
            All items are IN_STOCK at WH-A. DELIVERY items go to "Home".
            """)
    @TableTest("""
            Scenario                    | Items                                                                                 | Shipment count? | Fulfillment types?
            All DELIVERY                | ["camera:1:DELIVERY:Home"]                                                            | 1               | [DELIVERY]
            All PICKUP                  | ["camera:1:PICKUP"]                                                                   | 1               | [PICKUP]
            One DELIVERY one PICKUP     | ["camera:1:DELIVERY:Home", "tripod:1:PICKUP"]                                         | 2               | [DELIVERY, PICKUP]
            Multiple items of each type | ["camera:1:DELIVERY:Home", "lens:1:DELIVERY:Home", "bag:1:PICKUP", "strap:1:PICKUP"] | 2               | [DELIVERY, PICKUP]
            """)
    void partitionsByFulfillmentType(List<OrderItem> items, int shipmentCount,
                                     List<FulfillmentType> fulfillmentTypes) {
        WarehouseInventory inventory = allInStockAt("WH-A", productIds(items));

        List<Shipment> shipments = splitter.splitOrder(new Order(items), inventory);

        assertThat(shipments).hasSize(shipmentCount);
        assertThat(shipments.stream().map(Shipment::getFulfillmentType).sorted())
                .containsExactlyInAnyOrderElementsOf(fulfillmentTypes);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Partition by delivery address
    // ─────────────────────────────────────────────────────────────────────────

    @DisplayName("Partition DELIVERY items by address")
    @Description("""
            DELIVERY items destined for different addresses go in separate shipments.
            All items are IN_STOCK at WH-A.
            """)
    @TableTest("""
            Scenario                    | Items                                                                               | Shipment count?
            Same address                | ["camera:1:DELIVERY:Home", "lens:1:DELIVERY:Home"]                                  | 1
            Different addresses         | ["camera:1:DELIVERY:Home", "tripod:1:DELIVERY:Office"]                              | 2
            Two same address, one other | ["a:1:DELIVERY:Home", "b:1:DELIVERY:Home", "c:1:DELIVERY:Office"]                  | 2
            Three distinct addresses    | ["a:1:DELIVERY:Home", "b:1:DELIVERY:Office", "c:1:DELIVERY:Warehouse"]             | 3
            """)
    void partitionsByDeliveryAddress(List<OrderItem> items, int shipmentCount) {
        WarehouseInventory inventory = allInStockAt("WH-A", productIds(items));

        List<Shipment> shipments = splitter.splitOrder(new Order(items), inventory);

        assertThat(shipments).hasSize(shipmentCount);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Partition by availability
    // ─────────────────────────────────────────────────────────────────────────

    @DisplayName("In-stock items do not wait for backordered or pre-ordered items")
    @Description("""
            IN_STOCK items are placed in an IMMEDIATE shipment.
            BACKORDERED and PRE_ORDER items are placed in a DELAYED shipment.
            In-stock items are never held in the same shipment as unavailable items.
            All items are DELIVERY to "Home" at WH-A.
            Open: when a BACKORDERED and a PRE_ORDER item share the same address group,
            do they go in one DELAYED shipment or separate delayed shipments
            (they may have different availability dates)?
            """)
    @TableTest("""
            Scenario                    | Product availabilities                         | Immediate items? | Delayed items?
            All in stock                | [camera: IN_STOCK, lens: IN_STOCK]             | [camera, lens]   | []
            All backordered             | [camera: BACKORDERED, lens: BACKORDERED]       | []               | [camera, lens]
            All pre-ordered             | [camera: PRE_ORDER, lens: PRE_ORDER]           | []               | [camera, lens]
            In-stock with backordered   | [camera: IN_STOCK, lens: BACKORDERED]          | [camera]         | [lens]
            In-stock with pre-ordered   | [camera: IN_STOCK, lens: PRE_ORDER]            | [camera]         | [lens]
            Single in-stock item        | [camera: IN_STOCK]                             | [camera]         | []
            Single backordered item     | [camera: BACKORDERED]                          | []               | [camera]
            """)
    void separatesImmediateFromDelayedItems(Map<String, AvailabilityStatus> productAvailabilities,
                                            List<String> immediateItems,
                                            List<String> delayedItems) {
        List<OrderItem> items = productAvailabilities.keySet().stream()
                .map(p -> new OrderItem(p, 1, FulfillmentType.DELIVERY, "Home"))
                .toList();
        WarehouseInventory inventory = inventoryAt("WH-A", productAvailabilities);

        List<Shipment> shipments = splitter.splitOrder(new Order(items), inventory);

        assertThat(productIdsFromShipments(shipments, ShipmentStatus.IMMEDIATE))
                .containsExactlyInAnyOrderElementsOf(immediateItems);
        assertThat(productIdsFromShipments(shipments, ShipmentStatus.DELAYED))
                .containsExactlyInAnyOrderElementsOf(delayedItems);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Warehouse minimisation
    // ─────────────────────────────────────────────────────────────────────────

    @DisplayName("Choose warehouses to minimise shipment count")
    @Description("""
            When items are available across multiple warehouses, the algorithm picks
            the combination of warehouses that covers the full order in the fewest shipments.
            All items are DELIVERY to "Home", all IN_STOCK at the warehouses listed.
            Items ordered are exactly the distinct products in the warehouse stock,
            one unit each.
            """)
    @TableTest("""
            Scenario                        | Warehouse stock                   | Shipment count?
            One warehouse covers all        | WH-A:[a,b,c]                      | 1
            Two warehouses required         | WH-A:[a,b] WH-B:[c]               | 2
            Optimal 2 beats naive 3         | WH-A:[a,b] WH-B:[b,c] WH-C:[a,c] | 2
            Full-coverage warehouse wins    | WH-A:[a,b,c] WH-B:[a] WH-C:[b]   | 1
            Each item only at own warehouse | WH-A:[a] WH-B:[b] WH-C:[c]        | 3
            """)
    void minimisesShipmentCountAcrossWarehouses(Map<String, List<String>> warehouseStock,
                                                int shipmentCount) {
        Set<String> allProducts = warehouseStock.values().stream()
                .flatMap(List::stream).collect(toSet());
        List<OrderItem> items = allProducts.stream()
                .map(p -> new OrderItem(p, 1, FulfillmentType.DELIVERY, "Home"))
                .toList();
        WarehouseInventory inventory = inventoryFromStock(warehouseStock);

        List<Shipment> shipments = splitter.splitOrder(new Order(items), inventory);

        assertThat(shipments).hasSize(shipmentCount);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. Companion co-location
    // ─────────────────────────────────────────────────────────────────────────

    @DisplayName("Companion products ship from the same warehouse when possible")
    @Description("""
            When at least one warehouse stocks all products in a companion group,
            the group is assigned to that warehouse so companions arrive together.
            When no single warehouse stocks all companions, they ship from their
            respective warehouses.
            All items are DELIVERY to "Home", IN_STOCK at each warehouse listed.
            Open: when co-locating companions would increase the total shipment count,
            which rule takes priority — companion co-location or minimisation?
            Open: can a PICKUP item be a companion of a DELIVERY item? They would
            be in separate shipments regardless of co-location.
            """)
    @TableTest("""
            Scenario                               | Companions   | Warehouse stock                    | Together?
            Both companions at one warehouse       | [body, lens] | WH-A:[body,lens]                   | true
            Companions at separate warehouses only | [body, lens] | WH-A:[body] WH-B:[lens]            | false
            Companions at multiple warehouses      | [body, lens] | WH-A:[body,lens] WH-B:[body,lens]  | true
            One warehouse has both, one has one    | [body, lens] | WH-A:[body] WH-B:[lens,body]       | true
            Non-companion items do not interfere   | [body, lens] | WH-A:[body,lens,memory-card]       | true
            """)
    void coLocatesCompanionProducts(List<String> companions,
                                    Map<String, List<String>> warehouseStock,
                                    boolean together) {
        Set<String> allProducts = warehouseStock.values().stream()
                .flatMap(List::stream).distinct().collect(toSet());
        List<OrderItem> items = allProducts.stream()
                .map(p -> new OrderItem(p, 1, FulfillmentType.DELIVERY, "Home"))
                .toList();
        WarehouseInventory inventory = inventoryWithCompanions(warehouseStock, companions);

        List<Shipment> shipments = splitter.splitOrder(new Order(items), inventory);

        boolean companionsTogether = shipments.stream()
                .anyMatch(s -> s.getItems().stream()
                        .map(OrderItem::getProductId)
                        .collect(toSet())
                        .containsAll(companions));

        assertThat(companionsTogether).isEqualTo(together);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Type converters
    // ─────────────────────────────────────────────────────────────────────────

    /** Parses "productId:quantity:FULFILLMENT_TYPE[:deliveryAddress]" */
    @TypeConverter
    public static OrderItem parseOrderItem(String value) {
        String[] parts = value.split(":", 4);
        String productId = parts[0];
        int quantity = Integer.parseInt(parts[1]);
        FulfillmentType type = FulfillmentType.valueOf(parts[2]);
        String address = parts.length == 4 ? parts[3] : null;
        return new OrderItem(productId, quantity, type, address);
    }

    /** Parses "WH-A:[a,b,c] WH-B:[d,e]" — space-separated warehouse entries */
    @TypeConverter
    public static Map<String, List<String>> parseWarehouseStock(String value) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String entry : value.split(" ")) {
            int colon = entry.indexOf(':');
            String warehouse = entry.substring(0, colon);
            String productList = entry.substring(colon + 2, entry.length() - 1); // strip ":[ ... ]"
            result.put(warehouse, Arrays.asList(productList.split(",")));
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Set<String> productIds(List<OrderItem> items) {
        return items.stream().map(OrderItem::getProductId).collect(toSet());
    }

    private Set<String> productIdsFromShipments(List<Shipment> shipments, ShipmentStatus status) {
        return shipments.stream()
                .filter(s -> s.getStatus() == status)
                .flatMap(s -> s.getItems().stream())
                .map(OrderItem::getProductId)
                .collect(toSet());
    }

    private WarehouseInventory allInStockAt(String warehouseId, Set<String> productIds) {
        Map<String, AvailabilityStatus> stock = productIds.stream()
                .collect(toMap(id -> id, id -> AvailabilityStatus.IN_STOCK));
        return WarehouseInventory.builder().addWarehouse(warehouseId, stock).build();
    }

    private WarehouseInventory inventoryAt(String warehouseId,
                                           Map<String, AvailabilityStatus> availability) {
        return WarehouseInventory.builder().addWarehouse(warehouseId, availability).build();
    }

    private WarehouseInventory inventoryFromStock(Map<String, List<String>> warehouseStock) {
        WarehouseInventory.Builder builder = WarehouseInventory.builder();
        warehouseStock.forEach((warehouse, products) -> {
            Map<String, AvailabilityStatus> stock = products.stream()
                    .collect(toMap(p -> p, p -> AvailabilityStatus.IN_STOCK));
            builder.addWarehouse(warehouse, stock);
        });
        return builder.build();
    }

    private WarehouseInventory inventoryWithCompanions(Map<String, List<String>> warehouseStock,
                                                       List<String> companionGroup) {
        WarehouseInventory.Builder builder = WarehouseInventory.builder();
        warehouseStock.forEach((warehouse, products) -> {
            Map<String, AvailabilityStatus> stock = products.stream()
                    .collect(toMap(p -> p, p -> AvailabilityStatus.IN_STOCK));
            builder.addWarehouse(warehouse, stock);
        });
        builder.addCompanionGroup(companionGroup);
        return builder.build();
    }
}
```

---

**Five concerns, five tables. Key decisions:**

**Type converters:**
- `parseOrderItem` handles `"camera:1:DELIVERY:Home"` / `"tripod:1:PICKUP"` — quoted inside lists because they contain colons
- `parseWarehouseStock` handles `WH-A:[a,b] WH-B:[c]` — a custom compact format that avoids nesting issues with built-in collection parsing
- `Map<String, AvailabilityStatus>` uses **built-in JUnit conversion** (`[camera: IN_STOCK, lens: BACKORDERED]`) — no custom converter needed since `AvailabilityStatus` is an enum

**Warehouse minimisation "Optimal 2 beats naive 3" row:** WH-A has [a,b], WH-B has [b,c], WH-C has [a,c] — any two warehouses cover all three items, but a naïve per-item assignment produces three shipments. The test asserts 2.

**Two open questions surfaced** and recorded in `@Description` rather than silently resolved: whether BACKORDERED + PRE_ORDER share one delayed shipment, and whether companion co-location takes priority over minimisation when they conflict.