package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.example.HistoryConverters.PURCHASE_TIME;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

@TypeConverterSources(HistoryConverters.class)
class SingleTicketHistoryCounterTest {

    private final SingleTicketHistoryCounter counter = new SingleTicketHistoryCounterImpl();

    @DisplayName("Counts single tickets purchased in the trailing 30 days")
    @Description("""
            Purchase time is fixed at 2026-01-31T10:00:00 for every row, so the 30-day
            cutoff is 2026-01-01T10:00:00. "beforeCutoff" is one day older than the cutoff,
            "onCutoff" is exactly the cutoff instant, "afterCutoff" is one day newer.
            Traveler category and zone on past purchases do not affect the count and are
            fixed by the history converter.
            """)
    @TableTest("""
            Scenario                                    | History                                                        | Count?
            No purchase history                         | []                                                             | 0
            All recent purchases are single tickets     | [afterCutoff SINGLE, afterCutoff SINGLE]                      | 2
            Weekly and monthly tickets are not counted  | [afterCutoff SINGLE, afterCutoff WEEKLY, afterCutoff MONTHLY] | 1
            Purchase before the cutoff is excluded       | [beforeCutoff SINGLE, afterCutoff SINGLE]                     | 1
            Purchase exactly at the cutoff is included   | [onCutoff SINGLE, afterCutoff SINGLE]                         | 2
            """)
    void countsRecentSingleTickets(List<PastPurchase> history, int count) {
        assertEquals(count, counter.countRecentSingleTickets(history, PURCHASE_TIME));
    }
}
