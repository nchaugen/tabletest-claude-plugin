package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.*;

public class PermissionCheckerTest {

    private PermissionChecker checker;

     @BeforeEach
    void setUp() {
        checker = new PermissionChecker();
     }

     // Table 1 of 2: Positive permissions — which roles are allowed to perform actions.
     @TableTest("""
        Scenario                   | Role    | Action   | Expected?
         ADMIN reads a resource    | ADMIN   | READ     | true
         ADMIN writes a resource   | ADMIN   | WRITE    | true
         ADMIN deletes a resource  | ADMIN   | DELETE   | true
         USER reads a resource     | USER    | READ     | true
         USER writes a resource    | USER    | WRITE    | true
         GUEST reads a resource    | GUEST   | READ     | true
          """)
    void shouldGrantPermission(Role role, Action action, boolean expected) {
        assertEquals(expected, checker.canPerform(role, action));
     }

     // Table 2 of 2: Denials — actions a role does not have access to.
     @TableTest("""
         Scenario                   | Role    | Action   | Expected?
          USER deletes a resource   | USER    | DELETE   | false
          GUEST writes a resource   | GUEST   | WRITE    | false
          GUEST deletes a resource  | GUEST   | DELETE   | false
           """)
    void shouldDenyPermission(Role role, Action action, boolean expected) {
        assertEquals(expected, checker.canPerform(role, action));
     }
}
