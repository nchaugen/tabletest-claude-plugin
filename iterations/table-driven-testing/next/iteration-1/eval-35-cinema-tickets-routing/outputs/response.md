No problem — I won't touch git. Here's the summary of what I delivered:

**`build.gradle`** — added `org.tabletest:tabletest-junit:1.2.2` as a test dependency (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).

**`src/test/java/com/example/cinema/TicketPricerTest.java`** — two `@TableTest` methods:

1. `determinesStandardPriceByAgeBand` — the age-band → price rule, held at non-matinee. One row per tier as a value set spanning both boundaries (`{0,1,11}`→8, `{12,40,64}`→14, `{65,90,120}`→10), so each tier is proven fully without redundant boundary rows.
2. `appliesMatineeDiscount` — the flat 2-euro matinee reduction, tested once per age band (child/adult/senior) with the non-matinee price as a traceability column, so each row shows both the baseline and the discounted price.

Kept as two tables rather than one, per the "separate rules from arithmetic" principle — age-band classification and the matinee discount are different rules (lookup vs. flat adjustment), and fusing them would make expectation cells un-derivable in one step.

**Assumption stated in code** (not asked as a question, since this is non-interactive): ages are non-negative; no validation test for negative/invalid ages since the rules don't specify that behavior.

Verified with `gradle test`: the project compiles, and all 12 generated test cases fail with `UnsupportedOperationException` from the stub — exactly the "tests first, red until implemented" state you asked for.