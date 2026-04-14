package com.example;

public class InsuranceEvaluator {

    private boolean hasActivePolicy;
    private int internalRiskScore;

    public EvaluationResult evaluateApplication(String applicantType, int age, int claimCount) {
        internalRiskScore = calculateRiskScore(age, claimCount);
        hasActivePolicy = checkPolicyDatabase(applicantType);

        if (applicantType.equals("RENEWAL") && claimCount == 0) {
            return new EvaluationResult("AUTO_APPROVED", 0);
        }

        if (internalRiskScore > 75) {
            return new EvaluationResult("REJECTED", 0);
        }

        if (age >= 65) {
            double premium = calculateSeniorPremium(internalRiskScore);
            return new EvaluationResult("APPROVED", premium);
        }

        double premium = calculateStandardPremium(internalRiskScore);
        return new EvaluationResult("APPROVED", premium);
    }

    private int calculateRiskScore(int age, int claimCount) {
        return (age / 10) + (claimCount * 15);
    }

    private double calculateSeniorPremium(int riskScore) {
        return 200.0 + (riskScore * 3.5);
    }

    private double calculateStandardPremium(int riskScore) {
        return 100.0 + (riskScore * 2.0);
    }

    private boolean checkPolicyDatabase(String type) {
        // database lookup
        return type.equals("RENEWAL");
    }
}
