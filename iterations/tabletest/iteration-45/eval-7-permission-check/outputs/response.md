Done. Summary:

- Added `tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).
- Wrote `src/test/java/com/example/PermissionCheckerTest.java` with one `@TableTest` covering all 5 obligations (Admin/any action, User read+write, User delete denied, Guest read, Guest write+delete denied), using value sets to collapse "regardless of action" cases — 9 generated test cases total.
- Left `PermissionChecker.canPerform` as the existing stub (throws `UnsupportedOperationException`) — tests compile and run, all currently fail against the stub as expected since the logic isn't implemented yet.

Verified with `gradle compileTestJava` (clean compile) and `gradle test` (9/9 fail with the stub's exception, no parse errors).