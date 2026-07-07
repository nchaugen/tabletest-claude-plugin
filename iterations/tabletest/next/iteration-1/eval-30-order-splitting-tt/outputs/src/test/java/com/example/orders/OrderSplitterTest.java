package com.example.orders;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private final OrderSplitter splitter = new OrderSplitter();

    @Description("""
        Fixed for all rows: every item is in stock at a single warehouse (wh1). This isolates
        the fulfillment-type/address partitioning rule from the availability and warehouse-selection
        concerns covered in the tables below. Pickup items carry no delivery address ('-' denotes no address).
        """)
    @TableTest("""
        Scenario                              | Items                                               | Expected Shipments?
        Same fulfillment and address merge    | [P1/DELIVERY/AddrA, P2/DELIVERY/AddrA]               | [{P1, P2}]
        Different fulfillment types split     | [P1/DELIVERY/AddrA, P2/PICKUP/-]                     | [{P1}, {P2}]
        Different delivery addresses split    | [P1/DELIVERY/AddrA, P2/DELIVERY/AddrB]               | [{P1}, {P2}]
        Pickup items share one shipment       | [P1/PICKUP/-, P2/PICKUP/-]                           | [{P1, P2}]
        Three-way split across all boundaries | [P1/DELIVERY/AddrA, P2/DELIVERY/AddrB, P3/PICKUP/-]  | [{P1}, {P2}, {P3}]
        """)
    void splitsByFulfillmentTypeAndAddress(List<OrderItem> items, List<Set<String>> expectedShipments) {
        WarehouseInventory inventory = new WarehouseInventory();
        for (OrderItem item : items) {
            inventory.addStock("wh1", item.productId(), StockStatus.IN_STOCK);
        }
        Order order = new Order(items);

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(new HashSet<>(expectedShipments), productIdGroups(shipments));
    }

    @Description("""
        Fixed for all rows: DELIVERY to a single address, single warehouse (wh1) — isolates
        availability-driven splitting from the partitioning and warehouse-selection concerns
        in the other tables.
        """)
    @TableTest("""
        Scenario                                          | Products     | Stock                                             | Immediate? | When Available?
        All items in stock ship together                  | [P1, P2]     | [P1: IN_STOCK, P2: IN_STOCK]                      | {P1, P2}   |
        Backordered and pre-ordered items merge            | [P1, P2]     | [P1: BACKORDERED, P2: PRE_ORDERED]                |            | {P1, P2}
        In-stock item ships without waiting for backorder  | [P1, P2]     | [P1: IN_STOCK, P2: BACKORDERED]                   | {P1}       | {P2}
        In-stock item ships without waiting for pre-order  | [P1, P2]     | [P1: IN_STOCK, P2: PRE_ORDERED]                   | {P1}       | {P2}
        In-stock ships while two unavailable items wait    | [P1, P2, P3] | [P1: IN_STOCK, P2: BACKORDERED, P3: PRE_ORDERED]  | {P1}       | {P2, P3}
        """)
    void shipsInStockItemsWithoutWaitingForUnavailableOnes(List<String> products, Map<String, StockStatus> stock,
                                                            Set<String> expectedImmediate, Set<String> expectedWhenAvailable) {
        List<OrderItem> items = deliveryItemsTo("AddrA", products);
        WarehouseInventory inventory = new WarehouseInventory();
        stock.forEach((productId, status) -> inventory.addStock("wh1", productId, status));
        Order order = new Order(items);

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(orEmpty(expectedImmediate), productIdsWithAvailability(shipments, Availability.IMMEDIATE));
        assertEquals(orEmpty(expectedWhenAvailable), productIdsWithAvailability(shipments, Availability.WHEN_AVAILABLE));
    }

    @Description("""
        Fixed for all rows: DELIVERY to a single address, all stock IN_STOCK — isolates
        warehouse-selection from the partitioning and availability concerns above. Only the
        resulting shipment count is asserted: when several warehouse combinations tie for fewest
        shipments, this suite does not prescribe which one wins (the one tie-break rule that is
        specified — companion affinity — is covered in the table below).
        """)
    @TableTest("""
        Scenario                                                | Products     | Stock By Warehouse                                        | Expected Shipment Count?
        Single warehouse covers the whole order                 | [P1, P2]     | [wh1: "P1:IN_STOCK,P2:IN_STOCK", wh2: "P1:IN_STOCK"]      | 1
        No single warehouse covers everything, two are needed   | [P1, P2, P3] | [wh1: "P1:IN_STOCK,P2:IN_STOCK", wh2: "P3:IN_STOCK"]      | 2
        Overlapping pairs still combine into two shipments       | [P1, P2, P3] | [wh1: "P1:IN_STOCK,P2:IN_STOCK", wh2: "P2:IN_STOCK,P3:IN_STOCK"] | 2
        No pair covers everything, three warehouses are needed  | [P1, P2, P3] | [wh1: "P1:IN_STOCK", wh2: "P2:IN_STOCK", wh3: "P3:IN_STOCK"] | 3
        """)
    void minimizesShipmentCountAcrossWarehouses(List<String> products, WarehouseInventory inventory, int expectedShipmentCount) {
        List<OrderItem> items = deliveryItemsTo("AddrA", products);
        Order order = new Order(items);

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(expectedShipmentCount, shipments.size());
    }

    @Description("""
        Fixed for all rows: DELIVERY to a single address, all stock IN_STOCK. Companion groups are
        modeled via WarehouseInventory#addCompanionGroup — Order also exposes a companionGroups
        constructor parameter, but this suite treats WarehouseInventory as the source of truth since
        companion affinity reads as a catalog/fulfillment property rather than an order-specific one;
        this is a stated assumption, not a confirmed contract. Companion grouping is a tie-break among
        equally minimal warehouse combinations — it never increases the shipment count from the
        minimizesShipmentCountAcrossWarehouses table above.
        """)
    @TableTest("""
        Scenario                                                          | Products     | Companions   | Stock By Warehouse                                              | Expected Shipment Count? | Companions Same Shipment?
        Companion ships with its pair when it costs no extra shipments    | [P1, P2, P3] | {P1, P2}     | [wh1: "P1:IN_STOCK,P2:IN_STOCK", wh2: "P2:IN_STOCK,P3:IN_STOCK"] | 2                         | true
        Companions ship separately when no warehouse carries both         | [P1, P2]     | {P1, P2}     | [wh1: "P1:IN_STOCK", wh2: "P2:IN_STOCK"]                         | 2                         | false
        Companion group splits when one member shares no warehouse        | [P1, P2, P3] | {P1, P2, P3} | [wh1: "P1:IN_STOCK", wh2: "P2:IN_STOCK,P3:IN_STOCK"]             | 2                         | false
        """)
    void keepsCompanionsTogetherWhenPossible(List<String> products, Set<String> companions, WarehouseInventory inventory,
                                              int expectedShipmentCount, boolean companionsSameShipment) {
        inventory.addCompanionGroup(companions);
        List<OrderItem> items = deliveryItemsTo("AddrA", products);
        Order order = new Order(items);

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(expectedShipmentCount, shipments.size());
        assertEquals(companionsSameShipment, allInOneShipment(shipments, companions));
    }

    @TypeConverter
    public static OrderItem parseOrderItem(String value) {
        String[] parts = value.split("/", -1);
        String address = parts[2].equals("-") ? null : parts[2];
        return new OrderItem(parts[0], 1, FulfillmentType.valueOf(parts[1]), address);
    }

    @TypeConverter
    public static WarehouseInventory parseInventory(Map<String, String> stockByWarehouse) {
        WarehouseInventory inventory = new WarehouseInventory();
        stockByWarehouse.forEach((warehouseId, stock) -> {
            for (String entry : stock.split(",")) {
                String[] parts = entry.split(":");
                inventory.addStock(warehouseId, parts[0], StockStatus.valueOf(parts[1]));
            }
        });
        return inventory;
    }

    private static List<OrderItem> deliveryItemsTo(String address, List<String> productIds) {
        return productIds.stream()
                .map(productId -> new OrderItem(productId, 1, FulfillmentType.DELIVERY, address))
                .toList();
    }

    private static Set<Set<String>> productIdGroups(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> new HashSet<>(shipment.productIds()))
                .collect(Collectors.toSet());
    }

    private static Set<String> productIdsWithAvailability(List<Shipment> shipments, Availability availability) {
        return shipments.stream()
                .filter(shipment -> shipment.availability() == availability)
                .flatMap(shipment -> shipment.productIds().stream())
                .collect(Collectors.toSet());
    }

    private static boolean allInOneShipment(List<Shipment> shipments, Set<String> productIds) {
        return shipments.stream()
                .anyMatch(shipment -> new HashSet<>(shipment.productIds()).containsAll(productIds));
    }

    private static Set<String> orEmpty(Set<String> set) {
        return set == null ? Set.of() : set;
    }
}
