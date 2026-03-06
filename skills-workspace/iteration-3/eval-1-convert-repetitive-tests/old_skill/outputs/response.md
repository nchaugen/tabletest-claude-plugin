# Converting Repetitive Tests to a TableTest

Here's how to convert your three tests into a single TableTest using the TableTest JUnit 5 extension.

## Result

```java
@TableTest("""
    Scenario                    | Tier    | Amount | Discount?
    Gold customer gets 20%      | GOLD    | 100    | 20
    Silver customer gets 10%    | SILVER  | 100    | 10
    Bronze customer gets no discount | BRONZE | 100 | 0
    """)
void calculatesDiscount(String scenario, String tier, int amount, int expected) {
    assertEquals(expected, discountService.calculate(tier, amount));
}
```

## What Changed

- All three test cases are expressed as rows in a single table.
- The `Discount?` column (with the `?` suffix) indicates the expected output value.
- The `Scenario` column on the left gives each row a human-readable description — this also serves as the test name in your test report.
- The single `assertEquals` in the method body is applied once per row; no `if` or `switch` logic is needed.

## Notes

- The `@TableTest` annotation is provided by the TableTest library for JUnit 5.
- Column values are passed as method parameters in left-to-right order.
- The `?` suffix on `Discount?` is a convention to mark expected-value columns; it does not affect parsing but makes intent clear at a glance.
- If your `calculate` method takes `int` parameters, TableTest will handle the conversion from the string values in the table automatically.
