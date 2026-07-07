package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionCheckerTest {

    @TableTest("""
        Scenario                     | Role  | Action                | Allowed?
        Admin can perform any action | ADMIN | {READ, WRITE, DELETE} | true
        User can read                | USER  | READ                  | true
        User can write               | USER  | WRITE                 | true
        User cannot delete           | USER  | DELETE                | false
        Guest can read                | GUEST | READ                 | true
        Guest cannot write           | GUEST | WRITE                 | false
        Guest cannot delete          | GUEST | DELETE                | false
        """)
    void canPerform(Role role, Action action, boolean allowed) {
        assertEquals(allowed, new PermissionChecker().canPerform(role, action));
    }
}
