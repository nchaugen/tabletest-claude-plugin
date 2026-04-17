package com.example.orders;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private final List<OrderItem> items;
    private final List<List<String>> companionGroups;

    public Order(List<OrderItem> items) {
        this(items, List.of());
    }

    public Order(List<OrderItem> items, List<List<String>> companionGroups) {
        this.items = new ArrayList<>(items);
        this.companionGroups = new ArrayList<>(companionGroups);
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public List<OrderItem> items() {
        return items;
    }

    public List<List<String>> getCompanionGroups() {
        return companionGroups;
    }
}
