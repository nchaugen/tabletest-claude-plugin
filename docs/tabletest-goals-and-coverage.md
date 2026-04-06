# TableTest: Skill Goals & Eval Coverage

Last updated: 2026-04-06

## Purpose

The tabletest skill helps write and convert JUnit tests using the TableTest library.
It applies in multiple contexts:

- **From requirements** — natural-language feature descriptions with no existing code
- **From existing code** — production code that needs test coverage
- **From existing tests** — JUnit `@Test` methods, JUnit parameterized tests, Groovy
  Spock Framework tests, Kotest, TestNG, or other Java/Kotlin test frameworks to rewrite
  as TableTests

Regardless of starting point, the skill guides analysis of the domain logic (the same
analysis as spec-by-example), then expresses the results as valid `@TableTest` code
with readable data, clean method bodies, and documentation annotations.

---

## Skill Goals

### Layer A: Analysis & Design

These goals are shared with the spec-by-example skill. They are repeated here because
the agent cannot reliably trigger both skills together.

1. **Clarify known rules** — Identify and express decision rules, validation rules,
   state transitions, thresholds, rule interactions, and default/fallback behavior
   through concrete examples.

2. **Surface unknown rules** — Explicitly identify ambiguities, unresolved decisions,
   and conflicting cases rather than silently resolving them.

3. **Specify precisely** — Distinguish what matters from what doesn't (value sets for
   irrelevant inputs), where boundaries are (threshold rows), and what's absent vs
   irrelevant (blank cells vs value sets).

4. **Separate concerns** — One table per decision or rule. Rules separate from arithmetic.

### Layer B: Table Design & Expression

5. **Design tables at the right abstraction level** — Tables model observable inputs
   and outputs (black-box), not internal flags or implementation details. Exceptions
   are data columns (`Throws?`), not hardcoded in method bodies. Intermediate results
   appear as traceability columns. This is especially important when the starting
   point is existing code, where it's easy to mirror internal structure rather than
   test observable behavior.

6. **Express data readably and concisely** — Cell values use concrete domain values,
   not abstract codes or booleans. `@TypeConverter` methods bridge readable table
   representations to test parameter types. Tables should be concise enough to scan
   without horizontal scrolling. Column names use domain terminology.

7. **Use correct TableTest syntax** — Blank cells for null, `''` for empty strings,
   quoting for special characters, `[]` for lists, `{}` for sets, `[:]` for empty
   maps, `{...}` value set notation for "regardless of" relationships. Value sets
   only where all values produce the same result.

### Layer C: Test Code Quality

8. **Keep method bodies clean** — No if/switch statements, no inline parsing or
   conversion logic. Type conversion handled by `@TypeConverter` methods, not in
   the test method. Single uniform assertion logic across all rows.

9. **Document through annotations** — `@DisplayName` as section header for reports,
   `@Description` for context the table alone cannot convey (fixed values, domain
   context, open questions, table relationships). Scenario names describe conditions,
   not outcomes. Together these make tests function as living documentation,
   consumable by tools like tabletest-reporter.

10. **Follow annotation conventions** — Order: `@DisplayName` → `@Description` →
    `@TableTest`. `@Description` uses text blocks (`"""`). Omit `@Description` when
    it would merely restate the table.

### Layer D: Project Setup

11. **Manage dependencies correctly** — Verify `org.tabletest:tabletest-junit` is
    present with correct coordinates. Flag JUnit versions below 5.11.

12. **Assess test shape** — Recognise when `@Test` is more appropriate than
    `@TableTest` (trivial implementation, complex test-specific setup, redundant
    with integration tests).

---

## Eval Inventory

