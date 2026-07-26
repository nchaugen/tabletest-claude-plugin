package com.example.reis;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FakeTicketPurchaseHistory implements TicketPurchaseHistory {

    private final List<TicketPurchase> purchases = new ArrayList<>();

    public void record(TicketType type, Instant purchaseTime) {
        purchases.add(new TicketPurchase(type, purchaseTime));
    }

    @Override
    public List<TicketPurchase> purchasesFor(String passengerId) {
        return List.copyOf(purchases);
    }
}
