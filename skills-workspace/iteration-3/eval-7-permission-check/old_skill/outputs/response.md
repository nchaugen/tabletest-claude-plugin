# TableTest for `canPerform(Role role, Action action)`

Here is a TableTest covering the permission rules for all role/action combinations.

```java
@TableTest("""
    Scenario                  | Role  | Action | Can Perform?
    Admin can do anything     | ADMIN | READ   | true
    Admin can write           | ADMIN | WRITE  | true
    Admin can delete          | ADMIN | DELETE | true
    User can read             | USER  | READ   | true
    User can write            | USER  | WRITE  | true
    User cannot delete        | USER  | DELETE | false
    Guest can read            | GUEST | READ   | true
    Guest cannot write        | GUEST | WRITE  | false
    Guest cannot delete       | GUEST | DELETE | false
    """)
void testCanPerform(String scenario, Role role, Action action, boolean canPerform) {
    assertThat(permissionService.canPerform(role, action)).isEqualTo(canPerform);
}
```

### Notes

- Each row represents one role/action pair and its expected outcome.
- The `Scenario` column documents the intent of each row.
- The `Can Perform?` column uses `true`/`false` to match the boolean return type of `canPerform`.
- This exhaustively covers all 3 roles × 3 actions = 9 combinations, making the permission matrix explicit and easy to audit.
