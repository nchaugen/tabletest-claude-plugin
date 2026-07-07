package com.example.orders;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    @TableTest("""
        Scenario                                        | Items                                                                    | Shipments?
        Same type and address ship together             | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St]                     | [W1/IMMEDIATE=camera+lens]
        Different fulfillment types split                | [camera@DELIVERY@12 Elm St, battery@PICKUP@-]                            | [W1/IMMEDIATE=battery, W1/IMMEDIATE=camera]
        Different delivery addresses split               | [camera@DELIVERY@12 Elm St, lens@DELIVERY@88 Oak Ave]                    | [W1/IMMEDIATE=camera, W1/IMMEDIATE=lens]
        Pickup items group together despite no address   | [camera@PICKUP@-, lens@PICKUP@-]                                         | [W1/IMMEDIATE=camera+lens]
        Matching items still grouped alongside a split    | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St, battery@PICKUP@-]  | [W1/IMMEDIATE=battery, W1/IMMEDIATE=camera+lens]
        """)
    void groupsItemsByFulfillmentTypeAndDeliveryAddress(List<OrderItem> items, List<String> shipments) {
        Order order = new Order(items);
        WarehouseInventory inventory = singleWarehouseAllInStock(items, "W1");

        List<Shipment> result = new OrderSplitter().splitOrder(order, inventory);

        assertEquals(Set.copyOf(shipments), describeAll(result));
    }

    @Description("""
        All items are DELIVERY to the same address, held in a single warehouse (W1),
        so fulfillment-type/address grouping and warehouse selection do not interfere.
        Assumes backordered and pre-ordered items are indistinguishable "ship when
        available" and are grouped into the same shipment.
        """)
    @TableTest("""
        Scenario                                              | Items                                                | Stock                                        | Shipments?
        All items in stock ship together immediately          | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St] | [W1@camera@IN_STOCK, W1@lens@IN_STOCK]       | [W1/IMMEDIATE=camera+lens]
        In-stock item ships without waiting for backorder      | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St] | [W1@camera@IN_STOCK, W1@lens@BACKORDERED]    | [W1/IMMEDIATE=camera, W1/WHEN_AVAILABLE=lens]
        In-stock item ships without waiting for pre-order       | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St] | [W1@camera@IN_STOCK, W1@lens@PRE_ORDERED]    | [W1/IMMEDIATE=camera, W1/WHEN_AVAILABLE=lens]
        Backordered items ship together when available          | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St] | [W1@camera@BACKORDERED, W1@lens@BACKORDERED] | [W1/WHEN_AVAILABLE=camera+lens]
        Backordered and pre-ordered items ship together         | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St] | [W1@camera@BACKORDERED, W1@lens@PRE_ORDERED] | [W1/WHEN_AVAILABLE=camera+lens]
        """)
    void shipsInStockItemsImmediatelyWithoutHoldingForBackorderedItems(
            List<OrderItem> items, List<StockEntry> stock, List<String> shipments) {
        Order order = new Order(items);
        WarehouseInventory inventory = buildInventory(stock);

        List<Shipment> result = new OrderSplitter().splitOrder(order, inventory);

        assertEquals(Set.copyOf(shipments), describeAll(result));
    }

    @Description("""
        All items are DELIVERY to the same address and IN_STOCK, isolating warehouse
        selection from fulfillment-type/address grouping and stock-availability grouping.
        """)
    @TableTest("""
        Scenario                                                    | Items                                                                    | Warehouse Stock                                                                          | Shipments?
        Single warehouse covers all items                           | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St]                     | [W1@camera@IN_STOCK, W1@lens@IN_STOCK, W2@camera@IN_STOCK]                               | [W1/IMMEDIATE=camera+lens]
        No warehouse covers all items                               | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St]                     | [W1@camera@IN_STOCK, W2@lens@IN_STOCK]                                                   | [W1/IMMEDIATE=camera, W2/IMMEDIATE=lens]
        Two warehouses needed regardless of extra overlap           | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St, battery@DELIVERY@12 Elm St] | [W1@camera@IN_STOCK, W1@lens@IN_STOCK, W2@battery@IN_STOCK]                              | [W1/IMMEDIATE=camera+lens, W2/IMMEDIATE=battery]
        Warehouse covering everything beats splitting across two    | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St, battery@DELIVERY@12 Elm St] | [W1@camera@IN_STOCK, W1@lens@IN_STOCK, W2@camera@IN_STOCK, W2@lens@IN_STOCK, W2@battery@IN_STOCK] | [W2/IMMEDIATE=battery+camera+lens]
        """)
    void minimisesShipmentCountAcrossWarehouses(
            List<OrderItem> items, List<StockEntry> warehouseStock, List<String> shipments) {
        Order order = new Order(items);
        WarehouseInventory inventory = buildInventory(warehouseStock);

        List<Shipment> result = new OrderSplitter().splitOrder(order, inventory);

        assertEquals(Set.copyOf(shipments), describeAll(result));
    }

    @Description("""
        All items are DELIVERY to the same address and IN_STOCK unless noted, isolating
        companion handling from the other splitting rules. Companion preference only
        breaks ties between equally-minimal warehouse combinations; it never increases
        the total shipment count.
        """)
    @TableTest("""
        Scenario                                                  | Items                                                                            | Companions              | Stock                                                                        | Shipments?
        Companions grouped together when warehouse choice is tied | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St, battery@DELIVERY@12 Elm St] | [camera+lens]           | [W1@camera@IN_STOCK, W1@lens@IN_STOCK, W2@lens@IN_STOCK, W2@battery@IN_STOCK] | [W1/IMMEDIATE=camera+lens, W2/IMMEDIATE=battery]
        Companions split when no warehouse holds both             | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St]                             | [camera+lens]           | [W1@camera@IN_STOCK, W2@lens@IN_STOCK]                                       | [W1/IMMEDIATE=camera, W2/IMMEDIATE=lens]
        Multiple companion groups kept together independently     | [camera@DELIVERY@12 Elm St, lens@DELIVERY@12 Elm St, battery@DELIVERY@12 Elm St, charger@DELIVERY@12 Elm St] | [camera+lens, battery+charger] | [W1@camera@IN_STOCK, W1@lens@IN_STOCK, W2@camera@IN_STOCK, W2@battery@IN_STOCK, W2@charger@IN_STOCK] | [W1/IMMEDIATE=camera+lens, W2/IMMEDIATE=battery+charger]
        """)
    void prefersShippingCompanionProductsFromTheSameWarehouseWhenPossible(
            List<OrderItem> items, List<CompanionGroup> companions, List<StockEntry> stock, List<String> shipments) {
        Order order = new Order(items, companions.stream().map(CompanionGroup::productIds).toList());
        WarehouseInventory inventory = buildInventory(stock);

        List<Shipment> result = new OrderSplitter().splitOrder(order, inventory);

        assertEquals(Set.copyOf(shipments), describeAll(result));
    }

    @TypeConverter
    public static OrderItem parseOrderItem(String spec) {
        String[] parts = spec.split("@", -1);
        String productId = parts[0];
        FulfillmentType fulfillmentType = FulfillmentType.valueOf(parts[1]);
        String deliveryAddress = parts[2].equals("-") ? null : parts[2];
        return new OrderItem(productId, 1, fulfillmentType, deliveryAddress);
    }

    @TypeConverter
    public static StockEntry parseStockEntry(String spec) {
        String[] parts = spec.split("@", -1);
        return new StockEntry(parts[0], parts[1], StockStatus.valueOf(parts[2]));
    }

    @TypeConverter
    public static CompanionGroup parseCompanionGroup(String spec) {
        return new CompanionGroup(List.of(spec.split("\\+")));
    }

    private static WarehouseInventory singleWarehouseAllInStock(List<OrderItem> items, String warehouseId) {
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(item -> inventory.addStock(warehouseId, item.getProductId(), StockStatus.IN_STOCK));
        return inventory;
    }

    private static WarehouseInventory buildInventory(List<StockEntry> entries) {
        WarehouseInventory inventory = new WarehouseInventory();
        entries.forEach(entry -> inventory.addStock(entry.warehouseId(), entry.productId(), entry.status()));
        return inventory;
    }

    private static Set<String> describeAll(List<Shipment> shipments) {
        return shipments.stream().map(OrderSplitterTest::describeShipment).collect(Collectors.toSet());
    }

    private static String describeShipment(Shipment shipment) {
        String productIds = shipment.productIds().stream().sorted().collect(Collectors.joining("+"));
        return shipment.getWarehouseId() + "/" + shipment.getAvailability() + "=" + productIds;
    }

    public record StockEntry(String warehouseId, String productId, StockStatus status) {
    }

    public record CompanionGroup(List<String> productIds) {
    }
}
