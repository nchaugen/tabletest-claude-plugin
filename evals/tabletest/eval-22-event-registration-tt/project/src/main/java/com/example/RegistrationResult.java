package com.example;

import java.math.BigDecimal;

public class RegistrationResult {

    private final boolean accepted;
    private final String errorMessage;
    private final BigDecimal price;
    private final BigDecimal discount;

    public RegistrationResult(boolean accepted, String errorMessage, BigDecimal price, BigDecimal discount) {
        this.accepted = accepted;
        this.errorMessage = errorMessage;
        this.price = price;
        this.discount = discount;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean accepted() {
        return accepted;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal price() {
        return price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal discount() {
        return discount;
    }
}
