We are starting an application for calculating weekly pay for hourly employees.
We will tackle the calculation logic first, and we want to do it test-driven.

The calculation rules we need to implement are as follows:
- Weekday hours up to 40 are paid at the base hourly rate
- Weekday hours beyond 40 are overtime, paid at 1.5× the base rate (time-and-a-half)
- Sunday hours are always paid at 2× the base rate (double time)
- Holiday hours are always paid at 2× the base rate (double time)
- Total pay cannot go below zero
- A negative hourly rate is not allowed (should be rejected)

The calculation feature will receive employee hours (weekday, Sunday, holiday) and their hourly rate, and calculates their weekly pay.

The project is a Gradle Java project with `build.gradle` at the root and empty `src/main/java` and `src/test/java` directories. No implementation code exists yet.

Start out by writing tests for this feature, using stub implementation for the actual calculation.
