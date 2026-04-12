# Analysis: Agent Struggles in Iterations 26–28

## Context

Analyzed agent outputs across iterations 26, 27, and 28 (covering evals 1–30) to identify systematic areas where the agent struggles, spending extra tokens and time or producing incorrect results. The goal is to identify skill instruction improvements that would help the agent succeed more consistently.

---

## Top Struggle Areas (ordered by impact)

### 1. Concern Decomposition — Most Common Failure

**What happens:** The agent creates a single monolithic table mixing multiple behavioral concerns instead of splitting into separate `@TableTest` methods.

**Evidence:**
- Failed in iter-26 evals 25, 26, 27 (but succeeded in eval-28 with the same shipping domain)
- Failed in iter-27 evals 4, 6, 10, 13, 14, 15, 18, 23, 24, 25
- Failed in iter-28 evals 24, 25

**Root cause:** The skill says "don't split outputs of the same behavioral concern across separate tests" (SKILL.md:164–187) and the references say to split different concerns. But the agent struggles to *identify* what constitutes a separate concern. It often acknowledges separation in prose ("two distinct concerns") but doesn't act on it.

**What the skill is missing:** A concrete heuristic for when to split. The `requirements-to-tables.md` reference has good guidance ("if you can't name it without 'and', split") but this doesn't surface strongly enough. The agent needs a **mandatory decomposition step** before table design — not just advice to split when things feel wrong.

**Proposed fix location:** `skills/tabletest/SKILL.md` (Workflow > Design Phase) and `skills/tabletest/references/requirements-to-tables.md`

---

### 2. Map Columns + TypeConverter for Optional Properties

**What happens:** When a domain object has optional properties (e.g., PackageOptions with fragile, insuredValue, handling), the agent keeps them as separate columns with many blank cells instead of collapsing into a map column with a `@TypeConverter`.

**Evidence:**
- Failed `options-as-map` in iter-26 evals 25, 26, 27 (succeeded only in eval-28)
- Failed `options-type-converter` in iter-26 evals 25, 26, 27
- Failed in iter-27 evals 15, 25

**Root cause:** The `column-design.md` reference explains when to use maps vs separate columns, but the agent doesn't read it unless triggered. The main SKILL.md doesn't mention maps at all in the Table Design section — it only appears in the Collections syntax section as a data format, not as a design pattern.

**What the skill is missing:** A design heuristic in the main skill text: "When 3+ columns are mutually sparse (most rows leave them blank), collapse into a map column with a @TypeConverter." The reference exists but the signal to read it is too weak.

**Proposed fix location:** `skills/tabletest/SKILL.md` (Table Design section, near "Include All Outputs of a Concern") and `skills/tabletest/references/column-design.md` trigger condition in the references table.

---

### 3. Numeric Type Selection (double vs BigDecimal)

**What happens:** Agent uses `double` for monetary/weight values instead of `BigDecimal`.

**Evidence:**
- Failed `numeric-types-correct` in iter-26 eval-25, iter-28 eval-25
- Agent explicitly chose `double` with delta comparison, stating "to match original test's semantics"

**Root cause:** The `column-design.md` reference says to use BigDecimal for monetary/precise values, but the agent rationalizes using `double` when the source code uses it. There's no strong directive in the main skill text.

**What the skill is missing:** A clear rule in the quality checks: "Use BigDecimal for monetary values and physical measurements; double only for inherently imprecise values."

**Proposed fix location:** `skills/tabletest/SKILL.md` (Quality Checks section)

---

### 4. Conditional Logic in Test Methods (if/switch)

**What happens:** Agent puts `if` statements in the test method body to handle optional parameters instead of moving that logic into `@TypeConverter` methods.

**Evidence:**
- Failed `no-if-switch-in-method` in iter-26 evals 25, 26; iter-28 eval-25
- Agent creates `if (fragile) { ... }` style code to build options from separate columns

**Root cause:** This is directly connected to struggle #2 — when the agent keeps options as separate columns, it needs conditional logic to assemble them. Using a map column + TypeConverter eliminates the conditionals. The existing quality check says "no if/switch statements" but doesn't explain the escape hatch (TypeConverters).

**What the skill is missing:** Link the "no if/switch" quality check to the TypeConverter solution: "If you need conditional logic, it's a sign that a column should use a @TypeConverter or that columns should be consolidated into a map."

**Proposed fix location:** `skills/tabletest/SKILL.md` (Quality Checks, item about straightforward method)

---

### 5. Handling Ambiguity in Spec-by-Example

**What happens:** The agent either (a) ships incomplete work without marking it incomplete, or (b) asks the user to resolve all ambiguity before proceeding. Neither is ideal.

**Evidence:**
- Iter-27 eval-10: Listed 5 open questions, shipped response as if they're non-blocking
- Iter-27 eval-13: 3 open questions, said "once these are resolved, this maps cleanly"
- Iter-28 eval-24: 3 open questions, explicitly asked user to confirm before proceeding — never produced final output

