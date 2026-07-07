A Gradle Java project has been created for this feature.

Under `src/main/java/com/example/cinema`, the project already contains a stub `TicketPricer`. The pricing logic is not implemented yet.

We want to write the tests before implementing the pricing logic.

Write table-driven tests for `TicketPricer.priceInEuros(...)`.

The rules are:
- Children under 12 pay 8 euros
- Adults pay the standard 14 euros
- Seniors (65 and over) pay 10 euros
- Matinee screenings are 2 euros off for everyone

Keep the stub unimplemented — the tests are expected to fail until the logic is written, but they must compile.
