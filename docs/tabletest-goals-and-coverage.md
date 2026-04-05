# TableTest: Skill Goals & Eval Coverage

## Purpose

The tabletest skill helps write and convert JUnit tests using the TableTest library.
It applies in multiple contexts:

- **From requirements** — natural-language feature descriptions with no existing code
- **From existing code** — production code that needs test coverage
- **From existing tests** — JUnit `@Test` methods, JUnit parameterized tests, Groovy
  Spock Framework tests, or other Java/Kotlin test frameworks to rewrite as TableTests

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

## Goal → Category → Coverage Mapping

### Goal 1: Clarify known rules

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Depth of scenarios** (T9) | Boundary conditions, combined scenarios, error cases, all rule branches | **Reasonable** — evals 8,9,14,15 cover boundaries, errors, branches | Combined/interaction scenarios: only eval 14 |
| **Stateful features** | Row independence, state as before/action/after | Not covered in tabletest evals | **Gap** — no tabletest eval involves stateful domain |

**Starting-point coverage:**

| Starting Point | Eval(s) | Notes |
|---------------|---------|-------|
| From requirements | 7 (permissions), 8 (money parse), 9 (bonus), 14 (weekly pay), 15 (reis discount) | Well represented |
| From existing tests | 1 (convert repetitive tests) | Single eval |
| From existing code | _none_ | **Gap** — no eval presents production code to test |
| From parameterized/Spock tests | _none_ | **Gap** — no eval presents non-TableTest parameterized tests |

### Goal 2: Surface unknown rules

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Open questions** | `@Description` surfacing open questions | `description-if-present-adds-information` in 5+ evals — **partial** | No assertion specifically checks for open questions in tabletest output |

This is primarily a spec-by-example goal but applies when tabletest starts from
requirements. Current tabletest evals don't present ambiguous requirements.

### Goal 3: Specify precisely

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Value sets** (T7) | `{...}` for irrelevant inputs; correct semantics; row reduction | **Reasonable** — evals 7, 9, 14, 15 | — |
| **Blank cells** | Blank for null/absent; not 0 or defaults for optional inputs | `null-as-blank-cell` in 2, 8; `1.6-readability-empty-cells` in 14 — **partial** | **Blank vs value set distinction** not tested |
| **Boundaries** | Threshold values visible; boundary rows | `1.3-depth-overtime-boundary` in 14 — **weak** | Only 1 eval tests boundary precision |

### Goal 4: Separate concerns

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Decomposition** (T8) | Separate tables per concern; all outputs of same concern in one table | `2.1-decomposition-concern-separation` in eval 15 — **weak** (23% pass) | Only 1 eval; **all-outputs-in-one-table** untested |
| **Rules vs arithmetic** | Tables focus on rules, arithmetic minimal | _none_ | **Gap** — zero assertions |
| **Logic type matching** (T8.3) | Decision/parsing/transformation tables structured differently | _none_ | **Gap** |

### Goal 5: Design tables at the right abstraction level

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Black-box design** (T8.2) | Observable I/O, not internal flags | _none_ | **Gap** — zero assertions; critical when starting from code |
| **Exception columns** (T4) | `Throws?` column, not hardcoded exceptions | `exception-has-expected-column` in 8, 14 — **reasonable** | — |
| **Traceability columns** (T5.4) | Intermediate results visible | `1.1-traceability-columns` in 14 — **weak** | Only 1 eval |
| **Complete outputs** (T5.5) | All outputs of same concern in one table | _none_ | **Gap** |

### Goal 6: Express data readably and concisely

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Concrete domain values** (T6.1) | Not abstract codes or booleans | Implied only — **weak** | No direct assertion |
| **Domain terminology** (T6.2) | Column names use domain terms | _none_ | **Gap** |
| **Output traceability** (T6.4) | Expected values derivable from inputs | `1.8-correctness-expected-values` in 14 — **weak** | Only 1 eval |
| **TypeConverter for readability** (T3) | Readable table values via `@TypeConverter` | `type-conversion-addressed` in 2, `2.12` in 15 — **reasonable** | — |
| **Table width/conciseness** | Avoiding overly wide tables | _none_ | **Gap** — no assertion checks this |

