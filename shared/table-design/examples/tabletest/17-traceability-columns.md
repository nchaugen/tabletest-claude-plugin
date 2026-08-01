```java
@TableTest("""
    Scenario                     | Body Weight (kg) | Renal Function | Dose Band? | Daily Dose (mg)?
    Adult, normal function       | 70               | Normal         | Standard   | 500
    Adult, impaired function     | 70               | Impaired       | Reduced    | 250
    Low weight, normal function  | 40               | Normal         | Low        | 300
    """)
```

`Dose Band?` is not strictly necessary, but it lets a reader trace weight + renal function -> band ->
daily dose, and a failure shows which step broke.
