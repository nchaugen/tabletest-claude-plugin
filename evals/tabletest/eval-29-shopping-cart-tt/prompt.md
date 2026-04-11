Write TableTests for a shopping cart feature.

The method signatures are:

```java
CartResult addItem(Cart cart, String productId, int quantity, ProductCatalogue catalogue)
CartResult removeItem(Cart cart, String productId)
CartResult applyCoupon(Cart cart, String couponCode, CouponStore couponStore)
CheckoutResult checkout(Cart cart, InventoryService inventory)
BigDecimal calculateTotal(Cart cart, Coupon activeCoupon, ProductCatalogue catalogue)
```

The rules:
- Customers add items by product ID and quantity. Prices come from the catalogue, not the customer.
- Customers can remove items. Removing an item not in the cart returns an error message.
- Customers can enter a coupon code. Only one coupon active at a time — new code replaces old.
- Three coupon types: percentage off whole cart, fixed amount off, product-specific discount.
- Expired or nonexistent coupon codes return an error and don't replace the active coupon.
- At checkout, stock levels are verified. Insufficient inventory fails checkout with details of what's short.
- Empty cart cannot check out.
- Cart total = sum(qty × price) − coupon discount, floored at zero.
- Every operation returns a user-facing message (e.g. 'Added 2x Widget', 'Coupon expired').
