Here's the TableTest conversion:

```java
class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
        Scenario                  | Email                    | Valid?
        Standard address          | user@example.com         | true
        Subdomain TLD             | admin@company.co.uk      | true
        Dot, plus sign, and tag   | test.name+tag@domain.org | true
        Missing @ symbol          | plainaddress             | false
        Missing local part        | @missinglocal.com        | false
        Missing domain name       | user@.com                | false
        Missing TLD               | user@com                 | false
        Empty string              | ''                       | false
        """)
    void shouldValidateEmailAddress(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
```

Key changes from the original:
- `@ParameterizedTest` + `@CsvSource` replaced by a single `@TableTest` annotation
- Added a `Scenario` column describing each input condition
- `expected` parameter renamed to `valid` to match the `Valid?` column
- Empty string `''` maps directly to an empty `String` — TableTest handles this built-in

Make sure your `pom.xml`/`build.gradle` includes the `org.tabletest:tabletest-junit` dependency alongside JUnit Jupiter 5.11+. If it's not there yet, let me know and I can add the correct coordinates.