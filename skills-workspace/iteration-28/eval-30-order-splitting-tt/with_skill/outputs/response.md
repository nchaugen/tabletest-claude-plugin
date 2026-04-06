Here's the complete `SplitOrderTest.java`:

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.tabletest.Description;
import org.tabletest.TableTest;
import org.tabletest.TypeConverter;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SplitOrderTest {

    private OrderSplitter splitter;

    @BeforeEach
    void setUp() {
        splitter = new OrderSplitter();
    }

    // -------------------------------------------------------------------------
    // Concern 1: Group items by fulfillment type and delivery address
    // -------------------------------------------------------------------------

    @DisplayName("Group items by fulfillment type and delivery address")
    @Description("""
        Inventory is WH1 with all items in stock — warehouse assignment and
        availability are not the focus here. Tests how items are partitioned
        into shipment groups by fulfillment type (DELIVERY vs PICKUP) and
        delivery address.
        """)
    @TableTest("""
        Scenario                         | Items                                                           | Shipment groups?
        Single delivery item             | [P1:1:DELIVERY:Main-St]                                         | [DELIVERY:Main-St:P1]
        Single pickup item               | [P1:1:PICKUP]                                                   | [PICKUP:P1]
        Delivery and pickup split        | [P1:1:DELIVERY:Main-St, P2:1:PICKUP]                            | [DELIVERY:Main-St:P1, PICKUP:P2]
        Same address stays together      | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]                  | [DELIVERY:Main-St:P1+P2]
        Different addresses split        | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Oak-Ave]                  | [DELIVERY:Main-St:P1, DELIVERY:Oak-Ave:P2]
        Multiple pickups stay together   | [P1:1:PICKUP, P2:1:PICKUP]                                      | [PICKUP:P1+P2]
        Delivery, pickup, two addresses  | [P1:1:DELIVERY:Main-St, P2:1:PICKUP, P3:1:DELIVERY:Oak-Ave]    | [DELIVERY:Main-St:P1, DELIVERY:Oak-Ave:P3, PICKUP:P2]
        """)
    void shouldGroupItemsByFulfillmentTypeAndAddress(Order order, List<String> shipmentGroups) {
        List<Shipment> shipments = splitter.splitOrder(order, allInStock("WH1", order));
        assertThat(describeGroups(shipments)).containsExactlyInAnyOrderElementsOf(shipmentGroups);
    }

    // -------------------------------------------------------------------------
    // Concern 2: Split in-stock items from backordered / pre-ordered
    // -------------------------------------------------------------------------

    @DisplayName("Split in-stock items from backordered and pre-ordered items")
    @Description("""
        In-stock items must ship in their own immediate shipment — they must not
        be held for backordered or pre-ordered items in the same address/type group.
        All items use DELIVERY to Main-St throughout; only availability varies.
        Inventory format: [WHx@productId:STATUS, ...] where STATUS is
        INSTOCK, BACKORDERED, or PREORDERED.
        """)
    @TableTest("""
        Scenario                               | Items                                              | Inventory                                       | Immediate items? | Deferred items?
        All in stock ships immediately         | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]    | [WH1@P1:INSTOCK, WH1@P2:INSTOCK]               | [P1, P2]         | []
        All backordered defers all             | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]    | [WH1@P1:BACKORDERED, WH1@P2:BACKORDERED]        | []               | [P1, P2]
        In-stock not held for backordered      | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]    | [WH1@P1:INSTOCK, WH1@P2:BACKORDERED]            | [P1]             | [P2]
        In-stock not held for pre-ordered      | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]    | [WH1@P1:INSTOCK, WH1@P2:PREORDERED]             | [P1]             | [P2]
        Backordered and pre-ordered both defer | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]    | [WH1@P1:BACKORDERED, WH1@P2:PREORDERED]         | []               | [P1, P2]
        In-stock at alternative warehouse      | [P1:1:DELIVERY:Main-St]                            | [WH1@P1:BACKORDERED, WH2@P1:INSTOCK]            | [P1]             | []
        """)
    void shouldSplitByAvailability(Order order, WarehouseInventory inventory,
                                   List<String> immediateItems, List<String> deferredItems) {
        List<Shipment> shipments = splitter.splitOrder(order, inventory);
        assertThat(immediateProductIds(shipments)).containsExactlyInAnyOrderElementsOf(immediateItems);
        assertThat(deferredProductIds(shipments)).containsExactlyInAnyOrderElementsOf(deferredItems);
    }

    // -------------------------------------------------------------------------
    // Concern 3: Minimise shipment count when splitting across warehouses
    // -------------------------------------------------------------------------

    @DisplayName("Minimise shipment count when splitting across warehouses")
    @Description("""
        When no single warehouse stocks every item, the algorithm picks the
        smallest set of warehouses that covers the full order, minimising
        the number of resulting shipments. All items here use DELIVERY to
        Main-St and are in stock — only warehouse coverage varies.
        Open: when two equally minimal warehouse combinations exist
        (same count, different warehouses), which is preferred?
        The tie-breaking rule is not yet specified; all rows below have a
        unique minimum to avoid testing unspecified behaviour.
        """)
    @TableTest("""
        Scenario                                   | Items                                                                   | Inventory                                                                          | Shipment count? | Warehouses used?
        Single warehouse covers all                | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]                          | [WH1@P1:INSTOCK, WH1@P2:INSTOCK]                                                  | 1               | [WH1]
        No single warehouse covers both            | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]                          | [WH1@P1:INSTOCK, WH2@P2:INSTOCK]                                                  | 2               | [WH1, WH2]
        One warehouse covers all despite two avail | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St]                          | [WH1@P1:INSTOCK, WH1@P2:INSTOCK, WH2@P1:INSTOCK]                                 | 1               | [WH1]
        Two warehouses sufficient, not three       | [P1:1:DELIVERY:Main-St, P2:1:DELIVERY:Main-St, P3:1:DELIVERY:Main-St]  | [WH1@P1:INSTOCK, WH1@P2:INSTOCK, WH2@P2:INSTOCK, WH2@P3:INSTOCK]                | 2               | [WH1, WH2]
        """)
    void shouldMinimiseShipmentCount(Order order, WarehouseInventory inventory,
                                     int shipmentCount, List<String> warehousesUsed) {
        List<Shipment> shipments = splitter.splitOrder(order, inventory);
        assertThat(shipments).hasSize(shipmentCount);
        assertThat(shipments.stream().map(Shipment::getWarehouseId).toList())
            .containsExactlyInAnyOrderElementsOf(warehousesUsed);
    }

    // -------------------------------------------------------------------------
    // Concern 4: Companion products ship from the same warehouse
    // -------------------------------------------------------------------------

    @DisplayName("Companion products ship from the same warehouse when possible")
    @Description("""
        Items in the same companion group (e.g. camera body + lens) must
        come from the same warehouse whenever any warehouse stocks the
        full group. Item format: productId:qty:FULFILLMENT:address:companionGroup
        Open: when no single warehouse stocks the full companion group,
        what is the fallback? The requirement says "when possible" but
        does not specify behaviour for the impossible case — clarification needed.
        """)
    @TableTest("""
        Scenario                                  | Items                                                                                          | Inventory                                                       | Companion shipment?
        Full group at one warehouse               | [CAMERA:1:DELIVERY:Main-St:cam-kit, LENS:1:DELIVERY:Main-St:cam-kit]                           | [WH1@CAMERA:INSTOCK, WH1@LENS:INSTOCK]                         | WH1:CAMERA+LENS
        Full group at WH1 preferred over split    | [CAMERA:1:DELIVERY:Main-St:cam-kit, LENS:1:DELIVERY:Main-St:cam-kit]                           | [WH1@CAMERA:INSTOCK, WH1@LENS:INSTOCK, WH2@CAMERA:INSTOCK]    | WH1:CAMERA+LENS
        Non-companion items do not break grouping | [CAMERA:1:DELIVERY:Main-St:cam-kit, LENS:1:DELIVERY:Main-St:cam-kit, CASE:1:DELIVERY:Main-St]  | [WH1@CAMERA:INSTOCK, WH1@LENS:INSTOCK, WH2@CASE:INSTOCK]      | WH1:CAMERA+LENS
        """)
    void shouldKeepCompanionsTogether(Order order, WarehouseInventory inventory, String companionShipment) {
        List<Shipment> shipments = splitter.splitOrder(order, inventory);
        Shipment camShipment = shipments.stream()
            .filter(s -> s.getItems().stream().anyMatch(i -> "cam-kit".equals(i.getCompanionGroup())))
            .findFirst()
            .orElseThrow(() -> new AssertionError("No shipment found for companion group cam-kit"));
        assertThat(describeCompanionShipment(camShipment)).isEqualTo(companionShipment);
    }

    // =========================================================================
    // Type Converters
    // =========================================================================

    /**
     * Parses an Order from table cell format: [productId:qty:FULFILLMENT:address:companionGroup, ...]
     * Address is omitted for PICKUP items. companionGroup is optional.
     * Example: [P1:1:DELIVERY:Main-St, P2:1:PICKUP, CAM:1:DELIVERY:Main-St:cam-kit]
     */
    @TypeConverter
    public static Order parseOrder(String value) {
        if (value == null || value.isBlank()) return new Order(List.of());
        String inner = value.strip().replaceAll("^\\[|\\]$", "");
        List<OrderItem> items = Arrays.stream(inner.split(",\\s*"))
            .map(SplitOrderTest::parseItem)
            .toList();
        return new Order(items);
    }

    /**
     * Parses a WarehouseInventory from table cell format: [WHx@productId:STATUS, ...]
     * STATUS is one of: INSTOCK, BACKORDERED, PREORDERED
     * Example: [WH1@P1:INSTOCK, WH1@P2:BACKORDERED, WH2@P3:INSTOCK]
     */
    @TypeConverter
    public static WarehouseInventory parseInventory(String value) {
        if (value == null || value.isBlank()) return WarehouseInventory.empty();
        String inner = value.strip().replaceAll("^\\[|\\]$", "");
        Map<String, Map<String, AvailabilityStatus>> stock = new LinkedHashMap<>();
        for (String entry : inner.split(",\\s*")) {
            String[] wh = entry.split("@");
            String warehouseId = wh[0].strip();
            String[] product = wh[1].split(":");
            String productId = product[0].strip();
            AvailabilityStatus status = AvailabilityStatus.valueOf(product[1].strip());
            stock.computeIfAbsent(warehouseId, k -> new LinkedHashMap<>()).put(productId, status);
        }
        return new WarehouseInventory(stock);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private static OrderItem parseItem(String s) {
        String[] p = s.strip().split(":");
        String productId = p[0];
        int quantity = Integer.parseInt(p[1]);
        FulfillmentType type = FulfillmentType.valueOf(p[2]);
        String address = p.length > 3 ? p[3] : null;
        String companionGroup = p.length > 4 ? p[4] : null;
        return new OrderItem(productId, quantity, type, address, companionGroup);
    }

    /** Builds a single-warehouse inventory with all order items in stock. */
    private WarehouseInventory allInStock(String warehouseId, Order order) {
        Map<String, AvailabilityStatus> products = order.getItems().stream()
            .collect(Collectors.toMap(OrderItem::getProductId, ignored -> AvailabilityStatus.INSTOCK));
        return new WarehouseInventory(Map.of(warehouseId, products));
    }

    /**
     * Returns a sorted list of shipment descriptions for assertion.
     * Format: "DELIVERY:address:item1+item2" or "PICKUP:item1+item2"
     */
    private List<String> describeGroups(List<Shipment> shipments) {
        return shipments.stream()
            .map(s -> {
                String items = s.getItems().stream()
                    .map(OrderItem::getProductId)
                    .sorted()
                    .collect(Collectors.joining("+"));
                return s.getFulfillmentType() == FulfillmentType.PICKUP
                    ? "PICKUP:" + items
                    : "DELIVERY:" + s.getDeliveryAddress() + ":" + items;
            })
            .sorted()
            .toList();
    }

    private List<String> immediateProductIds(List<Shipment> shipments) {
        return shipments.stream()
            .filter(s -> s.getAvailability() == AvailabilityStatus.INSTOCK)
            .flatMap(s -> s.getItems().stream())
            .map(OrderItem::getProductId)
            .sorted()
            .toList();
    }

    private List<String> deferredProductIds(List<Shipment> shipments) {
        return shipments.stream()
            .filter(s -> s.getAvailability() != AvailabilityStatus.INSTOCK)
            .flatMap(s -> s.getItems().stream())
            .map(OrderItem::getProductId)
            .sorted()
            .toList();
    }

    /** Describes the companion shipment as "WHx:ITEM1+ITEM2" */
    private String describeCompanionShipment(Shipment shipment) {
        String items = shipment.getItems().stream()
            .filter(i -> i.getCompanionGroup() != null)
            .map(OrderItem::getProductId)
            .sorted()
            .collect(Collectors.joining("+"));
        return shipment.getWarehouseId() + ":" + items;
    }
}
```

---

**Four concerns, four tables:**

| Table | What it tests | Key rows |
|-------|--------------|----------|
| `shouldGroupItemsByFulfillmentTypeAndAddress` | DELIVERY vs PICKUP split; address split | 7 rows including multi-way split |
| `shouldSplitByAvailability` | In-stock not held; BACKORDERED and PREORDERED both defer; in-stock at alt warehouse ships immediately | 6 rows |
| `shouldMinimiseShipmentCount` | Single WH covers all; 2 needed; 1 optimal despite 2 available | 4 rows |
| `shouldKeepCompanionsTogether` | Companions collocated; WH with full group preferred; non-companions don't interfere | 3 rows |

**Key design decisions:**
- `@` as warehouse-product separator in inventory (avoids `|` column-separator conflict)
- `Items` column uses a `@TypeConverter` → `Order`; `Inventory` → `WarehouseInventory`
- `Shipment groups?` uses `List<String>` with auto-parsed `[...]` syntax
- `allInStock` helper keeps Concern 1's rows focused on grouping logic
- Two open questions surfaced and noted in `@Description`: tie-breaking for equal-cost warehouse combos, and companion fallback when no single WH stocks the full group