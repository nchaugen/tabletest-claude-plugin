# Eval Review — tabletest, Iteration 38

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-07 · **Evals:** 1

## Summary

12/20 (60.0%) · 3752644 tokens · 553.4s · $2.1646

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 37

**Improvements (12):**
- ✅ eval-15-reis-discount: `2.1-decomposition-concern-separation`
- ✅ eval-15-reis-discount: `2.2-children-flat-discount`
- ✅ eval-15-reis-discount: `2.3-depth-tier-boundaries`
- ✅ eval-15-reis-discount: `2.16-no-duplicate-tier-mapping`
- ✅ eval-15-reis-discount: `2.10-format-displayname`
- ✅ eval-15-reis-discount: `2.11-format-description`
- ✅ eval-15-reis-discount: `2.13-format-annotation-order`
- ✅ eval-15-reis-discount: `2.14-format-description-textblock`
- ✅ eval-15-reis-discount: `concerns-decomposed`
- ✅ eval-15-reis-discount: `minimal-rows-per-concern`
- ✅ eval-15-reis-discount: `has-tabletest-dependency`
- ✅ eval-15-reis-discount: `compiles`

## Resource Comparison vs Iteration 37

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-15-reis-discount | 12/20 | 0/20 | 3752644 | 555712 | 553.4 | 139.3 |

## Per-Eval Results

### ⚠️ Eval eval-15-reis-discount

**12/20** · 3752644 tokens · 553439ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > ReisWindowTest shows 'Ticket exactly 30 days before is excluded | ["2026-05-31T12:00 SINGLE"] | 2026-06-30T12:00 | 0' but does NOT test 31 days (or 29 days for inclusion boundary)
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > ReisLadderTest uses individual rows (0, 4, 5, 9, 10, 35, 39, 40, 60) rather than value sets like {0-4}, {5-9}, {10-14}, etc. No value sets are present.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > ReisLadderTest has 9 individual rows with single ticket counts (0, 4, 5, 9, 10, 35, 39, 40, 60) rather than value sets grouping each tier's qualifying counts
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > ReisDiscountCalculatorTest has no Zone column; ReisWindowTest @Description states zone does not affect count but does not include Zone in table columns
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > ReisDiscountCalculatorTest has separate rows for ADULT and SENIOR (e.g. 'Adult buying fifth ticket...' and 'Senior buying tenth ticket...'), not combined as a value set
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
  > ReisLadderTest covers tiers at 0%, 5%, 10%, 35%, 40% but omits explicitly testing the 15%, 20%, 25%, 30% tiers (no row for 15, 20, 25, 30, or their boundaries)
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > ReisLadderTest has two rows per tier boundary (e.g. '4' and '5', '9' and '10') instead of one row per tier with a value set like {5-9}
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > ReisWindowTest uses absolute dates like '2026-05-31T12:00', '2026-06-01T12:00' rather than relative expressions like 'days ago'
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

