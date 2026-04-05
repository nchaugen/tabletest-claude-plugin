We need to handle order splitting — when a customer places an order for multiple items, sometimes we can't ship everything together and need to break it into separate shipments.

This happens in a few situations we keep running into. Sometimes a customer adds items to their cart where some are for store pickup and others for home delivery — obviously those can't go in the same shipment. We also see orders where some items are in stock and others are pre-orders or backorders that won't be available for a while. We don't want to make the customer wait for everything, so items should ship when they are available. Then there's the inventory distribution problem — say someone orders six shirts and no single warehouse has all six. If a combination of warehouses can make up the order, e.g. Chicago might have four, LA has one, Houston has one, we split across locations rather than reject the order. And occasionally customers want items delivered to different addresses, like when they're ordering a gift for someone along with something for themselves.

When we do split, we want to minimise the number of shipments. In that shirt example, if Philadelphia and Washington each have three shirts, we should ship from those two locations rather than three. Fewer shipments means lower cost and less confusion for the customer.

One important constraint: some products are companions — like a camera body and its matching lens, which are separate line items but useless without each other. If possible those should ship together from the same location.

Please help us map out these scenarios as test cases.
