# TableTest as an Agentic Spec Format — Experiment Design

**Date:** 2026-03-22
**Status:** Draft — hypotheses and experiments only, no implementation planning

## Context

As AI agents take on more implementation work, developers face a shifting verification problem: reviewing agent-generated code becomes time-consuming and may not be necessary if the agent produces quality code. But developers (and their stakeholders) still need to verify that the system has the desired behaviour.

TableTest already has the pieces of a solution:
- `/spec-by-example` produces readable example tables in business language
- `@TableTest` makes those tables executable as JUnit tests
- `tabletest-reporter` publishes test results as readable documentation

The idea is to tighten this pipeline into a deliberate workflow for agentic development: agents capture specifications as tables of examples, those tables become runnable tests, and the published output serves as living documentation that humans review instead of code.

## Key Insight

BDD/Cucumber attempted something similar but largely failed — not because the format was wrong, but because human developers consistently drifted toward technical language when writing specs. A Cucumber scenario written by a developer might read "Given a valid JSON payload with status 200" rather than "Given a returning customer with a loyalty discount." The hypothesis here is that **agents instructed to write in business language can do so consistently**, solving the adoption problem that defeated BDD in human-only teams.

## The Hard Problem: Table Design

The central difficulty — for both humans and agents — is table design:
- What belongs in a table and what doesn't
- When to use one table vs multiple tables for different aspects
- How to represent complex values concisely while remaining readable
- Choosing the right level of abstraction (business language vs technical detail)

The `/spec-by-example` skill encodes guidance for exactly these decisions. A key question is whether that guidance is sufficient for agents to produce well-structured tables that serve as readable specs.

## Hypotheses

Four hypotheses in dependency order. Each gates whether the next is worth testing.

### H0: The spec-test identity holds

A single table can simultaneously be a readable behaviour specification and a well-structured `@TableTest`. These two goals do not create irreconcilable tension in column naming, level of abstraction, row design, or table structure.

**Kill criterion:** If readable tables consistently make bad tests (too vague, wrong granularity) or good tests consistently make unreadable specs (too technical, implementation-leaky), the core idea does not work.

### H1: Agents produce good spec-tables from requirements

Given a natural-language requirement, an agent can produce a `@TableTest` that scores well on both spec-readability and test-quality, including making good structural decisions (table boundaries, column selection, value representation).

**Kill criterion:** If agents consistently produce tables that are good tests but poor specs (technical language, poor structure, missing edge cases a stakeholder would care about), the "agent as BDD bridge" hypothesis fails.

### H1b: Agent-produced specs are faithful to agent-produced code

When an agent produces both implementation code and spec-tables for the same feature, the specs accurately describe what the code does. The spec is not an idealised description of requirements that diverges from the actual implementation.

**Kill criterion:** If the spec-tables describe behaviour the code does not implement (or omit behaviour the code does implement), the specs are unreliable as a verification mechanism and H2 cannot hold.

*Note: This is tested as part of Experiment 2, not as a separate experiment.*

### H2: Developers use spec-tables to verify agent output

When presented with agent-generated code alongside published spec-tables (via reporter), developers spend less time reading code and more time reviewing the spec — and catch behavioural issues they would have missed from code review alone.

**Kill criterion:** If developers ignore the spec and go straight to code, or if the spec does not surface issues that code review misses, the value proposition does not hold.

### H3: Business stakeholders can read agent-produced specs

The published spec-tables are comprehensible to product/business people without developer translation. They can identify when a table does not match their understanding of the requirement.

**Kill criterion:** If stakeholders cannot read the tables without explanation, or cannot spot errors, the stretch goal fails (but H0–H2 may still hold).

## Experiments

### Experiment 0: Spec-Test Identity (tests H0)

**Method:** Manual analysis — no tooling needed

**Setup:** Select 3–5 real-world features with varying complexity:
- A simple calculation or validation (e.g., discount eligibility)
- A multi-condition decision (e.g., shipping cost by region + weight + membership)
- A stateful workflow (e.g., order lifecycle transitions)

**Protocol:**
1. For each feature, write the **ideal spec table** — optimised purely for readability by a non-developer. Use business language, descriptive scenario names, no technical detail.
2. For each feature, write the **ideal test table** — optimised purely for test quality. Good coverage, precise assertions, appropriate granularity.
3. Compare the two along three dimensions:

