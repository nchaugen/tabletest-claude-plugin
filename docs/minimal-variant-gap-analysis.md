# Analysis: Valuable Heuristics Missing from Minimal Skill

## Context

Official skill (522 lines + 15 reference files) vs minimal variant (746 lines, 6 references used). Minimal iteration-2 scores **83.3%** (209/251) vs official iteration-29 at **79.0%** (203/257). Most high-value content from `requirements-to-tables.md` was already inlined into minimal (decomposition heuristics, value set tier grouping, stateful features as rules, rules vs arithmetic). Several evals (14, 15, 22, 23, 29, 30) DO start from specs/requirements, not just existing code.

## What's Actually Missing from Minimal

### 1. "Make Thresholds Visible" — HIGH VALUE, TESTED BUT FAILING

From `requirements-to-tables.md` step 7. The heuristic: include threshold/policy values as columns even when constant across all rows.

```
Customer Age | Max Age (Policy) | Eligible?
75           | 75               | yes
76           | 75               | no
```

- **Directly tested** by eval-23's `threshold-as-column` assertion
- **Fails in both variants** (official iteration-29 and minimal iteration-2)
- Only threshold-adjacent content in minimal is about calendar dates (line 357: "include the policy/cutoff date as a separate column")
- This is compact (~15 lines) and could be added to the Table Design section

### 2. "Orthogonal Concerns" — MEDIUM VALUE, UNCLEAR EVAL COVERAGE

From `table-design-advanced.md`. The heuristic: when one column could be `{true, false}` for every row without affecting any expectation, it's orthogonal — test separately, don't cross-multiply.

- No mention of "orthogonal" in minimal
- Needs verification of which eval tests this

### 3. "Probe for Variations" Checklist — LOW-MEDIUM VALUE

From `requirements-to-tables.md` steps 2-4. Systematic approach:
- Different outcomes: "What makes the answer change?"
- Boundary conditions: "At what exact value does the rule kick in?"
- Special cases: "What surprises new team members?"
- Absent inputs: "What if this field is empty?"

This is implicitly expected by boundary-testing assertions (eval-14, 18, 23) but not explicitly stated in minimal. The boundary testing failures in both variants might improve with this checklist.

## Summary

| Heuristic | In Minimal? | Tested? | Both Fail? | Action |
|-----------|-------------|---------|------------|--------|
| **Make Thresholds Visible** | No (only calendar dates) | Yes (eval-23) | Yes | Inline into minimal |
| **Orthogonal Concerns** | No | Unclear | N/A | Verify eval coverage |
| **Probe for Variations** | No (implicit only) | Yes (boundary assertions) | Partially | Consider inlining |
| Decomposition heuristics | Yes | Yes | Partially | Already there |
| Value set tier grouping | Yes | Yes | Partially | Already there |
| Stateful as rules | Yes | Yes | No | Already there |
| Rules vs arithmetic | Yes | Yes | Partially | Already there |
