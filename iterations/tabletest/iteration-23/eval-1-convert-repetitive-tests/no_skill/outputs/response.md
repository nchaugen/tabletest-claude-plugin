```java
@ParameterizedTest
@CsvSource({
    "GOLD,   20",
    "SILVER, 10",
    "BRONZE,  0"
})
void returnsDiscountForCustomer(String tier, int expectedDiscount) {
    assertEquals(expectedDiscount, discountService.calculate(tier, 100));
}
```

Make sure you have `junit-jupiter-params` on your classpath — it's included automatically if you use `junit-jupiter` (the aggregator artifact).