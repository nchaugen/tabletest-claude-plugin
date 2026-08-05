All 9 generated cases (correctly expanded from the value sets) fail as expected against the stub's `UnsupportedOperationException` — a proper red state, since `PermissionChecker.canPerform(...)` remains unimplemented per your instructions.

Summary of what was added:
- `build.gradle`: added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'`
- `src/test/java/com/example/PermissionCheckerTest.java`: one `@TableTest` covering all 9 role/action combinations in 5 rows, using value sets where the outcome doesn't depend on which action within a row (e.g., ADMIN is allowed for all three actions; GUEST is denied for both WRITE and DELETE)

`PermissionChecker.canPerform` was left as the original stub (throwing `UnsupportedOperationException`) — no permission logic was implemented, per your instructions.