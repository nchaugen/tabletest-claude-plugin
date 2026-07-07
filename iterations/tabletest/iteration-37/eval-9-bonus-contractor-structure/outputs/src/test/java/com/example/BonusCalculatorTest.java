package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusCalculatorTest {

    @TableTest("""
        Scenario                            | Level      | Department  | Bonus Percentage?
        Senior in sales                     | SENIOR     | SALES       | 15.0
        Senior in engineering               | SENIOR     | ENGINEERING | 12.0
        Junior in sales                     | JUNIOR     | SALES       | 8.0
        Junior in engineering               | JUNIOR     | ENGINEERING | 5.0
        Contractor, department doesn't matter | CONTRACTOR | {SALES, ENGINEERING} | 0.0
        """)
    void calculatesBonusPercentage(Level level, Department department, double bonusPercentage) {
        BonusCalculator calculator = new BonusCalculator();
        Employee employee = new Employee(level, department);

        assertEquals(bonusPercentage, calculator.calculateBonusPercentage(employee));
    }
}
