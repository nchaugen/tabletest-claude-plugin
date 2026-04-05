I have this JUnit parameterized test and I'd like to convert it to use TableTest:

```java
class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @ParameterizedTest
    @CsvSource({
        "user@example.com, true",
        "admin@company.co.uk, true",
        "test.name+tag@domain.org, true",
        "plainaddress, false",
        "@missinglocal.com, false",
        "user@.com, false",
        "user@com, false",
        "'', false"
    })
    void testEmailValidation(String email, boolean expected) {
        assertEquals(expected, validator.isValid(email));
    }
}
```

Can you convert this to a TableTest?
