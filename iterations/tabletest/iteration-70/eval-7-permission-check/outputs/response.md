All 9 rows correctly fail against the unimplemented stub (`UnsupportedOperationException`), confirming the table compiles and exercises `canPerform` correctly — implementation is left undone as requested.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` as a test dependency in `build.gradle` (JUnit Jupiter was already 6.0.3, satisfying the ≥5.11 requirement), and wrote `src/test/java/com/example/PermissionCheckerTest.java` with one `@TableTest` covering all 9 Role×Action combinations as a permission matrix:

- ADMIN: READ/WRITE/DELETE → all `true`
- USER: READ/WRITE → `true`, DELETE → `false`
- GUEST: READ → `true`, WRITE/DELETE → `false`

I exhaustively enumerated the matrix rather than collapsing rows, since the enum domain is small and closed (3 roles × 3 actions), and each combination is a distinct rule obligation per the spec. `PermissionChecker.canPerform` remains a stub throwing `UnsupportedOperationException` — verified this compiles and all 9 rows fail against it as expected. Implementation was intentionally left undone per your instructions.