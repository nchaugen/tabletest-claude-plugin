Here's the TableTest conversion:

```java
class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
        Scenario                | Email                    | Valid?
        Standard address        | user@example.com         | true
        Multi-part TLD          | admin@company.co.uk      | true
        With dot and plus tag   | test.name+tag@domain.org | true
        No @ symbol             | plainaddress             | false
        Missing local part      | @missinglocal.com        | false
        Domain starts with dot  | user@.com                | false
        No TLD dot              | user@com                 | false
        Empty string            | ''                       | false
        """)
    void testEmailValidation(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
```

Key points:
- `''` maps directly to an empty string (same as TableTest's empty string syntax)
- `Valid?` uses the `?` suffix to mark it as an expectation column
- Scenario names describe the input condition, not the outcome
- JUnit handles `String` and `boolean` conversion natively — no type converters needed