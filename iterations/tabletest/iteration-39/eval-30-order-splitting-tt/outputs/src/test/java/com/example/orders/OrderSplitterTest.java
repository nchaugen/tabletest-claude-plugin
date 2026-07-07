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
        Scenario                                              | Items                                                                            | Shipments?
        Same fulfillment type and address                     | ["camera:DELIVERY:Addr-A", "lens:DELIVERY:Addr-A"]                               | ["camera,lens"]
        Same type, different delivery addresses                | ["camera:DELIVERY:Addr-A", "watch:DELIVERY:Addr-B"]                              | [camera, watch]
        Delivery and pickup items always split                | ["camera:DELIVERY:Addr-A", "mug:PICKUP:"]                                        | [camera, mug]
        Two pickup items with no delivery address share a shipment | ["mug:PICKUP:", "candle:PICKUP:"]                                           | ["candle,mug"]
        Items sharing an address group together, others split | ["camera:DELIVERY:Addr-A", "lens:DELIVERY:Addr-A", "watch:DELIVERY:Addr-B"]      | ["camera,lens", watch]
        Delivery split by address, plus a separate pickup item | ["camera:DELIVERY:Addr-A", "watch:DELIVERY:Addr-B", "mug:PICKUP:"]              | [camera, mug, watch]
        """)
    void splitsShipmentsByFulfillmentTypeAndAddress(List<OrderItem> items, List<String> expectedShipments) {
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(item -> inventory.addStock("W1", item.getProductId(), StockStatus.IN_STOCK));

        List<Shipment> shipments = new OrderSplitter().splitOrder(new Order(items), inventory);

        assertEquals(expectedShipments, describeByItems(shipments));
    }

    @Description("""
        Fulfillment type and delivery address are fixed (DELIVERY to Addr-A) so only stock
        status varies, isolating the availability rule from the other splitting rules.
        BACKORDERED and PRE_ORDERED both ship WHEN_AVAILABLE: the domain model tracks only
        two availability states, so items in either status combine into one shipment once
        the other facets match - there is no separate "ready at different times" split.
        """)
    @TableTest("""
        Scenario                                             | Items                                              | Shipments?
        All items in stock                                   | ["camera:IN_STOCK", "lens:IN_STOCK"]               | ["IMMEDIATE:[camera,lens]"]
        In-stock item not held for a backordered item        | ["camera:IN_STOCK", "lens:BACKORDERED"]            | ["IMMEDIATE:[camera]", "WHEN_AVAILABLE:[lens]"]
        In-stock item not held for a pre-ordered item         | ["camera:IN_STOCK", "lens:PRE_ORDERED"]            | ["IMMEDIATE:[camera]", "WHEN_AVAILABLE:[lens]"]
        Two backordered items ship together                  | ["camera:BACKORDERED", "lens:BACKORDERED"]         | ["WHEN_AVAILABLE:[camera,lens]"]
        Backordered and pre-ordered items combine             | ["camera:BACKORDERED", "lens:PRE_ORDERED"]         | ["WHEN_AVAILABLE:[camera,lens]"]
        In-stock, backordered, and pre-ordered items together | ["camera:IN_STOCK", "lens:BACKORDERED", "mic:PRE_ORDERED"] | ["IMMEDIATE:[camera]", "WHEN_AVAILABLE:[lens,mic]"]
        """)
    void splitsShipmentsByStockAvailability(List<StockedItem> items, List<String> expectedShipments) {
        List<OrderItem> orderItems = items.stream()
                .map(item -> new OrderItem(item.productId(), 1, FulfillmentType.DELIVERY, "Addr-A"))
                .toList();
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(item -> inventory.addStock("W1", item.productId(), item.status()));

        List<Shipment> shipments = new OrderSplitter().splitOrder(new Order(orderItems), inventory);

        assertEquals(expectedShipments, describeByAvailability(shipments));
    }

    @Description("""
        Fulfillment type and delivery address are fixed (DELIVERY to Addr-A) and every item
        is IN_STOCK wherever it is stocked, isolating warehouse selection from the other
        splitting rules. Each row lists, per warehouse, which of the order's products it
        stocks; the fewest-shipments combination must be chosen even when it means
        overriding a warehouse that only partially covers the order.
        """)
    @TableTest("""
        Scenario                                                | Items                       | W1 Stock            | W2 Stock            | W3 Stock          | Shipments?
        Single warehouse covers the whole order                 | [camera, lens, mic]         | [camera, lens, mic] | [camera]            | []                | ["W1:[camera,lens,mic]"]
        No single warehouse covers everything, split is forced  | [camera, lens, mic]         | [camera, lens]      | [mic]               | []                | ["W1:[camera,lens]", "W2:[mic]"]
        Overlapping stock does not cause a needless split       | [camera, lens, mic]         | [camera]            | [camera, lens, mic] | []                | ["W2:[camera,lens,mic]"]
        Correct warehouse pair must be found, not just any pair | [camera, lens, mic, battery]| [camera, lens]      | [lens, mic]         | [mic, battery]    | ["W1:[camera,lens]", "W3:[battery,mic]"]
        """)
    void choosesWarehouseCombinationMinimizingShipmentCount(
            List<String> items, List<String> w1Stock, List<String> w2Stock, List<String> w3Stock,
            List<String> expectedShipments) {
        WarehouseInventory inventory = inventoryOf(w1Stock, w2Stock, w3Stock);

        List<Shipment> shipments = new OrderSplitter().splitOrder(new Order(deliveryItems(items)), inventory);

        assertEquals(expectedShipments, describeByWarehouse(shipments));
    }

    @Description("""
        Companion relationships are treated as a catalog/inventory-level fact via
        WarehouseInventory.addCompanionGroup, not a per-order property - Order also exposes
        a companionGroups constructor parameter, but these tests treat WarehouseInventory as
        the single source of truth for which products are companions. Fulfillment type and
        delivery address are fixed and every item is IN_STOCK wherever stocked, isolating the
        companion preference from the other splitting rules. Companions only affect the
        outcome when multiple warehouse combinations tie for fewest shipments.
        """)
    @TableTest("""
        Scenario                                                 | Items               | W1 Stock         | W2 Stock       | W3 Stock | Companions      | Shipments?
        Companions win a tie between equally minimal warehouses  | [camera, lens, mic] | [camera, lens]   | [camera, mic]  | [lens]   | {camera, lens}  | ["W1:[camera,lens]", "W2:[mic]"]
        Companions split when no warehouse stocks both           | [camera, lens]      | [camera]         | [lens]         | []       | {camera, lens}  | ["W1:[camera]", "W2:[lens]"]
        Companions already together needs no trade-off           | [camera, lens]      | [camera, lens]   | []             | []       | {camera, lens}  | ["W1:[camera,lens]"]
        """)
    void keepsCompanionProductsTogetherWhenPossible(
            List<String> items, List<String> w1Stock, List<String> w2Stock, List<String> w3Stock,
            Set<String> companions, List<String> expectedShipments) {
        WarehouseInventory inventory = inventoryOf(w1Stock, w2Stock, w3Stock);
        inventory.addCompanionGroup(companions);

        List<Shipment> shipments = new OrderSplitter().splitOrder(new Order(deliveryItems(items)), inventory);

        assertEquals(expectedShipments, describeByWarehouse(shipments));
    }

    @TypeConverter
    public static OrderItem parseOrderItem(String value) {
        String[] parts = value.split(":", 3);
        String address = parts.length > 2 && !parts[2].isEmpty() ? parts[2] : null;
        return new OrderItem(parts[0], 1, FulfillmentType.valueOf(parts[1]), address);
    }

    @TypeConverter
    public static StockedItem parseStockedItem(String value) {
        String[] parts = value.split(":", 2);
        return new StockedItem(parts[0], StockStatus.valueOf(parts[1]));
    }

    private static List<OrderItem> deliveryItems(List<String> productIds) {
        return productIds.stream()
                .map(productId -> new OrderItem(productId, 1, FulfillmentType.DELIVERY, "Addr-A"))
                .toList();
    }

    private static WarehouseInventory inventoryOf(List<String> w1Stock, List<String> w2Stock, List<String> w3Stock) {
        WarehouseInventory inventory = new WarehouseInventory();
        w1Stock.forEach(productId -> inventory.addStock("W1", productId, StockStatus.IN_STOCK));
        w2Stock.forEach(productId -> inventory.addStock("W2", productId, StockStatus.IN_STOCK));
        w3Stock.forEach(productId -> inventory.addStock("W3", productId, StockStatus.IN_STOCK));
        return inventory;
    }

    private static List<String> describeByItems(List<Shipment> shipments) {
        return shipments.stream()
                .map(OrderSplitterTest::sortedProductIds)
                .sorted()
                .toList();
    }

    private static List<String> describeByAvailability(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> shipment.getAvailability() + ":[" + sortedProductIds(shipment) + "]")
                .sorted()
                .toList();
    }

    private static List<String> describeByWarehouse(List<Shipment> shipments) {
        return shipments.stream()
                .map(shipment -> shipment.getWarehouseId() + ":[" + sortedProductIds(shipment) + "]")
                .sorted()
                .toList();
    }

    private static String sortedProductIds(Shipment shipment) {
        return shipment.productIds().stream().sorted().collect(Collectors.joining(","));
    }

    record StockedItem(String productId, StockStatus status) {}
}
