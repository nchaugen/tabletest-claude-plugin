The three tests share identical assertion logic and differ only in data — a perfect TableTest fit. The order value `100` is fixed across all rows, so I'll note it in `@Description` rather than adding a column.

```java
@Description("Order value is fixed at 100 for all scenarios.")
@TableTest("""
    Scenario        | Customer type | Discount?
    Gold customer   | GOLD          | 20
    Silver customer | SILVER        | 10
    Bronze customer | BRONZE        | 0
    """)
void calculatesDiscountByCustomerType(String customerType, int discount) {
    assertEquals(discount, discountService.calculate(customerType, 100));
}
```

Key decisions:
- `Customer type` is the varying input; `Discount?` is the expected output (suffixed with `?`)
- Scenario names describe the condition ("Gold customer"), not the outcome ("returns 20")
- Fixed `orderValue = 100` lives in `@Description` since it's not a column — it has no row-level variation but is meaningful context for anyone reading the test