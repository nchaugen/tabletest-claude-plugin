```swift
@TableTest("""
    Scenario                           | Donor Age | Haemoglobin | Recent Travel | Eligible?
    Below the minimum age              | 16        | {125, 140}  | {true, false} | false
    Eligible adult donor               | 35        | 140         | false         | true
    Travelled recently, otherwise fine | 35        | 140         | true          | false
    """)
func decidesDonorEligibility(age: Int, haemoglobin: Int, recentTravel: Bool, eligible: Bool) { ... }
```

The first row claims age alone decides it, and varies the two inputs it ignores so a row could
contradict the claim.
