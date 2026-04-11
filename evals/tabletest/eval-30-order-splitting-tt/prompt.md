Write TableTests for an order splitting feature.

The method signature is:

```java
List<Shipment> splitOrder(Order order, WarehouseInventory inventory)
```

An Order contains a list of OrderItems, each with productId, quantity, fulfillmentType (DELIVERY or PICKUP), and deliveryAddress. Some products are marked as companions (must ship together when possible).

Splitting rules:
- Items with different fulfillment types (delivery vs pickup) go in separate shipments
- Items going to different delivery addresses go in separate shipments
- In-stock items ship immediately; backordered/pre-ordered items ship when available (don't hold in-stock items)
- When splitting across warehouses, minimise shipment count — pick the combination of warehouses that covers the order in fewest shipments
- Companion products (e.g. camera body + lens) should ship from the same warehouse when possible
