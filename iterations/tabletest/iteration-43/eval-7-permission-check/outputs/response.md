All 9 rows generate correctly and fail with `UnsupportedOperationException`, confirming the table is wired to the stub and only the permission logic remains unimplemented (as required — expected red state before implementation).

Summary of what was delivered:
- `build.gradle:16` — added `org.tabletest:tabletest-junit:1.2.2` test dependency (JUnit Jupiter 6.0.3 already satisfies the 5.11+ requirement).
- `src/test/java/com/example/PermissionCheckerTest.java` — one `@TableTest` covering the three rules via value sets (9 role/action combinations in 5 rows): Admin allows any action, User allows read/write but not delete, Guest allows only read.
- `PermissionChecker.canPerform` stub left untouched (still throws `UnsupportedOperationException`), so the tests compile and currently fail red, ready for the permission logic to be implemented next.