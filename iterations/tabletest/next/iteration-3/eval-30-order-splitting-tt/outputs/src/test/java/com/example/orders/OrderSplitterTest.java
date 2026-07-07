package com.example.orders;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private final OrderSplitter splitter = new OrderSplitter();

    @Description("""
        Quantity never affects splitting decisions (WarehouseInventory tracks only
        per-warehouse stock status, not quantity), so every item uses a quantity of 1.
        A single warehouse ("W1") stocks everything in this table, since warehouse
        selection is a separate concern covered by choosesWarehouseCombinationMinimizingShipments.
        PICKUP items reuse the deliveryAddress field to carry a pickup location/store id,
        since OrderItem has no separate field for it.
        """)
    @TableTest("""
        Scenario                                   | Items                                                 | Shipment Groups?
        Same fulfillment type and address combine  | [A@DELIVERY@Addr1, B@DELIVERY@Addr1]                  | A,B
        Different fulfillment types split          | [A@DELIVERY@Addr1, B@PICKUP@StoreX]                   | A;B
        Different delivery addresses split         | [A@DELIVERY@Addr1, B@DELIVERY@Addr2]                  | A;B
        Address group combines while pickup splits | [A@DELIVERY@Addr1, B@DELIVERY@Addr1, C@PICKUP@StoreX] | A,B;C
        Different pickup locations split           | [A@PICKUP@StoreX, B@PICKUP@StoreY]                    | A;B
        """)
    void splitsByFulfillmentTypeAndAddress(List<OrderItem> items, Set<Set<String>> shipmentGroups) {
        List<Shipment> shipments = splitter.splitOrder(new Order(items), allInStockAt(items, "W1"));
        assertEquals(shipmentGroups, productIdGroups(shipments));
    }

    @Description("""
        All items share the same fulfillment type, address and warehouse ("W1"), since
        availability is the only concern under test here. Quantity is fixed at 1.
        """)
    @TableTest("""
        Scenario                                         | Product Ids | Stock                                                   | Immediate Items? | When Available Items?
        All items in stock ship together immediately     | [A, B]      | [A: W1@IN_STOCK, B: W1@IN_STOCK]                        | [A, B]           | []
        In-stock item is not held back for a backorder   | [A, B]      | [A: W1@IN_STOCK, B: W1@BACKORDERED]                     | [A]              | [B]
        Backordered and pre-ordered items ship together  | [A, B]      | [A: W1@BACKORDERED, B: W1@PRE_ORDERED]                  | []               | [A, B]
        Mixed stock states split into two shipments       | [A, B, C]   | [A: W1@IN_STOCK, B: W1@PRE_ORDERED, C: W1@BACKORDERED]  | [A]              | [B, C]
        """)
    void splitsByAvailability(List<String> productIds, WarehouseInventory stock,
                              List<String> immediateItems, List<String> whenAvailableItems) {
        List<Shipment> shipments = splitter.splitOrder(deliveryOrder(productIds), stock);
        assertEquals(Set.copyOf(immediateItems), productIdsWithAvailability(shipments, Availability.IMMEDIATE));
        assertEquals(Set.copyOf(whenAvailableItems), productIdsWithAvailability(shipments, Availability.WHEN_AVAILABLE));
    }

    @Description("""
        Every item is IN_STOCK wherever it is stocked, since availability is a separate
        concern covered by splitsByAvailability. All items share the same fulfillment type
        and address so only warehouse choice can affect shipment count. Quantity is fixed at 1.
        """)
    @TableTest("""
        Scenario                                               | Product Ids | Stock                                                                    | Shipment Count? | Warehouses Used?
        Single warehouse stocks everything                     | [A, B]      | [A: W1@IN_STOCK, B: W1@IN_STOCK]                                         | 1               | {W1}
        No shared warehouse forces a shipment per warehouse    | [A, B]      | [A: W1@IN_STOCK, B: W2@IN_STOCK]                                         | 2               | {W1, W2}
        Warehouse covering every item is preferred             | [A, B, C]   | [A: W1@IN_STOCK+W2@IN_STOCK, B: W1@IN_STOCK+W2@IN_STOCK, C: W2@IN_STOCK] | 1               | {W2}
        Minimum warehouse combination still forced by outliers | [A, B, C]   | [A: W1@IN_STOCK, B: W1@IN_STOCK+W2@IN_STOCK, C: W2@IN_STOCK]             | 2               | {W1, W2}
        """)
    void choosesWarehouseCombinationMinimizingShipments(List<String> productIds, WarehouseInventory stock,
                                                         int shipmentCount, Set<String> warehousesUsed) {
        List<Shipment> shipments = splitter.splitOrder(deliveryOrder(productIds), stock);
        assertEquals(shipmentCount, shipments.size());
        assertEquals(warehousesUsed, warehouseIdsUsed(shipments));
    }

    @Description("""
        Companion group is always {camera, lens} across every row; battery is never a
        companion. Every product is IN_STOCK wherever it is stocked, since availability is
        a separate concern. Rows vary where each product is stocked, and the last row varies
        fulfillment type, to show when the companion preference can and cannot be honored.
        """)
    @TableTest("""
        Scenario                                               | Items                                                                 | Stock                                                                      | Shipment Groups?
        Companions combine when a warehouse stocks both        | [camera@DELIVERY@Addr1, lens@DELIVERY@Addr1]                          | [camera: W1@IN_STOCK+W2@IN_STOCK, lens: W1@IN_STOCK+W2@IN_STOCK]           | camera,lens
        Companion follows its pair to the shared warehouse    | [camera@DELIVERY@Addr1, lens@DELIVERY@Addr1, battery@DELIVERY@Addr1] | [camera: W1@IN_STOCK+W2@IN_STOCK, lens: W2@IN_STOCK, battery: W1@IN_STOCK] | camera,lens;battery
        Companions split when no warehouse stocks both         | [camera@DELIVERY@Addr1, lens@DELIVERY@Addr1]                          | [camera: W1@IN_STOCK, lens: W2@IN_STOCK]                                   | camera;lens
        Fulfillment type split overrides companion preference | [camera@DELIVERY@Addr1, lens@PICKUP@StoreX]                           | [camera: W1@IN_STOCK, lens: W1@IN_STOCK]                                   | camera;lens
        """)
    void groupsCompanionsOnSameWarehouseWhenPossible(List<OrderItem> items, WarehouseInventory stock,
                                                      Set<Set<String>> shipmentGroups) {
        List<Shipment> shipments = splitter.splitOrder(companionOrder(items, Set.of("camera", "lens")), stock);
        assertEquals(shipmentGroups, productIdGroups(shipments));
    }

    @TypeConverter
    public static OrderItem parseItem(String spec) {
        String[] parts = spec.split("@", 3);
        return new OrderItem(parts[0], 1, FulfillmentType.valueOf(parts[1]), parts[2]);
    }

    @TypeConverter
    public static WarehouseInventory parseInventory(Map<String, String> stockByProduct) {
        WarehouseInventory inventory = new WarehouseInventory();
        stockByProduct.forEach((productId, encoded) -> parseStockEntries(encoded)
                .forEach(entry -> inventory.addStock(entry[0], productId, StockStatus.valueOf(entry[1]))));
        return inventory;
    }

    @TypeConverter
    public static Set<Set<String>> parseShipmentGroups(String spec) {
        return Arrays.stream(spec.split(";"))
                .map(group -> Set.copyOf(List.of(group.split(","))))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static List<String[]> parseStockEntries(String encoded) {
        return Arrays.stream(encoded.split("\\+")).map(entry -> entry.split("@", 2)).toList();
    }

    private static WarehouseInventory allInStockAt(List<OrderItem> items, String warehouseId) {
        WarehouseInventory inventory = new WarehouseInventory();
        items.stream().map(OrderItem::getProductId).distinct()
                .forEach(productId -> inventory.addStock(warehouseId, productId, StockStatus.IN_STOCK));
        return inventory;
    }

    private static Order deliveryOrder(List<String> productIds) {
        List<OrderItem> items = productIds.stream()
                .map(productId -> new OrderItem(productId, 1, FulfillmentType.DELIVERY, "Addr1"))
                .toList();
        return new Order(items);
    }

    private static Order companionOrder(List<OrderItem> items, Set<String> companions) {
        return new Order(items, List.of(List.copyOf(companions)));
    }

    private static Set<String> productIdsWithAvailability(List<Shipment> shipments, Availability availability) {
        return shipments.stream()
                .filter(shipment -> shipment.availability() == availability)
                .flatMap(shipment -> shipment.productIds().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<String> warehouseIdsUsed(List<Shipment> shipments) {
        return shipments.stream().map(Shipment::getWarehouseId).collect(Collectors.toUnmodifiableSet());
    }

    private static Set<Set<String>> productIdGroups(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> Set.copyOf(shipment.productIds()))
                .collect(Collectors.toUnmodifiableSet());
    }
}
