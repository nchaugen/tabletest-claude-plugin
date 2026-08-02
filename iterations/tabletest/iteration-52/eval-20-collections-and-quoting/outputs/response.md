All 14 rows compile, parse, and fail only because the stub throws — verified correct against a real implementation, then reverted.

## Summary

- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter is already 6.0.3, well above the 5.11 minimum).
- Added `TagFilterImpl` (`src/main/java/org/example/TagFilterImpl.java`) — a stub that throws `UnsupportedOperationException`, so tests compile but nothing is implemented yet.
- Wrote `src/test/java/org/example/TagFilterTest.java` with 5 `@TableTest` methods, decomposed by concern:
  1. `selectsTagsByCategoryPrefix` — the tech/dev, business/biz, and arbitrary-category prefix rules, plus the empty-list edge case.
  2. `appliesOptionalAdditionalCategories` — absent, empty, single, and multiple optional categories.
  3. `returnsEveryTagWhenCategoryIsNull` — null category short-circuits filtering, shown to hold regardless of the optional set.
  4. `dropsEmptyTagStringsRegardlessOfCategory` — empty tag strings are always dropped, even under a null category.
  5. `preservesSpecialCharactersInTagValues` — pipes, brackets, and embedded newlines pass through unmodified.

One notable finding while validating: the TableTest parser treats any colon inside `[...]` as map syntax, so every tag literal (all in `category:value` form) had to be quoted (`"tech:java"`) to parse as a plain list element rather than be misread as a map key — confirmed empirically by running the suite and fixing the resulting parse errors.

I temporarily wrote a real implementation to confirm all 14 rows' expected values are correct, then reverted `TagFilterImpl` to the stub. `gradle test` now fails all 14 cases solely with `UnsupportedOperationException`, the expected red state before implementation.