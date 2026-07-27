package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

class PermissionCheckerTest {

    private final PermissionChecker checker = new PermissionChecker();

    @DisplayName("Determines whether a role can perform an action")
    @TableTest("""
        Scenario                   | Role  | Action                | Can Perform?
        ADMIN performing any action| ADMIN | {READ, WRITE, DELETE} | true
        USER reading or writing    | USER  | {READ, WRITE}         | true
        USER deleting              | USER  | DELETE                | false
        GUEST reading              | GUEST | READ                  | true
        GUEST writing or deleting  | GUEST | {WRITE, DELETE}       | false
        """)
    void determinesWhetherRoleCanPerformAction(Role role, Action action, boolean canPerform) {
        assertEquals(canPerform, checker.canPerform(role, action));
    }
}
