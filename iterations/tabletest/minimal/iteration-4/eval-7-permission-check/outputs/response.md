No existing project files — writing the test standalone. The permission rules map cleanly to a single `@TableTest`: each row is a (role, action) combination with a boolean result.

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
    void permissionsByRoleAndAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, canPerform(role, action));
    }
}
```

Key design decisions:
- **Enums convert natively** — `Role` and `ADMIN`/`USER`/`GUEST` map directly; same for `Action`. No `@TypeConverter` needed.
- **All 9 combinations covered** — ADMIN×3 + USER×3 + GUEST×3. No combinatorial explosion because each rule has a distinct result per cell.
- **`boolean` not `Boolean`** — no null cases exist here, so primitives are fine.
- **Scenario names describe conditions** (`User cannot delete`), not outcomes (`Returns false`).

If you want to make the ADMIN rows more compact since role always wins regardless of action, you could use a value set:

```java
Admin can perform any action  | ADMIN | {READ, WRITE, DELETE} | true
```

That generates 3 tests from one row, and communicates "regardless of action" semantically.