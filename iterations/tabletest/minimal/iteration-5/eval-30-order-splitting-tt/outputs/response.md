Here's the complete `SplitOrderTest.java`:

```java
package com.example.order;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

public class SplitOrderTest {

    // ── Concern 1: Items with different fulfillment types go in separate shipments ──

    @DisplayName("Fulfillment type partitioning")
    @Description("Inventory: warehouse W1 holds all products in stock.")
    @TableTest("""
            Scenario                          | Order                                                           | Shipment count? | Fulfillment types?
            All delivery, same address        | DELIVERY/addr1/p1/1 ; DELIVERY/addr1/p2/1                       | 1               | [DELIVERY]
            All pickup                        | PICKUP//p1/1 ; PICKUP//p2/1                                     | 1               | [PICKUP]
            One delivery, one pickup          | DELIVERY/addr1/p1/1 ; PICKUP//p2/1                              | 2               | [DELIVERY, PICKUP]
            Multiple delivery with one pickup | DELIVERY/addr1/p1/1 ; DELIVERY/addr1/p2/1 ; PICKUP//p3/1        | 2               | [DELIVERY, PICKUP]
            """)
    void shouldSeparateByFulfillmentType(Order order, int shipmentCount, List<FulfillmentType> fulfillmentTypes) {
        WarehouseInventory inventory = allInStock("W1", "p1", "p2", "p3");
        List<Shipment> shipments = splitOrder(order, inventory);

        assertEquals(shipmentCount, shipments.size());
        assertEquals(new HashSet<>(fulfillmentTypes),
                shipments.stream().map(Shipment::getFulfillmentType).collect(Collectors.toSet()));
    }

    // ── Concern 2: Delivery items to different addresses go in separate shipments ──

    @DisplayName("Delivery address partitioning")
    @Description("All items DELIVERY type. Inventory: warehouse W1 holds all products in stock.")
    @TableTest("""
            Scenario                    | Order                                                                | Shipment count?
            Same address                | DELIVERY/addr1/p1/1 ; DELIVERY/addr1/p2/1                           | 1
            Two different addresses     | DELIVERY/addr1/p1/1 ; DELIVERY/addr2/p2/1                           | 2
            Three different addresses   | DELIVERY/addr1/p1/1 ; DELIVERY/addr2/p2/1 ; DELIVERY/addr3/p3/1    | 3
            Two same, one different     | DELIVERY/addr1/p1/1 ; DELIVERY/addr1/p2/1 ; DELIVERY/addr2/p3/1    | 2
            """)
    void shouldSeparateByDeliveryAddress(Order order, int shipmentCount) {
        WarehouseInventory inventory = allInStock("W1", "p1", "p2", "p3");
        List<Shipment> shipments = splitOrder(order, inventory);

        assertEquals(shipmentCount, shipments.size());
    }

    // ── Concern 3: In-stock items ship immediately; not held for backordered/pre-ordered ──

    @DisplayName("Stock availability partitioning")
    @Description("Fixed order: p1 and p2, both DELIVERY to addr1, fulfilled from warehouse W1.")
    @TableTest("""
            Scenario                          | Inventory                                        | Shipment count? | Immediate products? | Delayed products?
            All in stock                      | W1/p1/5/IN_STOCK ; W1/p2/5/IN_STOCK             | 1               | [p1, p2]            | []
            All backordered                   | W1/p1/0/BACKORDERED ; W1/p2/0/BACKORDERED        | 1               | []                  | [p1, p2]
            All pre-ordered                   | W1/p1/0/PRE_ORDER ; W1/p2/0/PRE_ORDER            | 1               | []                  | [p1, p2]
            In-stock not held for backordered | W1/p1/5/IN_STOCK ; W1/p2/0/BACKORDERED          | 2               | [p1]                | [p2]
            In-stock not held for pre-ordered | W1/p1/5/IN_STOCK ; W1/p2/0/PRE_ORDER            | 2               | [p1]                | [p2]
            """)
    void shouldShipInStockItemsImmediately(WarehouseInventory inventory,
                                           int shipmentCount,
                                           List<String> immediateProducts,
                                           List<String> delayedProducts) {
        Order order = parseOrder("DELIVERY/addr1/p1/1 ; DELIVERY/addr1/p2/1");
        List<Shipment> shipments = splitOrder(order, inventory);

        assertEquals(shipmentCount, shipments.size());
        assertEquals(new HashSet<>(immediateProducts), immediateProductIds(shipments));
        assertEquals(new HashSet<>(delayedProducts), delayedProductIds(shipments));
    }

    // ── Concern 4: Choose the warehouse combination covering the order in fewest shipments ──

    @DisplayName("Warehouse combination optimisation")
    @Description("All items: DELIVERY to addr1, all in stock.")
    @TableTest("""
            Scenario                             | Inventory                                                                   | Order                                                            | Shipment count? | Warehouses used?
            One warehouse covers all             | W1/p1/5/IN_STOCK ; W1/p2/5/IN_STOCK                                        | DELIVERY/addr1/p1/1 ; DELIVERY/addr1/p2/1                        | 1               | [W1]
            Full warehouse preferred over partial | W1/p1/5/IN_STOCK ; W1/p2/5/IN_STOCK ; W2/p1/5/IN_STOCK                    | DELIVERY/addr1/p1/1 ; DELIVERY/addr1/p2/1                        | 1               | [W1]
            No single warehouse covers all       | W1/p1/5/IN_STOCK ; W2/p2/5/IN_STOCK                                        | DELIVERY/addr1/p1/1 ; DELIVERY/addr1/p2/1                        | 2               | [W1, W2]
            Two warehouses preferred over three  | W1/p1/5/IN_STOCK ; W1/p2/5/IN_STOCK ; W2/p2/5/IN_STOCK ; W2/p3/5/IN_STOCK | DELIVERY/addr1/p1/1 ; DELIVERY/addr1/p2/1 ; DELIVERY/addr1/p3/1 | 2               | [W1, W2]
            """)
    void shouldMinimiseShipmentCountAcrossWarehouses(WarehouseInventory inventory, Order order,
                                                      int shipmentCount, List<String> warehousesUsed) {
        List<Shipment> shipments = splitOrder(order, inventory);

        assertEquals(shipmentCount, shipments.size());
        assertEquals(new HashSet<>(warehousesUsed),
                shipments.stream().map(Shipment::getWarehouseId).collect(Collectors.toSet()));
    }

    // ── Concern 5: Companion products ship from the same warehouse when possible ──

    @DisplayName("Companion product grouping")
    @Description("""
            Companion group membership is encoded as an optional 5th field in each item spec
            (e.g. DELIVERY/addr1/camera-body/1/cam-kit). Items sharing a group name are companions.
            All items: DELIVERY to addr1.
            """)
    @TableTest("""
            Scenario                               | Inventory                                                     | Order                                                                        | Together?
            Both companions in same warehouse      | W1/p1/5/IN_STOCK ; W1/p2/5/IN_STOCK                          | DELIVERY/addr1/p1/1/cam ; DELIVERY/addr1/p2/1/cam                            | true
            Consolidate to warehouse with both     | W1/p1/5/IN_STOCK ; W1/p2/5/IN_STOCK ; W2/p1/5/IN_STOCK       | DELIVERY/addr1/p1/1/cam ; DELIVERY/addr1/p2/1/cam                            | true
            Companions in exclusive warehouses     | W1/p1/5/IN_STOCK ; W2/p2/5/IN_STOCK                          | DELIVERY/addr1/p1/1/cam ; DELIVERY/addr1/p2/1/cam                            | false
            Companions together, other item splits | W1/p1/5/IN_STOCK ; W1/p2/5/IN_STOCK ; W2/p3/5/IN_STOCK       | DELIVERY/addr1/p1/1/cam ; DELIVERY/addr1/p2/1/cam ; DELIVERY/addr1/p3/1     | true
            """)
    void shouldKeepCompanionProductsTogether(WarehouseInventory inventory, Order order, boolean together) {
        List<Shipment> shipments = splitOrder(order, inventory);

        for (Map.Entry<String, Set<String>> group : companionGroupsOf(order).entrySet()) {
            assertEquals(together, areSameShipment(shipments, group.getValue()),
                    "companion group: " + group.getKey());
        }
    }

    // ── Type Converters ──────────────────────────────────────────────────────

    /**
     * Item format: TYPE/deliveryAddress/productId/quantity[/companionGroup]
     * Use empty string for address on PICKUP items: PICKUP//productId/qty
     */
    @TypeConverter
    public static Order parseOrder(String spec) {
        List<OrderItem> items = new ArrayList<>();
        for (String itemSpec : spec.split("\\s*;\\s*")) {
            String[] parts = itemSpec.trim().split("/", 5);
            FulfillmentType type = FulfillmentType.valueOf(parts[0]);
            String address = parts[1].isEmpty() ? null : parts[1];
            String productId = parts[2];
            int quantity = Integer.parseInt(parts[3]);
            String companionGroup = parts.length > 4 ? parts[4] : null;
            items.add(new OrderItem(productId, quantity, type, address, companionGroup));
        }
        return new Order(items);
    }

    /**
     * Entry format: warehouseId/productId/quantity/status (IN_STOCK | BACKORDERED | PRE_ORDER)
     */
    @TypeConverter
    public static WarehouseInventory parseInventory(String spec) {
        WarehouseInventory inventory = new WarehouseInventory();
        for (String entry : spec.split("\\s*;\\s*")) {
            String[] parts = entry.trim().split("/");
            inventory.addStock(parts[0], parts[1], Integer.parseInt(parts[2]), StockStatus.valueOf(parts[3]));
        }
        return inventory;
    }

    // ── Test helpers ─────────────────────────────────────────────────────────

    private static WarehouseInventory allInStock(String warehouseId, String... productIds) {
        WarehouseInventory inventory = new WarehouseInventory();
        for (String productId : productIds) {
            inventory.addStock(warehouseId, productId, 100, StockStatus.IN_STOCK);
        }
        return inventory;
    }

    private static Set<String> immediateProductIds(List<Shipment> shipments) {
        return shipments.stream()
                .filter(s -> s.getTiming() == ShipmentTiming.IMMEDIATE)
                .flatMap(s -> s.getItems().stream())
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());
    }

    private static Set<String> delayedProductIds(List<Shipment> shipments) {
        return shipments.stream()
                .filter(s -> s.getTiming() != ShipmentTiming.IMMEDIATE)
                .flatMap(s -> s.getItems().stream())
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());
    }

    private static Map<String, Set<String>> companionGroupsOf(Order order) {
        Map<String, Set<String>> groups = new LinkedHashMap<>();
        for (OrderItem item : order.getItems()) {
            if (item.getCompanionGroup() != null) {
                groups.computeIfAbsent(item.getCompanionGroup(), k -> new LinkedHashSet<>())
                        .add(item.getProductId());
            }
        }
        return groups;
    }

    private static boolean areSameShipment(List<Shipment> shipments, Set<String> productIds) {
        Map<String, Shipment> productToShipment = new HashMap<>();
        for (Shipment shipment : shipments) {
            for (OrderItem item : shipment.getItems()) {
                productToShipment.put(item.getProductId(), shipment);
            }
        }
        Set<Shipment> containing = productIds.stream()
                .map(productToShipment::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return containing.size() == 1;
    }

    private List<Shipment> splitOrder(Order order, WarehouseInventory inventory) {
        return new OrderSplitter().splitOrder(order, inventory);
    }
}
```

