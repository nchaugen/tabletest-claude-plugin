```java
@TableTest("""
    Scenario                   | Humidity % | Override Setpoint | Vent Position?
    No override configured     | 80         |                   | OPEN
    Override supplied          | 80         | 90                | CLOSED
    """)
void resolvesVentPosition(int humidity, Integer overrideSetpoint, VentPosition vent) { ... }
```

The blank row specifies what the controller does with *no* override. Writing `0` there would specify
something else, and defaulting it in the method body would specify nothing at all. A column whose
type is a number has no present-but-empty value to write: `''` is an empty **string** and fails to
convert.
