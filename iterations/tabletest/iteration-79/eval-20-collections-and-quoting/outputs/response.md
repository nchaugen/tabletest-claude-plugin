All 15 test cases compile and parse correctly, failing only with the expected `UnsupportedOperationException` from the stub.

## Summary

Wrote `src/test/java/org/example/TagFilterTest.java` with three `@TableTest` tables against the existing `TagFilter` interface:

1. **`filtersTagsByCategoryPrefix`** — the category→prefix rule (tech: keeps `tech:`/`dev:`, business: keeps `biz:`, other categories keep their own `prefix:`), plus null category (keeps everything) and empty-list input (value set across categories). Each row also threads an empty-string tag through to verify it's never kept, and includes near-miss prefixes (e.g. `bizarre:`, `sportscar:`) to pin down that matching requires the literal `:` boundary.
2. **`alsoKeepsTagsMatchingAnOptionalPrefix`** — the additive optional-set rule, holding category fixed at `tech` since the category rule is already covered above; covers null optional, empty optional, single/multiple matching prefixes, and a non-matching optional prefix.
3. **`preservesSpecialCharactersInKeptTags`** — pipes, brackets, and embedded newlines are preserved verbatim in kept tags.

Also:
- Added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'` to `build.gradle`.
- Added a stub `TagFilterImpl` (throws `UnsupportedOperationException`) so the tests compile.
- Verified table syntax/escaping by temporarily wiring an identity implementation (list elements needed quoting because tag prefixes contain `:`, which the parser otherwise reads as map syntax; the newline escape needed exactly `\\n` in the text block, not double that).

Ran `gradle test`: all 15 generated cases compile and execute, failing only with the intentional `UnsupportedOperationException` — ready for the real implementation.