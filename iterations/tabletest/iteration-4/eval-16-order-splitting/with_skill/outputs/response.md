Now I have what I need. The test class must be `public` for TypeConverter discovery, and converter methods must be `public static`.

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Description(
    "An order is split into the fewest shipments that satisfy all fulfilment constraints. " +
    "Splitting is required when items differ in fulfilment method, stock status, delivery " +
    "address, or when no single warehouse has all items in stock. " +
    "Companion products — items that must travel together (e.g. a camera body and its " +
    "matching lens) — are always grouped in the same shipment from the same warehouse.")
@DisplayName("Order shipment splitting")
public class OrderSplittingTest {

    private final OrderSplitter splitter = new OrderSplitter();

    @TableTest("""
        Scenario                          | Items                                                                          | Shipment count? | Item groups?
        All items compatible              | [shirt:DELIVERY:IN_STOCK:WH1:home, trousers:DELIVERY:IN_STOCK:WH1:home]        | 1               | [shirt+trousers]
        Mixed fulfilment methods          | [shirt:DELIVERY:IN_STOCK:WH1:home, gift:PICKUP:IN_STOCK:WH1:home]              | 2               | [shirt, gift]
        Mixed stock status                | [shirt:DELIVERY:IN_STOCK:WH1:home, lens:DELIVERY:PRE_ORDER:WH1:home]           | 2               | [shirt, lens]
        No single warehouse covers all    | [shirt:DELIVERY:IN_STOCK:WH1:home, shoes:DELIVERY:IN_STOCK:WH2:home]           | 2               | [shirt, shoes]
        Different delivery addresses      | [gift-A:DELIVERY:IN_STOCK:WH1:addr-A, gift-B:DELIVERY:IN_STOCK:WH1:addr-B]    | 2               | [gift-A, gift-B]
        """)
    @DisplayName("Splitting triggers")
    @Description(
        "Each constraint independently forces a split. Items are grouped into a single " +
        "shipment only when they share the same fulfilment method, stock status, " +
        "warehouse, and delivery address.")
    void splittingTriggers(List<OrderItem> items, int shipmentCount, List<String> itemGroups) {
        List<Shipment> result = splitter.split(new Order(items));
        assertThat(result).hasSize(shipmentCount);
        assertShipmentGroups(result, itemGroups);
    }

    @TableTest("""
        Scenario                           | Items                                                                                                                          | Shipment count? | Item groups?
        Two items at WH1, one at WH2       | [shirt:DELIVERY:IN_STOCK:WH1:home, trousers:DELIVERY:IN_STOCK:WH1:home, shoes:DELIVERY:IN_STOCK:WH2:home]                      | 2               | [shirt+trousers, shoes]
        Three items across three locations | [hat:DELIVERY:IN_STOCK:WH1:home, coat:DELIVERY:IN_STOCK:WH2:home, boots:DELIVERY:IN_STOCK:WH3:home]                            | 3               | [hat, coat, boots]
        All items at same warehouse        | [hat:DELIVERY:IN_STOCK:WH1:home, coat:DELIVERY:IN_STOCK:WH1:home, boots:DELIVERY:IN_STOCK:WH1:home]                            | 1               | [hat+coat+boots]
        """)
    @DisplayName("Minimise shipment count")
    @Description(
        "When items span multiple warehouses, items are grouped to produce the fewest " +
        "shipments. Two items stocked at WH1 and one item stocked only at WH2 yields " +
        "two shipments — not three.")
    void minimisesShipmentCount(List<OrderItem> items, int shipmentCount, List<String> itemGroups) {
        List<Shipment> result = splitter.split(new Order(items));
        assertThat(result).hasSize(shipmentCount);
        assertShipmentGroups(result, itemGroups);
    }

