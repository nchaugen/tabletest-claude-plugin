package com.example;

import java.time.LocalDateTime;
import java.util.List;

public interface ReisTravelFrequency {
    int countSingleTicketPurchasesInLast30Days(List<PastPurchase> purchaseHistory, LocalDateTime purchaseTime);
}
