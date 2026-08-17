package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @Description("""
        Bonus Percentage? is the raw percentage value (15 means 15%), not a fraction.
        Contractors get 0% regardless of department, so department is a value set on that row.
        """)
    @TableTest("""
        Scenario                             | Level      | Department           | Bonus Percentage?
        Senior in sales                      | SENIOR     | SALES                | 15
        Senior in engineering                | SENIOR     | ENGINEERING          | 12
        Junior in sales                      | JUNIOR     | SALES                | 8
        Junior in engineering                | JUNIOR     | ENGINEERING          | 5
        Contractor, department is irrelevant | CONTRACTOR | {SALES, ENGINEERING} | 0
        """)
    void calculatesBonusPercentageFromLevelAndDepartment(Level level, Department department, double bonusPercentage) {
        Employee employee = new Employee(level, department);

        assertEquals(bonusPercentage, calculator.calculateBonusPercentage(employee));
    }
}
