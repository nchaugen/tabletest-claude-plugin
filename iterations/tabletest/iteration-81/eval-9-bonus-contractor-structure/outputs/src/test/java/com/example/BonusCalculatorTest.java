package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @Description("""
        Level and Department are separate columns, rather than a single Employee cell,
        because the contractor row needs a value set on Department to state that the
        bonus does not depend on department for contractors. Bonus % is the whole
        percentage value (15 means 15%), not a fraction.
        """)
    @TableTest("""
        Scenario                   | Level      | Department           | Bonus %?
        Senior in sales            | SENIOR     | SALES                | 15
        Senior in engineering      | SENIOR     | ENGINEERING          | 12
        Junior in sales            | JUNIOR     | SALES                | 8
        Junior in engineering      | JUNIOR     | ENGINEERING          | 5
        Contractor, any department | CONTRACTOR | {SALES, ENGINEERING} | 0
        """)
    void calculatesBonusPercentageByLevelAndDepartment(Level level, Department department, double bonusPercentage) {
        Employee employee = new Employee(level, department);

        assertEquals(bonusPercentage, calculator.calculateBonusPercentage(employee));
    }
}
