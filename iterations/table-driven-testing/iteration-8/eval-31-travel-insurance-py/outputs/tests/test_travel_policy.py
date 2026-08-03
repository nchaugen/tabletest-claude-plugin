import pytest

from travel_policy import Decision, evaluate_application


@pytest.mark.parametrize(
    ("age", "has_medical_clearance", "decision"),
    [
        pytest.param(69, False, Decision.APPROVED, id="just under the age threshold without clearance"),
        pytest.param(69, True, Decision.APPROVED, id="just under the age threshold with clearance"),
        pytest.param(70, False, Decision.DECLINED, id="at the age threshold without clearance"),
        pytest.param(70, True, Decision.APPROVED, id="at the age threshold with clearance"),
    ],
)
def test_approves_based_on_age_and_medical_clearance(age, has_medical_clearance, decision):
    """Trip is held at 30 days and the destination unrestricted -- neither is this rule's concern."""
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
    ("trip_days", "age", "decision"),
    [
        pytest.param(90, 30, Decision.APPROVED, id="at the trip length limit"),
        pytest.param(91, 30, Decision.MANUAL_REVIEW, id="just past the trip length limit"),
        pytest.param(91, 80, Decision.MANUAL_REVIEW, id="past the trip length limit for an applicant over 70"),
    ],
)
def test_sends_long_trips_to_manual_review_regardless_of_age(trip_days, age, decision):
    """Destination is held unrestricted and clearance held absent -- neither is this rule's concern."""
    assert (
        evaluate_application(
            age=age,
            trip_days=trip_days,
            destination_restricted=False,
            has_medical_clearance=False,
        )
        == decision
    )


@pytest.mark.parametrize("has_medical_clearance", [True, False])
@pytest.mark.parametrize("trip_days", [30, 120])
@pytest.mark.parametrize("age", [30, 80])
def test_declines_restricted_destinations_regardless_of_age_trip_length_and_clearance(
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