---

Five `@TableTest` methods, one per concern:

**`shouldSeparateByFulfillmentType`** (4 rows) — DELIVERY and PICKUP go in separate shipments. Inventory is hardcoded via `allInStock`. Asserts on shipment count and the set of fulfillment types.

**`shouldSeparateByDeliveryAddress`** (4 rows) — All DELIVERY, same inventory. Varies only by address combinations. Asserts on shipment count.

**`shouldShipInStockItemsImmediately`** (5 rows) — The order is fixed (two items, noted in `@Description`); only inventory stock status varies. The `Immediate products?` and `Delayed products?` columns let you trace which products landed in which timing bucket per scenario.

**`shouldMinimiseShipmentCountAcrossWarehouses`** (4 rows) — Both inventory coverage and the ordered products vary. `Warehouses used?` provides traceability: you can verify that the fewest warehouses were chosen, not just the count. The "two preferred over three" row uses W1(p1,p2)+W2(p2,p3) so the winning pair is deterministic.

**`shouldKeepCompanionProductsTogether`** (4 rows) — Companion group membership is encoded as an optional 5th field in the item spec (e.g. `p1/1/cam`). The test body extracts groups from the order and calls `areSameShipment` per group, so any number of companion groups in an order is handled uniformly.

**Cell format** uses ` ; ` (space-semicolon-space) as the item separator within a cell, avoiding the `[...]` list syntax so no quoting is needed. `@TypeConverter` methods on `Order` and `WarehouseInventory` handle all parsing.