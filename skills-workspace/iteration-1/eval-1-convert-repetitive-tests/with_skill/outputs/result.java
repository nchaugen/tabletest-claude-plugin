import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TableTest("""
    Scenario       | Customer Tier | Order Total | Discount?
    Gold customer  | GOLD          | 100         | 20
    Silver customer| SILVER        | 100         | 10
    Bronze customer| BRONZE        | 100         | 0
    """)
void calculatesDiscountByCustomerTier(String customerTier, int orderTotal, int discount) {
    assertEquals(discount, discountService.calculate(customerTier, orderTotal));
}
