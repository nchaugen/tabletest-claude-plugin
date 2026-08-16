package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BonusCalculatorTest {

    private final BonusCalculator calculator = new BonusCalculator();

    @DisplayName("Bonus rate follows the level, and the department only where the level lets it")
    @Description("""
        The rate is a whole percentage of base salary, so 15 means 15% and no salary is in scope
        here. Level and department are separate columns rather than one Employee cell because the
        contractor rule is a claim about department — that it is not read — and a value set can
        only vary a whole cell, never one part of one.
        """)
    @TableTest("""
        Scenario                      | Level      | Department           | Bonus %?
        Senior in sales               | SENIOR     | SALES                | 15
        Senior in engineering         | SENIOR     | ENGINEERING          | 12
        Junior in sales               | JUNIOR     | SALES                | 8
        Junior in engineering         | JUNIOR     | ENGINEERING          | 5
        Contractor, either department | CONTRACTOR | {SALES, ENGINEERING} | 0
        """)
    void ratesEmployeesByLevelAndDepartment(Level level, Department department, double bonusPercentage) {
        Employee employee = new Employee(level, department);

        assertEquals(bonusPercentage, calculator.calculateBonusPercentage(employee));
    }
}
