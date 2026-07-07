class BagNotAccepted(Exception):
    """Raised when a bag exceeds the airline's checked-baggage limit."""


def fee_for_checked_bag(weight_kg: int) -> int:
    """Fee in euros for a checked bag of the given weight."""
    raise NotImplementedError
