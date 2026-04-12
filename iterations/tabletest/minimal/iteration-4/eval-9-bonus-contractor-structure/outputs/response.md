No existing Java project. Here's the TableTest for `calculateBonusPercentage`:

```java
@TableTest("""
    Scenario                  | Level      | Department  | Bonus %?
    Senior Sales              | SENIOR     | SALES       | 15.0
    Senior Engineering        | SENIOR     | ENGINEERING | 12.0
    Junior Sales              | JUNIOR     | SALES       | 8.0
    Junior Engineering        | JUNIOR     | ENGINEERING | 5.0
    Contractor Sales          | CONTRACTOR | SALES       | 0.0
    Contractor Engineering    | CONTRACTOR | ENGINEERING | 0.0
    """)
void shouldCalculateBonusPercentage(Level level, Department department, double bonusPercentage) {
    Employee employee = new Employee(level, department);
    assertEquals(bonusPercentage, calculateBonusPercentage(employee));
}
```

**Notes:**
- `Level` and `Department` are enums — JUnit converts them by name automatically, no `@TypeConverter` needed.
- The last two rows use a value set alternative: since CONTRACTOR always gives `0.0` regardless of department, you could collapse them to one row:
  ```
  Contractor (any dept) | CONTRACTOR | {SALES, ENGINEERING} | 0.0
  ```
  This makes the "regardless of department" rule explicit. Use this if you want to signal intent clearly; keep separate rows if you want exhaustive enumeration.