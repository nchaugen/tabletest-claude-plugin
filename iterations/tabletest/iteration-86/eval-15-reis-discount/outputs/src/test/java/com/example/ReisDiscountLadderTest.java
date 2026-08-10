package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountLadderTest {

    @Description("""
        Recent Single-Ticket Count (Last 30 Days) includes the single ticket currently being
        purchased, so the first discount tier (5%) is reached on the fifth ticket, not the
        fourth. Applies to adult and senior travelers only; children receive a separate flat
        discount regardless of travel frequency (see SingleTicketDiscountCalculatorTest).
        """)
    @TableTest("""
        Scenario                               | Recent Single-Ticket Count (Last 30 Days) | Discount?
        No discount before the first tier      | {0, 4}                                    | 0
        Reaches the first tier                 | 5                                         | 5
        Stays at the first tier                | 9                                         | 5
        Reaches the second tier                | 10                                        | 10
        Stays at the second tier               | 14                                        | 10
        Reaches the third tier                 | 15                                        | 15
        Stays at the third tier                | 19                                        | 15
        Reaches the fourth tier                | 20                                        | 20
        Stays at the fourth tier               | 24                                        | 20
        Reaches the fifth tier                 | 25                                        | 25
        Stays at the fifth tier                | 29                                        | 25
        Reaches the sixth tier                 | 30                                        | 30
        Stays at the sixth tier                | 34                                        | 30
        Reaches the seventh tier               | 35                                        | 35
        Stays at the seventh tier              | 39                                        | 35
        Reaches the maximum discount           | 40                                        | 40
        Discount stays capped past the maximum | 41                                        | 40
        """)
    void appliesTheReisDiscountLadder(int recentSingleTicketCount, DiscountPercentage discount) {
        assertEquals(discount, ReisDiscountLadder.forTravelCount(recentSingleTicketCount));
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(String value) {
        return new DiscountPercentage(Integer.parseInt(value));
    }
}
