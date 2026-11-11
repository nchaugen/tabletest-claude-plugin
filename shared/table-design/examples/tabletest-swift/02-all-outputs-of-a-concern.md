```swift
// Good — every output of one climate decision in one table
@TableTest("""
    Scenario            | Humidity % | Temp (C) | Vent Position? | Heater? | Alert?
    Warm and damp       | 80         | 28       | open           | false   |
    Cold and damp       | 80         | 8        | closed         | true    | Condensation risk
    Within target range | 55         | 21       | closed         | false   |
    """)
func resolvesClimateResponse(humidity: Int, temp: Int, vent: VentPosition, heater: Bool, alert: String?) { ... }
```
