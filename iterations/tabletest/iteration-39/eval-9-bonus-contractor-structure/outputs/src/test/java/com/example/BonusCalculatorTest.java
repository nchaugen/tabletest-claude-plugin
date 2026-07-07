package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Bonus percentage calculation")
    @Description("""
        Bonus percentage is determined by level and department. Contractors always
        receive 0%, regardless of department. Assumption: calculateBonusPercentage
        returns the percentage as a plain number (e.g. 15.0 for 15%), not a fraction
        (0.15) - not specified in the feature description.
        """)
    @TableTest("""
        Scenario                 | Level      | Department            | Bonus Percentage?
        Senior in sales          | SENIOR     | SALES                 | 15.0
        Senior in engineering    | SENIOR     | ENGINEERING           | 12.0
        Junior in sales          | JUNIOR     | SALES                 | 8.0
        Junior in engineering    | JUNIOR     | ENGINEERING           | 5.0
        Contractor gets no bonus | CONTRACTOR | {SALES, ENGINEERING}  | 0.0
        """)
    void calculatesBonusPercentage(Level level, Department department, double bonusPercentage) {
        Employee employee = new Employee(level, department);

        assertEquals(bonusPercentage, calculator.calculateBonusPercentage(employee), 0.0001);
    }
}
