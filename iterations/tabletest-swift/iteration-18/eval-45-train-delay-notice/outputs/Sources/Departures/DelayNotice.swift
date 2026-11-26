/// A departure on the board, as the traffic control reports it.
public struct Departure: Equatable, Sendable {
    public let scheduled: String
    public let destination: String
    public let delayMinutes: Int
    public let isCancelled: Bool
    /// The next departure to the same destination.
    public let nextDeparture: String

    public init(scheduled: String, destination: String, delayMinutes: Int, isCancelled: Bool, nextDeparture: String) {
        self.scheduled = scheduled
        self.destination = destination
        self.delayMinutes = delayMinutes
        self.isCancelled = isCancelled
        self.nextDeparture = nextDeparture
    }
}

/// What the notice tells the passengers of one departure.
public enum Notice: Codable, Equatable, Sendable {
    case onTime
    case delayed(minutes: Int)
    case delayedWithRefund(minutes: Int)
    case cancelled(next: String)
}

public enum DelayNotice {
    /// The notice a departure gets. A delay under 5 minutes counts as on time, and a delay of 60
    /// minutes or more gives the passengers a refund.
    public static func notice(for departure: Departure) -> Notice {
        if departure.isCancelled { return .cancelled(next: departure.nextDeparture) }
        switch departure.delayMinutes {
        case ..<5: return .onTime
        case ..<60: return .delayed(minutes: departure.delayMinutes)
        default: return .delayedWithRefund(minutes: departure.delayMinutes)
        }
    }

    /// The text of the notice on the platform screen and in the app.
    public static func text(for departure: Departure) -> String {
        text(of: notice(for: departure), service: "The \(departure.scheduled) service to \(departure.destination)")
    }

    static func text(of notice: Notice, service: String) -> String {
        switch notice {
        case .onTime:
            "\(service) is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board."
        case .delayed(let minutes):
            "\(service) is delayed by about \(minutes) minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more."
        case .delayedWithRefund(let minutes):
            "\(service) is delayed by about \(minutes) minutes. You can claim a refund of half your fare: apply on our website within 30 days of travel, and keep your ticket until the claim is settled."
        case .cancelled(let next):
            "\(service) is cancelled. Your ticket is valid on the next departure at \(next), or you can claim a full refund of your fare on our website within 30 days of travel."
        }
    }
}
