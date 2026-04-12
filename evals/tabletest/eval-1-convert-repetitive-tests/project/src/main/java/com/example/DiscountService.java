package com.example;

public class DiscountService {

    public int calculate(String tier, int amount) {
        return switch (tier) {
            case "GOLD" -> amount * 20 / 100;
            case "SILVER" -> amount * 10 / 100;
            default -> 0;
        };
    }
}
