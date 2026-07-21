package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the Reis single-ticket discount calculator.
 *
 * Reis tiers (adults/seniors): every completed set of 5 single tickets in
 * the last 30 days yields a 5 % step, capped at 40 %.
 * Children receive a flat 20 % regardless of purchase count.
 */
public class ReisDiscountTest {

    private DiscountService discountService;
    private List<PastPurchase> history;

    @BeforeEach
    void setUp() {
        history = new ArrayList<>();
        discountService = new DiscountService(() -> history);
    }

      /* ------------------------------------------------------------------ */
      /* Children — flat 20 % regardless of purchase history                 */
      /* ------------------------------------------------------------------ */

      @TableTest("""
         Scenario             | Category    | Purchases            | Discount?
         One purchase          | CHILD       | [one]                | 20
         Multiple purchases   | CHILD       | [one, one, one]      | 20
         Zero purchases        | CHILD       | []                   | 20
           """)
    void childrenAlwaysGetTwentyPercent(TravelerCategory category, List<PastPurchase> _unused, int discount) {
        history.clear();
        history.addAll(_unused);
        assertEquals(discount, discountService.discountOf(category).value());
    }

      /* ------------------------------------------------------------------ */
      /* Adults — zero tier (fewer than 5 tickets)                            */
      /* ------------------------------------------------------------------ */

      @TableTest("""
         Scenario             | Count? | Discount?
         No purchases          | 0       | 0
         One ticket            | 1       | 0
         Four tickets          | 4       | 0
           """)
    void adultsGetNoDiscountBelowFive(int count, int discount) {
        for (int i = 0; i < count; i++)
            history.add(new PastPurchase(null, TravelerCategory.ADULT, TicketType.SINGLE, null));
        assertEquals(discount, discountService.discountOf(TravelerCategory.ADULT).value());
    }

      /* ------------------------------------------------------------------ */
      /* Adults — first tier (5 %) boundary                                   */
      /* ------------------------------------------------------------------ */

      @TableTest("""
         Scenario             | Count? | Discount?
         Just below            | 4       | 0
         Enters tier           | 5       | 5
         Inside tier           | 6       | 5
           """)
    void firstTierKicksInAtFive(int count, int discount) {
        for (int i = 0; i < count; i++)
            history.add(new PastPurchase(null, TravelerCategory.ADULT, TicketType.SINGLE, null));
        assertEquals(discount, discountService.discountOf(TravelerCategory.ADULT).value());
    }

      /* ------------------------------------------------------------------ */
      /* Adults — second and third tier (10 %, 15 %)                           */
      /* ------------------------------------------------------------------ */

      @TableTest("""
         Scenario             | Count? | Discount?
         Nine tickets          | 9       | 5
         Enters second         | 10      | 10
         Just below third      | 14      | 10
         Enters third          | 15      | 15
           """)
    void secondAndThirdTier(int count, int discount) {
        for (int i = 0; i < count; i++)
            history.add(new PastPurchase(null, TravelerCategory.ADULT, TicketType.SINGLE, null));
        assertEquals(discount, discountService.discountOf(TravelerCategory.ADULT).value());
    }

      /* ------------------------------------------------------------------ */
      /* Adults — cap at 40 %                                                 */
      /* ------------------------------------------------------------------ */

      @TableTest("""
         Scenario             | Count? | Discount?
         Just below max        | 79      | 35
         Enters max            | 80      | 40
         Well above max       | 100     | 40
           """)
    void maxDiscountIsCapAtFortyPercent(int count, int discount) {
        for (int i = 0; i < count; i++)
            history.add(new PastPurchase(null, TravelerCategory.ADULT, TicketType.SINGLE, null));
        assertEquals(discount, discountService.discountOf(TravelerCategory.ADULT).value());
    }

      /* ------------------------------------------------------------------ */
      /* Seniors — same rules as adults                                       */
      /* ------------------------------------------------------------------ */

      @TableTest("""
         Scenario             | Count? | Discount?
         No purchases          | 0       | 0
         Enters first tier     | 5       | 5
         At maximum            | 80      | 40
           """)
    void seniorsFollowAdultRules(int count, int discount) {
        for (int i = 0; i < count; i++)
            history.add(new PastPurchase(null, TravelerCategory.SENIOR, TicketType.SINGLE, null));
        assertEquals(discount, discountService.discountOf(TravelerCategory.SENIOR).value());
    }

      /* ------------------------------------------------------------------ */
      /* Helper methods                                                       */
      /* ------------------------------------------------------------------ */

    private PastPurchase purchase() {
        return new PastPurchase(null, TravelerCategory.ADULT, TicketType.SINGLE, null);
    }

      @TypeConverter
    public static PastPurchase makePurchase(String _ignored) {
         return new PastPurchase(null, TravelerCategory.ADULT, TicketType.SINGLE, null);
      }
}
