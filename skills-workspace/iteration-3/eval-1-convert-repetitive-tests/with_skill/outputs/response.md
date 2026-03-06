# Converting Repetitive Tests to TableTest

These three tests share identical structure — same method under test, same assertion pattern, only the inputs and expected value differ. That's a perfect fit for TableTest.

Here's the converted version:

```java
@TableTest("""
    Scenario              | Tier   | Order Amount | Discount?
    Gold customer         | GOLD   | 100          | 20
    Silver customer       | SILVER | 100          | 10
    Bronze customer       | BRONZE | 100          | 0
    """)
void calculatesDiscountByTier(String tier, int orderAmount, int discount) {
    assertEquals(discount, discountService.calculate(tier, orderAmount));
}
```

## What changed

- The three separate `@Test` methods collapse into a single `@TableTest` with one row per original test.
- The scenario column (leftmost) gives each row a descriptive label that appears in failure messages.
- `Tier` and `Order Amount` are the inputs; `Discount?` (suffixed with `?`) is the expected output column.
- The method body has a single `assertEquals` — the table drives which values it uses per row.
- No `if` or `switch` statements are needed; the table data expresses all the variation.

## Adding more cases later

If you later add a PLATINUM tier or want to test edge cases (order amount of 0, large orders), you just add rows to the table — no new test methods needed:

```java
@TableTest("""
    Scenario              | Tier     | Order Amount | Discount?
    Gold customer         | GOLD     | 100          | 20
    Silver customer       | SILVER   | 100          | 10
    Bronze customer       | BRONZE   | 100          | 0
    Gold on zero order    | GOLD     | 0            | 0
    """)
void calculatesDiscountByTier(String tier, int orderAmount, int discount) {
    assertEquals(discount, discountService.calculate(tier, orderAmount));
}
```

## Dependency check

Before running this, ensure your `pom.xml` includes:

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

And that you're on JUnit 5.11 or higher (TableTest requires it).