**Dimension A — Content convergence:**
- Where do the tables agree? (columns, rows, naming, level of abstraction)
- Where do they diverge? (too technical for spec, too vague for test, different granularity)
- Can the divergences be resolved without compromising either goal?

**Dimension B — Table structure:**
- Do both versions split into the same number of tables?
- Are the table boundaries (what goes in which table) the same?
- Where structure diverges, which version made the better structural choice?

**Dimension C — Value representation:**
- Do complex values (dates, amounts, statuses, collections) take the same form?
- Where they differ, is the spec version readable but imprecise, or the test version precise but opaque?
- Can value representation serve both needs (e.g., through `@TypeConverter` that accepts business-friendly formats)?

**Dimension D — Sequential/temporal readability (stateful features only):**
- Can a reader reconstruct the workflow from the table rows alone?
- Are preconditions and state transitions clear without implementation knowledge?
- Does the table structure suit the sequential nature, or does a flat table obscure ordering?

**Grading:**
1. For each feature, attempt to produce a **merged table** that serves both spec and test goals. Do not just claim a merge is feasible — actually write it.
2. Score each merged table on spec-readability (1–5) and test-quality (1–5).
3. Convergence score: percentage of rows/columns from the original spec and test tables that appear in the merged version without compromise.
4. Structural alignment: do both original versions use the same table boundaries?
5. Tension catalogue: list of specific tensions found, with concrete resolutions demonstrated in the merged table.
6. Overall verdict: pass if merged tables score ≥ 4 on both readability and test quality for ≥ 3/5 features, and remaining tensions have demonstrated (not just claimed) resolutions.

### Experiment 1: Agent Table Generation (tests H1)

**Method:** Eval-style runs, graded against a dual rubric

**Setup:** Use the same 3–5 features from Experiment 0. For each, write a natural-language requirement as a product person would write in a ticket.

**Protocol:**
1. Give an agent the requirement and the instruction: *"Write one or more `@TableTest` methods that serve as both executable tests and readable behaviour specifications. Use business language in scenario names and column headers. Include `@DisplayName` on test methods (used as section headers in reports) and `@Description` on test classes (used as explanatory text in reports)."*
2. Run three variants:
   - **With spec-by-example + tabletest skills** (full guidance)
   - **With tabletest skill only** (knows the format but not the spec design process)
   - **Without any skill** (baseline)
3. Grade each output against three rubrics:

**Rubric A — Spec readability (1–5):**
- Business language throughout? Scenario names describe conditions, not outcomes?
- Would a product person understand each row without developer translation?
- Complete coverage of the stated requirements? Open questions surfaced where appropriate?

**Rubric B — Test quality (1–5):**
- Good coverage? Precise assertions? Appropriate granularity?
- Compiles and runs? No implementation leakage in the table?
- Well-structured test method (no parsing, no conditional logic)?

**Rubric C — Table structure (1–5):**
- Appropriate table boundaries? One concern per table?
- Right columns included, irrelevant columns excluded?
- Complex values represented concisely and readably?
- Value sets used where appropriate for "regardless of" conditions?

**Part 2 — Iterative refinement:**

The single-shot run tests agent capability in isolation. But the real workflow is iterative: human and agent pair to refine the spec through conversation. This part tests whether the table format supports efficient iteration.

4. Take the best single-shot output (from part 1) for each feature. Give the agent 2–3 rounds of realistic feedback:
   - Round 1: A missing edge case ("What happens when the customer has both a loyalty discount and a promotional code?")
   - Round 2: A readability concern ("The 'Discount Factor' column is confusing — can you use business terms?")
   - Round 3: A structural suggestion ("This table mixes pricing and eligibility concerns — should those be separate?")
5. Grade the final iterated output against the same three rubrics (A, B, C).
6. Record: How many rounds did it take to reach a good table? Did the agent understand table-level feedback (e.g., "split this table") or did it need code-level instructions?

