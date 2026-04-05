Here is a `@TableTest` for `calculateBonusPercentage` covering all the rules, including two CONTRACTOR rows to show it's department-agnostic:

```java
@TableTest("""
    level      | department  | expected
    SENIOR     | SALES       | 15.0
    SENIOR     | ENGINEERING | 12.0
    JUNIOR     | SALES       |  8.0
    JUNIOR     | ENGINEERING |  5.0
    CONTRACTOR | SALES       |  0.0
    CONTRACTOR | ENGINEERING |  0.0
    """)
void calculateBonusPercentage(Level level, Department department, double expected) {
    Employee employee = new Employee(level, department);
    assertThat(calculator.calculateBonusPercentage(employee)).isEqualTo(expected);
}
```

**Column notes:**
- `level` and `department` map directly to the `Level` and `Department` enums by name — TableTest's built-in enum converter handles this automatically.
- `expected` uses `double` matching the return type of the method under test.
- The two CONTRACTOR rows make it explicit that the 0% rule applies regardless of department — this is worth keeping as two separate rows rather than collapsing into one, because it documents the intent that department is irrelevant for contractors.

Adjust the constructor call and `calculator` field to match your actual class structure.