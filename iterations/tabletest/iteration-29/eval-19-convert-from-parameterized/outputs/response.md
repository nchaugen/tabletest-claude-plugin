There's no Java project in the working directory, so I can't verify dependencies — you'll need to add `org.tabletest:tabletest-junit` to your build file yourself (Maven groupId: `org.tabletest`, artifactId: `tabletest-junit`).

Here's the converted test:

```java
import org.tabletest.junit.TableTest;

class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
        Scenario                    | Email                    | Valid?
        Simple address              | user@example.com         | true
        Country-code TLD            | admin@company.co.uk      | true
        With dot, plus, and subdomain | test.name+tag@domain.org | true
        No at-sign                  | plainaddress             | false
        Missing local part          | @missinglocal.com        | false
        Empty label after at        | user@.com                | false
        No TLD                      | user@com                 | false
        Empty string                | ''                       | false
        """)
    void validatesEmailAddress(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
```

Key changes from the original:
- `@CsvSource` replaced with a `@TableTest` text block
- Scenario column added as the leftmost column (not mapped to a parameter)
- Expectation column suffixed with `?`
- `''` retained for empty string (TableTest uses the same convention)
- Scenario names describe input conditions, not expected outcomes