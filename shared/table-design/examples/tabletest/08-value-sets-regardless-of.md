```java
@TableTest("""
    Scenario                          | Donor Age | Haemoglobin | Recent Travel | Eligible?
    Below the minimum age             | 16        | {125, 140}  | {yes, no}     | no
    Eligible adult donor              | 35        | 140         | no            | yes
    Travelled recently, otherwise fine| 35        | 140         | yes           | no
    """)
void decidesDonorEligibility(int age, int haemoglobin, boolean recentTravel, boolean eligible) { ... }
```

The first row claims age alone decides it, and varies the two inputs it ignores so a row could
contradict the claim.
