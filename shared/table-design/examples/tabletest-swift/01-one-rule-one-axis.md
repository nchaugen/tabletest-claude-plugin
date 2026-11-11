```swift
// Two concerns, two tables. "Duty eligibility AND rest credit" fails the "and" test.
@TableTest("""
    Scenario       | Hours Since Rest | Max Duty Hours (Policy) | Fit To Fly?
    At the limit   | 13               | 13                      | true
    Past the limit | 14               | 13                      | false
    """)
func decidesFitnessToFly(hoursSinceRest: Int, maxDutyHours: Int, fitToFly: Bool) { ... }
```
