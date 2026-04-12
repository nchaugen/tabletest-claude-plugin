Here's the converted TableTest:

```java
import org.tabletest.junit.TableTest;

class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
        Scenario               | Email                    | Valid?
        Standard email         | user@example.com         | true
        Subdomain email        | admin@company.co.uk      | true
        With plus tag          | test.name+tag@domain.org | true
        No at sign             | plainaddress             | false
        Missing local part     | @missinglocal.com        | false
        Domain starts with dot | user@.com                | false
        No TLD                 | user@com                 | false
        Empty string           | ''                       | false
        """)
    void validatesEmailFormat(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
```

Key changes:
- **Scenario column** added as leftmost column — names describe the input condition rather than the outcome
- **`Valid?`** suffix signals this is the expectation column
- **`''`** for the empty string case (TableTest syntax for empty string)
- Method renamed to `validatesEmailFormat` — describes the behavior, not the test action
- JUnit's built-in converter handles `true`/`false` → `boolean` automatically, so no `@TypeConverter` needed

Make sure `org.tabletest:tabletest-junit` is in your `pom.xml`/`build.gradle` if it isn't already.