| Eval | Domain | Starting Point | Key Assertions | Iter 27 |
|------|--------|---------------|----------------|---------|
| 1 — convert-repetitive-tests | Generic | Existing `@Test` methods | format, scenario naming, setup | 100% |
| 2 — parse-dates | Date parsing | Requirements | null/blank, type conversion, empty string | 100% |
| 3 — dependency-setup | Build config | Project setup | groupId, artifactId, scope, JUnit version | 100% |
| 7 — permission-check | Permissions | Requirements | value sets, row count, format | 100% |
| 8 — money-parse | Currency parsing | Requirements | null/blank, exceptions, format | 100% |
| 9 — bonus-contractor-structure | Payroll rules | Requirements | value sets, rule coverage, format | 100% |
| 14 — weekly-pay | Payroll calc | Requirements | traceability, boundaries, decomposition, format | 79% |
| 15 — reis-discount | Discount tiers | Requirements | decomposition, boundaries, type converter, format | 75% |
| 18 — convert-from-code | Insurance | Existing code | black-box, observable I/O, decomposition | 86% |
| 19 — convert-from-parameterized | Generic | `@ParameterizedTest` | format, scenario naming | 100% |
| 20 — collections-and-quoting | Collections | Requirements | list syntax, empty list, special chars, format | 90% |
| 22 — event-registration-tt | Event registration | Requirements | validation, blank/value-set, decomposition, optional fields | 80% |
| 23 — loan-approval-tt | Loan approval | Requirements | thresholds, decomposition, domain values | 64% |
| 25 — convert-from-spock | Insurance | Spock framework | map/list syntax, type converter, decomposition | 67% |
| 26 — convert-from-kotest | Insurance | Kotest framework | map/list syntax, type converter, no Kotest syntax | 80% |
| 27 — convert-from-testng | Insurance | TestNG framework | map/list syntax, decomposition, no TestNG artifacts | 93% |
| 28 — convert-from-methodsource | Insurance | `@MethodSource` | map/list syntax, decomposition, no MethodSource artifacts | 87% |

---

## Goal → Coverage Mapping

### Goal 1: Clarify known rules

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Depth of scenarios** | Boundary conditions, combined scenarios, error cases, all rule branches | **Strong** — evals 8, 9, 14, 15, 22, 23 cover boundaries, errors, branches | Combined/interaction scenarios: evals 14, 15 |
| **Stateful features** | Row independence, state as before/action/after | Not covered | **Gap** — no tabletest eval involves stateful domain |

**Starting-point coverage:**

| Starting Point | Eval(s) | Notes |
|---------------|---------|-------|
| From requirements | 7, 8, 9, 14, 15, 22, 23 | Well represented (7 evals) |
| From existing tests | 1, 19 | Two evals — `@Test` and `@ParameterizedTest` |
| From existing code | 18 | Single eval; 86% pass rate |
| From other frameworks | 25 (Spock), 26 (Kotest), 27 (TestNG), 28 (MethodSource) | **Strong** — 4 evals, 67-93% pass rates |

### Goal 2: Surface unknown rules

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Open questions** | `@Description` surfacing open questions | **Partial** — `description-if-present-adds-information` in 10+ evals; eval 22 has `validation-includes-optional-fields` | No assertion specifically checks for open-question identification |

This is primarily a spec-by-example goal but applies when tabletest starts from
requirements. Current tabletest evals don't present deeply ambiguous requirements.

### Goal 3: Specify precisely

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Value sets** | `{...}` for irrelevant inputs; correct semantics; row reduction | **Strong** — evals 7, 9, 15, 25, 26, 27, 28 | `uses-value-sets` in 6 evals |
| **Blank cells** | Blank for null/absent; not 0 or defaults for optional | **Reasonable** — `null-as-blank-cell` in 2, 8; `1.6-readability-empty-cells` in 14 (fails); `blank-for-absent-optional` in 22 | 14's empty-cells assertion unreliable |
| **Boundaries** | Threshold values visible; boundary rows | **Reasonable** — `1.3-depth-overtime-boundary` in 14; `2.3-depth-tier-boundaries`, `2.4-depth-rolling-window-boundary` in 15; `threshold-as-column` in 23 | Eval 23 threshold assertion fails |

