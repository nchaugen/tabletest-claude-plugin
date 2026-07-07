package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    private final ReisDiscountCalculator calculator = new ReisDiscountCalculator();
    private final TripNumberCalculator tripNumberCalculator = new TripNumberCalculator();
    private static final int TRAILING_WINDOW_DAYS = 30;

    @Description("""
        Children always receive a flat 20% discount on single tickets. Zone and travel
        history never affect this -- both are varied here to demonstrate that.
        """)
    @TableTest("""
        Scenario                          | Traveler Category | Zone                     | Purchase History                                                                                                                                       | Purchase Time       | Discount?
        Child with no travel history      | CHILD             | {ZONE_1, ZONE_2, ZONE_3} | []                                                                                                                                                      | 2026-06-15T00:00:00 | 20
        Child with frequent recent travel | CHILD             | {ZONE_1, ZONE_2, ZONE_3} | ["2026-06-12T00:00:00 SINGLE", "2026-06-09T00:00:00 SINGLE", "2026-06-06T00:00:00 SINGLE", "2026-06-03T00:00:00 SINGLE", "2026-05-31T00:00:00 SINGLE"] | 2026-06-15T00:00:00 | 20
        """)
    void childrenAlwaysReceiveFlatDiscount(TravelerCategory travelerCategory, ZoneValidity zone,
                                           List<PastPurchase> purchaseHistory, LocalDateTime purchaseTime, int discount) {
        assertEquals(new DiscountPercentage(discount), calculator.calculateDiscount(travelerCategory, zone, purchaseHistory, purchaseTime));
    }

    @Description("""
        Adults and seniors follow the same Reis ladder and are treated alike here as a
        value set. Zone never affects the discount, shown once with no travel history.
        Trip Number traces how the trailing 30-day count (TripNumberCalculator) drives
        the ladder discount (ReisDiscountLadder); it is verified against the real
        TripNumberCalculator, not reimplemented -- only chosen when building each row's
        history. Because the discount is recalculated from the current trailing window
        on every purchase rather than remembered, "Below the first discount tier" and
        "Second discount tier" show the same traveler's discount can rise when travel
        picks up, and would just as readily fall back if travel later drops -- there is
        no ratchet.
        """)
    @TableTest("""
        Scenario                                       | Traveler Category | Zone                     | Purchase History                                                                                                                                                                                                                | Purchase Time       | Trip Number? | Discount?
        No travel history yet                          | {ADULT, SENIOR}   | {ZONE_1, ZONE_2, ZONE_3} | []                                                                                                                                                                                                                               | 2026-06-15T00:00:00 | 1            | 0
        Below the first discount tier                  | {ADULT, SENIOR}   | ZONE_1                   | ["2026-06-12T00:00:00 SINGLE", "2026-06-09T00:00:00 SINGLE", "2026-06-06T00:00:00 SINGLE"]                                                                                                                                      | 2026-06-15T00:00:00 | 4            | 0
        Reaches the first discount tier                | {ADULT, SENIOR}   | ZONE_1                   | ["2026-06-12T00:00:00 SINGLE", "2026-06-09T00:00:00 SINGLE", "2026-06-06T00:00:00 SINGLE", "2026-06-03T00:00:00 SINGLE"]                                                                                                        | 2026-06-15T00:00:00 | 5            | 5
        Second discount tier from more frequent travel | {ADULT, SENIOR}   | ZONE_1                   | ["2026-06-12T00:00:00 SINGLE", "2026-06-09T00:00:00 SINGLE", "2026-06-06T00:00:00 SINGLE", "2026-06-03T00:00:00 SINGLE", "2026-05-31T00:00:00 SINGLE", "2026-05-28T00:00:00 SINGLE", "2026-05-25T00:00:00 SINGLE", "2026-05-22T00:00:00 SINGLE", "2026-05-19T00:00:00 SINGLE"] | 2026-06-15T00:00:00 | 10           | 10
        """)
    void adultsAndSeniorsReceiveReisLadderDiscount(TravelerCategory travelerCategory, ZoneValidity zone,
                                                    List<PastPurchase> purchaseHistory, LocalDateTime purchaseTime,
                                                    int tripNumber, int discount) {
        assertEquals(tripNumber, tripNumberCalculator.calculateTripNumber(purchaseHistory, purchaseTime, TRAILING_WINDOW_DAYS));
        assertEquals(new DiscountPercentage(discount), calculator.calculateDiscount(travelerCategory, zone, purchaseHistory, purchaseTime));
    }

    @TypeConverter
    public static PastPurchase parseHistoryEntry(String input) {
        String[] parts = input.split(" ");
        return new PastPurchase(LocalDateTime.parse(parts[0]), TravelerCategory.ADULT, TicketType.valueOf(parts[1]), ZoneValidity.ZONE_1);
    }
}
