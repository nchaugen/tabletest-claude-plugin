Two tables in one class, one notation for the concept they share:

```java
@TableTest("""
    Scenario           | Upstream Latency | Response Time?
    Healthy upstream   | <10              | <50
    Upstream throttled | <400             | <500
    """)
void answersWithinTheLatencyBudget(Latency upstreamLatency, Latency responseTime) { ... }

@TableTest("""
    Scenario           | Upstream Latency | Report Time?
    Healthy upstream   | <10              | <50
    Upstream throttled | <400             | <500
    """)
void publishesTheReportWithinTheLatencyBudget(Latency upstreamLatency, Latency reportTime) { ... }

@TypeConverter
public static Latency toLatency(String value) { ... }
```

A bare `50` in the second table would leave the reader deciding whether it means a maximum or an
exact value. **One converter per target type, per class**, matched on the *erased* type: two
converters returning `Latency` throw at runtime, so once two columns share a type the shared notation
is not a matter of taste.
