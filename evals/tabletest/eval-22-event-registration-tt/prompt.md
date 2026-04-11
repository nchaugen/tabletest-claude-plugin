Write TableTests for an event registration validation and pricing feature.

The method signatures are:

```java
RegistrationResult register(String name, String email, String dietaryRequirements, String accessibilityNeeds, LocalDate registrationDate, int groupSize)
```

The rules:
- Validation: email must be valid format, name is required. dietaryRequirements and accessibilityNeeds are optional (null is fine).
- Early-bird pricing: registrations before 2025-03-01 get 20% off the base price of £100
- Group discount: groups of 5 or more get 15% off
- If both early-bird and group apply, only the higher discount is used (they don't stack)
- Invalid registrations return a rejected result with an error message
