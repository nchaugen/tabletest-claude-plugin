All 18 tests compile and fail red against the stub, as expected for TDD.

Summary of what was delivered:

- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter was already 6.0.3, which satisfies the ≥5.11 requirement).
- Added `TagFilterImpl` stub in `src/main/java/org/example/TagFilterImpl.java` — implements `TagFilter`, throws `UnsupportedOperationException`, no filtering logic.
- Wrote `src/test/java/org/example/TagFilterTest.java` with 5 `@TableTest` methods (18 cases total), one per concern:
  1. **`filtersTagsByCategoryPrefix`** — tech/dev, business/biz, custom-prefix, and the "prefix must be followed by a colon" boundary (e.g. `technology:` doesn't match `tech`).
  2. **`appliesOptionalCategoryFilter`** — null/empty/single/multiple optional sets, category held at `"tech"` (declared in `@Description` since Table 1 owns category variation).
  3. **`passesTagsThroughWhenCategoryIsNull`** — unfiltered passthrough, including the interaction where an empty tag string is still dropped even when category is null.
  4. **`returnsEmptyListForDegenerateInput`** — empty list, and empty-string-only input using a `{tech, business, urgent}` value set to show the drop is category-independent.
  5. **`preservesSpecialCharactersInKeptTags`** — pipe, brackets, and newline (via the documented `\\n` placeholder + manual `.replace` helper) preserved unmodified.

Assumptions made (non-interactive run):
- Optional category prefixes are matched the same way as custom categories (`prefix:`), and are purely additive to the category's base rule.
- Optional filtering is irrelevant when category is null (already-unfiltered), so no test combines them.
- List elements containing `:` must be double-quoted in TableTest syntax to avoid map-syntax ambiguity — verified empirically since the skill doc doesn't call this out explicitly for list contexts.

I validated correctness by temporarily swapping in a real implementation (all 18 tests passed) before reverting to the stub, so the expected values are verified, not guessed.