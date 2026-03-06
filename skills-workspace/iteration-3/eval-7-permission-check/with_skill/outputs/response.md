# TableTest for `canPerform(Role role, Action action)`

The permission rules are:
- ADMIN can perform any action
- USER can READ and WRITE but not DELETE
- GUEST can only READ

Rather than enumerate all 9 role/action combinations individually, we can use **value sets** to express "regardless of" relationships. For example, ADMIN can perform `{READ, WRITE, DELETE}` — the action doesn't matter. Similarly, USER's allowed actions (`{READ, WRITE}`) and disallowed action (DELETE) can be expressed in just two rows.

This gives us 5 rows that generate 9 test cases total via expansion:

```java
@TableTest("""
    Scenario                        | Role  | Action                 | Allowed?
    Admin can perform any action    | ADMIN | {READ, WRITE, DELETE}  | true
    User can read                   | USER  | READ                   | true
    User can write                  | USER  | WRITE                  | true
    User cannot delete              | USER  | DELETE                 | false
    Guest can only read             | GUEST | READ                   | true
    Guest cannot write              | GUEST | WRITE                  | false
    Guest cannot delete             | GUEST | DELETE                 | false
    """)
void permissionCheck(Role role, Action action, boolean allowed) {
    assertEquals(allowed, permissions.canPerform(role, action));
}
```

**Applying value sets more aggressively**, since the skill's `references/value-sets.md` shows we can use them to express "regardless of" rules, the ADMIN row already compresses 3 cases into 1. We could further compress GUEST's denied actions:

```java
@TableTest("""
    Scenario                          | Role  | Action                | Allowed?
    Admin can perform any action      | ADMIN | {READ, WRITE, DELETE} | true
    User can read and write           | USER  | {READ, WRITE}         | true
    User cannot delete                | USER  | DELETE                | false
    Guest can only read               | GUEST | READ                  | true
    Guest cannot write or delete      | GUEST | {WRITE, DELETE}       | false
    """)
void permissionCheck(Role role, Action action, boolean allowed) {
    assertEquals(allowed, permissions.canPerform(role, action));
}
```

This version has **5 rows** and generates 9 test cases via expansion — one row per meaningful rule, not one row per combination.

The scenario column describes the condition (which role, and what the rule is), not the outcome. The `Allowed?` column signals it is an expectation. No `if`/`switch` logic is needed in the test method body — the assertion is uniform across all rows.
