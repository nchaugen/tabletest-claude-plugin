package com.example.orders;

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

    @Description("""
        Every row uses a single warehouse (W1) so results isolate the grouping rule
        from warehouse selection, which is covered separately below. IN_STOCK maps
        to IMMEDIATE availability; BACKORDERED and PRE_ORDERED both map to
        WHEN_AVAILABLE and merge into one shipment when other attributes match.
        """)
    @TableTest("""
        Scenario                                             | Items                                              | Stock                                | Shipments?
        Different fulfillment types split                    | [cam/1/DELIVERY/home, tripod/1/PICKUP/-]           | [cam: IN_STOCK, tripod: IN_STOCK]    | [W1-DELIVERY-IMMEDIATE-cam, W1-PICKUP-IMMEDIATE-tripod]
        Different delivery addresses split                   | [cam/1/DELIVERY/home, lens/1/DELIVERY/office]      | [cam: IN_STOCK, lens: IN_STOCK]      | [W1-DELIVERY-IMMEDIATE-cam, W1-DELIVERY-IMMEDIATE-lens]
        Same fulfillment type and address merge               | [cam/1/DELIVERY/home, lens/1/DELIVERY/home]        | [cam: IN_STOCK, lens: IN_STOCK]      | [W1-DELIVERY-IMMEDIATE-cam+lens]
        In-stock item ships without waiting for backorder      | [cam/1/DELIVERY/home, lens/1/DELIVERY/home]        | [cam: IN_STOCK, lens: BACKORDERED]   | [W1-DELIVERY-IMMEDIATE-cam, W1-DELIVERY-WHEN_AVAILABLE-lens]
        In-stock item ships without waiting for pre-order       | [cam/1/DELIVERY/home, lens/1/DELIVERY/home]        | [cam: IN_STOCK, lens: PRE_ORDERED]   | [W1-DELIVERY-IMMEDIATE-cam, W1-DELIVERY-WHEN_AVAILABLE-lens]
        Backordered and pre-ordered items ship together        | [cam/1/DELIVERY/home, lens/1/DELIVERY/home]        | [cam: BACKORDERED, lens: PRE_ORDERED]| [W1-DELIVERY-WHEN_AVAILABLE-cam+lens]
        Pickup items split by availability too                 | [tripod/1/PICKUP/-, dock/1/PICKUP/-]               | [tripod: IN_STOCK, dock: BACKORDERED]| [W1-PICKUP-IMMEDIATE-tripod, W1-PICKUP-WHEN_AVAILABLE-dock]
        """)
    void groupsItemsByFulfillmentTypeAddressAndAvailability(
            List<OrderItem> items, Map<String, StockStatus> stock, List<String> shipments) {
        WarehouseInventory inventory = new WarehouseInventory();
        addStock(inventory, "W1", stock);

        assertEquals(sorted(shipments), describeAll(splitter.splitOrder(new Order(items), inventory)));
    }

    @Description("""
        Every item is DELIVERY to the same address and IN_STOCK wherever it is
        stocked, isolating the warehouse-selection rule: pick the combination of
        warehouses that covers the order in the fewest shipments.
        """)
    @TableTest("""
        Scenario                                            | Items                                                                    | W1 Stock                                  | W2 Stock                | W3 Stock                | Shipments?
        Single warehouse covers the whole order              | [cam/1/DELIVERY/home, lens/1/DELIVERY/home]                              | [cam: IN_STOCK, lens: IN_STOCK]           | [cam: IN_STOCK]         |                         | [W1-DELIVERY-IMMEDIATE-cam+lens]
        No warehouse covers all items; split across two      | [cam/1/DELIVERY/home, lens/1/DELIVERY/home, tripod/1/DELIVERY/home]      | [cam: IN_STOCK, lens: IN_STOCK]           | [tripod: IN_STOCK]      |                         | [W1-DELIVERY-IMMEDIATE-cam+lens, W2-DELIVERY-IMMEDIATE-tripod]
        Full coverage requires three warehouses               | [cam/1/DELIVERY/home, lens/1/DELIVERY/home, tripod/1/DELIVERY/home]      | [cam: IN_STOCK]                           | [lens: IN_STOCK]        | [tripod: IN_STOCK]      | [W1-DELIVERY-IMMEDIATE-cam, W2-DELIVERY-IMMEDIATE-lens, W3-DELIVERY-IMMEDIATE-tripod]
        Prefers one warehouse covering most items plus a leftover | [cam/1/DELIVERY/home, lens/1/DELIVERY/home, tripod/1/DELIVERY/home, dock/1/DELIVERY/home] | [cam: IN_STOCK, lens: IN_STOCK, tripod: IN_STOCK] | [dock: IN_STOCK]        |                         | [W1-DELIVERY-IMMEDIATE-cam+lens+tripod, W2-DELIVERY-IMMEDIATE-dock]
        """)
    void choosesWarehouseCombinationMinimisingShipmentCount(
            List<OrderItem> items, Map<String, StockStatus> w1Stock, Map<String, StockStatus> w2Stock,
            Map<String, StockStatus> w3Stock, List<String> shipments) {
        WarehouseInventory inventory = new WarehouseInventory();
        addStock(inventory, "W1", w1Stock);
        addStock(inventory, "W2", w2Stock);
        addStock(inventory, "W3", w3Stock);

        assertEquals(sorted(shipments), describeAll(splitter.splitOrder(new Order(items), inventory)));
    }

    @Description("""
        Companion relationships are modelled via WarehouseInventory.addCompanionGroup
        (Order's own companionGroups constructor parameter is left unused here, since
        the stub carries the same concept in both places; inventory-level catalog
        data is the more natural home for "which products are companions"). Shipping
        companions from the same warehouse is a tie-break: it only wins when it does
        not increase the total shipment count beyond the true minimum.
        """)
    @TableTest("""
        Scenario                                                        | Items                                                                       | Companions   | W1 Stock                          | W2 Stock                          | W3 Stock                | Shipments?
        Ships companions together when it costs no extra shipment       | [body/1/DELIVERY/home, lens/1/DELIVERY/home, mount/1/DELIVERY/home]         | {body, lens} | [body: IN_STOCK, lens: IN_STOCK]  | [lens: IN_STOCK, mount: IN_STOCK] |                         | [W1-DELIVERY-IMMEDIATE-body+lens, W2-DELIVERY-IMMEDIATE-mount]
        Splits companions when grouping them would add a shipment       | [body/1/DELIVERY/home, lens/1/DELIVERY/home, mount/1/DELIVERY/home, cable/1/DELIVERY/home] | {body, lens} | [body: IN_STOCK, mount: IN_STOCK] | [lens: IN_STOCK, cable: IN_STOCK] | [body: IN_STOCK, lens: IN_STOCK] | [W1-DELIVERY-IMMEDIATE-body+mount, W2-DELIVERY-IMMEDIATE-cable+lens]
        Splits companions when no warehouse stocks both                 | [body/1/DELIVERY/home, lens/1/DELIVERY/home]                                | {body, lens} | [body: IN_STOCK]                  | [lens: IN_STOCK]                  |                         | [W1-DELIVERY-IMMEDIATE-body, W2-DELIVERY-IMMEDIATE-lens]
        """)
    void prefersShippingCompanionsFromTheSameWarehouseWhenItDoesNotCostExtraShipments(
            List<OrderItem> items, Set<String> companions, Map<String, StockStatus> w1Stock,
            Map<String, StockStatus> w2Stock, Map<String, StockStatus> w3Stock, List<String> shipments) {
        WarehouseInventory inventory = new WarehouseInventory();
        addStock(inventory, "W1", w1Stock);
        addStock(inventory, "W2", w2Stock);
        addStock(inventory, "W3", w3Stock);
        inventory.addCompanionGroup(companions);

        assertEquals(sorted(shipments), describeAll(splitter.splitOrder(new Order(items), inventory)));
    }

    @TypeConverter
    public static OrderItem parseOrderItem(String value) {
        String[] parts = value.split("/", -1);
        String productId = parts[0];
        int quantity = Integer.parseInt(parts[1]);
        FulfillmentType fulfillmentType = FulfillmentType.valueOf(parts[2]);
        String deliveryAddress = "-".equals(parts[3]) ? null : parts[3];
        return new OrderItem(productId, quantity, fulfillmentType, deliveryAddress);
    }

    private static void addStock(WarehouseInventory inventory, String warehouseId, Map<String, StockStatus> stock) {
        if (stock == null) {
            return;
        }
        stock.forEach((productId, status) -> inventory.addStock(warehouseId, productId, status));
    }

    private static String describe(Shipment shipment) {
        String productIds = shipment.items().stream()
                .map(OrderItem::getProductId)
                .sorted()
                .collect(Collectors.joining("+"));
        return shipment.getWarehouseId() + "-" + shipment.getFulfillmentType() + "-" + shipment.getAvailability() + "-" + productIds;
    }

    private static List<String> describeAll(List<Shipment> shipments) {
        return shipments.stream().map(OrderSplitterTest::describe).sorted().toList();
    }

    private static List<String> sorted(List<String> values) {
        return values.stream().sorted().toList();
    }
}
