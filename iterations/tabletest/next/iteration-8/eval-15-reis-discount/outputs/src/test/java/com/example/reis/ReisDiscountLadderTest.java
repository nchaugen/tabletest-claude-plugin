package com.example.reis;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountLadderTest {

    @Description("""
        Adults and seniors accumulate the Reis discount identically; children always get
        a flat 20% discount, regardless of how often they travel. Trip Number In Last 30 Days
        is the count of single tickets purchased in the trailing 30 days, including the ticket
        now being purchased (see ReisTravelFrequencyTest for how that count is derived from
        purchase history). The discount rises 5 percentage points every 5th trip and caps at 40%.
        """)
    @TableTest("""
        Scenario                        | Passenger Type  | Trip Number In Last 30 Days | Discount %?
        Children get the flat rate      | CHILD           | {1, 4, 5, 40, 1000}         | 20
        Below the first tier            | {ADULT, SENIOR} | {1, 4}                      | 0
        Fifth trip reaches first tier   | {ADULT, SENIOR} | {5, 9}                      | 5
        Tenth trip                      | {ADULT, SENIOR} | {10, 14}                    | 10
        Fifteenth trip                  | {ADULT, SENIOR} | {15, 19}                    | 15
        Twentieth trip                  | {ADULT, SENIOR} | {20, 24}                    | 20
        Twenty-fifth trip               | {ADULT, SENIOR} | {25, 29}                    | 25
        Thirtieth trip                  | {ADULT, SENIOR} | {30, 34}                    | 30
        Thirty-fifth trip               | {ADULT, SENIOR} | {35, 39}                    | 35
        Fortieth trip reaches the cap   | {ADULT, SENIOR} | {40, 45, 1000}              | 40
        """)
    void discountForPassengerTypeAndTravelFrequency(PassengerType passengerType, int tripNumberInWindow, int discountPercent) {
        assertEquals(discountPercent, ReisDiscountLadder.discountFor(passengerType, tripNumberInWindow));
    }
}
