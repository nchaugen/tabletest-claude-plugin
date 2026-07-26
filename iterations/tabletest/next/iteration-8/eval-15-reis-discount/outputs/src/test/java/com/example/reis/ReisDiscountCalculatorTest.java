package com.example.reis;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    private static final Instant PURCHASE_TIME = Instant.parse("2026-06-30T12:00:00Z");
    private static final String PASSENGER_ID = "passenger-1";

    @Test
    void combinesPurchaseHistoryWithTheDiscountLadder() {
        FakeTicketPurchaseHistory history = new FakeTicketPurchaseHistory();
        history.record(TicketType.SINGLE, PURCHASE_TIME.minus(1, ChronoUnit.DAYS));
        history.record(TicketType.SINGLE, PURCHASE_TIME.minus(2, ChronoUnit.DAYS));
        history.record(TicketType.SINGLE, PURCHASE_TIME.minus(3, ChronoUnit.DAYS));
        history.record(TicketType.SINGLE, PURCHASE_TIME.minus(4, ChronoUnit.DAYS));
        history.record(TicketType.SINGLE, PURCHASE_TIME.minus(31, ChronoUnit.DAYS));

        int discount = new ReisDiscountCalculator()
            .calculateDiscount(PassengerType.ADULT, PASSENGER_ID, PURCHASE_TIME, history);

        assertEquals(5, discount);
    }

    @Test
    void childrenGetTheFlatDiscountRegardlessOfHistory() {
        FakeTicketPurchaseHistory history = new FakeTicketPurchaseHistory();
        history.record(TicketType.SINGLE, PURCHASE_TIME.minus(1, ChronoUnit.DAYS));

        int discount = new ReisDiscountCalculator()
            .calculateDiscount(PassengerType.CHILD, PASSENGER_ID, PURCHASE_TIME, history);

        assertEquals(20, discount);
    }
}
