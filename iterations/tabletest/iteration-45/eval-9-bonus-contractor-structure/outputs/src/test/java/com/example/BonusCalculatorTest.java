package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Calculates bonus percentage from level and department")
    @Description("""
        Bonus percentage is returned as a whole number, e.g. 15 for 15%, not a fraction.
        """)
    @TableTest("""
        Scenario                          | Level      | Department           | Bonus %?
        Senior in sales                   | SENIOR     | SALES                | 15
        Senior in engineering             | SENIOR     | ENGINEERING          | 12
        Junior in sales                   | JUNIOR     | SALES                | 8
        Junior in engineering             | JUNIOR     | ENGINEERING          | 5
        Contractor, regardless of dept    | CONTRACTOR | {SALES, ENGINEERING} | 0
        """)
    void calculatesBonusPercentage(Level level, Department department, double bonusPercent) {
        Employee employee = new Employee(level, department);

        assertEquals(bonusPercent, calculator.calculateBonusPercentage(employee));
    }
}
