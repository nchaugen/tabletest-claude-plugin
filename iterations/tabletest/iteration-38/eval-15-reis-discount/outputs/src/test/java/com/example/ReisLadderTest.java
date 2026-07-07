package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisLadderTest {

    @DisplayName("Reis discount ladder tier for a given qualifying ticket count")
    @Description("""
        Discount rises by 5 percentage points for every 5th qualifying single
        ticket in the trailing 30-day window (see ReisWindow), starting at
        ticket number 5, and is capped at a maximum of 40%.
        """)
    @TableTest("""
        Scenario                                     | Qualifying Ticket Count | Discount?
        No qualifying tickets yet                     | 0                       | 0
        Just below first discount tier                | 4                       | 0
        First discount tier begins                    | 5                       | 5
        Still within first tier                        | 9                       | 5
        Second discount tier begins                    | 10                      | 10
        One tier below maximum                         | 35                      | 35
        Just below maximum                              | 39                      | 35
        Reaches maximum discount                        | 40                      | 40
        Beyond count needed for maximum stays capped     | 60                      | 40
        """)
    void discountForTicketCount(int qualifyingTicketCount, int discount) {
        assertEquals(discount, ReisLadder.discountForTicketCount(qualifyingTicketCount).value());
    }
}
