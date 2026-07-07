Confirmed: all 19 test cases compile, parse correctly, and fail cleanly with `UnsupportedOperationException` from the stub (red state), as expected before implementation.

## Summary

**`build.gradle`**: added `org.tabletest:tabletest-junit:1.2.1` test dependency (existing JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).

**`TagFilterImpl.java`**: stub implementing `TagFilter`, throws `UnsupportedOperationException` — compiles but not implemented, per instructions.

**`TagFilterTest.java`**: 4 `@TableTest` methods, decomposed by concern:
1. `filtersTagsByCategory` — the core prefix rule (tech→tech:/dev:, business→biz:, other→prefix:), including boundary rows for prefixes without a colon (`techstuff`) and near-miss prefixes (`bizarre:thing`).
2. `appliesOptionalCategoryPrefixes` — optional set adds extra kept prefixes; includes a row proving optional entries are literal (not aliased through tech/business), documented via `@Description` since the spec didn't say explicitly.
3. `handlesEmptyAndNullInputs` — empty list, null category, null/empty optional, using a value set for category on the empty-list row since it doesn't affect the (always-empty) result.
4. `preservesSpecialCharactersInTagContent` — pipes, brackets, and embedded newlines in tag content survive filtering untouched; uses the `\\n` placeholder + `withNewlines` helper convention from the skill for embedding real newlines in table cells.

Along the way I hit and fixed a real parsing gotcha: unquoted list elements containing colons (e.g. `[tech:java, biz:sale]`) get misparsed by TableTest as map syntax. Fixed by quoting every tag element (`["tech:java", "biz:sale"]`).

I verified correctness by temporarily swapping in a real implementation — all 19 generated test cases passed — then reverted to the stub, which now fails all 19 with the expected `UnsupportedOperationException`, confirming the tests are ready to drive the real implementation.