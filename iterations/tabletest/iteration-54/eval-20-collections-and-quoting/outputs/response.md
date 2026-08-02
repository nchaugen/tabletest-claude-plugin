Confirmed: compiles cleanly and all 18 cases fail against the stub (`UnsupportedOperationException`), as expected for a red/TDD starting state.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`, a stub `TagFilterImpl` (throws `UnsupportedOperationException`), and `src/test/java/org/example/TagFilterTest.java` with 5 `@TableTest` tables (18 rows total), one per concern:

1. **`filtersTagsByCategoryPrefix`** — tech (`tech:`/`dev:`), business (`biz:`), custom-category (`<category>:`), no-match, and empty-list cases.
2. **`addsOptionalCategoryPrefixesToKeptSet`** — null vs. empty optional (equivalent), single and multiple added prefixes.
3. **`returnsTagsUnfilteredWhenCategoryIsNull`** — null category returns input unchanged regardless of optional.
4. **`excludesEmptyTagStrings`** — empty tag dropped, shown across a category value set (`{tech, business, sports}`).
5. **`preservesTagContentWhenKept`** — pipes, brackets, and embedded newlines (via `\n` escape + manual unescape helper) survive unchanged.

Two assumptions are documented in `@Description` blocks since the spec was silent on them:
- A null category bypasses **all** rules, including the empty-tag exclusion.
- Optional-set entries are bare category names (e.g. `"sports"`), matched the same way as the `category` parameter (prefix + `:`).

I verified correctness by temporarily wiring up a real implementation — all 18 cases passed — then reverted `TagFilterImpl` back to the stub, so the tests are currently red as intended for TDD.