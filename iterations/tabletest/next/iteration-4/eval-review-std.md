# Eval Review — tabletest variant=next, Iteration 4

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-24 · **Evals:** 17

## Summary

103/123 (83.7%) · 4531212 tokens · 1096.2s · $3.7212

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 3

**Eval definition changed — not comparable (4):**
- ⚠️ eval-2-parse-dates: fingerprint differs from iteration 3; re-baseline to compare
- ⚠️ eval-22-event-registration-tt: fingerprint differs from iteration 3; re-baseline to compare
- ⚠️ eval-23-loan-approval-tt: fingerprint differs from iteration 3; re-baseline to compare
- ⚠️ eval-29-shopping-cart-tt: fingerprint differs from iteration 3; re-baseline to compare

## Resource Comparison vs Iteration 3

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | — | — | — | — | — | — |
| eval-2-parse-dates | — | 14/15 | — | 591885 | — | 183.6 |
| eval-7-permission-check | — | — | — | — | — | — |
| eval-8-money-parse | — | — | — | — | — | — |
| eval-9-bonus-contractor-structure | — | — | — | — | — | — |
| eval-14-weekly-pay | — | — | — | — | — | — |
| eval-15-reis-discount | 21/25 | — | 1314175 | — | 306.3 | — |
| eval-18-convert-from-code | 17/24 | — | 840562 | — | 236.3 | — |
| eval-20-collections-and-quoting | — | — | — | — | — | — |
| eval-22-event-registration-tt | 26/27 | 25/27 | 672731 | 771866 | 137.3 | 176.1 |
| eval-23-loan-approval-tt | 14/21 | 20/21 | 701022 | 787298 | 120.7 | 197.6 |
| eval-25-convert-from-spock | — | — | — | — | — | — |
| eval-26-convert-from-kotest | — | — | — | — | — | — |
| eval-27-convert-from-testng | — | — | — | — | — | — |
| eval-28-convert-from-methodsource | — | — | — | — | — | — |
| eval-29-shopping-cart-tt | 25/26 | 24/26 | 1002722 | 852532 | 295.7 | 289.9 |
| eval-30-order-splitting-tt | — | — | — | — | — | — |

## Per-Eval Results

### ⚠️ Eval eval-15-reis-discount

**21/25** · 1314175 tokens · 306252ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ❌ **window-boundary-uses-purchase-time**: The 30-day window is exercised at time-of-purchase granularity, not whole days only. The requirement says the count is 'measured at the time of purchase', so at least one pair of rows must distinguish just-inside from just-outside by less than a day (e.g. 29 days 23 hours ago counts, 30 days 1 hour ago does not). A table whose finest distinction is whole days (30 vs 31) FAILS, however many boundary rows it has.
  > ReisTravelCountTest uses only whole-day boundaries (SINGLE@30 vs SINGLE@31). No sub-day distinction like '29 days 23 hours' vs '30 days 1 hour' is present.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > ReisDiscountLadderTest does not use value sets for ticket counts; each row has a single count value (1,4,5,7,10,15,39,40,45,100), not grouped sets like {5,6,7,8,9}
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > ReisDiscountLadderTest Travel Count column contains single integers (1,4,5,7,10,15,39,40,45,100), not value sets like {5,6,7,8,9} or {5,9}
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
- ❌ **zone-independent-counting**: The rolling-window counting concern shows by row that a past purchase's zone does not affect whether it counts toward the travel count — for example history entries whose zones differ (or a zone value set) reaching the same count. The claim must be exercised by data: stating it only in @Description, or fixing every history entry to a single zone in the test or a converter, FAILS. This is about counting, not about the discount percentage, which zone-irrelevance-visible covers.
  > ReisTravelCountTest has no zone column and no zone variation in history. ReisTestConverters.parsePastPurchase hardcodes all history to ZONE_1, making the claim unstated in data.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > ReisDiscountLadderTest uses separate rows for each tier (e.g., 'Below the first rung | 1 | 0', 'Just before the first rung | 4 | 0') rather than value sets like '| {1, 2, 3, 4} | 0'.
- ✅ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-18-convert-from-code

**17/24** · 840562 tokens · 236287ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
  > @Description states 'Risk score = (age / 10) + (claimCount * 15)' and 'Premium = 100 + riskScore * 2.0' / 'Premium = 200 + riskScore * 3.5' — exposing internal formulas violates black-box discipline.
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > First table mixes renewal auto-approval rule with premium calculation (includes Premium? column and rows testing premium values), conflating decision and premium concerns.
- ❌ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
  > First @TableTest 'autoApprovesRenewalsWithNoClaimsRegardlessOfAge' includes both Decision? and Premium? columns, mixing concerns in a single table.
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields. No arithmetic duplicating private method logic appears in the test.
- ❌ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details (e.g. 'riskScore = age/10 + claimCount*15', 'rejected when riskScore > 75'). Black-box tests describe observable behaviour, not code internals.
  > @Description in rejectsApplicationsAboveRiskThreshold states 'Risk score = (age / 10) + (claimCount * 15)' and 'rejected once this score exceeds 75' — exposing internal formula and threshold logic.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff. The senior threshold (64 vs 65) is NOT expected here — it produces no decision difference (both APPROVED) and belongs to the premium table.
  > No decision-only table exists; the renewal table lacks 4-claim rows. Second table tests age 0 + 5 claims (APPROVED) and age 10 + 5 claims (REJECTED), but never tests 4 claims to show the cliff boundary.