### Goal 7: Use correct TableTest syntax

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Has @TableTest** (T1.1) | Annotation present | 5+ evals — **strong** | — |
| **Null as blank** (T2.1) | Blank cells for null | Evals 2, 8 — **reasonable** | — |
| **Empty string** (T2.2) | `''` for empty strings | _none_ | **Gap** |
| **Quoting** (T2.3) | Quotes for pipes, brackets | _none_ | **Gap** |
| **Collections** (T2.4) | `[]` for lists, `{}` for sets, `[:]` for empty map | _none_ | **Gap** |
| **Value set syntax** (T7.1) | `{...}` notation | Evals 7, 9, 15 — **reasonable** | — |
| **Scenario column** (T5.1) | Leftmost, not mapped unless `@Scenario` | Evals 1, 7, 9 — **reasonable** | — |
| **`?` suffix** (T5.2) | On output columns, suffix not prefix | Evals 1, 8 — **reasonable** | — |

### Goal 8: Keep method bodies clean

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **No if/switch** (T1.6) | Clean method body | Evals 1, 7, 8, 9, 14 — **well covered** | — |
| **TypeConverter extracts logic** (T3.2) | Conversion in `@TypeConverter`, not inline | Evals 2, 15 — **reasonable** | — |
| **Uniform assertions** (T1.9) | Single assertion pattern across rows | Eval 1 — **weak** | Only 1 eval |

### Goal 9: Document through annotations

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **@DisplayName** (T1.3) | Descriptive method name or annotation | 7+ evals — **well covered** | — |
| **@Description quality** (T1.4) | Adds context beyond table, not restatement | 7+ evals — **well covered** | — |
| **Scenario naming** (T6.3) | Conditions, not outcomes | Eval 14 — **weak** | Only 1 tabletest eval checks this |

### Goal 10: Follow annotation conventions

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Annotation order** (T1.2) | `@DisplayName` → `@Description` → `@TableTest` | 7+ evals — **well covered** | — |
| **Text block** (T1.5) | `@Description` uses `"""` | 7+ evals — **well covered** | — |

### Goal 11: Manage dependencies correctly

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Dependency coordinates** (T10.1) | Correct groupId and artifactId | Eval 3 — **well covered** | — |
| **JUnit version** (T10.2) | Flag < 5.11 | Eval 3 — **covered** | — |

### Goal 12: Assess test shape

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **When to use @Test** (T10.3) | Trivial impl, complex setup, redundant coverage | _none_ | **Gap** — hard to test; needs eval where @Test is better answer |

---

## Gap Summary by Priority

| Priority | Gap | Goals Blocked | Current State | What's Needed |
|----------|-----|--------------|---------------|---------------|
| **P1** | Black-box design | 5 (Abstraction level) | Zero assertions | Eval presenting internal code — assert table uses observable I/O not internal flags |
| **P2** | Rules vs arithmetic separation | 4 (Separate concerns) | Zero assertions | Eval with decision rules + calculation — assert tables focus on rules |
| **P3** | Blank vs value set semantics | 3 (Precision), 7 (Syntax) | No assertion distinguishing them | Eval with absent AND irrelevant inputs — assert correct blank vs `{...}` usage |
| **P4** | Table syntax: empty strings, quoting, collections | 7 (Syntax) | Zero assertions each | Eval requiring `''`, quoted values, collection syntax — structural assertions |
| **P5** | Starting from existing code | 1 (Clarify rules), 5 (Abstraction) | No eval | New eval with production code as input |
| **P6** | Domain terminology in columns | 6 (Readable data) | Zero assertions | Add `business-language-columns` equivalent to tabletest evals |
| **P7** | Traceability columns | 5 (Abstraction level), 6 (Readable data) | 1 eval | Add to more evals with multi-step logic |
| **P8** | All outputs of same concern in one table | 4 (Separate concerns), 5 (Abstraction) | Zero assertions | Eval with multiple outputs from one operation |
| **P9** | Starting from parameterized/Spock tests | — | No eval | New eval with Spock or `@ParameterizedTest` input |
| **P10** | Decomposition (multiple tables) | 4 (Separate concerns) | 1 eval at 23% | Diagnose: skill improvement or better eval design needed |
| **P11** | Scenario naming (conditions not outcomes) | 9 (Documentation) | 1 eval | Add assertion to more evals |
| **P12** | Assess test shape (@Test vs @TableTest) | 12 (Test shape) | No eval | Eval where @Test is the correct answer — hard to design |
