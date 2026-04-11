I want to write a TableTest for this method:

```java
double calculateBonusPercentage(Employee employee)
```

The bonus rules are:
- SENIOR + SALES department = 15%
- SENIOR + ENGINEERING department = 12%
- JUNIOR + SALES department = 8%
- JUNIOR + ENGINEERING department = 5%
- CONTRACTOR level always gets 0%, regardless of department
