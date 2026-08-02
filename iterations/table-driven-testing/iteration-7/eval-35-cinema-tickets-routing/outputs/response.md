Not a git repo (confirmed by environment info) — no commit needed. Here's a summary of the delivered work.

## What was written

**`build.gradle`** — added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'` (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).

**`src/test/java/com/example/cinema/TicketPricerTest.java`** — one `@TableTest` with 7 rows over `Age`, `Matinee`, `Price?`:

| Scenario | Age | Matinee | Price? |
|---|---|---|---|
| Child just below the adult cutoff | 11 | false | 8 |
| Adult just at the cutoff | 12 | false | 14 |
| Adult just before the senior cutoff | 64 | false | 14 |
| Senior at the cutoff | 65 | false | 10 |
| Child, matinee screening | 8 | true | 6 |
| Adult, matinee screening | 30 | true | 12 |
| Senior, matinee screening | 70 | true | 8 |

Design notes:
- The four non-matinee rows sit exactly on the two tier boundaries (11/12 and 64/65), so each row does double duty: proving the tier price *and* exercising the boundary from both sides.
- The three matinee rows use non-boundary representative ages, since the discount is a flat −2 with no boundary of its own, and confirm the discount applies to each tier.
- `@Description` records the age-band assumption and flags negative-age behavior as unspecified/untested (documented rather than guessed at).

**Verified**: ran `gradle test` — it compiles, and all 7 cases fail with `UnsupportedOperationException` from the untouched stub, confirming the tests are wired correctly and will pass once pricing logic is implemented.