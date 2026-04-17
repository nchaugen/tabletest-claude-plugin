A Gradle Java project has been created for this feature.

Under `src/main/java/com/example/orders`, the project already contains the order-splitting domain model and a stub `OrderSplitter`. The splitting behavior is not implemented yet.

We want to write tests before implementing the splitter.

Write TableTests for the order splitting feature using that existing API.

An `Order` contains a list of `OrderItem`s, each with productId, quantity, fulfillmentType (`DELIVERY` or `PICKUP`), and deliveryAddress. Some products are marked as companions and should ship together when possible.

The splitting rules are:
- Items with different fulfillment types (delivery vs pickup) go in separate shipments
- Items going to different delivery addresses go in separate shipments
- In-stock items ship immediately; backordered/pre-ordered items ship when available (don't hold in-stock items)
- When splitting across warehouses, minimise shipment count — pick the combination of warehouses that covers the order in fewest shipments
- Companion products (e.g. camera body + lens) should ship from the same warehouse when possible

You may add small helper types or adjust stubs if needed so the tests compile, but do not implement the splitting logic yet.
