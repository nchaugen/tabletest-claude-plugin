package com.example;

import java.util.List;

public interface PurchaseHistoryRepository {
    public List<PastPurchase> loadPurchaseHistory();
}
