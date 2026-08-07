import pytest

from travel_policy import Decision, evaluate_application


# Assumption: a senior applicant (70+) without medical clearance is DECLINED —
# the spec says such applicants are "approved only if" cleared, but names no
# other outcome; MANUAL_REVIEW is reserved for the trip-length rule, so the
# only remaining decision for a non-approval here is DECLINED.


@pytest.mark.parametrize(
    ("age", "trip_days", "has_medical_clearance"),
    [
        pytest.param(30, 10, False, id="young applicant, short trip, no clearance"),
        pytest.param(75, 120, True, id="senior applicant, long trip, with clearance"),
    ],
)
def test_declines_restricted_destinations_regardless_of_age_trip_length_and_clearance(
    age, trip_days, has_medical_clearance
):
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
        pytest.param(90, 30, False, Decision.APPROVED, id="at the 90 day limit"),
        pytest.param(91, 30, False, Decision.MANUAL_REVIEW, id="just over the limit"),
        pytest.param(
            91, 75, True, Decision.MANUAL_REVIEW,
            id="just over the limit, senior applicant with clearance",
        ),
    ],
)
def test_sends_trips_over_90_days_to_manual_review_regardless_of_age(
    trip_days, age, has_medical_clearance, decision
):
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
        pytest.param(69, False, Decision.APPROVED, id="under 70 without clearance"),
        pytest.param(69, True, Decision.APPROVED, id="under 70 with clearance"),
        pytest.param(70, True, Decision.APPROVED, id="at 70 with clearance"),
        pytest.param(70, False, Decision.DECLINED, id="at 70 without clearance"),
    ],
)
def test_approves_applications_based_on_age_and_medical_clearance(
    age, has_medical_clearance, decision
):
    result = evaluate_application(
        age=age,
        trip_days=10,
        destination_restricted=False,
        has_medical_clearance=has_medical_clearance,
    )
    assert result == decision
