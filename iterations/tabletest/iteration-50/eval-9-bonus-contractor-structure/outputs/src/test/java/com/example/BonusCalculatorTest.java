package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Calculates the bonus percentage from level and department")
    @Description("""
        Bonus Percentage? is expressed in percentage points (15.0 means 15%),
        not as a fraction (0.15).
        """)
    @TableTest("""
        Scenario                   | Level      | Department            | Bonus Percentage?
        Senior in sales            | SENIOR     | SALES                 | 15.0
        Senior in engineering      | SENIOR     | ENGINEERING           | 12.0
        Junior in sales            | JUNIOR     | SALES                 | 8.0
        Junior in engineering      | JUNIOR     | ENGINEERING           | 5.0
        Contractor, any department | CONTRACTOR | {SALES, ENGINEERING}  | 0.0
        """)
    void calculatesBonusPercentage(Level level, Department department, double bonusPercentage) {
        Employee employee = new Employee(level, department);
        assertEquals(bonusPercentage, calculator.calculateBonusPercentage(employee), 0.0001);
    }
}
