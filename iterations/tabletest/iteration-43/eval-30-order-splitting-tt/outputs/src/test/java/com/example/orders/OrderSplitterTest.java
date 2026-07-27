package com.example.orders;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private final OrderSplitter splitter = new OrderSplitter();

    @Description("""
        All three items are always in stock at a single shared warehouse, so warehouse
        selection cannot interact with this table. deliveryAddress is treated as the
        differentiator for PICKUP items too (the pickup location), since OrderItem
        carries the same field for both fulfillment types.
        """)
    @TableTest("""
        Scenario                                                | Item1              | Item2              | Item3              | Shipments?
        All items share type and address                        | "DELIVERY:addr1"   | "DELIVERY:addr1"   | "DELIVERY:addr1"   | [[P1, P2, P3]]
        Different fulfillment type splits a shipment             | "DELIVERY:addr1"   | "DELIVERY:addr1"   | "PICKUP:addr1"     | [[P1, P2], [P3]]
        Different delivery address splits a shipment             | "DELIVERY:addr1"   | "DELIVERY:addr1"   | "DELIVERY:addr2"   | [[P1, P2], [P3]]
        Type and address both differ, forcing three shipments     | "DELIVERY:addr1"   | "PICKUP:addr1"     | "DELIVERY:addr2"   | [[P1], [P2], [P3]]
        """)
    void groupsShipmentsByFulfillmentTypeAndAddress(FulfillmentSpec item1, FulfillmentSpec item2, FulfillmentSpec item3,
                                                     List<List<String>> shipments) {
        Order order = new Order(List.of(
                new OrderItem("P1", 1, item1.type(), item1.address()),
                new OrderItem("P2", 1, item2.type(), item2.address()),
                new OrderItem("P3", 1, item3.type(), item3.address())
        ));
        WarehouseInventory inventory = allInStockAt("WH1", "P1", "P2", "P3");

        List<Shipment> result = splitter.splitOrder(order, inventory);

        assertEquals(shipments, normalizedProductGroups(result));
    }

    @Description("""
        Items A and B share the same fulfillment type, address and warehouse, so only
        stock status varies. Isolates the "don't hold in-stock items" rule from
        segmentation and warehouse selection.
        """)
    @TableTest("""
        Scenario                             | Item A Status              | Item B Status              | Immediate? | When Available?
        Both items in stock                   | IN_STOCK                    | IN_STOCK                    | [A, B]     | []
        Both items awaiting stock              | {BACKORDERED, PRE_ORDERED}  | {BACKORDERED, PRE_ORDERED}  | []         | [A, B]
        In-stock item ships without waiting    | IN_STOCK                    | {BACKORDERED, PRE_ORDERED}  | [A]        | [B]
        """)
    void doesNotHoldInStockItemsForItemsAwaitingStock(StockStatus itemAStatus, StockStatus itemBStatus,
                                                       List<String> immediate, List<String> whenAvailable) {
        Order order = deliveryOrderFor("A", "B");
        WarehouseInventory inventory = new WarehouseInventory();
        inventory.addStock("WH1", "A", itemAStatus);
        inventory.addStock("WH1", "B", itemBStatus);

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(immediate, productIdsWithAvailability(shipments, Availability.IMMEDIATE));
        assertEquals(whenAvailable, productIdsWithAvailability(shipments, Availability.WHEN_AVAILABLE));
    }

    @Description("""
        Items A, B and C share fulfillment type and address and are always in stock,
        isolating warehouse-combination minimisation from segmentation and availability.
        Where more than one warehouse combination achieves the minimum, the exact
        combination is not asserted here (see determinesWarehousesUsedWhenCombinationIsForced
        for the subset of scenarios where the combination is uniquely determined).
        """)
    @TableTest("""
        Scenario                                                     | A Warehouses | B Warehouses | C Warehouses | Shipment Count?
        Single warehouse stocks every item                           | [WH1]        | [WH1]        | [WH1]        | 1
        No warehouse overlap forces one shipment per item             | [WH1]        | [WH2]        | [WH3]        | 3
        Shared warehouse beats splitting when one item has a choice   | [WH1, WH2]   | [WH2]        | [WH2]        | 1
        Two single-warehouse items force two shipments                | [WH1, WH2]   | [WH1]        | [WH2]        | 2
        Pairwise overlap still covers the order in two shipments      | [WH1, WH2]   | [WH2, WH3]   | [WH1, WH3]   | 2
        """)
    void minimisesShipmentCountAcrossWarehouseChoices(List<String> warehousesForA, List<String> warehousesForB,
                                                       List<String> warehousesForC, int shipmentCount) {
        Order order = deliveryOrderFor("A", "B", "C");
        WarehouseInventory inventory = inventoryFrom(Map.of(
                "A", warehousesForA, "B", warehousesForB, "C", warehousesForC));

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(shipmentCount, shipments.size());
    }

    @Description("""
        Same setup as minimisesShipmentCountAcrossWarehouseChoices, restricted to scenarios
        where availability leaves only one possible warehouse combination, so the exact
        set of warehouses used is deterministic.
        """)
    @TableTest("""
        Scenario                                           | A Warehouses | B Warehouses | C Warehouses | Warehouses Used?
        Single shared warehouse is the only option          | [WH1]        | [WH1]        | [WH1]        | {WH1}
        Disjoint availability forces all three warehouses   | [WH1]        | [WH2]        | [WH3]        | {WH1, WH2, WH3}
        Both single-warehouse items force their warehouses  | [WH1, WH2]   | [WH1]        | [WH2]        | {WH1, WH2}
        """)
    void determinesWarehousesUsedWhenCombinationIsForced(List<String> warehousesForA, List<String> warehousesForB,
                                                          List<String> warehousesForC, Set<String> warehousesUsed) {
        Order order = deliveryOrderFor("A", "B", "C");
        WarehouseInventory inventory = inventoryFrom(Map.of(
                "A", warehousesForA, "B", warehousesForB, "C", warehousesForC));

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(warehousesUsed, shipments.stream().map(Shipment::getWarehouseId).collect(Collectors.toSet()));
    }

    @Description("""
        Camera and Lens are declared as a companion group; Other is an unrelated item used
        to force a particular warehouse into the mix. Companion grouping is assumed to be a
        tie-breaker among equally-minimal warehouse combinations, not an override of the
        minimisation rule. Open question: the domain model exposes companion groups on both
        Order and WarehouseInventory without clarifying which is authoritative, so both are
        populated here.
        """)
    @TableTest("""
        Scenario                                                      | Camera Warehouses | Lens Warehouses | Other Warehouses | Shipment Count? | Companions Share Warehouse?
        Tied minimal combinations favour keeping companions together   | [WH1, WH2]        | [WH2, WH3]      | [WH1]            | 2                | true
        No shared warehouse exists, so companions ship separately      | [WH1]             | [WH2]           | [WH1, WH2]       | 2                | false
        """)
    void groupsCompanionProductsOnTheSameWarehouseWhenPossible(List<String> cameraWarehouses, List<String> lensWarehouses,
                                                                List<String> otherWarehouses, int shipmentCount,
                                                                boolean companionsShareWarehouse) {
        Order order = new Order(
                List.of(
                        new OrderItem("Camera", 1, FulfillmentType.DELIVERY, "addr1"),
                        new OrderItem("Lens", 1, FulfillmentType.DELIVERY, "addr1"),
                        new OrderItem("Other", 1, FulfillmentType.DELIVERY, "addr1")
                ),
                List.of(List.of("Camera", "Lens"))
        );
        WarehouseInventory inventory = inventoryFrom(Map.of(
                "Camera", cameraWarehouses, "Lens", lensWarehouses, "Other", otherWarehouses));
        inventory.addCompanionGroup(Set.of("Camera", "Lens"));

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(shipmentCount, shipments.size());
        assertEquals(companionsShareWarehouse, warehouseFor(shipments, "Camera").equals(warehouseFor(shipments, "Lens")));
    }

    @TypeConverter
    public static FulfillmentSpec parseFulfillmentSpec(String value) {
        String[] parts = value.split(":", 2);
        return new FulfillmentSpec(FulfillmentType.valueOf(parts[0]), parts[1]);
    }

    private record FulfillmentSpec(FulfillmentType type, String address) {
    }

    private static Order deliveryOrderFor(String... productIds) {
        List<OrderItem> items = List.of(productIds).stream()
                .map(id -> new OrderItem(id, 1, FulfillmentType.DELIVERY, "addr1"))
                .toList();
        return new Order(items);
    }

    private static WarehouseInventory allInStockAt(String warehouseId, String... productIds) {
        WarehouseInventory inventory = new WarehouseInventory();
        for (String productId : productIds) {
            inventory.addStock(warehouseId, productId, StockStatus.IN_STOCK);
        }
        return inventory;
    }

    private static WarehouseInventory inventoryFrom(Map<String, List<String>> warehousesByProduct) {
        WarehouseInventory inventory = new WarehouseInventory();
        warehousesByProduct.forEach((productId, warehouses) ->
                warehouses.forEach(warehouseId -> inventory.addStock(warehouseId, productId, StockStatus.IN_STOCK)));
        return inventory;
    }

    private static List<List<String>> normalizedProductGroups(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> shipment.productIds().stream().sorted().toList())
                .sorted(Comparator.comparing(group -> group.get(0)))
                .toList();
    }

    private static List<String> productIdsWithAvailability(List<Shipment> shipments, Availability availability) {
        return shipments.stream()
                .filter(shipment -> shipment.getAvailability() == availability)
                .flatMap(shipment -> shipment.productIds().stream())
                .sorted()
                .toList();
    }

    private static String warehouseFor(List<Shipment> shipments, String productId) {
        return shipments.stream()
                .filter(shipment -> shipment.productIds().contains(productId))
                .map(Shipment::getWarehouseId)
                .findFirst()
                .orElseThrow();
    }
}
