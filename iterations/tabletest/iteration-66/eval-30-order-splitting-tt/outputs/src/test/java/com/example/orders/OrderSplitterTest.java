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

    @Description("""
        All items are in stock at a single warehouse, so grouping is driven only by fulfillment
        type and delivery address. Stock timing and warehouse selection are covered separately.
        """)
    @TableTest("""
        Scenario                         | Items                                                                                                                                               | Shipments?
        Same address, same type          | [[productId: p1, type: DELIVERY, address: addr1], [productId: p2, type: DELIVERY, address: addr1]]                                                  | {{p1, p2}}
        Different delivery addresses     | [[productId: p1, type: DELIVERY, address: addr1], [productId: p2, type: DELIVERY, address: addr2]]                                                  | {{p1}, {p2}}
        Delivery and pickup mixed        | [[productId: p1, type: DELIVERY, address: addr1], [productId: p2, type: PICKUP]]                                                                    | {{p1}, {p2}}
        Both pickup                      | [[productId: p1, type: PICKUP], [productId: p2, type: PICKUP]]                                                                                      | {{p1, p2}}
        Three items across two addresses | [[productId: p1, type: DELIVERY, address: addr1], [productId: p2, type: DELIVERY, address: addr1], [productId: p3, type: DELIVERY, address: addr2]] | {{p1, p2}, {p3}}
        """)
    void groupsItemsByFulfillmentTypeAndDeliveryAddress(List<OrderItem> items, Set<Set<String>> shipments) {
        Order order = new Order(items);
        WarehouseInventory inventory = allInStock(items, "W1");

        List<Shipment> result = new OrderSplitter().splitOrder(order, inventory);

        assertEquals(shipments, shipmentGroups(result));
    }

    @Description("""
        Both items are DELIVERY to the same address at a single warehouse, so grouping is driven
        only by stock availability. Fulfillment-type/address grouping is covered separately.
        """)
    @TableTest("""
        Scenario                                         | Item 1 Stock               | Item 2 Stock               | Immediate Shipment? | When-Available Shipment?
        Both items in stock ship together immediately    | IN_STOCK                   | IN_STOCK                   | {p1, p2}            |
        Non-immediate items ship together when available | {BACKORDERED, PRE_ORDERED} | {BACKORDERED, PRE_ORDERED} |                     | {p1, p2}
        In-stock item ships without waiting on the other | IN_STOCK                   | {BACKORDERED, PRE_ORDERED} | {p1}                |
        """)
    void doesNotHoldInStockItemsForItemsAwaitingAvailability(StockStatus item1Stock, StockStatus item2Stock,
                                                              Set<String> immediateShipment, Set<String> whenAvailableShipment) {
        List<OrderItem> items = List.of(
                new OrderItem("p1", 1, FulfillmentType.DELIVERY, "addr1"),
                new OrderItem("p2", 1, FulfillmentType.DELIVERY, "addr1"));
        Order order = new Order(items);
        WarehouseInventory inventory = new WarehouseInventory();
        inventory.addStock("W1", "p1", item1Stock);
        inventory.addStock("W1", "p2", item2Stock);

        List<Shipment> result = new OrderSplitter().splitOrder(order, inventory);

        assertEquals(immediateShipment, productIdsWithAvailability(result, Availability.IMMEDIATE));
        assertEquals(whenAvailableShipment, productIdsWithAvailability(result, Availability.WHEN_AVAILABLE));
    }

    @Description("""
        All items are DELIVERY to the same address and in stock wherever listed; only warehouse
        coverage varies. Fulfillment/address grouping and stock timing are covered separately.
        """)
    @TableTest("""
        Scenario                                                           | Products         | Warehouse Stock                                                      | Shipments By Warehouse?
        Chooses the warehouse that covers the whole order                  | [p1, p2, p3]     | [W1: [p1: IN_STOCK, p2: IN_STOCK, p3: IN_STOCK], W2: [p1: IN_STOCK]] | [W1: {p1, p2, p3}]
        Splits across the minimal set of warehouses when none covers alone | [p1, p2, p3, p4] | [W1: [p1: IN_STOCK, p2: IN_STOCK], W2: [p3: IN_STOCK, p4: IN_STOCK]] | [W1: {p1, p2}, W2: {p3, p4}]
        """)
    void choosesTheWarehouseCombinationWithFewestShipments(List<String> products, WarehouseInventory inventory,
                                                             Map<String, Set<String>> shipmentsByWarehouse) {
        List<OrderItem> items = products.stream()
                .map(productId -> new OrderItem(productId, 1, FulfillmentType.DELIVERY, "addr1"))
                .toList();
        Order order = new Order(items);

        List<Shipment> result = new OrderSplitter().splitOrder(order, inventory);

        assertEquals(shipmentsByWarehouse, shipmentsByWarehouseId(result));
    }

    @Description("""
        Camera body (cb) and lens are declared as a companion group on the order. Both are
        DELIVERY and in stock unless a row states otherwise.
        """)
    @TableTest("""
        Scenario                                                           | Warehouse Stock                                          | Camera Address | Lens Address | Shipments?
        Companions ship together when a common warehouse stocks both       | [W1: [cb: IN_STOCK, lens: IN_STOCK], W2: [cb: IN_STOCK]] | addr1          | addr1        | {{cb, lens}}
        Companions ship separately when no warehouse stocks both           | [W1: [cb: IN_STOCK], W2: [lens: IN_STOCK]]               | addr1          | addr1        | {{cb}, {lens}}
        Different delivery addresses split companions despite shared stock | [W1: [cb: IN_STOCK, lens: IN_STOCK]]                     | addr1          | addr2        | {{cb}, {lens}}
        Different availability splits companions despite shared warehouse  | [W1: [cb: IN_STOCK, lens: BACKORDERED]]                  | addr1          | addr1        | {{cb}, {lens}}
        """)
    void shipsCompanionProductsFromTheSameWarehouseWhenPossible(WarehouseInventory inventory, String cameraAddress,
                                                                  String lensAddress, Set<Set<String>> shipments) {
        List<OrderItem> items = List.of(
                new OrderItem("cb", 1, FulfillmentType.DELIVERY, cameraAddress),
                new OrderItem("lens", 1, FulfillmentType.DELIVERY, lensAddress));
        Order order = new Order(items, List.of(List.of("cb", "lens")));

        List<Shipment> result = new OrderSplitter().splitOrder(order, inventory);

        assertEquals(shipments, shipmentGroups(result));
    }

    private static WarehouseInventory allInStock(List<OrderItem> items, String warehouseId) {
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(item -> inventory.addStock(warehouseId, item.productId(), StockStatus.IN_STOCK));
        return inventory;
    }

    private static Set<Set<String>> shipmentGroups(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> new HashSet<>(shipment.productIds()))
                .collect(Collectors.toSet());
    }

    private static Map<String, Set<String>> shipmentsByWarehouseId(List<Shipment> shipments) {
        return shipments.stream()
                .collect(Collectors.toMap(Shipment::getWarehouseId, shipment -> new HashSet<>(shipment.productIds())));
    }

    private static Set<String> productIdsWithAvailability(List<Shipment> shipments, Availability availability) {
        Set<String> productIds = shipments.stream()
                .filter(shipment -> shipment.availability() == availability)
                .flatMap(shipment -> shipment.productIds().stream())
                .collect(Collectors.toSet());
        return productIds.isEmpty() ? null : productIds;
    }

    @TypeConverter
    public static OrderItem toOrderItem(Map<String, String> fields) {
        return new OrderItem(
                fields.get("productId"),
                1,
                FulfillmentType.valueOf(fields.get("type")),
                fields.get("address"));
    }

    @TypeConverter
    public static WarehouseInventory toInventory(Map<String, Map<String, String>> stockByWarehouse) {
        WarehouseInventory inventory = new WarehouseInventory();
        stockByWarehouse.forEach((warehouseId, stock) ->
                stock.forEach((productId, status) ->
                        inventory.addStock(warehouseId, productId, StockStatus.valueOf(status))));
        return inventory;
    }
}
