```java
            Open: behaviour when productId is absent from the catalogue is unspecified.
            """)
    @TableTest("""
            Scenario                  | Cart before   | Product | Qty | Catalogue                     | Cart after?             | Message?
            Add to empty cart         | [:]           | WIDGET  | 2   | [WIDGET: 10.00]               | [WIDGET: 2]             | Added 2x Widget
            Add new item              | [WIDGET: 1]   | GADGET  | 3   | [WIDGET: 10.00, GADGET: 5.00] | [WIDGET: 1, GADGET: 3]  | Added 3x Gadget
            Accumulate existing item  | [WIDGET: 1]   | WIDGET  | 2   | [WIDGET: 10.00]               | [WIDGET: 3]             | Added 2x Widget
            """)
    void shouldAddItemToCart(Cart cartBefore, String productId, int qty, ProductCatalogue catalogue,
                             Cart expectedCart, String expectedMessage) {
        CartResult result = addItem(cartBefore, productId, qty, catalogue);
        assertThat(result.message()).isEqualTo(expectedMessage);
        assertThat(result.cart().items()).isEqualTo(expectedCart.items());
    }

    // ==================== Remove Item ====================

    @DisplayName("Remove item from cart")
    @TableTest("""
            Scenario               | Cart before             | Product | Success? | Cart after?   | Message?
            Remove one of many     | [WIDGET: 2, GADGET: 1]  | WIDGET  | true     | [GADGET: 1]   | Removed Widget
            Remove last item       | [WIDGET: 2]             | WIDGET  | true     | [:]           | Removed Widget
            Item not in cart       | [WIDGET: 2]             | GADGET  | false    | [WIDGET: 2]   | "Item not found: Gadget"
            Remove from empty cart | [:]                     | WIDGET  | false    | [:]           | "Item not found: Widget"
            """)
    void shouldRemoveItemFromCart(Cart cartBefore, String productId, boolean success,
                                  Cart expectedCart, String expectedMessage) {
        CartResult result = removeItem(cartBefore, productId);
        assertThat(result.success()).isEqualTo(success);
        assertThat(result.message()).isEqualTo(expectedMessage);
        assertThat(result.cart().items()).isEqualTo(expectedCart.items());
    }

    // ==================== Apply Coupon ====================

    @DisplayName("Apply coupon to cart")
    @Description("""
            Uses a fixed coupon store with four codes:
              SAVE10     → 10% off the whole cart
              FLAT5      → $5.00 fixed off the whole cart
              WIDGETDEAL → $2.00 off WIDGET (product-specific)
              EXPIRED    → an expired coupon
            A new valid coupon replaces the active coupon; an invalid one leaves it unchanged.
            Coupon type behaviour (PERCENT / FIXED / PRODUCT arithmetic) is covered in shouldCalculateCartTotal.
            Open: is re-applying the same coupon code a no-op or a success response?
            """)
    @TableTest("""
            Scenario                   | Active coupon | Coupon code | Applied? | Active coupon after? | Message?
            Apply percentage coupon    |               | SAVE10      | true     | SAVE10               | "Coupon applied: 10% off"
            Apply fixed coupon         |               | FLAT5       | true     | FLAT5                | "Coupon applied: $5.00 off"
            Apply product coupon       |               | WIDGETDEAL  | true     | WIDGETDEAL           | "Coupon applied: $2.00 off Widget"
            Replace active coupon      | FLAT5         | SAVE10      | true     | SAVE10               | "Coupon applied: 10% off"
            Expired coupon rejected    | SAVE10        | EXPIRED     | false    | SAVE10               | Coupon expired
            Nonexistent code rejected  | SAVE10        | UNKNOWN     | false    | SAVE10               | Invalid coupon code
            """)
    void shouldApplyCoupon(String activeCouponCode, String couponCode, boolean applied,
                           String expectedActiveCouponCode, String expectedMessage) {
        Cart cart = cartWithCoupon(activeCouponCode);
        CartResult result = applyCoupon(cart, couponCode, COUPON_STORE);
        assertThat(result.success()).isEqualTo(applied);
        assertThat(result.message()).isEqualTo(expectedMessage);
        assertThat(activeCouponCodeOf(result.cart())).isEqualTo(nullIfBlank(expectedActiveCouponCode));
    }

    // ==================== Checkout ====================

    @DisplayName("Checkout: inventory validation")
    @Description("""
            All shortages are reported together in one message, not just the first.
            Assumed: stock equal to the requested quantity is sufficient (inclusive boundary).
            """)
    @TableTest("""
            Scenario                  | Cart                    | Inventory               | Success? | Message?
            Empty cart rejected       | [:]                     | [:]                     | false    | Cart is empty
            All items in stock        | [WIDGET: 2, GADGET: 1]  | [WIDGET: 5, GADGET: 3]  | true     | Order placed successfully
            Exactly enough stock      | [WIDGET: 3]             | [WIDGET: 3]             | true     | Order placed successfully
            One item short            | [WIDGET: 5]             | [WIDGET: 3]             | false    | "Insufficient stock: Widget (need 5, have 3)"
            Multiple items short      | [WIDGET: 5, GADGET: 2]  | [WIDGET: 3, GADGET: 1]  | false    | "Insufficient stock: Widget (need 5, have 3), Gadget (need 2, have 1)"
            """)
    void shouldValidateInventoryAtCheckout(Cart cart, InventoryService inventory,
                                           boolean success, String expectedMessage) {
        CheckoutResult result = checkout(cart, inventory);
        assertThat(result.success()).isEqualTo(success);
        assertThat(result.message()).isEqualTo(expectedMessage);
    }

    // ==================== Calculate Total ====================

    @DisplayName("Calculate cart total")
    @Description("""
            Total = sum(qty × price) − coupon discount, floored at zero.
            PERCENT coupon: discount = subtotal × (rate / 100).
            FIXED coupon:   discount = min(fixed amount, subtotal).
            PRODUCT coupon: discount = amount × qty of the matching product only.
            Subtotal? and Discount? are traceability columns — they verify the intermediate arithmetic.
            """)
    @TableTest("""
            Scenario                        | Cart                    | Catalogue                      | Coupon               | Subtotal? | Discount? | Total?
            No coupon                       | [WIDGET: 2]             | [WIDGET: 10.00]                |                      | 20.00     | 0.00      | 20.00
            Percentage off whole cart       | [WIDGET: 2]             | [WIDGET: 10.00]                | PERCENT:10           | 20.00     | 2.00      | 18.00
            Fixed amount off whole cart     | [WIDGET: 2]             | [WIDGET: 10.00]                | FIXED:5.00           | 20.00     | 5.00      | 15.00
            Product coupon matches item     | [WIDGET: 2]             | [WIDGET: 10.00]                | PRODUCT:WIDGET:3.00  | 20.00     | 6.00      | 14.00
            Product coupon, no match        | [GADGET: 1]             | [GADGET: 5.00]                 | PRODUCT:WIDGET:3.00  | 5.00      | 0.00      | 5.00
            Fixed discount exceeds subtotal | [WIDGET: 1]             | [WIDGET: 3.00]                 | FIXED:10.00          | 3.00      | 3.00      | 0.00
            Multiple items, no coupon       | [WIDGET: 2, GADGET: 3]  | [WIDGET: 10.00, GADGET: 5.00] |                      | 35.00     | 0.00      | 35.00
            """)
    void shouldCalculateCartTotal(Cart cart, ProductCatalogue catalogue, Coupon activeCoupon,
                                  BigDecimal expectedSubtotal, BigDecimal expectedDiscount,
                                  BigDecimal expectedTotal) {
        BigDecimal actualSubtotal = computeSubtotal(cart, catalogue);
        BigDecimal actualTotal    = calculateTotal(cart, activeCoupon, catalogue);
        BigDecimal actualDiscount = actualSubtotal.subtract(actualTotal);

        assertThat(actualSubtotal).isEqualByComparingTo(expectedSubtotal);
        assertThat(actualDiscount).isEqualByComparingTo(expectedDiscount);
        assertThat(actualTotal).isEqualByComparingTo(expectedTotal);
    }

    // ==================== Type Converters ====================

    @TypeConverter
    public static Cart parseCart(String value) {
        if (value == null || value.isBlank() || value.equals("[:]")) return Cart.empty();
        return Cart.of(parseIntValuedMap(value));
    }

    @TypeConverter
    public static ProductCatalogue parseCatalogue(String value) {
        return ProductCatalogue.of(parseTypedMap(value, BigDecimal::new));
    }

    @TypeConverter
    public static InventoryService parseInventory(String value) {
        if (value == null || value.isBlank() || value.equals("[:]")) return productId -> 0;
        Map<String, Integer> stock = parseIntValuedMap(value);
        return productId -> stock.getOrDefault(productId, 0);
    }

    @TypeConverter
    public static Coupon parseCoupon(String value) {
        if (value == null || value.isBlank()) return null;
        String[] parts = value.split(":", 2);
        return switch (parts[0]) {
            case "PERCENT" -> Coupon.percentage(Integer.parseInt(parts[1]));
            case "FIXED"   -> Coupon.fixed(new BigDecimal(parts[1]));
            case "PRODUCT" -> {
                String[] pp = parts[1].split(":", 2);
                yield Coupon.forProduct(pp[0], new BigDecimal(pp[1]));
            }
            default -> throw new IllegalArgumentException("Unknown coupon format: " + value);
        };
    }

    @TypeConverter
    public static BigDecimal parseBigDecimal(String value) {
        return (value == null || value.isBlank()) ? null : new BigDecimal(value);
    }

    // ==================== Helpers ====================

    private static Cart cartWithCoupon(String couponCode) {
        if (couponCode == null || couponCode.isBlank()) return Cart.empty();
        Coupon coupon = COUPON_STORE.find(couponCode).orElseThrow();
        return Cart.emptyWithCoupon(couponCode, coupon);
    }

    private static String activeCouponCodeOf(Cart cart) {
        return cart.activeCoupon() == null ? null : cart.activeCoupon().code();
    }

    private static String nullIfBlank(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private static BigDecimal computeSubtotal(Cart cart, ProductCatalogue catalogue) {
        return cart.items().entrySet().stream()
                .map(e -> catalogue.priceOf(e.getKey()).multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Map<String, Integer> parseIntValuedMap(String value) {
        return parseTypedMap(value, Integer::parseInt);
    }

    private static <V> Map<String, V> parseTypedMap(String value, Function<String, V> parseValue) {
        String content = value.replaceAll("^\\[|\\]$", "").trim();
        if (content.isEmpty() || content.equals(":")) return Map.of();
        return Arrays.stream(content.split(","))
                .map(entry -> entry.split(":", 2))
                .collect(Collectors.toMap(
                        kv -> kv[0].trim(),
                        kv -> parseValue.apply(kv[1].trim()),
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }
}
```

---

**A few things to adjust to match your implementation:**

| Assumption | Likely adjustment needed |
|---|---|
| `Cart.empty()`, `Cart.of(Map)`, `Cart.emptyWithCoupon(code, coupon)` | Match your actual Cart factory/constructor |
| `cart.items()` returns `Map<String, Integer>` | Match your Cart item accessor |
| `cart.activeCoupon()` returns nullable `Coupon` with `.code()` | Match your coupon accessor |
| `Coupon.percentage(int)`, `.fixed(BigDecimal)`, `.forProduct(String, BigDecimal)`, `.expired()` | Match your Coupon factories |
| `CouponStore.of(Map)` and `.find(String)` returns `Optional<Coupon>` | Match your CouponStore API |
| `CartResult.success()`, `.message()`, `.cart()` | Match your result type |
| `CheckoutResult.success()`, `.message()` | Match your result type |
| `static import com.example.cart.CartService.*` | Update to your actual class |

The message strings in the tables (e.g. `"Item not found: Gadget"`) are placeholders — update them to match whatever your implementation produces.