    @TableTest("""
        Scenario                                    | Items                                                                                                                        | Shipment count? | Item groups?
        Companions at same warehouse                | [camera:DELIVERY:IN_STOCK:WH1:home:kit, lens:DELIVERY:IN_STOCK:WH1:home:kit]                                                  | 1               | [camera+lens]
        Companions ship alongside non-companion     | [camera:DELIVERY:IN_STOCK:WH1:home:kit, lens:DELIVERY:IN_STOCK:WH1:home:kit, bag:DELIVERY:IN_STOCK:WH1:home]                  | 1               | [camera+lens+bag]
        Companion group separated from other items  | [camera:DELIVERY:IN_STOCK:WH1:home:kit, lens:DELIVERY:IN_STOCK:WH1:home:kit, scarf:DELIVERY:IN_STOCK:WH2:home]                | 2               | [camera+lens, scarf]
        """)
    @DisplayName("Companion products ship together")
    @Description(
        "Items sharing a companion group must be placed in the same shipment from the " +
        "same warehouse. The companion constraint is never relaxed for optimisation — " +
        "companions are kept together even when separating them would reduce the " +
        "overall shipment count.")
    void companionProductsShipTogether(List<OrderItem> items, int shipmentCount, List<String> itemGroups) {
        List<Shipment> result = splitter.split(new Order(items));
        assertThat(result).hasSize(shipmentCount);
        assertShipmentGroups(result, itemGroups);
    }

    @Test
    @DisplayName("Companions with no shared warehouse cannot be fulfilled")
    @Description(
        "If companion products are not co-stocked at any single warehouse, the order " +
        "cannot be split into valid shipments and fulfilment is rejected.")
    void companionsWithNoSharedWarehouseAreRejected() {
        Order order = new Order(List.of(
            new OrderItem("camera", FulfilmentMethod.DELIVERY, StockStatus.IN_STOCK, "WH1", "home", "kit"),
            new OrderItem("lens",   FulfilmentMethod.DELIVERY, StockStatus.IN_STOCK, "WH2", "home", "kit")
        ));
        assertThatThrownBy(() -> splitter.split(order))
            .isInstanceOf(UnfulfillableOrderException.class)
            .hasMessageContaining("kit");
    }

    // --- Type converter ---

    /**
     * Parses an item descriptor in the form:
     *   productId:METHOD:STATUS:warehouse:address
     *   productId:METHOD:STATUS:warehouse:address:companionGroup
     */
    @TypeConverter
    public static OrderItem parseOrderItem(String value) {
        String[] parts = value.split(":");
        return new OrderItem(
            parts[0],
            FulfilmentMethod.valueOf(parts[1]),
            StockStatus.valueOf(parts[2]),
            parts[3],
            parts[4],
            parts.length > 5 ? parts[5] : null
        );
    }

    // --- Helpers ---

    /**
     * Asserts that shipments contain exactly the item groups described in expectedGroups.
     * Each group string uses '+' to separate items within the same shipment,
     * e.g. "shirt+trousers" means shirt and trousers shipped together.
     */
    private void assertShipmentGroups(List<Shipment> shipments, List<String> expectedGroups) {
        List<String> actual = shipments.stream()
            .map(s -> s.items().stream()
                .map(OrderItem::productId)
                .sorted()
                .collect(Collectors.joining("+")))
            .sorted()
            .collect(Collectors.toList());

        List<String> expected = expectedGroups.stream()
            .map(g -> Arrays.stream(g.split("\\+")).sorted().collect(Collectors.joining("+")))
            .sorted()
            .collect(Collectors.toList());

        assertThat(actual).isEqualTo(expected);
    }
}
```

---

**Design notes:**

**4 tests, 3 concerns:**
- `splittingTriggers` — one row per rule, each in isolation. The "all compatible" row establishes the baseline.
- `minimisesShipmentCount` — tests the optimization constraint separately. The same warehouse scenario is included to show that no split happens when unnecessary.
- `companionProductsShipTogether` — three rows covering: companions alone, companions plus a co-located non-companion (no forced split), companions separated from a different-warehouse item.
- `companionsWithNoSharedWarehouseAreRejected` — a `@Test` because it's a single scenario with exception-specific assertion logic that doesn't share the same assertion shape as the others.

**Item groups column** uses `+` as an intra-group separator within each list element (e.g., `[shirt+trousers, shoes]`). This is a plain `List<String>` — no custom converter needed. The `assertShipmentGroups` helper normalises and compares order-independently.

**Traceability:** both `Shipment count?` and `Item groups?` are included — the count assertion fails fast if the split is wrong, and the groups column lets you trace exactly which items ended up together.