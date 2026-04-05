# Eval Review — Iteration 23

**Model:** sonnet · **Date:** 2026-04-02 · **Evals:** 1

## Summary

**no_skill:** 8/9 (88.9%) · 158115 tokens · 74.8s · $0.1980

## Delta vs Iteration 22

No changes.

## Resource Comparison vs Iteration 22

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-9-bonus-contractor-structure | 8/9 | — | 158115 | — | 74.8 | — |

## Per-Eval Results

### ⚠️ Eval eval-9-bonus-contractor-structure [no_skill]

**8/9** · 158115 tokens · 74805ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
  > CONTRACTOR | SALES | 0.0 and CONTRACTOR | ENGINEERING | 0.0 are enumerated as separate rows. The assertion requires a value set like {SALES, ENGINEERING} in a single row, and explicitly forbids separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table already expresses — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

