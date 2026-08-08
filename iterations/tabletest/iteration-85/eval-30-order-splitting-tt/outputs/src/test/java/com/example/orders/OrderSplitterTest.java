package com.example.orders;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSplitterTest {

    private final OrderSplitter splitter = new OrderSplitter();

    @Description("""
        PICKUP items carry no meaningful delivery address, so address only separates DELIVERY items.
        """)
    @TableTest("""
        Scenario                                 | Items                                                                                  | Shipment Groups?
        Same fulfillment type and address        | [[id: p1, type: DELIVERY, address: addr-1], [id: p2, type: DELIVERY, address: addr-1]] | {{p1, p2}}
        Different fulfillment types split apart  | [[id: p1, type: DELIVERY, address: addr-1], [id: p2, type: PICKUP]]                    | {{p1}, {p2}}
        Different delivery addresses split apart | [[id: p1, type: DELIVERY, address: addr-1], [id: p2, type: DELIVERY, address: addr-2]] | {{p1}, {p2}}
        Pickup items ignore delivery address     | [[id: p1, type: PICKUP, address: addr-1], [id: p2, type: PICKUP, address: addr-2]]     | {{p1, p2}}
        """)
    void groupsItemsThatMustShipSeparately(List<OrderItem> items, Set<Set<String>> shipmentGroups) {
        Set<Set<String>> actual = splitter.groupByShipmentKey(items).stream()
                .map(group -> group.stream().map(OrderItem::getProductId).collect(toSet()))
                .collect(toSet());

        assertEquals(shipmentGroups, actual);
    }

    @TableTest("""
        Scenario                              | Stock Status               | Availability?
        In-stock item ships now               | IN_STOCK                   | IMMEDIATE
        Backordered or pre-ordered item waits | {BACKORDERED, PRE_ORDERED} | WHEN_AVAILABLE
        """)
    void resolvesAvailabilityFromStockStatus(StockStatus stockStatus, Availability availability) {
        assertEquals(availability, splitter.resolveAvailability(stockStatus));
    }

    @TableTest("""
        Scenario                                              | Stock Statuses                     | Ships Immediately? | Ships When Available?
        All items in stock ship together                      | [p1: IN_STOCK, p2: IN_STOCK]       | {p1, p2}           | {}
        In-stock item is not held for a backordered companion | [p1: IN_STOCK, p2: BACKORDERED]    | {p1}               | {p2}
        Items awaiting availability ship together when ready  | [p1: BACKORDERED, p2: PRE_ORDERED] | {}                 | {p1, p2}
        """)
    void splitsItemsFromOneWarehouseByAvailability(Map<String, StockStatus> stockStatuses,
                                                     Set<String> shipsImmediately,
                                                     Set<String> shipsWhenAvailable) {
        Map<Availability, Set<String>> actual = splitter.splitByAvailability(stockStatuses);

        assertEquals(shipsImmediately, actual.getOrDefault(Availability.IMMEDIATE, Set.of()));
        assertEquals(shipsWhenAvailable, actual.getOrDefault(Availability.WHEN_AVAILABLE, Set.of()));
    }

    @Description("""
        No companion products in this table; companion co-location preference is covered
        in prefersCompanionCoLocationWhenItDoesNotCostAnExtraWarehouse below.
        """)
    @TableTest("""
        Scenario                                                 | Product Ids  | Warehouses By Product                      | Warehouses Used?
        One warehouse carries the whole order                    | {p1, p2, p3} | [p1: {W1, W2, W3}, p2: {W1, W2}, p3: {W1}] | {W1}
        Products only overlap in stock at exclusive warehouses   | {p1, p2}     | [p1: {W1}, p2: {W2}]                       | {W1, W2}
        Redundant availability still resolves to the smaller set | {p1, p2, p3} | [p1: {W1, W2}, p2: {W1, W2}, p3: {W1}]     | {W1}
        """)
    void choosesTheFewestWarehousesThatCoverTheOrder(Set<String> productIds,
                                                       Map<String, Set<String>> warehousesByProduct,
                                                       Set<String> warehousesUsed) {
        Set<String> actual = splitter.assignWarehouses(productIds, warehousesByProduct, List.of()).keySet();

        assertEquals(warehousesUsed, actual);
    }

    @Description("""
        Both scenarios need exactly two warehouses either way; the companion rule only picks
        between assignments that are already tied on shipment count, and cannot avoid a split
        when no single warehouse carries both companions.
        """)
    @TableTest("""
        Scenario                                                    | Product Ids                | Warehouses By Product                            | Companion Groups      | Warehouse Assignment?
        Companions ship together when either warehouse works        | {camera-body, lens, strap} | [camera-body: {W1, W2}, lens: {W1}, strap: {W2}] | [{camera-body, lens}] | [W1: {camera-body, lens}, W2: {strap}]
        Companions ship apart when no shared warehouse is available | {camera-body, lens}        | [camera-body: {W1}, lens: {W2}]                  | [{camera-body, lens}] | [W1: {camera-body}, W2: {lens}]
        """)
    void prefersCompanionCoLocationWhenItDoesNotCostAnExtraWarehouse(Set<String> productIds,
                                                                       Map<String, Set<String>> warehousesByProduct,
                                                                       List<Set<String>> companionGroups,
                                                                       Map<String, Set<String>> warehouseAssignment) {
        Map<String, Set<String>> actual = splitter.assignWarehouses(productIds, warehousesByProduct, companionGroups);

        assertEquals(warehouseAssignment, actual);
    }

    @Description("""
        Verifies the rules above are wired together through the public splitOrder API. Companion
        products are read from WarehouseInventory.addCompanionGroup (a catalog-level fact);
        Order.getCompanionGroups is not used by this feature.

        p1 and p4 are both DELIVERY and both ship from W1, but to different addresses, so they
        must land in different shipments; p1 requires W1 and p3 requires W2, forcing two
        warehouses for the addr-1 group regardless of companions, so the companion rule places
        p2 alongside p3 at W2 at no extra shipment cost.
        """)
    @TableTest("""
        Scenario                                                     | Items                                                                                                                                                                                                | Companion Groups | Stock By Warehouse                                                                               | Shipments?
        Combines fulfillment, address, warehouse and companion rules | [[id: p1, type: DELIVERY, address: addr-1], [id: p2, type: DELIVERY, address: addr-1], [id: p3, type: DELIVERY, address: addr-1], [id: p4, type: DELIVERY, address: addr-2], [id: p5, type: PICKUP]] | [{p2, p3}]       | [W1: [p1: IN_STOCK, p2: IN_STOCK, p4: IN_STOCK, p5: IN_STOCK], W2: [p2: IN_STOCK, p3: IN_STOCK]] | [DELIVERY@addr-1@W1@IMMEDIATE: {p1}, DELIVERY@addr-1@W2@IMMEDIATE: {p2, p3}, DELIVERY@addr-2@W1@IMMEDIATE: {p4}, PICKUP@-@W1@IMMEDIATE: {p5}]
        """)
    void splitsOrderAccordingToAllRulesTogether(List<OrderItem> items,
                                                 List<Set<String>> companionGroups,
                                                 Map<String, Map<String, StockStatus>> stockByWarehouse,
                                                 Map<String, Set<String>> shipments) {
        WarehouseInventory inventory = new WarehouseInventory(stockByWarehouse);
        companionGroups.forEach(inventory::addCompanionGroup);

        List<Shipment> actual = splitter.splitOrder(new Order(items), inventory);

        assertEquals(shipments, toShipmentsByKey(actual));
    }

    @TypeConverter
    public static OrderItem toOrderItem(Map<String, String> fields) {
        return new OrderItem(
                fields.get("id"),
                1,
                FulfillmentType.valueOf(fields.get("type")),
                fields.get("address"));
    }

    private static Map<String, Set<String>> toShipmentsByKey(List<Shipment> shipments) {
        return shipments.stream().collect(toMap(
                OrderSplitterTest::shipmentKey,
                shipment -> new HashSet<>(shipment.productIds())));
    }

    private static String shipmentKey(Shipment shipment) {
        String address = shipment.getItems().isEmpty() ? "-" : shipment.getItems().get(0).getDeliveryAddress();
        return shipment.getFulfillmentType() + "@" + (address == null ? "-" : address) + "@"
                + shipment.getWarehouseId() + "@" + shipment.getAvailability();
    }
}
