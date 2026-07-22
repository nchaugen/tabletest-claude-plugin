package com.example.orders;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private final OrderSplitter splitter = new OrderSplitter();

    @Description("""
        All items are IN_STOCK at a single warehouse WH1, isolating the fulfillment-type and
        delivery-address grouping rule from the availability and warehouse-selection rules.
        """)
    @TableTest("""
        Scenario                             | Items                                                                       | Shipment Groups?
        Same fulfillment and address combine | [camera/DELIVERY/123 Main St, lens/DELIVERY/123 Main St]                    | [camera+lens]
        Different fulfillment types split    | [camera/DELIVERY/123 Main St, giftcard/PICKUP]                              | [camera, giftcard]
        Different delivery addresses split   | [camera/DELIVERY/123 Main St, lens/DELIVERY/456 Oak Ave]                    | [camera, lens]
        Multiple pickup items combine        | [giftcard/PICKUP, batteries/PICKUP]                                         | [giftcard+batteries]
        Three-way split by type and address  | [camera/DELIVERY/123 Main St, lens/DELIVERY/456 Oak Ave, giftcard/PICKUP]   | [camera, lens, giftcard]
        """)
    void groupsByFulfillmentTypeAndAddress(List<OrderItem> items, List<ShipmentGroup> shipmentGroups) {
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(item -> inventory.addStock("WH1", item.getProductId(), StockStatus.IN_STOCK));

        List<Shipment> result = splitter.splitOrder(new Order(items), inventory);

        assertEquals(expectedGroups(shipmentGroups), actualGroups(result));
    }

    @Description("""
        All items use DELIVERY fulfillment to the same address on a single warehouse WH1,
        isolating the availability rule (in-stock ships immediately; backordered/pre-ordered
        items ship separately when available, without holding back in-stock items) from the
        fulfillment-type/address grouping rule.
        """)
    @TableTest("""
        Scenario                                       | Items                                   | Shipments?
        All in stock ships together immediately        | [camera/IN_STOCK, lens/IN_STOCK]         | ["IMMEDIATE:camera+lens"]
        Backordered item held back separately           | [camera/IN_STOCK, lens/BACKORDERED]      | ["IMMEDIATE:camera", "WHEN_AVAILABLE:lens"]
        Pre-ordered item held back separately           | [camera/IN_STOCK, lens/PRE_ORDERED]      | ["IMMEDIATE:camera", "WHEN_AVAILABLE:lens"]
        Backordered and pre-ordered ship together later | [camera/BACKORDERED, lens/PRE_ORDERED]   | ["WHEN_AVAILABLE:camera+lens"]
        """)
    void splitsByAvailability(List<String> items, List<AvailabilityGroup> shipments) {
        List<OrderItem> orderItems = items.stream()
                .map(entry -> entry.split("/"))
                .map(parts -> new OrderItem(parts[0], 1, FulfillmentType.DELIVERY, "123 Main St"))
                .toList();
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(entry -> {
            String[] parts = entry.split("/");
            inventory.addStock("WH1", parts[0], StockStatus.valueOf(parts[1]));
        });

        List<Shipment> result = splitter.splitOrder(new Order(orderItems), inventory);

        assertEquals(Set.copyOf(shipments), actualAvailabilityGroups(result));
    }

    @Description("""
        All items use DELIVERY fulfillment to the same address and are IN_STOCK wherever listed,
        isolating warehouse-selection minimization from the fulfillment/address and availability
        rules. Each row is designed so the minimal covering combination of warehouses is unique,
        keeping the expected warehouse set unambiguous even though several equally-minimal
        combinations could otherwise exist.
        """)
    @TableTest("""
        Scenario                                                  | Availability                                                | Shipment Count? | Warehouses Used?
        Single warehouse covers everything                        | "camera:WH1+WH2, lens:WH1"                                  | 1               | {WH1}
        Two disjoint warehouses required                          | "camera:WH1, lens:WH2"                                      | 2               | {WH1, WH2}
        Shared warehouse avoids extra warehouse for flexible item  | "camera:WH2, charger:WH3, lens:WH2+WH3, battery:WH1+WH3"    | 2               | {WH2, WH3}
        Redundant warehouse not selected                           | "camera:WH1+WH2, lens:WH1+WH2, battery:WH2"                 | 1               | {WH2}
        """)
    void selectsMinimalWarehouseCombination(Map<String, Set<String>> availability, int shipmentCount, Set<String> warehousesUsed) {
        List<OrderItem> items = new ArrayList<>();
        WarehouseInventory inventory = new WarehouseInventory();
        availability.forEach((productId, warehouses) -> {
            items.add(new OrderItem(productId, 1, FulfillmentType.DELIVERY, "123 Main St"));
            warehouses.forEach(warehouseId -> inventory.addStock(warehouseId, productId, StockStatus.IN_STOCK));
        });

        List<Shipment> result = splitter.splitOrder(new Order(items), inventory);

        assertEquals(shipmentCount, result.size());
        assertEquals(warehousesUsed, result.stream().map(Shipment::getWarehouseId).collect(Collectors.toSet()));
    }

    @Description("""
        Camera and lens are declared as companions on every row via Order's per-order companion
        groups constructor. Assumption: companion groups are supplied per-order through
        Order(items, companionGroups) rather than WarehouseInventory.addCompanionGroup, since the
        grouping is scoped to the items actually being split; WarehouseInventory also exposes a
        companion-group method, which these tests leave unused. All items are DELIVERY to the
        same address and IN_STOCK wherever listed, isolating companion affinity from the other
        splitting rules.
        """)
    @TableTest("""
        Scenario                                     | Availability                                            | Same Warehouse?
        Shared warehouse chosen when otherwise tied  | "camera:WH1+WH2, lens:WH1+WH2, battery:WH1, widget:WH2" | true
        No shared warehouse possible                 | "camera:WH1, lens:WH2"                                  | false
        Single warehouse suffices for both            | "camera:WH1+WH2, lens:WH1+WH2"                          | true
        """)
    void keepsCompanionsTogetherWhenPossible(Map<String, Set<String>> availability, boolean sameWarehouse) {
        List<OrderItem> items = new ArrayList<>();
        WarehouseInventory inventory = new WarehouseInventory();
        availability.forEach((productId, warehouses) -> {
            items.add(new OrderItem(productId, 1, FulfillmentType.DELIVERY, "123 Main St"));
            warehouses.forEach(warehouseId -> inventory.addStock(warehouseId, productId, StockStatus.IN_STOCK));
        });
        Order order = new Order(items, List.of(List.of("camera", "lens")));

        List<Shipment> result = splitter.splitOrder(order, inventory);

        String cameraWarehouse = warehouseFor(result, "camera");
        String lensWarehouse = warehouseFor(result, "lens");
        assertEquals(sameWarehouse, cameraWarehouse.equals(lensWarehouse));
    }

    private static Set<Set<String>> actualGroups(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> Set.copyOf(shipment.productIds()))
                .collect(Collectors.toSet());
    }

    private static Set<Set<String>> expectedGroups(List<ShipmentGroup> groups) {
        return groups.stream()
                .map(ShipmentGroup::productIds)
                .collect(Collectors.toSet());
    }

    private static Set<AvailabilityGroup> actualAvailabilityGroups(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> new AvailabilityGroup(shipment.availability(), Set.copyOf(shipment.productIds())))
                .collect(Collectors.toSet());
    }

    private static String warehouseFor(List<Shipment> shipments, String productId) {
        return shipments.stream()
                .filter(shipment -> shipment.productIds().contains(productId))
                .map(Shipment::getWarehouseId)
                .findFirst()
                .orElseThrow();
    }

    @TypeConverter
    public static OrderItem parseOrderItem(String value) {
        String[] parts = value.split("/", -1);
        String productId = parts[0];
        FulfillmentType fulfillmentType = FulfillmentType.valueOf(parts[1]);
        String address = parts.length > 2 && !parts[2].isEmpty() ? parts[2] : null;
        return new OrderItem(productId, 1, fulfillmentType, address);
    }

    @TypeConverter
    public static ShipmentGroup parseShipmentGroup(String value) {
        return new ShipmentGroup(Set.of(value.split("\\+")));
    }

    @TypeConverter
    public static AvailabilityGroup parseAvailabilityGroup(String value) {
        String[] parts = value.split(":", 2);
        Availability availability = Availability.valueOf(parts[0]);
        Set<String> productIds = Set.of(parts[1].split("\\+"));
        return new AvailabilityGroup(availability, productIds);
    }

    @TypeConverter
    public static Map<String, Set<String>> parseAvailability(String value) {
        Map<String, Set<String>> result = new java.util.LinkedHashMap<>();
        for (String entry : value.split(",")) {
            String[] parts = entry.trim().split(":");
            result.put(parts[0], Set.of(parts[1].split("\\+")));
        }
        return result;
    }

    private record ShipmentGroup(Set<String> productIds) {
    }

    private record AvailabilityGroup(Availability availability, Set<String> productIds) {
    }
}
