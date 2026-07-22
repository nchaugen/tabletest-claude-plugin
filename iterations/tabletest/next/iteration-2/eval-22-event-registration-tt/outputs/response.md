Wrote two `@TableTest` methods in `src/test/java/com/example/EventRegistrationServiceTest.java` covering the two concerns, added the `tabletest-junit` dependency to `build.gradle`, and verified everything compiles and all 15 generated cases run (failing only on the stub's `UnsupportedOperationException`, as intended pre-implementation).

**Design:**
- `validatesRegistration` — 9 rows covering name required (missing/blank), email format (missing @, local part, domain, TLD), and both optional fields present/absent. Registration date/group size are fixed constants in the method body since they don't affect validation.
- `appliesEarlyBirdAndGroupDiscount` — 6 rows covering no discount, early-bird alone, the exact cutoff-date boundary (not early-bird), group discount alone, the group-size boundary (4 vs 5), and the combined case proving discounts don't stack (early-bird's 20% wins over group's 15%). Used a `@TypeConverter` for `before cutoff`/`on cutoff`/`after cutoff` per the skill's date-readability guidance, and `compareTo` for BigDecimal assertions so scale differences in the eventual implementation won't cause false failures.

**Assumptions** (documented in `@Description`): rejected registrations return `"Name is required"` or `"Invalid email format"`.

Next step is implementing the actual logic in `EventRegistrationService` to make these pass.