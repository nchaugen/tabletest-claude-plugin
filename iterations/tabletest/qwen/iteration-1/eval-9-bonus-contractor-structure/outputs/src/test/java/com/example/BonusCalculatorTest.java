package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.*;

class BonusCalculatorTest {

      @TableTest("""
        Level            | Department       | Bonus?
        SENIOR           | SALES            | 15.0
        SENIOR           | ENGINEERING      | 12.0
        JUNIOR           | SALES            | 8.0
        JUNIOR           | ENGINEERING      | 5.0
        CONTRACTOR       | SALES            | 0.0
        CONTRACTOR       | ENGINEERING      | 0.0
          """)
    void calculateBonusPercentage(Level level, Department department, double bonus) {
        Employee employee = new Employee(level, department);
        assertEquals(bonus, new BonusCalculator().calculateBonusPercentage(employee), 0.0);
      }
}
