package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Calculates the bonus percentage from level and department")
    @Description("""
        Bonus Percentage? is the percentage value itself (e.g. 15 means 15%), not a fraction.
        Level and Department are separate columns, and Employee is assembled in the test method,
        because expressing "regardless of department" for CONTRACTOR as a value set requires
        Department to be its own column rather than part of a composite Employee cell.
        """)
    @TableTest("""
        Scenario                             | Level      | Department           | Bonus Percentage?
        Senior in sales                      | SENIOR     | SALES                | 15
        Senior in engineering                | SENIOR     | ENGINEERING          | 12
        Junior in sales                      | JUNIOR     | SALES                | 8
        Junior in engineering                | JUNIOR     | ENGINEERING          | 5
        Contractor, regardless of department | CONTRACTOR | {SALES, ENGINEERING} | 0
        """)
    void calculatesBonusPercentage(Level level, Department department, double bonusPercentage) {
        Employee employee = new Employee(level, department);

        assertEquals(bonusPercentage, calculator.calculateBonusPercentage(employee));
    }
}
