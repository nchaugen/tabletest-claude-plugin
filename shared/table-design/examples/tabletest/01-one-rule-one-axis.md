```java
// Two concerns, two tables. "Duty eligibility AND rest credit" fails the "and" test.
@TableTest("""
    Scenario              | Hours Since Rest | Max Duty Hours (Policy) | Fit To Fly?
    Well inside the limit | 6                | 13                      | yes
    At the limit          | 13               | 13                      | yes
    Past the limit        | 14               | 13                      | no
    """)
void decidesFitnessToFly(int hoursSinceRest, int maxDutyHours, boolean fitToFly) { ... }
```
