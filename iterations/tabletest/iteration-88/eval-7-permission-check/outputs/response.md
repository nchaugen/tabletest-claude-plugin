The table expands correctly to all 9 role/action combinations, and every case fails with the expected `UnsupportedOperationException` from the unimplemented stub — confirming the tests are wired correctly and just waiting on the real permission logic.

Summary:
- Added `tabletest-junit:1.2.2` to `build.gradle` test dependencies.
- Created `src/test/java/com/example/PermissionCheckerTest.java` with one `@TableTest` covering the full ADMIN/USER/GUEST × READ/WRITE/DELETE permission matrix, using value sets to collapse same-outcome cases (e.g. all three actions allowed for ADMIN, WRITE+DELETE both denied for GUEST) into 5 rows instead of 9.
- Left `PermissionChecker.canPerform` as the existing stub (throws `UnsupportedOperationException`) — did not implement the logic, per your instructions.

Test/compile run confirms: compiles clean, all 9 expanded cases fail only due to the stub, not due to table/setup errors. Ready for you to implement `canPerform` next.