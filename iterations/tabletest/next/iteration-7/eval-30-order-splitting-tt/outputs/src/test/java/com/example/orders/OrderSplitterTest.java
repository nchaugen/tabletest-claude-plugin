package com.example.orders;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private final OrderSplitter splitter = new OrderSplitter();

    @TableTest("""
        Scenario                   | Items                                             | Inventory                                   | Fulfillment Groups?
        All delivery, same address | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1"]  | [W1: [camera: IN_STOCK, lens: IN_STOCK]]     | [DELIVERY: {camera, lens}]
        All pickup                 | ["camera:PICKUP:_", "lens:PICKUP:_"]              | [W1: [camera: IN_STOCK, lens: IN_STOCK]]     | [PICKUP: {camera, lens}]
        Mixed delivery and pickup  | ["camera:DELIVERY:addr1", "tripod:PICKUP:_"]      | [W1: [camera: IN_STOCK, tripod: IN_STOCK]]   | [DELIVERY: {camera}, PICKUP: {tripod}]
        """)
    void splitsByFulfillmentType(List<OrderItem> items, WarehouseInventory inventory,
                                  Map<FulfillmentType, Set<String>> fulfillmentGroups) {
        List<Shipment> shipments = splitter.splitOrder(new Order(items), inventory);

        assertEquals(fulfillmentGroups, groupShipmentsBy(shipments, Shipment::getFulfillmentType));
    }

    @TableTest("""
        Scenario        | Items                                                                    | Inventory                                                   | Address Groups?
        Same address    | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1"]                          | [W1: [camera: IN_STOCK, lens: IN_STOCK]]                     | [addr1: {camera, lens}]
        Two addresses   | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr2"]                          | [W1: [camera: IN_STOCK, lens: IN_STOCK]]                     | [addr1: {camera}, addr2: {lens}]
        Three addresses | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr2", "tripod:DELIVERY:addr3"] | [W1: [camera: IN_STOCK, lens: IN_STOCK, tripod: IN_STOCK]]   | [addr1: {camera}, addr2: {lens}, addr3: {tripod}]
        """)
    void splitsByDeliveryAddress(List<OrderItem> items, WarehouseInventory inventory,
                                  Map<String, Set<String>> addressGroups) {
        List<Shipment> shipments = splitter.splitOrder(new Order(items), inventory);

        assertEquals(addressGroups, groupShipmentsBy(shipments, s -> s.items().get(0).getDeliveryAddress()));
    }

    @Description("""
        Fixed for all rows: two items, 'camera' and 'lens', both DELIVERY to the same
        address and both stocked in a single warehouse. Only the stock status of each
        item varies.
        """)
    @TableTest("""
        Scenario                                                | Camera Status              | Lens Status                | Availability Groups?
        Both in stock                                           | IN_STOCK                   | IN_STOCK                   | [IMMEDIATE: {camera, lens}]
        Both awaiting availability, regardless of stock reason  | {BACKORDERED, PRE_ORDERED} | {BACKORDERED, PRE_ORDERED} | [WHEN_AVAILABLE: {camera, lens}]
        In-stock item ships without waiting for a backorder     | IN_STOCK                   | BACKORDERED                | [IMMEDIATE: {camera}, WHEN_AVAILABLE: {lens}]
        In-stock item ships without waiting for a pre-order     | IN_STOCK                   | PRE_ORDERED                | [IMMEDIATE: {camera}, WHEN_AVAILABLE: {lens}]
        """)
    void splitsByAvailability(StockStatus cameraStatus, StockStatus lensStatus,
                              Map<Availability, Set<String>> availabilityGroups) {
        Order order = new Order(List.of(
                new OrderItem("camera", 1, FulfillmentType.DELIVERY, "addr1"),
                new OrderItem("lens", 1, FulfillmentType.DELIVERY, "addr1")));
        WarehouseInventory inventory = new WarehouseInventory();
        inventory.addStock("W1", "camera", cameraStatus);
        inventory.addStock("W1", "lens", lensStatus);

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(availabilityGroups, groupShipmentsBy(shipments, Shipment::availability));
    }

    @Description("""
        Fixed for all rows: items are DELIVERY to the same address and immediately in
        stock wherever stocked. The only variable is which warehouse(s) hold each product.
        """)
    @TableTest("""
        Scenario                                                           | Items                                                                                            | Inventory                                                                                                                 | Shipments By Warehouse?
        Single warehouse covers everything, beating a partial overlap      | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1"]                                                | [W1: [camera: IN_STOCK, lens: IN_STOCK], W2: [lens: IN_STOCK]]                                                            | [W1: {camera, lens}]
        No single warehouse covers all items, forcing an unavoidable split | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1"]                                                | [W1: [camera: IN_STOCK], W2: [lens: IN_STOCK]]                                                                            | [W1: {camera}, W2: {lens}]
        No overlap across three warehouses forces three shipments         | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1", "tripod:DELIVERY:addr1"]                       | [W1: [camera: IN_STOCK], W2: [lens: IN_STOCK], W3: [tripod: IN_STOCK]]                                                    | [W1: {camera}, W2: {lens}, W3: {tripod}]
        A distractor warehouse does not change the minimal combination    | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1", "tripod:DELIVERY:addr1"]                       | [W1: [camera: IN_STOCK, lens: IN_STOCK], W2: [tripod: IN_STOCK], W3: [camera: IN_STOCK]]                                  | [W1: {camera, lens}, W2: {tripod}]
        Correct pair chosen among three candidate warehouses               | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1", "tripod:DELIVERY:addr1", "bag:DELIVERY:addr1"] | [W1: [camera: IN_STOCK, lens: IN_STOCK], W2: [tripod: IN_STOCK, bag: IN_STOCK], W3: [camera: IN_STOCK, tripod: IN_STOCK]] | [W1: {camera, lens}, W2: {tripod, bag}]
        """)
    void choosesWarehouseCombinationMinimisingShipments(List<OrderItem> items, WarehouseInventory inventory,
                                                        Map<String, Set<String>> shipmentsByWarehouse) {
        List<Shipment> shipments = splitter.splitOrder(new Order(items), inventory);

        assertEquals(shipmentsByWarehouse, groupShipmentsBy(shipments, Shipment::getWarehouseId));
    }

    @Description("""
        Fixed for all rows: items are DELIVERY to the same address and immediately in
        stock wherever stocked. 'camera' and 'lens' are declared as companion products.
        """)
    @TableTest("""
        Scenario                                                        | Items                                                                                            | Inventory                                                                                                               | Companions       | Shipments By Warehouse?
        Companions ship together when only one warehouse stocks both    | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1"]                                                | [W1: [camera: IN_STOCK, lens: IN_STOCK]]                                                                                 | [[camera, lens]] | [W1: {camera, lens}]
        Companion preference breaks a tie between equally-sized splits  | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1", "tripod:DELIVERY:addr1"]                       | [W1: [camera: IN_STOCK, lens: IN_STOCK], W2: [lens: IN_STOCK, tripod: IN_STOCK]]                                         | [[camera, lens]] | [W1: {camera, lens}, W2: {tripod}]
        Minimising shipment count outranks keeping companions together  | ["camera:DELIVERY:addr1", "lens:DELIVERY:addr1", "tripod:DELIVERY:addr1", "bag:DELIVERY:addr1"] | [W1: [camera: IN_STOCK, lens: IN_STOCK], W2: [camera: IN_STOCK, tripod: IN_STOCK], W3: [lens: IN_STOCK, bag: IN_STOCK]]  | [[camera, lens]] | [W2: {camera, tripod}, W3: {lens, bag}]
        """)
    void prefersShippingCompanionsFromSameWarehouse(List<OrderItem> items, WarehouseInventory inventory,
                                                    List<List<String>> companionGroups,
                                                    Map<String, Set<String>> shipmentsByWarehouse) {
        Order order = new Order(items, companionGroups);

        List<Shipment> shipments = splitter.splitOrder(order, inventory);

        assertEquals(shipmentsByWarehouse, groupShipmentsBy(shipments, Shipment::getWarehouseId));
    }

    private static <K> Map<K, Set<String>> groupShipmentsBy(List<Shipment> shipments, Function<Shipment, K> keyFn) {
        Map<K, Set<String>> result = new HashMap<>();
        for (Shipment shipment : shipments) {
            result.computeIfAbsent(keyFn.apply(shipment), ignored -> new HashSet<>())
                    .addAll(shipment.productIds());
        }
        return result;
    }

    @TypeConverter
    public static OrderItem parseOrderItem(String spec) {
        String[] parts = spec.split(":", -1);
        String productId = parts[0];
        FulfillmentType fulfillmentType = FulfillmentType.valueOf(parts[1]);
        String address = "_".equals(parts[2]) ? null : parts[2];
        return new OrderItem(productId, 1, fulfillmentType, address);
    }

    @TypeConverter
    public static WarehouseInventory parseInventory(Map<String, Map<String, String>> stockByWarehouse) {
        WarehouseInventory inventory = new WarehouseInventory();
        stockByWarehouse.forEach((warehouseId, stock) ->
                stock.forEach((productId, status) ->
                        inventory.addStock(warehouseId, productId, StockStatus.valueOf(status))));
        return inventory;
    }
}
