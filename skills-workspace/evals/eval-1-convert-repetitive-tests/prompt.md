I have these three JUnit 5 tests that all test the same method but with different inputs. Can you convert them into a single TableTest?

```java
@Test
void returnsDiscountForGoldCustomer() {
    assertEquals(20, discountService.calculate("GOLD", 100));
}

@Test
void returnsDiscountForSilverCustomer() {
    assertEquals(10, discountService.calculate("SILVER", 100));
}

@Test
void returnsNoDiscountForBronzeCustomer() {
    assertEquals(0, discountService.calculate("BRONZE", 100));
}
```
