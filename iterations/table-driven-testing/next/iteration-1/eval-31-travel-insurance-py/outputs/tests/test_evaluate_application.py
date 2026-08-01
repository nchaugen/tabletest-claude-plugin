"""Table-driven tests for evaluate_application, written ahead of the implementation.

Assumption: the rules state applicants aged 70+ are "approved only if they have
medical clearance" but do not say what happens otherwise. We take the natural
contrast to be DECLINED (as with the other disqualifying rule, restricted
destinations) rather than MANUAL_REVIEW, which the rules reserve explicitly for
trips over 90 days.
"""

import pytest

from travel_policy import Decision, evaluate_application


@pytest.mark.parametrize("has_medical_clearance", [True, False])
@pytest.mark.parametrize("trip_days", [30, 120])
@pytest.mark.parametrize("age", [25, 80])
def test_declines_restricted_destination_regardless_of_other_factors(
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
    ("age", "trip_days", "decision"),
    [
        pytest.param(25, 90, Decision.APPROVED, id="90 day trip, young applicant"),
        pytest.param(25, 91, Decision.MANUAL_REVIEW, id="91 day trip, young applicant"),
        pytest.param(80, 91, Decision.MANUAL_REVIEW, id="91 day trip, elderly applicant"),
    ],
)
def test_routes_trips_over_90_days_to_manual_review_regardless_of_age(
    age, trip_days, decision
):
    # Destination not restricted and clearance withheld: both belong to other rules.
    result = evaluate_application(
        age=age,
        trip_days=trip_days,
        destination_restricted=False,
        has_medical_clearance=False,
    )
    assert result == decision


@pytest.mark.parametrize(
    ("age", "has_medical_clearance", "decision"),
    [
        pytest.param(69, True, Decision.APPROVED, id="just under the age threshold, with clearance"),
        pytest.param(69, False, Decision.APPROVED, id="just under the age threshold, without clearance"),
        pytest.param(70, True, Decision.APPROVED, id="at the age threshold, with clearance"),
        pytest.param(70, False, Decision.DECLINED, id="at the age threshold, without clearance"),
    ],
)
def test_requires_medical_clearance_for_applicants_aged_70_or_over(
    age, has_medical_clearance, decision
):
    # Trip length held at 30 days (within the 90-day limit) and destination not
    # restricted: both belong to other rules.
    result = evaluate_application(
        age=age,
        trip_days=30,
        destination_restricted=False,
        has_medical_clearance=has_medical_clearance,
    )
    assert result == decision
