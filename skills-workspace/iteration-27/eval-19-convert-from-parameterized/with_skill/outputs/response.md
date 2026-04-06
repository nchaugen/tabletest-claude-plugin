No build file found in this project, so I can't verify the dependency. I'll include a note about that.

The conversion is straightforward — one input column, one boolean expectation column:

```java
class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
        Scenario           | Email                    | Valid?
        Standard address   | user@example.com         | true
        Subdomain TLD      | admin@company.co.uk      | true
        With plus tag      | test.name+tag@domain.org | true
        No @ symbol        | plainaddress             | false
        Missing local part | @missinglocal.com        | false
        Empty domain part  | user@.com                | false
        Missing TLD        | user@com                 | false
        Empty string       | ''                       | false
        """)
    void shouldValidateEmail(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
```

Key points:
- `''` maps the empty string case (blank cell would be `null` instead)
- `@missinglocal.com` doesn't need quoting — `@` isn't a special TableTest character
- Parameter order follows column order (scenario excluded)

**Dependency**: If `org.tabletest:tabletest-junit` isn't in your `pom.xml`/`build.gradle` yet, read `skills/tabletest/references/dependency-setup.md` for the exact coordinates.