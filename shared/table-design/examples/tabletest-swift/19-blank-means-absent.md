```swift
@TableTest("""
    Scenario               | Humidity % | Override Setpoint | Vent Position?
    No override configured | 80         |                   | open
    Override supplied      | 80         | 90                | closed
    """)
func resolvesVentPosition(humidity: Int, overrideSetpoint: Int?, vent: VentPosition) { ... }
```

The blank row specifies what the controller does with *no* override. Writing `0` there would specify
something else, and defaulting it in the function body would specify nothing at all. A blank cell is
`nil`, so its parameter is optional; `''` is an empty **string** and fails to convert to a number.
