No project files found — this looks like a standalone conversion request. Here's the TableTest equivalent:

```java
import org.tabletest.junit.TableTest;

class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
        Scenario               | email                    | Valid?
        Standard address       | user@example.com         | true
        Subdomain with TLD     | admin@company.co.uk      | true
        With plus tag          | test.name+tag@domain.org | true
        Plain address          | plainaddress             | false
        Missing local part     | @missinglocal.com        | false
        Missing domain label   | user@.com                | false
        No TLD                 | user@com                 | false
        Empty string           | ''                       | false
        """)
    void validatesEmail(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
```

A few things to check before using this:

1. **Dependency** — add to your `pom.xml`/`build.gradle` if not already present:
   ```xml
   <dependency>
       <groupId>org.tabletest</groupId>
       <artifactId>tabletest-junit</artifactId>
       <version>VERSION</version>
       <scope>test</scope>
   </dependency>
   ```

2. **Imports** — replace the `@ParameterizedTest`/`@CsvSource` imports with `import org.tabletest.junit.TableTest;`

The scenario names now describe the *condition* being tested (e.g., "Missing local part") rather than just repeating the input, which makes failures easier to diagnose.