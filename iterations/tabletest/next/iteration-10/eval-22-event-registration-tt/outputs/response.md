All 13 rows compile and run correctly, failing as expected with `UnsupportedOperationException` from the stub — exactly the TDD red state the task calls for.

I wrote two `@TableTest` methods in `src/test/java/com/example/EventRegistrationServiceTest.java`:

1. **`validatesRegistrationDetails`** (8 rows) — covers name-required, email-format validation, and optional dietary/accessibility fields, with `Accepted?`/`Error Message?` as expectation columns. Registration date and group size are held at pricing-neutral values (documented in `@Description`) since pricing is a separate concern.

2. **`appliesTheHigherDiscount`** (5 rows) — covers the early-bird cutoff boundary (on vs. day-before 2025-03-01), the group-size threshold boundary (4 vs. 5), and the non-stacking rule (both conditions met → 20% early-bird wins, not 35% or 15%), with `Price?`/`Discount?` as expectation columns compared via `BigDecimal.compareTo` to avoid scale mismatches.

Assumptions documented in the `@Description` blocks (since the spec didn't fully pin these down):
- Whitespace-only name is treated as missing.
- "Invalid email" covers anything without a local-part/`@`/domain shape.
- `discount` is the currency amount removed from base price; `price` is the post-discount amount.

I also added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`. The service, `RegistrationResult`, and the two empty `EventRegistration`/`RegistrationService` subclasses were left untouched — logic is still a stub throwing `UnsupportedOperationException`, ready for implementation next.