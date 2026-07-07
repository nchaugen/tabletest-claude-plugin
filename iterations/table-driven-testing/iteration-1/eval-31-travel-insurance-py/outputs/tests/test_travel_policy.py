"""Table-driven tests for evaluate_application, written ahead of the implementation.

Assumption: the rules state that travelers aged 70+ are "approved only if" they
have medical clearance, but don't say what happens otherwise. Since a dedicated
MANUAL_REVIEW outcome already exists for the trip-length rule, an over-70
traveler without medical clearance is assumed to be DECLINED, not sent to
manual review.
"""

import pytest

from travel_policy import Decision, evaluate_application

AGE_LIMIT = 70
TRIP_DAY_LIMIT = 90


@pytest.mark.parametrize(
    ("age", "has_medical_clearance", "decision"),
    [
        pytest.param(0, False, Decision.APPROVED, id="youngest traveler"),
        pytest.param(AGE_LIMIT - 1, False, Decision.APPROVED, id="just under the age limit"),
        pytest.param(AGE_LIMIT, True, Decision.APPROVED, id="at the age limit with medical clearance"),
        pytest.param(AGE_LIMIT, False, Decision.DECLINED, id="at the age limit without medical clearance"),
        pytest.param(AGE_LIMIT + 20, True, Decision.APPROVED, id="well over the age limit with medical clearance"),
        pytest.param(AGE_LIMIT + 20, False, Decision.DECLINED, id="well over the age limit without medical clearance"),
    ],
)
def test_decision_by_age_and_medical_clearance_within_trip_limit(age, has_medical_clearance, decision):
    assert (
        evaluate_application(
            age=age,
            trip_days=TRIP_DAY_LIMIT,
            destination_restricted=False,
            has_medical_clearance=has_medical_clearance,
        )
        == decision
    )


@pytest.mark.parametrize(
    ("age", "has_medical_clearance"),
    [
        pytest.param(30, False, id="young traveler"),
        pytest.param(AGE_LIMIT + 10, True, id="older traveler with medical clearance"),
        pytest.param(AGE_LIMIT + 10, False, id="older traveler without medical clearance"),
    ],
)
@pytest.mark.parametrize(
    "trip_days",
    [TRIP_DAY_LIMIT + 1, TRIP_DAY_LIMIT + 200],
    ids=["just over the trip limit", "far over the trip limit"],
)
def test_long_trip_forces_manual_review_regardless_of_age_and_clearance(trip_days, age, has_medical_clearance):
    assert (
        evaluate_application(
            age=age,
            trip_days=trip_days,
            destination_restricted=False,
            has_medical_clearance=has_medical_clearance,
        )
        == Decision.MANUAL_REVIEW
    )


@pytest.mark.parametrize(
    ("age", "trip_days", "has_medical_clearance"),
    [
        pytest.param(30, 10, False, id="young traveler, short trip"),
        pytest.param(AGE_LIMIT + 10, 10, True, id="older traveler with clearance, short trip"),
        pytest.param(AGE_LIMIT + 10, 10, False, id="older traveler without clearance, short trip"),
        pytest.param(30, TRIP_DAY_LIMIT + 10, False, id="young traveler, long trip"),
        pytest.param(AGE_LIMIT + 10, TRIP_DAY_LIMIT + 10, True, id="older traveler with clearance, long trip"),
    ],
)
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
