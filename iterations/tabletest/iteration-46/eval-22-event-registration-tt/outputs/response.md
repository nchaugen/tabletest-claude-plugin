Done. Summary of what was written:

**`build.gradle`** — added `org.tabletest:tabletest-junit:1.2.2` test dependency (JUnit Jupiter was already 6.0.3, well above the 5.11 minimum).

**`src/test/java/com/example/EventRegistrationServiceTest.java`** — two `@TableTest` methods, split by concern:

1. **`validatesRegistration`** — name/email validation. Covers: valid registration with optional fields absent vs. provided, missing name, blank name (value set for `''`/`'   '`), missing email, malformed email (value set of several bad formats, one obligation), and a precedence case where both name and email are invalid (documented assumption: name error wins). `registrationDate`/`groupSize` are held constant and declared in `@Description` since this table isn't about pricing.

2. **`appliesPricingAndDiscount`** — early-bird vs. group discount resolution. Uses a `@TypeConverter` (`parseRegistrationTiming`) so rows read as `before cutoff`/`on cutoff` instead of raw ISO dates, with the cutoff meaning explained in `@Description`. Four rows cover no-discount, early-bird-only, group-only, and both-thresholds-met (proving early-bird wins without stacking), with group size expressed as tier value sets (`{1,4}` and `{5,10}`) spanning each side of the size-5 boundary.

Verified: `compileTestJava` succeeds, and `test` runs all 18 generated cases (parsed/converted correctly), each failing only with the stub's `UnsupportedOperationException` as expected — no implementation was added, per the request.