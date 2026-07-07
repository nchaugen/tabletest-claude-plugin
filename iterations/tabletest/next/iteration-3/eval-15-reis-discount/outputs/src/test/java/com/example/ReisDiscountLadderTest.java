package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountLadderTest {

    private final ReisDiscountLadder ladder = new ReisDiscountLadder();

    @TableTest("""
        Scenario                               | Trip Number          | Max Discount (Policy) | Discount?
        Below the first discount tier          | {1, 2, 3, 4}         | 40                     | 0
        First discount tier                    | {5, 6, 7, 8, 9}      | 40                     | 5
        Second discount tier                   | {10, 11, 12, 13, 14} | 40                     | 10
        Trip count just below the cap          | 39                   | 40                     | 35
        Trip count at the cap boundary         | 40                   | 40                     | 40
        Trip count just past the cap boundary  | 45                   | 40                     | 40
        Trip count far past the cap boundary   | 1000                 | 40                     | 40
        """)
    void calculatesLadderDiscountFromTripNumber(int tripNumber, int maxDiscountPercent, int discount) {
        assertEquals(new DiscountPercentage(discount), ladder.discountForTripNumber(tripNumber, maxDiscountPercent));
    }
}
