Let me trace the logic before designing the table.

**Decision paths in `evaluateApplication`:**
1. `RENEWAL` + 0 claims → `AUTO_APPROVED`, premium 0
2. Risk score > 75 → `REJECTED`, premium 0
3. Age ≥ 65 (and risk ≤ 75) → `APPROVED`, senior premium
4. Age < 65 (and risk ≤ 75) → `APPROVED`, standard premium

**Risk score formula:** `(age / 10) + (claimCount * 15)` (integer division on age)

Since `internalRiskScore` is private, I can't assert on it directly — but I'll document the formulas in `@Description` so premium values are traceable. Here's the test:

```java
class InsuranceEvaluatorTest {

    private InsuranceEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new InsuranceEvaluator();
    }

    @DisplayName("Application evaluation")
    @Description("""
        Risk score = (age / 10) + (claimCount × 15), integer division.
        Senior premium (age ≥ 65): 200.00 + (riskScore × 3.5).
        Standard premium (age < 65): 100.00 + (riskScore × 2.0).
        AUTO_APPROVED and REJECTED carry a premium of 0.
        """)
    @TableTest("""
        Scenario                    | Applicant type | Age | Claims | Decision?     | Premium?
        Renewal, no claims          | RENEWAL        | 30  | 0      | AUTO_APPROVED | 0.00
        Renewal with claims         | RENEWAL        | 30  | 1      | APPROVED      | 136.00
        High claim count, rejected  | NEW            | 40  | 5      | REJECTED      | 0.00
        Standard applicant          | NEW            | 40  | 0      | APPROVED      | 108.00
        Senior applicant            | NEW            | 70  | 0      | APPROVED      | 224.50
        Senior renewal with claims  | RENEWAL        | 70  | 2      | APPROVED      | 329.50
        """)
    void evaluatesApplication(String applicantType, int age, int claimCount,
                              String expectedDecision, double expectedPremium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertAll(
            () -> assertEquals(expectedDecision, result.getStatus()),
            () -> assertEquals(expectedPremium, result.getPremium(), 0.01)
        );
    }
}
```

**Verified expected values:**

| Scenario | Risk score | Decision | Premium calc |
|---|---|---|---|
| RENEWAL, 30, 0 claims | 3 | AUTO_APPROVED | 0 (auto path) |
| RENEWAL, 30, 1 claim | 18 | APPROVED | 100 + 18×2 = 136.00 |
| NEW, 40, 5 claims | 79 | REJECTED | 0 (79 > 75) |
| NEW, 40, 0 claims | 4 | APPROVED | 100 + 4×2 = 108.00 |
| NEW, 70, 0 claims | 7 | APPROVED | 200 + 7×3.5 = 224.50 |
| RENEWAL, 70, 2 claims | 37 | APPROVED | 200 + 37×3.5 = 329.50 |

Two things to confirm before running:
1. **`EvaluationResult` API** — I assumed `getStatus()` and `getPremium()`; adjust if the field names differ.
2. **`hasActivePolicy`** — this field is set in `evaluateApplication` but never read back in the method body, so it doesn't affect any output and doesn't need a column.