import Foundation

/// A lane the bowling alley books out.
public enum Lane: Sendable {
    case standard
    case deluxe
    case kids
}

public enum BookingError: Error, Equatable {
    case noHours
}

/// A booking as the guest makes it at the desk.
public struct Booking: Sendable {
    public let lane: Lane
    public let hours: Int
    public let isMember: Bool

    public init(lane: Lane, hours: Int, isMember: Bool = false) {
        self.lane = lane
        self.hours = hours
        self.isMember = isMember
    }
}

public enum LanePrice {
    /// The price of a booking in euros. Each lane has its own hourly rate, the hours after the
    /// third are free, and a member pays 80 % of the price.
    public static func euros(for booking: Booking) throws -> Decimal {
        guard booking.hours > 0 else { throw BookingError.noHours }
        let charged = Decimal(min(booking.hours, 3)) * hourlyRate(of: booking.lane)
        return booking.isMember ? charged * Decimal(string: "0.8")! : charged
    }

    private static func hourlyRate(of lane: Lane) -> Decimal {
        switch lane {
        case .standard: 30
        case .deluxe: 45
        case .kids: 20
        }
    }
}
