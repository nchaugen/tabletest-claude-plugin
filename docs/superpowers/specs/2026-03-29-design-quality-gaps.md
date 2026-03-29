# Design Quality Gaps in Evals 14-17

**Date:** 2026-03-29
**Status:** Backlog — captured from iteration 9 analysis, not yet scheduled

## Context

Iteration 9 evals 14-17 surfaced 9 design quality failures that are independent of skill routing. These represent cases where the skill guidance exists but the agent doesn't apply it consistently for complex domain problems. Fixing these requires strengthening skill content, not just routing.

## Failures by Theme

### 1. Table Decomposition (3 failures)

The agent produces single monolithic tables instead of separating concerns.

| Eval | Assertion | What happened |
|------|-----------|--------------|
| 15 reis-discount | 2.1-decomposition | Single table instead of separate discount ladder / traveller eligibility / rolling window |
| 16 order-splitting | 3.2-trigger-interactions | Each table covers one trigger; no row where multiple triggers fire simultaneously |
| 17 shopping-cart | 4.13-coupon-cart-contents | No cart contents column for product-specific coupons — the coupon concern isn't fully decomposed |

**Root cause:** The spec-by-example skill says "one table per concern" and gives signals for when concerns are mixed, but doesn't give enough guidance on how to identify independent axes in a complex domain. The agent tends to organize by input category rather than by independent decision.

**Potential fix:** Add worked examples showing how to identify independent concerns in a feature description. E.g., for reis-discount: "who is eligible" (traveller type), "what discount they get" (ticket count → tier), and "which tickets count" (rolling window) are three independent rules.

### 2. Value Set Usage for Tier Grouping (2 failures)

The agent enumerates boundary values as separate rows instead of grouping same-outcome values into value sets.

| Eval | Assertion | What happened |
|------|-----------|--------------|
| 15 reis-discount | 2.9-value-set-tier-semantics | Single values per row instead of grouping tier members |
| 15 reis-discount | 2.15-ticket-count-value-sets | Enumerates boundaries (5, 9, 10, ...) instead of {5, 6, 7, 8, 9} → 5% |

**Root cause:** The agent understands value sets for "regardless of" relationships (e.g., `{Economy, Premium}` when category doesn't matter) but doesn't apply them for tier grouping — where multiple input values produce the same output. The value-sets reference covers syntax but not this design pattern.

**Potential fix:** Add a "value sets for tier consolidation" example showing how discount tiers map to value sets. This is distinct from "regardless of" — it's "these values all produce the same result."

### 3. Boundary Testing Depth (2 failures)

The agent identifies boundary conditions but doesn't always test them with concrete values.

| Eval | Assertion | What happened |
|------|-----------|--------------|
| 14 weekly-pay | 1.5-depth-error-edge-cases | Negative hours not tested as input values |
| 15 reis-discount | 2.4-rolling-window-boundary | Surfaced as open question but not tested with 30-day vs 31-day values |

**Root cause:** The agent recognizes boundaries intellectually (mentions them in open questions or notes) but doesn't always include them as concrete table rows. The spec-by-example skill says "include rows at exact boundaries" but the agent sometimes surfaces the boundary as a question instead of testing it.

**Potential fix:** Strengthen guidance: "When you identify a boundary, always add rows at the boundary. If the boundary value is ambiguous (inclusive vs exclusive?), add rows on both sides AND mark the expected output as an open question. The rows should exist even if the outcome is uncertain."

### 4. Column Design for State Transitions (2 failures)

The agent misses before/after columns that make state changes explicit.

| Eval | Assertion | What happened |
|------|-----------|--------------|
| 14 weekly-pay | 1.6-readability-empty-cells | Uses int/0 instead of Integer/blank for irrelevant inputs |
| 17 shopping-cart | 4.14-coupon-before-after | No before/after coupon columns showing state change |

**Root cause:** The spec-by-example skill covers state-as-rules framing (before/action/after pattern) but the agent doesn't consistently apply it when the feature description doesn't frame things as state transitions. The shopping cart prompt describes operations ("enter a coupon code") without explicitly mentioning state — the agent should recognize this as a state transition anyway.

**Potential fix:** Add guidance: "When an operation changes system state (cart contents, active coupon, account balance), include before and after columns — even when the prompt describes the operation procedurally rather than as a state transition."

## Priority

These are second-order improvements. The routing fix (making tabletest self-sufficient for unstructured requirements) addresses the 6 format failures first. Once that's done, these design quality gaps become the next target for iteration.

## Relationship to Experiment 1

These gaps exist in both with-skill and without-skill runs. Fixing them strengthens the with-skill variant's score but doesn't change the experimental design. They can be addressed before or after experiment 1.
