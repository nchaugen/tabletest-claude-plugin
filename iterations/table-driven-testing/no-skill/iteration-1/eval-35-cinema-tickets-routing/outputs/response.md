The tests compile cleanly and fail exactly as expected (each hits the stub's `UnsupportedOperationException`), which confirms the harness is wired correctly ahead of the real implementation.

I wrote `src/test/java/com/example/cinema/TicketPricerTest.java` with a single `@ParameterizedTest` + `@CsvSource` table (10 rows) covering:
- each price tier (child 8€, adult 14€, senior 10€) with and without matinee
- the tier boundaries at age 11/12 and 64/65
- the matinee 2€ discount applied on top of each tier

Assumption stated: "under 12" is exclusive (age 12 is adult) and "65 and over" is inclusive (age 65 is senior) — reflected directly in the boundary rows (11→child, 12→adult, 64→adult, 65→senior).