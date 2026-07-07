import pytest

from travel_policy import Decision, evaluate_application


CASES = [
    # age, trip_days, destination_restricted, has_medical_clearance, expected
    pytest.param(30, 30, False, False, Decision.APPROVED, id="under_70-short_trip-no_clearance-approved"),
    pytest.param(30, 30, False, True, Decision.APPROVED, id="under_70-short_trip-clearance_irrelevant-approved"),
    pytest.param(69, 90, False, False, Decision.APPROVED, id="age_boundary_69-trip_boundary_90-approved"),
    pytest.param(75, 30, False, True, Decision.APPROVED, id="70_or_over-with_clearance-approved"),
    pytest.param(70, 90, False, True, Decision.APPROVED, id="age_boundary_70-trip_boundary_90-with_clearance-approved"),
    pytest.param(75, 30, False, False, Decision.DECLINED, id="70_or_over-no_clearance-declined"),
    pytest.param(70, 90, False, False, Decision.DECLINED, id="age_boundary_70-trip_boundary_90-no_clearance-declined"),
    pytest.param(95, 30, False, False, Decision.DECLINED, id="well_over_70-no_clearance-declined"),
    pytest.param(30, 91, False, False, Decision.MANUAL_REVIEW, id="trip_boundary_91-under_70-manual_review"),
    pytest.param(30, 120, False, False, Decision.MANUAL_REVIEW, id="long_trip-under_70-manual_review"),
    pytest.param(75, 120, False, True, Decision.MANUAL_REVIEW, id="long_trip-70_or_over-with_clearance-still_manual_review"),
    pytest.param(75, 120, False, False, Decision.MANUAL_REVIEW, id="long_trip-70_or_over-no_clearance-still_manual_review"),
    pytest.param(30, 30, True, False, Decision.DECLINED, id="restricted_destination-under_70-short_trip-declined"),
    pytest.param(30, 120, True, False, Decision.DECLINED, id="restricted_destination-long_trip-overrides_manual_review"),
    pytest.param(75, 30, True, True, Decision.DECLINED, id="restricted_destination-with_clearance-overrides_approval"),
    pytest.param(75, 120, True, True, Decision.DECLINED, id="restricted_destination-long_trip-with_clearance-declined"),
]


@pytest.mark.parametrize(
    "age, trip_days, destination_restricted, has_medical_clearance, expected",
    CASES,
)
def test_evaluate_application(age, trip_days, destination_restricted, has_medical_clearance, expected):
    assert (
        evaluate_application(age, trip_days, destination_restricted, has_medical_clearance)
        is expected
    )
