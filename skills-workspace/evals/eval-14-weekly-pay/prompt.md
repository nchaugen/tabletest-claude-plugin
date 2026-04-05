Write tests for this feature:

---

Calculate weekly pay for hourly employees

We need to calculate weekly pay based on hours worked. The rules:
- Weekday hours up to 40 are paid at the base hourly rate
- Weekday hours beyond 40 are overtime, paid at 1.5× the base rate (time-and-a-half)
- Sunday hours are always paid at 2× the base rate (double time)
- Holiday hours are always paid at 2× the base rate (double time)
- Total pay cannot go below zero
- A negative hourly rate is not allowed (should be rejected)

The employee submits their hours (weekday, Sunday, holiday) and their hourly rate. The system calculates their weekly pay.
