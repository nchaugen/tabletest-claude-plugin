Working through the analysis steps from the reference:

**Concern**: "Check whether a role can perform an action" — one concern, one table.

**Variations & value sets**:
- ADMIN: action is irrelevant → `{READ, WRITE, DELETE}` → true
- USER: READ and WRITE allowed → `{READ, WRITE}` → true; DELETE denied
- GUEST: only READ allowed; WRITE and DELETE denied → `{WRITE, DELETE}` → false

JUnit converts `Role` and `Action` enums automatically — no `@TypeConverter` needed. No `@Description` needed as the table is fully self-explanatory.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionTest {

    @DisplayName("Role permission checks")
    @TableTest("""
        Scenario                     | Role  | Action               | Allowed?
        Admin can perform any action | ADMIN | {READ, WRITE, DELETE} | true
        User can read                | USER  | READ                 | true
        User can write               | USER  | WRITE                | true
        User cannot delete           | USER  | DELETE               | false
        Guest can read               | GUEST | READ                 | true
        Guest cannot write or delete | GUEST | {WRITE, DELETE}      | false
        """)
    void canPerformAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, canPerform(role, action));
    }
}
```

Key design decisions:
- **Value set on ADMIN row** (`{READ, WRITE, DELETE}`) asserts the action is irrelevant — more precise than three separate rows
- **Value set on GUEST deny row** (`{WRITE, DELETE}`) groups the two disallowed actions into one row
- **Separate rows for USER READ/WRITE** because they could diverge independently, making each rule explicit