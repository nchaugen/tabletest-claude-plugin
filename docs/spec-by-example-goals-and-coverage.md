# Spec-by-Example: Skill Goals & Eval Coverage

## Purpose

The spec-by-example skill helps clarify requirements before implementation by working
through concrete examples organised in tables. The tables serve as a shared specification
— readable and verifiable by domain experts and developers alike — and a natural starting
point for TableTests when coding begins.

---

## Skill Goals

The goals are organised in three layers:

### Layer A: What the tables capture

1. **Clarify known rules** — Surface and pin down decision rules, validation rules,
   state transitions, thresholds, rule interactions, and default/fallback behavior
   through concrete examples.

2. **Surface unknown rules** — Explicitly identify ambiguities, unresolved decisions,
   and conflicting cases rather than silently resolving them.

3. **Specify precisely** — Distinguish what matters from what doesn't (value sets for
   irrelevant inputs), where boundaries are (threshold rows), and what's absent vs
   irrelevant (blank cell semantics).

### Layer B: How the tables are organised and expressed

4. **Separate concerns** — One table per decision or rule. Rules separate from arithmetic.

5. **Communicate across audiences** — Business language in columns and values, traceable
   outputs, scenario names that describe conditions.

### Layer C: Where the tables go next

6. **Produce TableTest-ready structure** — `?` output columns, scenario column, row
   independence, `{...}` value set notation.

---

## Goal → Category → Coverage Mapping

### Goal 1: Clarify known rules

The skill should guide the agent to identify and express all types of domain rules
through concrete examples.

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Depth of examples** | Different outcomes, boundary conditions, special cases, missing inputs | **Moderate** — spread across evals 4,5,6,10,16,17 but thin per-aspect | Boundary conditions only in 1 eval (10) |
| **Boundaries & thresholds** | Threshold values visible as concrete numbers; boundary rows at/above/below | **Weak** — `threshold-values-visible` in eval 4 only; `24h-boundary-near-boundary` in eval 10 only | **Thresholds as explicit columns** entirely untested |
| **Stateful features** | Each row independent; state as before/action/after; value sets for multi-state rules | **Weak** — only eval 17 (with failures) and eval 5 | Before/after framing fails in eval 17 |

**Rule sources currently tested by evals:**

| Rule Source | Eval(s) | Notes |
|------------|---------|-------|
| Decision/eligibility rules | 4 (loan), 13 (shipping) | Well represented |
| State transitions | 5 (order status) | Single eval |
| Rule interactions/conflicts | 6 (discount stacking), 12 (loyalty+trial) | Good — 2 evals testing interaction ambiguity |
| Validation rules | _none_ | **Gap** — no eval presents validation logic |
| Default/fallback behavior | 4 (missing income) | Single scenario in 1 eval |
| Temporal/window rules | 10 (24h boundary, 30-day return) | Single eval |

### Goal 2: Surface unknown rules

The skill should guide the agent to identify and mark ambiguities rather than
silently resolving them.

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Open questions & ambiguity** | Ambiguities surfaced not resolved; open cells marked | **Strong** — evals 6, 12, 17 test this well | Open cell marking (`?` or notes) only in eval 4 |

This is one of the best-covered goals. The main gap is testing the _format_ of open
questions (marked cells vs prose notes vs separate section).

### Goal 3: Specify precisely

The skill should guide the agent to express specifications precisely — what matters,
what doesn't, what's absent.

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Value sets & irrelevant inputs** | `{...}` notation; value sets preferred over duplicate rows | **Weak** — eval 13 tests this but fails; eval 5 partial | Value set preference over duplicate rows: 1 eval, fails |
| **Blank cell semantics** | Blanks for absent values; value sets for irrelevant; no filler like "N/A" | **Gap** — zero assertions | **Entirely untested** — blank vs value set distinction is central to precision |
| **Boundaries & thresholds** | Threshold as explicit column (not buried in output) | **Gap** — no assertion | Threshold-as-column untested |

