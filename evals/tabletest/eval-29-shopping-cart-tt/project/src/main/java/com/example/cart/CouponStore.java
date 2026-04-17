package com.example.cart;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@FunctionalInterface
public interface CouponStore {

    Optional<Coupon> findByCode(String code);

    static CouponStore of(Map<String, Coupon> available, Set<String> expired) {
        return code -> {
            if (expired.contains(code)) {
                return Optional.of(new Coupon(CouponType.PERCENT, java.math.BigDecimal.ZERO, null, true));
            }
            return Optional.ofNullable(available.get(code));
        };
    }
}