- ❌ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
  > The premium table `calculatesPremiumBySeniorAgeTier` shows age 64 vs 65 boundary (112.00 vs 221.00) but lacks a row isolating the 0-to-1 claim impact at a single age. Row 'Standard premium below senior age' (age 40, 0 claims, 108.00) exists but no corresponding 1-claim row at age 40 to show the +15 risk score effect.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
  > @Description in `rejectsApplicationsAboveRiskThreshold` states 'Risk score = (age / 10) + (claimCount * 15)' and 'exceeds 75', exposing internal formula and threshold. Per spec: 'the risk score is internal...must not appear...in @Description — the reader sees its effect through the input/output rows, not the arithmetic.'
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against InsuranceEvaluator

### ⚠️ Eval eval-22-event-registration-tt

**26/27** · 672731 tokens · 137261ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'. These are genuinely optional and absent.
- ✅ **blank-vs-value-set-correct**: When testing pricing rules, irrelevant inputs (e.g. dietary requirements don't affect price) use value sets or representative values — not blanks (which mean null). Blanks are reserved for genuinely absent/null inputs.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Result?', 'Price?')
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Name', 'Email', 'Registration date', 'Group size', 'Price?') — not code identifiers.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Early-bird single attendee', 'Missing name') — not outcomes ('Rejected', '20% off').
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null (blank) and non-null values are accepted — not hardcoded as null in the method body.
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates — not raw date literals like '2025-02-28'. Readability over precision.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28' instead of descriptive), the early-bird cutoff date appears as a separate policy column so the reader can see both dates and verify the comparison. Passes automatically if descriptive date values are used.
- ✅ **description-no-irrelevant-information**: If @Description is present, it does not include information already visible in the table columns or derivable from the table structure — such as fixed input values that could be columns, or restating a rule the rows already demonstrate. It PASSES when the description adds what the rows cannot: a threshold or constant fixed in the method body, currency, where/when the rule applies, or naming an interpretation as a deliberate assumption (legitimate even when a row also demonstrates it — that is rationale, not redundancy).
- ✅ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row (e.g. scenario 'Age 35, score 700' beside Age and Credit score columns holding 35 and 700); or (2) @Description states a fixed value for an input that is already a column in that table. Otherwise it PASSES. Stating a constant that is NOT a column — a threshold, a policy figure, a value fixed in the method body — is correct and must not fail this assertion.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**14/21** · 701022 tokens · 120677ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-verifiable-from-table**: Each policy threshold (650 for non-seniors, 600 for seniors) is verifiable from the table, in one of two ways: (a) a dedicated policy column (e.g. 'Credit threshold') beside the applicant's score, or (b) boundary rows that bracket it — within one age band, a row that qualifies just above the threshold and a row that fails at/below it, carrying different decisions. The assertion FAILS only if a threshold governs the outcome yet neither a policy column nor a bracketing row pair makes its location visible — e.g. the score column holds arbitrary values with no boundary pair and no threshold column.
  > [split vote 1/3 pass] First table has rows at 64/650 (REJECTED) and 64/651 (APPROVED), and 65/600 (REJECTED) and 65/601 (APPROVED). However, second table 'missingIncomeInformationYieldsPendingReview' uses value sets {18, 65, 90} × {300, 650, 850} all yielding PENDING_REVIEW, obscuring the threshold entirely — no boundary pair visible.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'. Boolean true/false is acceptable for yes/no flags when the parameter type is boolean.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
  > Scenario names include outcomes: 'Approved: standard threshold, stable income', 'Rejected: standard threshold, no stable income', 'Rejected: below standard threshold, regardless of income', 'Approved: senior threshold, stable income', 'Rejected: below senior threshold, regardless of income'. These restate the Result? column value in the name.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **concerns-decomposed**: The distinct concerns (age→threshold, credit boundary, income status) are each covered, without a monolithic table that re-proves unrelated rules through redundant permutations. A single table is acceptable when the concerns share the same input columns and each is covered by only a row or two — as here — so the compact single-table form and a multi-table split are both correct. The assertion FAILS only for a table that cross-multiplies concerns into redundant rows, not for using one table.
  > [split vote 1/3 pass] Three separate tables exist. Table 1 covers age/credit boundary (6 rows). Table 2 covers null income (1 scenario with value sets generating 9 cases). Table 3 re-covers age/credit/income together (6 rows). Table 3 re-proves the boundary pairs (rows 1, 5) and income effects (rows 2, 3, 4, 6) already shown in tables 1 and 2, creating redundant permutations across tables.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Table 3 ('evaluatesFinalLoanDecision') has 6 rows that re-prove rules already covered: rows 1–2 re-prove the income effect at a qualifying score (already in table 1 with stable=true); rows 3, 6 re-prove below-threshold rejection (already in table 1); row 4 re-proves null income → PENDING (already in table 2); rows 5–6 re-prove senior threshold (already in table 1).