**This is the weakest goal in terms of eval coverage.** The blank-vs-value-set
distinction and threshold-as-column guidance are distinctive to this skill and have
zero coverage.

### Goal 4: Separate concerns

The skill should guide the agent to decompose complex features into focused tables.

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Decomposition** | Multiple tables when needed; each table has distinct concern; rules separate from arithmetic | **Moderate** — evals 10, 12, 16, 17 test splitting | Rules-vs-arithmetic separation: **zero assertions** |
| **Rules vs arithmetic** | Tables focus on decisions; arithmetic gets minimal rows | **Gap** — no assertion tests this | Dedicated skill section (step 6) with no coverage |

### Goal 5: Communicate across audiences

The skill should produce tables that domain experts can read and challenge without
seeing code.

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Business language** | Domain terms in headers; concrete domain values in cells | **Partial** — `business-language-columns` in evals 4, 10, 17 | **Concrete domain values** (vs abstract codes/booleans) never directly asserted |
| **Scenario naming** | Scenario names describe conditions, not outcomes | **Reasonable** — evals 4, 16, 17 | — |
| **Output traceability** | Outputs derivable from inputs; test data visible in table | **Weak** — only eval 17 | Only 1 eval covers this |

### Goal 6: Produce TableTest-ready structure

The skill should produce tables that translate directly to `@TableTest` code.

| Category | Testable Aspects | Coverage | Gaps |
|----------|-----------------|----------|------|
| **Table structure** | Markdown table; `?` on output columns; `?` only on outputs | **Strong** — well covered across 6+ evals | Scenario column presence: **no assertion** |
| **Value set notation** | `{...}` syntax used (maps to TableTest value sets) | **Reasonable** — evals 5, 13 | — |
| **Row independence** | Each row independently verifiable; no sequential dependencies | **Weak** — eval 17 only | Only 1 eval, and it partially fails |

---

## Gap Summary by Priority

Prioritised by: how many goals the gap blocks, how distinctive the aspect is to
this skill, and how feasible it is to test.

| Priority | Gap | Goals Blocked | Current State | What's Needed |
|----------|-----|--------------|---------------|---------------|
| **P1** | Blank cell vs value set semantics | 3 (Precision), 6 (TableTest-ready) | Zero assertions | Eval with optional inputs where some are absent and others irrelevant — assert correct use of blanks vs `{...}` |
| **P2** | Rules vs arithmetic separation | 4 (Separate concerns) | Zero assertions | Eval with a feature involving both decision rules and calculation — assert tables focus on rules, arithmetic minimal |
| **P3** | Threshold as explicit column | 1 (Clarify rules), 3 (Precision) | Zero assertions | Eval with policy thresholds — assert threshold appears as column, not buried in output values |
| **P4** | Value set preference over duplicate rows | 3 (Precision), 6 (TableTest-ready) | 1 eval, fails | Either improve skill guidance or add evals that make consolidation more obvious |
| **P5** | Concrete domain values (not abstract codes) | 5 (Communicate) | Implied only | Add assertion to existing evals checking cells use domain values not booleans/codes |
| **P6** | Row independence | 6 (TableTest-ready) | 1 eval, partial fail | Add stateful-domain eval (e.g. cart, workflow) — assert no row dependencies |
| **P7** | Output traceability | 5 (Communicate) | 1 eval | Add assertion to existing evals checking outputs are derivable from inputs |
| **P8** | Scenario column presence | 6 (TableTest-ready) | Zero assertions | Simple structural assertion, easy to add to existing evals |
| **P9** | Validation rules as rule source | 1 (Clarify rules) | No eval covers validation | New eval with input validation scenario |
| **P10** | Open question format (marked cells) | 2 (Surface unknowns) | 1 eval (eval 4) | Add to more evals that have ambiguous prompts |
