No build file found — I'll write the test as a standalone example. Based on the rules:

- `ADMIN` → any action (READ, WRITE, DELETE)
- `USER` → READ, WRITE (not DELETE)
- `GUEST` → READ only

The permissions vary by role × action, which is a classic decision table:

```java
import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionTest {

    @TableTest("""
        Scenario              | role  | action | Allowed?
        Admin can read        | ADMIN | READ   | true
        Admin can write       | ADMIN | WRITE  | true
        Admin can delete      | ADMIN | DELETE | true
        User can read         | USER  | READ   | true
        User can write        | USER  | WRITE  | true
        User cannot delete    | USER  | DELETE | false
        Guest can read        | GUEST | READ   | true
        Guest cannot write    | GUEST | WRITE  | false
        Guest cannot delete   | GUEST | DELETE | false
        """)
    void canPerform(Role role, Action action, boolean allowed) {
        assertEquals(allowed, permissions.canPerform(role, action));
    }
}
```

A few design notes:
- `Role` and `Action` are enums, so JUnit's built-in conversion handles them — no `@TypeConverter` needed.
- Each row is a distinct decision point (role × action combination), which is the right structure for permission logic.
- Value sets could collapse ADMIN's rows into one (`ADMIN | {READ, WRITE, DELETE} | true`), but explicit rows make failures easier to diagnose and keep the table readable as a permission matrix.