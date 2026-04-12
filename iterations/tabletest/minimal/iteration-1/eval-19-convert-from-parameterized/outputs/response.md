No build files in this directory. Here's the TableTest conversion:

```java
import org.tabletest.junit.TableTest;

class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
        Scenario                  | Email                    | Valid?
        Standard address          | user@example.com         | true
        Subdomain address         | admin@company.co.uk      | true
        With dot and plus tag     | test.name+tag@domain.org | true
        No at-sign                | plainaddress             | false
        Missing local part        | @missinglocal.com        | false
        Domain starts with dot    | user@.com                | false
        No dot in domain          | user@com                 | false
        Empty string              | ''                       | false
        """)
    void validatesEmailAddress(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
```

Key changes from the original:
- `@CsvSource` replaced with `@TableTest` and a Markdown-style pipe table
- Added a `Scenario` column (leftmost, not bound to a parameter) describing each case
- Expectation column `Valid?` uses the `?` suffix convention
- Empty string uses `''` (TableTest syntax for empty string, matching the original `''` in `@CsvSource`)
- Make sure to add `org.tabletest:tabletest-junit` to your build file if it isn't already there.