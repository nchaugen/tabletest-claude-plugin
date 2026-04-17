package com.example.cart;

@FunctionalInterface
public interface InventoryService {

    int availableQuantity(String productId);
}
