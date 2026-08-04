package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountPolicyTest {

    private final ReisDiscountPolicy policy = new ReisSingleTicketDiscountPolicy();

    @Description("""
        Children never accumulate a Reis discount - the flat rate applies no matter how many
        single tickets they have recently purchased.
        """)
    @TableTest("""
        Scenario                                 | Traveler Category | Recent Single Ticket Count | Discount?
        Flat discount regardless of ticket count | CHILD             | {0, 4, 5, 45}              | 20%
        """)
    void appliesAFlatDiscountToChildren(TravelerCategory travelerCategory, int recentSingleTicketCount, DiscountPercentage discount) {
        assertEquals(discount, policy.discountFor(travelerCategory, recentSingleTicketCount));
    }

    @Description("""
        Adults and seniors are treated identically by Reis. The discount rises by 5 percentage
        points for every 5 recent single tickets, and never exceeds 40%.
        """)
    @TableTest("""
        Scenario                                      | Traveler Category | Recent Single Ticket Count | Discount?
        Below the first discount tier                 | {ADULT, SENIOR}   | {0, 4}                     | 0%
        Fifth ticket reaches the first discount tier  | {ADULT, SENIOR}   | {5, 9}                     | 5%
        Tenth ticket reaches the second discount tier | {ADULT, SENIOR}   | {10, 14}                   | 10%
        Formula alone reaches the maximum discount    | {ADULT, SENIOR}   | {40, 44}                   | 40%
        Discount never exceeds the maximum            | {ADULT, SENIOR}   | {45, 100}                  | 40%
        """)
    void calculatesARisingDiscountForAdultsAndSeniors(TravelerCategory travelerCategory, int recentSingleTicketCount, DiscountPercentage discount) {
        assertEquals(discount, policy.discountFor(travelerCategory, recentSingleTicketCount));
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(String value) {
        return new DiscountPercentage(Integer.parseInt(value.replace("%", "")));
    }
}
