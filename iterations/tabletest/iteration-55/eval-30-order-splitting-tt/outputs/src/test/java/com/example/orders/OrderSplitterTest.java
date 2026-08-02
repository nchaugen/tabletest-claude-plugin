package com.example.orders;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private final OrderSplitter splitter = new OrderSplitter();

    @Description("""
        All items are in stock at a single warehouse (W1), so warehouse
        selection and stock availability do not influence the outcome; those
        are covered by separate tables below. PICKUP items carry no delivery
        address.
        """)
    @TableTest("""
        Scenario                  | Items                                                                               | Shipments By Type?
        All delivery items        | [[id: camera, type: DELIVERY, address: 123 Main St], [id: tripod, type: DELIVERY, address: 123 Main St]] | [DELIVERY: {camera, tripod}]
        All pickup items          | [[id: camera, type: PICKUP], [id: tripod, type: PICKUP]]                          | [PICKUP: {camera, tripod}]
        Mixed delivery and pickup | [[id: camera, type: DELIVERY, address: 123 Main St], [id: battery, type: PICKUP]] | [DELIVERY: {camera}, PICKUP: {battery}]
        """)
    void splitsShipmentsByFulfillmentType(List<OrderItem> items, Map<FulfillmentType, Set<String>> shipmentsByType) {
        List<Shipment> shipments = splitter.splitOrder(new Order(items), allInStockAt("W1", items));

        assertEquals(shipmentsByType.size(), shipments.size());
        assertEquals(shipmentsByType, shipments.stream()
                .collect(toMap(Shipment::fulfillmentType, s -> new HashSet<>(s.productIds()))));
    }

    @Description("""
        All items are DELIVERY, in stock at a single warehouse (W1);
        fulfillment type and stock availability are covered by separate
        tables above and below.
        """)
    @TableTest("""
        Scenario                     | Items                                                                                                     | Shipments By Address?
        Same delivery address        | [[id: camera, address: 123 Main St], [id: tripod, address: 123 Main St]]                                 | [123 Main St: {camera, tripod}]
        Different delivery addresses | [[id: camera, address: 123 Main St], [id: tripod, address: 9 Oak Ave]]                                   | [123 Main St: {camera}, 9 Oak Ave: {tripod}]
        Two addresses, uneven split  | [[id: camera, address: 123 Main St], [id: lens, address: 123 Main St], [id: tripod, address: 9 Oak Ave]] | [123 Main St: {camera, lens}, 9 Oak Ave: {tripod}]
        """)
    void splitsDeliveryShipmentsByAddress(List<OrderItem> items, Map<String, Set<String>> shipmentsByAddress) {
        List<Shipment> shipments = splitter.splitOrder(new Order(items), allInStockAt("W1", items));

        assertEquals(shipmentsByAddress.size(), shipments.size());
        assertEquals(shipmentsByAddress, shipments.stream()
                .collect(toMap(s -> s.items().get(0).deliveryAddress(), s -> new HashSet<>(s.productIds()))));
    }

    @Description("""
        All items are DELIVERY to the same address from a single warehouse
        (W1); fulfillment type, address and warehouse selection are covered
        by separate tables. Backordered and pre-ordered items both resolve
        to WHEN_AVAILABLE.
        """)
    @TableTest("""
        Scenario                                  | Items                                                                                    | Stock                                                            | Shipments By Availability?
        All items in stock                        | [[id: camera, address: 123 Main St], [id: tripod, address: 123 Main St]]                | [W1: [camera: IN_STOCK, tripod: IN_STOCK]]                       | [IMMEDIATE: {camera, tripod}]
        All items backordered                     | [[id: camera, address: 123 Main St], [id: tripod, address: 123 Main St]]                | [W1: [camera: BACKORDERED, tripod: BACKORDERED]]                 | [WHEN_AVAILABLE: {camera, tripod}]
        In-stock item not held for backorder      | [[id: camera, address: 123 Main St], [id: tripod, address: 123 Main St]]                | [W1: [camera: IN_STOCK, tripod: BACKORDERED]]                    | [IMMEDIATE: {camera}, WHEN_AVAILABLE: {tripod}]
        Backordered and pre-ordered ship together | [[id: camera, address: 123 Main St], [id: tripod, address: 123 Main St]]                | [W1: [camera: BACKORDERED, tripod: PRE_ORDERED]]                 | [WHEN_AVAILABLE: {camera, tripod}]
        Mixed stock statuses                      | [[id: camera, address: 123 Main St], [id: tripod, address: 123 Main St], [id: lens, address: 123 Main St]] | [W1: [camera: IN_STOCK, tripod: BACKORDERED, lens: PRE_ORDERED]] | [IMMEDIATE: {camera}, WHEN_AVAILABLE: {tripod, lens}]
        """)
    void shipsInStockItemsImmediatelyWithoutHoldingForBackorder(
            List<OrderItem> items, WarehouseInventory stock, Map<Availability, Set<String>> shipmentsByAvailability) {
        List<Shipment> shipments = splitter.splitOrder(new Order(items), stock);

        assertEquals(shipmentsByAvailability.size(), shipments.size());
        assertEquals(shipmentsByAvailability, shipments.stream()
                .collect(toMap(Shipment::availability, s -> new HashSet<>(s.productIds()))));
    }

    @Description("""
        All items are DELIVERY to the same address and in stock wherever
        listed; fulfillment type, address and stock availability are
        covered by separate tables. A product absent from a warehouse's
        stock entry is not carried there.
        """)
    @TableTest("""
        Scenario                                              | Items                                                                                                         | Stock                                                                                                                       | Shipments By Warehouse?
        Single warehouse covers the whole order                | [[id: camera, address: 123 Main St], [id: lens, address: 123 Main St]]                                      | [W1: [camera: IN_STOCK, lens: IN_STOCK], W2: [camera: IN_STOCK]]                                                           | [W1: {camera, lens}]
        No warehouse covers everything, split is unavoidable   | [[id: camera, address: 123 Main St], [id: lens, address: 123 Main St]]                                      | [W1: [camera: IN_STOCK], W2: [lens: IN_STOCK]]                                                                             | [W1: {camera}, W2: {lens}]
        Fewer warehouses preferred over a naive split           | [[id: camera, address: 123 Main St], [id: lens, address: 123 Main St], [id: tripod, address: 123 Main St]] | [W1: [camera: IN_STOCK, lens: IN_STOCK], W2: [tripod: IN_STOCK], W3: [camera: IN_STOCK, lens: IN_STOCK, tripod: IN_STOCK]] | [W3: {camera, lens, tripod}]
        """)
    void choosesWarehouseCombinationMinimisingShipmentCount(
            List<OrderItem> items, WarehouseInventory stock, Map<String, Set<String>> shipmentsByWarehouse) {
        List<Shipment> shipments = splitter.splitOrder(new Order(items), stock);

        assertEquals(shipmentsByWarehouse.size(), shipments.size());
        assertEquals(shipmentsByWarehouse, shipments.stream()
                .collect(toMap(Shipment::getWarehouseId, s -> new HashSet<>(s.productIds()))));
    }

    @Description("""
        Companion groups are supplied via Order's companion-groups
        constructor overload (a list of product-id groups). WarehouseInventory
        also exposes an addCompanionGroup method that this feature does not
        appear to use; if the real implementation reads companions from
        there instead, move this column's data accordingly. All items are
        DELIVERY to the same address; every listed warehouse entry means the
        product is in stock there.
        """)
    @TableTest("""
        Scenario                                            | Items                                                                                                        | Stock                                                                                                       | Companion Groups | Shipments By Warehouse?
        Companions ship together over an equal-count split  | [[id: camera, address: 123 Main St], [id: lens, address: 123 Main St], [id: battery, address: 123 Main St]] | [W1: [camera: IN_STOCK, battery: IN_STOCK], W2: [lens: IN_STOCK], W3: [camera: IN_STOCK, lens: IN_STOCK]]   | [[camera, lens]] | [W3: {camera, lens}, W1: {battery}]
        Companions split when no warehouse stocks both       | [[id: camera, address: 123 Main St], [id: lens, address: 123 Main St]]                                       | [W1: [camera: IN_STOCK], W2: [lens: IN_STOCK]]                                                               | [[camera, lens]] | [W1: {camera}, W2: {lens}]
        """)
    void shipsCompanionProductsFromTheSameWarehouseWhenPossible(
            List<OrderItem> items, WarehouseInventory stock, List<List<String>> companionGroups,
            Map<String, Set<String>> shipmentsByWarehouse) {
        List<Shipment> shipments = splitter.splitOrder(new Order(items, companionGroups), stock);

        assertEquals(shipmentsByWarehouse.size(), shipments.size());
        assertEquals(shipmentsByWarehouse, shipments.stream()
                .collect(toMap(Shipment::getWarehouseId, s -> new HashSet<>(s.productIds()))));
    }

    @TypeConverter
    public static OrderItem parseOrderItem(Map<String, String> fields) {
        String productId = fields.get("id");
        int quantity = Integer.parseInt(fields.getOrDefault("qty", "1"));
        FulfillmentType type = FulfillmentType.valueOf(fields.getOrDefault("type", "DELIVERY"));
        String address = fields.get("address");
        return new OrderItem(productId, quantity, type, address);
    }

    @TypeConverter
    public static WarehouseInventory parseInventory(Map<String, Map<String, StockStatus>> stockByWarehouse) {
        WarehouseInventory inventory = new WarehouseInventory();
        stockByWarehouse.forEach((warehouseId, stock) ->
                stock.forEach((productId, status) -> inventory.addStock(warehouseId, productId, status)));
        return inventory;
    }

    private static WarehouseInventory allInStockAt(String warehouseId, List<OrderItem> items) {
        WarehouseInventory inventory = new WarehouseInventory();
        items.forEach(item -> inventory.addStock(warehouseId, item.productId(), StockStatus.IN_STOCK));
        return inventory;
    }
}
