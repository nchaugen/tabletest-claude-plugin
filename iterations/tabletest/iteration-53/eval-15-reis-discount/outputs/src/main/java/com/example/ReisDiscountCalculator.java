package com.example;

import java.time.LocalDateTime;

public interface ReisDiscountCalculator {
    DiscountPercentage calculateDiscount(TravelerCategory travelerCategory, LocalDateTime purchaseTime, PurchaseHistoryRepository purchaseHistoryRepository);
}
