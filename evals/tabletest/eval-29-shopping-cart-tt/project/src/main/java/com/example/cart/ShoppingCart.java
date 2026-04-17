package com.example.cart;

import java.math.BigDecimal;

public final class ShoppingCart {

    private ShoppingCart() {
    }

    public static CartResult addItem(Cart cart, String productId, int quantity, ProductCatalogue catalogue) {
        throw new UnsupportedOperationException("Cart logic not implemented yet");
    }

    public static CartResult removeItem(Cart cart, String productId) {
        throw new UnsupportedOperationException("Cart logic not implemented yet");
    }

    public static CartResult applyCoupon(Cart cart, String couponCode, CouponStore couponStore) {
        throw new UnsupportedOperationException("Cart logic not implemented yet");
    }

    public static CheckoutResult checkout(Cart cart, InventoryService inventory) {
        throw new UnsupportedOperationException("Cart logic not implemented yet");
    }

    public static BigDecimal calculateTotal(Cart cart, Coupon activeCoupon, ProductCatalogue catalogue) {
        throw new UnsupportedOperationException("Cart logic not implemented yet");
    }
}
