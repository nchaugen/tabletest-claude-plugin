Working through the design:

**Concerns:** One — whether a role can perform an action.

**Value sets:** ADMIN gets `{READ, WRITE, DELETE}` (all true — genuinely "regardless of action"). GUEST gets `{WRITE, DELETE}` (both false). USER needs separate rows because READ/WRITE→true but DELETE→false.

JUnit converts enums and booleans natively, so no `@TypeConverter` needed.

```java
public class PermissionTest {

    @DisplayName("Role-based action permissions")
    @TableTest("""
        Scenario                     | Role  | Action                  | Allowed?
        Admin can perform any action | ADMIN | {READ, WRITE, DELETE}   | true
        User can read                | USER  | READ                    | true
        User can write               | USER  | WRITE                   | true
        User cannot delete           | USER  | DELETE                  | false
        Guest can read               | GUEST | READ                    | true
        Guest cannot write or delete | GUEST | {WRITE, DELETE}         | false
        """)
    void permissionCheck(Role role, Action action, boolean allowed) {
        assertEquals(allowed, canPerform(role, action));
    }
}
```

Key decisions:
- `{READ, WRITE, DELETE}` for ADMIN asserts the rule explicitly: *any* action is permitted, not just the three listed
- `{WRITE, DELETE}` for GUEST similarly groups the two denied actions — same outcome, so value set is valid
- USER gets individual rows because READ/WRITE and DELETE produce different results — a value set would be wrong there
- No `@Description` needed; the table is self-explanatory