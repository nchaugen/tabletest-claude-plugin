Here's the TableTest conversion:

```java
class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
        Scenario           | Email                    | Valid?
        Standard address   | user@example.com         | true
        Subdomain TLD      | admin@company.co.uk      | true
        With dot and plus  | test.name+tag@domain.org | true
        No @ symbol        | plainaddress             | false
        Missing local part | @missinglocal.com        | false
        Empty domain label | user@.com                | false
        No TLD             | user@com                 | false
        Empty string       | ''                       | false
        """)
    void validatesEmail(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
```

Key points:
- Scenario names describe the input condition, not the outcome
- `''` maps to an empty string (same as your original `''` in `@CsvSource`)
- `boolean` is handled by JUnit's built-in conversion — no `@TypeConverter` needed
- `Valid?` suffix signals it's the expectation column