**Root cause:** The spec-by-example skill says "not everything needs to be resolved before coding starts" (SKILL.md:221) and "mark uncertain cells" (SKILL.md:225). But it doesn't tell the agent what to DO with ambiguity — make an assumption and proceed, or block.

**What the skill is missing:** A clear protocol: "State your assumption explicitly ('Assuming X'), mark it in the table, and proceed. Don't ask the user to resolve before continuing. The table IS the vehicle for resolving ambiguity — ship it with documented assumptions."

**Proposed fix location:** `skills/spec-by-example/SKILL.md` (section 9, "Note What Is Still Open")

---

### 6. Timeouts from Permission Issues (iter-28 evals 29, 30)

**What happens:** Agent tried to Write files, got permission denied, retried, and timed out after 10 minutes.

**Evidence:** Both eval-29 and eval-30 in iteration 28 timed out. Conversation logs show the agent attempted Write operations that were denied and had no recovery strategy.

**Root cause:** This is a harness/infrastructure issue, not a skill issue. The evals may need `--allowedTools` configuration for Write operations, or the agent needs to output code in response.md rather than writing files.

**Proposed fix:** Not a skill change — this is an eval harness fix. Ensure evals that expect code output configure write permissions or instruct the agent to output code in markdown.

---

### 7. Scenario Names Describing Outcomes Instead of Conditions

**What happens:** Agent names scenarios like "Standard approval" or "Senior approval" (outcomes) instead of "Standard applicant at threshold" or "Senior applicant above threshold" (conditions).

**Evidence:**
- Failed in iter-27 eval-23
- The skill explicitly teaches this (SKILL.md:306–315) but the agent doesn't always follow it

**What the skill is missing:** This is already well-documented. The issue is that the quality check mentions it but the agent doesn't treat the quality checks as a mandatory pre-submission checklist. Making the quality checks more prominent or mandatory would help.

---

### 8. Policy Thresholds Not Surfaced as Columns

**What happens:** Thresholds (credit score 650, age 65, cutoff dates) are buried in scenario names or @Description instead of being visible as table columns.

**Evidence:**
- Failed `threshold-as-column` in iter-28 eval-23
- Failed `cutoff-date-column-if-literal-dates` in iter-28 eval-22
- Failed `descriptive-registration-date` in iter-28 eval-22

**Root cause:** The spec-by-example skill covers this well in "Make Thresholds and Limits Visible" (SKILL.md:295–349). The tabletest skill's `requirements-to-tables.md` also covers it. But the main tabletest SKILL.md doesn't have a design heuristic about thresholds — only about traceability columns.

**Proposed fix:** Add a brief heuristic to the tabletest SKILL.md Table Design section about surfacing policy values as columns.

---

### 9. Redundant @Description Content

**What happens:** Agent restates information already visible in the table rows within the @Description annotation.

**Evidence:**
- Failed `description-no-irrelevant-information` in iter-28 eval-22

**Root cause:** The skill already says "do not restate what the table already shows" (SKILL.md:221). The agent just doesn't self-check against this. Minor issue.

---

### 10. Special Character Quoting

**What happens:** Values containing colons (e.g., `tech:java`) are left unquoted when they should be quoted to avoid parsing conflicts.

**Evidence:**
- Failed `special-chars-quoted` in iter-27 and iter-28 eval-20

**Root cause:** The quoting rules table (SKILL.md:60–66) lists pipe, quotes, `[`, and `{` as needing quoting but doesn't mention colons. Colons are significant in map syntax (`key: value`) so values containing colons need quoting.

**Proposed fix:** Add colon to the quoting rules table in `skills/tabletest/SKILL.md`.

---

## Summary: Recommended Skill Changes (Priority Order)

| Priority | Area | File to Change | Change Type |
|----------|------|---------------|-------------|
| **P0** | Concern decomposition | SKILL.md (Workflow) | Add mandatory decomposition step before table design |
| **P0** | Map columns for sparse optionals | SKILL.md (Table Design) | Add design heuristic + strengthen reference trigger |
| **P1** | if/switch → TypeConverter link | SKILL.md (Quality Checks) | Link quality check to solution |
| **P1** | Ambiguity protocol | spec-by-example SKILL.md (§9) | Add "assume and proceed" protocol |
| **P1** | BigDecimal for money | SKILL.md (Quality Checks) | Add explicit numeric type rule |
| **P2** | Threshold columns | SKILL.md (Table Design) | Add brief heuristic |
| **P2** | Colon quoting | SKILL.md (Quoting table) | Add colon to special chars |
| **P2** | Quality checks as mandatory | SKILL.md (Quality Checks) | Strengthen language to "verify before finalizing" |
| **P3** | Timeout/permission | Eval harness | Fix eval-29/30 permissions |

## Token/Time Patterns

- Cache reads dominate token counts (50–84%), meaning the skill prompt itself is large. Any additions should be concise.
- Evals with perfect scores (eval-28 iter-26, eval-19 iter-27) tend to use MORE tokens on analysis — thorough analysis correlates with better results.
- The worst failures come from skipping analysis, not from spending too long on it.
- The agent's token budget is not the bottleneck — decision quality is.