**Grading:**
- **Single-shot scores:** For each feature, compute the per-feature minimum of the three rubric scores (A, B, C). This penalises imbalance — a table that is a great test but a poor spec scores low.
- **Iterated scores:** Same per-feature minimum, computed on the final iterated output.
- **Iteration delta:** How much did iteration improve scores? If single-shot scores are already high and iteration adds little, that's a positive signal (agents are good out of the box). If single-shot scores are low but iteration brings them up, that's also useful (the format supports refinement, but agents need guidance).
- **Iteration efficiency:** Number of rounds to reach a satisfactory result. Feedback comprehension — did the agent act on table-level feedback directly?
- Pass threshold: the average of per-feature minimums is ≥ 3.5 (single-shot or iterated), and no individual feature has any rubric score below 3.
- Informational comparison (not part of pass/fail): compare agent outputs against the merged tables from Experiment 0. How close does the agent get? Where does it diverge? This provides insight but does not gate the result, since the agent may find equally valid alternative structures.
- Compare the three variants — does the spec-by-example skill make a measurable difference in spec quality and table structure?

### Experiment 2: Developer Verification (tests H2)

**Method:** Real-project trial, qualitative

**Prerequisite:** H0 and H1 passed

**Setup:** Select a real project where agents are used for implementation. Choose 2–3 upcoming features.

**Protocol:**
1. For each feature, have the agent produce both:
   - The implementation code
   - `@TableTest` spec-tables (using the approach validated in Experiment 1)
2. Publish the spec-tables via tabletest-reporter
3. Review the agent's work using **only** the published spec-tables (not the code). Record:
   - What behavioural issues you caught from the spec alone
   - What you missed that you would have caught from code review
   - How long the spec review took vs a typical code review
4. Then do a full code review. Record anything the spec review missed.
5. **Faithfulness check (tests H1b):** For each feature, verify that the spec-tables accurately describe the code. Run the tests — do they pass? Read the spec alongside the code — does the spec omit implemented behaviour or describe behaviour the code does not implement? Record discrepancies.
6. **Deliberate defect (mandatory):** For at least one feature, introduce a behavioural defect in the code (e.g., wrong boundary condition). Does the spec review catch the mismatch?

**Time measurement:** Record focused time (actively reading/thinking) for both spec review and code review. Exclude time spent understanding the requirement itself, as that is constant across both approaches.

**Grading:**
- Faithfulness (H1b): percentage of features where spec accurately matches code behaviour
- Coverage: percentage of behavioural issues caught by spec review vs code review
- Efficiency: focused time on spec review vs code review
- Confidence: after spec review alone, how confident are you the code is correct? (1–5)
- Defect journal: log of what was caught where (spec only, code only, both, neither)
- Pass threshold: faithfulness ≥ 80%, spec review catches ≥70% of behavioural issues in ≤50% of the time, confidence ≥ 3

### Experiment 3: Stakeholder Readability (tests H3)

**Method:** Real-project trial, qualitative

**Prerequisite:** H1 passed (agents produce decent tables). Note: this experiment is gated on H1 only, not H2. Even if developers do not use specs for verification (H2 fails), stakeholder readability may be independently valuable for requirements sign-off before implementation.

**Setup:** Take 2–3 published spec-tables — from Experiment 2 if it ran, or generated specifically for this experiment if Experiment 2 was skipped.

**Protocol:**
1. Show the tables to 2–3 product or business people without explanation of the format
2. Ask: *"Does this match your understanding of how [feature] should work? Can you spot anything that looks wrong or missing?"*
3. Observe per stakeholder:
   - Can they read the table without asking about syntax?
   - Do their questions concern format or content?
   - Can they identify at least one correct behaviour and one incorrect/missing behaviour?
4. If they struggle, note specifically what confused them (notation? column names? level of detail? domain terms?)

**Grading:**
- Comprehension: could they read the table without format explanation? (yes/partial/no)
- Engagement: did they discuss content rather than format?
- Error detection: could they spot a deliberate or genuine issue?
- Pass threshold: at least 2 of 3 stakeholders identify at least one correct and one incorrect/missing behaviour without developer assistance
- *Note: with n=2–3, results are indicative rather than conclusive. If the signal is positive, plan a broader follow-up.*

## Experiment Execution Order

```
Experiment 0 (manual, ~1 day)
    │
    ├── FAIL → Stop. The spec-test identity doesn't hold.
    │          Investigate whether a thin adaptation layer could resolve tensions.
    │
    └── PASS → Experiment 1 (eval runs, ~2 days)
                    │
                    ├── FAIL → Stop. Agents can't produce good spec-tables.
                    │          Consider whether skill improvements could close the gap.
                    │
                    └── PASS ─┬─→ Experiment 2 (real project, ~1–2 weeks)
                              │       │
                              │       ├── FAIL → Developer verification doesn't hold.
                              │       │          But H3 may still be worth testing.
                              │       │
                              │       └── PASS → Core idea validated (H0–H2).
                              │
                              └──→ Experiment 3 (stakeholder trial, ~1 day)
                                        │   Gated on H1 only. Can run in parallel
                                        │   with or after Experiment 2.
                                        │
                                        ├── FAIL → Stakeholder use not viable yet.
                                        │
                                        └── PASS → Stretch goal validated (H3).

Full validation = H0 + H1 + H2 + H3.
Minimum viable validation = H0 + H1 + H2.
Proceed to implementation design after minimum viable validation.
```

