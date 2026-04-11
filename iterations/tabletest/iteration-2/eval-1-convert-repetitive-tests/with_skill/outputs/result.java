@TableTest("""
    Scenario        | Customer Tier | Discount?
    Gold customer   | GOLD          | 20
    Silver customer | SILVER        | 10
    Bronze customer | BRONZE        | 0
    """)
void calculatesDiscountByCustomerTier(String customerTier, int discount) {
    assertEquals(discount, discountService.calculate(customerTier, 100));
}
