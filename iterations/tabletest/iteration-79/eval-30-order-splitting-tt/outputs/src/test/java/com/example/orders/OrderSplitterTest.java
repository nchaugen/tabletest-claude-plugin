package com.example.orders;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private final OrderSplitter splitter = new OrderSplitter();

    @DisplayName("Groups order items into shipment groups by fulfillment type and delivery address")
    @Description("""
            Pickup items carry no delivery address; the address column is left blank for them.
            Groups preserve the order in which their first member appears in the input.
            """)
    @TableTest("""
        Scenario                                   | Items                                                                                                                                                                                                                                                           | Groups?
        Same fulfillment type and address          | [[productId: P1, fulfillmentType: DELIVERY, deliveryAddress: AddressA], [productId: P2, fulfillmentType: DELIVERY, deliveryAddress: AddressA]]                                                                                                                  | [[P1, P2]]
        Different fulfillment types, same address  | [[productId: P1, fulfillmentType: DELIVERY, deliveryAddress: AddressA], [productId: P2, fulfillmentType: PICKUP]]                                                                                                                                               | [[P1], [P2]]
        Same fulfillment type, different addresses | [[productId: P1, fulfillmentType: DELIVERY, deliveryAddress: AddressA], [productId: P2, fulfillmentType: DELIVERY, deliveryAddress: AddressB]]                                                                                                                  | [[P1], [P2]]
        Pickup items without a delivery address    | [[productId: P1, fulfillmentType: PICKUP], [productId: P2, fulfillmentType: PICKUP]]                                                                                                                                                                            | [[P1, P2]]
        Mixed fulfillment types and addresses      | [[productId: P1, fulfillmentType: DELIVERY, deliveryAddress: AddressA], [productId: P2, fulfillmentType: DELIVERY, deliveryAddress: AddressA], [productId: P3, fulfillmentType: DELIVERY, deliveryAddress: AddressB], [productId: P4, fulfillmentType: PICKUP]] | [[P1, P2], [P3], [P4]]
        """)
    void groupsItemsByFulfillmentAndAddress(List<OrderItem> items, List<List<String>> groups) {
        assertEquals(groups, splitter.groupByFulfillment(items));
    }

    @DisplayName("Splits items sharing a warehouse into an immediate shipment and a pending shipment by stock status")
    @Description("""
            Backordered and pre-ordered items are not distinguished from one another: the rule only
            separates in-stock items (ship now) from everything else (ship when available).
            """)
    @TableTest("""
        Scenario                                    | Stock Status                       | Immediate Items? | Pending Items?
        All items in stock                          | [P1: IN_STOCK, P2: IN_STOCK]       | [P1, P2]         | []
        Backordered item alongside an in-stock item | [P1: IN_STOCK, P2: BACKORDERED]    | [P1]             | [P2]
        Pre-ordered item alongside an in-stock item | [P1: IN_STOCK, P2: PRE_ORDERED]    | [P1]             | [P2]
        Backordered and pre-ordered items together  | [P1: BACKORDERED, P2: PRE_ORDERED] | []               | [P1, P2]
        """)
    void splitsItemsByStockAvailability(Map<String, StockStatus> stockStatus, List<String> immediateItems, List<String> pendingItems) {
        AvailabilityGroups groups = splitter.groupByAvailability(stockStatus);
        assertEquals(immediateItems, groups.immediate());
        assertEquals(pendingItems, groups.pending());
    }

    @DisplayName("Selects the warehouse combination that covers the order in the fewest shipments, preferring companions from the same warehouse")
    @Description("""
            Companion product pairings and per-product warehouse stock are given directly as inputs;
            resolving them from a WarehouseInventory is covered separately.
            Only scenarios with a uniquely determined minimal-shipment answer are included: where
            several warehouse combinations tie on shipment count and no companion grouping breaks the
            tie, the choice is implementation-defined and intentionally not exercised here.
            """)
    @TableTest("""
        Scenario                                                                 | Product Ids  | Warehouses By Product              | Companion Groups | Warehouse Assignment?
        Single item with one stocking warehouse                                  | [P1]         | [P1: {W1}]                         | []               | [P1: W1]
        One warehouse alone stocks every item                                    | [P1, P2]     | [P1: {W1, W2}, P2: {W1}]           | []               | [P1: W1, P2: W1]
        No warehouse stocks every item                                           | [P1, P2]     | [P1: {W1}, P2: {W2}]               | []               | [P1: W1, P2: W2]
        Companion joins its pair's warehouse without changing the shipment count | [P1, P2, P3] | [P1: {W1}, P2: {W1, W2}, P3: {W2}] | [{P1, P2}]       | [P1: W1, P2: W1, P3: W2]
        Companions with no shared warehouse still ship apart                     | [P1, P2]     | [P1: {W1}, P2: {W2}]               | [{P1, P2}]       | [P1: W1, P2: W2]
        """)
    void selectsWarehousesMinimisingShipmentsWithCompanionPreference(List<String> productIds,
                                                                       Map<String, Set<String>> warehousesByProduct,
                                                                       List<Set<String>> companionGroups,
                                                                       Map<String, String> warehouseAssignment) {
        assertEquals(warehouseAssignment, splitter.selectWarehouses(productIds, warehousesByProduct, companionGroups));
    }

    @DisplayName("Keeps companion products in separate shipments when fulfillment type or availability differs, even from the same warehouse")
    @Description("""
            Companion pairings are read from WarehouseInventory; Order also exposes a companion
            groups field in the stub, but it is treated as unused here since a single companion
            source is simpler and the inventory (the catalog/stock system) is the more natural owner
            of a product-level fact like "these two are companions".
            Both rows below use products that are companions and could share a warehouse, to show
            that shipment-count minimisation and availability splitting still take precedence over
            shipping companions together - grouping by fulfillment/address and by availability wins
            over co-shipping companions, even when nothing stops them from doing so.
            """)
    @TableTest("""
        Scenario                                     | Order                                                                                                                                          | Inventory                                                              | Shipments?
        Companions with different fulfillment types  | [[productId: P1, fulfillmentType: DELIVERY, deliveryAddress: AddressA], [productId: P2, fulfillmentType: PICKUP]]                              | [stock: [W1: [P1: IN_STOCK, P2: IN_STOCK]], companions: [{P1, P2}]]    | {[items: {P1}, fulfillmentType: DELIVERY, warehouseId: W1, availability: IMMEDIATE], [items: {P2}, fulfillmentType: PICKUP, warehouseId: W1, availability: IMMEDIATE]}
        Companions with different stock availability | [[productId: P1, fulfillmentType: DELIVERY, deliveryAddress: AddressA], [productId: P2, fulfillmentType: DELIVERY, deliveryAddress: AddressA]] | [stock: [W1: [P1: IN_STOCK, P2: BACKORDERED]], companions: [{P1, P2}]] | {[items: {P1}, fulfillmentType: DELIVERY, warehouseId: W1, availability: IMMEDIATE], [items: {P2}, fulfillmentType: DELIVERY, warehouseId: W1, availability: WHEN_AVAILABLE]}
        """)
    void keepsCompanionsApartAcrossHigherPrecedenceSplits(Order order, WarehouseInventory inventory, Set<ShipmentView> shipments) {
        Set<ShipmentView> actual = splitter.splitOrder(order, inventory).stream()
                .map(ShipmentView::from)
                .collect(Collectors.toSet());
        assertEquals(shipments, actual);
    }

    @TypeConverter
    public static OrderItem toOrderItem(Map<String, String> fields) {
        return new OrderItem(
                fields.get("productId"),
                Integer.parseInt(fields.getOrDefault("quantity", "1")),
                FulfillmentType.valueOf(fields.get("fulfillmentType")),
                fields.get("deliveryAddress"));
    }

    @TypeConverter
    public static Order toOrder(List<OrderItem> items) {
        return new Order(items);
    }

    @TypeConverter
    @SuppressWarnings("unchecked")
    public static WarehouseInventory toWarehouseInventory(Map<String, Object> fields) {
        WarehouseInventory inventory = new WarehouseInventory();
        ((Map<String, Map<String, String>>) fields.get("stock")).forEach((warehouseId, productStock) ->
                productStock.forEach((productId, status) ->
                        inventory.addStock(warehouseId, productId, StockStatus.valueOf(status))));
        ((List<Set<String>>) fields.get("companions")).forEach(inventory::addCompanionGroup);
        return inventory;
    }

    @TypeConverter
    @SuppressWarnings("unchecked")
    public static ShipmentView toShipmentView(Map<String, Object> fields) {
        return new ShipmentView(
                (Set<String>) fields.get("items"),
                FulfillmentType.valueOf((String) fields.get("fulfillmentType")),
                (String) fields.get("warehouseId"),
                Availability.valueOf((String) fields.get("availability")));
    }

    private record ShipmentView(Set<String> productIds, FulfillmentType fulfillmentType, String warehouseId,
                                 Availability availability) {

        static ShipmentView from(Shipment shipment) {
            return new ShipmentView(
                    Set.copyOf(shipment.productIds()),
                    shipment.fulfillmentType(),
                    shipment.getWarehouseId(),
                    shipment.availability());
        }
    }
}
