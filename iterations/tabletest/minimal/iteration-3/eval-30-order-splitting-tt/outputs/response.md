Here's the complete `OrderSplitTest.java`:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;
import org.tabletest.junit.Description;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Order splitting")
public class OrderSplitTest {

    // ─── Concern 1: Fulfillment type ──────────────────────────────────────────

    @Description("All DELIVERY items share the same delivery address. Inventory has all items in stock at WH1.")
    @TableTest("""
        Scenario                          | Items                                                           | Shipment count? | Fulfillment types?
        All delivery                      | [P1:1:DELIVERY:addr1, P2:1:DELIVERY:addr1]                      | 1               | [DELIVERY]
        All pickup                        | [P1:1:PICKUP, P2:1:PICKUP]                                      | 1               | [PICKUP]
        One delivery, one pickup          | [P1:1:DELIVERY:addr1, P2:1:PICKUP]                              | 2               | [DELIVERY, PICKUP]
        Multiple delivery with one pickup | [P1:1:DELIVERY:addr1, P2:1:DELIVERY:addr1, P3:1:PICKUP]         | 2               | [DELIVERY, PICKUP]
        """)
    void splitsByFulfillmentType(List<OrderItem> items, int shipmentCount,
                                 List<FulfillmentType> fulfillmentTypes) {
        WarehouseInventory inventory = allInStock("WH1", items);

        List<Shipment> shipments = splitOrder(new Order(items), inventory);

        assertThat(shipments).hasSize(shipmentCount);
        assertThat(shipments).extracting(Shipment::getFulfillmentType)
                .containsExactlyInAnyOrderElementsOf(fulfillmentTypes);
    }

    // ─── Concern 2: Delivery address ──────────────────────────────────────────

    @Description("All items are DELIVERY. Inventory has all items in stock at WH1.")
    @TableTest("""
        Scenario                     | Items                                                                         | Shipment count?
        Same address                 | [P1:1:DELIVERY:addr1, P2:1:DELIVERY:addr1]                                    | 1
        Two different addresses      | [P1:1:DELIVERY:addr1, P2:1:DELIVERY:addr2]                                    | 2
        Three different addresses    | [P1:1:DELIVERY:addr1, P2:1:DELIVERY:addr2, P3:1:DELIVERY:addr3]               | 3
        Two same, one different      | [P1:1:DELIVERY:addr1, P2:1:DELIVERY:addr1, P3:1:DELIVERY:addr2]               | 2
        """)
    void splitsByDeliveryAddress(List<OrderItem> items, int shipmentCount) {
        WarehouseInventory inventory = allInStock("WH1", items);

        List<Shipment> shipments = splitOrder(new Order(items), inventory);

        assertThat(shipments).hasSize(shipmentCount);
    }

    // ─── Concern 3: Stock status ──────────────────────────────────────────────

    @Description("""
        All items are DELIVERY to addr1. Each inventory entry is warehouseId:productId:status,
        where status is IN_STOCK, BACKORDER, or PRE_ORDER.
        Immediate products? lists product IDs in shipments that ship now.
        Deferred products? lists product IDs in shipments that ship when available.
        """)
    @TableTest("""
        Scenario                             | Items              | Inventory                                                     | Immediate products? | Deferred products?
        All in stock                         | [P1:1, P2:1]       | [WH1:P1:IN_STOCK, WH1:P2:IN_STOCK]                            | [P1, P2]            | []
        All backordered                      | [P1:1, P2:1]       | [WH1:P1:BACKORDER, WH1:P2:BACKORDER]                          | []                  | [P1, P2]
        Pre-ordered separated from in-stock  | [P1:1, P2:1]       | [WH1:P1:IN_STOCK, WH1:P2:PRE_ORDER]                           | [P1]                | [P2]
        In-stock not held for backordered    | [P1:1, P2:1, P3:1] | [WH1:P1:IN_STOCK, WH1:P2:IN_STOCK, WH1:P3:BACKORDER]          | [P1, P2]            | [P3]
        """)
    void shipsInStockItemsWithoutWaiting(List<OrderItem> items, WarehouseInventory inventory,
                                         List<String> immediateProducts, List<String> deferredProducts) {
        List<Shipment> shipments = splitOrder(new Order(items), inventory);

        List<String> actualImmediate = productIds(shipments, true);
        List<String> actualDeferred = productIds(shipments, false);

        assertThat(actualImmediate).containsExactlyInAnyOrderElementsOf(immediateProducts);
        assertThat(actualDeferred).containsExactlyInAnyOrderElementsOf(deferredProducts);
    }

    // ─── Concern 4: Warehouse selection ───────────────────────────────────────

    @Description("""
        All items are DELIVERY to addr1, all in-stock. Each inventory entry is
        warehouseId:productId:IN_STOCK. The algorithm picks the fewest warehouses
        that together cover all order items.
        """)
    @TableTest("""
        Scenario                              | Items              | Inventory                                                              | Shipment count? | Warehouses used?
        Single warehouse covers all           | [P1:1, P2:1]       | [WH1:P1:IN_STOCK, WH1:P2:IN_STOCK]                                     | 1               | [WH1]
        One product per warehouse             | [P1:1, P2:1]       | [WH1:P1:IN_STOCK, WH2:P2:IN_STOCK]                                     | 2               | [WH1, WH2]
        Full-coverage warehouse preferred     | [P1:1, P2:1]       | [WH1:P1:IN_STOCK, WH1:P2:IN_STOCK, WH2:P1:IN_STOCK]                    | 1               | [WH1]
        Two warehouses beats three            | [P1:1, P2:1, P3:1] | [WH1:P1:IN_STOCK, WH1:P2:IN_STOCK, WH2:P2:IN_STOCK, WH2:P3:IN_STOCK]  | 2               | [WH1, WH2]
        """)
    void minimisesShipmentCountAcrossWarehouses(List<OrderItem> items, WarehouseInventory inventory,
                                                int shipmentCount, List<String> warehousesUsed) {
        List<Shipment> shipments = splitOrder(new Order(items), inventory);

        assertThat(shipments).hasSize(shipmentCount);
        assertThat(shipments).extracting(Shipment::getWarehouseId)
                .containsExactlyInAnyOrderElementsOf(warehousesUsed);
    }