## Workflow Coverage

Tables are relevant at different points in a development workflow. The experiments above cover the initial creation and review stages. Later stages become relevant only if the core hypotheses hold.

| Workflow step | Description | Covered by |
|---|---|---|
| 1. Iterative spec development | Agent + human pair to build a spec for a new feature | Exp 1 (part 2 — iterative refinement) |
| 2. Reviewing a spec | Human and/or agent review a completed spec for correctness and completeness | Exp 2 (spec-only review), Exp 3 (stakeholder review) |
| 3. Reviewing implementation before merge | Human verifies agent-generated code matches the spec | Exp 2 (spec review vs code review, faithfulness check) |
| 4. Understanding a feature | Agent or human uses published specs to familiarise themselves with existing behaviour | Exp 2/3 implicitly — not tested as a distinct task |
| 5. Specifying changes | Updating an existing spec-table when requirements change (by agent or human) | Not covered — future experiment |
| 6. Reviewing changes via table diff | Using table diffs to review what changed in a feature's behaviour | Not covered — future experiment |

### Future Experiments (gated on H0–H2 passing)

**Experiment 4: Spec Evolution (tests workflow steps 5–6)**

Can existing spec-tables be updated efficiently when requirements change? Are table diffs structurally meaningful — i.e., does a row added/removed/changed in a table clearly communicate a behaviour added/removed/changed?

This is a particularly interesting differentiator from prose specs: a diff of a well-structured table is inherently more meaningful than a diff of prose paragraphs, because each row represents a discrete behaviour.

*Design deferred until core hypotheses are validated.*

**Experiment 5: Feature Comprehension (tests workflow step 4)**

Can a developer or agent unfamiliar with a feature understand its behaviour from the published spec-tables alone, without reading the implementation? This tests the "living documentation" value independent of the verification workflow.

*Design deferred until core hypotheses are validated.*

## Bias Acknowledgement

The experimenter is also the tool author. This creates bias risk in all experiments:
- In Experiment 0, the "ideal" tables will naturally reflect the tool author's design sensibilities.
- In Experiment 1, grading will be done by someone who already knows how to read these tables.
- In Experiment 2, the developer reviewer already believes in the approach.

**Mitigations:**
- Pre-register grading criteria with concrete examples before running experiments (this document is a start).
- For Experiment 1, define anchor examples for each rubric score level (what does a "3" look like vs a "5"?) before grading.
- For Experiments 2–3, seek at least one additional reviewer/stakeholder who is not the tool author.
- Record raw observations before assigning scores. Score after the fact, not during.

## What This Document Does Not Cover

This document deliberately excludes:
- Implementation design (format options, annotation vs external files, tooling changes)
- Changes to the tabletest library, reporter, or plugin
- Workflow design for the agentic spec process

These are worth exploring only after the experiments validate the core hypotheses.

## Open Questions

1. **Feature selection for Experiments 0–1:** Which real-world features best stress-test the spec-test identity? Need a mix of complexity levels and domain types.
2. **Eval infrastructure:** Can the existing eval framework in `skills-workspace/` be reused for Experiment 1, or does the triple-rubric grading need a different structure?
3. **Deliberate defects for Experiment 2:** How to introduce realistic behavioural defects that a spec review might catch but are subtle enough to be a fair test?
4. **Rubric anchor examples:** Before running Experiment 1, define concrete examples of what a "2", "3", and "5" look like for each rubric (A, B, C). This prevents grading drift and makes the scores reproducible. Each anchor should be a short table excerpt with an explanation of why it receives that score.
5. **Stateful workflow treatment:** Experiment 0 includes a stateful workflow feature. If the flat table format proves inadequate for sequential behaviour, is that a failure of H0 or a scope limitation? Consider whether the conclusion should be "identity holds for decision/validation logic but not workflows" rather than a binary pass/fail.
