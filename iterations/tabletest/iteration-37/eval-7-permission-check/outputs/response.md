All 9 rows expanded correctly from the value sets and fail as expected against the stub (`UnsupportedOperationException`), confirming the tests are correctly wired and compiling — a proper red state for TDD, since `PermissionChecker` is left unimplemented as instructed.

Summary of changes:
- `build.gradle` — added `testImplementation 'org.tabletest:tabletest-junit:1.2.1'`
- `src/test/java/com/example/PermissionCheckerTest.java` — new `@TableTest` covering the rule: ADMIN allowed on any action (value set), USER allowed on READ/WRITE but not DELETE, GUEST allowed only on READ (9 cases from 5 rows via value sets)
- `PermissionChecker.canPerform` left as the existing stub (throws `UnsupportedOperationException`) — not implemented, per your request