package com.example.cart;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@FunctionalInterface
public interface ProductCatalogue {

    Optional<Product> findById(String productId);

    static ProductCatalogue from(Map<String, BigDecimal> prices) {
        return productId -> Optional.ofNullable(prices.get(productId))
                .map(price -> new Product(productId, productId, price));
    }
}
