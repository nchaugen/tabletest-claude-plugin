package com.example.orders;

import java.util.ArrayList;
import java.util.List;

public class Shipment {

    private final List<OrderItem> items;
    private final FulfillmentType fulfillmentType;
    private final String warehouseId;
    private final Availability availability;

    public Shipment(List<OrderItem> items, FulfillmentType fulfillmentType, String warehouseId, Availability availability) {
        this.items = new ArrayList<>(items);
        this.fulfillmentType = fulfillmentType;
        this.warehouseId = warehouseId;
        this.availability = availability;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public List<OrderItem> items() {
        return items;
    }

    public List<String> productIds() {
        return items.stream().map(OrderItem::getProductId).toList();
    }

    public FulfillmentType getFulfillmentType() {
        return fulfillmentType;
    }

    public FulfillmentType fulfillmentType() {
        return fulfillmentType;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public Availability getAvailability() {
        return availability;
    }

    public Availability availability() {
        return availability;
    }

    public boolean immediate() {
        return availability == Availability.IMMEDIATE;
    }
}
