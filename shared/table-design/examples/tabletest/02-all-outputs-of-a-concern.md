```java
// Good — every output of one climate decision in one table
@TableTest("""
    Scenario             | Humidity % | Temp (C) | Vent Position? | Heater? | Alert?
    Warm and damp        | 80         | 28       | OPEN           | off     |
    Cold and damp        | 80         | 8        | CLOSED         | on      | Condensation risk
    Within target range  | 55         | 21       | CLOSED         | off     |
    """)
void resolvesClimateResponse(int humidity, int temp,
                             VentPosition vent, String heater, String alert) { ... }
```