### Goal 4: Separate concerns

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Decomposition** | Separate tables per concern | **Partial (breadth Strong, reliability Partial)** — `concerns-decomposed` in evals 14, 18, 22, 23, 25, 27, 28; dedicated assertions in 14, 15, 18, 22, 23 | Fails in 5 of 7 evals with `concerns-decomposed` (14, 18, 23, 25, 27, 28) |
| **Rules vs arithmetic** | Tables focus on rules, arithmetic minimal | `separates-classification-and-calculation` in 14 (fails) | **Weak** — single assertion, unreliable |
| **Logic type matching** | Decision/parsing/transformation structured differently | _none_ | **Gap** |

### Goal 5: Design tables at the right abstraction level

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Black-box design** | Observable I/O, not internal flags | **Reasonable** — `black-box-columns`, `observable-io-only` in eval 18 (pass) | Single eval but both assertions pass reliably |
| **Exception columns** | `Throws?` column, not hardcoded exceptions | **Reasonable** — `exception-has-expected-column` in 8; `1.2-error-has-expected-column` in 14; `exception-cases-handled` in 8 | — |
| **Traceability columns** | Intermediate results visible | **Weak** — `1.1-traceability-columns` in 14 (fails) | Only 1 eval, unreliable |
| **Complete outputs** | All outputs of same concern in one table | _none_ | **Gap** |

### Goal 6: Express data readably and concisely

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Concrete domain values** | Not abstract codes or booleans | **Reasonable** — `concrete-domain-values` in 23; `2.6-readability-human-readable-values` in 15 (fails) | Eval 15 assertion unreliable |
| **Domain terminology** | Column names use domain terms | **Reasonable** — `business-language-columns` in evals 18, 22, 23, 25, 26, 27, 28 | Eval 25 fails this assertion |
| **Output traceability** | Expected values derivable from inputs | **Weak** — `1.8-correctness-expected-values` in 14 | Only 1 eval |
| **TypeConverter for readability** | Readable table values via `@TypeConverter` | **Reasonable** — `type-conversion-addressed` in 2; `2.12-format-typeconverter` in 15 (fails); `options-type-converter` in 25, 26, 27, 28 | Evals 25, 26 fail type-converter assertion |
| **Table width/conciseness** | Avoiding overly wide tables | _none_ | **Gap** |

### Goal 7: Use correct TableTest syntax

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Has @TableTest** | Annotation present | **Strong** — all 17 evals | — |
| **Null as blank** | Blank cells for null | **Reasonable** — evals 2, 8 | — |
| **Empty string** | `''` for empty strings | **Weak** — `empty-string-uses-quotes` in eval 2 | Single eval |
| **Quoting** | Quotes for pipes, brackets | **Weak** — `special-chars-quoted` in eval 20 (fails) | Single eval, unreliable |
| **List syntax** | `[]` for lists | **Reasonable** — `list-syntax-correct` in 20; `dimensions-as-list` in 25, 26, 27, 28 | — |
| **Map syntax** | Map representation | **Partial** — `options-as-map` in 25, 26, 27, 28 | Fails in 25, 26, 28 |
| **Set syntax `{}`** | Sets distinct from value sets | _none_ | **Gap** |
| **Newline `\n`** | Escaped newlines in cells | _none_ | **Gap** |
| **Value set syntax** | `{...}` notation | **Strong** — evals 7, 9, 15, 25, 26, 27, 28 | — |
| **Scenario column** | Leftmost, not mapped unless `@Scenario` | **Strong** — all 14 conversion evals | — |
| **`?` suffix** | On output columns | **Strong** — all 14 conversion evals | — |

### Goal 8: Keep method bodies clean

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **No if/switch** | Clean method body | **Strong** — all 14 conversion evals | — |
| **TypeConverter extracts logic** | Conversion in `@TypeConverter`, not inline | **Reasonable** — evals 2, 15, 25, 26, 27, 28 | Evals 25, 26 fail |
| **Uniform assertions** | Single assertion pattern across rows | **Weak** — `single-assertion-in-method` in eval 1 | Only 1 eval |

