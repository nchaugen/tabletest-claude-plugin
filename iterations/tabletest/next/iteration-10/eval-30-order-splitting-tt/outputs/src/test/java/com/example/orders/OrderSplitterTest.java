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
        Inventory holds a single warehouse with every item in stock, so this table isolates the
        fulfillment-type/address partitioning rule from warehouse selection and stock availability.
        """)
    @TableTest("""
        Scenario                                  | Items                                 | Shipment Groups?
        Same fulfillment type and address combine | [A@DELIVERY@addr1, B@DELIVERY@addr1]  | {{A, B}}
        Different fulfillment types separate      | [A@DELIVERY@addr1, B@PICKUP]          | {{A}, {B}}
        Different delivery addresses separate     | [A@DELIVERY@addr1, B@DELIVERY@addr2]  | {{A}, {B}}
        Pickup items combine regardless of address| [A@PICKUP, B@PICKUP@unused]           | {{A, B}}
        """)
    void groupsShipmentsByFulfillmentTypeAndAddress(List<OrderItem> items, Set<Set<String>> shipmentGroups) {
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(item -> inventory.addStock("W1", item.productId(), StockStatus.IN_STOCK));

        List<Shipment> shipments = splitter.splitOrder(new Order(items), inventory);

        assertEquals(shipmentGroups, productIdGroups(shipments));
    }

    @Description("""
        A single warehouse ("W1") stocks every item, and every item is DELIVERY to the same
        address, isolating the ship-immediately-vs-wait rule from warehouse selection and from
        the fulfillment-type/address partitioning rule covered above.
        """)
    @TableTest("""
        Scenario                                    | Stock                          | Immediate Items? | When Available Items?
        All items in stock ship together immediately| [A: IN_STOCK, B: IN_STOCK]      | {A, B}            | {}
        All items backordered ship when available   | [A: BACKORDERED, B: BACKORDERED]| {}                | {A, B}
        All items pre-ordered ship when available    | [A: PRE_ORDERED, B: PRE_ORDERED]| {}                | {A, B}
        In-stock item is not held for a backordered one | [A: IN_STOCK, B: BACKORDERED]| {A}               | {B}
        Backordered and pre-ordered items still combine | [A: BACKORDERED, B: PRE_ORDERED]| {}            | {A, B}
        """)
    void shipsInStockItemsImmediatelyWithoutHoldingForItemsAwaitingStock(
            Map<String, StockStatus> stock, Set<String> immediateItems, Set<String> whenAvailableItems) {
        WarehouseInventory inventory = new WarehouseInventory();
        stock.forEach((productId, status) -> inventory.addStock("W1", productId, status));
        Order order = new Order(orderItemsFor(stock.keySet()));

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(immediateItems, itemsWithAvailability(shipments, Availability.IMMEDIATE));
        assertEquals(whenAvailableItems, itemsWithAvailability(shipments, Availability.WHEN_AVAILABLE));
    }

    @Description("""
        Every item is in stock and DELIVERY to the same address, isolating warehouse-combination
        selection from the availability-splitting and fulfillment-type/address rules covered
        above. "Stock By Warehouse" maps each product to the warehouses that carry it.
        """)
    @TableTest("""
        Scenario                                                  | Stock By Warehouse                     | Shipments By Warehouse?
        Uses the one warehouse that covers everything             | [A: {W1, W2}, B: {W1, W2}, C: {W1}]    | [W1: {A, B, C}]
        Splits only when no warehouse covers every item            | [A: {W1}, B: {W2}]                     | [W1: {A}, W2: {B}]
        Picks the warehouse combination that minimises shipments   | [A: {W1, W2}, B: {W2}, C: {W3}]        | [W2: {A, B}, W3: {C}]
        """)
    void minimisesShipmentsAcrossWarehouseCombinations(
            Map<String, Set<String>> stockByWarehouse, Map<String, Set<String>> shipmentsByWarehouse) {
        WarehouseInventory inventory = stockedInventory(stockByWarehouse);
        Order order = new Order(orderItemsFor(stockByWarehouse.keySet()));

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(shipmentsByWarehouse, groupByWarehouse(shipments));
    }

    @Description("""
        Every item is in stock and DELIVERY to the same address. Companion products are declared
        via WarehouseInventory.addCompanionGroup. Assumes shipment-count minimisation takes
        precedence over companion affinity: companions only break ties among warehouse
        combinations that are already equally minimal.
        """)
    @TableTest("""
        Scenario                                                        | Stock By Warehouse             | Companion Groups | Shipments By Warehouse?
        Breaks a tie in favour of shipping companions together          | [A: {W1, W2}, B: {W1}, C: {W2}]| {{A, C}}         | [W1: {B}, W2: {A, C}]
        Ships companions separately when no warehouse stocks both       | [A: {W1}, B: {W2}]              | {{A, B}}         | [W1: {A}, W2: {B}]
        """)
    void shipsCompanionProductsFromTheSameWarehouseWhenPossible(
            Map<String, Set<String>> stockByWarehouse, Set<Set<String>> companionGroups,
            Map<String, Set<String>> shipmentsByWarehouse) {
        WarehouseInventory inventory = stockedInventory(stockByWarehouse);
        companionGroups.forEach(inventory::addCompanionGroup);
        Order order = new Order(orderItemsFor(stockByWarehouse.keySet()));

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(shipmentsByWarehouse, groupByWarehouse(shipments));
    }

    @TypeConverter
    public static OrderItem parseOrderItem(String spec) {
        String[] parts = spec.split("@");
        FulfillmentType fulfillmentType = FulfillmentType.valueOf(parts[1]);
        String deliveryAddress = parts.length > 2 ? parts[2] : null;
        return new OrderItem(parts[0], 1, fulfillmentType, deliveryAddress);
    }

    private static WarehouseInventory stockedInventory(Map<String, Set<String>> stockByWarehouse) {
        WarehouseInventory inventory = new WarehouseInventory();
        stockByWarehouse.forEach((productId, warehouses) ->
                warehouses.forEach(warehouseId -> inventory.addStock(warehouseId, productId, StockStatus.IN_STOCK)));
        return inventory;
    }

    private static List<OrderItem> orderItemsFor(Set<String> productIds) {
        return productIds.stream()
                .map(productId -> new OrderItem(productId, 1, FulfillmentType.DELIVERY, "addr1"))
                .toList();
    }

    private static Set<Set<String>> productIdGroups(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> (Set<String>) new HashSet<>(shipment.productIds()))
                .collect(Collectors.toSet());
    }

    private static Set<String> itemsWithAvailability(List<Shipment> shipments, Availability availability) {
        return shipments.stream()
                .filter(shipment -> shipment.availability() == availability)
                .flatMap(shipment -> shipment.productIds().stream())
                .collect(Collectors.toSet());
    }

    private static Map<String, Set<String>> groupByWarehouse(List<Shipment> shipments) {
        return shipments.stream()
                .collect(Collectors.toMap(Shipment::getWarehouseId, shipment -> new HashSet<>(shipment.productIds())));
    }
}
