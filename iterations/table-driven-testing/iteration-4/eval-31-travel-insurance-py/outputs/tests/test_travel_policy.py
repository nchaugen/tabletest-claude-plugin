"""Table-driven tests for evaluate_application.

Assumption: the rules state approval conditions for applicants aged 70+
(medical clearance) and a manual-review trigger for long trips, but never
say what happens to a 70+ applicant without medical clearance on a trip of
90 days or less. These tests assume that case is DECLINED, since none of
the stated approval or manual-review conditions are met.
"""

import pytest

from travel_policy import Decision, evaluate_application


@pytest.mark.parametrize(
    ("age", "has_medical_clearance", "decision"),
    [
        pytest.param(69, False, Decision.APPROVED, id="under the age limit without clearance"),
        pytest.param(69, True, Decision.APPROVED, id="under the age limit with clearance"),
        pytest.param(70, True, Decision.APPROVED, id="at the age limit with clearance"),
        pytest.param(70, False, Decision.DECLINED, id="at the age limit without clearance"),
        pytest.param(71, True, Decision.APPROVED, id="just over the age limit with clearance"),
        pytest.param(71, False, Decision.DECLINED, id="just over the age limit without clearance"),
    ],
)
def test_eligibility_by_age_and_medical_clearance(age, has_medical_clearance, decision):
    assert (
        evaluate_application(
            age=age,
            trip_days=30,
            destination_restricted=False,
            has_medical_clearance=has_medical_clearance,
        )
        == decision
    )


@pytest.mark.parametrize(
    ("trip_days", "decision"),
    [
        pytest.param(90, Decision.APPROVED, id="at the trip length limit"),
        pytest.param(91, Decision.MANUAL_REVIEW, id="just over the trip length limit"),
    ],
)
def test_trip_length_threshold(trip_days, decision):
    assert (
        evaluate_application(
            age=30,
            trip_days=trip_days,
            destination_restricted=False,
            has_medical_clearance=False,
        )
        == decision
    )


@pytest.mark.parametrize("age", [69, 70, 71])
@pytest.mark.parametrize("has_medical_clearance", [True, False])
def test_long_trip_goes_to_manual_review_regardless_of_age_and_clearance(age, has_medical_clearance):
    assert (
        evaluate_application(
            age=age,
            trip_days=120,
            destination_restricted=False,
            has_medical_clearance=has_medical_clearance,
        )
        == Decision.MANUAL_REVIEW
    )


@pytest.mark.parametrize("age", [30, 80])
@pytest.mark.parametrize("trip_days", [30, 120])
@pytest.mark.parametrize("has_medical_clearance", [True, False])
def test_restricted_destination_declines_regardless_of_age_trip_length_and_clearance(
    age, trip_days, has_medical_clearance
):
    assert (
        evaluate_application(
            age=age,
            trip_days=trip_days,
            destination_restricted=True,
            has_medical_clearance=has_medical_clearance,
        )
        == Decision.DECLINED
    )
