"""Table-driven tests for evaluate_application, written ahead of the implementation.

Assumption: the rules only ever say DECLINED for restricted destinations, and never
say what happens to an applicant aged 70+ without medical clearance (short trip,
unrestricted destination). We treat MANUAL_REVIEW as the catch-all for anything not
explicitly covered by an APPROVED or DECLINED rule, so that case resolves to
MANUAL_REVIEW rather than DECLINED.
"""

import pytest

from travel_policy import Decision, evaluate_application


@pytest.mark.parametrize("age", [40, 90])
@pytest.mark.parametrize("trip_days", [30, 120])
@pytest.mark.parametrize("has_medical_clearance", [True, False])
def test_restricted_destination_declines_regardless_of_age_trip_length_and_clearance(
    age, trip_days, has_medical_clearance
):
    decision = evaluate_application(
        age=age,
        trip_days=trip_days,
        destination_restricted=True,
        has_medical_clearance=has_medical_clearance,
    )

    assert decision == Decision.DECLINED


@pytest.mark.parametrize("age", [40, 90])
@pytest.mark.parametrize("has_medical_clearance", [True, False])
def test_trip_over_ninety_days_goes_to_manual_review_regardless_of_age_and_clearance(
    age, has_medical_clearance
):
    decision = evaluate_application(
        age=age,
        trip_days=120,
        destination_restricted=False,
        has_medical_clearance=has_medical_clearance,
    )

    assert decision == Decision.MANUAL_REVIEW


@pytest.mark.parametrize(
    ("trip_days", "decision"),
    [
        pytest.param(90, Decision.APPROVED, id="at the trip length limit"),
        pytest.param(91, Decision.MANUAL_REVIEW, id="just over the trip length limit"),
    ],
)
def test_trip_length_threshold_for_an_applicant_under_the_age_limit(trip_days, decision):
    result = evaluate_application(
        age=40,
        trip_days=trip_days,
        destination_restricted=False,
        has_medical_clearance=False,
    )

    assert result == decision


@pytest.mark.parametrize(
    ("age", "has_medical_clearance", "decision"),
    [
        pytest.param(30, False, Decision.APPROVED, id="well under the age limit without medical clearance"),
        pytest.param(30, True, Decision.APPROVED, id="well under the age limit with medical clearance"),
        pytest.param(69, False, Decision.APPROVED, id="just under the age limit"),
        pytest.param(70, True, Decision.APPROVED, id="at the age limit with medical clearance"),
        pytest.param(70, False, Decision.MANUAL_REVIEW, id="at the age limit without medical clearance"),
        pytest.param(95, True, Decision.APPROVED, id="well over the age limit with medical clearance"),
        pytest.param(95, False, Decision.MANUAL_REVIEW, id="well over the age limit without medical clearance"),
    ],
)
def test_decision_by_age_and_medical_clearance_for_a_trip_within_the_length_limit(
    age, has_medical_clearance, decision
):
    result = evaluate_application(
        age=age,
        trip_days=30,
        destination_restricted=False,
        has_medical_clearance=has_medical_clearance,
    )

    assert result == decision
