I have converted the three individual JUnit tests into a single TableTest in `src/test/java/com/example/DiscountServiceTest.java`.

### Changes made:
1.  **Added Import**: Included `org.tabletest.junit.TableTest`.
2.  **Consolidated Tests**: Replaced the three separate `@Test` methods with a single `@TableTest` method that parameterizes the customer level, amount, and expected discount.
3.  **Applied Table Design Principles**:
    *   Included a **Scenario** column to describe the condition (e.g., "Gold customer").
    *   Sufixed the expectation column with `?` (`Discount?`) to signal intent.
    *   Used concrete domain values in the table, ensuring the input amount is visible rather than hardcoded in the method body.

```java
    @TableTest("""
        Scenario         | Level  | Amount | Discount?
        Gold customer    | GOLD   | 100    | 20
        Silver customer  | SILVER | 100    | 10
        Bronze customer  | BRONZE | 100    | 0
        """)
    void calculateDiscount(String level, int amount, int expectedDiscount) {
        assertEquals(expectedDiscount, discountService.calculate(level, amount));
    }
```