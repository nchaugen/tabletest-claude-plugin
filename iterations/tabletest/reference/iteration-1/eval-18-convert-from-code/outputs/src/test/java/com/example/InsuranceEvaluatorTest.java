package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Decides an application from the applicant type and their claim history")
    @Description("""
        A renewal with a clean claim history is settled without assessment. Every other
        application is assessed on prior claims and refused above the claim limit. Age is held
        at 40 except where a row varies it deliberately, which is how the table shows that the
        limit is about claims and not about age.
        """)
    @TableTest("""
        Scenario                           | Applicant Type | Age        | Claims | Decision?
        New applicant with a clean history | NEW            | 40         | 0      | APPROVED
        New applicant at the claim limit   | NEW            | {40, 100}  | 4      | APPROVED
        Applicant over the claim limit     | {NEW, RENEWAL} | {40, 100}  | 5      | REJECTED
        Renewal with a clean history       | RENEWAL        | 40         | 0      | AUTO_APPROVED
        Renewal with a claim               | RENEWAL        | 40         | 1      | APPROVED
        """)
    void decidesTheApplication(String applicantType, int age, int claims, String decision) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claims);
        assertEquals(decision, result.decision());
    }

    @DisplayName("Prices an approved application from the applicant's age and claim history")
    @Description("""
        Each prior claim adds a fixed amount, and that amount is larger once the applicant
        reaches senior age — which is why the claim run appears at both ages. Age is banded
        by decade rather than counted: every applicant in one decade pays the same, the price
        steps at each decade edge, and it steps again, far harder, at the senior threshold.
        Applicant type is held at NEW throughout, because it does not price an application
        that has already been approved. The figures are the observable prices, not the
        arithmetic behind them.
        """)
    @TableTest("""
        Scenario                           | Age      | Claims | Premium?
        Applicant in their thirties        | {30, 39} | 0      | 106.0
        Applicant in their forties         | {40, 49} | 0      | 108.0
        Standard applicant, one claim      | 30       | 1      | 136.0
        Standard applicant, a second claim | 30       | 2      | 166.0
        Just below senior age              | 64       | 0      | 112.0
        Senior applicant, clean history    | 65       | 0      | 221.0
        Senior applicant, one claim        | 65       | 1      | 273.5
        Senior applicant, a second claim   | 65       | 2      | 326.0
        """)
    void pricesAnApprovedApplication(int age, int claims, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claims);
        assertEquals(premium, result.premium());
    }
}
