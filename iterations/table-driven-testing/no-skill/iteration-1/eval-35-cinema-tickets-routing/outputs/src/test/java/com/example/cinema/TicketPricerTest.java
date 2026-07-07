package com.example.cinema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TicketPricerTest {

    private final TicketPricer pricer = new TicketPricer();

    @ParameterizedTest(name = "age={0}, matinee={1} -> {2} euros")
    @CsvSource({
        //  age, matinee, expectedEuros
            "  5,   false,             8", // child, standard screening
            " 11,   false,             8", // child, just under the 12 boundary
            " 12,   false,            14", // adult, boundary turns 12 into adult pricing
            " 30,   false,            14", // adult, standard screening
            " 64,   false,            14", // adult, just under the 65 senior boundary
            " 65,   false,            10", // senior, boundary turns 65 into senior pricing
            " 80,   false,            10", // senior, standard screening
            "  5,    true,             6", // child, matinee discount applies
            " 12,    true,            12", // adult, matinee discount applies
            " 65,    true,             8", // senior, matinee discount applies
    })
    void pricesTicketsByAgeAndScreeningType(int age, boolean matinee, int expectedEuros) {
        assertEquals(expectedEuros, pricer.priceInEuros(age, matinee));
    }
}
