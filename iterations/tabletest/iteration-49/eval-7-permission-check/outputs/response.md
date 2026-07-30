All 9 generated test cases run correctly (table parsing and value-set expansion work as designed) and fail only because `canPerform` is an unimplemented stub — exactly the expected TDD red state.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/PermissionCheckerTest.java`:

```java
@DisplayName("Determines whether a role may perform an action")
@TableTest("""
    Scenario                        | Role  | Action                | Allowed?
    Admin performs any action       | ADMIN | {READ, WRITE, DELETE} | true
    User reads or writes            | USER  | {READ, WRITE}         | true
    User attempts delete            | USER  | DELETE                | false
    Guest reads                     | GUEST | READ                  | true
    Guest attempts write or delete  | GUEST | {WRITE, DELETE}       | false
    """)
void determinesWhetherRoleMayPerformAction(Role role, Action action, boolean allowed) {
    assertEquals(allowed, checker.canPerform(role, action));
}
```

Design notes (assumptions, since the task gave no other detail):
- One table, one axis: role tier × action, since this is a single decision rule with two inputs — no need to split into multiple tables.
- Used value sets (`{READ, WRITE, DELETE}`, `{READ, WRITE}`, `{WRITE, DELETE}`) to group actions that produce the same outcome for a role, rather than writing all 9 combinations as separate rows — this expands to 9 generated test cases (verified above) while keeping the table at 5 rows.
- `Role`/`Action` convert automatically via built-in enum conversion — no custom converter needed.
- Left `PermissionChecker.canPerform` as the existing stub (throws `UnsupportedOperationException`); did not implement the rules. Ran the suite to confirm all 9 cases fail on the stub exception rather than a table/parsing error, confirming the test design is sound and ready for TDD.