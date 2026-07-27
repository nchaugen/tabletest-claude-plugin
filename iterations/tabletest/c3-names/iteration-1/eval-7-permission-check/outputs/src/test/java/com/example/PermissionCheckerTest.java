package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionCheckerTest {

    private final PermissionChecker checker = new PermissionChecker();

    @TableTest("""
        Scenario                          | Role  | Action                   | Allowed?
        Admin role, any action            | ADMIN | {READ, WRITE, DELETE}    | true
        User role, read or write action   | USER  | {READ, WRITE}            | true
        User role, delete action          | USER  | DELETE                   | false
        Guest role, read action           | GUEST | READ                     | true
        Guest role, write or delete action| GUEST | {WRITE, DELETE}          | false
        """)
    void checksPermissionByRoleAndAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, checker.canPerform(role, action));
    }
}
