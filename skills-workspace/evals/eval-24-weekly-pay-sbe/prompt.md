We're about to implement weekly pay calculation for hourly employees and I want to work through the examples before we start coding.

The rules:
- Weekday hours up to 40 are paid at the base hourly rate
- Weekday hours beyond 40 are overtime, paid at 1.5× the base rate (time-and-a-half)
- Sunday hours are always paid at 2× the base rate (double time)
- Holiday hours are always paid at 2× the base rate (double time)
- Total pay cannot go below zero
- A negative hourly rate is not allowed (should be rejected)

Can you help me work through the examples so we can agree on the expected behaviour before implementing?
