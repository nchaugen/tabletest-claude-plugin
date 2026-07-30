Confirmed back to the correct state: 14 tests, all failing on `UnsupportedOperationException` from the stub (not implemented yet), same as before.

## Summary

Created `src/test/java/org/example/TagFilterTest.java` with three `@TableTest` methods, plus `TagFilterImpl` as a stub (throws `UnsupportedOperationException`) so the tests compile:

- **`keepsTagsMatchingTheCategoryPrefix`** — the core category→prefix rule (tech/dev, biz, custom category), no-match, empty list, null category (unfiltered), empty tag string dropped even under null category, and special characters (`|`, `[]`) preserved as-is.
- **`preservesNewlinesInTagContent`** — embedded newlines (via the `\\n` table-value technique) don't break prefix matching or get stripped.
- **`widensKeptTagsWithOptionalCategories`** — optional category set unions into the kept tags; null vs. empty optional both apply no extra filter. Flagged an assumption in `@Description`: optional entries match tags by literal prefix only (`"tech"` doesn't also admit `"dev:"` tags) — this wasn't specified and may need confirming.

I added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter was already ≥5.11). I verified the table design by temporarily swapping in a real implementation — all 14 generated cases passed — then reverted to the stub, so `gradle test` now fails only on the expected `UnsupportedOperationException`.