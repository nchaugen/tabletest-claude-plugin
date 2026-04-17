A Gradle Java project has been created for this feature.

Under `src/main/java/com/example`, the project already contains a stub `PermissionChecker` plus `Role` and `Action`. The permission logic is not implemented yet.

We want to write tests before implementing the permission checks.

Write TableTests for `PermissionChecker.canPerform(...)`.

The rules are:
- ADMIN can perform any action
- USER can READ and WRITE but not DELETE
- GUEST can only READ

It is OK to keep a stub implementation for the checker so the tests compile, but do not implement the permission logic yet.
