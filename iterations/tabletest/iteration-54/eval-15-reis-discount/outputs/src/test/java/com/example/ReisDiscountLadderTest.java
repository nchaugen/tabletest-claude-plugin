package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(TestConverters.class)
public class ReisDiscountLadderTest {

    private final ReisDiscountLadder ladder = new ReisDiscountLadder();

    @Description("""
        Trip number is the ordinal position of the current single-ticket purchase among an
        ADULT or SENIOR traveler's qualifying purchases in the trailing 30 days (see
        SingleTicketTripCounterTest). The discount increases by 5 percentage points for
        every 5th qualifying trip and never exceeds the 40% cap.
        """)
    @TableTest("""
        Scenario                              | Trip Number | Discount %?
        Below the first discount tier         | 1           | 0
        Just below the first discount tier    | 4           | 0
        First discount tier reached           | 5           | 5
        Between the first and second tier     | 9           | 5
        Second discount tier reached          | 10          | 10
        High tier below the cap               | 39          | 35
        Natural ladder value hits the cap     | 40          | 40
        Ladder value beyond the cap is capped | 45          | 40
        """)
    void computesDiscountFromTripNumber(int tripNumber, DiscountPercentage discount) {
        assertEquals(discount, ladder.discountForTripNumber(tripNumber));
    }
}
