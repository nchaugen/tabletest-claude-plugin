Test structure verified — that command was unnecessary noise, ignore it. Everything's in place:

- `build.gradle` — added `org.tabletest:tabletest-junit:1.2.2` test dependency (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).
- `src/test/java/com/example/BonusCalculatorTest.java` — one `@TableTest` covering all 6 level×department combinations as a single lookup rule. The CONTRACTOR row uses a `{SALES, ENGINEERING}` value set on `Department` since the rule states department doesn't affect the outcome for contractors — that claim is now exercised, not just declared.
- Ran `gradle test`: it compiles, the table parses, and all 6 generated cases fail with `UnsupportedOperationException` from the stub — exactly the pre-implementation state you asked for.

Assumption documented in `@Description`: `calculateBonusPercentage` returns the raw percentage number (e.g. `15` for 15%), not a fraction like `0.15` — this wasn't specified, so I noted it on the table itself.