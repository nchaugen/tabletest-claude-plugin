Compiles cleanly with the stub in place. Everything is set up:

- `build.gradle` — added the `tabletest-junit` dependency
- `src/main/java/org/example/TagFilterImpl.java` — stub implementing `TagFilter`, throws `UnsupportedOperationException` (logic not yet written)
- `src/test/java/org/example/TagFilterTest.java` — three `@TableTest` methods:
  1. **`keepsTagsMatchingTheCategorysResolvedPrefix`** — the core prefix-resolution rule (tech→tech:/dev:, business→biz:, custom category→itself+":"), plus no-match, empty-list, empty-tag-string, special characters, and newline-content rows
  2. **`keepsTagsWhosePrefixIsInTheOptionalSet`** — optional set extending the kept prefixes, including null/empty-optional as no-ops
  3. **`returnsAllTagsUnchangedWhenCategoryIsNull`** — the null-category bypass, with an explicit assumption documented in `@Description`: optional is ignored too when category is null, and the empty-tag rule still applies

I verified the tables themselves are correct by temporarily swapping in a real implementation — all 16 rows passed — then reverted to the stub. One thing worth flagging: the tag values needed quoting (`"tech:java"` not `tech:java`) because TableTest's grammar treats an unquoted colon inside `[...]` as map syntax; without quotes, `[tech:java, dev:kotlin]` parses as a map, not a list of strings, and blows up either at parse time or at `List<String>` conversion. I confirmed this concretely by running the build before fixing it.

Assumptions documented in the test file (in `@Description` blocks) and worth your sign-off:
- Optional entries are matched the same way custom categories are — as `entry + ":"` prefix matching.
- A null category is treated as a full bypass, including the optional set — only the empty-tag rule survives it.