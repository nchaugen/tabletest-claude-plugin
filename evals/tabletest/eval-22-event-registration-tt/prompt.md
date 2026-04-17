A Gradle Java project has been created for this feature.

Under `src/main/java/com/example`, the project already contains a stub `EventRegistrationService` plus `RegistrationResult` with both price and discount fields. The registration logic is not implemented yet.

We want to write tests before implementing the logic.

Write TableTests for the event registration validation and pricing/discount behavior exposed via `EventRegistrationService.register(...)`.

The rules are:
- Validation: email must be valid format, name is required. dietaryRequirements and accessibilityNeeds are optional (null is fine).
- Early-bird pricing: registrations before 2025-03-01 get 20% off the base price of £100
- Group discount: groups of 5 or more get 15% off
- If both early-bird and group apply, only the higher discount is used (they don't stack)
- Invalid registrations return a rejected result with an error message

It is OK to keep a stub implementation for the service so the tests compile, but do not implement the actual registration logic yet.
