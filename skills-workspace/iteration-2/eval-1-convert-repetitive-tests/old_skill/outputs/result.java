@TableTest("""
    Scenario       | Customer Tier | Amount | Discount?
    Gold customer  | GOLD          | 100    | 20
    Silver customer| SILVER        | 100    | 10
    Bronze customer| BRONZE        | 100    | 0
    """)
void calculatesDiscountByCustomerTier(String customerTier, int amount, int discount) {
    assertEquals(discount, discountService.calculate(customerTier, amount));
}