- ✅ **covers-age-credit-income**: Age boundary policy, credit score categorisation, and income status are each covered by minimal scenarios. This may be one @TableTest whose rows exercise each concern (boundary pairs for age/credit, income held-constant rows for income status) or separate methods per concern with a combining verdict — both are correct given only a row or two per concern. The assertion FAILS if a concern is not exercised at all (e.g. income status never varied against a fixed score/age), not for choosing one table over several.
- ❌ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row (e.g. scenario 'Age 35, score 700' beside Age and Credit score columns holding 35 and 700); or (2) @Description states a fixed value for an input that is already a column in that table. Otherwise it PASSES. Stating a constant that is NOT a column — a threshold, a policy figure, a value fixed in the method body — is correct and must not fail this assertion.
  > Table 3 scenario 'Approved: standard threshold, stable income' contains 'standard threshold' (650) and 'stable income' (true), both shown in the row's Credit Score and Has Stable Income columns. Scenario 'Rejected: below standard threshold, regardless of income' restates 'below standard threshold' (600 < 650) and 'income' (the column name).
- ✅ **depth-stable-income-effect**: The effect of stable income on the outcome is visible from table rows, not merely implied. The assertion PASSES if some single @TableTest table contains two rows that differ in the stable-income column (or equivalent) while holding the credit score and age columns equal, and those two rows carry different expected decisions. It FAILS if no such row pair exists in any table — including when stable income appears only in scenario names, only in the @Description, or only in rows that also vary another input.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ❌ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
  > [split vote 1/3 pass] Second table 'missingIncomeInformationYieldsPendingReview' has all 9 generated rows with Result? = PENDING_REVIEW (condition 1: expectation column holds the same value in every row), making the rule unfalsifiable.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
  > Second table states 'When hasStableIncome is null, the applicant is sent to pending review regardless of age or score' in @Description, but the critical precedence rule (below-threshold + null income → REJECTED, not PENDING) is absent from all three tables and their descriptions.
- ❌ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
  > Second method title 'missingIncomeInformationYieldsPendingReview' states an external fact (missing income yields pending) rather than what the evaluator does. Striking 'evaluator' leaves 'missing income yields pending review' — still true as a domain rule, not a system behaviour.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-29-shopping-cart-tt

**25/26** · 1002722 tokens · 295673ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing item operations, coupon logic, total calculation, and checkout.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-item-coupon-total-checkout**: Item operations (add/remove), coupon application (validity and replacement), cart total calculation (coupon type effects on price), and checkout (stock verification) are in separate @TableTest methods.
- ✅ **rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script. Each row specifies its own preconditions (e.g. 'Cart with items' as an input column, not implied from a previous row).
- ✅ **coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **coupon-validity-not-coupon-types**: Coupon application table tests validity and replacement (valid code, expired code, nonexistent code, replacing active coupon) — not coupon type effects on price. Coupon type effects belong in the cart total table.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Message?', 'Total?', 'Active coupon after?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Add to empty cart', 'Replace active coupon with expired') — not outcomes ('Error', 'Success').
- ❌ **business-language-columns**: Column names use domain/business language (e.g. 'Product', 'Quantity', 'Message?', 'Cart total?') — not code identifiers like 'productId', 'result'.
  > Column header 'Product Id' appears in addsItemToCart and removesItemFromCart tables; should be 'Product' per business-language guidance
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product. The column format may be map syntax (e.g. [type: PERCENTAGE, value: 10]) or readable shorthand (e.g. '20% cart', '$5 off', '50% off widget').
- ✅ **uses-standard-map-syntax**: Columns representing maps (e.g. cart contents, product catalogue, inventory levels) use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation. Custom string-parsing TypeConverters are not needed for data that maps naturally to Map<String, X>.
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not merely repeat information that the scenario names already convey — e.g. coupon type behaviour (what PERCENT/FIXED/PRODUCT mean) belongs in descriptive scenario names, not duplicated in the description. It PASSES when the @Description adds what scenario names cannot: naming an interpretation as a deliberate assumption (an underspecified rule resolved a particular way), stating why a choice applies, or fixed constants. Noting an assumption is legitimate even when a row also demonstrates it — that is rationale, not redundancy.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

