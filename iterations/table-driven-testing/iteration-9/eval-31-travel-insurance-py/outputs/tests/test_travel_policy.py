import pytest

from travel_policy import Decision, evaluate_application

# Assumption: the rules state that applicants aged 70+ are approved only with
# medical clearance, but don't say what happens otherwise. We take the most
# direct reading -- failing that eligibility check declines the application,
# it does not go to manual review.


@pytest.mark.parametrize(
    ("age", "trip_days", "has_medical_clearance"),
    [
        pytest.param(30, 45, False, id="young applicant on a short trip"),
        pytest.param(75, 120, False, id="older applicant on a long trip"),
    ],
)
def test_declines_applications_for_restricted_destinations(age, trip_days, has_medical_clearance):
    decision = evaluate_application(
        age=age,
        trip_days=trip_days,
        destination_restricted=True,
        has_medical_clearance=has_medical_clearance,
    )

    assert decision == Decision.DECLINED


@pytest.mark.parametrize(
    ("trip_days", "age", "has_medical_clearance", "decision"),
    [
        pytest.param(90, 30, False, Decision.APPROVED, id="trip at the 90-day limit, young applicant"),
        pytest.param(91, 30, False, Decision.MANUAL_REVIEW, id="trip one day over the limit, young applicant"),
        pytest.param(
            91, 75, True, Decision.MANUAL_REVIEW,
            id="trip one day over the limit, older applicant with clearance",
        ),
        pytest.param(
            91, 75, False, Decision.MANUAL_REVIEW,
            id="trip one day over the limit, older applicant without clearance",
        ),
    ],
)
def test_sends_overlong_trips_to_manual_review_regardless_of_age(trip_days, age, has_medical_clearance, decision):
    result = evaluate_application(
        age=age,
        trip_days=trip_days,
        destination_restricted=False,
        has_medical_clearance=has_medical_clearance,
    )

    assert result == decision


@pytest.mark.parametrize(
    ("age", "has_medical_clearance", "decision"),
    [
        pytest.param(69, False, Decision.APPROVED, id="just under the age threshold, no clearance"),
        pytest.param(69, True, Decision.APPROVED, id="just under the age threshold, with clearance"),
        pytest.param(70, True, Decision.APPROVED, id="at the age threshold, with clearance"),
        pytest.param(70, False, Decision.DECLINED, id="at the age threshold, no clearance"),
    ],
)
def test_approves_applications_by_age_and_medical_clearance(age, has_medical_clearance, decision):
    result = evaluate_application(
        age=age,
        trip_days=30,
        destination_restricted=False,
        has_medical_clearance=has_medical_clearance,
    )

    assert result == decision
