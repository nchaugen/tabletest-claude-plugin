package com.example.cart;

public class CartResult {

    private final boolean success;
    private final String message;
    private final Cart cart;

    public CartResult(boolean success, String message, Cart cart) {
        this.success = success;
        this.message = message;
        this.cart = cart;
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

    public Cart getCart() {
        return cart;
    }

    public Cart cart() {
        return cart;
    }
}
