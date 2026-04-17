package com.example.cart;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Cart {

    private final Map<String, Integer> items;
    private String activeCouponCode;

    public Cart() {
        this(new LinkedHashMap<>(), null);
    }

    public Cart(Map<String, Integer> items, String activeCouponCode) {
        this.items = new LinkedHashMap<>(items);
        this.activeCouponCode = activeCouponCode;
    }

    public static Cart empty() {
        return new Cart();
    }

    public static Cart withItems(Map<String, Integer> items) {
        return new Cart(items, null);
    }

    public Cart withActiveCouponCode(String activeCouponCode) {
        this.activeCouponCode = activeCouponCode;
        return this;
    }

    public void putItem(String productId, int quantity) {
        items.put(productId, quantity);
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public Map<String, Integer> items() {
        return items;
    }

    public String getActiveCouponCode() {
        return activeCouponCode;
    }

    public String activeCouponCode() {
        return activeCouponCode;
    }

    public void setActiveCouponCode(String activeCouponCode) {
        this.activeCouponCode = activeCouponCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Cart cart)) {
            return false;
        }
        return Objects.equals(items, cart.items)
                && Objects.equals(activeCouponCode, cart.activeCouponCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, activeCouponCode);
    }
}
