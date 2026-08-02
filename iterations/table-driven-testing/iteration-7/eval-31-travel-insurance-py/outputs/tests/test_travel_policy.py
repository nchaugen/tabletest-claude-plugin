import pytest

from travel_policy import Decision, evaluate_application


@pytest.mark.parametrize(
    ("age", "has_medical_clearance", "expected_decision"),
    [
        pytest.param(45, True, Decision.APPROVED, id="well under the age threshold with clearance"),
        pytest.param(45, False, Decision.APPROVED, id="well under the age threshold without clearance"),
        pytest.param(69, False, Decision.APPROVED, id="just under the age threshold"),
        pytest.param(70, True, Decision.APPROVED, id="at the age threshold with medical clearance"),
        pytest.param(70, False, Decision.DECLINED, id="at the age threshold without medical clearance"),
    ],
)
def test_approves_by_age_and_medical_clearance(age, has_medical_clearance, expected_decision):
    """Trip is held at 30 days (within the 90-day limit) and the destination is unrestricted,
    since neither belongs to this rule's axis."""
    assert (
        evaluate_application(
            age=age,
            trip_days=30,
            destination_restricted=False,
            has_medical_clearance=has_medical_clearance,
        )
        == expected_decision
    )


@pytest.mark.parametrize(
    ("trip_days", "age", "has_medical_clearance", "expected_decision"),
    [
        pytest.param(90, 30, False, Decision.APPROVED, id="at the 90-day trip limit"),
        pytest.param(91, 30, False, Decision.MANUAL_REVIEW, id="just over the 90-day trip limit"),
        pytest.param(
            91, 85, False, Decision.MANUAL_REVIEW,
            id="over the trip limit for an older applicant without clearance",
        ),
    ],
)
def test_sends_trips_over_90_days_to_manual_review_regardless_of_age(
    trip_days, age, has_medical_clearance, expected_decision
):
    """Destination is held unrestricted, since that belongs to a different rule."""
    assert (
        evaluate_application(
            age=age,
            trip_days=trip_days,
            destination_restricted=False,
            has_medical_clearance=has_medical_clearance,
        )
        == expected_decision
    )


@pytest.mark.parametrize(
    ("age", "trip_days", "has_medical_clearance"),
    [
        pytest.param(30, 30, False, id="restricted destination for a young applicant on a short trip"),
        pytest.param(75, 30, True, id="restricted destination for an older applicant with medical clearance"),
        pytest.param(75, 30, False, id="restricted destination for an older applicant without medical clearance"),
        pytest.param(30, 120, False, id="restricted destination for a long trip"),
    ],
)
def test_declines_restricted_destinations_regardless_of_other_factors(age, trip_days, has_medical_clearance):
    """Each case would be APPROVED or MANUAL_REVIEW under the other rules; the restricted
    destination must override all of them."""
    assert (
        evaluate_application(
            age=age,
            trip_days=trip_days,
            destination_restricted=True,
            has_medical_clearance=has_medical_clearance,
        )
        == Decision.DECLINED
    )
