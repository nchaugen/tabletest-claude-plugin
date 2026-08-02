Test-first work is done and verified.

**Summary:**
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`.
- Wrote `src/test/java/com/example/PermissionCheckerTest.java` with one `@TableTest` covering the role/action permission rule — using value sets to compactly express "ADMIN allows any action" and "USER/GUEST disallow this specific action or set of actions," rather than one row per role×action pair.
- Left `PermissionChecker.canPerform` as the stub (throws `UnsupportedOperationException`) per your instruction — logic isn't implemented.
- Ran `gradle test`: it compiles cleanly and all 9 generated cases (from the value-set cartesian expansion) fail on the stub's exception, confirming the tests are correctly wired to the not-yet-implemented method.

One assumption: DELETE/WRITE denial for USER/GUEST is grouped via value sets `{WRITE, DELETE}` since both produce `false` for GUEST — if you'd rather see every role×action pair as its own row, I can expand it, but the current shape follows the "value sets for regardless-of" rule instead.