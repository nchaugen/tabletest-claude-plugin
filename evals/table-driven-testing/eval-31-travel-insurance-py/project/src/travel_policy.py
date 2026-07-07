from enum import Enum


class Decision(Enum):
    APPROVED = "APPROVED"
    DECLINED = "DECLINED"
    MANUAL_REVIEW = "MANUAL_REVIEW"


def evaluate_application(
    age: int,
    trip_days: int,
    destination_restricted: bool,
    has_medical_clearance: bool,
) -> Decision:
    """Decide the outcome of a travel insurance application."""
    raise NotImplementedError
