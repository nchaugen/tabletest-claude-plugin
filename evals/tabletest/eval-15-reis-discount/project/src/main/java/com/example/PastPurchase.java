package com.example;

import java.time.LocalDateTime;

public record PastPurchase(LocalDateTime purchasedAt, TravelerCategory travelerCategory, TicketType ticketType, ZoneValidity zoneValidity) {
}
