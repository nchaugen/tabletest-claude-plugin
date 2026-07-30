The table parses correctly and generates 9 test cases (all currently failing with `UnsupportedOperationException`, as expected since the permission logic isn't implemented yet).

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/PermissionCheckerTest.java`:

- One `@TableTest` table (single rule: role/action → allowed), rows use value sets to express "regardless of" relationships stated in the spec (ADMIN allowed for any action; USER's two forbidden-vs-allowed split; GUEST's one allowed vs. two forbidden), covering all 9 role/action combinations in 5 rows.
- Verified via `gradle compileTestJava` that it compiles, and `gradle test` confirms the table parses into the expected 9 cases — all failing on the stub's `UnsupportedOperationException`, as intended since the logic isn't implemented yet.