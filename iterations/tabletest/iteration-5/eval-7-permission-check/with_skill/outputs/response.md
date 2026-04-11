```java
@TableTest("""
    Scenario              | Role  | Action            | Allowed?
    Admin can do anything | ADMIN | {READ, WRITE, DELETE} | true
    User can read         | USER  | READ              | true
    User can write        | USER  | WRITE             | true
    User cannot delete    | USER  | DELETE            | false
    Guest can read        | GUEST | READ              | true
    Guest cannot write    | GUEST | WRITE             | false
    Guest cannot delete   | GUEST | DELETE            | false
    """)
void canPerform(Role role, Action action, boolean allowed) {
    assertEquals(allowed, permissions.canPerform(role, action));
}
```

The ADMIN row uses a value set `{READ, WRITE, DELETE}` — all three generate separate tests each asserting `true`, expressing "ADMIN wins regardless of action" without listing three near-identical rows.