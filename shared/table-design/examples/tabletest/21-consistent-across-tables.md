Two tables in one class, one notation for the concept they share:

```java
@TableTest("""
    Scenario         | Response Time?
    Healthy upstream | <50
    """)
void answersWithinTheLatencyBudget(Latency responseTime) { ... }

@TableTest("""
    Scenario         | Report Time?
    Healthy upstream | <50
    """)
void publishesTheReportWithinTheLatencyBudget(Latency reportTime) { ... }

@TypeConverter
public static Latency toLatency(String value) { ... }
```

A bare `50` in the second table would leave the reader deciding whether it means a maximum or an
exact value. **One converter per target type, per class**, matched on the *erased* type: two
converters returning `Latency` throw at runtime, so once two columns share a type the shared notation
is not a matter of taste.
