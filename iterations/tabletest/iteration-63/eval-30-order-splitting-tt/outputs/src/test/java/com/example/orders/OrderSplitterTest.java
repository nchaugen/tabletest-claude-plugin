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

    @DisplayName("Separates shipments by fulfillment type")
    @Description("""
        Single delivery address, single warehouse, everything in stock throughout,
        to isolate fulfillment type as the only thing that can force a split.
        """)
    @TableTest("""
        Scenario                         | Items                                                                                                                                                | Shipments?
        Two delivery items               | [[product: p1, fulfillment: DELIVERY], [product: p2, fulfillment: DELIVERY]]                                                                         | [{p1, p2}]
        Two pickup items                 | [[product: p1, fulfillment: PICKUP], [product: p2, fulfillment: PICKUP]]                                                                             | [{p1, p2}]
        One delivery and one pickup item | [[product: p1, fulfillment: DELIVERY], [product: p2, fulfillment: PICKUP]]                                                                           | [{p1}, {p2}]
        Several items of each type       | [[product: p1, fulfillment: DELIVERY], [product: p2, fulfillment: DELIVERY], [product: p3, fulfillment: PICKUP], [product: p4, fulfillment: PICKUP]] | [{p1, p2}, {p3, p4}]
        """)
    void separatesShipmentsByFulfillmentType(List<OrderItem> items, List<Set<String>> shipments) {
        Order order = new Order(items);
        WarehouseInventory inventory = fullyStockedAt("W1", items);

        List<Shipment> actual = splitter.splitOrder(order, inventory);

        assertEquals(Set.copyOf(shipments), groupsOf(actual));
    }

    @DisplayName("Separates delivery shipments by delivery address")
    @Description("""
        All items are DELIVERY, single warehouse, everything in stock throughout,
        to isolate delivery address as the only thing that can force a split.
        """)
    @TableTest("""
        Scenario                          | Items                                                                                            | Shipments?
        Same address                      | [[product: p1, address: addr-1], [product: p2, address: addr-1]]                                 | [{p1, p2}]
        Different addresses               | [[product: p1, address: addr-1], [product: p2, address: addr-2]]                                 | [{p1}, {p2}]
        Two addresses, uneven item counts | [[product: p1, address: addr-1], [product: p2, address: addr-1], [product: p3, address: addr-2]] | [{p1, p2}, {p3}]
        """)
    void separatesDeliveryShipmentsByAddress(List<OrderItem> items, List<Set<String>> shipments) {
        Order order = new Order(items);
        WarehouseInventory inventory = fullyStockedAt("W1", items);

        List<Shipment> actual = splitter.splitOrder(order, inventory);

        assertEquals(Set.copyOf(shipments), groupsOf(actual));
    }

    @DisplayName("Ships in-stock items immediately without waiting for items that are not yet available")
    @Description("""
        Single delivery address, single warehouse, so only stock status can force a split.
        Backordered and pre-ordered items both ship WHEN_AVAILABLE, since Shipment only
        distinguishes IMMEDIATE from WHEN_AVAILABLE and the feature description does not
        say backordered and pre-ordered items must ship apart from each other.
        """)
    @TableTest("""
        Scenario                                                       | Items    | Stock By Product?                  | Shipments?
        All items in stock ship together immediately                   | [p1, p2] | [p1: IN_STOCK, p2: IN_STOCK]       | [[items: [p1, p2], availability: IMMEDIATE]]
        Backordered and pre-ordered items ship together when available | [p1, p2] | [p1: BACKORDERED, p2: PRE_ORDERED] | [[items: [p1, p2], availability: WHEN_AVAILABLE]]
        In-stock item does not wait for a backordered item             | [p1, p2] | [p1: IN_STOCK, p2: BACKORDERED]    | [[items: [p1], availability: IMMEDIATE], [items: [p2], availability: WHEN_AVAILABLE]]
        In-stock item does not wait for a pre-ordered item             | [p1, p2] | [p1: IN_STOCK, p2: PRE_ORDERED]    | [[items: [p1], availability: IMMEDIATE], [items: [p2], availability: WHEN_AVAILABLE]]
        """)
    void shipsInStockItemsWithoutWaitingForItemsThatAreNotYetAvailable(
            List<String> productIds, Map<String, StockStatus> stockByProduct, List<AvailabilityGroup> shipments) {
        Order order = new Order(productIds.stream().map(this::deliveryItem).toList());
        WarehouseInventory inventory = new WarehouseInventory(Map.of("W1", stockByProduct));

        List<Shipment> actual = splitter.splitOrder(order, inventory);

        assertEquals(Set.copyOf(shipments), availabilityGroupsOf(actual));
    }

    @DisplayName("Chooses the combination of warehouses that minimises shipment count")
    @Description("""
        Single delivery address, all items IN_STOCK wherever listed, so only warehouse
        coverage can force a split.
        """)
    @TableTest("""
        Scenario                                                                          | Items    | Stock?                                                                     | Shipments?
        No single warehouse covers every item                                             | [p1, p2] | [W1: [p1: IN_STOCK], W2: [p2: IN_STOCK]]                                   | [[items: [p1], warehouse: W1], [items: [p2], warehouse: W2]]
        One warehouse already covers every item                                           | [p1, p2] | [W1: [p1: IN_STOCK, p2: IN_STOCK], W2: [p1: IN_STOCK]]                     | [[items: [p1, p2], warehouse: W1]]
        Reusing an already-needed warehouse avoids an extra shipment                      | [p1, p2] | [W1: [p1: IN_STOCK, p2: IN_STOCK], W2: [p2: IN_STOCK]]                     | [[items: [p1, p2], warehouse: W1]]
        Selects the one warehouse that covers everything over two that individually could | [p1, p2] | [W1: [p1: IN_STOCK], W2: [p2: IN_STOCK], W3: [p1: IN_STOCK, p2: IN_STOCK]] | [[items: [p1, p2], warehouse: W3]]
        """)
    void choosesWarehouseCombinationThatMinimisesShipmentCount(
            List<String> productIds, Map<String, Map<String, StockStatus>> stock, List<WarehouseGroup> shipments) {
        Order order = new Order(productIds.stream().map(this::deliveryItem).toList());
        WarehouseInventory inventory = new WarehouseInventory(stock);

        List<Shipment> actual = splitter.splitOrder(order, inventory);

        assertEquals(Set.copyOf(shipments), warehouseGroupsOf(actual));
    }

    @DisplayName("Keeps companion products together when possible")
    @Description("""
        Single delivery address, all items IN_STOCK wherever listed. Companion
        preference is read as a tie-breaker among warehouse choices that already
        achieve the minimum shipment count, not as a reason to add an extra shipment -
        that reading is what keeps this rule consistent with shipment-count minimisation.
        """)
    @TableTest("""
        Scenario                                                                 | Items        | Companion Groups | Stock?                                                               | Shipments?
        Flexible companion joins its partner's warehouse over a tied alternative | [p1, p2, p3] | [[p1, p2]]       | [W1: [p1: IN_STOCK, p2: IN_STOCK], W2: [p2: IN_STOCK, p3: IN_STOCK]] | [[items: [p1, p2], warehouse: W1], [items: [p3], warehouse: W2]]
        Companions ship apart when no warehouse carries both                     | [p1, p2]     | [[p1, p2]]       | [W1: [p1: IN_STOCK], W2: [p2: IN_STOCK]]                             | [[items: [p1], warehouse: W1], [items: [p2], warehouse: W2]]
        """)
    void keepsCompanionProductsTogetherWhenPossible(
            List<String> productIds, List<List<String>> companionGroups,
            Map<String, Map<String, StockStatus>> stock, List<WarehouseGroup> shipments) {
        Order order = new Order(productIds.stream().map(this::deliveryItem).toList(), companionGroups);
        WarehouseInventory inventory = new WarehouseInventory(stock);

        List<Shipment> actual = splitter.splitOrder(order, inventory);

        assertEquals(Set.copyOf(shipments), warehouseGroupsOf(actual));
    }

    private OrderItem deliveryItem(String productId) {
        return new OrderItem(productId, 1, FulfillmentType.DELIVERY, "addr-1");
    }

    private WarehouseInventory fullyStockedAt(String warehouseId, List<OrderItem> items) {
        Map<String, StockStatus> stock = items.stream()
                .collect(Collectors.toMap(OrderItem::getProductId, item -> StockStatus.IN_STOCK, (a, b) -> a));
        return new WarehouseInventory(Map.of(warehouseId, stock));
    }

    private static Set<Set<String>> groupsOf(List<Shipment> shipments) {
        return shipments.stream().map(s -> Set.copyOf(s.productIds())).collect(Collectors.toSet());
    }

    private static Set<AvailabilityGroup> availabilityGroupsOf(List<Shipment> shipments) {
        return shipments.stream()
                .map(s -> new AvailabilityGroup(Set.copyOf(s.productIds()), s.availability()))
                .collect(Collectors.toSet());
    }

    private static Set<WarehouseGroup> warehouseGroupsOf(List<Shipment> shipments) {
        return shipments.stream()
                .map(s -> new WarehouseGroup(Set.copyOf(s.productIds()), s.getWarehouseId()))
                .collect(Collectors.toSet());
    }

    public record AvailabilityGroup(Set<String> items, Availability availability) {}

    public record WarehouseGroup(Set<String> items, String warehouse) {}

    @TypeConverter
    public static OrderItem parseOrderItem(Map<String, String> fields) {
        String fulfillment = fields.getOrDefault("fulfillment", "DELIVERY");
        String address = fields.getOrDefault("address", "addr-1");
        return new OrderItem(fields.get("product"), 1, FulfillmentType.valueOf(fulfillment), address);
    }

    @TypeConverter
    @SuppressWarnings("unchecked")
    public static AvailabilityGroup parseAvailabilityGroup(Map<String, Object> fields) {
        return new AvailabilityGroup(
                Set.copyOf((List<String>) fields.get("items")),
                Availability.valueOf((String) fields.get("availability")));
    }

    @TypeConverter
    @SuppressWarnings("unchecked")
    public static WarehouseGroup parseWarehouseGroup(Map<String, Object> fields) {
        return new WarehouseGroup(
                Set.copyOf((List<String>) fields.get("items")),
                (String) fields.get("warehouse"));
    }
}
