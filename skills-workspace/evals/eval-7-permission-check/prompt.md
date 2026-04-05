Write a TableTest for a permission check method:

```java
boolean canPerform(Role role, Action action)
```

The rules are:
- ADMIN can perform any action
- USER can READ and WRITE but not DELETE
- GUEST can only READ
