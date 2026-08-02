package com.example;

import java.time.LocalDateTime;

public class ReisDiscountCalculatorImpl implements ReisDiscountCalculator {

    private final ReisTravelFrequency travelFrequency;
    private final ReisDiscountLadder discountLadder;

    public ReisDiscountCalculatorImpl(ReisTravelFrequency travelFrequency, ReisDiscountLadder discountLadder) {
        this.travelFrequency = travelFrequency;
        this.discountLadder = discountLadder;
    }

    @Override
    public DiscountPercentage calculateDiscount(TravelerCategory travelerCategory, LocalDateTime purchaseTime, PurchaseHistoryRepository purchaseHistoryRepository) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
