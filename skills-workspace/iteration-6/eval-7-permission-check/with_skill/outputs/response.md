```java
@Description("ADMIN has full access; USER can read and write but not delete; GUEST is read-only")
@TableTest("""
    Scenario                    | Role  | Action                | Can perform?
    Admin can do anything       | ADMIN | {READ, WRITE, DELETE} | true
    User can read and write     | USER  | {READ, WRITE}         | true
    User cannot delete          | USER  | DELETE                | false
    Guest can read              | GUEST | READ                  | true
    Guest cannot write or delete| GUEST | {WRITE, DELETE}       | false
    """)
void shouldEnforcePermissions(Role role, Action action, boolean canPerform) {
    assertEquals(canPerform, permissionChecker.canPerform(role, action));
}
```

A few design notes:

- **Value sets** — `{READ, WRITE, DELETE}` on the ADMIN row generates 3 tests, all asserting `true`. Same applies to `{READ, WRITE}` and `{WRITE, DELETE}`. This is valid because the result is genuinely identical for every value in each set.
- **Enum conversion** — JUnit converts `Role` and `Action` enum values automatically; no `@TypeConverter` needed.
- **Coverage** — 9 total test cases (3 ADMIN + 2 USER-true + 1 USER-false + 1 GUEST-true + 2 GUEST-false) from 5 rows.