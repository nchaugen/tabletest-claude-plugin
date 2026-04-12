No project files found — I'll provide the converted TableTest directly.

The test has a single concern (email validation), with all rows sharing the same assertion. Here's the conversion:

```java
import org.tabletest.junit.TableTest;

class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
        Scenario             | email                    | Valid?
        Standard address     | user@example.com         | true
        Country code domain  | admin@company.co.uk      | true
        With plus tag        | test.name+tag@domain.org | true
        No domain parts      | plainaddress             | false
        Missing local part   | @missinglocal.com        | false
        Empty domain label   | user@.com                | false
        No TLD               | user@com                 | false
        Empty string         | ''                       | false
        """)
    void validatesEmailFormat(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
```

Key decisions:
- **Scenario names** describe the structural condition of the email, not the outcome (`Missing local part` not `Returns false`)
- `''` preserves the empty string case from the original `@CsvSource`
- The `?` suffix on `Valid?` marks it as the expectation column
- No custom converters needed — JUnit handles `String` and `boolean` natively

**Dependency to add** if not already present:
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```