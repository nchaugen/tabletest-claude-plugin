Confirmed back to the stub state (all 16 fail with `UnsupportedOperationException`, as required before implementation).

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`, a stub `TagFilterImpl` (throws `UnsupportedOperationException`), and `src/test/java/org/example/TagFilterTest.java` with 4 `@TableTest` methods, decomposed by rule:

1. **`keepsTagsMatchingCategoryPrefix`** — tech/business/custom/null category prefix rules, plus empty-list input (5 rows).
2. **`alsoKeepsTagsFromOptionalCategories`** — optional set is additive; null/empty optional is a no-op (4 rows).
3. **`dropsEmptyTagRegardlessOfCategory`** — empty-tag invariant, including its interaction with the null-category passthrough (5 rows, one using a `{tech, business, urgent}` value set).
4. **`preservesSpecialCharactersInKeptTags`** — pipes/brackets and embedded newlines (via the `\\n`-escape convention) survive filtering unmangled (2 rows).

Verified all 16 generated cases against a throwaway correct implementation (all passed), then confirmed they all fail with `UnsupportedOperationException` against the required stub. Next step is implementing `TagFilterImpl.filterTags`.