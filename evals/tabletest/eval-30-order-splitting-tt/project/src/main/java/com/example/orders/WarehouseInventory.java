package com.example.orders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WarehouseInventory {

    private final Map<String, Map<String, StockStatus>> stockByWarehouse;
    private final List<Set<String>> companionGroups;

    public WarehouseInventory() {
        this(new HashMap<>());
    }

    public WarehouseInventory(Map<String, Map<String, StockStatus>> stockByWarehouse) {
        this.stockByWarehouse = new HashMap<>();
        stockByWarehouse.forEach((warehouseId, stock) ->
                this.stockByWarehouse.put(warehouseId, new HashMap<>(stock))
        );
        this.companionGroups = new ArrayList<>();
    }

    public void addStock(String warehouseId, String productId, StockStatus status) {
        stockByWarehouse.computeIfAbsent(warehouseId, ignored -> new HashMap<>())
                .put(productId, status);
    }

    public void addCompanionGroup(Set<String> productIds) {
        companionGroups.add(new HashSet<>(productIds));
    }

    public Map<String, Map<String, StockStatus>> getStockByWarehouse() {
        return stockByWarehouse;
    }

    public Map<String, Map<String, StockStatus>> stockByWarehouse() {
        return stockByWarehouse;
    }

    public List<Set<String>> getCompanionGroups() {
        return companionGroups;
    }
}
