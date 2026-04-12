There are no Java or build files in this project. Based on the three tests you shared, here's the TableTest conversion:

```java
@TableTest("""
    Scenario        | tier   | amount | Discount?
    Gold customer   | GOLD   | 100    | 20
    Silver customer | SILVER | 100    | 10
    Bronze customer | BRONZE | 100    | 0
    """)
void calculatesDiscountByTier(String tier, int amount, int discount) {
    assertEquals(discount, discountService.calculate(tier, amount));
}
```

A few notes:
- The `amount` is the same across all rows (100). If it's always fixed, you could drop it as a column and hardcode it in the method — but keeping it as a column makes the inputs explicit and makes it easy to add rows with different amounts later.
- Scenario names describe the condition ("Gold customer"), not the outcome ("returns 20").
- `Discount?` uses the `?` suffix to signal it's an expected output.

Make sure you have the TableTest dependency in your build file and the import:
```java
import org.tabletest.junit.TableTest;
```