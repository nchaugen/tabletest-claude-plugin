The logic maps cleanly to a table: `Role` × `Action` → `boolean`. ADMIN, USER, and GUEST rows with value sets where multiple actions share the same result.

```java
import org.junit.jupiter.api.Assertions.assertEquals;
import org.tabletest.junit.TableTest;

class PermissionTest {

    @TableTest("""
        Scenario              | Role  | Action                | Allowed?
        ADMIN unrestricted    | ADMIN | {READ, WRITE, DELETE} | true
        USER read or write    | USER  | {READ, WRITE}         | true
        USER no delete        | USER  | DELETE                | false
        GUEST read only       | GUEST | READ                  | true
        GUEST no write/delete | GUEST | {WRITE, DELETE}       | false
        """)
    void shouldEnforcePermissions(Role role, Action action, boolean allowed) {
        assertEquals(allowed, canPerform(role, action));
    }
}
```

A few things worth noting:

**Value sets** are used where multiple actions produce the same result for a role — `{READ, WRITE, DELETE}` for ADMIN (all `true`), `{READ, WRITE}` for USER (all `true`), and `{WRITE, DELETE}` for GUEST (all `false`). Each generates a separate test invocation.

**JUnit converts enums automatically**, so `ADMIN`, `USER`, `GUEST`, `READ`, `WRITE`, `DELETE` map straight to your enum types without a `@TypeConverter`.

**5 rows → 9 test cases** after value set expansion:
- ADMIN×READ, ADMIN×WRITE, ADMIN×DELETE (true)
- USER×READ, USER×WRITE (true), USER×DELETE (false)
- GUEST×READ (true), GUEST×WRITE, GUEST×DELETE (false)