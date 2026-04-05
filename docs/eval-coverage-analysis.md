# Eval Coverage Analysis

Analysis of how well the current evals and assertions cover the skill instructions,
categorised by testable aspect. Last updated: 2026-04-05 (added evals 25-28).

---

# Spec-by-Example Skill

## Skill Instruction Categories → Testable Aspects

### Category 1: Table Structure & Format

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 1.1 | Output is a markdown table | `produces-markdown-table` | 4,5,6,10,12,13 — **well covered** |
| 1.2 | Output columns suffixed with `?` | `output-column-has-question-mark` | 4,5,6,13 + `output-columns-have-question-marks` in 10,12 — **well covered** |
| 1.3 | `?` only on output columns, never inputs | `question-mark-only-on-outputs` | 10,12 — **partial** (only 2 evals) |
| 1.4 | Has a Scenario/name column | _none_ | **gap** |

### Category 2: Business Language & Readability

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 2.1 | Column headers use domain terms, not code | `business-language-columns` | 4,10 + `4.8-readability-business-language` in 17 — **partial** |
| 2.2 | Cell values are concrete domain values, not abstract codes/booleans | _none as standalone_ | **gap** — partially implied by business-language checks |
| 2.3 | Scenario names describe conditions, not outcomes | `scenario-names-describe-conditions` | 4 + `3.6-readability-scenario-names`, `4.15-readability-scenario-names` in 16,17 — **reasonable** |

### Category 3: One Table per Concern (Decomposition)

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 3.1 | Multiple tables when multiple concerns exist | `produces-multiple-tables` | 10,12 — **partial** |
| 3.2 | Each table addresses a distinct concern | `tables-have-distinct-concerns` | 10 + `3.1-decomposition-concern-separation` in 16, `4.1a-d` in 17 — **reasonable** |
| 3.3 | Eligibility/rules separate from amounts/arithmetic | `eligibility-separate-from-amount` | 10 — **weak** (1 eval) |

### Category 4: Value Sets & Irrelevant Inputs

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 4.1 | `{...}` notation for "regardless of" relationships | `value-set-or-multiple-states` in 5, `express-uses-value-sets` + `overnight-grouped` + `standard-destination-value-set-or-blank` in 13 — **reasonable** |
| 4.2 | Value sets preferred over duplicate rows | `express-uses-value-sets` | 13 — **weak** (1 eval, and it fails) |
| 4.3 | Blanks mean absent, value sets mean irrelevant | _none_ | **gap** — no assertion distinguishes these |

### Category 5: Boundary & Threshold Handling

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 5.1 | Threshold values visible as concrete numbers | `threshold-values-visible` | 4 — **weak** (1 eval) |
| 5.2 | Boundary rows (at, just above, just below threshold) | `24h-boundary-near-boundary` | 10 — **weak** (1 eval) |
| 5.3 | Thresholds as explicit columns (not buried in output) | _none_ | **gap** |

### Category 6: Rules vs Arithmetic

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 6.1 | Tables focus on decisions/rules, not arithmetic | `prorated-refund-formula` in 10 (partial) — **weak** |
| 6.2 | Arithmetic gets minimal rows (2-3), rules get depth | _none_ | **gap** |

### Category 7: Stateful Features as Independent Rules

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 7.1 | Each row is independent (no row depends on prior row) | `4.3-rows-independently-executable` | 17 — **weak** (1 eval) |
| 7.2 | State framed as before/action/after, not sequential steps | `4.14-coupon-before-after-columns` | 17 — **weak** (1 eval, fails) |
| 7.3 | State transitions use value sets for multi-state rules | `cancellation-coverage` + `value-set-or-multiple-states` | 5 — **weak** |

### Category 8: Output Traceability

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 8.1 | Output values derivable from inputs by reader | `4.11-correctness-cart-totals` | 17 — **weak** (1 eval) |
| 8.2 | Test data visible in table (not referenced externally) | `4.9-readability-test-data-visible` | 17 — **weak** (1 eval) |

### Category 9: Open Questions & Ambiguity Handling

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 9.1 | Ambiguities surfaced, not silently resolved | `does-not-invent-resolution` + `open-question-surfaced` in 6, `surfaces-genuine-open-questions` + `open-question-surfaced` + `loyalty-refund-ambiguity` in 12, `4.10-depth-open-questions` in 17 — **well covered** |
| 9.2 | Open cells marked with `?` or notes | `missing-income-marked-open` | 4 — **weak** (1 eval) |

