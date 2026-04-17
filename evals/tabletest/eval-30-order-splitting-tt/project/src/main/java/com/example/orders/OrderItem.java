package com.example.orders;

public class OrderItem {

    private final String productId;
    private final int quantity;
    private final FulfillmentType fulfillmentType;
    private final String deliveryAddress;

    public OrderItem(String productId, int quantity, FulfillmentType fulfillmentType, String deliveryAddress) {
        this.productId = productId;
        this.quantity = quantity;
        this.fulfillmentType = fulfillmentType;
        this.deliveryAddress = deliveryAddress;
    }

    public String getProductId() {
        return productId;
    }

    public String productId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int quantity() {
        return quantity;
    }

    public FulfillmentType getFulfillmentType() {
        return fulfillmentType;
    }

    public FulfillmentType fulfillmentType() {
        return fulfillmentType;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String deliveryAddress() {
        return deliveryAddress;
    }
}
