```java
@TableTest("""
    Scenario                        | Role  | Action              | Can perform?
    ADMIN can do anything           | ADMIN | {READ, WRITE, DELETE} | true
    USER can read                   | USER  | READ                | true
    USER can write                  | USER  | WRITE               | true
    USER cannot delete              | USER  | DELETE              | false
    GUEST can only read             | GUEST | READ                | true
    GUEST cannot write or delete    | GUEST | {WRITE, DELETE}     | false
    """)
@Description("Permission matrix: ADMIN is unrestricted; USER may read and write; GUEST is read-only")
void canPerform(Role role, Action action, boolean canPerform) {
    assertEquals(canPerform, permissions.canPerform(role, action));
}
```

Key design choices:

- **Value sets** on ADMIN's row (`{READ, WRITE, DELETE}`) and GUEST's deny row (`{WRITE, DELETE}`) collapse what would be 5 extra rows into concise "regardless of action" assertions — valid because the result is identical for every value in the set.
- USER's three actions are separate rows since DELETE produces a different result than READ/WRITE.
- `@Description` captures the policy rule so the table doesn't need to be self-explanatory on every detail.