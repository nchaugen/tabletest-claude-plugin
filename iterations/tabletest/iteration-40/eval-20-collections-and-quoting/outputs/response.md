## Summary

Wrote `src/test/java/org/example/TagFilterTest.java` with three `@TableTest` methods covering distinct concerns, plus a compile-only stub:

- **`filtersTagsByCategory`** (7 rows) — the tech/business/other prefix rules, the boundary case where a tag merely contains but doesn't start with `category:` (e.g. `techdebt:old` vs `tech:java`), no-match, empty list, and null category (bypasses filtering entirely).
- **`optionalCategoriesSupplementTheKeptSet`** (4 rows) — optional set adding one/multiple extra kept prefixes, and null vs. empty optional both being no-ops.
- **`preservesTagFormatting`** (5 rows) — pipe/bracket characters and embedded newlines (via the `\\n` placeholder convention) surviving unchanged, and empty-string tags always being dropped, including under a null category.

Assumption I made and documented in `@Description`: the "empty tag is never kept, whatever the category" rule also applies when category is `null` (i.e., null-category bypass skips *prefix* filtering but not the empty-string exclusion) — this was ambiguous in the spec, so I chose the interpretation that makes the rule non-redundant and added an explicit test row for it.

Setup:
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter was already 6.0.3, well above the 5.11 minimum).
- Added `TagFilterImpl` implementing `TagFilter` with a stub that throws `UnsupportedOperationException`, so tests compile without any filtering logic.

Verified: `gradle test` compiles cleanly and runs all 16 generated test cases — all fail with `UnsupportedOperationException` as expected for an unimplemented stub. I also independently cross-checked every expected `Kept?` value against a scratch reference implementation of the described rules to confirm the table data itself is correct.