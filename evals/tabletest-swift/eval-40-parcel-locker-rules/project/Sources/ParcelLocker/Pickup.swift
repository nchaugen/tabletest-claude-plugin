/// A parcel waiting in a locker.
public struct Parcel: Sendable {
    /// The six-digit code the recipient got by text message.
    public let pickupCode: String
    /// Wrong codes given since the last right one.
    public let wrongCodesInARow: Int
    /// Whole hours since the courier put the parcel in the locker.
    public let hoursSinceDelivery: Int

    public init(pickupCode: String, wrongCodesInARow: Int, hoursSinceDelivery: Int) {
        self.pickupCode = pickupCode
        self.wrongCodesInARow = wrongCodesInARow
        self.hoursSinceDelivery = hoursSinceDelivery
    }
}

/// What the locker does when the recipient types a code.
public enum PickupResult: Equatable, Sendable {
    case opens
    case wrongCode(attemptsLeft: Int)
    case blocked
    case returnedToDepot
}

struct NotImplemented: Error {}

public enum Locker {
    /// What the locker does when the recipient types `code` for `parcel`.
    public static func pickUp(_ parcel: Parcel, code: String) throws -> PickupResult {
        throw NotImplemented()
    }
}
