package com.example.orders;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private static final String ONE_WAREHOUSE = "W1";
    private static final String ONE_ADDRESS = "Addr-A";

    private final OrderSplitter splitter = new OrderSplitter();

    @DisplayName("Groups items by fulfillment type and delivery address")
    @Description("""
        An item is written as product/type/address, and a pickup carries no address. Every item is in
        stock at one warehouse, so nothing here turns on stock or warehouse choice. A shipment is a
        set of products and the result a set of shipments, so neither the order of shipments nor the
        order within one is part of the rule.
        """)
    @TableTest("""
        Scenario                       | Items                                              | Shipments?
        Same type and same address     | [camera/DELIVERY/Addr-A, lens/DELIVERY/Addr-A]     | {{camera, lens}}
        Two delivery addresses         | [camera/DELIVERY/Addr-A, watch/DELIVERY/Addr-B]    | {{camera}, {watch}}
        Delivery beside pickup         | [camera/DELIVERY/Addr-A, mug/PICKUP]               | {{camera}, {mug}}
        Two pickups, neither addressed | [mug/PICKUP, candle/PICKUP]                        | {{mug, candle}}
        """)
    void groupsItemsByFulfillmentTypeAndDeliveryAddress(List<OrderItem> items, Set<Set<String>> shipments) {
        List<Shipment> result = splitter.splitOrder(new Order(items), everythingInStock(items));

        assertEquals(shipments, productSetPerShipment(result));
    }

    @DisplayName("Ships available items without waiting for delayed ones")
    @Description("""
        Both items are delivered to one address from one warehouse, so only stock standing varies.
        The model records when a shipment goes out, not how long a delay is, so backordered and
        pre-ordered are one state — the last row is where that reading can be challenged.
        """)
    @TableTest("""
        Scenario                             | Camera Stock | Lens Stock                 | Shipments By Availability?
        Both in stock                        | IN_STOCK     | IN_STOCK                   | [IMMEDIATE: {camera, lens}]
        One in stock, one delayed            | IN_STOCK     | {BACKORDERED, PRE_ORDERED} | [IMMEDIATE: {camera}, WHEN_AVAILABLE: {lens}]
        Delayed for two different reasons    | BACKORDERED  | PRE_ORDERED                | [WHEN_AVAILABLE: {camera, lens}]
        """)
    void shipsAvailableItemsWithoutWaitingForDelayedOnes(
            StockStatus cameraStock,
            StockStatus lensStock,
            Map<Availability, Set<String>> shipmentsByAvailability) {
        List<OrderItem> items = List.of(delivered("camera"), delivered("lens"));
        WarehouseInventory inventory = new WarehouseInventory();
        inventory.addStock(ONE_WAREHOUSE, "camera", cameraStock);
        inventory.addStock(ONE_WAREHOUSE, "lens", lensStock);

        List<Shipment> result = splitter.splitOrder(new Order(items), inventory);

        assertEquals(shipmentsByAvailability, productSetPerAvailability(result));
    }

    @DisplayName("Covers the order from as few warehouses as possible")
    @Description("""
        Every item is delivered to one address and in stock wherever a warehouse lists it, so only
        coverage varies. Each warehouse column is the set of products that warehouse holds.
        """)
    @TableTest("""
        Scenario                            | Order                        | W1 Stock            | W2 Stock            | W3 Stock       | Shipments By Warehouse?
        One warehouse holds the whole order | {camera, lens, mic}          | {camera, lens, mic} | {camera}            | {}             | [W1: {camera, lens, mic}]
        No single warehouse holds it all    | {camera, lens, mic}          | {camera, lens}      | {mic}               | {}             | [W1: {camera, lens}, W2: {mic}]
        Overlapping stock, one still covers | {camera, lens, mic}          | {camera}            | {camera, lens, mic} | {}             | [W2: {camera, lens, mic}]
        Only one pair of warehouses covers  | {camera, lens, mic, battery} | {camera, lens}      | {lens, mic}         | {mic, battery} | [W1: {camera, lens}, W3: {mic, battery}]
        """)
    void coversTheOrderFromAsFewWarehousesAsPossible(
            Set<String> order,
            Set<String> w1Stock,
            Set<String> w2Stock,
            Set<String> w3Stock,
            Map<String, Set<String>> shipmentsByWarehouse) {
        List<OrderItem> items = order.stream().map(OrderSplitterTest::delivered).toList();

        List<Shipment> result = splitter.splitOrder(
                new Order(items), stockedAs(w1Stock, w2Stock, w3Stock));

        assertEquals(shipmentsByWarehouse, productSetPerWarehouse(result));
    }

    @DisplayName("Keeps companion products together when a tie allows it")
    @Description("""
        Companions are declared on the order. Keeping them in one warehouse breaks a tie between
        equally small covers; it never buys an extra shipment, and it gives way when no warehouse
        holds both. Every item is delivered to one address and in stock wherever listed.
        """)
    @TableTest("""
        Scenario                                | Order               | W1 Stock       | W2 Stock      | W3 Stock | Companions     | Shipments By Warehouse?
        Two covers tie, one keeps them together | {camera, lens, mic} | {camera, lens} | {camera, mic} | {lens}   | {camera, lens} | [W1: {camera, lens}, W2: {mic}]
        No warehouse holds both                 | {camera, lens}      | {camera}       | {lens}        | {}       | {camera, lens} | [W1: {camera}, W2: {lens}]
        Already together, nothing to trade      | {camera, lens}      | {camera, lens} | {}            | {}       | {camera, lens} | [W1: {camera, lens}]
        """)
    void keepsCompanionProductsTogetherWhenATieAllowsIt(
            Set<String> order,
            Set<String> w1Stock,
            Set<String> w2Stock,
            Set<String> w3Stock,
            Set<String> companions,
            Map<String, Set<String>> shipmentsByWarehouse) {
        List<OrderItem> items = order.stream().map(OrderSplitterTest::delivered).toList();
        Order withCompanions = new Order(items, List.of(List.copyOf(companions)));

        List<Shipment> result = splitter.splitOrder(
                withCompanions, stockedAs(w1Stock, w2Stock, w3Stock));

        assertEquals(shipmentsByWarehouse, productSetPerWarehouse(result));
    }

    @TypeConverter
    public static OrderItem parseItem(String shorthand) {
        String[] parts = shorthand.split("/");
        FulfillmentType type = FulfillmentType.valueOf(parts[1]);
        return new OrderItem(parts[0], 1, type, parts.length > 2 ? parts[2] : null);
    }

    private static OrderItem delivered(String productId) {
        return new OrderItem(productId, 1, FulfillmentType.DELIVERY, ONE_ADDRESS);
    }

    private static WarehouseInventory everythingInStock(List<OrderItem> items) {
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(item -> inventory.addStock(ONE_WAREHOUSE, item.productId(), StockStatus.IN_STOCK));
        return inventory;
    }

    /**
     * One warehouse column per warehouse keeps the coverage visible down the column, which is what
     * these two tables are read for. A `@TypeConverter` cannot serve them, since it converts a
     * single cell and the inventory spans three; the products listed are all in stock, because
     * stock standing is the availability table's subject.
     */
    private static WarehouseInventory stockedAs(Set<String>... stockPerWarehouse) {
        WarehouseInventory inventory = new WarehouseInventory();
        for (int warehouse = 0; warehouse < stockPerWarehouse.length; warehouse++) {
            String warehouseId = "W" + (warehouse + 1);
            stockPerWarehouse[warehouse]
                    .forEach(productId -> inventory.addStock(warehouseId, productId, StockStatus.IN_STOCK));
        }
        return inventory;
    }

    private static Set<Set<String>> productSetPerShipment(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> (Set<String>) new LinkedHashSet<>(shipment.productIds()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Map<String, Set<String>> productSetPerWarehouse(List<Shipment> shipments) {
        Map<String, Set<String>> byWarehouse = new LinkedHashMap<>();
        shipments.forEach(shipment -> byWarehouse
                .put(shipment.getWarehouseId(), new LinkedHashSet<>(shipment.productIds())));
        return byWarehouse;
    }

    private static Map<Availability, Set<String>> productSetPerAvailability(List<Shipment> shipments) {
        Map<Availability, Set<String>> byAvailability = new LinkedHashMap<>();
        shipments.forEach(shipment -> byAvailability
                .put(shipment.availability(), new LinkedHashSet<>(shipment.productIds())));
        return byAvailability;
    }
}
