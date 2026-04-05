# Best Practices for Spec-by-Example and TableTest

Heuristics distilled from eval review. These apply to both SBE (markdown tables) and TT (@TableTest) unless noted otherwise.

## Concern separation

- **One table per concern.** Each table should test a single rule or decision. A monolithic table mixing unrelated rules forces unnecessary row permutations and obscures which rule each row is testing.
- **Separate tables reduce rows.** If a pay calculation mixes overtime classification with Sunday premium with arithmetic, you need rows for every combination. Separate tables test each concern with minimal rows.
- **Table structure guides implementation.** Five concern tables suggest five functions. The spec becomes a design tool, not just a verification artifact.
- **TT: multiple @TableTest methods in one class.** Even when testing a single API method, decompose concerns into separate @TableTest methods using default values for irrelevant inputs.

## Policy values and thresholds

- **Numeric policy thresholds deserve their own column.** Credit score cutoffs (650/600), minimum group sizes, overtime hour thresholds — these are configuration values that business experts may discuss. Surface them as dedicated policy columns, not just as data values in input columns.
- **Calendar dates: use descriptive values.** "before cutoff", "on cutoff", "after cutoff" with a TypeConverter is more readable than raw dates. The reader doesn't need to mentally compare 2025-02-28 against 2025-03-01.
- **If raw dates are used, include the policy date column.** When registration dates are literal, the cutoff date must appear as a separate column so the reader can verify the comparison.
- **Don't duplicate policy in descriptive values.** If registration date already says "before cutoff", a separate "Early bird cutoff" column is redundant. But "Min group size" as a column is fine — it's a numeric threshold, not encoded in the descriptions.

## Intermediate columns for traceability

- **Show the reader's work.** When a result comes from multi-step logic, add intermediate expected columns so each step is traceable. For weekly pay: show 1x hours, 1.5x hours, 2x hours — not just total pay.
- **Traceability columns are optional when logic is a simple lookup.** A level+department→percentage table doesn't need intermediate columns if there's no multi-step derivation.

## Output columns

- **Prefer the rule's direct output.** Use "Discount?" over "Price?" — the discount is what the rule decides; the price requires knowing the base price to verify. If price is used, include base price as a column.
- **Output columns end with `?`.** Input columns never have `?` suffixes — including yes/no flag columns that describe scenario state.

## Domain language and values

- **SBE: use yes/no** for boolean-like values. "true"/"false" are implementation terms.
- **TT: true/false is acceptable** when the Java parameter type is boolean.
- **Use concrete domain terms.** "Stable"/"Unstable" over "CATEGORY_A", "Senior" over raw age checks. The table should read as a specification a product person could review.

## Scenario names

- **Describe conditions, not outcomes.** "Senior applicant at threshold" — not "Approved" or "Gets 15%". The outcome is in the output column; the scenario name says *why* this row exists.

## Value sets

- **Use value sets when all values produce the same result.** `{READ, WRITE}` for actions that are all permitted consolidates redundant rows.
- **Don't use value sets when scenario descriptions add context.** Email validation patterns like "missing local part", "no TLD", "missing @" each test a different structural rule — grouping them as `{@missing.com, user@.com}` loses the *why*.
- **One row per tier/group.** If tickets 5-9 all produce 5% discount, express as `{5, 6, 7, 8, 9}` in one row — not separate rows for "tier kicks in" and "tier holds".

## Blank cells vs value sets for irrelevant inputs

- **Blank = null/absent.** The input genuinely doesn't exist (optional dietary requirements not provided).
- **Value set = irrelevant but present.** `{UK, Ireland, Other}` for standard shipping destination means "destination exists but doesn't affect cost".
- **Don't use blanks for "doesn't matter".** Blanks mean null. If the input exists but is irrelevant, use a value set or a representative value.

## @Description (TT-specific)

- **Add context the table can't show.** Fixed assumptions (overtime threshold is 40 hours), application context, open questions.
- **Don't restate what's visible.** If discount percentages are in the table, don't repeat them in @Description. If input values are hardcoded in the method body, they should be columns instead.
- **Don't include irrelevant fixed values.** "Fixed for all rows: name = 'Alice Smith'" belongs as a column or as a visible default in the method — not as noise in the description.

## Special characters and quoting (TT-specific)

- **Quote values containing `:`, `|`, or `[]`.** Unquoted colons are interpreted as map key:value pairs. Unquoted pipes conflict with column delimiters. Unquoted brackets conflict with list syntax.
- **Use `[]` for empty lists, blank for null.** The distinction matters.
