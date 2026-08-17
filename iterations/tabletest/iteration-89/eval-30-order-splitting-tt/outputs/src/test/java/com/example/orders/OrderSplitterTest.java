package com.example.orders;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private static final String DEFAULT_ADDRESS = "742 Evergreen Terrace";
    private static final String SHARED_ADDRESS = "1 Warehouse Way";

    private final OrderSplitter splitter = new OrderSplitter();

    @Description("""
        A single warehouse "W1" stocks every item in this table, all in stock; warehouse
        selection is covered separately in choosesWarehousesToMinimizeShipmentCount.
        """)
    @TableTest("""
        Scenario                                                | Items                                                                                  | Shipments?
        Same fulfillment type stays in one shipment             | [[product: camBody, fulfillment: DELIVERY], [product: camLens, fulfillment: DELIVERY]] | [DELIVERY: {camBody, camLens}]
        Delivery and pickup items split into separate shipments | [[product: camBody, fulfillment: DELIVERY], [product: tripod, fulfillment: PICKUP]]    | [DELIVERY: {camBody}, PICKUP: {tripod}]
        """)
    void splitsShipmentsByFulfillmentType(List<ItemSpec> items, Map<String, Set<String>> shipments) {
        List<Shipment> actual = splitter.splitOrder(toOrder(items), toInventory(items, "W1"));
        assertEquals(shipments, groupBy(actual, s -> s.getFulfillmentType().name()));
    }

    @Description("""
        Fulfillment type is fixed at DELIVERY throughout; address splitting does not apply to
        pickup items, which is shown in splitsShipmentsByFulfillmentType instead. A single
        warehouse "W1" stocks every item, all in stock.
        """)
    @TableTest("""
        Scenario                                                   | Items                                                                                        | Shipments?
        Same delivery address stays in one shipment                | [[product: camBody, address: '221B Baker St'], [product: camLens, address: '221B Baker St']] | ['221B Baker St': {camBody, camLens}]
        Different delivery addresses split into separate shipments | [[product: camBody, address: '221B Baker St'], [product: tripod, address: '10 Downing St']]  | ['221B Baker St': {camBody}, '10 Downing St': {tripod}]
        """)
    void splitsDeliveryShipmentsByAddress(List<ItemSpec> items, Map<String, Set<String>> shipments) {
        List<Shipment> actual = splitter.splitOrder(toOrder(items), toInventory(items, "W1"));
        assertEquals(shipments, groupBy(actual, s -> s.items().get(0).getDeliveryAddress()));
    }

    @TableTest("""
        Scenario                              | Stock Status | Availability?
        In-stock item ships immediately       | IN_STOCK     | IMMEDIATE
        Backordered item ships when available | BACKORDERED  | WHEN_AVAILABLE
        Pre-ordered item ships when available | PRE_ORDERED  | WHEN_AVAILABLE
        """)
    void resolvesShipmentAvailabilityFromStockStatus(StockStatus stockStatus, Availability availability) {
        assertEquals(availability, splitter.resolveAvailability(stockStatus));
    }

    @Description("""
        A single warehouse "W1" stocks every item, all delivered to the same address;
        splitting by fulfillment type and address is covered separately. Backordered stands
        as the sole representative of "not yet available" stock, since
        resolvesShipmentAvailabilityFromStockStatus already shows backordered and pre-ordered
        resolve to the same availability.
        """)
    @TableTest("""
        Scenario                                                               | Items                                                                         | Shipments?
        All items in stock ship together immediately                           | [[product: camBody, stock: IN_STOCK], [product: camLens, stock: IN_STOCK]]    | [IMMEDIATE: {camBody, camLens}]
        An unavailable item ships separately without holding the in-stock item | [[product: camBody, stock: IN_STOCK], [product: camLens, stock: BACKORDERED]] | [IMMEDIATE: {camBody}, WHEN_AVAILABLE: {camLens}]
        """)
    void shipsInStockItemsWithoutHoldingForUnavailableItems(List<ItemSpec> items, Map<String, Set<String>> shipments) {
        List<Shipment> actual = splitter.splitOrder(toOrder(items), toInventory(items, "W1"));
        assertEquals(shipments, groupBy(actual, s -> s.getAvailability().name()));
    }

    @Description("""
        Every item is a delivery to the same address and in stock wherever listed; fulfillment
        type, address and availability splitting are covered separately. Ties between
        warehouse combinations that use the same number of warehouses are broken by companion
        grouping, covered separately in
        shipsCompanionProductsFromTheSameWarehouseWhenPossible.
        """)
    @TableTest("""
        Scenario                                                               | Stocked At                                       | Warehouse Assignment?
        A flexible item joins the warehouse already required by another item   | [camBody: {W1, W2}, battery: {W1}]               | [W1: {camBody, battery}]
        Items with no shared warehouse ship from separate warehouses           | [camBody: {W1}, tripod: {W2}]                    | [W1: {camBody}, W2: {tripod}]
        A flexible item joins an already-required warehouse over an unused one | [camBody: {W1}, tripod: {W2}, battery: {W2, W3}] | [W1: {camBody}, W2: {tripod, battery}]
        """)
    void choosesWarehousesToMinimizeShipmentCount(Map<String, Set<String>> stockedAt, Map<String, Set<String>> warehouseAssignment) {
        List<Shipment> actual = splitter.splitOrder(toOrderFromStock(stockedAt), toInventoryFromStock(stockedAt));
        assertEquals(warehouseAssignment, groupBy(actual, Shipment::getWarehouseId));
    }

    @Description("""
        Every item is a delivery to the same address and in stock wherever listed. Both rows
        have two equally minimal warehouse combinations available; companion grouping decides
        between them when a shared warehouse exists, and has nothing to decide when it doesn't.
        Companion membership is read from WarehouseInventory.addCompanionGroup, treating
        companionship as a product/catalog fact rather than an Order-specific one; Order also
        exposes a companionGroups list, which this table assumes is unused by the splitter.
        """)
    @TableTest("""
        Scenario                                                         | Stocked At                                    | Companions      | Warehouse Assignment?
        Companions consolidate when it does not cost an extra shipment   | [camBody: {W1}, lens: {W1, W2}, tripod: {W2}] | {camBody, lens} | [W1: {camBody, lens}, W2: {tripod}]
        Companions ship separately when no shared warehouse is available | [camBody: {W1}, lens: {W2}]                   | {camBody, lens} | [W1: {camBody}, W2: {lens}]
        """)
    void shipsCompanionProductsFromTheSameWarehouseWhenPossible(Map<String, Set<String>> stockedAt, Set<String> companions, Map<String, Set<String>> warehouseAssignment) {
        WarehouseInventory inventory = toInventoryFromStock(stockedAt);
        inventory.addCompanionGroup(companions);
        List<Shipment> actual = splitter.splitOrder(toOrderFromStock(stockedAt), inventory);
        assertEquals(warehouseAssignment, groupBy(actual, Shipment::getWarehouseId));
    }

    @TypeConverter
    public static ItemSpec parseItemSpec(Map<String, String> fields) {
        FulfillmentType fulfillmentType = fields.containsKey("fulfillment")
                ? FulfillmentType.valueOf(fields.get("fulfillment"))
                : FulfillmentType.DELIVERY;
        String address = fields.containsKey("address")
                ? fields.get("address")
                : fulfillmentType == FulfillmentType.PICKUP ? null : DEFAULT_ADDRESS;
        StockStatus stockStatus = fields.containsKey("stock")
                ? StockStatus.valueOf(fields.get("stock"))
                : StockStatus.IN_STOCK;
        return new ItemSpec(fields.get("product"), fulfillmentType, address, stockStatus);
    }

    record ItemSpec(String productId, FulfillmentType fulfillmentType, String deliveryAddress, StockStatus stockStatus) {
    }

    private static Order toOrder(List<ItemSpec> specs) {
        return new Order(specs.stream()
                .map(spec -> new OrderItem(spec.productId(), 1, spec.fulfillmentType(), spec.deliveryAddress()))
                .toList());
    }

    private static WarehouseInventory toInventory(List<ItemSpec> specs, String warehouseId) {
        WarehouseInventory inventory = new WarehouseInventory();
        specs.forEach(spec -> inventory.addStock(warehouseId, spec.productId(), spec.stockStatus()));
        return inventory;
    }

    private static Order toOrderFromStock(Map<String, Set<String>> stockedAt) {
        return new Order(stockedAt.keySet().stream()
                .map(productId -> new OrderItem(productId, 1, FulfillmentType.DELIVERY, SHARED_ADDRESS))
                .toList());
    }

    private static WarehouseInventory toInventoryFromStock(Map<String, Set<String>> stockedAt) {
        WarehouseInventory inventory = new WarehouseInventory();
        stockedAt.forEach((productId, warehouses) ->
                warehouses.forEach(warehouseId -> inventory.addStock(warehouseId, productId, StockStatus.IN_STOCK)));
        return inventory;
    }

    private static Map<String, Set<String>> groupBy(List<Shipment> shipments, Function<Shipment, String> keyFn) {
        return shipments.stream().collect(Collectors.toMap(
                keyFn,
                shipment -> Set.copyOf(shipment.productIds())));
    }
}