### Category 10: Depth of Example Coverage

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 10.1 | Different outcomes covered | `cancellation-coverage` in 5, `covers-both-discounts-applying` in 6, various depth assertions in 16,17 — **reasonable** |
| 10.2 | Boundary conditions included | 5.2 above — **weak** |
| 10.3 | Special/surprising cases included | `extreme-discount-row` in 6, `refund-exception-covered` in 10 — **partial** |
| 10.4 | Missing/absent input scenarios | `missing-income-marked-open` in 4, `trial-cancellation-row` in 10 — **partial** |

### Category 11: Blank Cell Semantics

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| 11.1 | Blank output cells for N/A outcomes (not filler like "N/A") | _none_ | **gap** |
| 11.2 | Blank input cells for genuinely absent optional inputs | _none_ | **gap** |
| 11.3 | Not using blanks where value sets should be used | _none_ | **gap** (related to 4.3) |

---

## Coverage Heat Map (Spec-by-Example)

| Category | Coverage Level | Notes |
|----------|---------------|-------|
| 1. Table Structure & Format | **Strong** | Well covered across many evals |
| 2. Business Language | **Moderate** | Scenario naming good; concrete values gap |
| 3. Decomposition | **Moderate** | Multiple evals but thin per-aspect |
| 4. Value Sets | **Weak** | Key differentiator of skill, under-tested |
| 5. Boundaries & Thresholds | **Weak** | Only 1 eval each, threshold-as-column untested |
| 6. Rules vs Arithmetic | **Gap** | No assertion tests this separation |
| 7. Stateful Features | **Weak** | Only eval 17, with failures |
| 8. Output Traceability | **Weak** | Only eval 17 |
| 9. Open Questions | **Strong** | Well covered across 3 evals |
| 10. Depth of Examples | **Moderate** | Spread thin, boundaries weak |
| 11. Blank Cell Semantics | **Gap** | Entirely untested |

## Priority Gaps (Spec-by-Example)

1. **Blank vs value set semantics** (Cat 11 + 4.3) — Central to skill's design guidance, zero coverage
2. **Threshold as explicit column** (5.3) — Distinctive skill instruction, untested
3. **Rules vs arithmetic separation** (Cat 6) — Dedicated skill section, no assertions
4. **Value set consolidation** (4.2) — Only 1 eval, and it fails — needs more evals or skill refinement
5. **Concrete domain values vs abstract codes** (2.2) — Implied but never directly asserted

---
---

# TableTest Skill

## Skill Instruction Categories → Testable Aspects

The tabletest skill is larger and covers both table design (shared with spec-by-example) and Java/Kotlin code generation. Categories are grouped by: **Code Structure**, **Table Design**, and **Workflow/Process**.

---

### Category T1: Code Structure — Annotations & Method Shape

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T1.1 | Has `@TableTest` annotation | `has-tabletest-annotation` | 1,2,7,8,9,25,26,27,28 — **well covered** |
| T1.2 | Annotation order: `@DisplayName` → `@Description` → `@TableTest` | `annotation-order` | 1,2,7,8,9,14,15,25,26,27,28 — **well covered** |
| T1.3 | `@DisplayName` or descriptive method name | `has-descriptive-title` | 1,2,7,8,9,14,15,25,26,27,28 — **well covered** |
| T1.4 | `@Description` adds info beyond table (not restatement) | `description-if-present-adds-information` | 1,2,7,8,9,14,15 — **well covered** |
| T1.5 | `@Description` uses text block (`"""`) | `description-uses-textblock` | 1,2,7,8,9,14,15 — **well covered** |
| T1.6 | No if/switch in method body | `no-if-switch-in-method` | 1,7,8,9,14,25,26,27,28 — **well covered** |
| T1.7 | Method non-private, non-static, void | _none_ | **gap** (hard to test without compilation) |
| T1.8 | Parameters match column order left-to-right | _none_ | **gap** (hard to test without compilation) |
| T1.9 | Single assertion logic (uniform across rows) | `single-assertion-in-method` | 1 — **weak** (1 eval) |

