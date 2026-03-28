The three tests are a perfect match for TableTest — same method, same assertion, only the tier and expected discount vary. The order value (`100`) is fixed across all rows, which warrants a `@Description`.

Here's the converted `@TableTest`:

```java
@Description("Order value is always 100.")
@TableTest("""
    Scenario         | Customer type | Discount?
    Gold customer    | GOLD          | 20
    Silver customer  | SILVER        | 10
    Bronze customer  | BRONZE        | 0
    """)
void calculatesDiscount(String customerType, int discount) {
    assertEquals(discount, discountService.calculate(customerType, 100));
}
```

Key decisions:
- **`@Description`** documents the fixed `100` order value, since it's not visible in the table but affects every row.
- **Scenario names** describe the condition ("Gold customer"), not the outcome ("returns 20").
- **`Discount?`** suffix signals it's the expectation column.
- The scenario column is not bound to a parameter (no `@Scenario`), so `customerType` aligns to the second column and `discount` to the third.