    // ─── Concern 5: Companion products ───────────────────────────────────────

    @Description("""
        All items are DELIVERY to addr1, all in-stock. Companions lists groups of
        product IDs that should ship from the same warehouse when possible (e.g. P1+P2).
        Companion shipments? counts how many distinct shipments contain the companion products:
        1 means all companions shipped together; 2 means they were forced to split.
        """)
    @TableTest("""
        Scenario                               | Companions | Items              | Inventory                                                              | Companion shipments?
        Companions co-located                  | [P1+P2]    | [P1:1, P2:1]       | [WH1:P1:IN_STOCK, WH1:P2:IN_STOCK]                                     | 1
        Companion-covering warehouse preferred | [P1+P2]    | [P1:1, P2:1, P3:1] | [WH1:P1:IN_STOCK, WH1:P2:IN_STOCK, WH2:P1:IN_STOCK, WH2:P3:IN_STOCK]  | 1
        Companions split when unavoidable      | [P1+P2]    | [P1:1, P2:1]       | [WH1:P1:IN_STOCK, WH2:P2:IN_STOCK]                                     | 2
        """)
    void keepsCompanionsTogether(List<CompanionGroup> companions, List<OrderItem> items,
                                  WarehouseInventory inventory, int companionShipments) {
        Order order = new Order(items, companions);

        List<Shipment> shipments = splitOrder(order, inventory);

        Set<String> companionProductIds = companions.stream()
                .flatMap(g -> g.productIds().stream())
                .collect(Collectors.toSet());
        long shipmentsContainingCompanions = shipments.stream()
                .filter(s -> s.getItems().stream()
                        .anyMatch(i -> companionProductIds.contains(i.getProductId())))
                .count();
        assertThat(shipmentsContainingCompanions).isEqualTo(companionShipments);
    }

    // ─── Type converters ──────────────────────────────────────────────────────

    /**
     * productId:qty:DELIVERY:address  — full delivery form
     * productId:qty:PICKUP            — pickup (no address)
     * productId:qty                   — defaults to DELIVERY:addr1
     */
    @TypeConverter
    public static OrderItem parseOrderItem(String value) {
        String[] parts = value.strip().split(":");
        String productId = parts[0];
        int quantity = Integer.parseInt(parts[1]);
        if (parts.length == 2) {
            return new OrderItem(productId, quantity, FulfillmentType.DELIVERY, "addr1");
        }
        FulfillmentType type = FulfillmentType.valueOf(parts[2]);
        String address = parts.length > 3 ? parts[3] : null;
        return new OrderItem(productId, quantity, type, address);
    }

    /** [WH1:P1:IN_STOCK, WH2:P2:BACKORDER, ...] */
    @TypeConverter
    public static WarehouseInventory parseWarehouseInventory(String value) {
        String content = value.strip().replaceAll("^\\[|\\]$", "");
        WarehouseInventory inventory = new WarehouseInventory();
        for (String entry : content.split(",\\s*")) {
            String[] parts = entry.strip().split(":");
            inventory.addStock(parts[0], parts[1], StockStatus.valueOf(parts[2]));
        }
        return inventory;
    }

    /** P1+P2  or  P1+P2+P3 */
    @TypeConverter
    public static CompanionGroup parseCompanionGroup(String value) {
        return new CompanionGroup(Set.of(value.strip().split("\\+")));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static WarehouseInventory allInStock(String warehouseId, List<OrderItem> items) {
        WarehouseInventory inventory = new WarehouseInventory();
        for (OrderItem item : items) {
            inventory.addStock(warehouseId, item.getProductId(), StockStatus.IN_STOCK);
        }
        return inventory;
    }

    private static List<String> productIds(List<Shipment> shipments, boolean immediate) {
        return shipments.stream()
                .filter(s -> s.isImmediate() == immediate)
                .flatMap(s -> s.getItems().stream().map(OrderItem::getProductId))
                .collect(Collectors.toList());
    }
}
```

---

**Five concerns, one table each:**

| Method | Concern | Fixed inputs |
|---|---|---|
| `splitsByFulfillmentType` | DELIVERY vs PICKUP always separate | all in stock at WH1 |
| `splitsByDeliveryAddress` | different addresses always separate | all in stock at WH1 |
| `shipsInStockItemsWithoutWaiting` | in-stock ships now, backordered/pre-order deferred | single warehouse |
| `minimisesShipmentCountAcrossWarehouses` | fewest warehouses to cover the order | all in stock |
| `keepsCompanionsTogether` | companions in same warehouse when possible | all in stock |

**Notable design choices:**

- **`Companion shipments?`** counts distinct shipments that contain companion products — 1 means together, 2 means forced to split. This avoids conditional logic in the test body while clearly expressing the concern.
- **"Two warehouses beats three"** row in concern 4 has a unique optimal solution (WH1+WH2) because P1 only exists at WH1 and P3 only exists at WH2, so those two warehouses are forced; WH3 would be redundant. This makes the `Warehouses used?` expectation deterministic.
- **`FulfillmentType` list** (`[DELIVERY, PICKUP]`) works with JUnit's built-in enum conversion without a custom converter.
- Short-form items (`P1:1`) default to `DELIVERY:addr1` in the converter — documented in `@Description` for the tables that use it.