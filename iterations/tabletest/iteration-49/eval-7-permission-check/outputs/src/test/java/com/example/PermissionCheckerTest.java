package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckerTest {

    private final PermissionChecker checker = new PermissionChecker();

    @DisplayName("Determines whether a role may perform an action")
    @TableTest("""
        Scenario                        | Role  | Action                | Allowed?
        Admin performs any action       | ADMIN | {READ, WRITE, DELETE} | true
        User reads or writes            | USER  | {READ, WRITE}         | true
        User attempts delete            | USER  | DELETE                | false
        Guest reads                     | GUEST | READ                  | true
        Guest attempts write or delete  | GUEST | {WRITE, DELETE}       | false
        """)
    void determinesWhetherRoleMayPerformAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, checker.canPerform(role, action));
    }
}