### Category T2: Code Structure — Syntax & Quoting

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T2.1 | Blank cells for null | `null-as-blank-cell` | 2,8 — **reasonable** |
| T2.2 | `''` for empty strings | _none as assertion_ | **gap** (eval 2 prompt mentions it but no assertion) |
| T2.3 | Quoting for pipes, brackets, quotes | _none_ | **gap** |
| T2.4 | Collection syntax: `[]` for lists, `{}` for sets, `[:]` for empty map | `dimensions-as-list` + `options-as-map` in 25,26,27,28 — **reasonable** (list and map syntax tested; set syntax untested) |
| T2.5 | `\\n` for newlines in values (not literal) | _none_ | **gap** |

### Category T3: Code Structure — Type Conversion

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T3.1 | Built-in conversion for standard types (LocalDate, enum, etc.) | `localdate-result-column` | 2 — **weak** |
| T3.2 | `@TypeConverter` for non-standard formats | `type-conversion-addressed` in 2, `2.12-format-typeconverter` in 15, `options-type-converter` in 25,26,27,28 — **well covered** |
| T3.3 | ISO date format limitation awareness | Implied by eval 2 prompt — **weak** |

### Category T4: Code Structure — Exception Handling

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T4.1 | Exception type as expected column (`Throws?`) | `exception-has-expected-column` in 8, `1.2-error-has-expected-column` in 14 — **reasonable** |
| T4.2 | Exception cases covered in table (not separate @Test) | `exception-handled-cleanly` in 2, `exception-cases-handled` in 8 — **reasonable** |

