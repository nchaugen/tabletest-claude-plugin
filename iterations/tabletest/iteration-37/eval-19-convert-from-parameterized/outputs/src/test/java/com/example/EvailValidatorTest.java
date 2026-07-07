package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @TableTest("""
            Scenario                        | Email                     | Valid?
            Simple address                  | user@example.com          | true
            Country-code TLD                | admin@company.co.uk       | true
            Plus addressing with dot        | test.name+tag@domain.org  | true
            No @ symbol                     | plainaddress              | false
            Missing local part              | @missinglocal.com         | false
            Missing domain label before TLD | user@.com                 | false
            Missing TLD                     | user@com                  | false
            Empty string                    | ''                        | false
            """)
    void testEmailValidation(String email, boolean valid) {
        assertEquals(valid, validator.isValid(email));
    }
}