### Goal 9: Document through annotations

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **@DisplayName** | Descriptive section header | **Strong** — 14+ evals | — |
| **@Description quality** | Adds context beyond table, not restatement | **Strong** — 10+ evals with `description-if-present-adds-information`; eval 22 has `description-no-irrelevant-information` (fails) | — |
| **Scenario naming** | Conditions, not outcomes | **Reasonable** — `scenario-names-describe-conditions` in evals 9, 18, 22, 23, 25, 26, 27, 28 | Eval 23 fails this assertion |

### Goal 10: Follow annotation conventions

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Annotation order** | `@DisplayName` → `@Description` → `@TableTest` | **Strong** — 14+ evals | Eval 8 also has `annotation-order-strict` |
| **Text block** | `@Description` uses `"""` | **Strong** — 14+ evals | — |

### Goal 11: Manage dependencies correctly

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Dependency coordinates** | Correct groupId and artifactId | **Strong** — eval 3 (100%) | — |
| **JUnit version** | Flag < 5.11 | **Strong** — eval 3 (100%) | — |

### Goal 12: Assess test shape

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **When to use @Test** | Trivial impl, complex setup, redundant coverage | _none_ | **Gap** — needs eval where `@Test` is the better answer |

---

## Gap Summary by Priority

| Priority | Gap | Goals | Current State | What's Needed |
|----------|-----|-------|---------------|---------------|
| **P1** | Concern decomposition reliability | 4 | Present in 7 evals but fails in 5 (14, 18, 23, 25, 27, 28) | Skill improvement — breadth is sufficient, pass rate is not |
| **P2** | Rules vs arithmetic separation | 4 | 1 assertion (`separates-classification-and-calculation` in 14), fails | Skill improvement + add to more evals |
| **P3** | Map syntax (`options-as-map`) | 7 | 4 evals (25-28), fails in 3 (25, 26, 28) | Skill improvement — assertions exist, reliability needed |
| **P4** | Special-char quoting | 7 | 1 eval (20), fails | Skill improvement or eval adjustment |
| **P5** | Table width/conciseness | 6 | Zero assertions | New assertion in existing evals |
| **P6** | Set syntax `{}`, newline `\n` | 7 | Zero assertions each | New eval or extend eval 20 |
| **P7** | All outputs of same concern in one table | 4, 5 | Zero assertions | Add assertion to multi-output evals |
| **P8** | Traceability columns | 5, 6 | 1 eval (14), fails | Skill improvement + add to more evals |
| **P9** | Stateful domain | 1 | No eval | New eval with stateful domain |
| **P10** | Assess test shape (`@Test` vs `@TableTest`) | 12 | No eval | Eval where `@Test` is correct answer — hard to design |
| **P11** | Uniform assertions | 8 | 1 eval | Add assertion to more evals |

## Closed Gaps (since last update)

| Gap | Status | How Closed |
|-----|--------|------------|
| Starting from existing code | **Covered** — eval 18 (86%) | Eval 18 — convert-from-code |
| Starting from parameterized/Spock tests | **Strong** — evals 19, 25, 26, 27, 28 | Five evals covering `@ParameterizedTest`, Spock, Kotest, TestNG, `@MethodSource` |
| Black-box design | **Reasonable** — eval 18 | `black-box-columns` and `observable-io-only` assertions pass |
| Domain terminology in columns | **Reasonable** — `business-language-columns` in 7 evals | Previously zero assertions |
| Empty string syntax | **Weak** — eval 2 | `empty-string-uses-quotes` assertion (was listed as Gap) |
| Collection syntax (lists) | **Reasonable** — evals 20, 25-28 | `list-syntax-correct`, `dimensions-as-list` |
| Scenario naming | **Reasonable** — 8 evals | Previously only eval 14; now evals 9, 18, 22, 23, 25-28 |
| Boundary testing | **Reasonable** — evals 14, 15, 23 | Previously single eval |
| Concrete domain values | **Reasonable** — evals 15, 23 | Previously no direct assertion |