### Category T5: Table Design — Column Structure

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T5.1 | Scenario column as leftmost | `scenario-column-present` | 1,7,9,25,26,27,28 — **well covered** |
| T5.2 | Expectation columns end with `?` (suffix not prefix) | `has-question-mark-column`, `result-column-with-question-mark` | 1,8,25,26,27,28 — **well covered** |
| T5.3 | Multiple rows (2+) | `has-three-data-rows` | 1 — **weak** (only 1 eval, and it's trivially met) |
| T5.4 | Traceability/intermediate columns | `1.1-traceability-columns` | 14 — **weak** (1 eval) |
| T5.5 | All outputs of same concern in one table | _none_ | **gap** |

### Category T6: Table Design — Values & Readability

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T6.1 | Concrete domain values, not abstract codes | `numeric-types-correct` in 25,26,27,28 + implied by scenario assertions in 14 — **reasonable** |
| T6.2 | Domain terminology in column names | `business-language-columns` | 25,26,27,28 — **reasonable** |
| T6.3 | Scenario names describe conditions not outcomes | `1.7-readability-scenario-names` in 14, `scenario-names-describe-conditions` in 25,26,27,28 — **well covered** |
| T6.4 | Output values traceable to inputs | `1.8-correctness-expected-values` in 14 — **weak** (1 eval) |
| T6.5 | Blank cells for optional/irrelevant inputs (not 0) | `1.6-readability-empty-cells` | 14 — **weak** (1 eval) |

### Category T7: Table Design — Value Sets

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T7.1 | `{...}` for "regardless of" relationships | `uses-value-sets` in 7,9,15,25,26,27,28 — **well covered** |
| T7.2 | Value sets only when all values produce same result | `1.9-correctness-value-set-semantics` in 14, `2.9` in 15 — **reasonable** |
| T7.3 | Value sets reduce row count vs listing combinations | `fewer-than-nine-rows` + `no-duplicate-role-output` in 7 — **partial** |

### Category T8: Table Design — Decomposition (Multiple Tables)

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T8.1 | Separate tables for separate concerns | `2.1-decomposition-concern-separation` in 15, `concerns-decomposed` in 25,26,27,28 — **reasonable** |
| T8.2 | Black-box design (observable I/O, not internal flags) | _none_ | **gap** |
| T8.3 | Match table structure to logic type (decision/parsing/transformation) | _none_ | **gap** |

### Category T9: Table Design — Depth of Scenarios

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T9.1 | Boundary conditions at thresholds | `1.3-depth-overtime-boundary` in 14, `2.3-depth-tier-boundaries` + `2.4-depth-rolling-window-boundary` in 15 — **reasonable** |
| T9.2 | Combined/interaction scenarios | `1.4-depth-combined-scenario` in 14 — **weak** |
| T9.3 | Error/edge cases covered | `1.5-depth-error-edge-cases` in 14, `exception-cases-handled` in 8 — **reasonable** |
| T9.4 | All core rule branches covered | `four-core-rules-covered` in 9, `2.19-depth-all-tiers` in 15 — **reasonable** |

### Category T10: Pre-Check & Dependencies

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T10.1 | Check for TableTest dependency in pom/gradle | `provides-correct-groupid` + `provides-artifactid` + `includes-test-scope` | 3 — **well covered** (dedicated eval) |
| T10.2 | Flag JUnit version < 5.11 | `flags-junit-version` | 3 — **covered** |
| T10.3 | Assess test shape (when to use @Test vs @TableTest) | _none_ | **gap** (hard to test — requires prompts where @Test is better) |

### Category T11: Workflow & Process

| # | Testable Aspect | Current Coverage | Evals |
|---|----------------|-----------------|-------|
| T11.1 | Design phase before coding (mockup with 2-3 rows) | _none_ | **gap** (process, not output-testable) |
| T11.2 | Refinement after tests pass | _none_ | **gap** (process, not output-testable) |
| T11.3 | Column consolidation check | _none_ | **gap** |
| T11.4 | Cross-table consistency | _none_ | **gap** |

---

## Coverage Heat Map (TableTest)

| Category | Coverage Level | Notes |
|----------|---------------|-------|
| T1. Annotations & Method Shape | **Strong** | Well covered by structural assertions across 5+ evals |
| T2. Syntax & Quoting | **Partial** | Null-as-blank tested; collection syntax (list, map) now in 25-28; empty strings, quoting, set syntax still untested |
| T3. Type Conversion | **Strong** | TypeConverter now well covered across 2,15,25-28; built-in conversion limits undertested |
| T4. Exception Handling | **Reasonable** | Throws? column and exception coverage in 2-3 evals |
| T5. Column Structure | **Strong** | Scenario and ? columns well covered (1,7-9,25-28); traceability and completeness weak |
| T6. Values & Readability | **Moderate** | Domain terminology and scenario names now covered in 25-28; output traceability and blank cells still weak |
| T7. Value Sets | **Strong** | Well covered across 7,9,15,25-28 |
| T8. Decomposition | **Partial** | Now in 15,25-28 but black-box and logic-type matching still untested |
| T9. Depth of Scenarios | **Reasonable** | Boundaries and core branches covered in 14,15 |
| T10. Pre-Check | **Strong** | Dedicated eval 3 covers this well |
| T11. Workflow & Process | **Gap** | Not output-testable in current eval format |

## Priority Gaps (TableTest)

1. **Syntax & quoting** (T2) — Empty strings, quoting rules, set syntax still have zero assertions. List and map syntax now covered by evals 25-28.
2. **Black-box design** (T8.2) — Core skill principle with no assertion. An eval could present internal flags vs observable I/O and check the model avoids internal details.
3. **Traceability columns** (T5.4) — Distinctive skill instruction, only tested in eval 14.
4. **All outputs in one table** (T5.5) — Skill section with zero coverage.
5. **Output traceability** (T6.4) and **blank cells** (T6.5) — Only tested in eval 14.

---
---

# Cross-Skill Observations

## Shared Categories (both skills teach these)

Several categories appear in both skills with similar or identical instructions. Assertions for these are currently split across skill-specific evals with no cross-referencing:

| Shared Aspect | Spec-by-Example Coverage | TableTest Coverage |
|--------------|-------------------------|-------------------|
| Scenario names as conditions | Moderate (3 evals) | Strong (14,25-28) |
| Value sets for "regardless of" | Weak-Reasonable | Strong (7,9,15,25-28) |
| Business/domain language | Partial | Moderate (25-28) |
| One table per concern | Moderate | Reasonable (15,25-28) |
| Concrete domain values | Gap | Reasonable (25-28) |
| Blank cells vs value sets | Gap | Weak |
| Output traceability | Weak | Weak |
| Boundary/threshold coverage | Weak | Reasonable |

## Testability Notes

Some skill instructions are **process-oriented** (design phase, refinement, pair programming) rather than **output-oriented**. These are not directly testable via the current eval framework which examines final output. Categories T11 (Workflow) and parts of the elicitation workflow (steps 1-3, 8) fall into this bucket.

**Recommendation**: Focus coverage improvements on output-testable aspects first. Process aspects could be tested indirectly by crafting prompts where skipping the process leads to worse output (e.g., a prompt where value sets are only obvious after systematic review).
