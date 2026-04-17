package com.example.cart;

import java.math.BigDecimal;

public final class CartService {

    private CartService() {
    }

    public static CartResult addItem(Cart cart, String productId, int quantity, ProductCatalogue catalogue) {
        return ShoppingCart.addItem(cart, productId, quantity, catalogue);
    }

    public static CartResult removeItem(Cart cart, String productId) {
        return ShoppingCart.removeItem(cart, productId);
    }

    public static CartResult applyCoupon(Cart cart, String couponCode, CouponStore couponStore) {
        return ShoppingCart.applyCoupon(cart, couponCode, couponStore);
    }

    public static CheckoutResult checkout(Cart cart, InventoryService inventory) {
        return ShoppingCart.checkout(cart, inventory);
    }

    public static BigDecimal calculateTotal(Cart cart, Coupon activeCoupon, ProductCatalogue catalogue) {
        return ShoppingCart.calculateTotal(cart, activeCoupon, catalogue);
    }
}
