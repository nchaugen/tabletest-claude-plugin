package com.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
