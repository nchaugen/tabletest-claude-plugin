package com.example.cart;

public class CheckoutResult {

    private final boolean success;
    private final String message;

    public CheckoutResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean success() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String message() {
        return message;
    }
}
