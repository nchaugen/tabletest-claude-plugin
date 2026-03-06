# Pair Programming with TableTest

## Collaborative Cadence

When writing TableTests with a pair, follow this flow:

1. **Design Phase**: Discuss table structure together (see below)
2. **Confirmation**: Show mockup with 2-3 example rows, agree on structure
3. **Implementation**: One person writes while explaining design decisions
4. **Verification**: Both review test output and failures together
5. **Refinement**: Both improve names and structure after tests pass
6. **Enhancement**: Identify additional tables together

A 30-second mockup review prevents 5 minutes of rework. Never skip step 2.

## Refinement Phase

After tests pass, improve names and structure with your pair.

**Column names evolve**: Replace implementation terms with domain language
- `registered` → `Feature Toggles`
- `expectedQueryCount` → `Query Count?`
- `expected` → `Result?`

**Scenario names evolve**: Add clarity as the table grows (see `references/table-design-advanced.md`).

**Refinement is normal**: Names emerge from understanding. Don't expect perfect names on the first implementation.

## Test Infrastructure Emerges from Need

Don't design helper classes upfront. Write the table first, then create helpers based on what needs observing.

**Anti-pattern**: Designing `QueryCounter` before knowing what to test

**Good pattern**: Table design reveals what to observe
```
Need to count queries → create QueryCounter
Need to record sequence → create QueryRecorder
Need to verify timing → create TimingCapture
```

**Helper organization**:
- Place at bottom of test class after all test methods
- Name clearly: `QueryCounter`, `QueryRecorder` (not `Helper`, `Utils`)
- Keep simple: one focused responsibility per helper
- Extract to separate file only when reused across test classes
