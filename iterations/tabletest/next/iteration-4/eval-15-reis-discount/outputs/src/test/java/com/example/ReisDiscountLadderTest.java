package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

@TypeConverterSources(ReisTestConverters.class)
public class ReisDiscountLadderTest {

    @Description("""
        Travel Count is the number of single tickets purchased in the trailing 30 days,
        counting the ticket currently being bought (so the very first purchase has a
        Travel Count of 1, not 0). The ladder rises by 5% every 5th ticket, starting at
        ticket 5, and is capped at 40%.
        """)
    @TableTest("""
        Scenario                              | Travel Count | Discount?
        Below the first rung                  | 1            | 0
        Just before the first rung             | 4            | 0
        First rung reached                     | 5            | 5
        Between the first and second rung      | 7            | 5
        Second rung reached                    | 10           | 10
        Mid-ladder rung reached                | 15           | 15
        High rung just before the maximum      | 39           | 35
        Ladder naturally reaches the maximum   | 40           | 40
        Beyond the maximum is capped           | 45           | 40
        Far beyond the maximum stays capped    | 100          | 40
        """)
    void appliesLadderToTravelCount(int travelCount, DiscountPercentage discount) {
        assertEquals(discount, ReisDiscountLadder.discountForTravelCount(travelCount));
    }
}
