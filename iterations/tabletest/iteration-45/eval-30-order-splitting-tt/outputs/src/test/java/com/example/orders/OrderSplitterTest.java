package com.example.orders;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private static final Order CAMERA_AND_LENS_DELIVERY = new Order(List.of(
            new OrderItem("camera", 1, FulfillmentType.DELIVERY, "addr1"),
            new OrderItem("lens", 1, FulfillmentType.DELIVERY, "addr1")));

    private final OrderSplitter splitter = new OrderSplitter();

    @DisplayName("Separates shipments by fulfillment type")
    @Description("""
        All items are in stock in a single warehouse (wh1), so only fulfillment type
        varies the shipment grouping in this table.
        """)
    @TableTest("""
        Scenario                   | Items                                              | Shipments by Fulfillment?
        Delivery and pickup mixed  | ["camera:DELIVERY:addr1", "battery:PICKUP"]        | [DELIVERY: {camera}, PICKUP: {battery}]
        All delivery items         | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1"]   | [DELIVERY: {camera, lens}]
        All pickup items           | ["battery:PICKUP", "charger:PICKUP"]               | [PICKUP: {battery, charger}]
        """)
    void separatesShipmentsByFulfillmentType(List<OrderItem> items, Map<String, Set<String>> expected) {
        Order order = new Order(items);
        WarehouseInventory inventory = allInStockAt(items, "wh1");

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(expected, groupByFulfillmentType(shipments));
    }

    @DisplayName("Separates shipments by delivery address")
    @Description("""
        All items are DELIVERY, in stock, in a single warehouse (wh1), so only the
        delivery address varies the shipment grouping in this table.
        """)
    @TableTest("""
        Scenario              | Items                                              | Shipments by Address?
        Same address          | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1"]   | [addr1: {camera, lens}]
        Different addresses   | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr2"]   | [addr1: {camera}, addr2: {lens}]
        """)
    void separatesShipmentsByDeliveryAddress(List<OrderItem> items, Map<String, Set<String>> expected) {
        Order order = new Order(items);
        WarehouseInventory inventory = allInStockAt(items, "wh1");

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(expected, groupByDeliveryAddress(shipments));
    }

    @DisplayName("Ships in-stock items immediately without waiting for delayed items")
    @Description("""
        Camera and lens are both DELIVERY items to the same address, held in a single
        warehouse (wh1), for every row in this table.
        """)
    @TableTest("""
        Scenario                                | Stock                                                | Shipments by Availability?
        Camera in stock, lens backordered       | ["wh1:camera:IN_STOCK", "wh1:lens:BACKORDERED"]      | [IMMEDIATE: {camera}, WHEN_AVAILABLE: {lens}]
        Both items in stock                     | ["wh1:camera:IN_STOCK", "wh1:lens:IN_STOCK"]         | [IMMEDIATE: {camera, lens}]
        Camera backordered, lens pre-ordered    | ["wh1:camera:BACKORDERED", "wh1:lens:PRE_ORDERED"]   | [WHEN_AVAILABLE: {camera, lens}]
        """)
    void shipsInStockItemsWithoutWaitingForDelayedItems(WarehouseInventory inventory, Map<String, Set<String>> expected) {
        List<Shipment> shipments = splitter.splitOrder(CAMERA_AND_LENS_DELIVERY, inventory);

        assertEquals(expected, groupByAvailability(shipments));
    }

    @DisplayName("Splits across warehouses in the minimum number of shipments needed to cover the order")
    @Description("""
        All items are DELIVERY to the same address, so fulfillment type and address
        never force a split here - only warehouse coverage does. No companion groups
        are declared in this table.
        """)
    @TableTest("""
        Scenario                                    | Items                                                                       | Stock                                                                  | Shipments by Warehouse?
        One warehouse can cover the whole order      | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1"]                            | ["wh1:camera:IN_STOCK", "wh1:lens:IN_STOCK", "wh2:camera:IN_STOCK"]    | [wh1: {camera, lens}]
        No single warehouse covers the order          | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1", "tripod:DELIVERY:addr1"]   | ["wh1:camera:IN_STOCK", "wh1:lens:IN_STOCK", "wh2:tripod:IN_STOCK"]    | [wh1: {camera, lens}, wh2: {tripod}]
        """)
    void minimisesShipmentCountAcrossWarehouses(List<OrderItem> items, WarehouseInventory inventory, Map<String, Set<String>> expected) {
        Order order = new Order(items);

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(expected, groupByWarehouse(shipments));
    }

    @DisplayName("Keeps companion products on the same warehouse shipment when it does not cost extra shipments")
    @Description("""
        Camera and lens are declared as a companion group in every row of this table.
        All items are DELIVERY to the same address.
        """)
    @TableTest("""
        Scenario                                            | Items                                                                       | Stock                                                                                       | Shipments by Warehouse?
        Two equally minimal warehouse splits are possible    | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1", "tripod:DELIVERY:addr1"]   | ["wh1:camera:IN_STOCK", "wh1:lens:IN_STOCK", "wh2:camera:IN_STOCK", "wh2:tripod:IN_STOCK"]  | [wh1: {camera, lens}, wh2: {tripod}]
        Companions share no common warehouse                | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1"]                            | ["wh1:lens:IN_STOCK", "wh2:camera:IN_STOCK"]                                                | [wh1: {lens}, wh2: {camera}]
        """)
    void keepsCompanionsTogetherWhenPossible(List<OrderItem> items, WarehouseInventory inventory, Map<String, Set<String>> expected) {
        Order order = new Order(items, List.of(List.of("camera", "lens")));

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(expected, groupByWarehouse(shipments));
    }

    @TypeConverter
    public static OrderItem parseOrderItem(String spec) {
        String[] parts = spec.split(":", -1);
        FulfillmentType fulfillmentType = FulfillmentType.valueOf(parts[1]);
        String address = parts.length > 2 && !parts[2].isEmpty() ? parts[2] : null;
        return new OrderItem(parts[0], 1, fulfillmentType, address);
    }

    @TypeConverter
    public static WarehouseInventory parseInventory(List<String> stockEntries) {
        WarehouseInventory inventory = new WarehouseInventory();
        for (String entry : stockEntries) {
            String[] parts = entry.split(":", -1);
            inventory.addStock(parts[0], parts[1], StockStatus.valueOf(parts[2]));
        }
        return inventory;
    }

    private static WarehouseInventory allInStockAt(List<OrderItem> items, String warehouseId) {
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(item -> inventory.addStock(warehouseId, item.getProductId(), StockStatus.IN_STOCK));
        return inventory;
    }

    private static Map<String, Set<String>> groupByFulfillmentType(List<Shipment> shipments) {
        return groupBy(shipments, s -> s.getFulfillmentType().name(), OrderSplitterTest::productIdSet);
    }

    private static Map<String, Set<String>> groupByDeliveryAddress(List<Shipment> shipments) {
        return groupBy(shipments, OrderSplitterTest::addressKey, OrderSplitterTest::productIdSet);
    }

    private static Map<String, Set<String>> groupByAvailability(List<Shipment> shipments) {
        return groupBy(shipments, s -> s.getAvailability().name(), OrderSplitterTest::productIdSet);
    }

    private static Map<String, Set<String>> groupByWarehouse(List<Shipment> shipments) {
        return groupBy(shipments, Shipment::getWarehouseId, OrderSplitterTest::productIdSet);
    }

    private static <K> Map<K, Set<String>> groupBy(List<Shipment> shipments, Function<Shipment, K> keyFn,
                                                     Function<Shipment, Set<String>> valueFn) {
        Map<K, Set<String>> result = new LinkedHashMap<>();
        for (Shipment shipment : shipments) {
            K key = keyFn.apply(shipment);
            if (result.containsKey(key)) {
                throw new IllegalStateException("Multiple shipments share key: " + key);
            }
            result.put(key, valueFn.apply(shipment));
        }
        return result;
    }

    private static Set<String> productIdSet(Shipment shipment) {
        return new HashSet<>(shipment.productIds());
    }

    private static String addressKey(Shipment shipment) {
        Set<String> addresses = new TreeSet<>();
        for (OrderItem item : shipment.items()) {
            addresses.add(item.getDeliveryAddress());
        }
        return String.join(",", addresses);
    }
}
