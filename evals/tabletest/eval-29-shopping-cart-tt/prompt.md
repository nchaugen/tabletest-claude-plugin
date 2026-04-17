A Gradle Java project has been created for this feature.

Under `src/main/java/com/example/cart`, the project already contains shopping-cart domain types and a stubbed service API for add item, remove item, apply coupon, checkout, and total calculation. The behavior is not implemented yet.

We want to write tests before implementing the cart logic.

Write TableTests for the shopping cart feature using that existing API.

The rules are:
- Customers add items by product ID and quantity. Prices come from the catalogue, not the customer.
- Customers can remove items. Removing an item not in the cart returns an error message.
- Customers can enter a coupon code. Only one coupon active at a time — new code replaces old.
- Three coupon types: percentage off whole cart, fixed amount off, product-specific discount.
- Expired or nonexistent coupon codes return an error and don't replace the active coupon.
- At checkout, stock levels are verified. Insufficient inventory fails checkout with details of what's short.
- Empty cart cannot check out.
- Cart total = sum(qty × price) − coupon discount, floored at zero.
- Every operation returns a user-facing message (e.g. 'Added 2x Widget', 'Coupon expired').

You may add small helper types or adjust stubs if needed so the tests compile, but do not implement the shopping cart behavior yet.
