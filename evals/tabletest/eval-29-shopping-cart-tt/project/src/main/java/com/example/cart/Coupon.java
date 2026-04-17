package com.example.cart;

import java.math.BigDecimal;

public record Coupon(CouponType type, BigDecimal amount, String targetProductId, boolean expired) {

    public static Coupon percentage(int percent) {
        return new Coupon(CouponType.PERCENT, BigDecimal.valueOf(percent), null, false);
    }

    public static Coupon fixed(BigDecimal amount) {
        return new Coupon(CouponType.FIXED, amount, null, false);
    }

    public static Coupon productSpecific(String productId, BigDecimal amount) {
        return new Coupon(CouponType.PRODUCT, amount, productId, false);
    }
}
