Done. Summary:

- `build.gradle`: added `org.tabletest:tabletest-junit:1.2.2` test dependency (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).
- `src/main/java/org/example/TagFilterImpl.java`: stub implementation of `TagFilter` that throws `UnsupportedOperationException` — compiles but has no filtering logic yet.
- `src/test/java/org/example/TagFilterTest.java`: two `@TableTest` methods:
  - `shouldFilterTagsByCategory` — the core category-prefix rule (tech/dev, business/biz, custom category) plus the format edge cases (null category passthrough, empty list, empty tag dropped even under null category, pipe/bracket preservation, embedded newline preservation). `optional` is held at `null` throughout, documented via `@Description`.
  - `shouldApplyOptionalCategoryFilter` — the `optional` set's additive behavior, including null/empty optional meaning no extra filter, and combining with both built-in and custom categories.

Assumption: "prefix" for a custom category means the category string itself followed by `:` (e.g. category `"urgent"` keeps `"urgent:..."` tags), and the same applies to each entry in `optional`.

Verified with `gradle compileTestJava` (succeeds) and `gradle test` (all 13 generated cases fail only on the stub's `UnsupportedOperationException`, confirming table syntax/conversions are valid). Filtering logic itself is intentionally left unimplemented per your instructions.