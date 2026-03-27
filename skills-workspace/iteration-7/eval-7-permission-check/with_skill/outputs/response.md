```java
@TableTest("""
    Scenario              | Role  | Action               | Can perform?
    ADMIN can do anything | ADMIN | {READ, WRITE, DELETE} | true
    USER can read         | USER  | READ                  | true
    USER can write        | USER  | WRITE                 | true
    USER cannot delete    | USER  | DELETE                | false
    GUEST can read        | GUEST | READ                  | true
    GUEST cannot write    | GUEST | {WRITE, DELETE}       | false
    """)
void permissionCheck(Role role, Action action, boolean canPerform) {
    assertEquals(canPerform, permissions.canPerform(role, action));
}
```

Key design choices:

- **ADMIN row** uses `{READ, WRITE, DELETE}` — all three actions produce `true`, so a value set expresses "regardless of action" cleanly (3 tests from one row).
- **GUEST row** for denied actions uses `{WRITE, DELETE}` — both produce `false`, same reasoning.
- **USER** needs two separate `true` rows because READ and WRITE are distinct allowed actions, and one `false` row for DELETE.
- Enums (`Role`, `Action`) are converted automatically by JUnit — no `@TypeConverter` needed.