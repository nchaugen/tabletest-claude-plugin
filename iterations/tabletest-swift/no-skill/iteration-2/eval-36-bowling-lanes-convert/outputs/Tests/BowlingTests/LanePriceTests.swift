import Foundation
import TableTest
import Testing
@testable import Bowling

extension Lane: TableTextDecodable {
    public init?(tableText: String) {
        switch tableText {
        case "standard": self = .standard
        case "deluxe": self = .deluxe
        case "kids": self = .kids
        default: return nil
        }
    }
}

struct LanePriceTests {

    @TableTest("""
        Scenario                   | Lane     | Hours | Member | Price (EUR)?
        Standard lane hourly rate  | standard | 2     | false  | 60
        Deluxe lane hourly rate    | deluxe   | 2     | false  | 90
        Kids lane hourly rate      | kids     | 1     | false  | 20
        Hours after the third      | standard | 5     | false  | 90
        Member discount            | deluxe   | 2     | true   | 72
        """)
    func pricesABookingFromLaneHoursAndMembership(lane: Lane, hours: Int, member: Bool, price: Decimal) throws {
        let booking = Booking(lane: lane, hours: hours, isMember: member)

        #expect(try LanePrice.euros(for: booking) == price)
    }

    @Test func rejectsABookingWithoutHours() {
        let booking = Booking(lane: .standard, hours: 0)

        #expect(throws: BookingError.noHours) {
            try LanePrice.euros(for: booking)
        }
    }
}
